package it.polimi.ingsw.listeners;

import it.polimi.ingsw.model.Cloud;
import it.polimi.ingsw.server.answers.model.CloudsMessage;
import it.polimi.ingsw.server.VirtualClient;

import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 * CloudsListener class is a AbsListener used for notifying the client after a clouds update.
 *
 * @see AbsListener
 */
public class CloudsListener extends AbsListener {

    /**
     * Constructor CloudsListener creates a new CloudsListener instance.
     *
     * @param client       the virtual client's view on Server.
     * @param propertyName the type of the listener to be set.
     */
    public CloudsListener(VirtualClient client, String propertyName) {
        super(client, propertyName);
    }

    /**
     * Method propertyChange notifies the client with a CloudsMessage.
     *
     * @param evt of type PropertyChangeEvent - the event received.
     * @see AbsListener#propertyChange(PropertyChangeEvent)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CloudsMessage message = new CloudsMessage((List<Cloud>) evt.getNewValue());
        virtualClient.send(message);
    }
}