package com.faust.ghosthouse.game.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.faust.ghosthouse.GhosthouseGame;
import com.faust.ghosthouse.game.gameentities.enums.*;
import com.faust.ghosthouse.game.instances.impl.*;
import com.faust.ghosthouse.game.music.MusicManager;
import com.faust.ghosthouse.game.music.enums.TuneEnum;
import com.faust.ghosthouse.saves.RoomSaveEntry;
import com.faust.ghosthouse.game.ai.PathNode;
import com.faust.ghosthouse.game.ai.RoomNodesGraph;
import com.faust.ghosthouse.game.instances.interfaces.Killable;
import com.faust.ghosthouse.game.instances.AnimatedInstance;
import com.faust.ghosthouse.game.instances.GameInstance;
import com.faust.ghosthouse.game.rooms.interfaces.SpawnFactory;
import com.faust.ghosthouse.game.rooms.areas.WallArea;
import com.faust.ghosthouse.game.rooms.enums.MapLayersEnum;
import com.faust.ghosthouse.game.rooms.enums.MapObjTypeEnum;
import com.faust.ghosthouse.game.rooms.enums.RoomFlagEnum;
import com.faust.ghosthouse.game.rooms.enums.RoomTypeEnum;
import com.faust.ghosthouse.game.splash.SplashManager;
import com.faust.ghosthouse.game.textbox.manager.TextBoxManager;
import com.faust.ghosthouse.game.world.manager.WorldManager;

import java.util.*;


