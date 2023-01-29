package makao.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import makao.server.Client;
import makao.server.ClientMessage;
import makao.view.Main;

import java.io.IOException;

/**
 * GameEndingController class handles the game quited scene. It displays information that one of the players has left the game
 * and allows the user to go back to the main menu.
 *
 */
public class GameQuitController {
    @FXML
    private AnchorPane quitPane;
    private Client client;

    /**
     * Changes the scene to the main menu. Closes the connection to the server and exits the application when the window is closed.
     *
     * @throws IOException
     */
    public void changeScene() throws IOException {
        Stage stage = (Stage) quitPane.getScene().getWindow();
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

    public void setClient(Client client) {
        this.client = client;
    }
}
