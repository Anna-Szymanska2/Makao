package makao.server;


import makao.model.game.Hand;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * The class ServerPlayer implements the Runnable interface and is responsible for handling the communication between
 * the server and a single client.
 * It contains methods for handling the login and registration of clients, creating and joining rooms, and playing the game.
 *
 */
public class ServerPlayer implements Runnable{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static ArrayList<ServerPlayer> users = new ArrayList<>();
    private Server server;
    private ServerGame serverGame;
    private boolean gameIsOn = false;
    private boolean turnIsOn = false;
    private boolean receivedMessage = false;
    private boolean gameEnded = false;
    private ClientMessage messageFromClient;
    private String clientName;
    private String clientAvatar;
    private Hand hand = new Hand();
    private  NamesAndStoredDetails namesAndStoredDetails;
    private boolean isWinner;

    public ServerPlayer(Socket socket, Server server){
        try{
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            this.server = server;
            ClientMessage clientMessage;
            clientMessage = (ClientMessage) in.readObject();
            this.messageFromClient = clientMessage;
            this.clientName = clientMessage.getPlayerName();
            this.namesAndStoredDetails = server.getNamesAndPasswords();
            this.clientAvatar = clientMessage.getPath();
            users.add(this);
            listenForMessage();
        }catch  (IOException | ClassNotFoundException e) {
            closeEverything(socket, in, out);
        }
    }

