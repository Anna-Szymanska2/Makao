package makao.model.cards;

import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;

import java.util.ArrayList;

public class FightingKingCard extends Card {
    public FightingKingCard(CardColour cardColour, CardValue cardValue, String imagePath){
        super(cardColour, cardValue, imagePath);
    }

    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.KING);}});
       // stateOfRound.setPossibleNextColour(new ArrayList<>() {{add(makao.model.cards.CardColour.SPADES); add(makao.model.cards.CardColour.HEARTS);}});
        stateOfRound.setCardsToDraw(stateOfRound.getCardsToDraw() + 5);
    }
}
