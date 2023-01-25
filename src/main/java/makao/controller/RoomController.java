package makao.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import makao.server.Client;
import makao.server.ServerMessage;
import makao.view.Main;

import java.io.IOException;

public class RoomController {

    @FXML
    private Label codeLabel;

    @FXML
    private AnchorPane waitingPane;

    private Client client;

    public void startGame(){

    }
    public void setCodeOnLabel(int code){
        codeLabel.setText(String.valueOf(code));
    }

    public void changeScene(ServerMessage msgFromServer, String name){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage stage = (Stage) waitingPane.getScene().getWindow();
                    stage.close();
                    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("game_scene.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.setScene(scene);
                    HelloController gameController = fxmlLoader.<HelloController>getController();
                    client.setGameController(gameController);
                    gameController.setClient(client);
                    gameController.init(msgFromServer, name);
                    stage.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
