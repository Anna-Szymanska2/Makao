public enum CardColour {
    HEARTS(1),
    SPADES(2),
    CLUBS(3),
    DIAMONDS(4);

    private int colourValue;

    private CardColour(int colourValue){
        this.colourValue = colourValue;
    }

    public int getColourValue(){
        return colourValue;
    }
}
