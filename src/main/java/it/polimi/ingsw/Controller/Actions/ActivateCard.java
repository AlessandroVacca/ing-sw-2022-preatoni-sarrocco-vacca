package it.polimi.ingsw.Controller.Actions;

import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Model.Cards.CharacterCards.CharacterCard;
import it.polimi.ingsw.Model.Enumerations.GameState;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Model.Player;

import java.util.Optional;

public class ActivateCard extends Performable {

    private final int choice;

    public ActivateCard(String myNickname, int choice) {
        super(myNickname);
        this.choice = choice;
    }

    @Override
    public boolean canPerformExt(Game game, Rules rules) {
        // Simple check that verifies that there is a player with the specified name, and that he/she is the roundOwner
        if (!super.canPerformExt(game, rules)) {
            return false;
        }

        Player player = getPlayer(game);
        // Simple check to verify that we're in the correct state
        if (!game.getGameState().equals(GameState.ACTION_MOVE_MOTHER) && !game.getGameState().equals(GameState.ACTION_MOVE_STUDENTS)) {
            return false;
        }

        // Verify that we are under expert rules
        if (!game.isExpertMode()) {
            return false;
        }

        if (choice < 0 || choice > Rules.numCharacterCards) {
            return false;
        }

        CharacterCard choiceCard = game.getCharacterCards().get(choice);
        // Verify that the player has enough money
        if (player.getBalance() < choiceCard.getPrice()) {
            return false;
        }
        if (choiceCard.isActive()) {
            return false;
        }
        return true;
    }

    @Override
    public void performMove(Game game, Rules rules) {
        Optional<Player> player_opt = game.getPlayerByNickname(myNickName);
        if (player_opt.isEmpty())    // if there is no Player with that nick
            return;
        Player player = player_opt.get();

        CharacterCard choiceCard = game.getCharacterCards().get(choice);
        choiceCard.activate(rules, game);

        game.incrementBalance(choiceCard.getPrice());
        player.spendCoins(choiceCard.getPrice());

    }

}