/**
 * Abstract room common logic
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class AbstractRoom implements SpawnFactory {

    /**
     * Boundaries for room changing
     */
    public static final float LEFT_BOUNDARY = 12;
    public static final float BOTTOM_BOUNDARY = 4;
    public static final float RIGHT_BOUNDARY = GhosthouseGame.GAME_WIDTH - 12;
    public static final float TOP_BOUNDARY = GhosthouseGame.GAME_HEIGHT - 24;

    protected final RoomContent roomContent = new RoomContent();
    protected final SplashManager splashManager;
    protected final TextBoxManager textManager;
    protected final MusicManager musicManager;
    protected final AssetManager assetManager;
    protected final WorldManager worldManager;

    private GameInstance addedInstance; //Buffer for new enemies spawned during gameplay

    /**
     * Constructor
     *  @param roomType
     * @param worldManager
     * @param textManager
     * @param splashManager
     * @param player
     * @param camera
     * @param roomSaveEntry
     * @param musicManager
     */
    public AbstractRoom(final RoomTypeEnum roomType, final WorldManager worldManager, final TextBoxManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera, final AssetManager assetManager, final RoomSaveEntry roomSaveEntry, MusicManager musicManager) {
        Objects.requireNonNull(worldManager);
        Objects.requireNonNull(textManager);
        Objects.requireNonNull(player);
        Objects.requireNonNull(roomType);
        Objects.requireNonNull(splashManager);
        Objects.requireNonNull(assetManager);

        // Clear world bodies, if present
        worldManager.clearBodies();

        this.assetManager = assetManager;
        this.worldManager = worldManager;

        // Load tiled map by name
        this.roomContent.roomType = roomType;
        this.roomContent.roomFileName = "terrains/" + roomType.getMapFileName();
        this.roomContent.roomFlags = roomSaveEntry.savedFlags;

        loadTiledMap(roomSaveEntry);

        // Extract mapObjects
        final MapObjects mapObjects = this.roomContent.tiledMap.getLayers().get(MapLayersEnum.OBJECT_LAYER.getLayerName()).getObjects();

        // Add content to room
        this.roomContent.player = player;
        this.splashManager = splashManager;
        this.textManager = textManager;
        this.musicManager = musicManager;

        this.roomContent.poiList = new ArrayList<>();
        this.roomContent.decorationList = new ArrayList<>();
        this.roomContent.enemyList = new ArrayList<>();
        this.roomContent.wallList = new ArrayList<>();
        this.roomContent.spellEffects = new ArrayList<>();
        this.roomContent.removedPoiList = new ArrayList<>();

        // Place objects in room
        mapObjects.forEach(obj -> {

            String typeString = (String) obj.getProperties().get("type");

            // Prepare POI
            if (MapObjTypeEnum.POI.name().equals(typeString)) {
                addObjAsPOI(obj, textManager, assetManager);
            }

            // Prepare decoration
            if (MapObjTypeEnum.DECO.name().equals(typeString)) {
                addObjAsDecoration(obj, assetManager);
            }

            // Prepare enemy if they are enabled
            if (!roomSaveEntry.savedFlags.get(RoomFlagEnum.DISABLED_ENEMIES) && MapObjTypeEnum.ENEMY.name().equals(typeString)) {
                addObjAsEnemy(obj, assetManager, false);
            }

            // Prepare enemy (casual choice)
            if (MapObjTypeEnum.WALL.name().equals(typeString)) {
                addObjAsWall(obj);
            }

            // Prepare PathNodes
            if (PathNode.class.getSimpleName().equals(typeString)) {
                addObjAsPathNode(obj);
            }
        });

        worldManager.insertPlayerIntoWorld(player, player.getStartX(), player.getStartY());
        worldManager.insertPOIIntoWorld(roomContent.poiList);
        worldManager.insertDecorationsIntoWorld(roomContent.decorationList);
        worldManager.insertEnemiesIntoWorld(roomContent.enemyList);
        worldManager.insertWallsIntoWorld(roomContent.wallList);
        player.changePOIList(roomContent.poiList);
        if (Objects.nonNull(roomContent.roomGraph)) {
            roomContent.roomGraph.initGraph(worldManager);
        }

        // Do other stuff
        this.onRoomEnter(roomType, worldManager, assetManager, roomSaveEntry, mapObjects);
    }

    /**
     * Add invisible walls
     *
     * @param obj
     */
    protected void addObjAsWall(MapObject obj) {

        RectangleMapObject mapObject = (RectangleMapObject) obj;

        roomContent.wallList.add(new WallArea(mapObject.getRectangle()));
    }

    protected void addObjAsPathNode(MapObject obj) {

        if (Objects.isNull(roomContent.roomGraph)) {
            roomContent.roomGraph = new RoomNodesGraph();
        }
        roomContent.roomGraph.addPathNode(new PathNode(
                MathUtils.floor((float) obj.getProperties().get("x")),
                MathUtils.floor((float) obj.getProperties().get("y"))));

    }

    /**
     * Implements tiled map load
     *
     * @param roomSaveEntry if needed
     */
    protected abstract void loadTiledMap(RoomSaveEntry roomSaveEntry);

    /**
     * Add a object as POI
     *
     * @param obj
     * @param textManager
     */
    protected void addObjAsPOI(MapObject obj, TextBoxManager textManager, AssetManager assetManager) {

        POIEnum poiType = POIEnum.valueOf((String) obj.getProperties().get("poiType"));
        Objects.requireNonNull(poiType);

        Gdx.app.log("DEBUG", "GUARANTEED_GOLDCROSS: " + roomContent.roomFlags.get(RoomFlagEnum.GUARANTEED_GOLDCROSS));
        Gdx.app.log("DEBUG", "GUARANTEED_HERBS: " + roomContent.roomFlags.get(RoomFlagEnum.GUARANTEED_HERBS));
        Gdx.app.log("DEBUG", "WITHOUT_HERBS: " + roomContent.roomFlags.get(RoomFlagEnum.WITHOUT_HERBS));

        roomContent.poiList.add(new POIInstance(textManager,
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                poiType, (int)  obj.getProperties().get("id") ,splashManager, assetManager,
                roomContent.roomFlags.get(RoomFlagEnum.GUARANTEED_GOLDCROSS)));

    }

    /**
     * Add a object as Decoration
     *
     * @param obj MapObject to add
     */
    protected void addObjAsDecoration(MapObject obj, AssetManager assetManager) {

        DecorationsEnum decoType = DecorationsEnum.valueOf((String) obj.getProperties().get("decoType"));

        roomContent.decorationList.add(new DecorationInstance(
                (float) obj.getProperties().get("x"),
                (float) obj.getProperties().get("y"),
                (int)  obj.getProperties().get("id"),
                decoType, assetManager));
    }

    /**
     * Add a object as Enemy
     *
     * @param obj            MapObject to add
     * @param addNewInstance
     */
    protected void addObjAsEnemy(MapObject obj, AssetManager assetManager, boolean addNewInstance) {

        // Enemies are usually dynamically determined, with a couple of exceptional cases
        // which should be set as "type" property on MapObject
        EnemyEnum enemyEnum = EnemyEnum.UNDEFINED;
        if (obj.getProperties().containsKey("enemyType")) {
            enemyEnum = EnemyEnum.valueOf((String) obj.getProperties().get("enemyType"));
            Objects.requireNonNull(enemyEnum);
        }

        switch (enemyEnum) {
            default: {

            }
        }

        // If is not a spawned instance (usually MeatInstance), add it right now
        if (!addNewInstance) {
            roomContent.enemyList.add((AnimatedInstance) addedInstance);
            addedInstance = null;
        }
    }

    /**
     * Method for additional room initialization
     * @param roomType
     * @param worldManager
     * @param roomSaveEntry
     * @param mapObjects
     */
    protected abstract void onRoomEnter(RoomTypeEnum roomType, final WorldManager worldManager, AssetManager assetManager, RoomSaveEntry roomSaveEntry, MapObjects mapObjects);

    /**
     * Disposes the terrain and the contents of the room
     */
    public void dispose() {
        textManager.removeAllBoxes();
        roomContent.tiledMap.dispose();
        roomContent.enemyList.forEach(AnimatedInstance::dispose);
        roomContent.decorationList.forEach(DecorationInstance::dispose);
        roomContent.poiList.forEach(POIInstance::dispose);
        roomContent.wallList.forEach(WallArea::dispose);
    }

    public RoomTypeEnum getRoomType() {
        return roomContent.roomType;
    }

    public synchronized void doRoomContentsLogic(float stateTime) {

        // Do Player logic
        roomContent.player.doLogic(stateTime, roomContent);

        //Stop music
        if (roomContent.player.isDead() && musicManager.isPlaying()) {
            musicManager.stopMusic();
        }

        // Do enemy logic
        roomContent.enemyList.forEach((ene) -> {

            ene.doLogic(stateTime, roomContent);

            if (roomContent.player.isDead()) {
                musicManager.stopMusic();
            } else if (roomContent.enemyList.size() == 1 && ClassReflection.isAssignableFrom(Killable.class,ene.getClass()) && ((Killable) ene).isDead()) {
                //Changing music based on enemy behaviour and number
                musicManager.playMusic(TuneEnum.DANGER, true);
            } else if ((!RoomTypeEnum.FINAL.equals(roomContent.roomType) && !RoomTypeEnum.INFERNUM.equals(roomContent.roomType) && !RoomTypeEnum.CHURCH_ENTRANCE.equals(roomContent.roomType)) &&
                    !GameBehavior.IDLE.equals(ene.getCurrentBehavior())) {
                musicManager.playMusic(TuneEnum.ATTACK, 0.65f, true);
            }
        });

        // If there is an instance to add, do it and clean reference
        if (!Objects.isNull(addedInstance)) {
            roomContent.enemyList.add((AnimatedInstance) addedInstance);
            addedInstance = null;
        }

        // Dispose enemies
        roomContent.enemyList.forEach(ene -> {
            if (ene.isDisposable()) {
                ene.dispose();
            }
        });

        // Remove some dead enemies
        roomContent.enemyList.removeIf(ene -> ((Killable) ene).isDead());

        //Remove examined removable POI
        roomContent.poiList.removeIf(poiInstance -> {
            boolean check =  poiInstance.isAlreadyExamined() && poiInstance.isRemovableOnExamination();

            if(check){
                roomContent.removedPoiList.add(poiInstance);
            }

            return check;
        });

        //Spells logic
        roomContent.spellEffects.forEach(spell -> spell.doLogic(stateTime, roomContent));

        //Dispose spells
        roomContent.spellEffects.forEach(spell -> {
            if (spell.isDisposable()) {
                spell.dispose();
            }
        });


        //Remove spells
        roomContent.spellEffects.removeIf(spell -> ((Killable) spell).isDead());
    }

    @Override
    public synchronized <T extends GameInstance> void spawnInstance(Class<T> instanceClass, float startX, float startY, String instanceIdentifierEnum) {

        if (!Objects.isNull(addedInstance)) {
            return;
        }

        //Create a stub MapObject
        final MapObject mapObjectStub = new MapObject();
        mapObjectStub.getProperties().put("x", startX);
        mapObjectStub.getProperties().put("y", startY);

        //Insert last enemy into world
        if (ClassReflection.isAssignableFrom(AnimatedInstance.class,instanceClass)) {
            mapObjectStub.getProperties().put("enemyType", instanceIdentifierEnum);
            addObjAsEnemy(mapObjectStub, assetManager, true);
            worldManager.insertEnemiesIntoWorld(Collections.singletonList((AnimatedInstance) addedInstance));
        } else if (instanceClass.equals(POIInstance.class)) {
            mapObjectStub.getProperties().put("poiType", instanceIdentifierEnum);
            mapObjectStub.getProperties().put("id", 0);
            addObjAsPOI(mapObjectStub, textManager, assetManager);
            POIInstance lastPOIInstance = roomContent.poiList.get(roomContent.poiList.size() - 1);
            worldManager.insertPOIIntoWorld(Collections.singletonList(lastPOIInstance));
            roomContent.player.changePOIList(roomContent.poiList);
        }
}

    public abstract void onRoomLeave(RoomSaveEntry roomSaveEntry);

    public RoomContent getRoomContent(){
        return roomContent;
    }

    public abstract String getLayerToDraw();
}
