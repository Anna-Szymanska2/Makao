package model.cards;

import model.game.Stack;
import model.game.StateOfRound;

import java.util.ArrayList;

public class FourCard extends Card{
    public FourCard(CardColour cardColour, CardValue cardValue){
        super(cardColour, cardValue);
    }

    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.FOUR);}});
        stateOfRound.setRoundsToStay(stateOfRound.getRoundsToStay() + 1);
    }
}
