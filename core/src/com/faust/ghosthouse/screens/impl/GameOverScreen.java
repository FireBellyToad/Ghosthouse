package com.faust.ghosthouse.screens.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.faust.ghosthouse.screens.AbstractScreen;
import com.faust.ghosthouse.GhosthouseGame;
import com.faust.ghosthouse.game.music.MusicManager;
import com.faust.ghosthouse.game.music.enums.TuneEnum;
import com.faust.ghosthouse.utils.TextLocalizer;
import com.faust.ghosthouse.menu.Menu;
import com.faust.ghosthouse.menu.enums.MenuItem;

/**
 * Class for game over screen
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class GameOverScreen extends AbstractScreen {

    private final MusicManager musicManager;
    private final TextLocalizer textLocalizer;
    private final Menu menu;
    private final Texture gameOverScreen;

    public GameOverScreen(GhosthouseGame game) {
        super(game);
        musicManager = game.getMusicManager();
        textLocalizer = game.getTextLocalizer();
        gameOverScreen = assetManager.get("splash/gameover_splash.png");

        //Permadeath!
        game.getSaveFileManager().cleanSaveFile();

        menu = new Menu(game.getSaveFileManager(), MenuItem.GAME_OVER, assetManager);
    }

    @Override
    public void show() {
        //Load next screen image
        assetManager.load("splash/gameover_splash.png", Texture.class);
        assetManager.finishLoading();

        menu.loadFonts(assetManager);

        //Loop title music
        musicManager.playMusic(TuneEnum.GAMEOVER, false);

        Gdx.input.setInputProcessor(menu);
    }

    @Override
    public void render(float delta) {

        if (menu.isChangeToNextScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new MenuScreen(game));
        } else if (menu.isChangeToGameScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new GameScreen(game));
        } else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Menu screen render
            game.getBatch().begin();
            game.getBatch().draw(gameOverScreen, 0, 0);
            menu.drawCurrentMenuLocalized(game.getBatch(), textLocalizer);
            game.getBatch().end();
        }

    }
}