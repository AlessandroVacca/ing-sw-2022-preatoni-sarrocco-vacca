package it.polimi.ingsw.Model.Cards.CharacterCards;

import it.polimi.ingsw.Controller.Rules.DynamicRules.BaseRules;
import it.polimi.ingsw.Controller.Rules.DynamicRules.MushroomRules;
import it.polimi.ingsw.Controller.Rules.Rules;
import it.polimi.ingsw.Constants.Color;
import it.polimi.ingsw.Constants.GameState;
import it.polimi.ingsw.Model.Game;


public class MushroomCharacter extends CharacterCard {
    Color student;
    private GameState previousState;

    public MushroomCharacter(String imagePath) {
        super(imagePath);
        price = 3;
    }

    @Override
    public void activate(Rules rules, Game game) {
        super.activate(rules, game);
        previousState = game.getGameState();
        game.setGameState(GameState.MUSHROOM_CHOOSE_COLOR);
        rules.setDynamicRules(new MushroomRules());
    }

    @Override
    public void deactivate(Rules rules, Game game) {
        super.deactivate(rules, game);
        game.setGameState(previousState);
        rules.setDynamicRules(new BaseRules());
        setStudent(null);
    }

    public Color getStudent() {
        return student;
    }

    public void setStudent(Color student) {
        this.student = student;
    }

    public GameState getPreviousState() {
        return previousState;
    }
}
