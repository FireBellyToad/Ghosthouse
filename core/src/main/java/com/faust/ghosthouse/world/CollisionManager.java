package com.faust.ghosthouse.world;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import com.faust.ghosthouse.game.gameentities.enums.GameBehavior;
import com.faust.ghosthouse.game.instances.impl.PlayerInstance;
import com.faust.ghosthouse.game.rooms.areas.WallArea;

import java.util.Objects;

/**
 * Class for handling all the collisions between GameInstances
 */
public class CollisionManager implements ContactListener {

    public static final int SOLID_GROUP = 1;
    public static final int PLAYER_GROUP = 2;
    public static final int ENEMY_GROUP = 4;
    public static final int WEAPON_GROUP = 8;

    @Override
    public void beginContact(Contact contact) {

        if (isContactOfClass(contact, PlayerInstance.class)) {
            handlePlayerBeginContact(contact);
        }
    }

    @Override
    public void endContact(Contact contact) {

        if (isContactOfClass(contact, PlayerInstance.class)) {
            handlePlayerEndContact(contact);
        }
    }

    /**
     * Handleing player contact start
     *
     * @param contact
     */
    private void handlePlayerBeginContact(Contact contact) {

        if (isContactOfClass(contact, WallArea.class)) {

            WallArea wallArea = ((WallArea) getCorrectFixture(contact, WallArea.class).getBody().getUserData());
            PlayerInstance playerInstance = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());

            if (wallArea.getBody().getPosition().y < playerInstance.getBody().getPosition().y) {
                playerInstance.setCanJump(true);
            }

        }

    }

    /**
     * Handling Player contact end
     *
     * @param contact
     */
    private void handlePlayerEndContact(Contact contact) {

        if (isContactOfClass(contact, WallArea.class)) {

            final WallArea wallArea = ((WallArea) getCorrectFixture(contact, WallArea.class).getBody().getUserData());
            final PlayerInstance playerInstance = ((PlayerInstance) getCorrectFixture(contact, PlayerInstance.class).getBody().getUserData());

            if (wallArea.getBody().getPosition().y < playerInstance.getBody().getPosition().y) {
                // Coyote time for jump
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        playerInstance.setCanJump(false);
                    }
                }, 0.15f);
            }

        }
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    /**
     * Check if this contact is of a particular Instance
     *
     * @param contact
     * @param gameInstanceClass the class of the GameInstance to check
     * @param <T>               the class of the GameInstance to check
     * @return
     */
    private <T> boolean isContactOfClass(Contact contact, Class<T> gameInstanceClass) {
        Objects.requireNonNull(contact.getFixtureA().getBody().getUserData());
        Objects.requireNonNull(contact.getFixtureB().getBody().getUserData());

        return contact.getFixtureA().getBody().getUserData().getClass().equals(gameInstanceClass) ||
            contact.getFixtureB().getBody().getUserData().getClass().equals(gameInstanceClass);
    }

    /**
     * Extract from a contact the fixture of the GameInstance
     *
     * @param contact
     * @param gameInstanceClass the class of the GameInstance fixture to extract
     * @param <T>
     * @return
     */
    private <T> Fixture getCorrectFixture(Contact contact, Class<T> gameInstanceClass) {
        if (contact.getFixtureA().getBody().getUserData().getClass().equals(gameInstanceClass))
            return contact.getFixtureA();

        return contact.getFixtureB();
    }
}
