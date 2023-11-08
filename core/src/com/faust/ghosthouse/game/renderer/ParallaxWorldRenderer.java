package com.faust.ghosthouse.game.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.faust.ghosthouse.camera.CameraManager;
import com.faust.ghosthouse.game.PauseManager;
import com.faust.ghosthouse.game.gameentities.enums.GameBehavior;
import com.faust.ghosthouse.game.hud.Hud;
import com.faust.ghosthouse.game.rooms.AbstractRoom;
import com.faust.ghosthouse.game.rooms.OnRoomChangeListener;
import com.faust.ghosthouse.game.rooms.RoomContent;
import com.faust.ghosthouse.game.rooms.enums.MapLayersEnum;
import com.faust.ghosthouse.game.splash.SplashManager;
import com.faust.ghosthouse.game.textbox.manager.TextBoxManager;
import com.faust.ghosthouse.utils.DepthComparatorUtils;
import com.faust.ghosthouse.game.instances.GameInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TopView World Renderer
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class ParallaxWorldRenderer implements WorldRenderer<AbstractRoom>, OnRoomChangeListener {

    private final SpriteBatch batch;
    private final CameraManager cameraManager;
    private final SplashManager splashManager;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public ParallaxWorldRenderer(SpriteBatch batch, CameraManager cameraManager, SplashManager splashManager) {
        Objects.requireNonNull(batch);
        Objects.requireNonNull(cameraManager);

        this.batch = batch;
        this.cameraManager = cameraManager;
        this.splashManager = splashManager;
    }

    /**
     * Draws the background color and terrain tiles
     */
    public void drawBackground(AbstractRoom currentRoom) {
        batch.begin();
        cameraManager.renderBackground();
        batch.end();

        final RoomContent roomContent = currentRoom.getRoomContent();

        MapLayers mapLayers = roomContent.tiledMap.getLayers();
        TiledMapTileLayer terrainLayer = (TiledMapTileLayer) mapLayers.get(currentRoom.getLayerToDraw());

        //Overlay layer should is required
        Objects.requireNonNull(terrainLayer);

        tiledMapRenderer.getBatch().begin();
        tiledMapRenderer.renderTileLayer(terrainLayer);
        tiledMapRenderer.getBatch().end();
    }

    /**
     * Draws all the overlays
     *
     * @param stateTime
     * @param hud
     * @param player
     * @param currentRoom
     * @param pauseManager
     * @param textManager
     */
    public void drawOverlays(float stateTime, Hud hud, Object player, AbstractRoom currentRoom, PauseManager pauseManager, TextBoxManager textManager) {
        final RoomContent roomContent = currentRoom.getRoomContent();

        //Draw overlay tiles
        MapLayers mapLayers = roomContent.tiledMap.getLayers();
        TiledMapTileLayer overlayLayer = (TiledMapTileLayer) mapLayers.get(MapLayersEnum.OVERLAY_LAYER.getLayerName());

        //Overlay layer should not be required
        if (Objects.nonNull(overlayLayer)) {
            tiledMapRenderer.getBatch().begin();
            tiledMapRenderer.renderTileLayer(overlayLayer);
            tiledMapRenderer.getBatch().end();
        }

        // Draw splash XOR hud
        if (splashManager.isDrawingSplash()) {
            splashManager.drawSplash(batch, stateTime);
        } else {
            if (pauseManager.isGamePaused()) {
                pauseManager.draw(batch, cameraManager.getCamera());
            }
        }
        // draw text
        textManager.renderTextBoxes(batch, cameraManager.getCamera());

    }

    /**
     * Draws all the room content
     *
     * @param stateTime
     * @param currentRoom
     */
    @Override
    public void drawWorld(float stateTime, AbstractRoom currentRoom) {
        final RoomContent roomContent = currentRoom.getRoomContent();

        List<GameInstance> allInstance = new ArrayList<>();
        //TODO review to render in platform game style
//        allInstance.addAll(roomContent.poiList);
//        allInstance.addAll(roomContent.decorationList);
//        allInstance.add(roomContent.player);
//        allInstance.addAll(roomContent.enemyList);
//
//        //add all actors with an activated trigger
//        if (Objects.nonNull(roomContent.echoActors)) {
//            for (ScriptActorInstance echoActorInstance : roomContent.echoActors) {
//                if (echoActorInstance.isEchoIsActive()) {
//                    allInstance.add(echoActorInstance);
//                }
//            }
//        }
//
//        allInstance.addAll(roomContent.spellEffects);
//
//        // Sort by Y for depth effect. If decoration is interacted, priority is lowered
//        allInstance.sort(DepthComparatorUtils::compareEntities);
//
//        allInstance.forEach((i) -> i.draw(batch, stateTime));
    }

    @Override
    public void dispose() {
        tiledMapRenderer.dispose();
    }

    @Override
    public void onRoomChangeStart(AbstractRoom newRoom) {
        //Disposing old room tiledrendered
        if(Objects.nonNull(tiledMapRenderer)){
            tiledMapRenderer.dispose();
        }
    }

    @Override
    public void onRoomChangeEnd(AbstractRoom newRoom) {
        //Setting new tileRenderer
        tiledMapRenderer = new OrthogonalTiledMapRenderer(newRoom.getRoomContent().tiledMap);
        tiledMapRenderer.setView(cameraManager.getCamera());
    }
}
