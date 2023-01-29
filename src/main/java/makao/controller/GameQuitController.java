package makao.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import makao.server.Client;
import makao.server.ClientMessage;
import makao.view.Main;

import java.io.IOException;

public class GameQuitController {

    @FXML
    private Button goBackButton;

    @FXML
    private AnchorPane quitPane;
    private Client client;

    public void changeScene() throws IOException {
        Stage stage = (Stage) quitPane.getScene().getWindow();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            public void handle(WindowEvent e){
                System.out.print("choosing");
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

    public void setClient(Client client) {
        this.client = client;
    }
}
