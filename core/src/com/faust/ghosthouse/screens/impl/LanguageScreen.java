package com.faust.ghosthouse.screens.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.faust.ghosthouse.screens.AbstractScreen;
import com.faust.ghosthouse.GhosthouseGame;
import com.faust.ghosthouse.utils.TextLocalizer;
import com.faust.ghosthouse.menu.Menu;
import com.faust.ghosthouse.menu.enums.MenuItem;

/**
 * Class for game over screen
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class LanguageScreen extends AbstractScreen {

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color darkness = new Color(0x000000ff);

    private final TextLocalizer textLocalizer;
    private final Menu menu;

    public LanguageScreen(GhosthouseGame game) {
        super(game);
        textLocalizer = game.getTextLocalizer();

        menu = new Menu(game.getSaveFileManager(), MenuItem.LANGUAGE, assetManager);
    }

    @Override
    public void show() {
        //Load next screen image
        assetManager.load("splash/fbt_splash.png", Texture.class);
        assetManager.finishLoading();

        menu.loadFonts(assetManager);

        Gdx.input.setInputProcessor(menu);

        if(this.game.isWebBuild()){
            //Prevents arrow keys browser scrolling
            Gdx.input.setCatchKey(Input.Keys.UP, true);
            Gdx.input.setCatchKey(Input.Keys.DOWN, true);
            Gdx.input.setCatchKey(Input.Keys.LEFT, true);
            Gdx.input.setCatchKey(Input.Keys.RIGHT, true);
        }
    }

    @Override
    public void render(float delta) {

        if (menu.isChangeToNextScreen()) {
            //Set language and Change screen
            textLocalizer.setLanguage( menu.getSelectedMenuVoice() == 0 ? "eng" : "ita");
            Gdx.input.setInputProcessor(null);
            game.setScreen(new FBTScreen(game));
        } else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Black background
            game.getBatch().begin();
            backgroundBox.setColor(darkness);
            backgroundBox.setProjectionMatrix(cameraManager.getCamera().combined);
            backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
            backgroundBox.rect(0, 0, GhosthouseGame.GAME_WIDTH,  GhosthouseGame.GAME_HEIGHT);
            backgroundBox.end();
            game.getBatch().end();

            //Menu screen render
            game.getBatch().begin();
            menu.drawCurrentMenu(game.getBatch());
            game.getBatch().end();
        }

    }
}