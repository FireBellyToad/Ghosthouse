package com.faust.ghosthouse.screens.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.faust.ghosthouse.GhostHouse;
import com.faust.ghosthouse.camera.CameraManager;
import com.faust.ghosthouse.game.instances.PlayerInstance;

public class GameScreen extends com.faust.lhengine.screens.AbstractScreen {
    private final PlayerInstance player;

    private float stateTime = 0f;
    private CameraManager cameraManager;


    public GameScreen(GhostHouse game) {
        super(game);
        player = new PlayerInstance();
        cameraManager = game.getCameraManager();
        Gdx.input.setInputProcessor(player);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        //Render before game logic to avoid desync

        cameraManager.applyAndUpdate();
        game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

        game.getBatch().begin();
        cameraManager.renderBackground();
        game.getBatch().end();

        game.getBatch().begin();
        player.draw(game.getBatch(),delta);
        game.getBatch().end();
    }

    /**
     * Executes the logic of each game Instance
     */
    private void doLogic() {
    }

    @Override
    public void dispose() {
        player.dispose();
    }
}
