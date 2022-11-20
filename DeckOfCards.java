import java.util.Random;

public class DeckOfCards {
    public Card[][] deckOfCards;
    public static final int NUM_VALUES = CardValue.values().length-1;
    public static final int NUM_COLOURS = CardColour.values().length-1;

    public DeckOfCards(){
        deckOfCards = new Card[NUM_COLOURS][NUM_VALUES];
        CardColour[] cardColours = CardColour.values();
        CardValue[] cardValues = CardValue.values();

        for(int coloursIterator = 0; coloursIterator < NUM_COLOURS; coloursIterator++){
            CardColour cardColour = cardColours[coloursIterator + 1];

            for(int valuesIterator = 0; valuesIterator < NUM_VALUES; valuesIterator++){
                CardValue cardValue = cardValues[valuesIterator + 1];

                deckOfCards[coloursIterator][valuesIterator] = new Card(cardColour, cardValue);
            }
        }
    }

    public Card drawRandomCard(){
        Random rand = new Random();
        int randomColourIndex = rand.nextInt(NUM_COLOURS);
        int randomValueIndex = rand.nextInt(NUM_VALUES);

        return deckOfCards[randomColourIndex][randomValueIndex];
    }
}