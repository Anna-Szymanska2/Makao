package makao.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import makao.server.Client;

public class RoomController {

    @FXML
    private Label codeLabel;

    private Client client;

    public void startGame(){

    }
    public void setCodeOnLabel(int code){
        codeLabel.setText(String.valueOf(code));
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
