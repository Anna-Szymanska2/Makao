package makao.model.cards;

import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;

import java.util.ArrayList;

public class TwoOrThreeCard extends Card {

    public TwoOrThreeCard(CardColour cardColour, CardValue cardValue, String imagePath){
        super(cardColour, cardValue, imagePath);
    }
    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.THREE); add( CardValue.TWO);}});
        stateOfRound.setCardsToDraw(stateOfRound.getCardsToDraw() + getCardValue().getValueOfCard());
    }
}

