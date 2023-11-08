package com.faust.ghosthouse.screens.impl;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import com.faust.ghosthouse.screens.AbstractScreen;
import com.faust.ghosthouse.GhosthouseGame;

public class FBTScreen  extends AbstractScreen {

    private final Texture fbtScreen;

    public FBTScreen(GhosthouseGame game) {
        super(game);
        fbtScreen = assetManager.get("splash/fbt_splash.png");
    }

    @Override
    public void show() {

        // Load next menu assets
        assetManager.load("splash/title_splash.png", Texture.class);
        assetManager.finishLoading();

        assetManager.load("fonts/main_font.png", Texture.class);
        assetManager.finishLoading();

        //Load next screen image
        assetManager.load("splash/loading_splash.png", Texture.class);
        assetManager.finishLoading();

        //Two seconds splash screen
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                game.setScreen(new LoadingScreen(game));
            }
        }, 2);
    }

    @Override
    public void render(float delta) {

        cameraManager.applyAndUpdate();
        game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);
        cameraManager.renderBackground();

        //Load screen
        game.getBatch().begin();
        game.getBatch().draw(fbtScreen,0,0);
        game.getBatch().end();

    }
}
