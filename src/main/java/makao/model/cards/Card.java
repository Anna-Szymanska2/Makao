package makao.model.cards;

import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;

import java.util.ArrayList;

public class Card {
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

    public void setCardValue(CardValue cardValue) {
        this.cardValue = cardValue;
    }

    public CardValue getCardValue() {
        return cardValue;
    }

    public void setCardColour(CardColour cardColour) {
        this.cardColour = cardColour;
    }

    public CardColour getCardColour() {
        return cardColour;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

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

    public void playCard(StateOfRound stateOfRound, Stack stack){
        stack.addCard(this);

       /* if(stateOfRound.getRoundsOfRequest() - 1 > 0){
            stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(stateOfRound.getRequestedValue());}});

        }
        else{*/
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
        stateOfRound.setPossibleNextColour(cardColour);

        // }
        stateOfRound.setLastCard(this);

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

