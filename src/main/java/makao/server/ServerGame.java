package makao.server;

import makao.model.cards.Card;
import makao.model.cards.CardColour;
import makao.model.cards.CardValue;
import makao.model.game.DeckOfCards;
import makao.model.game.Stack;
import makao.model.game.StateOfRound;

import java.util.ArrayList;

public class ServerGame implements Runnable{

    private ArrayList<ServerPlayer> serverPlayers = new ArrayList<>();
    private StateOfRound stateOfRound = new StateOfRound(2);
    private DeckOfCards deckOfCards = new DeckOfCards();
    private String whoseTurn;
    private boolean gameIsOn = false;
    int index = 0;

    @Override
    public void run() {
        initializeGame();
        gameIsOn = true;
        do{
            playMakao();
        }while(gameIsOn);

    }

    public void playMakao(){
        whoseTurn = serverPlayers.get(index).getClientName();
        serverPlayers.get(index).setTurnIsOn(true);
        if(serverPlayers.get(index).hasPlayerWon()) {
            System.out.println("Game is finished");
            for(ServerPlayer serverPlayer : serverPlayers){
                serverPlayer.setGameIsOn(false);
            }
            gameIsOn = false;
        }
        Card lastCard = stateOfRound.getLastCard();
        if(lastCard.getCardValue() == CardValue.KING && lastCard.getCardColour() == CardColour.SPADES && stateOfRound.getCardsToDraw() > 0)
            index = (index + serverPlayers.size() - 1)%serverPlayers.size();
        else
            index = (index + 1)%serverPlayers.size();
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

    public String getWhoseTurn() {
        return whoseTurn;
    }
    public void addServerPlayer(ServerPlayer serverPlayer){
        serverPlayers.add(serverPlayer);
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

}
