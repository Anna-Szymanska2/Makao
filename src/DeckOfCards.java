import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DeckOfCards {
    public ArrayList<Card> deckOfCards;
    public Stack stack = new Stack();

    public DeckOfCards(){
        this.deckOfCards = new ArrayList();

        for (int j = 1; j < 5; j++) {
            Card card = new AceCard(CardColour.values()[j], CardValue.ACE);
            this.deckOfCards.add(card);
        }
        for (int j = 1; j < 5; j++) {
            Card card = new TwoOrThreeCard(CardColour.values()[j], CardValue.TWO);
            this.deckOfCards.add(card);
        }
        for (int j = 1; j < 5; j++) {
            Card card = new TwoOrThreeCard(CardColour.values()[j], CardValue.THREE);
            this.deckOfCards.add(card);
        }
        for (int j = 1; j < 5; j++) {
            Card card = new FourCard(CardColour.values()[j], CardValue.FOUR);
            this.deckOfCards.add(card);
        }
        for (int i=5; i<11; i++) {
            CardValue value = CardValue.values()[i];

            for (int j = 1; j < 5; j++) {
                Card card = new Card(CardColour.values()[j], value);
                this.deckOfCards.add(card);
            }
        }
        for (int j = 1; j < 5; j++) {
            Card card = new JackCard(CardColour.values()[j], CardValue.JACK);
            this.deckOfCards.add(card);
        }
        for (int j = 1; j < 5; j++) {
            Card card = new QueenCard(CardColour.values()[j], CardValue.QUEEN);
            this.deckOfCards.add(card);
        }
        Card card = new Card(CardColour.CLUBS, CardValue.KING);
        this.deckOfCards.add(card);
        Card card2 = new Card(CardColour.DIAMONDS, CardValue.KING);
        this.deckOfCards.add(card2);
        Card card3 = new FightingKing(CardColour.HEARTS, CardValue.KING);
        this.deckOfCards.add(card3);
        Card card4 = new FightingKing(CardColour.SPADES, CardValue.KING);
        this.deckOfCards.add(card4);

    }

    public void printDeckOfCards(){
        for(Card card:deckOfCards)
            System.out.println(card.toString());
    }

    public void shuffle(){
        Collections.shuffle(deckOfCards);
    }
    public Card drawRandomCard(){
        Random rand = new Random();
        int randomIndex = rand.nextInt(deckOfCards.size()-1);

        return deckOfCards.get(randomIndex);
    }
    public Card drawLastCard(){
        if(deckOfCards.size()==1){
            for(Card card:stack.stack)
                deckOfCards.add(card);
            stack.clearStack();
            shuffle();
        }
        Card card = deckOfCards.get(deckOfCards.size()-1);
        deckOfCards.remove(deckOfCards.size()-1);
        return card;
    }
    public void dealCards(Game game){
        for(Player player:game.players){
            for(int i = 0; i<5; i++){
                Card card = drawLastCard();
                player.hand.cardsInHand.add(card);
            }
        }
    }
}