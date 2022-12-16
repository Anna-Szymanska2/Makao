package model.game;

import model.cards.Card;

import java.util.ArrayList;

public class Stack {
    public ArrayList<Card> stack = new ArrayList<>();

    public void addCard(Card c) {
        stack.add(c);
    }
    public void removeCard(Card c) {
        stack.remove(c);
    }
    public void clearStack(){
        stack.clear();
    }

    public void printStack(){
        for(Card card:stack)
            System.out.println(card.toString());
    }
    public Card getLastCard(){
        return stack.get(stack.size()-1);
    }

}
