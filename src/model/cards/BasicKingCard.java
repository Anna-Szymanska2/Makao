package model.cards;

import model.game.Stack;
import model.game.StateOfRound;

public class BasicKingCard extends Card {
    public BasicKingCard(CardColour cardColour, CardValue cardValue) {
        super(cardColour, cardValue);
    }

    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack) {
        super.playCard(stateOfRound, stack);

        stateOfRound.setCardsToDraw(0);
    }
}
