package makao.model.cards;

public enum CardColour {
    ANYCOLOUR(0),
    HEARTS(1),
    SPADES(2),
    CLUBS(3),
    DIAMONDS(4);

    private final int colourValue;

    CardColour(int colourValue){
        this.colourValue = colourValue;
    }

    public int getColourValue(){
        return colourValue;
    }
}
