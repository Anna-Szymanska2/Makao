package makao.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import makao.view.Main;

import java.io.IOException;

public class LoggingController {
    @FXML
    private Button registerButton;

    @FXML
    private AnchorPane loggingPane;

    @FXML
    private Button logInButton;

    @FXML
    private TextField nickTextFiled;

    @FXML
    private PasswordField passwordField;

    public void changeViewToRegister() throws IOException {
        nickTextFiled.clear();
        passwordField.clear();
        Stage stage = (Stage) loggingPane.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("register_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
        /*RegisterController controller = new RegisterController();
        Stage newStage = (Stage) scene.getWindow();
        newStage.show();*/

    }
    public void tryToLogIn(){
        String nick = nickTextFiled.getText();
        String password = passwordField.getText();
        if(nick.length() <= 3|| password.length() <= 3){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("All fields must be filled in with words with minimum 4 letters!");
            alert.showAndWait();
            return;
        }

    }
}
