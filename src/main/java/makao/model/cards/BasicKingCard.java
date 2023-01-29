package makao.model.cards;

import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;
/**
 * Class representing a Basic King card.
 *
 */
public class BasicKingCard extends Card {
    public BasicKingCard(CardColour cardColour, CardValue cardValue, String imagePath) {
        super(cardColour, cardValue, imagePath);
    }

    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack) {
        super.playCard(stateOfRound, stack);

        stateOfRound.setCardsToDraw(0);
    }
}
