package it.polimi.ingsw.controller.actions;

import it.polimi.ingsw.controller.GameManager;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.characters.CharacterCard;
import it.polimi.ingsw.constants.GameState;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.GameHandler;
import it.polimi.ingsw.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ActivateCardTest tests the ActivateCard action.
 *
 * @see ActivateCard
 */
class ActivateCardTest {

    GameManager gameManager;
    Player p1, p2, p3;
    Game game;
    Performable action;
    int selection;

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
        game.setExpertMode(true);
        gameManager.initGame();
        game.setRoundOwner(p3);
        game.setGameState(GameState.ACTION_MOVE_MOTHER);
        selection = 1;
        action = new ActivateCard(p3.getNickname(), selection);
    }

    /**
     * Method wrongNickname tests if an action is created with a wrong player nickname.
     */
    @DisplayName("Wrong nickname test")
    @Test
    void wrongNickname() {
        String wrongNickname = "Wrong";
        action = new ActivateCard(wrongNickname, selection);
        assertThrows(InvalidPlayerException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method wrongRoundOwner tests if an action is created with a player which is not the current round owner.
     */
    @DisplayName("Wrong roundOwner test")
    @Test
    void wrongRoundOwner() {
        action = new ActivateCard(p1.getNickname(), selection);
        assertThrows(RoundOwnerException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method wrongState tests if an action is created the wrong state set.
     */
    @DisplayName("Wrong state test")
    @Test
    void wrongState() {
        game.setGameState(GameState.PLANNING_CHOOSE_CARD);
        assertThrows(WrongStateException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method normalMode tests if an action is created the wrong game mode set.
     */
    @DisplayName("Wrong game mode test")
    @Test
    void normalMode() {
        game.setExpertMode(false);
        assertThrows(GameException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method invalidSelection tests if an action is created with an invalid index.
     */
    @DisplayName("Wrong card selection test")
    @Test
    void invalidSelection() {
        selection = -1;
        action = new ActivateCard(p3.getNickname(), selection);
        assertThrows(InvalidIndexException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
        selection = 10;
        action = new ActivateCard(p3.getNickname(), selection);
        assertThrows(InvalidIndexException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method noMoney tests if a player has not enough money to perform an ActivateCard action on a certain CharacterCard.
     */
    @DisplayName("Not enough money test")
    @Test
    void noMoney() {
        p3.spendCoins(p3.getBalance());
        assertThrows(GameException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method alreadyActive tests if a player tries to activate a card which has been already activated.
     */
    @DisplayName("Card already active test")
    @Test
    void alreadyActive() {
        CharacterCard card = game.getCharacterCards().get(selection);
        for (int i = 0; i < card.getPrice() + 1; i++) {
            game.incrementPlayerBalance(p3.getNickname());
        }
        try {
            action.performMove(game, gameManager.getRules());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        for (int i = 0; i < card.getPrice() + 1; i++) {
            game.incrementPlayerBalance(p3.getNickname());
        }
        game.setGameState(GameState.ACTION_MOVE_MOTHER);
        assertThrows(GameException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method cardPerTurn tests that only a card per turn is to be activated.
     */
    @DisplayName("Max one card per turn test")
    @Test
    void cardPerTurn() {
        CharacterCard card = game.getCharacterCards().get(selection);
        for (int i = 0; i < card.getPrice() + 1; i++) {
            game.incrementPlayerBalance(p3.getNickname());
        }
        try {
            action.performMove(game, gameManager.getRules());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        for (int i = 0; i < card.getPrice() + 1; i++) {
            game.incrementPlayerBalance(p3.getNickname());
        }
        card.deactivate(gameManager.getRules(), game);
        game.setGameState(GameState.ACTION_MOVE_MOTHER);
        assertThrows(GameException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }

    /**
     * Method activateCard tests the activation of a card.
     */
    @DisplayName("Card activation test")
    void activateCard() {
        CharacterCard card = game.getCharacterCards().get(selection);
        for (int i = 0; i < card.getPrice() + 1; i++) {
            game.incrementPlayerBalance(p3.getNickname());
        }
        int previousPlayerBalance = p3.getBalance();
        int previousGameBalance = game.getBalance();
        int previousCardPrice = card.getPrice();
        // This is due to cards costing more than the initial balance
        try {
            action.performMove(game, gameManager.getRules());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals(previousPlayerBalance - previousCardPrice, p3.getBalance(), "money are removed correctly");
        assertEquals(previousGameBalance + previousCardPrice - 1, game.getBalance(), "first activation of the card, game get back price - 1");
        assertEquals(previousCardPrice + 1, card.getPrice(), "cost increment on card is right");
        card.deactivate(gameManager.getRules(), game);
        // Give some money to P3, in order to try to activate a card again
        for (int i = 0; i < card.getPrice() + 1; i++) {
            game.incrementPlayerBalance(p3.getNickname());
        }
        // Check that a card can be activated once per turn
        assertThrows(GameException.class, () -> {
            action.performMove(game, gameManager.getRules());
        });
    }
}