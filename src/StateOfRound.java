import java.util.ArrayList;

public class StateOfRound {



    private ArrayList<CardValue> possibleNextCards = new ArrayList<>();
    private ArrayList<CardColour> possibleNextColour = new ArrayList<>();
    private CardValue lastCardValue =  null;
    private int cardsToDraw;
    private int roundsToStay;
    private int players;
    private int roundsOfRequest;
    private CardValue requestedValue = null;

    public StateOfRound(ArrayList<CardValue> possibleNextCards, ArrayList<CardColour> possibleNextColour, CardValue lastCardValue, int players) {
        this.possibleNextCards = possibleNextCards;
        this.possibleNextColour = possibleNextColour;
        this.lastCardValue = lastCardValue;
        this.cardsToDraw = 0;
        this.players = players;
    }

    public StateOfRound(int players) {
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
