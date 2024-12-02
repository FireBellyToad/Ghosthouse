package com.faust.ghosthouse.render;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.faust.ghosthouse.camera.CameraManager;
import com.faust.ghosthouse.game.instances.GameInstance;
import com.faust.ghosthouse.game.rooms.Room;
import com.faust.ghosthouse.game.rooms.RoomContent;
import com.faust.ghosthouse.game.rooms.enums.MapLayersEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TopView World Renderer
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class SideViewWorldRenderer implements WorldRenderer<Room> {

    private final SpriteBatch batch;
    private final CameraManager cameraManager;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;

    public SideViewWorldRenderer(SpriteBatch batch, CameraManager cameraManager, Room room) {
        Objects.requireNonNull(batch);
        Objects.requireNonNull(cameraManager);

        this.batch = batch;
        this.cameraManager = cameraManager;

        //Setting new tileRenderer
        //TODO change level on event!
        tiledMapRenderer = new OrthogonalTiledMapRenderer(room.getRoomContent().tiledMap);
        tiledMapRenderer.setView(cameraManager.getCamera());

    }

    /**
     * Draws the background color and terrain tiles
     */
    public void drawBackground(Room currentRoom) {
        batch.begin();
        cameraManager.renderBackground();
        batch.end();

        final RoomContent roomContent = currentRoom.getRoomContent();

        MapLayers mapLayers = roomContent.tiledMap.getLayers();
        TiledMapTileLayer terrainLayer = (TiledMapTileLayer) mapLayers.get(MapLayersEnum.TILES_LAYER.getLayerName());

        //Overlay layer should is required
        Objects.requireNonNull(terrainLayer);

        tiledMapRenderer.getBatch().begin();
        tiledMapRenderer.renderTileLayer(terrainLayer);
        tiledMapRenderer.getBatch().end();
    }

    /**
     * Draws all the room content
     *
     * @param stateTime
     * @param currentRoom
     */
    @Override
    public void drawWorld(float stateTime, Room currentRoom) {
        final RoomContent roomContent = currentRoom.getRoomContent();

        List<GameInstance> allInstance = new ArrayList<>();

        allInstance.add(roomContent.player);
        allInstance.addAll(roomContent.debugInstances);
        if(Objects.nonNull(roomContent.shot)){
            allInstance.add(roomContent.shot);
        }
        //TODO uncomment when enemies are available
//        allInstance.addAll(roomContent.enemyList);


        allInstance.forEach(i -> i.draw(batch, stateTime));
    }

    @Override
    public void dispose() {
        tiledMapRenderer.dispose();
    }

}
