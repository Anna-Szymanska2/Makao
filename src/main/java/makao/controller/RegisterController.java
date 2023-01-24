package makao.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import makao.server.Client;
import makao.server.ClientMessage;
import makao.view.Main;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;



public class RegisterController implements Initializable {
    @FXML
    transient private Button signUpButton;
    @FXML
    private TextField nickTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField secondPasswordField;
    @FXML
    private Button backToSignInButton;
    @FXML
    private ImageView chosenAvatarView;
    @FXML
    private HBox firstHBox;
    @FXML
    private HBox secondHBox;
    @FXML
    private HBox thirdHBox;
    @FXML
    private HBox fourthHBox;
    @FXML
    private AnchorPane registerPane;

    private final int numberOfAvatarsInRow = 5;
    private Client client;

    public AnchorPane getRegisterPane() {
        return registerPane;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initHBox(0, firstHBox);
        initHBox(5, secondHBox);
        initHBox(10, thirdHBox);
        initHBox(15, fourthHBox);

    }
    public void initHBox(int startNumber, HBox hBox){
        int imageIndex = startNumber + 1;
        for(int i = 0; i < numberOfAvatarsInRow; i++) {
            ImageView view = (ImageView) hBox.getChildren().get(i);
            Image image = new Image(getClass().getResource("avatars/"+ imageIndex +".jpg").toExternalForm());
            imageIndex++;
            view.setImage(image);
            view.setOnMouseClicked((mouseEvent) -> {
                chooseAvatar(view);
            });
        }

    }

    public void chooseAvatar(ImageView view){
        chosenAvatarView.setImage(view.getImage());

    }
    public void tryToSignUp() throws IOException {
        String nick = nickTextField.getText();
        String password = passwordField.getText();
        String password2 = secondPasswordField.getText();
        if(nick.length() <= 3 || password.length() <= 3 || password2.length() <= 3){
            showMessage("All fields must be filled in with words with minimum 4 letters", Alert.AlertType.WARNING);
            clearFields();
            return;
        }
        if(!password2.equals(password)){
            showMessage("Passwords must be the same", Alert.AlertType.WARNING);
            clearFields();
            return;
        }

        Socket socket = new Socket("localhost", 4444);
        client = new Client(socket, nick, password);
        client.setRegisterController(this);
        client.listenForMessage();
        ClientMessage clientMessage = new ClientMessage(client.getName(), client.getPassword(), "REGISTER");
        client.sendMessage(clientMessage);

    }
    public void returnToLogView() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) registerPane.getScene().getWindow();
                stage.close();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("log_scene.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage.setScene(scene);
                stage.show();
            }
        });


    }
    public void registerOK() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                showMessage("You registered correctly", Alert.AlertType.INFORMATION);
                clearFields();
                try {
                    client.getSocket().close();
                    returnToLogView();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }
    public void registerFailed(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                showMessage("This nick is already taken!", Alert.AlertType.WARNING);
                clearFields();
                try {
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
        secondPasswordField.clear();
        passwordField.clear();
        nickTextField.clear();
    }
}
