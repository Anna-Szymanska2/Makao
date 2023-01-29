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
    public Hand(ArrayList<Card> cards){
        cardsInHand = cards;
    }


    public void addCard(Card c) {
        cardsInHand.add(c);
    }
    public void removeCard(Card c) {
        cardsInHand.remove(c);
    }
    public void removeCard(int position) {
        cardsInHand.remove(position);
    }
    public Card getCard(int position) {
        return cardsInHand.get(position);
    }
    public int getCardCount() {
        return cardsInHand.size();
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    public void removeAllCards(){
        cardsInHand.clear();
    }

    public void displayCardsInHand(){
        for(Card card:cardsInHand)
            System.out.println((cardsInHand.indexOf(card)+1) + ". " + card.toString());
    }
}
