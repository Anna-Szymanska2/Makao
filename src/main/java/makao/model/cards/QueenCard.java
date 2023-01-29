package makao.model.cards;

import makao.model.game.Stack;
import  makao.model.game.StateOfRound;

import java.util.ArrayList;

/**
 * The QueenCard class represents a card with the value of Queen.
 * The QueenCard can be played on any card and changes the possible next color to any color.
 *
 */
public class QueenCard extends Card {
    public QueenCard(CardColour cardColour, CardValue cardValue, String imagePath){
        super(cardColour, cardValue, imagePath);
    }

    /**
     * This method is called when the Queen card is played.
     * It changes the possible next color to any color.
     *
     * @param stateOfRound The current state of the round.
     * @param stack The current stack of cards on the table.
     */
    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextColour(CardColour.ANYCOLOUR);
    }

    /**
     * This method checks if the Queen card can be played in the current round state.
     * A Queen card can only be played if there is no request round and the possible next card is any card.
     *
     * @param stateOfRound The current state of the round.
     * @return true if the card can be played, false otherwise.
     */
    @Override
    public boolean isPossibleToPlayCard(StateOfRound stateOfRound){
        ArrayList<CardValue> possibleNextCards = stateOfRound.getPossibleNextCards();
        if(stateOfRound.getRoundsOfRequest() > 0)
            return false;

        return possibleNextCards.get(0) == CardValue.ANYCARD;
    }
}
