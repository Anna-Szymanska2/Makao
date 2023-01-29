package makao.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import makao.server.Client;
import makao.server.ClientMessage;
import makao.view.Main;

import java.io.IOException;
import java.util.ArrayList;

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
    public void changeScene() throws IOException {
        Stage stage = (Stage) endingPane.getScene().getWindow();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            public void handle(WindowEvent e){
                ClientMessage clientMessage = new ClientMessage(client.getName(),"DISCONNECTED");
                client.sendMessage(clientMessage);
                client.closeEverything(client.getSocket(),client.getIn(),client.getOut());
                try {
                    Platform.exit();
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("choosing_room_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

        fxmlLoader.<ChoosingRoomController>getController().setClient(client);
        client.setChoosingRoomController(fxmlLoader.<ChoosingRoomController>getController());

        stage.show();
    }
}
