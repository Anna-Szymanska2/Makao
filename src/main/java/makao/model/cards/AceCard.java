package makao.model.cards;

import javafx.event.ActionEvent;
import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;


public class AceCard extends Card {
    public AceCard(CardColour cardColour, CardValue cardValue, String imagePath){
        super(cardColour, cardValue, imagePath);
    }
    private AceListener listener;

    public void setListener(AceListener listener) {
        this.listener = listener;
    }

    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        CardColour chosenColor = chooseColour();
        stateOfRound.setPossibleNextColour(chosenColor);
        stateOfRound.setChosenColor(chosenColor);
    }

    public CardColour chooseColour(){
        CardColour chosenColour = listener.aceWasPlayed( new ActionEvent());
        return chosenColour;
    }
}
