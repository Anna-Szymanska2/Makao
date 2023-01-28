package makao.controller;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import makao.server.Client;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameEndingController {

    @FXML
    private Label winnerLabel;

    @FXML
    private ListView<String> rankingList;

    @FXML
    private Button goBackButton;

    @FXML
    private AnchorPane endingPane;
    private Client client;

    public void setWinnerLabel(String winner){
        if(client.getName().equals(winner))
            winnerLabel.setText("You won!");
        else
            winnerLabel.setText(winner + " won");
    }

    public void addRanking(ArrayList<String> ranking){
        rankingList.getItems().addAll(ranking);
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