    /**
     * The run method of the ServerPlayer class. It handles the login and registration of clients,
     * creating and joining rooms, and playing the game.
     */
    @Override
    public void run(){
        try {
            loginOrRegister();
            while(users.contains(this)) {
                while (!socket.isClosed()) {
                    receivedMessage = false;
                    getClientMessage(false);
                    if (messageFromClient.getActionID().equals("START_ROOM")) {
                        int code = gameCodeGenerator();
                        ServerGame serverGame = new ServerGame(code, messageFromClient.getNumberOfPlayers(), messageFromClient.getTimeOfRound());
                        Thread gameThread = new Thread(serverGame);
                        serverGame.addServerPlayer(this);
                        setServerGame(serverGame);
                        server.getGames().add(serverGame);
                        gameThread.start();
                        ServerMessage serverMessage2 = new ServerMessage("ROOM_STARTED", code);
                        sendServerMessage(serverMessage2);
                        break;
                    } else if (messageFromClient.getActionID().equals("JOIN_ROOM")) {
                        int code = messageFromClient.getCode();
                        boolean gameExists = false;
                        for (ServerGame serverGame : server.getGames()) {
                            if (serverGame.getCode() == code) {
                                serverGame.addServerPlayer(this);
                                setServerGame(serverGame);
                                ServerMessage serverMessage2 = new ServerMessage("ROOM_JOINED", code);
                                sendServerMessage(serverMessage2);
                                gameExists = true;
                            }

                        }
                        if (!gameExists) {
                            ServerMessage serverMessage2 = new ServerMessage("GAME_NOT_EXISTS");
                            sendServerMessage(serverMessage2);
                        } else {
                            break;
                        }
                    }
                }
                gameEnded = false;
                isWinner = false;
                while (!socket.isClosed() && !gameEnded) {
                    boolean isTheFirstTime = true;
                    while (gameIsOn) {
                        if (isTheFirstTime) {
                            ServerMessage serverMessage2 = new ServerMessage("INIT", serverGame.getCardOnTopOfTheStack(), serverGame.getPlayersNames(), serverGame.getPlayersAvatars());
                            serverMessage2.setStateOfRound(serverGame.getStateOfRound());
                            serverMessage2.setNewHand(getHand());
                            sendServerMessage(serverMessage2);
                            isTheFirstTime = false;
                            Thread.sleep(1000);
                        }
                        Thread.sleep(10);
                        playMakao();
                    }
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }   catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method checks if a server game is currently ongoing and if the turn is on for the client.
     * If so, it sends a ServerMessage to the client containing information about the current game state,
     * including the top card on the stack, the current round state, and the deck of cards.
     * If the client's turn is currently active, the method also waits for a message from the client and updates the game state accordingly
     *
     * @throws IOException
     */
    private void playMakao() throws IOException {
        if (serverGame != null) {
            if (serverGame.isGameIsOn()) {
                if (turnIsOn) {
                    String whoseTurn = serverGame.getWhoseTurn();
                    ServerMessage serverMessage = new ServerMessage("DEFAULT", whoseTurn, serverGame.getCardOnTopOfTheStack(), serverGame.getStateOfRound(), serverGame.getDeckOfCards());
                    serverMessage.setNewHand(getHand());
                    sendServerMessage(serverMessage);
                    if (whoseTurn.equals(this.clientName)) {
                        receivedMessage = false;
                        getClientMessage(true);
                        if (messageFromClient.getActionID().equals("END")) {
                            serverGame.setStateOfRound(messageFromClient.getStateOfRound());
                            serverGame.setDeckOfCards(messageFromClient.getDeckOfCards());
                            gameEnded = true;
                        }
                        if (messageFromClient.getActionID().equals("WIN")) {
                            serverGame.setStateOfRound(messageFromClient.getStateOfRound());
                            serverGame.setDeckOfCards(messageFromClient.getDeckOfCards());
                            isWinner = true;
                            gameIsOn = false;
                            serverGame.setGameIsOn(false);
                            serverGame.setWinner(messageFromClient.getPlayerName());
                            serverGame.endGameForAllPlayers();
                            gameEnded = true;
                        }

                    }
                }
                turnIsOn = false;
            }
        }
    }

    /**
     * Method sends a ServerMessage to the client containing information about the game winner
     * and the final ranking of the players.
     *
     * @param ranking the final ranking of the players
     */
    public void endGame(ArrayList<String> ranking){
        ServerMessage serverMessage = new ServerMessage("END", serverGame.getWinner(), serverGame.getCardOnTopOfTheStack(), serverGame.getStateOfRound(), serverGame.getDeckOfCards());
        serverMessage.setRanking(ranking);
        sendServerMessage(serverMessage);
    }
    public void drawCard(){
        hand.addCard(serverGame.drawCard());
    }

    public void removeAllCardsInHand(){
        hand.removeAllCards();
    }

    /**
     * Method sends a given ServerMessage to the client.
     *
     * @param serverMessage
     */
    public void sendServerMessage(ServerMessage serverMessage){
        try {
            out.writeObject(serverMessage);
            out.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTurnIsOn() {
        return turnIsOn;
    }

    public String getClientAvatar() {
        return clientAvatar;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public void setTurnIsOn(boolean turnIsOn) {
        this.turnIsOn = turnIsOn;
    }

    /**
     * Method handles the login and registration process for the client.
     * It first checks the actionID of the message received from the client, and based on that,
     * either registers or logs in the user.
     *
     * @throws IOException
     */
    public synchronized void loginOrRegister() throws IOException {
        String action = messageFromClient.getActionID();
        String username = messageFromClient.getPlayerName();
        String password = messageFromClient.getPassword();
        if (action.equals("REGISTER")) {
            if(namesAndStoredDetails.register(username, password, messageFromClient.getPath())){
                ServerMessage serverMessage = new ServerMessage("REGISTER_OK");
                sendServerMessage(serverMessage);
                users.remove(this);
                out.flush();
                socket.close();
                SaveAndRestoreData.save(namesAndStoredDetails);
            }else {
                ServerMessage serverMessage = new ServerMessage("REGISTER_WRONG");
                sendServerMessage(serverMessage);
                users.remove(this);
                out.flush();
                socket.close();
            }
        } else if (action.equals("LOGIN")) {
            File file = new File("namesAndStoredDetails.ser");
            if (file.exists()) {
                namesAndStoredDetails = SaveAndRestoreData.restore();
            }
            boolean alreadyLogged = false;
            int i = 0;
            for(ServerPlayer serverPlayer:users){
                if(serverPlayer.getClientName().equals(username))
                    i++;
                if(i>1)
                    alreadyLogged = true;

            }

            if(!alreadyLogged && namesAndStoredDetails.checkLogin(username, password)){
                ServerMessage serverMessage = new ServerMessage("LOGIN_OK");
                sendServerMessage(serverMessage);
                clientAvatar = namesAndStoredDetails.returnAvatar(username);
            }else {
                ServerMessage serverMessage = new ServerMessage("LOGIN_WRONG");
                users.remove(this);
                sendServerMessage(serverMessage);
                out.flush();
                socket.close();
            }
        }
    }

    /**
     * The gameCodeGenerator method generates a random 6-digit game code.
     *
     * @return a random 6-digit game code
     */
    public int gameCodeGenerator(){
        Random rnd = new Random();
        int code = 100000 + rnd.nextInt(900000);
        return code;
    }

    /**
     * The closeEverything method closes the socket, input and output streams.
     *
     * @param socket the socket to be closed
     * @param in the input stream to be closed
     * @param out the output stream to be closed
     */
    public void closeEverything(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        users.remove(this);
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

    /**
     * Waits for message from the client
     *
     * @param gameIsOn
     */
    public void getClientMessage(boolean gameIsOn){
        //try {
            while (!receivedMessage) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

    }

    /**
     * Starts a new thread that listens for messages from the client and processes it.
     *
     */
    public void listenForMessage(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                ClientMessage messageClient;

                while (!socket.isClosed()){
                    try {
                        messageClient = (ClientMessage) in.readObject();
                            messageFromClient = messageClient;
                            receivedMessage = true;
                            if(gameIsOn)
                                serverGame.setStateOfRound(messageClient.getStateOfRound());


                        if(messageClient.getActionID().equals("DISCONNECTED")&&messageClient.getPlayerName().equals(clientName)){
                            if(gameIsOn){
                                serverGame.closeGame();
                            }
                            if(serverGame!=null)
                                serverGame.removeServerPlayer(ServerPlayer.this);
                            closeEverything(socket,in,out);
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

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public void setServerGame(ServerGame serverGame) {
        this.serverGame = serverGame;
    }

    public void setGameIsOn(boolean gameIsOn) {
        this.gameIsOn = gameIsOn;
    }

    public String getClientName() {
        return clientName;
    }
    public Hand getHand() {
        return hand;
    }

    public boolean hasPlayerWon(){
        return hand.getCardCount() == 0;
    }
    public boolean getTurnIsOn() {
        return turnIsOn;
    }
}




