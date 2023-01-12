package makao.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.nio.Buffer;

public class ChoosingRoomController {
    @FXML
    private Button createRoomButton;
    @FXML
    private TextField codeTextField;
    @FXML
    private Button enterButton;


    public void tryToEnterTheRoom(){
        String code = codeTextField.getText();
        if(code.length() != 4){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("The code must have 4 letters!");
            alert.showAndWait();
            codeTextField.clear();
            return;
        }

    }
    public void createNewRoom(){

    }
}
