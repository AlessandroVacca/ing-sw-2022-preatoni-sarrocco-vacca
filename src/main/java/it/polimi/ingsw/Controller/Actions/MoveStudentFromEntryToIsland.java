package it.polimi.ingsw.Controller.Actions;

import it.polimi.ingsw.Controller.Rules;
import it.polimi.ingsw.Model.Enumerations.Color;
import it.polimi.ingsw.Model.Enumerations.GameState;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Model.Player;

import java.util.Optional;

public class MoveStudentFromEntryToIsland extends MoveStudentFromEntry {

    private int islandIndex;

    public MoveStudentFromEntryToIsland(String player, Color color, int islandIndex) {
        super(player, color);
        this.islandIndex = islandIndex;
    }

    @Override
    public void performMove(Game game) {
        Optional<Player> player_opt = game.getPlayerByNickname(myNickName);
        if (player_opt.isEmpty())    // if there is no Player with that nick
            return;
        Player player = player_opt.get();

        player.getSchool().removeStudentFromEntry(color);
        game.getIslands().get(islandIndex).addStudent(color);

        if(Rules.getEntrySize(game.numPlayers()) - player.getSchool().getStudentsEntry().size() >= Rules.getStudentsPerTurn(game.numPlayers())){
            game.setGameState(GameState.ACTION_MOVE_MOTHER);
        }
    }

    @Override
    public boolean canPerformExt(Game game) {
        super.canPerformExt(game);
        boolean isPossible = false;
        if(islandIndex > 0 && islandIndex < game.getIslands().size()) {
            isPossible = true;
        }
        return isPossible;
    }
}
