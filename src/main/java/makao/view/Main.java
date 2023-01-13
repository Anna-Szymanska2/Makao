package makao.view;

import javafx.application.Application;
import javafx.stage.Stage;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import makao.controller.HelloController;
import makao.server.Client;
import makao.server.ClientMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String name = scanner.nextLine();
        Socket socket = new Socket("localhost", 4444);
        Client client = new Client(socket, name);
        ClientMessage clientMessage = new ClientMessage(client.getName());
        client.sendMessage(clientMessage);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("game_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        fxmlLoader.<HelloController>getController().setClient(client);
        client.listenForMessage();

        stage.setTitle("Makao");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
