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

/**
 * GameEndingController class handles the game ending scene. It displays the winner, the ranking of the players,
 * and allows the user to go back to the main menu.
 *
 */
public class GameEndingController {
    @FXML
    private Label winnerLabel;
    @FXML
    private ListView<String> rankingList;
    @FXML
    private AnchorPane endingPane;
    private Client client;

    /**
     * Sets the winner label with the name of the winner.
     *
     * @param winner name of the winner
     */
    public void setWinnerLabel(String winner){
        if(client.getName().equals(winner))
            winnerLabel.setText("You won!");
        else
            winnerLabel.setText(winner + " won");
    }

    /**
     * Adds the ranking of the players to the ranking list.
     *
     * @param ranking list of players' names in order of their ranking
     */
    public void addRanking(ArrayList<String> ranking){
        rankingList.getItems().addAll(ranking);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Changes the scene to the main menu. Closes the connection to the server and exits the application when the window is closed.
     *
     * @throws IOException
     */
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
