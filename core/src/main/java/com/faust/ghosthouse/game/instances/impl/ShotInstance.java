package com.faust.ghosthouse.game.instances.impl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.faust.ghosthouse.game.gameentities.enums.DirectionEnum;
import com.faust.ghosthouse.game.gameentities.enums.GameBehavior;
import com.faust.ghosthouse.game.gameentities.impl.ShotEntity;
import com.faust.ghosthouse.game.instances.AnimatedInstance;
import com.faust.ghosthouse.game.instances.interfaces.Killable;
import com.faust.ghosthouse.game.rooms.RoomContent;
import com.faust.ghosthouse.world.CollisionManager;
import com.faust.ghosthouse.world.WorldManager;

import java.util.Objects;

public class ShotInstance extends AnimatedInstance implements Killable {
    private static final float SHOT_SPEED = 60 * WorldManager.FORCE_MODIFIER;
    public static final int POSITION_Y_OFFSET = 15;
    private static final float SEGMENT_LENGTH = 8;
    private int segments = 1;

    public ShotInstance(DirectionEnum currentDirectionEnum) {
        super(new ShotEntity());
        this.currentDirectionEnum = currentDirectionEnum;
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        switch (currentDirectionEnum) {
            case LEFT -> body.setLinearVelocity(-SHOT_SPEED, 0);
            case RIGHT -> body.setLinearVelocity(SHOT_SPEED, 0);
            default -> throw new IllegalStateException("Impossible direction!");
        }

        // Coyote time
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                changeCurrentBehavior(GameBehavior.DEAD);
            }
        }, 0.5f);
    }

    @Override
    public void createBody(World world, float x, float y) {
        Objects.requireNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(x, y);

        // Define shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6, 3);

        // Define Fixtures
        FixtureDef mainFixtureDef = new FixtureDef();
        mainFixtureDef.shape = shape;
        mainFixtureDef.friction = 0;
        mainFixtureDef.density = 0;
        mainFixtureDef.isSensor = true;
        mainFixtureDef.filter.categoryBits = CollisionManager.ENEMY_GROUP;

        // Associate body to world
        body = world.createBody(bodyDef);
        body.setGravityScale(0);
        body.setUserData(this);
        body.createFixture(mainFixtureDef);
        shape.dispose();

        startX = x;
        startY = y;
    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {

        if (GameBehavior.DEAD.equals(getCurrentBehavior())) {
            return;
        }

        // Get frame (looping if is not dead)
        TextureRegion frame = ((ShotEntity) entity).getFrame(getCurrentBehavior(), currentDirectionEnum, stateTime, !GameBehavior.DEAD.equals(getCurrentBehavior()));

        Vector2 drawPosition = adjustPosition();

        for (int i = 0; i < segments; i++) {
            if(DirectionEnum.RIGHT.equals(currentDirectionEnum)){
                batch.draw(frame, drawPosition.x - POSITION_OFFSET - (i * SEGMENT_LENGTH), drawPosition.y - POSITION_Y_OFFSET);
            } else {
                batch.draw(frame, drawPosition.x - POSITION_OFFSET + (i * SEGMENT_LENGTH), drawPosition.y - POSITION_Y_OFFSET);
            }
        }

        if ((DirectionEnum.RIGHT.equals(currentDirectionEnum) &&getBody().getPosition().x == startX + (SEGMENT_LENGTH * segments) )||
            (DirectionEnum.LEFT.equals(currentDirectionEnum)  &&(getBody().getPosition().x == startX - (SEGMENT_LENGTH * segments)))){
            segments++;
        }
    }

    @Override
    public boolean isDying() {
        return false;
    }

    @Override
    public boolean isDead() {
        return GameBehavior.DEAD.equals(getCurrentBehavior());
    }

    @Override
    public boolean isDisposable() {
        return isDead();
    }
}
