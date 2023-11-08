package com.faust.ghosthouse.screens.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.faust.ghosthouse.screens.AbstractScreen;
import com.faust.ghosthouse.utils.ValidScript;
import com.faust.ghosthouse.game.scripts.enums.ScriptActorType;
import com.faust.ghosthouse.GhosthouseGame;
import com.faust.ghosthouse.game.music.MusicManager;

public class LoadingScreen extends AbstractScreen {

    private final MusicManager musicManager;
    private final Texture loadScreen;

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color back = new Color(0x666666ff);

    private final ShapeRenderer cornerBox = new ShapeRenderer();
    private static final Color corner = new Color(0xffffffff);

    public LoadingScreen(GhosthouseGame game) {
        super(game);
        musicManager = game.getMusicManager();
        loadScreen = assetManager.get("splash/loading_splash.png");
    }

    @Override
    public void show() {

        //Validate on the run
        try {
            ValidScript.validateAllScriptsGdx();
        } catch (Exception e) {
            //If validation fails, stop game
            throw new GdxRuntimeException(e);
        }

        assetManager.load("sprites/walfrit_sheet.png", Texture.class);

        assetManager.load("sounds/SFX_collect&bonus13.ogg", Sound.class);

        assetManager.load("sprites/hud.png", Texture.class);

        musicManager.loadMusicFromFiles(assetManager);

        JsonValue allSplashScreens = new JsonReader().parse(Gdx.files.internal("splash/splashScreen.json")).get("splashScreens");
        for (JsonValue splashInfo : allSplashScreens) {
            assetManager.load(splashInfo.getString("splashPath"), Texture.class);
        }

    }

    @Override
    public void render(float delta) {

        cameraManager.applyAndUpdate();
        game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

        assetManager.update();
        if(assetManager.isFinished()){
            game.setScreen(new MenuScreen(game));
        }

        double loadingProgress = Math.floor(assetManager.getProgress()*100);

        //Load screen
        game.getBatch().begin();
        game.getBatch().draw(loadScreen,0,0);
        game.getBatch().end();

        //Black Corner
        game.getBatch().begin();
        backgroundBox.setColor(back);
        backgroundBox.setProjectionMatrix(cameraManager.getCamera().combined);
        backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
        backgroundBox.rect(10, GhosthouseGame.GAME_HEIGHT/2-6, GhosthouseGame.GAME_WIDTH-20,  10);
        backgroundBox.end();

        cornerBox.setColor(corner);
        cornerBox.setProjectionMatrix(cameraManager.getCamera().combined);
        cornerBox.begin(ShapeRenderer.ShapeType.Filled);
        cornerBox.rect(12, GhosthouseGame.GAME_HEIGHT/2-5, (float) (GhosthouseGame.GAME_WIDTH-25 - (100-loadingProgress)), 8);
        cornerBox.end();
        game.getBatch().end();

        Gdx.app.log("DEBUG", "Loading progress: " + loadingProgress + "%" );
    }
}
