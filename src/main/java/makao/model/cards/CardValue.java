package makao.model.cards;

public enum CardValue {
    ANYCARD(0),
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13);

    private final int valueOfCard;

    CardValue(int cardValue){
        this.valueOfCard = cardValue;
    }

    public int getValueOfCard() {
        return valueOfCard;
    }


}

