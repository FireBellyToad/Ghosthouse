package com.faust.ghosthouse.game.scripts.enums;


/**
 * Script Actor entity class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum ScriptActorType {;

    private final String filename;
    private final String spriteFilename;
    private final String soundFileName;

    ScriptActorType(String filename) {
        this.filename = filename;
        this.spriteFilename = "sprites/fleshpillar_sheet.png";
        this.soundFileName = null;
    }

    ScriptActorType(String filename, String spriteFilename) {
        this.filename = filename;
        this.spriteFilename = spriteFilename;
        this.soundFileName = null;
    }

    ScriptActorType(String filename, String spriteFilename, String soundFileName) {
        this.filename = filename;
        this.spriteFilename = spriteFilename;
        this.soundFileName = soundFileName;
    }

    public String getFilename() {
        return filename;
    }

    public String getSpriteFilename() {
        return spriteFilename;
    }

    public String getSoundFileName() {
        return soundFileName;
    }
}
