package com.faust.ghosthouse.game.instances.interfaces;

/**
 * Interface for describing dying behaviour
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public interface Killable {

    /**
     * @return true if the instance is dying, usually a condition like "damage is greater or equal than its resistance"
     */
    boolean isDying();

    /**
     * @return true if the instance is really dead
     */
    boolean isDead();
}
