package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.constants.Color;
import it.polimi.ingsw.constants.GameState;
import it.polimi.ingsw.controller.GameManager;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.AssistantCard;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.islands.SuperIsland;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.GameHandler;
import it.polimi.ingsw.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class MoveMotherNatureTest tests the MoveMotherNature action.
 *
 * @author Alessandro Vacca
 * @see MoveMotherNature
 */
class MoveMotherNatureTest {

    GameManager gameManager;
    Player p1, p2, p3;
    Game game;
    Performable action;
    int movement;
    Color profColor;
    AssistantCard card;


    /**
     * Method init initializes the values needed for the test.
     */
    @BeforeEach
    void init() {
        gameManager = new GameManager(new Game(), new GameHandler(new Server()));
        game = gameManager.getGame();
        game.createPlayer(0, "Ale");
        game.createPlayer(1, "Davide");
        game.createPlayer(2, "Fede");
        p1 = game.getPlayers().get(0);
        p2 = game.getPlayers().get(1);
        p3 = game.getPlayers().get(2);
        gameManager.initGame();
        game.setGameState(GameState.ACTION_MOVE_MOTHER);
        movement = 3;
        game.setRoundOwner(p3);
        profColor = Color.BLUE;
        action = new MoveMotherNature(p3.getNickname(), movement);
    }

