package makao.model.cards;

import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;

import java.util.ArrayList;

/**
 * The TwoOrThreeCard represents a card with a value of 2 or 3.
 *
 */
public class TwoOrThreeCard extends Card {

    public TwoOrThreeCard(CardColour cardColour, CardValue cardValue, String imagePath){
        super(cardColour, cardValue, imagePath);
    }

    /**
     * This method overrides the playCard method in the Card class.
     * It sets the possible next cards to 3 and 2, and increases the number of cards to draw by the value of the card.
     * @param stateOfRound the current state of the round.
     * @param stack the current stack of cards.
     */
    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.THREE); add( CardValue.TWO);}});
        stateOfRound.setCardsToDraw(stateOfRound.getCardsToDraw() + getCardValue().getValueOfCard());
    }
}

