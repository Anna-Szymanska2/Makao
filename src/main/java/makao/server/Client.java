package makao.server;

import makao.controller.*;
import makao.model.cards.Card;
import makao.model.cards.CardValue;
import makao.model.game.DeckOfCards;
import makao.model.game.Hand;
import makao.model.game.StateOfRound;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements Serializable{

    transient private Socket socket;
    transient private ObjectOutputStream out;
    transient private ObjectInputStream in;
    transient private GameController gameController;
    transient private RegisterController registerController;
    transient private LoggingController loggingController;
    transient private ChoosingRoomController choosingRoomController;
    transient private RoomController roomController;
    transient private GameEndingController gameEndingController;
    transient private GameQuitController gameQuitController;
    private String name;
    private String path;
    private String password;
    private boolean gameIsOn = false;
    private boolean turnIsOn = false;
    private boolean gameClosed = false;
    Hand hand = new Hand();
    ArrayList<Card> chosenCards = new ArrayList<>();
    int roundsToStay = 0;

    public Client(Socket socket, String name, String password) {
        this.socket = socket;
        this.name = name;
        this.password = password;
        try{
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        }catch  (IOException e) {
        closeEverything(socket, in, out);
    }

    }

    public Client(Socket socket, String name, String password, String path) {
        this.socket = socket;
        this.name = name;
        this.password = password;
        this.path = path;
        try{
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        }catch  (IOException e) {
            closeEverything(socket, in, out);
        }

    }

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

    public String getPath() {
        return path;
    }

    public void setGameEndingController(GameEndingController gameEndingController) {
        this.gameEndingController = gameEndingController;
    }

    public void setChoosingRoomController(ChoosingRoomController choosingRoomController) {
        this.choosingRoomController = choosingRoomController;
    }

    public void setLoggingController(LoggingController loggingController) {
        this.loggingController = loggingController;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getPassword() {
        return password;
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

                while (!socket.isClosed()){
                    try{
                        /*if(gameClosed){
                            System.out.println("test");
                            closeEverything(socket, in, out);
                            return;
                        }*/
                        messageFromServer = (ServerMessage) in.readObject();

                        switch(messageFromServer.getActionID()){
                            /*case "WELCOME":
                                System.out.println("Welcome, the game is starting soon ");
                                break;*/
                            case "INIT":
                                //System.out.println("INIT");
                                roomController.changeScene(messageFromServer, name);
                                //gameController.init(messageFromServer, name);
                                break;
                            case "END":
                                //controller.endOfGame(messageFromServer);
                                gameController.changeSceneToRanking(messageFromServer);
                                //zakomentowane bo poki działa w petli jest dosyć mordercze
                               /* if(!name.equals(messageFromServer.getWhoseTurn()))
                                    System.out.println(messageFromServer.getWhoseTurn() + " has won the game");
                                else
                                    System.out.println("Congratulations, you have won the game!!!");*/
                                gameIsOn = false;
                                break;
                            case "REGISTER_OK":
                                registerController.registerOK();
                                break;
                            case "REGISTER_WRONG":
                                registerController.registerFailed();
                                break;
                            case "LOGIN_OK":
                                loggingController.loginCorrect();
                                break;
                            case "LOGIN_WRONG":
                                loggingController.loginFailed();
                                break;
                            case "ROOM_STARTED":
                            case "ROOM_JOINED":
                                choosingRoomController.changeToWaitingScene(messageFromServer.getCode());
                                Thread.sleep(100);
                                break;
                            /*case "GAME_ALREADY_STATED":
                                break;*/
                            case "GAME_NOT_EXISTS":
                                choosingRoomController.wrongRoom();
                                break;
                            case "GAME_EXITED":
                                gameController.changeSceneToQuit();
                                break;
                            case "DEFAULT":
                               // System.out.println("Card on top of the stack: " + messageFromServer.getCardOnTopOfTheStack().toString());
                                /*if(hand.getCardCount() == 0)
                                    hand = messageFromServer.getNewHand();*/
                                if(messageFromServer.getWhoseTurn().equals(name))
                                    gameController.nextThisPlayerMove(messageFromServer);
                                else{
                                    //System.out.println(messageFromServer.getWhoseTurn() + " is making their move");
                                    gameController.nextPlayerMove(messageFromServer);
                                }


                                break;
                        }
                    }catch (IOException e){
                        closeEverything(socket, in, out);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
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

    public String getName() {
        return name;
    }
    public void setRegisterController(RegisterController registerController) {
        this.registerController = registerController;
    }

    public GameController getGameController() {
        return gameController;
    }

    public void setRoomController(RoomController roomController) {
        this.roomController = roomController;
    }

    public void setGameQuitController(GameQuitController gameQuitController) {
        this.gameQuitController = gameQuitController;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
    public void setGameClosed(boolean gameClosed) {
        this.gameClosed = gameClosed;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }
}
