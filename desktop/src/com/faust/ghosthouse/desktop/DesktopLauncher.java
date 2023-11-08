package com.faust.ghosthouse.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.faust.ghosthouse.saves.impl.DesktopSaveFileManager;
import com.faust.ghosthouse.GhosthouseGame;

public class DesktopLauncher {
	private static final int SCALE_FACTOR = 6;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "LHEngine";
		config.resizable = false;
		config.width = GhosthouseGame.GAME_WIDTH * SCALE_FACTOR;
		config.height = GhosthouseGame.GAME_HEIGHT * SCALE_FACTOR;
		//if parameter w is set, go windowed
		config.fullscreen = false;//!Arrays.stream(arg).anyMatch(stringarg -> "w".equals(stringarg) || "windowed".equals(stringarg));
		config.addIcon("icon.png", Files.FileType.Internal);
		new LwjglApplication(new GhosthouseGame(false, new DesktopSaveFileManager()), config);
	}
}
