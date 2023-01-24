package makao.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import makao.server.Client;

public class RoomController {

    @FXML
    private Label waitingLabel;

    private Client client;

    public void startGame(){

    }

    public void setClient(Client client) {
        this.client = client;
    }

}
