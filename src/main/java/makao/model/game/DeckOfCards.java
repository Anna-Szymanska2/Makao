package makao.model.game;


import makao.model.cards.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DeckOfCards implements Serializable {
    public ArrayList<Card> deckOfCards;
    public Stack stack;

    public DeckOfCards(){
        this.stack = new Stack();
        this.deckOfCards = new ArrayList();


        for (int j = 1; j < 5; j++) {
            String imagePath = "cards_images/ace_of_" + CardColour.values()[j].toString() + ".png";
            Card card = new AceCard(CardColour.values()[j], CardValue.ACE, imagePath);
            this.deckOfCards.add(card);
        }
        for (int j = 1; j < 5; j++) {
            String imagePath = "cards_images/2_of_" + CardColour.values()[j].toString() + ".png";
            Card card = new TwoOrThreeCard(CardColour.values()[j], CardValue.TWO, imagePath);
            this.deckOfCards.add(card);
        }
        for (int j = 1; j < 5; j++) {
            String imagePath = "cards_images/3_of_" + CardColour.values()[j].toString() + ".png";
            Card card = new TwoOrThreeCard(CardColour.values()[j], CardValue.THREE, imagePath);
            this.deckOfCards.add(card);
        }
        for (int j = 1; j < 5; j++) {
            String imagePath = "cards_images/4_of_" + CardColour.values()[j].toString() + ".png";
            Card card = new FourCard(CardColour.values()[j], CardValue.FOUR, imagePath);
            this.deckOfCards.add(card);
        }
        for (int i=5; i<11; i++) {
            CardValue value = CardValue.values()[i];

            for (int j = 1; j < 5; j++) {
                String imagePath = "cards_images/" + i+ "_of_" + CardColour.values()[j].toString() + ".png";
                Card card = new Card(CardColour.values()[j], value, imagePath);
                this.deckOfCards.add(card);
            }
        }
        for (int j = 1; j < 5; j++) {
            String imagePath = "cards_images/jack_of_" + CardColour.values()[j].toString() + ".png";
            Card card = new JackCard(CardColour.values()[j], CardValue.JACK, imagePath);
            this.deckOfCards.add(card);
        }
        for (int j = 1; j < 5; j++) {
            String imagePath = "cards_images/queen_of_" + CardColour.values()[j].toString() + ".png";
            Card card = new QueenCard(CardColour.values()[j], CardValue.QUEEN, imagePath);
            this.deckOfCards.add(card);
        }
        String imagePath = "cards_images/king_of_clubs.png";
        Card card = new BasicKingCard(CardColour.CLUBS, CardValue.KING, imagePath);
        this.deckOfCards.add(card);
        imagePath = "cards_images/king_of_diamonds.png";
        Card card2 = new BasicKingCard(CardColour.DIAMONDS, CardValue.KING, imagePath);
        this.deckOfCards.add(card2);
        imagePath = "cards_images/king_of_hearts.png";
        Card card3 = new FightingKingCard(CardColour.HEARTS, CardValue.KING, imagePath);
        this.deckOfCards.add(card3);
        imagePath = "cards_images/king_of_spades.png";
        Card card4 = new FightingKingCard(CardColour.SPADES, CardValue.KING, imagePath);
        this.deckOfCards.add(card4);

    }

    public DeckOfCards (DeckOfCards deckOfCards){
        this.stack = new Stack(deckOfCards.getStack());
        this.deckOfCards = new ArrayList<>(deckOfCards.getDeckOfCards());
    }

    public ArrayList<Card> getDeckOfCards() {
        return deckOfCards;
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

        Card card = deckOfCards.get(randomIndex);
        deckOfCards.remove(randomIndex);
        return card;
    }
    public Card drawLastCard(){
        if(deckOfCards.size()==1){
            Card card = stack.getLastCard();
            stack.removeCard(card);
            deckOfCards.addAll(stack.stack);
            stack.clearStack();
            stack.addCard(card);
            shuffle();
        }
        Card card = deckOfCards.get(deckOfCards.size()-1);
        deckOfCards.remove(deckOfCards.size()-1);
        return card;
    }

    public Stack getStack() {
        return stack;
    }
}