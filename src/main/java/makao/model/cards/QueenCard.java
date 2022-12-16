package makao.model.cards;

import makao.model.game.Stack;
import  makao.model.game.StateOfRound;

import java.util.ArrayList;

public class QueenCard extends Card {
    public QueenCard(CardColour cardColour, CardValue cardValue){
        super(cardColour, cardValue);
    }
    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextColour(CardColour.ANYCOLOUR);
    }
    @Override
    public boolean isPossibleToPlayCard(StateOfRound stateOfRound){
        ArrayList<CardValue> possibleNextCards = stateOfRound.getPossibleNextCards();
        if(stateOfRound.getRoundsOfRequest() > 0)
            return false;

        return possibleNextCards.get(0) == CardValue.ANYCARD;


    }

}
