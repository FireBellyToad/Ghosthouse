package com.faust.ghosthouse.game.rooms.interfaces;

import com.faust.ghosthouse.game.instances.GameInstance;

/**
 * Interface for adding game instances in something during runtime (spawning)
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public interface SpawnFactory {

    <T extends GameInstance> void spawnInstance(Class<T> instanceClass, float startX, float startY, Object extraInfo);
}
