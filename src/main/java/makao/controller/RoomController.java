package makao.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import makao.server.Client;
import makao.server.ClientMessage;
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
                    GameController gameController = fxmlLoader.<GameController>getController();
                    client.setGameController(gameController);
                    gameController.setClient(client);
                    gameController.init(msgFromServer, name);
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>()
                    {
                        public void handle(WindowEvent e){
                            //System.out.print("game");
                            ClientMessage clientMessage = new ClientMessage(client.getName(),"DISCONNECTED");
                            client.sendMessage(clientMessage);
                            client.closeEverything(client.getSocket(),client.getIn(),client.getOut());
                            client.getGameController().endOfThisPlayerRound();
                            try {
                                Platform.exit();
                            }
                            catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });

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
