package makao.model.game;

import makao.model.cards.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class Player implements Serializable {
    private Hand hand = new Hand();
    private ArrayList<Card> chosenCards = new ArrayList<>();
    private int roundsToStay = 0;
    private String nick;

    public Player(String nick){
        this.nick = nick;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }
    public void setRoundsToStay(int roundsToStay) {
        this.roundsToStay = roundsToStay;
    }

    public int getRoundsToStay() {
        return roundsToStay;
    }
    public ArrayList<Card> getCardsInHand(){
        return hand.cardsInHand;
    }
    public void addToHand(Card card){
        hand.addCard(card);
    }
    public void removeFromHand(Card card){
        hand.removeCard(card);
    }
    public void addToChosen(int index){
        Card card = hand.cardsInHand.get(index);
        removeFromHand(card);
        chosenCards.add(card);
    }
    public void removeFromChosen(int index){
        Card card = chosenCards.get(index);
        chosenCards.remove(card);
        addToHand(card);
    }

    public ArrayList<Card> getChosenCards() {
        return chosenCards;
    }

    public int getNumberOfCards(){
       return hand.getCardCount();
    }
    public void displayCards(){
        hand.displayCardsInHand();
    }
    public void checkStateOfWaiting(StateOfRound stateOfRound){
        if(getRoundsToStay() > 0){
            setRoundsToStay(getRoundsToStay() - 1);
        }
    }

    public void checkStateOfRequests(StateOfRound stateOfRound){
        if(stateOfRound.getRoundsOfRequest() > 0)
            stateOfRound.setRoundsOfRequest(stateOfRound.getRoundsOfRequest() - 1);
    }

    public boolean areChosenCardsCorrect(StateOfRound stateOfRound){
        if(chosenCards.size() == 0)
            return false;
        CardValue cardValue = chosenCards.get(0).getCardValue();
        for(int i = 1; i < chosenCards.size(); i++){
            if(chosenCards.get(i).getCardValue() != cardValue)
                return false;
        }
        return chosenCards.get(0).isPossibleToPlayCard(stateOfRound);
    }

    public void playChosenCards(StateOfRound stateOfRound, DeckOfCards deckOfCards){
        Card lastCard = chosenCards.get(chosenCards.size() - 1);
        boolean isJackOrAce = false;
        if(lastCard.getCardValue() == CardValue.JACK || lastCard.getCardValue() == CardValue.ACE)
            isJackOrAce = true;

        if(isJackOrAce) {
            for (Card card : chosenCards) {
                if(!card.equals(lastCard))
                    deckOfCards.stack.addCard(card);
            }
            lastCard.playCard(stateOfRound, deckOfCards.stack);
        }
        else{
            for (Card card : chosenCards) {
                card.playCard(stateOfRound, deckOfCards.stack);
                //hand.removeCard(card);
            }
        }
        chosenCards.clear();
    }

    public void putBackChosenCards(){
        for(Card card: chosenCards){
            addToHand(card);
        }
        chosenCards.clear();
    }


    public void takeDrewCards(Card firstCard, StateOfRound stateOfRound, DeckOfCards deckOfCards, AceListener aceListener, JackListener jackListener){
        int cardsToDraw = stateOfRound.getCardsToDraw();

        if(firstCard != null){
            hand.addCard(firstCard);
        }
        for(int i = 0; i < cardsToDraw -1; i++){
            Card card = deckOfCards.drawLastCard();
            if(card.getCardValue() == CardValue.ACE){
                AceCard cardCasted = (AceCard) card;
                cardCasted.setListener(aceListener);
            }
            if(card.getCardValue() == CardValue.JACK){
                JackCard cardCasted = (JackCard) card;
                cardCasted.setListener(jackListener);
            }
            hand.addCard(card);
        }
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
        stateOfRound.setCardsToDraw(0);
    }

    public void waitRounds(StateOfRound stateOfRound){
        setRoundsToStay(stateOfRound.getRoundsToStay() - 1);
        stateOfRound.setRoundsToStay(0);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});

    }
    public boolean hasPlayerWon(){
        return hand.getCardCount() == 0;
    }

    public String getNick() {
        return nick;
    }




}
