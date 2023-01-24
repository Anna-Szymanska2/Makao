package makao.server;


import makao.model.game.Hand;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class ServerPlayer implements Runnable{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Server server;
    private ServerGame serverGame;
    private boolean gameIsOn = false;
    private boolean turnIsOn = false;
    private boolean receivedMessage = false;
    private ClientMessage messageFromClient;
    private String clientName;
    private Hand hand = new Hand();
    private NamesAndPasswords namesAndPasswords;


    public ServerPlayer(Socket socket, ServerGame serverGame){
        try{
            this.socket = socket;
            this.serverGame = serverGame;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            ClientMessage clientMessage;
            clientMessage = (ClientMessage) in.readObject();
            this.clientName = clientMessage.getPlayerName();
        }catch  (IOException | ClassNotFoundException e) {
            closeEverything(socket, in, out);
        }
    }
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
            this.namesAndPasswords = server.getNamesAndPasswords();
        }catch  (IOException | ClassNotFoundException e) {
            closeEverything(socket, in, out);
        }
    }

    @Override
    public void run(){
        ServerMessage serverMessage = new ServerMessage("WELCOME", null, null, null, getHand());
        //serverMessage.setNewHand(getHand());
        boolean isTheFirstTime = true;
        try {
            sendServerMessage(serverMessage);
            loginOrRegister();
            if(!socket.isClosed()){
                receivedMessage = false;
                getClientMessage();
                if(messageFromClient.getActionID().equals("START_ROOM")){
                    int code = gameCodeGenerator();
                    ServerGame serverGame = new ServerGame(code,messageFromClient.getNumberOfPlayers());
                    Thread gameThread = new Thread(serverGame);
                    serverGame.addServerPlayer(this);
                    setServerGame(serverGame);
                    gameThread.start();
                    ServerMessage serverMessage2 = new ServerMessage("ROOM_STARTED");
                    sendServerMessage(serverMessage2);
                }else if(messageFromClient.getActionID().equals("JOIN_ROOM")) {
                    int code = messageFromClient.getCode();
                    for(ServerGame serverGame: server.getGames()){
                        if(serverGame.getCode() == code){
                            if(!serverGame.isGameIsOn()) {
                                serverGame.addServerPlayer(this);
                                setServerGame(serverGame);
                                ServerMessage serverMessage2 = new ServerMessage("ROOM_JOINED");
                                sendServerMessage(serverMessage2);
                            }else{
                                ServerMessage serverMessage2 = new ServerMessage("GAME_ALREADY_STARTED");
                                sendServerMessage(serverMessage2);
                            }
                        }
                    }
                }
            }
            //synchronized (serverGame.getDeckOfCards()){
                while (!socket.isClosed()) {
                    System.out.println(clientName + "connected");
                    while(gameIsOn){
                        if(isTheFirstTime){
                            /*StateOfRound stateOfRound = new StateOfRound(serverGame.getStateOfRound());
                            DeckOfCards deckOfCards = new DeckOfCards(serverGame.getDeckOfCards());*/
                            ServerMessage serverMessage2 = new ServerMessage("INIT", null, serverGame.getCardOnTopOfTheStack(), null,null);
                            serverMessage2.setNewHand(getHand());
                            sendServerMessage(serverMessage2);
                            isTheFirstTime = false;
                        }
                        System.out.println(clientName + "playing");
                        playMakao();
                    }
                }

           // }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void playMakao() throws IOException {
        if (!serverGame.isGameIsOn()) {
            ServerMessage serverMessage = new ServerMessage("END", serverGame.getWinner(), serverGame.getCardOnTopOfTheStack(), serverGame.getStateOfRound(), serverGame.getDeckOfCards());
            sendServerMessage(serverMessage);
        } else {
            if(turnIsOn) {
                String whoseTurn = serverGame.getWhoseTurn();
                ServerMessage serverMessage = new ServerMessage("DEFAULT", whoseTurn, serverGame.getCardOnTopOfTheStack(), serverGame.getStateOfRound(), serverGame.getDeckOfCards());
                serverMessage.setNewHand(getHand());
                sendServerMessage(serverMessage);
                if (whoseTurn.equals(this.clientName)) {
                    receivedMessage = false;
                    getClientMessage();
                    if(messageFromClient.getActionID().equals("END")){
                        serverGame.setStateOfRound(messageFromClient.getStateOfRound());
                        serverGame.setDeckOfCards(messageFromClient.getDeckOfCards());

                    }
                    if(messageFromClient.getActionID().equals("WIN")){
                        serverGame.setStateOfRound(messageFromClient.getStateOfRound());
                        serverGame.setDeckOfCards(messageFromClient.getDeckOfCards());
                        serverGame.setGameIsOn(false);
                        serverGame.setWinner(messageFromClient.getPlayerName());
                    }

                }
            }
            turnIsOn = false;
        }
    }

    public void getClientMessage(){
        try {
            while (!receivedMessage) {
                ClientMessage clientMessage;
                if ((clientMessage = (ClientMessage) in.readObject()) != null) {
                    messageFromClient = clientMessage;
                    receivedMessage = true;
                    serverGame.setStateOfRound(clientMessage.getStateOfRound());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void drawCard(){
        hand.addCard(serverGame.drawCard());
    }
    public void sendServerMessage(ServerMessage serverMessage){
        try {
            out.writeObject(serverMessage);
            out.reset();
            //serverGame.getDeckOfCards().wait();
            //serverGame.getDeckOfCards().notifyAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } /*catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IllegalMonitorStateException e){
            System.out.println("Wątek nie jest włascicielem monitora");
        }*/
    }

    public boolean isTurnIsOn() {
        return turnIsOn;
    }

    public void setTurnIsOn(boolean turnIsOn) {
        this.turnIsOn = turnIsOn;
    }

    public void loginOrRegister() throws IOException {
        String action = messageFromClient.getActionID();
        String username = messageFromClient.getPlayerName();
        String password = messageFromClient.getPassword();
        if (action.equals("REGISTER")) {
            if(namesAndPasswords.register(username, password)){
                ServerMessage serverMessage = new ServerMessage("REGISTER_OK");
                sendServerMessage(serverMessage);
                System.out.println("Registration went ok");
                out.flush();
                socket.close();
            }else {
                ServerMessage serverMessage = new ServerMessage("REGISTER_WRONG");
                sendServerMessage(serverMessage);
                out.flush();
                socket.close();
            }
        } else if (action.equals("LOGIN")) {
            if(namesAndPasswords.checkLogin(username, password)){
                ServerMessage serverMessage = new ServerMessage("LOGIN_OK");
                sendServerMessage(serverMessage);
            }else {
                ServerMessage serverMessage = new ServerMessage("LOGIN_WRONG");
                sendServerMessage(serverMessage);
                out.flush();
                socket.close();
            }
        }
    }

    public int gameCodeGenerator(){
        Random rnd = new Random();
        int code = 100000 + rnd.nextInt(900000);
        return code;
    }


//    public ClientMessage saveMessage(ClientMessage clientMessage){
//        return clientMessage;
//    }
//
//    public void broadcastMessage(ServerMessage messageToSend){
//        for(ServerPlayer serverPlayer : serverPlayers){
//            try{
//                serverPlayer.out.writeObject(messageToSend);
//                out.flush();
//            }catch (IOException e){
//                closeEverything(socket, in, out);
//            }
//        }
//    }
//
//    public void removeClientHandler(){
//        serverPlayers.remove(this);
//    }
//
    public void closeEverything(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        serverGame.removeServerPlayer(this);
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




