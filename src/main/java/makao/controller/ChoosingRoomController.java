package makao.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import makao.server.Client;
import makao.server.ClientMessage;
import makao.view.Main;

import java.io.IOException;

public class ChoosingRoomController {
    @FXML
    private Button createRoomButton;
    @FXML
    private TextField codeTextField;
    @FXML
    private Button enterButton;
    @FXML
    private AnchorPane choosingRoomPane;
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
        Stage stage = (Stage) choosingRoomPane.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("roommaker_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        RoomController roomController = fxmlLoader.<RoomController>getController();
        roomController.setClient(client);
        client.setRoomController(roomController);
        roomController.getStartButton().setVisible(false);
        stage.show();
    }
    public void createNewRoom(){
        ClientMessage clientMessage = new ClientMessage(client.getName(),client.getPassword(),"START_ROOM");
        client.sendMessage(clientMessage);
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
}
