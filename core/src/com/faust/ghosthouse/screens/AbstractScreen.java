package com.faust.ghosthouse.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.faust.ghosthouse.camera.CameraManager;
import com.faust.ghosthouse.GhosthouseGame;

/**
 * Abstact screen class to avoid too much empty methods around
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class AbstractScreen implements Screen {

    protected final GhosthouseGame game;
    protected final CameraManager cameraManager;
    protected final AssetManager assetManager;

    public AbstractScreen(GhosthouseGame game) {
        this.game = game;
        this.cameraManager = game.getCameraManager();
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

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
