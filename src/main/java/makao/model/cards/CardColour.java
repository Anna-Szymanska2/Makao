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
    @Override
    public String toString() {
        String color = "";
        switch(colourValue){
            case 1 -> color = "hearts";
            case 2 -> color = "spades";
            case 3 -> color = "clubs";
            case 4 -> color = "diamonds";
        }
        return color;
    }
}
