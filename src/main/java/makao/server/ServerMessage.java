package makao.server;

import makao.model.cards.Card;
import makao.model.game.DeckOfCards;
import makao.model.game.Hand;
import makao.model.game.StateOfRound;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The class ServerMessage is used to represent the messages sent by the server to the clients.
 */
public class ServerMessage implements Serializable {
    private String actionID = "DEFAULT";
    private String whoseTurn;
    private Card cardOnTopOfTheStack;
    private StateOfRound stateOfRound;
    private DeckOfCards deckOfCards;
    private Hand newHand;
    private ArrayList<Card> cardsInHand;
    private int code;
    private ArrayList<String> playersNames;
    private ArrayList<String> playersAvatars;
    private ArrayList<String> ranking;

    ServerMessage(String actionID){
        this.actionID = actionID;
    }
    ServerMessage(String actionID, int code){
        this.actionID = actionID;
        this.code = code;
    }

    public ServerMessage(String actionID, Card cardOnTopOfTheStack, ArrayList<String> playersNames, ArrayList<String> playersAvatars) {
        this.actionID = actionID;
        this.cardOnTopOfTheStack = cardOnTopOfTheStack;
        this.playersNames = playersNames;
        this.playersAvatars = playersAvatars;
    }

    ServerMessage(String actionID, String whoseTurn, Card cardOnTopOfTheStack, StateOfRound stateOfRound, DeckOfCards deckOfCards){
        this.actionID = actionID;
        this.whoseTurn = whoseTurn;
        this.cardOnTopOfTheStack = cardOnTopOfTheStack;
        this.stateOfRound = stateOfRound;
        this.deckOfCards = deckOfCards;
    }

    public ArrayList<String> getPlayersNames() {
        return playersNames;
    }

    public ArrayList<String> getPlayersAvatars() {
        return playersAvatars;
    }

    public ArrayList<String> getRanking() {
        return ranking;
    }

    public void setRanking(ArrayList<String> ranking) {
        this.ranking = ranking;
    }
    public void setNewHand(Hand newHand) {
        this.newHand = newHand;
    }
    public void setActionID(String actionID) {
        this.actionID = actionID;
    }

    public void setWhoseTurn(String whoseTurn) {
        this.whoseTurn = whoseTurn;
    }
    public String getActionID() {
        return actionID;
    }

    public int getCode() {
        return code;
    }

    public String getWhoseTurn() {
        return whoseTurn;
    }

    public Card getCardOnTopOfTheStack() {
        return cardOnTopOfTheStack;
    }

    public StateOfRound getStateOfRound() {
        return stateOfRound;
    }

    public Hand getNewHand() {
        return newHand;
    }

    public DeckOfCards getDeckOfCards() {
        return deckOfCards;
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    public void setStateOfRound(StateOfRound stateOfRound) {
        this.stateOfRound = stateOfRound;
    }
}
