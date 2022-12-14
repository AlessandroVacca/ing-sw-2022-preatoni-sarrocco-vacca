package it.polimi.ingsw.listeners;

import it.polimi.ingsw.constants.GameState;
import it.polimi.ingsw.server.answers.model.GameStateMessage;
import it.polimi.ingsw.server.VirtualClient;

import java.beans.PropertyChangeEvent;

/**
 * GameStateListener class is a AbsListener used for notifying the client after game state change.
 *
 * @see AbsListener
 */
public class GameStateListener extends AbsListener {

    /**
     * Constructor GameStateListener creates a new GameStateListener instance.
     *
     * @param client       the virtual client's view on Server.
     * @param propertyName the type of the listener to be set.
     */
    public GameStateListener(VirtualClient client, String propertyName) {
        super(client, propertyName);
    }

    /**
     * Method propertyChange notifies the client with a GameStateMessage.
     *
     * @param evt of type PropertyChangeEvent - the event received.
     * @see AbsListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        GameStateMessage message = new GameStateMessage((GameState) evt.getNewValue());
        virtualClient.send(message);
    }
}