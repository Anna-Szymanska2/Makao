package makao.server;

import makao.model.game.DeckOfCards;
import makao.model.game.StateOfRound;

import java.io.Serializable;

public class ClientMessage implements Serializable {

    private String playerName;
    private StateOfRound stateOfRound;
    private String actionID;
    //private int numberOfCardsToDraw;
    private DeckOfCards deckOfCards;
    //ArrayList<Card> cardsToPlay = new ArrayList<>();
    private String password;

    public ClientMessage(String playerName, StateOfRound stateOfRound, String actionID, DeckOfCards deckOfCards){
        this.playerName = playerName;
        this.stateOfRound = stateOfRound;
        this.actionID = actionID;
        this.deckOfCards = deckOfCards;
    }

    public ClientMessage(String playerName){
        this.playerName = playerName;
    }
    public ClientMessage(String playerName, String password,String actionID){
        this.playerName = playerName;
        this.password = password;
        this.actionID = actionID;
    }

    /*public ClientMessage(String playerName, StateOfRound stateOfRound, String actionID, int numberOfCardsToDraw, ArrayList<Card> cardsToPlay, DeckOfCards deckOfCards) {
        this.playerName = playerName;
        this.stateOfRound = stateOfRound;
        this.actionID = actionID;
        //this.numberOfCardsToDraw = numberOfCardsToDraw;
        //this.cardsToPlay = cardsToPlay;
        this.deckOfCards = deckOfCards;
    }*/

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

  /*  public void setCardsToPlay(ArrayList<Card> cardsToPlay) {
        this.cardsToPlay = cardsToPlay;
    }*/

    public StateOfRound getStateOfRound() {
        return stateOfRound;
    }

    public String getActionID() {
        return actionID;
    }

    public DeckOfCards getDeckOfCards() {
        return deckOfCards;
    }
    public String getPassword() {
        return password;
    }

}