    /**
     * Method wrongNickname tests if an action is created with a wrong player nickname.
     */
    @Test
    @DisplayName("Wrong nickname test")
    void wrongNicknameTest() {
        // checking if the nickname doesn't belong to the game
        // Nicolò isn't in the game
        action = new MoveMotherNature("Nicolò", movement);
        assertThrows(InvalidPlayerException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method wrongRoundOwner tests if an action is created with a player which is not the current round owner.
     */
    @Test
    @DisplayName("Wrong roundOwner test")
    void wrongRoundOwnerTest() {
        // checking if the player argument is the actual round owner
        // p2 is the actual round owner, while I try to pass p1
        action = new MoveMotherNature(p1.getNickname(), movement);
        assertThrows(RoundOwnerException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method wrongState tests if an action is created the wrong state set.
     * MoveMotherNature action can only be performed in the MoveMother state.
     */
    @Test
    @DisplayName("Wrong state set test")
    void wrongStateTest() {
        // checks if the game is set to the correct game state
        game.setGameState(GameState.ACTION_MOVE_STUDENTS);
        assertThrows(WrongStateException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method notEnoughMovementTest tests that an action cannot be performed
     * if the movement index selected is not covered by the AssistantCard played.
     */
    @Test
    @DisplayName("playedCard doesn't have enough movement")
    void notEnoughMovementTest() {
        // checks if I have played a card with not enough movement points
        final int cardValue = 1;
        card = p3.getCards().stream().filter(c -> c.getValue() == cardValue).findFirst().get();
        p3.setAndRemovePlayedCard(card);
        assertThrows(GameException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method movementNotZeroTest tests that the action cannot be performed if the selected movement is zero.
     */
    @Test
    @DisplayName("Movement not zero")
    void movementNotZeroTest() {
        // checking that I cannot have 0 movements (or negative)
        movement = 0;
        final int cardValue = 1;
        card = p3.getCards().stream().filter(c -> c.getValue() == cardValue).findFirst().get();
        p3.setAndRemovePlayedCard(card);
        action = new MoveMotherNature(p3.getNickname(), movement);
        assertThrows(InvalidIndexException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method moveMN performs a basic MoveMotherNature action.
     */
    @Test
    @DisplayName("Basic moveMotherNature test")
    void moveMN() {
        // Normal use case
        final int newCardValue = 5;
        card = p3.getCards().stream().filter(c -> c.getValue() == newCardValue).findFirst().get();
        p3.setAndRemovePlayedCard(card);
        assertDoesNotThrow(() -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method singleIslandConquer verifies that after you move motherNature to a certain island
     * and provided you have enough influence, you actually conquer the island.
     */
    @RepeatedTest(30)
    @DisplayName("Single island conquer")
    void singleIslandConquer() {
        // Simple action test
        EnumMap<Color, String> profs = new EnumMap<Color, String>(Color.class);
        profs.put(profColor, game.getRoundOwner().getNickname());
        game.setProfessors(profs);
        // Now Fede controls the BLUE professor
        // Next we will be adding some students to the island we're going to go to
        int nextPosition = game.getIslandContainer().correctIndex(movement, game.getMotherNature().getPosition());
        for (int i = 0; i < 5; i++) {
            game.addIslandStudent(nextPosition, profColor);
        }
        // Then, we play a valid card: 3 movement, we need to play a 5+ value card
        final int cardValue = 5;
        AssistantCard card = p3.getCards().stream().filter(c -> c.getValue() == cardValue).findFirst().get();
        p3.setAndRemovePlayedCard(card);
        action = new MoveMotherNature(game.getRoundOwner().getNickname(), movement);
        try {
            action.performMove(game, gameManager.getRules());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Checking if motherNature has in fact moved
        assertEquals(nextPosition, game.getMotherNature().getPosition());
        // Now, we check if Fede now controls the island
        int motherNaturePosition = game.getMotherNature().getPosition();
        assertEquals(game.getIslandContainer().get(motherNaturePosition).getOwner(), p3.getNickname());
    }


    /**
     * Method previousSuperIsland creates the superIsland by first conquering a certain island,
     * then it moves MN by a single position and conquers the island next to the previously conquered one,
     * thus joining the two islands in a superIsland.
     */
    @RepeatedTest(10)
    @DisplayName("Previous SuperIsland creation")
    void previousSuperIsland() {
        // Simple action test
        EnumMap<Color, String> map = new EnumMap<>(Color.class);
        map.put(profColor, game.getRoundOwner().getNickname());
        game.setProfessors(map);
        // Now Fede controls the BLUE professor
        // Next we will be adding some students to the island we're going to go to
        int nextPosition = game.getIslandContainer().correctIndex(movement, game.getMotherNature().getPosition());
        for (int i = 0; i < 5; i++) {
            game.addIslandStudent(nextPosition, profColor);
        }
        // Then, we play a valid card: 3 movement, we need to play a 5+ value card
        final int cardValue = 5;
        AssistantCard card = p3.getCards().stream().filter(c -> c.getValue() == cardValue).findFirst().get();
        p3.setAndRemovePlayedCard(card);
        // Now we move MN
        action = new MoveMotherNature(game.getRoundOwner().getNickname(), movement);
        try {
            action.performMove(game, gameManager.getRules());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Checking if motherNature has in fact moved
        assertEquals(nextPosition, game.getMotherNature().getPosition());
        // Now, we check if Fede now controls the island
        int motherNaturePosition = game.getMotherNature().getPosition();
        assertEquals(game.getIslandContainer().get(motherNaturePosition).getOwner(), p3.getNickname());
        // And then we check if the game has correctly changed state
        game.setGameState(GameState.ACTION_MOVE_MOTHER);

        // We repeat this for the following one
        movement = 1;
        nextPosition = game.getIslandContainer().correctIndex(movement, game.getMotherNature().getPosition());
        for (int i = 0; i < 5; i++) {
            game.addIslandStudent(nextPosition, profColor);
        }
        int oldIslands = game.getIslandContainer().size();
        int next = game.getIslandContainer().correctIndex(2, game.getMotherNature().getPosition());
        game.setIslandOwner(next, p3.getNickname());
        action = new MoveMotherNature(p3.getNickname(), movement);
        try {
            action.performMove(game, gameManager.getRules());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(game.getIslandContainer().get(game.getMotherNature().getPosition()) instanceof SuperIsland);
        assertEquals(game.getIslandContainer().size(), oldIslands - 2);
    }

    /**
     * Method nextSuperIsland creates the superIsland by first conquering a certain island,
     * then it moves MN by 11 positions (the full game board) and conquers the island
     * before the previously conquered one, thus joining the two islands in a superIsland.
     */
    @RepeatedTest(10)
    @DisplayName("Next SuperIsland creation")
    void nextSuperIsland() {
        // Simple action test
        EnumMap<Color, String> map = new EnumMap<>(Color.class);
        map.put(profColor, game.getRoundOwner().getNickname());
        game.setProfessors(map);        // Now Fede controls the BLUE professor
        // Next we will be adding some students to the island we're going to go to
        int nextPosition = game.getIslandContainer().correctIndex(movement, game.getMotherNature().getPosition());
        for (int i = 0; i < 5; i++) {
            game.addIslandStudent(nextPosition, profColor);
        }
        // Then, we play a valid card: 3 movement, we need to play a 5+ value card
        final int cardValue = 10;
        AssistantCard card = p3.getCards().stream().filter(c -> c.getValue() == cardValue).findFirst().get();
        p3.setAndRemovePlayedCard(card);
        // Now we move MN
        action = new MoveMotherNature(game.getRoundOwner().getNickname(), movement);
        try {
            action.performMove(game, gameManager.getRules());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Checking if motherNature has in fact moved
        assertEquals(nextPosition, game.getMotherNature().getPosition());
        // Now, we check if Fede now controls the island
        int motherNaturePosition = game.getMotherNature().getPosition();
        assertEquals(game.getIslandContainer().get(motherNaturePosition).getOwner(), p3.getNickname());

        // We repeat this for the following one
        movement = 11;
        nextPosition = game.getIslandContainer().correctIndex(movement, game.getMotherNature().getPosition());

        for (int i = 0; i < 5; i++) {
            game.addIslandStudent(nextPosition, profColor);
        }

        int oldIslands = game.getIslandContainer().size();
        action = new MoveMotherNature(p3.getNickname(), movement);
        try {
            action = new MoveMotherNature(p3.getNickname(), 2);
            action.performMove(game, gameManager.getRules());
            action = new MoveMotherNature(p3.getNickname(), movement / 2 - 1);
            action.performMove(game, gameManager.getRules());
            action = new MoveMotherNature(p3.getNickname(), movement - movement / 2 - 1);
            action.performMove(game, gameManager.getRules());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertTrue(game.getIslandContainer().get(game.getMotherNature().getPosition()) instanceof SuperIsland);
        assertEquals(game.getIslandContainer().size(), oldIslands - 1);
    }
}