package it.polimi.ingsw.Client.gui.controllers;

import it.polimi.ingsw.Client.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;


public class GameModeController extends GUIController{

    private GUI gui;
    private final static String BOARD ="/fxml/board.fxml";

    @FXML
    RadioButton NormalGame, ExpertGame;
    @FXML
    Button btn2,btn3;
    @FXML
    ToggleGroup gameModeToggle;
    @FXML
    Label title,error;




    @FXML
    public void check(MouseEvent event) {
        Button btn =(Button) event.getSource();
        String id = btn.getId();
        Boolean expert = false;

        if(ExpertGame.isPressed()){
            expert = true;
        }
        if(NormalGame.isSelected() || ExpertGame.isSelected()){
           // gui.getModelView().setExpert(expert);
           gui.changeScene(BOARD);
        }
        else{
            error.setText("choose the mode you want to play before starting");
            sleepAndExec(()->error.setText(""));
        }
    }

    @Override
    public void setGui(GUI gui) {
        this.gui = gui;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btn2.setFont(font);
        btn3.setFont(font);
        error.setFont(font);
        title.setFont(font);


    }

}