package com.faust.ghosthouse.game.gameentities.impl;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.faust.ghosthouse.game.gameentities.AnimatedEntity;
import com.faust.ghosthouse.game.gameentities.SpriteEntity;
import com.faust.ghosthouse.game.gameentities.enums.DirectionEnum;
import com.faust.ghosthouse.game.gameentities.enums.GameBehavior;

import java.util.Arrays;

public class PlayerEntity extends AnimatedEntity {
    public PlayerEntity() {
        super(new Texture("sprites/player-sheet.png"));
    }

    @Override
    protected void initAnimations() {

        TextureRegion[] allFrames = getFramesFromTexture();

        TextureRegion[] idleFramesRight = Arrays.copyOfRange(allFrames, 0, getTextureColumns());
        TextureRegion[] idleFramesLeft = Arrays.copyOfRange(allFrames, getTextureColumns(), getTextureColumns() * 2);

        // Initialize the Idle Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesLeft), GameBehavior.IDLE, DirectionEnum.LEFT);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesRight), GameBehavior.IDLE, DirectionEnum.RIGHT);
    }

    @Override
    protected int getTextureColumns() {
        return 1;
    }

    @Override
    protected int getTextureRows() {
        return 2;
    }
}
