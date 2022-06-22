package it.polimi.ingsw.Client.gui.controllers;

import it.polimi.ingsw.Client.ServerMessageHandler;
import it.polimi.ingsw.Client.gui.GUI;
import it.polimi.ingsw.Constants.GameState;
import it.polimi.ingsw.Server.Answer.CustomMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.beans.PropertyChangeEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class LoaderController extends GUIController {
    @FXML
    Label status;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        status.setFont(font);
    }

    public void setText(String status) {
        this.status.setText(status);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ServerMessageHandler.CUSTOM_MESSAGE_LISTENER -> handleCustomMessages(((CustomMessage) evt.getNewValue()).getMessage());
        }
    }

    private void handleCustomMessages(String msg) {
        boolean roundOwner = gui.getModelView().amIRoundOwner();
        if (gui.getModelView().getGameState() == GameState.GAME_ROOM && !roundOwner) {
            this.status.setText(msg);
        }
        if (gui.getModelView().getGameState() == GameState.SETUP_CHOOSE_MAGICIAN &&  !roundOwner) {
            this.status.setText(msg);
        }
    }
}
