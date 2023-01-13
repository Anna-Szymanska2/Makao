package makao.model.game;

import makao.model.cards.*;

import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        test();
        //test3();


    }
    public static void test(){

        Hand hand = new Hand();
        hand.addCard(new Card(CardColour.SPADES, CardValue.KING, "imagePath"));
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream("user.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(hand);
            out.close(); fileOut.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Object has been saved");

        Hand user2 = null;

        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream("user.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            user2 = (Hand) in.readObject();
            in.close(); fileIn.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        user2.displayCardsInHand();



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
