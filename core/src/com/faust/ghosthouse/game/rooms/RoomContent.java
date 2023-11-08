package com.faust.ghosthouse.game.rooms;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.faust.ghosthouse.game.ai.RoomNodesGraph;
import com.faust.ghosthouse.game.instances.AnimatedInstance;
import com.faust.ghosthouse.game.instances.GameInstance;
import com.faust.ghosthouse.game.instances.impl.POIInstance;
import com.faust.ghosthouse.game.instances.impl.PlayerInstance;
import com.faust.ghosthouse.game.rooms.areas.TriggerArea;
import com.faust.ghosthouse.game.rooms.areas.WallArea;
import com.faust.ghosthouse.game.rooms.enums.RoomFlagEnum;
import com.faust.ghosthouse.game.rooms.enums.RoomTypeEnum;
import com.faust.ghosthouse.game.instances.impl.DecorationInstance;

import java.util.List;
import java.util.Map;

/**
 * Model class for storing Rooom contents
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomContent {

    public TiledMap tiledMap;
    public List<POIInstance> poiList;
    public List<DecorationInstance> decorationList;
    public List<AnimatedInstance> enemyList;
    public List<WallArea> wallList;

    public PlayerInstance player;
    public RoomTypeEnum roomType;
    public String roomFileName;
    public List<GameInstance> spellEffects;
    public List<POIInstance> removedPoiList;
    public List<TriggerArea> triggerAreaList;

    public Map<RoomFlagEnum, Boolean> roomFlags;
    public RoomNodesGraph roomGraph;
}
