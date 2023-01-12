package makao.controller;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import makao.view.Main;

import java.io.IOException;
import java.net.URL;
import java.nio.Buffer;
import java.util.ResourceBundle;



public class RegisterController implements Initializable {
    @FXML
    private Button signUpButton;
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
    public void tryToSignUp(){
        String nick = nickTextField.getText();
        String password = passwordField.getText();
        String password2 = secondPasswordField.getText();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(null);
        alert.setHeaderText(null);
        if(nick.length() <= 3 || password.length() <= 3 || password2.length() <= 3){
            alert.setContentText("All fields must be filled in with words with minimum 4 letters");
            alert.showAndWait();
            secondPasswordField.clear();
            passwordField.clear();
            nickTextField.clear();
            return;
        }
        if(!password2.equals(password)){
            alert.setContentText("Passwords must be the same");
            alert.showAndWait();
            secondPasswordField.clear();
            passwordField.clear();
            return;
        }

    }
    public void returnToLogView() throws IOException {
        Stage stage = (Stage) registerPane.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("log_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();

    }
}
