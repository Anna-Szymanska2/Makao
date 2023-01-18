package makao.server;


import makao.model.cards.Card;
import makao.model.cards.CardValue;
import makao.model.game.DeckOfCards;
import makao.model.game.Hand;
import makao.model.game.StateOfRound;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerPlayer implements Runnable{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ServerGame serverGame;
    private boolean gameIsOn = false;
    private boolean turnIsOn = false;
    private boolean receivedMessage = false;
    private ClientMessage messageFromClient;
    private String clientName;
    private Hand hand = new Hand();


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

    @Override
    public void run(){
        ServerMessage serverMessage = new ServerMessage("WELCOME", null, null, null, getHand());
        //serverMessage.setNewHand(getHand());
        boolean isTheFirstTime = true;
        try {
            sendServerMessage(serverMessage);
            //synchronized (serverGame.getDeckOfCards()){
                while (socket.isConnected()) {
                    System.out.println(clientName + "connected");
                    while(gameIsOn){
                        if(isTheFirstTime){
                            StateOfRound stateOfRound = new StateOfRound(serverGame.getStateOfRound());
                            DeckOfCards deckOfCards = new DeckOfCards(serverGame.getDeckOfCards());
                            ServerMessage serverMessage2 = new ServerMessage("INIT", null, stateOfRound,deckOfCards,getHand());
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
            ServerMessage serverMessage = new ServerMessage("END", serverGame.getWhoseTurn(), serverGame.getCardOnTopOfTheStack(), serverGame.getStateOfRound(), serverGame.getDeckOfCards());
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




