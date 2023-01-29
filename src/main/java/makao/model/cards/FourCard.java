package makao.model.cards;

import makao.model.game.Stack;
import  makao.model.game.StateOfRound;

import java.util.ArrayList;
/**
 * The FourCard class extends the Card class and represents a Four card.
 */
public class FourCard extends Card {
    public FourCard(CardColour cardColour, CardValue cardValue, String imagePath){
        super(cardColour, cardValue, imagePath);
    }

    /**
     * Overrides the playCard method of the parent class Card.
     * Adds 1 to rounds to stay of the next player.
     *
     * @param stateOfRound The current state of the round.
     * @param stack        The current stack of cards on the table.
     */
    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.FOUR);}});
        stateOfRound.setRoundsToStay(stateOfRound.getRoundsToStay() + 1);
    }
}
