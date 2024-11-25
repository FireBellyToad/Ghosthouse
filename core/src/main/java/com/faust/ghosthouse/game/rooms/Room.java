package com.faust.ghosthouse.game.rooms;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.faust.ghosthouse.GhostHouse;
import com.faust.ghosthouse.game.instances.PlayerInstance;
import com.faust.ghosthouse.game.rooms.areas.WallArea;
import com.faust.ghosthouse.game.rooms.enums.MapLayersEnum;
import com.faust.ghosthouse.game.rooms.enums.MapObjTypeEnum;
import com.faust.ghosthouse.world.WorldManager;

import java.util.ArrayList;
import java.util.Objects;

public class Room {

    protected final RoomContent roomContent = new RoomContent();

    protected final WorldManager worldManager;

    public Room(WorldManager worldManager, PlayerInstance playerInstance) {
        Objects.requireNonNull(worldManager);

        this.worldManager = worldManager;

        // Put in room
        roomContent.tiledMap = new TmxMapLoader().load("levels/level1.tmx");
        roomContent.player = playerInstance;
        roomContent.wallList = new ArrayList<>();


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
}
