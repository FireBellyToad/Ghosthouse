package com.faust.ghosthouse.game.instances.interfaces;

/**
 * Interface for describing interaction with player instance
 */
public interface Interactable {

    void doPlayerInteraction(PlayerInstance playerInstance);
    
    void endPlayerInteraction(PlayerInstance playerInstance);
}
