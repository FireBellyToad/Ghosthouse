package com.faust.ghosthouse.screens.impl;

import com.badlogic.gdx.Gdx;
import com.faust.ghosthouse.GhostHouse;
import com.faust.ghosthouse.camera.CameraManager;
import com.faust.ghosthouse.game.instances.PlayerInstance;
import com.faust.ghosthouse.game.rooms.Room;
import com.faust.ghosthouse.render.SideViewWorldRenderer;
import com.faust.ghosthouse.world.WorldManager;

public class GameScreen extends com.faust.lhengine.screens.AbstractScreen {
    private final PlayerInstance player;
    private final WorldManager worldManager;
    private final Room room;
    private final SideViewWorldRenderer worldRenderer;

    private float stateTime = 0f;
    private final CameraManager cameraManager;


    public GameScreen(GhostHouse game) {
        super(game);
        worldManager = new WorldManager();
        player = new PlayerInstance();
        room = new Room(worldManager, player);
        cameraManager = game.getCameraManager();
        Gdx.input.setInputProcessor(player);

        worldRenderer = new SideViewWorldRenderer(game.getBatch(), cameraManager, room);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        //Render before game logic to avoid desync
        stateTime += Gdx.graphics.getDeltaTime();

        cameraManager.applyAndUpdate();
        game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

        game.getBatch().begin();
        cameraManager.renderBackground();
        game.getBatch().end();

        // Drow real game
        worldRenderer.drawBackground(room);
        game.getBatch().begin();
        worldRenderer.drawWorld(stateTime, room);
        player.draw(game.getBatch(), stateTime);
        game.getBatch().end();

        cameraManager.box2DDebugRenderer(worldManager.getWorld());

        worldManager.doStep(delta);
        doLogic(delta);

    }

    /**
     * Executes the logic of each game Instance
     */
    private void doLogic(float delta) {
        player.doLogic(delta, room.getRoomContent());
    }

    @Override
    public void dispose() {
        player.dispose();
        worldManager.dispose();
    }
}
