package makao.server;


import makao.model.cards.Card;
import makao.model.cards.CardValue;
import makao.model.game.Hand;

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
            this.clientName = clientMessage.playerName;
        }catch  (IOException | ClassNotFoundException e) {
            closeEverything(socket, in, out);
        }
    }

    @Override
    public void run(){
        ServerMessage serverMessage = new ServerMessage("WELCOME");
        try {
            out.writeObject(serverMessage);
        while (socket.isConnected()) {
            System.out.println(clientName + "connected");
                while(gameIsOn){
                    System.out.println(clientName + "playing");
                    playMakao();
                }
        }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void playMakao() throws IOException {
        if (!serverGame.isGameIsOn()) {
            ServerMessage serverMessage = new ServerMessage("END", serverGame.getWhoseTurn(), serverGame.getCardOnTopOfTheStack(), serverGame.getStateOfRound());
            out.writeObject(serverMessage);
        } else {
            if(turnIsOn) {
                String whoseTurn = serverGame.getWhoseTurn();
                ServerMessage serverMessage = new ServerMessage("DEFAULT", whoseTurn, serverGame.getCardOnTopOfTheStack(), serverGame.getStateOfRound());
                serverMessage.setNewHand(hand);
                out.writeObject(serverMessage);
                if (whoseTurn.equals(this.clientName)) {
                    receivedMessage = false;
                    getClientMessage();
                    switch (messageFromClient.actionID) {
                        case "DRAW":
                            drawCard();
                            serverMessage.setNewHand(hand);
                            serverMessage.setActionID("DRAW");
                            sendServerMessage(serverMessage);
                            receivedMessage = false;
                            getClientMessage();
                            switch (messageFromClient.actionID) {
                                case "DRAW_MORE":
                                    for (int i = 0; i < messageFromClient.numberOfCardsToDraw; i++)
                                        drawCard();
                                    serverMessage.setNewHand(hand);
                                    sendServerMessage(serverMessage);
                                    break;
                                case "END":
                                    break;
                                case "PLAY":
                                    for (Card card : messageFromClient.cardsToPlay) {
                                        hand.removeCard(card);
                                        card.playCard(serverGame.getStateOfRound(), serverGame.getStack());
                                    }
                                    break;
                            }
                            break;
                        case "WAIT":
                            break;
                        case "PLAY":
                            Card lastCard = messageFromClient.cardsToPlay.get(messageFromClient.cardsToPlay.size() - 1);
                            boolean isJackOrAce = false;
                            if (lastCard.getCardValue() == CardValue.JACK || lastCard.getCardValue() == CardValue.ACE)
                                isJackOrAce = true;

                            if (isJackOrAce) {
                                for (Card card : messageFromClient.cardsToPlay) {
                                    if (!card.equals(lastCard))
                                        serverGame.getDeckOfCards().stack.addCard(card);
                                    hand.removeCard(card);
                                }
                                lastCard.playCard(serverGame.getStateOfRound(), serverGame.getStack());
                            } else {
                                for (Card card : messageFromClient.cardsToPlay) {
                                    card.playCard(serverGame.getStateOfRound(), serverGame.getStack());
                                    hand.removeCard(card);
                                }
                            }
                            messageFromClient.cardsToPlay.clear();
                            break;
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
                    serverGame.setStateOfRound(clientMessage.stateOfRound);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public boolean hasPlayerWon(){
        return hand.getCardCount() == 0;
    }
    public boolean getTurnIsOn() {
        return turnIsOn;
    }
}




