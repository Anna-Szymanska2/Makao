package makao.model.cards;

import javafx.event.ActionEvent;
import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;
import java.util.ArrayList;

/**
 * The JackCard class represents a playing card with the value of Jack.
 *
 */
public class JackCard extends Card {
    private JackListener listener;
    public JackCard(CardColour cardColour, CardValue cardValue, String imagePath){
        super(cardColour, cardValue, imagePath);
    }

    public void setListener(JackListener listener) {
        this.listener = listener;
    }

    /**
     * Overrides the playCard method from the Card class.
     * This method sets the possible next cards, how many rounds request will stay, and requested value.
     * @param stateOfRound the current state of the round
     * @param stack the current stack of cards
     */
    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        CardValue chosenValue = chooseValue();
        if(chosenValue != CardValue.ANYCARD){
            stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.JACK);add(chosenValue);}});
            stateOfRound.setRoundsOfRequest(stateOfRound.getPlayers() + 1);
            stateOfRound.setRequestedValue(chosenValue);
            stateOfRound.setPossibleNextColour(CardColour.ANYCOLOUR);
        }
    }

    /**
     * Chooses a card value for request.
     * @return the chosen card value.
     */
    public CardValue chooseValue(){
        CardValue chosenValue = listener.jackWasPlayed(new ActionEvent());
        return chosenValue;
    }
}
