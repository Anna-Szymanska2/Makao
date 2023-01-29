package makao.model.game;

import  makao.model.cards.Card;
import  makao.model.cards.CardColour;
import  makao.model.cards.CardValue;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The StateOfRound class is responsible for keeping track of all the information related to the state of the current round.
 *
 */
public class StateOfRound implements Serializable {
    private ArrayList<CardValue> possibleNextCards = new ArrayList<>();
    private CardColour possibleNextColour = null;
    private Card lastCard =  null;
    private int cardsToDraw;
    private int roundsToStay;
    private int players;
    private int roundsOfRequest;
    private int timeOfRound;
    private CardValue requestedValue = null;
    private CardColour chosenColor = null;

    public StateOfRound(ArrayList<CardValue> possibleNextCards, CardColour possibleNextColour, Card lastCard, int players) {
        this.possibleNextCards = possibleNextCards;
        this.possibleNextColour = possibleNextColour;
        this.lastCard = lastCard;
        this.cardsToDraw = 0;
        this.players = players;
    }

    public StateOfRound (StateOfRound stateOfRound){
        this.possibleNextCards = new ArrayList<>(stateOfRound.getPossibleNextCards());
        this.possibleNextColour = stateOfRound.getPossibleNextColour();
        this.lastCard =  stateOfRound.getLastCard();
        this.cardsToDraw = stateOfRound.getCardsToDraw();
        this.roundsToStay = stateOfRound.getRoundsToStay();
        this.players = stateOfRound.getPlayers();
        this.roundsOfRequest = stateOfRound.getRoundsOfRequest();
        this.requestedValue = stateOfRound.requestedValue;
        this.chosenColor = stateOfRound.getChosenColor();
    }

    public StateOfRound(int players) {
        this.players = players;
    }

    public CardValue getRequestedValue() {
        return requestedValue;
    }

    public CardColour getChosenColor() {
        return chosenColor;
    }

    public void setChosenColor(CardColour chosenColor) {
        this.chosenColor = chosenColor;
    }

    public void setRequestedValue(CardValue requestedValue) {
        this.requestedValue = requestedValue;
    }

    public int getRoundsOfRequest() {
        return roundsOfRequest;
    }

    public void setRoundsOfRequest(int roundsOfRequest) {
        this.roundsOfRequest = roundsOfRequest;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }


    public int getRoundsToStay() {
        return roundsToStay;
    }

    public CardColour getPossibleNextColour() {
        return possibleNextColour;
    }

    public int getCardsToDraw() {
        return cardsToDraw;
    }

    public Card getLastCard() {
        return lastCard;
    }

    public ArrayList<CardValue> getPossibleNextCards() {
        return possibleNextCards;
    }

    public void setRoundsToStay(int roundsToStay) {
        this.roundsToStay = roundsToStay;
    }

    public void setPossibleNextCards(ArrayList<CardValue> possibleNextCards) {
        this.possibleNextCards = possibleNextCards;
    }

    public void setPossibleNextColour(CardColour possibleNextColour) {
        this.possibleNextColour = possibleNextColour;
    }

    public void setLastCard(Card lastCard) {
        this.lastCard = lastCard;
    }

    public void setCardsToDraw(int cardsToDraw) {
        this.cardsToDraw = cardsToDraw;
    }

    public void setTimeOfRound(int timeOfRound) {
        this.timeOfRound = timeOfRound;
    }

    public int getTimeOfRound() {
        return timeOfRound;
    }
}
