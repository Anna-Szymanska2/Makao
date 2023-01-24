package makao.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import makao.server.Client;

public class RoomController {
    @FXML
    private Button startButton;
    @FXML
    private Label codeLabel;
    @FXML
    private VBox avatarsVBox;
    @FXML
    private VBox namesVBox;
    private Client client;

    public void startGame(){

    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Button getStartButton() {
        return startButton;
    }
}
