package makao.model.cards;

import javafx.event.ActionEvent;
import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;

/**
 * The AceCard class represents a specific type of card.
 * It extends the Card class and has the ability to allow the player to choose the color
 * of the card when it is played.
 *
 */
public class AceCard extends Card {
    public AceCard(CardColour cardColour, CardValue cardValue, String imagePath){
        super(cardColour, cardValue, imagePath);
    }
    private AceListener listener;

    public void setListener(AceListener listener) {
        this.listener = listener;
    }
    /**
     * Overrides the playCard method from the parent class.
     * Allows the player to choose the color of the card when it is played.
     *
     * @param stateOfRound the current state of the round
     * @param stack the current stack of cards
     */
    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        CardColour chosenColor = chooseColour();
        stateOfRound.setPossibleNextColour(chosenColor);
        stateOfRound.setChosenColor(chosenColor);
    }
    /**
     * Allows the player to choose the color of the card.
     *
     * @return the chosen color of the card
     */
    public CardColour chooseColour(){
        CardColour chosenColour = listener.aceWasPlayed( new ActionEvent());
        return chosenColour;
    }
}
