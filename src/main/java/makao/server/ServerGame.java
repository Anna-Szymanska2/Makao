package makao.server;

import makao.model.cards.Card;
import makao.model.cards.CardColour;
import makao.model.cards.CardValue;
import makao.model.game.*;

import java.util.ArrayList;

public class ServerGame implements Runnable{

    private ArrayList<ServerPlayer> serverPlayers = new ArrayList<>();
    private StateOfRound stateOfRound = new StateOfRound(4);
    private DeckOfCards deckOfCards = new DeckOfCards();
    private String whoseTurn;
    private String winner;
    private ArrayList<String> playersNames = new ArrayList<>();
    private ArrayList<String> playersAvatars = new ArrayList<>();
    private boolean gameIsOn = false;
    private boolean nextTurn = false;
    private boolean gameExists = false;
    int index = 0;
    int code;

    public ServerGame(int code, int numberOfPlayers, int timeOfRound) {
        this.stateOfRound.setPlayers(numberOfPlayers);
        this.stateOfRound.setTimeOfRound(timeOfRound*1000);
        this.code = code;

    }

    @Override
    public void run() {
        gameExists = true;
        while (gameExists) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //System.out.println(serverPlayers.size());
            if (serverPlayers.size() == stateOfRound.getPlayers()) {
                initializeGame();
                gameIsOn = true;
                System.out.println("Gra sie zaczela");
                ServerPlayer[] players = new ServerPlayer[serverPlayers.size()];
                players = serverPlayers.toArray(players);

                for (ServerPlayer serverPlayer : players) {
            /*ServerMessage serverMessage = new ServerMessage("INIT", null, getStateOfRound(),getDeckOfCards(), serverPlayer.getHand());
            serverPlayer.sendServerMessage(serverMessage);*/
                    serverPlayer.setGameIsOn(true);
                }


//        whoseTurn = serverPlayers.get(0).getClientName();
//        serverPlayers.get(0).setTurnIsOn(true);
                do {
                    playMakao(players);
                } while (gameIsOn);
                System.out.println("Game has ended");
                gameExists = false;
            }
        }
    }

    public void playMakao(ServerPlayer [] players){
        nextTurn = true;
        /*for (ServerPlayer serverPlayer : serverPlayers) {
            if(serverPlayer.getTurnIsOn())
                nextTurn = false;
        }*/
        for (ServerPlayer serverPlayer : players) {
            if(serverPlayer.getTurnIsOn())
                nextTurn = false;
        }
        if(nextTurn) {
            Card lastCard = stateOfRound.getLastCard();
            if (lastCard.getCardValue() == CardValue.KING && lastCard.getCardColour() == CardColour.SPADES && stateOfRound.getCardsToDraw() > 0)
                index = (index + serverPlayers.size() - 1) % serverPlayers.size();
            else
                index = (index + 1) % serverPlayers.size();
            whoseTurn = serverPlayers.get(index).getClientName();
            /*for (ServerPlayer serverPlayer : serverPlayers) {
                serverPlayer.setTurnIsOn(true);
            }*/
            for (ServerPlayer serverPlayer : players) {
                serverPlayer.setTurnIsOn(true);
            }
            if (serverPlayers.get(index).isWinner()) {
                System.out.println("Game is finished");
            for(ServerPlayer serverPlayer : serverPlayers){
                serverPlayer.setGameIsOn(false);
            }
                gameIsOn = false;
            }

            nextTurn = false;
        }

    }

    public ArrayList<String> getPlayersNames() {
        return playersNames;
    }

    public ArrayList<String> getPlayersAvatars() {
        return playersAvatars;
    }

    public void setGameIsOn(boolean gameIsOn) {
        this.gameIsOn = gameIsOn;
    }


    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }

    public StateOfRound getStateOfRound() {
        return stateOfRound;
    }
    public Card getCardOnTopOfTheStack(){
        return deckOfCards.stack.getLastCard();
    }

    public Stack getStack(){
        return deckOfCards.stack;
    }
    public Card drawCard(){
        return deckOfCards.drawLastCard();
    }

    public int getCode() {
        return code;
    }

    public String getWhoseTurn() {
        return whoseTurn;
    }
    public void addServerPlayer(ServerPlayer serverPlayer){

        serverPlayers.add(serverPlayer);
        playersNames.add(serverPlayer.getClientName());
        playersAvatars.add(serverPlayer.getClientAvatar());
    }

    public void removeServerPlayer(ServerPlayer serverPlayer){
        serverPlayers.remove(serverPlayer);
    }

    public void initializeGame(){
        index = 0;
        deckOfCards.shuffle();
        dealCards();
        Card card = deckOfCards.drawRandomCard();
        while(card.getClass()!= Card.class){
            deckOfCards.deckOfCards.add(card);
            card = deckOfCards.drawRandomCard();
        }
        card.playCard(stateOfRound, deckOfCards.stack);
    }

    public void dealCards(){
        for(ServerPlayer player:serverPlayers){
            for(int i = 0; i<5; i++){
                player.drawCard();
            }
        }
    }

    public void setStateOfRound(StateOfRound stateOfRound) {
        this.stateOfRound = stateOfRound;
    }
    public DeckOfCards getDeckOfCards() {
        return deckOfCards;
    }
    public boolean isGameIsOn() {
        return gameIsOn;
    }

    public void endGameForAllPlayers(){
        for(ServerPlayer serverPlayer : serverPlayers){
            serverPlayer.setGameIsOn(false);
            serverPlayer.endGame();
        }
    }

    public void setDeckOfCards(DeckOfCards deckOfCards) {
        this.deckOfCards = deckOfCards;
    }
}
