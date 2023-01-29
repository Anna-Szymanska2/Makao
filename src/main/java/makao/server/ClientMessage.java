package makao.server;

import makao.model.game.DeckOfCards;
import makao.model.game.StateOfRound;

import java.io.Serializable;

public class ClientMessage implements Serializable {

    private String playerName;
    private StateOfRound stateOfRound;
    private String actionID;
    private DeckOfCards deckOfCards;
    private String password;
    private String path;
    private int code;
    private int numberOfPlayers;
    private int timeOfRound;

    public ClientMessage(String playerName, StateOfRound stateOfRound, String actionID, DeckOfCards deckOfCards){
        this.playerName = playerName;
        this.stateOfRound = stateOfRound;
        this.actionID = actionID;
        this.deckOfCards = deckOfCards;
    }

    public ClientMessage(String playerName, String actionID, int code) {
        this.playerName = playerName;
        this.actionID = actionID;
        this.code = code;
    }

    public ClientMessage(String playerName, String actionID, String password, int numberOfPlayers, int timeOfRound) {
        this.playerName = playerName;
        this.actionID = actionID;
        this.password = password;
        this.numberOfPlayers = numberOfPlayers;
        this.timeOfRound = timeOfRound;
    }
    public ClientMessage(String playerName, String actionID, String password, String path) {
        this.playerName = playerName;
        this.actionID = actionID;
        this.password = password;
        this.path = path;
    }

    public ClientMessage(String playerName, String password,String actionID){
        this.playerName = playerName;
        this.password = password;
        this.actionID = actionID;
    }

    public ClientMessage(String name, String actionID) {
        this.playerName = name;
        this.actionID = actionID;
    }

    public String getPath() {
        return path;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

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

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getTimeOfRound() {
        return timeOfRound;
    }

    public int getCode() {
        return code;
    }
}
