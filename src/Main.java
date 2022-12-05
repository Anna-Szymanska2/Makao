import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        //test();
        test3();


    }
    public static void test(){

        DeckOfCards deckOfCards = new DeckOfCards();
        Card randomCard = deckOfCards.drawRandomCard();
        int players = 4;
        System.out.println(randomCard.getCardValue() + " of " + randomCard.getCardColour() + " " + randomCard.getPoints());

        StateOfRound stateOfRound = new StateOfRound(
                new ArrayList<>() {{add(CardValue.FIVE);}},
                CardColour.DIAMONDS,
                new Card(CardColour.CLUBS, CardValue.ACE),
                players
        );

        Card card = new Card(CardColour.DIAMONDS, CardValue.SEVEN);
        TwoOrThreeCard twoOrThreeCard = new TwoOrThreeCard(CardColour.DIAMONDS, CardValue.TWO);
        QueenCard queen = new QueenCard(CardColour.CLUBS, CardValue.QUEEN);
        AceCard ace = new AceCard(CardColour.CLUBS, CardValue.ACE);
        //card.playCard(stateOfRound, );
        //twoOrThreeCard.playCard(stateOfRound);
        queen.isPossibleToPlayCard(stateOfRound);
        //ace.playCard(stateOfRound, );
        System.out.println(card.getCardValue() + " of " + card.getCardColour() + " " + card.getPoints());
        System.out.println(card.isPossibleToPlayCard(stateOfRound));
        DeckOfCards deckOfCards1 = new DeckOfCards();

    }
    public static void test2(){
        DeckOfCards deckOfCards = new DeckOfCards();
        deckOfCards.shuffle();
        deckOfCards.printDeckOfCards();
        System.out.println();
        System.out.println();
        Player maciej = new Player("Maciej");
        Player agata = new Player("Agata");
        ArrayList<Player> players = new ArrayList<>();
        players.add(maciej);
        players.add(agata);
        Game game = new Game(players);
        game.dealCards();
        maciej.hand.sortByCardColour();
        maciej.hand.displayCardsInHand();
        System.out.println();
        agata.hand.sortByCardValue();
        agata.hand.displayCardsInHand();

        System.out.println(deckOfCards.deckOfCards.size());
    }
    public static void test3(){
        Player maciej = new Player("Maciej");
        Player agata = new Player("Agata");
        ArrayList<Player> players = new ArrayList<>();
        players.add(maciej);
        players.add(agata);
        Game game = new Game(players);
        game.initializeGame();
        game.deckOfCards.stack.printStack();
    }
}
