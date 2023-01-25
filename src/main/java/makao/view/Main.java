package makao.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
      /*  Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String name = scanner.nextLine();*/

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("log_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        //fxmlLoader.<HelloController>getController().setClient(client);


        stage.setTitle("Makao");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
