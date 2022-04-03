package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Enumerations.Color;
import it.polimi.ingsw.Model.Enumerations.TowerColor;

import java.util.ArrayList;
import java.util.Map;

public class Island {

    private Map<Color, Integer> students;
    private TowerColor tower;
    private Island nextIsland;
    private Player owner;

    public Island() {}

    public Map<Color, Integer> getStudents() {
        return students;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void addStudent(Color student) {
        this.students.put(student, 1);
    }

    // TODO setTower and setOwner are inherently connected, if someone can put a tower on a certain island, we are sure he is the owner.
    public void setTower(TowerColor tower) {
        this.tower = tower;
    }

    public void setNextIsland(Island nextIsland) {
        this.nextIsland = nextIsland;
    }
}
