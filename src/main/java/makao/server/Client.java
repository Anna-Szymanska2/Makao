package makao.server;

import makao.model.cards.Card;
import makao.model.game.Hand;
import makao.model.game.StateOfRound;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String name;
    Hand hand = new Hand();
    ArrayList<Card> chosenCards = new ArrayList<>();
    int roundsToStay = 0;

    public Client(Socket socket, String name){
        try{
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.name = name;

    }catch  (IOException e) {
        closeEverything(socket, in, out);
    }
    }

    public void sendMessage(ClientMessage clientMessage) {
        try {
            out.writeObject(clientMessage);
            out.flush();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                ServerMessage messageFromServer;

                while (socket.isConnected()){
                    try{
                        messageFromServer = (ServerMessage) in.readObject();

                        switch(messageFromServer.getActionID()){
                            case "WELCOME":
                                System.out.println("Welcome, the game is starting soon ");
                                break;
                            case "END":
                                if(name != messageFromServer.getWhoseTurn())
                                    System.out.println(messageFromServer.getWhoseTurn() + " has won the game");
                                else
                                    System.out.println("Congratulations, you have won the game!!!");
                                break;
                            case "DRAW":
                                hand = messageFromServer.getNewHand();
                                break;
                            case "DEFAULT":
                                break;
                        }
                    }catch (IOException e){
                        closeEverything(socket, in, out);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        try{
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void readMessageFromServer(ServerMessage serverMessage){

    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String name = scanner.nextLine();
        Socket socket = new Socket("localhost", 4444);
        Client client = new Client(socket, name);
        ClientMessage clientMessage = new ClientMessage(client.name);
        client.sendMessage(clientMessage);
        client.listenForMessage();
    }
}
