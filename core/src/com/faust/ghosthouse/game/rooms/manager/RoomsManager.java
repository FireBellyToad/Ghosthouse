package com.faust.ghosthouse.game.rooms.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.faust.ghosthouse.GhosthouseGame;
import com.faust.ghosthouse.game.gameentities.enums.DirectionEnum;
import com.faust.ghosthouse.game.instances.impl.PlayerInstance;
import com.faust.ghosthouse.game.music.MusicManager;
import com.faust.ghosthouse.game.rooms.*;
import com.faust.ghosthouse.game.rooms.enums.RoomFlagEnum;
import com.faust.ghosthouse.game.rooms.enums.RoomTypeEnum;
import com.faust.ghosthouse.game.rooms.impl.FixedRoom;
import com.faust.ghosthouse.saves.AbstractSaveFileManager;
import com.faust.ghosthouse.saves.RoomSaveEntry;
import com.faust.ghosthouse.utils.serialization.MainWorldSerializer;
import com.faust.ghosthouse.game.gameentities.enums.ItemEnum;
import com.faust.ghosthouse.game.rooms.*;
import com.faust.ghosthouse.game.splash.SplashManager;
import com.faust.ghosthouse.game.textbox.manager.TextBoxManager;
import com.faust.ghosthouse.game.world.manager.WorldManager;

import java.util.*;

