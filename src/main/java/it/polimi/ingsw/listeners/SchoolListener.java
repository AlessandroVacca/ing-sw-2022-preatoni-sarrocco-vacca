package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.School;
import it.polimi.ingsw.server.answers.model.SchoolMessage;
import it.polimi.ingsw.server.VirtualClient;

import java.beans.PropertyChangeEvent;

/**
 * SchoolListener class is a AbsListener used for notifying the client after a player's school update.
 *
 * @see AbsListener
 */
public class SchoolListener extends AbsListener {

    /**
     * Constructor SchoolListener creates a new SchoolListener instance.
     *
     * @param client       the virtual client on the Server.
     * @param propertyName the type of the listener to be set.
     */
    public SchoolListener(VirtualClient client, String propertyName) {
        super(client, propertyName);
    }

    /**
     * Method propertyChange notifies the client with a SchoolMessage.
     *
     * @param evt of type PropertyChangeEvent - the event received.
     * @see AbsListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SchoolMessage message = new SchoolMessage(virtualClient.getNickname(), (School) evt.getNewValue());
        virtualClient.sendAll(message);
    }
}