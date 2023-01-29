package makao.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import makao.server.Client;
import makao.server.ClientMessage;
import makao.view.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The ChoosingRoomController class is responsible for handling events and actions
 * related to the choosing room view. It allows users to join an existing room by
 * entering a room code or create a new room by choosing the number of players and
 * the time of round.
 *
 */
public class ChoosingRoomController implements Initializable {
    @FXML
    private TextField codeTextField;
    @FXML
    private AnchorPane choosingRoomPane;
    @FXML
    private ChoiceBox<Integer> numberOfPlayersChoiceBox;
    @FXML
    private ChoiceBox<Integer>  timeOfRoundChoiceBox;

    private Integer[] numberOfPlayers = {2,3,4};
    private Integer[] timeOfRound = {30, 45, 60, 90};
    private Client client;

    /**
     * Attempts to join a room by sending a JOIN_ROOM message to the server with the code entered by the user.
     * If the code is invalid, the user is informed with an error message.
     *
     * @throws IOException
     */
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
        ClientMessage clientMessage = new ClientMessage(client.getName(),"JOIN_ROOM", codeInt);
        client.sendMessage(clientMessage);

    }
    /**
     * Creates a new room by sending a START_ROOM message to the server with the chosen number of players
     * and time of round.
     */
    public void createNewRoom(){
        ClientMessage clientMessage = new ClientMessage(client.getName(),"START_ROOM",client.getPassword(),
                numberOfPlayersChoiceBox.getValue(),timeOfRoundChoiceBox.getValue());
        client.sendMessage(clientMessage);

    }

    /**
     * Displays a message to the user indicating that the room code entered is not valid.
     */
    public void wrongRoom(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
             showMessage("There is not available room with such code", Alert.AlertType.ERROR);
             codeTextField.clear();
            }
        });


    }

    /**
     * Changes the current scene to the waiting scene.
     * Closes the current stage and opens a new stage with the waiting scene.
     * Sets the client, code, and room controller for the new scene.
     *
     * @param code the code to be set on the label of the waiting scene.
     */
    public void changeToWaitingScene(int code){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Stage stage = (Stage) choosingRoomPane.getScene().getWindow();
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
                    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("waiting_scene.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.setScene(scene);
                    RoomController roomController = fxmlLoader.<RoomController>getController();
                    roomController.setClient(client);
                    roomController.setCodeOnLabel(code);
                    client.setRoomController(roomController);
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

    /**
     * Shows a message to the user with the provided type and message.
     *
     * @param message the message to be displayed.
     * @param type the type of the message to be displayed.
     */
    public void showMessage(String message, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Initializes the choice boxes for number of players and time of round.
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numberOfPlayersChoiceBox.getItems().addAll(numberOfPlayers);
        numberOfPlayersChoiceBox.setValue(2);
        timeOfRoundChoiceBox.getItems().addAll(timeOfRound);
        timeOfRoundChoiceBox.setValue(60);
    }
}
