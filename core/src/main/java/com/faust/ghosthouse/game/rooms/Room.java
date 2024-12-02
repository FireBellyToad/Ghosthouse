package com.faust.ghosthouse.game.rooms;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.faust.ghosthouse.GhostHouse;
import com.faust.ghosthouse.game.gameentities.enums.DirectionEnum;
import com.faust.ghosthouse.game.instances.GameInstance;
import com.faust.ghosthouse.game.instances.impl.DebugParticle;
import com.faust.ghosthouse.game.instances.impl.PlayerInstance;
import com.faust.ghosthouse.game.instances.impl.ShotInstance;
import com.faust.ghosthouse.game.rooms.areas.WallArea;
import com.faust.ghosthouse.game.rooms.enums.MapLayersEnum;
import com.faust.ghosthouse.game.rooms.enums.MapObjTypeEnum;
import com.faust.ghosthouse.game.rooms.interfaces.SpawnFactory;
import com.faust.ghosthouse.world.WorldManager;

import java.util.ArrayList;
import java.util.Objects;

public class Room implements SpawnFactory {

    protected final RoomContent roomContent = new RoomContent();

    protected final WorldManager worldManager;

    public Room(WorldManager worldManager, PlayerInstance playerInstance) {
        Objects.requireNonNull(worldManager);

        playerInstance.setSpawnFactory(this);

        this.worldManager = worldManager;

        // Put in room
        roomContent.tiledMap = new TmxMapLoader().load("levels/level1.tmx");
        roomContent.player = playerInstance;
        roomContent.wallList = new ArrayList<>();
        roomContent.debugInstances = new ArrayList<>();


        // Extract mapObjects
        final MapObjects mapObjects = this.roomContent.tiledMap.getLayers().get(MapLayersEnum.OBJECT_LAYER.getLayerName()).getObjects();

        mapObjects.forEach(obj -> {

            String typeString = (String) obj.getProperties().get("type");

            // Prepare enemy (casual choice)
            if (MapObjTypeEnum.WALL.name().equals(typeString)) {
                addObjAsWall(obj);
            }
        });

        // Put in Box2D world
        worldManager.insertPlayerIntoWorld(playerInstance, GhostHouse.GAME_WIDTH / 2, (GhostHouse.GAME_HEIGHT / 2));
        worldManager.insertWallsIntoWorld(roomContent.wallList);
    }

    public  void doRoomContentsLogic(float stateTime) {
        roomContent.player.doLogic(stateTime, roomContent);

        if(Objects.nonNull(roomContent.shot)){
            roomContent.shot.doLogic(stateTime, roomContent);

            //Remove and dispose
            if(roomContent.shot.isDisposable()){
                roomContent.shot.dispose();
                roomContent.shot = null;
            }
        }

        for(var debug : roomContent.debugInstances){
            debug.doLogic(stateTime, roomContent);
        }
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


    public RoomContent getRoomContent() {
        return roomContent;
    }

    @Override
    public <T extends GameInstance> void spawnInstance(Class<T> instanceClass, float startX, float startY, Object extraInfo) {

        //Insert last enemy into world
        if (ClassReflection.isAssignableFrom(ShotInstance.class, instanceClass)) {
            //Only one shot per room
            if(Objects.isNull(roomContent.shot)) {
                roomContent.shot = new ShotInstance((DirectionEnum) extraInfo);
                worldManager.insertShotIntoWorld(roomContent.shot,startX,startY);
            }
        } else if (ClassReflection.isAssignableFrom(DebugParticle.class, instanceClass)) {
            roomContent.debugInstances.add(new DebugParticle(startX,startY));
        }


    }
}
