package makao.model.cards;

import makao.model.game.Stack;
import makao.model.game.StateOfRound;

import java.io.Serializable;
import java.util.ArrayList;
/**
 *
 * The Card class represents a card in a standard deck of playing cards.
 * It has a card value and a card color, as well as an image path representing its visual representation.
 * The class also contains methods for determining if the card can be played, playing the card, and getting its properties.
 *
 */
public class Card implements Serializable {
    private CardValue cardValue;
    private CardColour cardColour;
    private String imagePath;

    private int points;

    public Card(CardColour cardColour, CardValue cardValue, String imagePath) {
        this.cardValue = cardValue;
        this.cardColour = cardColour;
        this.imagePath = imagePath;
        this.points = cardValue.getValueOfCard();
    }

    public CardValue getCardValue() {
        return cardValue;
    }

    public CardColour getCardColour() {
        return cardColour;
    }

    /**
     * Check if a card can be played based on the current round state
     *
     * @param stateOfRound current state of the round
     * @return true if the card can be played, false otherwise
     */
    public boolean isPossibleToPlayCard(StateOfRound stateOfRound){
        Card lastCard = stateOfRound.getLastCard();
        CardValue lastCardValue = lastCard.getCardValue();
        ArrayList<CardValue> possibleNextCards = stateOfRound.getPossibleNextCards();
        CardColour possibleNextColour = stateOfRound.getPossibleNextColour();

        if(lastCard.getClass() == FightingKingCard.class && stateOfRound.getCardsToDraw() > 0) {
            if(this.getClass() == FightingKingCard.class)
                return true;
            else
                return false;
        }

        if(lastCardValue == cardValue){
            return true;
        }


        if(stateOfRound.getRoundsOfRequest() > 0){
            return cardValue == stateOfRound.getRequestedValue();
        }

        boolean isOneOfPossibleCards = false;
        if(possibleNextCards.get(0) != CardValue.ANYCARD){
            for (CardValue possibleNextCard : possibleNextCards) {
                if (possibleNextCard == cardValue) {
                    isOneOfPossibleCards = true;
                    break;
                }
            }
            if(!isOneOfPossibleCards)
                return false;
        }

       if(possibleNextColour != CardColour.ANYCOLOUR)
           if(possibleNextColour != cardColour)
               return false;

        return true;
    }

    /**
     * Play the card, updating the state of the round and adding the card to the stack
     * @param stateOfRound the current state of the round
     * @param stack the current stack of cards
     */
    public void playCard(StateOfRound stateOfRound, Stack stack){
        stack.addCard(this);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
        stateOfRound.setPossibleNextColour(cardColour);
        stateOfRound.setLastCard(this);
        stateOfRound.setChosenColor(null);

    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString(){
        return getCardValue() + " of " + getCardColour();
    }

    @Override
    public boolean equals(Object object){
        Card card = (Card) object;
        if(card.getCardValue() != this.cardValue)
            return false;

        if(card.getCardColour() != this.cardColour)
            return false;

        return true;
    }
}

