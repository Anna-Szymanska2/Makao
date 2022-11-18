public class Card {
    private CardValue cardValue;
    private CardColour cardColour;

    private int points;

    public Card(CardColour cardColour, CardValue cardValue) {
        this.cardValue = cardValue;
        this.cardColour = cardColour;

        this.points = cardValue.getCardValue();
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
}
