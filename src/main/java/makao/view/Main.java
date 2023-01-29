package makao.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The main method of the class launches the application by calling the launch() method.
 *
 */
public class Main extends Application {
    /**
     * The start() method is the entry point of the application. It loads the log_scene.fxml
     * file and sets it as the scene for the primary stage of the application.
     *
     * @param stage The primary stage of the application
     * @throws IOException if the log_scene.fxml file is not found
     */
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("log_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Makao");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
