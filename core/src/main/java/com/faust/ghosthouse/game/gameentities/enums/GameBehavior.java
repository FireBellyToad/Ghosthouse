package com.faust.ghosthouse.game.gameentities.enums;

/**
 * Game Behavior class.
 * This enum tells us which behaviour the GameInstance is.
 * Used for animation and ScriptActor steps
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum GameBehavior {
    WALK,
    JUMP,
    HURT,
    ATTACK,
    IDLE,
    DEAD;

    public static GameBehavior getFromOrdinal(int ord) {
        return GameBehavior.values()[ord];
    }
    }
