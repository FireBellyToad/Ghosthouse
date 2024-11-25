package com.faust.ghosthouse.game.rooms.enums;

/**
 * Map Layer enum
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public enum MapLayersEnum {
    TILES_LAYER("tiles"),
    OBJECT_LAYER("objects");

    private final String layerName;

    MapLayersEnum(String layerName) {
        this.layerName = layerName;
    }

    public String getLayerName() {
        return layerName;
    }
}
