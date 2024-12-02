package com.faust.ghosthouse.game.gameentities.impl;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.faust.ghosthouse.game.gameentities.AnimatedEntity;
import com.faust.ghosthouse.game.gameentities.enums.DirectionEnum;
import com.faust.ghosthouse.game.gameentities.enums.GameBehavior;

import java.util.Arrays;

public class ShotEntity extends AnimatedEntity {

    public ShotEntity() {
        super(new Texture("sprites/shot-sheet.png"));
    }

    @Override
    protected void initAnimations() {
        TextureRegion[] allFrames = getFramesFromTexture();

        TextureRegion[] idleFramesRight = Arrays.copyOfRange(allFrames, 0, getTextureColumns());

        // Initialize the Idle Animation with the frame interval and array of frames
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesRight), GameBehavior.IDLE, DirectionEnum.LEFT);
        addAnimationForDirection(new Animation<>(FRAME_DURATION, idleFramesRight), GameBehavior.IDLE, DirectionEnum.RIGHT);

    }

    @Override
    protected int getTextureColumns() {
        return 6;
    }

    @Override
    protected int getTextureRows() {
        return 1;
    }
}
