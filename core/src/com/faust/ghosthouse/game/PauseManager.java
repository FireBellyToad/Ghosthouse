package com.faust.ghosthouse.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.faust.ghosthouse.GhosthouseGame;
import com.faust.ghosthouse.game.instances.impl.PlayerInstance;
import com.faust.ghosthouse.menu.Menu;
import com.faust.ghosthouse.menu.enums.MenuItem;
import com.faust.ghosthouse.saves.AbstractSaveFileManager;
import com.faust.ghosthouse.screens.impl.MenuScreen;
import com.faust.ghosthouse.game.rooms.manager.RoomsManager;
import com.faust.ghosthouse.game.music.MusicManager;

/**
 * @author Jacopo "Faust" Buttiglieri
 */
public class PauseManager {

    private final Menu menu;
    private final AbstractSaveFileManager saveFileManager;
    private boolean gamePaused = false;
    private final MusicManager musicManager;

    private final ShapeRenderer backgroundBox = new ShapeRenderer();
    private static final Color back = new Color(0x000000ff);


    public PauseManager(AbstractSaveFileManager saveFileManager, MusicManager musicManager, AssetManager assetManager) {

        this.musicManager = musicManager;
        this.saveFileManager = saveFileManager;
        menu = new Menu(saveFileManager, MenuItem.PAUSE_GAME, assetManager);
        menu.loadFonts(assetManager);
    }

    /**
     *
     * @param game
     */
    public void doLogic(GhosthouseGame game, PlayerInstance playerInstance, RoomsManager roomsManager){
        //Exit or resume game game
        if(menu.isChangeToGameScreen()){
            resumeGame();
        } else if (menu.isChangeToNextScreen()){
            saveFileManager.saveOnFile(playerInstance,roomsManager.getSaveMap());
            game.setScreen(new MenuScreen(game));
        }
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera) {

        //Black Background
        batch.begin();
        backgroundBox.setColor(back);
        backgroundBox.setProjectionMatrix(camera.combined);
        backgroundBox.begin(ShapeRenderer.ShapeType.Filled);
        backgroundBox.rect(0, (float) ((GhosthouseGame.GAME_HEIGHT * 0.5)-40), GhosthouseGame.GAME_WIDTH , 30);
        backgroundBox.end();
        batch.end();

        batch.begin();
        menu.drawCurrentMenu(batch);
        batch.end();
    }

    /**
     * Pause and set as inputProcessor
     */
    public void pauseGame(){
        gamePaused = true;
        musicManager.pauseMusic();
        Gdx.input.setInputProcessor(menu);
    }

    /**
     * Unpause and reset menu
     */
    public void resumeGame(){
        gamePaused = false;
        musicManager.resumeMusic();
        menu.reset();
    }

    public boolean isGamePaused() {
        return gamePaused;
    }
}
