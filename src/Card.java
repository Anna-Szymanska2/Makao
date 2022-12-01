import java.util.ArrayList;

public class Card {
    private CardValue cardValue;
    private CardColour cardColour;

    private int points;

    public Card(CardColour cardColour, CardValue cardValue) {
        this.cardValue = cardValue;
        this.cardColour = cardColour;

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
        ArrayList<CardColour> possibleNextColour = stateOfRound.getPossibleNextColour();

        if(lastCardValue == cardValue)
            return true;

        if(stateOfRound.getRoundsOfRequest() > 0){
            return cardValue == stateOfRound.getRequestedValue();
        }

        if(possibleNextCards.get(0) != CardValue.ANYCARD){
            for (CardValue possibleNextCard : possibleNextCards) {
                if (possibleNextCard == cardValue) {
                    break;
                }
                return false;
            }
        }

       if(possibleNextColour.get(0) != CardColour.ANYCOLOUR)
           for (CardColour colour : possibleNextColour) {
               if (colour == cardColour) {
                   break;
               }
               return false;
           }
        return true;
    }

    public void playCard(StateOfRound stateOfRound){
        //updateStackOfCards()

        if(stateOfRound.getRoundsOfRequest() > 0){
            stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(stateOfRound.getRequestedValue());}});
            stateOfRound.setRoundsOfRequest(stateOfRound.getRoundsOfRequest() - 1);
        }
        else{
            stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
            stateOfRound.setPossibleNextColour(new ArrayList<>() {{add(cardColour);}});

        }
        stateOfRound.setLastCard(this);

    }

    @Override
    public String toString(){
        return getCardValue() + " of " + getCardColour();
    }
}

