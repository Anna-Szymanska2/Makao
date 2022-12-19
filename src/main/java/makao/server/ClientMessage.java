package makao.server;

import makao.model.cards.Card;
import makao.model.game.StateOfRound;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientMessage implements Serializable {

    String playerName;
    StateOfRound stateOfRound;
    String actionID;
    int numberOfCardsToDraw;
    ArrayList<Card> cardsToPlay = new ArrayList<>();

    ClientMessage(String playerName, StateOfRound stateOfRound, String actionID){
        this.playerName = playerName;
        this.stateOfRound = stateOfRound;
        this.actionID = actionID;
    }

    ClientMessage(String playerName){
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setCardsToPlay(ArrayList<Card> cardsToPlay) {
        this.cardsToPlay = cardsToPlay;
    }
}