/**
 * Room Manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomsManager {

    private final SplashManager splashManager;
    private final AssetManager assetManager;
    private final AbstractSaveFileManager saveFileManager;
    private final List<OnRoomChangeListener> onRoomChangeListeners;

    private AbstractRoom currentRoom;
    private RoomPosition currentRoomPosInWorld = new RoomPosition(0, 0);

    /**
     * MainWorld Matrix
     */
    private MainWorldModel mainWorld;
    private final Map<RoomPosition, RoomSaveEntry> saveMap = new HashMap<>();
    private RoomPosition mainWorldSize;

    private final WorldManager worldManager;
    private final TextBoxManager textManager;
    private final PlayerInstance player;
    private final OrthographicCamera camera;
    private final MusicManager musicManager;

    public RoomsManager(WorldManager worldManager, TextBoxManager textManager, SplashManager splashManager, PlayerInstance player, OrthographicCamera camera, AssetManager assetManager, AbstractSaveFileManager saveFileManager, MusicManager musicManager) {
        this.worldManager = worldManager;
        this.textManager = textManager;
        this.splashManager = splashManager;
        this.assetManager = assetManager;
        this.saveFileManager = saveFileManager;
        this.musicManager = musicManager;
        this.player = player;
        this.camera = camera;
        this.onRoomChangeListeners = new ArrayList<>();
        addRoomChangeListener(player);

        initMainWorld();
    }

    /**
     * Inits world from file
     */
    private void initMainWorld() {

        final Vector2 mainWorldSize = new Vector2(0, 0);
        final Json jsonParser = new Json();
        jsonParser.setSerializer(MainWorldModel.class, new MainWorldSerializer());

        mainWorld = jsonParser.fromJson(MainWorldModel.class, Gdx.files.internal("mainWorldModel.json"));

        //TODO improve somehow?
        mainWorld.terrains.forEach((roomPosition,roomModel) -> {
            mainWorldSize.set(Math.max(mainWorldSize.x, roomPosition.getX()), Math.max(mainWorldSize.y, roomPosition.getY()));
        });
        
        // Finalize size
        this.mainWorldSize = new RoomPosition(mainWorldSize.x + 1, mainWorldSize.y + 1);

        //Try to load predefined casualnumbers for casual rooms from file
        try {
            saveFileManager.loadSaveForGame(player, saveMap);
        } catch (SerializationException ex) {
            Gdx.app.log("WARN", "No valid savefile to load");
        }
        //Init gamefile if no valid one has found
        saveFileManager.saveOnFile(player, saveMap);
    }

    /**
     * Changes the currentRoom
     *
     * @param newRoomPosX relative to the matrix of the world
     * @param newRoomPosY relative to the matrix of the world
     */
    public void changeCurrentRoom(int newRoomPosX, int newRoomPosY) {

        // Notifiy all listeners
        for(OnRoomChangeListener l: onRoomChangeListeners){
            l.onRoomChangeStart(currentRoom);
        }

        //Do stuff while leaving room
        RoomSaveEntry currentRoomSaveEntry = saveMap.get(currentRoomPosInWorld);

        if (Objects.nonNull(currentRoom)) {
            currentRoom.onRoomLeave(currentRoomSaveEntry);
        }

        //Change room position
        int finalX = (newRoomPosX < 0 ? mainWorldSize.getX() - 1 : (newRoomPosX == mainWorldSize.getX() ? 0 : newRoomPosX));
        int finalY = (newRoomPosY < 0 ? mainWorldSize.getY() - 1 : (newRoomPosY == mainWorldSize.getY() ? 0 : newRoomPosY));

        // Safety check on y
        if (finalY == 8 && finalX != 3) {
            finalY--;
        }

        currentRoomPosInWorld = new RoomPosition(finalX, finalY);

        //get entry from save or create new
        currentRoomSaveEntry = saveMap.get(currentRoomPosInWorld);
        if (Objects.isNull(currentRoomSaveEntry)) {
            currentRoomSaveEntry = new RoomSaveEntry(
                    (int) finalX,
                    (int) finalY,
                    0, populateRoomFlags(), new HashMap<>());
        } else {
            currentRoomSaveEntry.savedFlags.putAll(populateRoomFlags());
        }

//        if (mainWorld.terrains.get(currentRoomPosInWorld).type == RoomTypeEnum.CASUAL) {
//            currentRoom = new CasualRoom(worldManager, textManager, splashManager, player, camera, assetManager, currentRoomSaveEntry, musicManager);
//        } else {
            currentRoom = new FixedRoom(mainWorld.terrains.get(currentRoomPosInWorld).type, worldManager, textManager, splashManager, player, camera, assetManager, currentRoomSaveEntry, musicManager);
//        }


        Gdx.app.log("DEBUG", "ROOM " + currentRoomPosInWorld.getX() + "," + currentRoomPosInWorld.getY());
        //Keep the same state of already visited rooms
        saveMap.put(new RoomPosition(currentRoomPosInWorld.getX(), currentRoomPosInWorld.getY()), currentRoomSaveEntry);

        // Notifiy all listeners
        for(OnRoomChangeListener l: onRoomChangeListeners){
            l.onRoomChangeEnd(currentRoom);
        }

    }

    /**
     * @return populated map of flags
     */
    private Map<RoomFlagEnum, Boolean> populateRoomFlags() {
        //default map
        Map<RoomFlagEnum, Boolean> newRoomFlags = RoomFlagEnum.generateDefaultRoomFlags();

        return newRoomFlags;
    }

    /**
     * Wraps the room contents game logic
     *
     * @param stateTime
     */
    public void doRoomContentsLogic(float stateTime) {
        currentRoom.doRoomContentsLogic(stateTime);

        // In final room should never change
        if (RoomTypeEnum.FINAL.equals(currentRoom.getRoomType())) {
            return;
        }

        // After room logic, handle the room change
        Vector2 playerPosition = player.getBody().getPosition();
        player.setStartX(playerPosition.x);
        player.setStartY(playerPosition.y);

        int newXPosInMatrix = getCurrentRoomPosInWorld().getX();
        int newYPosInMatrix = getCurrentRoomPosInWorld().getY();

        DirectionEnum switchDirection = DirectionEnum.UNUSED;
        // Check for left or right passage
        if (playerPosition.x < AbstractRoom.LEFT_BOUNDARY) {
            switchDirection = DirectionEnum.LEFT;
        } else if ((playerPosition.x > AbstractRoom.RIGHT_BOUNDARY)) {
            switchDirection = DirectionEnum.RIGHT;
        }

        // Check for top or bottom passage
        if (playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY) {
            switchDirection = DirectionEnum.DOWN;
        } else if (playerPosition.y > AbstractRoom.TOP_BOUNDARY) {
            switchDirection = DirectionEnum.UP;
        } else if (playerPosition.y > GhosthouseGame.GAME_HEIGHT * 0.45 &&
                RoomTypeEnum.CHURCH_ENTRANCE.equals(currentRoom.getRoomType())) {
            //FIXME should add door object?
            //Final room
            switchDirection = DirectionEnum.UP;
            player.setStartY(AbstractRoom.BOTTOM_BOUNDARY + 8);
            saveFileManager.saveOnFile(player, saveMap);
        }


        // Adjustments for world extremes, semi pacman effect
        if (!DirectionEnum.UNUSED.equals(switchDirection)) {
            boolean hasBoundary = mainWorld.terrains.get(currentRoomPosInWorld).boundaries.containsKey(switchDirection);
            if (hasBoundary) {
                if (Objects.nonNull(mainWorld.terrains.get(currentRoomPosInWorld).boundaries.get(switchDirection))) {
                    newXPosInMatrix = mainWorld.terrains.get(currentRoomPosInWorld).boundaries.get(switchDirection).getX();
                    newYPosInMatrix = mainWorld.terrains.get(currentRoomPosInWorld).boundaries.get(switchDirection).getY();
                }
            }
            switch (switchDirection) {
                case UP: {
                    if (playerPosition.y > AbstractRoom.TOP_BOUNDARY) {
                        player.setStartY(AbstractRoom.BOTTOM_BOUNDARY + 4);
                        if (!hasBoundary) {
                            newYPosInMatrix++;
                        }
                    }
                    break;
                }
                case RIGHT: {
                    if (playerPosition.x > AbstractRoom.RIGHT_BOUNDARY) {
                        player.setStartX(AbstractRoom.LEFT_BOUNDARY + 4);
                        if (!hasBoundary) {
                            newXPosInMatrix++;
                        }
                    }
                    break;
                }
                case LEFT: {
                    if (playerPosition.x < AbstractRoom.LEFT_BOUNDARY) {
                        player.setStartX(AbstractRoom.RIGHT_BOUNDARY - 4);
                        if (!hasBoundary) {
                            newXPosInMatrix--;
                        }
                    }
                    break;
                }
                case DOWN: {
                    if (playerPosition.y < AbstractRoom.BOTTOM_BOUNDARY) {
                        player.setStartY(AbstractRoom.TOP_BOUNDARY - 4);
                        if (!hasBoundary) {
                            newYPosInMatrix--;
                        }
                    }
                    break;
                }
            }

            //Change room and clear nearest poi reference
            if (getCurrentRoomPosInWorld().getX() != newXPosInMatrix || getCurrentRoomPosInWorld().getY() != newYPosInMatrix) {
                changeCurrentRoom(newXPosInMatrix, newYPosInMatrix);
                player.cleanReferences();
            }
        }
    }

    public RoomPosition getCurrentRoomPosInWorld() {
        return currentRoomPosInWorld;
    }

    /**
     * Dispose current room contents
     */
    public void dispose() {
        saveFileManager.saveOnFile(player, saveMap);
        onRoomChangeListeners.clear();

        currentRoom.dispose();
    }

    public Map<RoomPosition, RoomSaveEntry> getSaveMap() {
        return saveMap;
    }

    /**
     *
     * @return current room
     */
    public AbstractRoom getCurrentRoom() {
        return currentRoom;
    }

    /**
     *
     * @param listener
     */
    public void addRoomChangeListener(OnRoomChangeListener listener){
        onRoomChangeListeners.add(listener);
    }

    public void putPlayerInStartingRoom(PlayerInstance player) {

        for(Map.Entry<RoomPosition, RoomModel> entry : mainWorld.terrains.entrySet()){
            if(RoomTypeEnum.START_POINT.equals(entry.getValue().type)){
                changeCurrentRoom(entry.getKey().getX(), entry.getKey().getY());
            }
        }

    }
}
