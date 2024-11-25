package com.faust.lhengine.screens;

import com.badlogic.gdx.Screen;
import com.faust.ghosthouse.GhostHouse;
import com.faust.ghosthouse.camera.CameraManager;

/**
 * Abstact screen class to avoid too much empty methods around
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public abstract class AbstractScreen implements Screen {

    protected final GhostHouse game;
    private final CameraManager cameraManager;

    public AbstractScreen(GhostHouse game) {
        this.game = game;
        this.cameraManager = game.getCameraManager();
    }

    @Override
    public void show() {

    }

    @Override
    public abstract void render(float delta);

    @Override
    public void resize(int width, int height) {
        cameraManager.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
