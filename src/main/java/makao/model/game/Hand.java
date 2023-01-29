package makao.model.game;

import makao.model.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * The Hand class represents a collection of Card objects in a player hand.
 *
 */
public class Hand implements Serializable {
    ArrayList<Card> cardsInHand  = new ArrayList<>();
    public Hand(){};
    public void addCard(Card c) {
        cardsInHand.add(c);
    }
    public void removeCard(Card c) {
        cardsInHand.remove(c);
    }
    public int getCardCount() {
        return cardsInHand.size();
    }
    public void removeAllCards(){
        cardsInHand.clear();
    }

    public void displayCardsInHand(){
        for(Card card:cardsInHand)
            System.out.println((cardsInHand.indexOf(card)+1) + ". " + card.toString());
    }
}
