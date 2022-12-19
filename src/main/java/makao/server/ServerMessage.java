package makao.server;

import makao.model.cards.Card;
import makao.model.game.Hand;
import makao.model.game.StateOfRound;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerMessage implements Serializable {
    private String actionID = "DEFAULT";
    private String whoseTurn;
    private Card cardOnTopOfTheStack;
    private StateOfRound stateOfRound;
    private Hand newHand;
    private ArrayList<Card> cardsToDraw = new ArrayList<>();

    ServerMessage(String whoseTurn, Card cardOnTopOfTheStack, StateOfRound stateOfRound){
        this.whoseTurn = whoseTurn;
        this.cardOnTopOfTheStack = cardOnTopOfTheStack;
        this.stateOfRound = stateOfRound;
    }
    ServerMessage(String actionID){
        this.actionID = actionID;
    }
    ServerMessage(String actionID,String whoseTurn, Card cardOnTopOfTheStack, StateOfRound stateOfRound){
        this.actionID = actionID;
        this.whoseTurn = whoseTurn;
        this.cardOnTopOfTheStack = cardOnTopOfTheStack;
        this.stateOfRound = stateOfRound;
    }

    public void setCardsToDraw(ArrayList<Card> cardsToDraw) {
        this.cardsToDraw = cardsToDraw;
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

}
