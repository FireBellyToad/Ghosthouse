package com.faust.ghosthouse.game.instances;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.faust.ghosthouse.GhostHouse;
import com.faust.ghosthouse.game.gameentities.enums.DirectionEnum;
import com.faust.ghosthouse.game.gameentities.enums.GameBehavior;
import com.faust.ghosthouse.game.gameentities.impl.PlayerEntity;
import com.faust.ghosthouse.game.rooms.RoomContent;

public class PlayerInstance extends AnimatedInstance implements InputProcessor {
    public PlayerInstance() {
        super(new PlayerEntity());
        this.currentDirectionEnum = DirectionEnum.RIGHT;
    }

    @Override
    public void doLogic(float stateTime, RoomContent roomContent) {
        //Nothing to do here... yet
    }

    @Override
    public void createBody(World world, float x, float y) {
        //Nothing to do here... yet

    }

    @Override
    public void draw(SpriteBatch batch, float stateTime) {

        // Get frame (looping if is not dead)
        TextureRegion frame = ((PlayerEntity) entity).getFrame(getCurrentBehavior(), currentDirectionEnum,
            mapStateTimeFromBehaviour(stateTime), !GameBehavior.DEAD.equals(getCurrentBehavior()));

//TODO enable when body2d is working
//        Vector2 drawPosition = adjustPosition();
//        batch.draw(frame, drawPosition.x - POSITION_OFFSET, drawPosition.y - POSITION_Y_OFFSET-4);
        Vector2 drawPosition = new Vector2(GhostHouse.GAME_WIDTH / 2, (GhostHouse.GAME_HEIGHT / 2));
        batch.draw(frame, drawPosition.x - POSITION_OFFSET, drawPosition.y - POSITION_Y_OFFSET - 4);
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

        switch (keycode) {
            case Input.Keys.A:
            case Input.Keys.LEFT: {
                this.currentDirectionEnum = DirectionEnum.LEFT;

                break;
            }
            case Input.Keys.D:
            case Input.Keys.RIGHT: {
                this.currentDirectionEnum = DirectionEnum.RIGHT;
                break;
            }

        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
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
