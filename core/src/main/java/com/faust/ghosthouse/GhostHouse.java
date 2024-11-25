package com.faust.ghosthouse;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.faust.ghosthouse.camera.CameraManager;
import com.faust.ghosthouse.game.instances.PlayerInstance;
import com.faust.ghosthouse.screens.impl.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GhostHouse extends Game {

    public static final float GAME_WIDTH = 160;
    public static final float GAME_HEIGHT = 144;

    private SpriteBatch batch;
    private Texture image;
    private GameScreen gameScreen;
    private CameraManager cameraManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        cameraManager = new CameraManager();
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }

    @Override
    public void dispose() {
        gameScreen.dispose();
        cameraManager.dispose();
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
