package it.polimi.ingsw.controller.actions.characters;

import it.polimi.ingsw.controller.actions.Performable;
import it.polimi.ingsw.controller.rules.Rules;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.cards.characters.CharacterCard;
import it.polimi.ingsw.model.cards.characters.Herald;
import it.polimi.ingsw.constants.GameState;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.islands.Island;
import it.polimi.ingsw.model.islands.IslandContainer;
import it.polimi.ingsw.model.Player;

import java.util.Optional;

/**
 * HeraldChooseIsland class represent the Herald character card game action.
 * The action allows a player to calculate the new influence on a chosen island.
 *
 * @see Herald
 */
public class HeraldChooseIsland extends Performable {

    private int islandIndex;

    /**
     * Constructor HeraldChooseIsland creates the HeraldChooseIsland instance, and sets the island selection by index.
     *
     * @param player      the nickname of the action owner.
     * @param islandIndex the index of the island selection.
     */
    public HeraldChooseIsland(String player, int islandIndex) {
        super(player);
        this.islandIndex = islandIndex;
    }

    /**
     * Method canPerform extends the Performable definition with the HeraldChooseIsland specific checks.
     *
     * @param game  represents the game Model.
     * @param rules represents the current game rules.
     * @throws InvalidPlayerException if the player is not in the current game.
     * @throws RoundOwnerException    if the player is not the current round owner.
     * @throws GameException          for generic errors.
     * @see Performable#canPerform(Game, Rules)
     */
    @Override
    protected void canPerform(Game game, Rules rules) throws InvalidPlayerException, RoundOwnerException, GameException {
        // Simple check that verifies that there is a player with the specified name, and that he/she is the roundOwner
        super.canPerform(game, rules);

        if (!game.getGameState().equals(GameState.HERALD_ACTIVE)) {
            throw new WrongStateException("state you access by activating the herald card.");
        }

        // There is no an active card check
        Optional<CharacterCard> card = game.getActiveCharacter(Herald.class);
        if (card.isEmpty()) {
            throw new GameException("There isn't any active card present.");
        }

        // The active card is not of the right type
        if (!(card.get() instanceof Herald)) {
            throw new GameException("The card that has been activated in this turn is not of the herald type.");
        }

        if (!game.getIslandContainer().isFeasibleIndex(islandIndex)) {
            throw new InvalidIndexException("island", 0, game.getIslandContainer().size() - 1, islandIndex);
        }
    }

    /**
     * Method performMove checks if an action is performable,
     * and only if successful it executes the action on the Game Model.
     * The herald card effect will be activated,
     * and on the selected island the new influence and owner will be calculated.
     * If necessary, the method will handle island merging.
     *
     * @param game  the current game model reference.
     * @param rules the current game rules.
     */
    @Override
    public void performMove(Game game, Rules rules) throws InvalidPlayerException, RoundOwnerException, GameException {
        canPerform(game, rules);
        Island island = game.getIslandContainer().get(islandIndex);
        // Set the new owner (if present)
        Optional<String> islandNewOwner_opt = rules.getDynamicRules().computeIslandInfluence(game, island);
        if (islandNewOwner_opt.isPresent()) {
            String islandPrevOwner = island.getOwner();
            if (!islandNewOwner_opt.get().equals(islandPrevOwner)) {
                game.setIslandOwner(islandIndex, islandNewOwner_opt.get());
                // remove tower to the player
                Optional<Player> islandOwnerPlayer_opt = game.getPlayerByNickname(islandNewOwner_opt.get());
                islandOwnerPlayer_opt.ifPresent(owner -> owner.getSchool().decreaseTowers());
                // give back the tower to the previous owner
                Optional<Player> islandPrevPlayer_opt = game.getPlayerByNickname(islandPrevOwner);
                islandPrevPlayer_opt.ifPresent(owner -> owner.getSchool().increaseTowers());
            }
        }
        // SuperIsland creation
        IslandContainer islandContainer = game.getIslandContainer();
        Island prevIsland = islandContainer.prevIsland(islandIndex);
        if (Island.checkJoin(prevIsland, island)) {
            islandContainer.joinPrevIsland(islandIndex);
            game.moveMotherNature(-1);
        }
        Island nextIsland = islandContainer.nextIsland(islandIndex);
        if (Island.checkJoin(island, nextIsland)) {
            islandContainer.joinNextIsland(islandIndex);
        }
    }
}