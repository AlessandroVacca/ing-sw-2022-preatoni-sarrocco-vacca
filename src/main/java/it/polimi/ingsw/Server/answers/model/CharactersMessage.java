package it.polimi.ingsw.Server.answers.model;

import it.polimi.ingsw.Model.Cards.CharacterCards.CharacterCard;
import it.polimi.ingsw.Model.Cards.CharacterCards.ReducedCharacterCard;

import java.util.List;

/**
 * CharactersMessage class is type of ModelMessage used for sending updates of game's character cards.
 *
 * @author Federico Sarrocco, Alessandro Vacca
 * @see ModelMessage
 */
public class CharactersMessage implements ModelMessage {
    private final List<ReducedCharacterCard> message;

    /**
     * Constructor CharactersMessage creates a new CharactersMessage instance.
     * This constructor converts the CharacterCard Object to the ReducedCharacterCard client one.
     *
     * @param characterCards the list of cards to be sent to the client.
     */
    public CharactersMessage(List<CharacterCard> characterCards) {
        this.message = characterCards.stream().map(ReducedCharacterCard::new
        ).toList();
    }

    /**
     * Method getMessage returns the message of this Answer object.
     *
     * @return the message (type Object) of this Answer object.
     * @see ModelMessage#getMessage()
     */
    @Override
    public List<ReducedCharacterCard> getMessage() {
        return message;
    }
}