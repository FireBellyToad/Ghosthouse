package com.faust.ghosthouse.camera;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.faust.ghosthouse.GhostHouse;
import com.faust.ghosthouse.camera.viewport.GhostHouseViewport;

import java.util.Objects;

/**
 * Camera manager class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class CameraManager {

    private final OrthographicCamera camera;
    private final Box2DDebugRenderer box2DDebugRenderer;
    private final Viewport viewport;

    private final ShapeRenderer background;
    private static final Color back = new Color(0x000000ff);

    public CameraManager() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GhostHouse.GAME_WIDTH, GhostHouse.GAME_HEIGHT);
        viewport = new GhostHouseViewport(camera);
        box2DDebugRenderer = new Box2DDebugRenderer();
        background = new ShapeRenderer();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Used for rendering a World bodies and collision shape, usually for debug
     * @param world
     */
    public void box2DDebugRenderer(World world) {
        Objects.requireNonNull(world);
        box2DDebugRenderer.render(world, camera.combined);
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void applyAndUpdate() {
        viewport.apply();
        camera.update();
    }

    /**
     * Render blank background
     */
    public void renderBackground() {
        background.setColor(back);
        background.setProjectionMatrix(camera.combined);
        background.begin(ShapeRenderer.ShapeType.Filled);
        background.rect(0, 0, GhostHouse.GAME_WIDTH, GhostHouse.GAME_HEIGHT);
        background.end();
    }

    public void dispose() {
        box2DDebugRenderer.dispose();
    }
}
