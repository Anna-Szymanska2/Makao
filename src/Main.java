import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        //test();
        test2();


    }
    public static void test(){

        DeckOfCards deckOfCards = new DeckOfCards();
        Card randomCard = deckOfCards.drawRandomCard();
        int players = 4;
        System.out.println(randomCard.getCardValue() + " of " + randomCard.getCardColour() + " " + randomCard.getPoints());

        StateOfRound stateOfRound = new StateOfRound(
                new ArrayList<>() {{add(CardValue.FIVE);}},
                new ArrayList<>() {{add(CardColour.DIAMONDS);}},
                CardValue.FIVE,
                players
        );

        Card card = new Card(CardColour.DIAMONDS, CardValue.SEVEN);
        TwoOrThreeCard twoOrThreeCard = new TwoOrThreeCard(CardColour.DIAMONDS, CardValue.TWO);
        QueenCard queen = new QueenCard(CardColour.CLUBS, CardValue.QUEEN);
        AceCard ace = new AceCard(CardColour.CLUBS, CardValue.ACE);
        card.playCard(stateOfRound);
        //twoOrThreeCard.playCard(stateOfRound);
        queen.isPossibleToPlayCard(stateOfRound);
        ace.playCard(stateOfRound);
        System.out.println(card.getCardValue() + " of " + card.getCardColour() + " " + card.getPoints());
        System.out.println(card.isPossibleToPlayCard(stateOfRound));
        DeckOfCards deckOfCards1 = new DeckOfCards();

    }
    public static void test2(){
        DeckOfCards deckOfCards = new DeckOfCards();
        deckOfCards.printDeckOfCards();
        System.out.println();
        System.out.println(deckOfCards.deckOfCards.size());
        System.out.println();
        deckOfCards.shuffle();
        deckOfCards.printDeckOfCards();
        System.out.println();
        System.out.println();
        Card randomCard = deckOfCards.drawRandomCard();
        System.out.println(randomCard.toString());
    }
}
