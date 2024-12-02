package com.faust.ghosthouse.game.rooms;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.faust.ghosthouse.game.instances.AnimatedInstance;
import com.faust.ghosthouse.game.instances.impl.DebugParticle;
import com.faust.ghosthouse.game.instances.impl.PlayerInstance;
import com.faust.ghosthouse.game.instances.impl.ShotInstance;
import com.faust.ghosthouse.game.rooms.areas.WallArea;

import java.util.List;

/**
 * Model class for storing Rooom contents
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomContent {

    public TiledMap tiledMap;
    public List<AnimatedInstance> enemyList;
    public List<WallArea> wallList;
    public PlayerInstance player;
    public List<DebugParticle> debugInstances;
    public ShotInstance shot;
}
