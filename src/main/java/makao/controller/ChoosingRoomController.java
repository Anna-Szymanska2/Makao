package makao.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import makao.server.Client;
import makao.server.ClientMessage;
import makao.view.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChoosingRoomController implements Initializable {
    @FXML
    private Button createRoomButton;
    @FXML
    private TextField codeTextField;
    @FXML
    private Button enterButton;
    @FXML
    private AnchorPane choosingRoomPane;
    @FXML
    private ChoiceBox<Integer> numberOfPlayersComboBox;
    private Integer[] numberOfPlayers = {2,3,4};
    private Client client;


    public void tryToEnterTheRoom() throws IOException {
        String code = codeTextField.getText();
        if(code.length() != 6){
            showMessage("The code must have 6 digits!", Alert.AlertType.WARNING);
            codeTextField.clear();
            return;
        }
        int codeInt;
        try{
             codeInt = Integer.parseInt(code);
        } catch(NumberFormatException e){
            showMessage("You can use only numbers", Alert.AlertType.WARNING);
            codeTextField.clear();
            return;
        }
        changeToWaitingScene(codeInt);
        /*Stage stage = (Stage) choosingRoomPane.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("roommaker_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        RoomController roomController = fxmlLoader.<RoomController>getController();
        roomController.setClient(client);
        client.setRoomController(roomController);
        stage.show();*/
    }
    public void createNewRoom(){
        ClientMessage clientMessage = new ClientMessage(client.getName(),"START_ROOM",client.getPassword(),numberOfPlayersComboBox.getValue());
        client.sendMessage(clientMessage);
        client.listenForMessage();
    }

    public void changeToWaitingScene(int code) throws IOException {
        Stage stage = (Stage) choosingRoomPane.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("waiting_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        RoomController roomController = fxmlLoader.<RoomController>getController();
        roomController.setClient(client);
        roomController.setCodeOnLabel(code);
        client.setRoomController(roomController);
        stage.show();
    }


    public void setClient(Client client) {
        this.client = client;
    }
    public void showMessage(String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numberOfPlayersComboBox.getItems().addAll(numberOfPlayers);
    }
}
