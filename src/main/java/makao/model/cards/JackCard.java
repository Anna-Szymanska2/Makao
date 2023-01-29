package makao.model.cards;

import javafx.event.ActionEvent;
import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;
import java.util.ArrayList;


public class JackCard extends Card {
    private JackListener listener;
    public JackCard(CardColour cardColour, CardValue cardValue, String imagePath){
        super(cardColour, cardValue, imagePath);
    }

    public void setListener(JackListener listener) {
        this.listener = listener;
    }

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
    public CardValue chooseValue(){
        CardValue chosenValue = listener.jackWasPlayed(new ActionEvent());
        return chosenValue;
    }
}
