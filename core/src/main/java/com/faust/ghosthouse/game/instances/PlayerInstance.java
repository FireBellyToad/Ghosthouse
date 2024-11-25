package com.faust.ghosthouse.game.instances;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.faust.ghosthouse.game.gameentities.enums.DirectionEnum;
import com.faust.ghosthouse.game.gameentities.enums.GameBehavior;
import com.faust.ghosthouse.game.gameentities.impl.PlayerEntity;
import com.faust.ghosthouse.game.rooms.RoomContent;
import com.faust.ghosthouse.world.CollisionManager;
import com.faust.ghosthouse.world.WorldManager;

import java.util.Objects;

public class PlayerInstance extends AnimatedInstance implements InputProcessor {
    private static final float FLOOR_OFFSET = 2;
    private static final float PLAYER_SPEED = 50;
    private static final float JUMP_FORCE = 10f * WorldManager.FORCE_MODIFIER;

    public PlayerInstance() {
        super(new PlayerEntity());
        this.currentDirectionEnum = DirectionEnum.RIGHT;
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {

        // If the player has stopped moving, set idle behaviour
        if (this.body.getLinearVelocity().x == 0 && this.body.getLinearVelocity().y == 0) {
            changeCurrentBehavior(GameBehavior.IDLE);
        } else {
            changeCurrentBehavior(GameBehavior.WALK);
        }
        // Set horizontal direction if horizontal velocity is not zero
        if (this.body.getLinearVelocity().x == PLAYER_SPEED) {
            this.currentDirectionEnum = DirectionEnum.RIGHT;
        } else if (this.body.getLinearVelocity().x == -PLAYER_SPEED) {
            this.currentDirectionEnum = DirectionEnum.LEFT;
        }
    }

    @Override
    public void createBody(World world, float x, float y) {
        Objects.requireNonNull(world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;
        bodyDef.position.set(x, y + POSITION_Y_OFFSET);

        // Define shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(6, 12);

        // Define Fixtures
        FixtureDef mainFixtureDef = new FixtureDef();
        mainFixtureDef.shape = shape;
        mainFixtureDef.friction = 0;
        mainFixtureDef.density = 0;
        mainFixtureDef.filter.categoryBits = CollisionManager.PLAYER_GROUP;

        // Associate body to world
        body = world.createBody(bodyDef);

        body.setUserData(this);
        body.createFixture(mainFixtureDef);
        shape.dispose();

    }

    /**
     * Handles player bodies movement
     *
     * @param horizontalVelocity
     * @param verticalVelocity
     */
    private void setPlayerLinearVelocity(float horizontalVelocity, float verticalVelocity) {

        this.body.setLinearVelocity(horizontalVelocity, verticalVelocity);
    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {

        // Get frame (looping if is not dead)
        TextureRegion frame = ((PlayerEntity) entity).getFrame(getCurrentBehavior(), currentDirectionEnum,
            mapStateTimeFromBehaviour(stateTime), !GameBehavior.DEAD.equals(getCurrentBehavior()));

        Vector2 drawPosition = adjustPosition();
        batch.draw(frame, drawPosition.x - POSITION_OFFSET, drawPosition.y - POSITION_Y_OFFSET + FLOOR_OFFSET);
    }


    private float mapStateTimeFromBehaviour(float stateTime) {
        return stateTime;
    }

    @Override
    public boolean keyDown(int keycode) {

        // If hurt o dying, can't do anything
        if (GameBehavior.HURT.equals(getCurrentBehavior()) || GameBehavior.DEAD.equals(getCurrentBehavior())) {
            return false;
        }
        // Keep the initial velocity
        float horizontalVelocity = this.body.getLinearVelocity().x;
        float verticalVelocity = this.body.getLinearVelocity().y;

        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT: {
                horizontalVelocity = -PLAYER_SPEED;
                this.currentDirectionEnum = DirectionEnum.LEFT;

                break;
            }
            case Input.Keys.D:
            case Input.Keys.RIGHT: {
                horizontalVelocity = +PLAYER_SPEED;
                this.currentDirectionEnum = DirectionEnum.RIGHT;
                break;
            }
            case Input.Keys.Z:
            case Input.Keys.J: {
                verticalVelocity += JUMP_FORCE;
                break;
            }

        }

        setPlayerLinearVelocity(horizontalVelocity, verticalVelocity);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        // Keep the initial velocity
        float horizontalVelocity = this.body.getLinearVelocity().x;
        float verticalVelocity = this.body.getLinearVelocity().y;

        // Determine new velocity
        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.D:
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT: {
                horizontalVelocity = 0;
                break;
            }
        }
        setPlayerLinearVelocity(horizontalVelocity, verticalVelocity);
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
