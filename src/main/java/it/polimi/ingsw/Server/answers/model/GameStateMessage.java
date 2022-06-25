package it.polimi.ingsw.Server.answers.model;

import it.polimi.ingsw.Constants.GameState;

/**
 * GameStateMessage class is type of ModelMessage used for sending updates of game's state.
 *
 * @author Federico Sarrocco
 * @see ModelMessage
 */
public class GameStateMessage implements ModelMessage {

    private final GameState message;

    /**
     * Constructor GameStateMessage creates a new GameStateMessage instance.
     *
     * @param message the new game state.
     */
    public GameStateMessage(GameState message) {
        this.message = message;
    }

    /**
     * Method getMessage returns the message of this Answer object.
     *
     * @return the message (type Object) of this Answer object.
     * @see ModelMessage#getMessage()
     */
    @Override
    public GameState getMessage() {
        return message;
    }
}
