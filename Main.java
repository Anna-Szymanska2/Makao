public class Main {
    public static void main(String[] args) {
        DeckOfCards deckOfCards = new DeckOfCards();
        Card randomCard = deckOfCards.drawRandomCard();
        System.out.println(randomCard.getCardValue() + " of " + randomCard.getCardColour() + " " + randomCard.getPoints());
    }
}
