package makao.model.game;

import  makao.model.cards.Card;

import java.io.Serializable;
import java.util.ArrayList;

public class Stack implements Serializable {
    public ArrayList<Card> stack = new ArrayList<>();

    public Stack (Stack stack){
        this.stack = new ArrayList<>(stack.getStack());
    }

    public Stack(){};

    public ArrayList<Card> getStack() {
        return stack;
    }

    public void addCard(Card c) {
        stack.add(c);
    }
    public void removeCard(Card c) {
        stack.remove(c);
    }
    public void clearStack(){
        stack.clear();
    }

    public Card getLastCard(){
        return stack.get(stack.size()-1);
    }

}
