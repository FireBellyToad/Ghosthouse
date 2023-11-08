package com.faust.ghosthouse.screens.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.faust.ghosthouse.GhosthouseGame;
import com.faust.ghosthouse.enums.cutscenes.CutsceneEnum;
import com.faust.ghosthouse.screens.AbstractScreen;
import com.faust.ghosthouse.game.music.MusicManager;
import com.faust.ghosthouse.game.music.enums.TuneEnum;
import com.faust.ghosthouse.utils.TextLocalizer;
import com.faust.ghosthouse.menu.Menu;

/**
 * Menu screen class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class MenuScreen extends AbstractScreen {

    private final MusicManager musicManager;
    private final TextLocalizer textLocalizer;
    private final Menu menu;
    private final Texture titleTexture;

    public MenuScreen(GhosthouseGame game) {
        super(game);
        musicManager = game.getMusicManager();
        textLocalizer = game.getTextLocalizer();

        titleTexture = assetManager.get("splash/title_splash.png");
        musicManager.loadSingleTune(TuneEnum.TITLE, assetManager);

        menu = new Menu(game.getSaveFileManager(),assetManager);
    }

    @Override
    public void show() {
        menu.loadFonts(assetManager);
        textLocalizer.loadTextFromLanguage();

        //Loop title music
        musicManager.playMusic(TuneEnum.TITLE);

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

        if (menu.isChangeToIntroScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new CutsceneScreen(game, CutsceneEnum.INTRO));
        } else if (menu.isChangeToGameScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new GameScreen(game));
        }  else if (menu.isChangeToCreditScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new CutsceneScreen(game, CutsceneEnum.CREDITS));
        }  else if (menu.isChangeToStoryScreen()) {
            //Stop music and change screen
            musicManager.stopMusic();
            game.setScreen(new CutsceneScreen(game, CutsceneEnum.STORY));
        }else {
            cameraManager.applyAndUpdate();
            game.getBatch().setProjectionMatrix(cameraManager.getCamera().combined);

            //Menu screen render
            game.getBatch().begin();
            game.getBatch().draw(titleTexture, 0, 0);
            menu.drawCurrentMenuLocalized(game.getBatch(), textLocalizer);
            game.getBatch().end();
        }

    }
}