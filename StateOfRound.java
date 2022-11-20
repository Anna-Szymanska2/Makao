import java.util.ArrayList;

public class StateOfRound {



    private ArrayList<CardValue> possibleNextCards;
    private ArrayList<CardColour> possibleNextColour;
    private CardValue lastCardValue;
    private int cardsToDraw;
    private int roundsToStay = 0;
    private int players;
    private int roundsOfRequest = 0;
    private CardValue requestedValue = null;

    public StateOfRound(ArrayList<CardValue> possibleNextCards, ArrayList<CardColour> possibleNextColour, CardValue lastCardValue, int players) {
        this.possibleNextCards = possibleNextCards;
        this.possibleNextColour = possibleNextColour;
        this.lastCardValue = lastCardValue;
        this.cardsToDraw = 0;
        this.players = players;
    }

    public CardValue getRequestedValue() {
        return requestedValue;
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

    public ArrayList<CardColour> getPossibleNextColour() {
        return possibleNextColour;
    }

    public int getCardsToDraw() {
        return cardsToDraw;
    }

    public CardValue getLastCardValue() {
        return lastCardValue;
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

    public void setPossibleNextColour(ArrayList<CardColour> possibleNextColour) {
        this.possibleNextColour = possibleNextColour;
    }

    public void setLastCardValue(CardValue lastCardValue) {
        this.lastCardValue = lastCardValue;
    }

    public void setCardsToDraw(int cardsToDraw) {
        this.cardsToDraw = cardsToDraw;
    }

}
