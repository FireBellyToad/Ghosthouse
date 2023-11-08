package com.faust.ghosthouse.game.rooms;

import com.faust.ghosthouse.game.gameentities.enums.DirectionEnum;
import com.faust.ghosthouse.game.rooms.enums.RoomTypeEnum;

import java.util.Map;

/**
 * Simple room model
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class RoomModel {

    public final Map<DirectionEnum, RoomPosition> boundaries;
    public final RoomTypeEnum type;

    public RoomModel(Map<DirectionEnum, RoomPosition> boundaries, RoomTypeEnum type) {
        this.boundaries = boundaries;
        this.type = type;
    }

}
