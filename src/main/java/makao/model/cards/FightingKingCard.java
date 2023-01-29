package makao.model.cards;

import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;

import java.util.ArrayList;
/**
 * Represents a Fighting King card.
 * Extends the Card class and overrides the playCard method to change the
 * possible next cards and number of cards to draw.
 */
public class FightingKingCard extends Card {
    public FightingKingCard(CardColour cardColour, CardValue cardValue, String imagePath){
        super(cardColour, cardValue, imagePath);
    }

    /**
     * Plays the card.
     * This method sets the possible next cards to King and increases the number of cards to draw by 5.
     *
     * @param stateOfRound the current state of the round
     * @param stack the stack of played cards
     */
    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.KING);}});
        stateOfRound.setCardsToDraw(stateOfRound.getCardsToDraw() + 5);
    }
}
