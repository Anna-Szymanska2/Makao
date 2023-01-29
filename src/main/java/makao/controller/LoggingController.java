package makao.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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

public class LoggingController {
    @FXML
    private Button registerButton;

    @FXML
    private AnchorPane loggingPane;

    @FXML
    private Button logInButton;

    @FXML
    private TextField nickTextField;

    @FXML
    private PasswordField passwordField;
    private Client client;

    public void changeViewToRegister() throws IOException {
        clearFields();
        changeScene("register_scene.fxml");

        /*RegisterController controller = new RegisterController();
        Stage newStage = (Stage) scene.getWindow();
        newStage.show();*/

    }

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
