package com.faust.ghosthouse.game.instances.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.faust.ghosthouse.game.gameentities.impl.ParticleEffectEntity;
import com.faust.ghosthouse.game.instances.GameInstance;
import com.faust.ghosthouse.game.rooms.RoomContent;

import java.util.Objects;

/**
 * Confusion spell instance class
 *
 * @author Jacopo "Faust" Buttiglieri
 */
public class DebugParticle extends GameInstance  {

    private final long attackTimer = 0;

    public DebugParticle(float x, float y) {
        super(new ParticleEffectEntity("debug"));
        this.startX = x;
        this.startY = y;

        ((ParticleEffectEntity) entity).getParticleEffect().start();
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        ((ParticleEffectEntity) entity).getParticleEffect().setPosition(startX, startY);
    }

    @Override
    public void createBody(World world, float x, float y) {


    }

    /**
     * Draw the Entity frames using Body position
     *
     * @param batch
     * @param stateTime
     */
    public void draw(final SpriteBatch batch, float stateTime) {
        Objects.requireNonNull(batch);
        ((ParticleEffectEntity) entity).getParticleEffect().draw(batch, Gdx.graphics.getDeltaTime());
    }

}
