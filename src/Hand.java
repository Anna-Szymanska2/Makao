import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Hand {
    ArrayList<Card> cardsInHand = new ArrayList<>();

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
    public void displayCardsInHand(){
        for(Card card:cardsInHand)
            System.out.println((cardsInHand.indexOf(card)+1) + ". " + card.toString());
    }

    public void sortByCardValue(){
        ArrayList<Card> handAfterSorting = (ArrayList<Card>) cardsInHand.stream()
                .sorted(Comparator.comparing(card -> card.getCardValue().getValueOfCard()))
                .collect(Collectors.toList());
        Collections.copy(cardsInHand,handAfterSorting);
    }

    public void sortByCardColour(){
        ArrayList<Card> handAfterSorting = (ArrayList<Card>) cardsInHand.stream()
                .sorted(Comparator.comparing(card -> card.getCardColour().getColourValue()))
                .collect(Collectors.toList());
        Collections.copy(cardsInHand,handAfterSorting);
    }
}
