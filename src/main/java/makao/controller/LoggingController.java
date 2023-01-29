package makao.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import makao.server.Client;
import makao.server.ClientMessage;
import makao.view.Main;

import java.io.IOException;
import java.net.Socket;

/**
 * LoggingController is a class that handles the logic for the login scene of the application.
 * It allows users to input their username and password, and try to log in to the application.
 * If the login is successful, the user is taken to the room selection scene.
 * If the login fails, an error message is displayed.
 *
 */
public class LoggingController {
    @FXML
    private AnchorPane loggingPane;
    @FXML
    private TextField nickTextField;
    @FXML
    private PasswordField passwordField;
    private Client client;

    /**
     * Changes the scene to the registration scene.
     *
     * @throws IOException
     */
    public void changeViewToRegister() throws IOException {
        clearFields();
        changeScene("register_scene.fxml");

    }

    /**
     * Changes the scene to the specified scene.
     *
     * @param sceneName the name of the FXML file for the desired scene
     * @throws IOException
     */
    public void changeScene(String sceneName) throws IOException {
        Stage stage = (Stage) loggingPane.getScene().getWindow();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            public void handle(WindowEvent e){
                //System.out.print("choosing");
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
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(sceneName));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        if(sceneName.equals("choosing_room_scene.fxml")){
            fxmlLoader.<ChoosingRoomController>getController().setClient(client);
            client.setChoosingRoomController(fxmlLoader.<ChoosingRoomController>getController());
        }
        stage.show();
    }

    /**
     * Attempts to log in to the application using the username and password entered by the user.
     *
     * @throws IOException
     */
    public void tryToLogIn() throws IOException {
        String nick = nickTextField.getText();
        String password = passwordField.getText();
        if(nick.length() <= 3|| password.length() <= 3){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("All fields must be filled in with words with minimum 4 letters!");
            alert.showAndWait();
            clearFields();
            return;
        }
        Socket socket = new Socket("localhost", 4444);
        client = new Client(socket, nick, password);
        client.setLoggingController(this);
        client.listenForMessage();
        ClientMessage clientMessage = new ClientMessage(client.getName(), client.getPassword(), "LOGIN");
        client.sendMessage(clientMessage);

    }

    /**
     * This method is used when login is successful. It clears the fields and changes the scene to "choosing_room_scene.fxml"
     */
    public void loginCorrect() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clearFields();
                try {
                    changeScene("choosing_room_scene.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    /**
     * This method is used when login fails. It shows a warning message "Wrong username or password" and closes the socket.
     */
    public void loginFailed() {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    showMessage("Wrong username or password", Alert.AlertType.WARNING);
                    clearFields();
                    client.getSocket().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    /**
     * Show message method which is used to show the message of specified type.
     * @param message the message to be shown
     * @param type the type of message to be shown.
     */
    public void showMessage(String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void clearFields(){
        nickTextField.clear();
        passwordField.clear();
    }
}
