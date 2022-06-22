package it.polimi.ingsw.Model.Cards.CharacterCards;

import it.polimi.ingsw.Constants.Character;
import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Constants.GameState;
import it.polimi.ingsw.Model.Game;

/**
 * Grandma class is model representation of the Grandma character card.
 */
public class Grandma extends CharacterCard {

    private int blockingCards;
    private GameState previousState;

    /**
     * Constructor Grandma sets the correct Character enum type and the correct price to the card.
     */
    public Grandma(String imagePath) {
        super(imagePath);
        price = 2;
        blockingCards = 4;
        character = Character.GRANDMA;
    }

    /**
     * Method activate extends the default activate behaviour of the CharacterCard abstract with the Grandma logic.
     *
     * @param rules the current rules of the game.
     * @param game  the reference to the current game.
     */
    @Override
    public void activate(Rules rules, Game game) {
        super.activate(rules, game);
        previousState = game.getGameState();
        game.setGameState(GameState.GRANDMA_BLOCK_ISLAND);
    }

    /**
     * Method deactivate extends the default deactivate behaviour of the CharacterCard abstract with the Grandma logic.
     *
     * @param rules the current rules of the game.
     * @param game  the reference to the current game.
     */
    @Override
    public void deactivate(Rules rules, Game game) {
        super.deactivate(rules, game);
        game.setGameState(previousState);
    }

    /**
     * Method moveBlockingCard removes a blocking card from the Grandma card.
     */
    public void moveBlockingCard() {
        if (blockingCards > 0) {
            blockingCards--;
        }
    }

    /**
     * Method addBlockingCard adds a blocking card to the Grandma card.
     */
    public void addBlockingCard() {
        if (blockingCards <= 4) {
            blockingCards++;
        }
    }

    /**
     * Method getBlockingCards returns the amount of blockingCards present on the Grandma card.
     */
    @Override
    public int getBlockingCards() {
        return blockingCards;
    }
}