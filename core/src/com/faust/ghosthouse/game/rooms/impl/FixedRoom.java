package com.faust.ghosthouse.game.rooms.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.faust.ghosthouse.game.instances.impl.POIInstance;
import com.faust.ghosthouse.game.instances.impl.PlayerInstance;
import com.faust.ghosthouse.game.music.MusicManager;
import com.faust.ghosthouse.game.music.enums.TuneEnum;
import com.faust.ghosthouse.game.rooms.AbstractRoom;
import com.faust.ghosthouse.game.rooms.enums.*;
import com.faust.ghosthouse.game.rooms.enums.RoomTypeEnum;
import com.faust.ghosthouse.saves.RoomSaveEntry;
import com.faust.ghosthouse.game.gameentities.enums.ItemEnum;
import com.faust.ghosthouse.game.instances.GameInstance;
import com.faust.ghosthouse.game.instances.interfaces.Killable;
import com.faust.ghosthouse.game.rooms.areas.TriggerArea;
import com.faust.ghosthouse.game.scripts.enums.ScriptActorType;
import com.faust.ghosthouse.game.splash.SplashManager;
import com.faust.ghosthouse.game.textbox.manager.TextBoxManager;
import com.faust.ghosthouse.game.world.manager.WorldManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Fixed Room class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class FixedRoom extends AbstractRoom {

    private String layerToDraw = MapLayersEnum.TERRAIN_LAYER.getLayerName();

    public FixedRoom(final RoomTypeEnum roomType, final WorldManager worldManager, final TextBoxManager textManager, final SplashManager splashManager, final PlayerInstance player, final OrthographicCamera camera, final AssetManager assetManager, final RoomSaveEntry roomSaveEntry, MusicManager musicManager) {
        super(roomType, worldManager, textManager, splashManager, player, camera, assetManager, roomSaveEntry, musicManager);
    }

    @Override
    protected void loadTiledMap(RoomSaveEntry roomSaveEntry) {
        // Load Tiled map
        roomContent.tiledMap = new TmxMapLoader().load(roomContent.roomFileName);
        //onMapChange() todo

    }

    @Override
    protected void onRoomEnter(RoomTypeEnum roomType, WorldManager worldManager, AssetManager assetManager, RoomSaveEntry roomSaveEntry, MapObjects mapObjects) {

        this.roomContent.triggerAreaList = new ArrayList<>();
        mapObjects.forEach(obj -> {

            // Prepare Triggers if not disabled
            if (!roomContent.roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && MapObjTypeEnum.TRIGGER.name().equals(obj.getProperties().get("type"))) {
                addObjAsTrigger(obj);
            }
        });

        worldManager.insertTriggersIntoWorld(roomContent.triggerAreaList);

        if (Objects.nonNull(roomSaveEntry)) {
            roomSaveEntry.poiStates.forEach((id, isExamined) -> {

                //update POI status
                POIInstance poi = this.roomContent.poiList.stream().filter(p -> id.equals(p.getPoiIdInMap())).findFirst().orElse(null);

                if (Objects.nonNull(poi)) {
                    poi.setAlreadyExamined(isExamined);
                }
            });
        }

        if (RoomTypeEnum.FINAL.equals(roomType)) {
            //Loop title music
            musicManager.playMusic(TuneEnum.CHURCH, 0.75f);
        } else if (roomContent.enemyList.size() > 0) {
            //Loop title music
            musicManager.playMusic(TuneEnum.DANGER, 0.75f);
        } else {
            //Loop title music
            musicManager.playMusic(TuneEnum.AMBIENCE, 0.85f);
        }
    }

    /**
     * Add invisible emerged areas
     *
     * @param obj
     */
    protected void addObjAsTrigger(MapObject obj) {

        RectangleMapObject mapObject = (RectangleMapObject) obj;

        TriggerTypeEnum triggerTypeEnum = TriggerTypeEnum.valueOf((String) mapObject.getProperties().get("triggerType"));
        Integer referencedInstanceId = (Integer) (mapObject.getProperties().containsKey("referencedInstanceId") ? mapObject.getProperties().get("referencedInstanceId") : null);

        GameInstance referencedInstance = null;
        List<ItemEnum> requiredItemList = Collections.emptyList();

        //Search referencedInstanceId in POIs
        if(Objects.nonNull(referencedInstanceId)){

            referencedInstance = roomContent.poiList.stream().filter(p -> p.getPoiIdInMap() == referencedInstanceId).findFirst().orElse(null);

            //Fallback on decorations if no POI with referencedInstanceId has been found
            if(Objects.isNull(referencedInstance)){
                referencedInstance = roomContent.decorationList.stream().filter(d -> d.getDecoIdInMap() == referencedInstanceId).findFirst().orElse(null);
            } else {
                //Set requiredItemList to the item required to activate POI (right now Singleton list)
                POIInstance poi = ((POIInstance) referencedInstance);
                if(Objects.nonNull(poi.getType().getItemRequired())){
                    requiredItemList = Collections.singletonList(poi.getType().getItemRequired());
                }
            }
        }

        roomContent.triggerAreaList.add(new TriggerArea((int) mapObject.getProperties().get("id"),
                triggerTypeEnum, requiredItemList,
                mapObject.getRectangle(), referencedInstance));
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void doRoomContentsLogic(float stateTime) {
        super.doRoomContentsLogic(stateTime);
        layerToDraw = MapLayersEnum.TERRAIN_LAYER.getLayerName();
        roomContent.enemyList.removeIf(ene -> ((Killable) ene).isDead());
    }

    @Override
    public void onRoomLeave(RoomSaveEntry roomSaveEntry) {
        roomContent.poiList.forEach(poiInstance -> roomSaveEntry.poiStates.put(poiInstance.getPoiIdInMap(), poiInstance.isAlreadyExamined()));
        roomContent.removedPoiList.forEach(poiInstance -> roomSaveEntry.poiStates.put(poiInstance.getPoiIdInMap(), poiInstance.isAlreadyExamined()));

        //Disable Echo on room leave if activated trigger is already examined POI
        if (!roomContent.roomFlags.get(RoomFlagEnum.DISABLED_ECHO) && roomContent.triggerAreaList.stream().anyMatch(t->
                (t.isActivated() && Objects.isNull(t.getReferencedInstance())) || //activated trigger without referenced Instance
                        (t.isActivated() && Objects.nonNull(t.getReferencedInstance()) && !(t.getReferencedInstance() instanceof POIInstance)) ||  //activated trigger with a non POI referenced Instance
                        (t.isActivated() && Objects.nonNull(t.getReferencedInstance()) && t.getReferencedInstance() instanceof POIInstance && ((POIInstance) t.getReferencedInstance()).isAlreadyExamined()))) {//activated trigger with an activated POI referenced Instance
            roomContent.roomFlags.put(RoomFlagEnum.DISABLED_ECHO, true);
        }

        //always enable enemies
        roomContent.roomFlags.put(RoomFlagEnum.DISABLED_ENEMIES, false);
    }

    @Override
    public String getLayerToDraw() {
        return layerToDraw;
    }

}
