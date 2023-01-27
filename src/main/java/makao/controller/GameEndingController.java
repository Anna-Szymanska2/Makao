package makao.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import makao.server.Client;

public class GameEndingController {

    @FXML
    private Label winnerLabel;

    @FXML
    private ListView<String> rankingList;

    @FXML
    private AnchorPane endingPane;
    private Client client;

    public void setWinnerLabel(String winner){
        if(client.getName().equals(winner))
            winnerLabel.setText("You are the winner!");
        else
            winnerLabel.setText(winner + " is the winner");
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
