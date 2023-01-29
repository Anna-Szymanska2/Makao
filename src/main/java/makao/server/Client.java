package makao.server;

import makao.controller.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * The Client class is responsible for handling all client-side communication with the server.
 * It provides methods for sending messages to the server, listening for incoming messages,
 * and handling those messages based on their type.
 *
 */
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

    /**
     * Sends a message to the server.
     *
     * @param clientMessage the message to be sent
     */

    public void sendMessage(ClientMessage clientMessage) {
        try {
            out.writeObject(clientMessage);
            out.flush();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listens for messages from the server and handles them according to their actionID.
     * This method starts a new thread to run the listening in the background.
     *
     */

    public void listenForMessage(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                ServerMessage messageFromServer;

                while (!socket.isClosed()){
                    try{
                        messageFromServer = (ServerMessage) in.readObject();

                        switch(messageFromServer.getActionID()){
                            case "INIT":
                                roomController.changeScene(messageFromServer, name);
                                break;
                            case "END":
                                gameController.changeSceneToRanking(messageFromServer);
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
                            case "GAME_NOT_EXISTS":
                                choosingRoomController.wrongRoom();
                                break;
                            case "GAME_EXITED":
                                gameController.changeSceneToQuit();
                                break;
                            case "DEFAULT":
                                if(messageFromServer.getWhoseTurn().equals(name))
                                    gameController.nextThisPlayerMove(messageFromServer);
                                else{
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

    /**
     * Closes the given socket, input stream, and output stream
     *
     * @param socket The socket to be closed
     * @param in The input stream to be closed
     * @param out The output stream to be closed
     */
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
    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }
}
