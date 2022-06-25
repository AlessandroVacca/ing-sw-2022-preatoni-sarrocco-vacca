package it.polimi.ingsw.Server.answers.model;

import it.polimi.ingsw.Constants.Color;

import java.util.EnumMap;

/**
 * ProfsMessage class is a ModelMessage used for sending updates of the game's professors.
 *
 * @author Alessandro Vacca
 * @see ModelMessage
 */
public class ProfsMessage implements ModelMessage {

    private final EnumMap<Color, String> message;

    /**
     * Constructor ProfsMessage creates a new ProfsMessage instance.
     *
     * @param profs the ownership map of professors.
     */
    public ProfsMessage(EnumMap<Color, String> profs) {
        this.message = profs;
    }

    /**
     * Method getMessage returns the message of this Answer object.
     *
     * @return the message (type Object) of this Answer object.
     * @see ModelMessage#getMessage()
     */
    @Override
    public EnumMap<Color, String> getMessage() {
        return message;
    }
}
