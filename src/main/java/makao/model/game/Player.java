package makao.model.game;

import makao.model.cards.*;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Player class is responsible for representing a player in a game.
 * It holds the player's hand, chosen cards, rounds to stay and nick.
 */
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

    /**
     * Method that adds a card to chosen cards list, and removes it from player's hand.
     *
     * @param index Index of the card in player's hand to add to chosen cards list.
     */
    public void addToChosen(int index){
        Card card = hand.cardsInHand.get(index);
        removeFromHand(card);
        chosenCards.add(card);
    }
    /**
     * Method that removes a card from chosen cards list, and adds it back to player's hand.
     *
     * @param index Index of the card in chosen cards list to remove.
     */
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

    /**
     * Method checks the state of waiting for the player.
     * If the player has rounds to stay, it decrements the roundsToStay by 1
     *
     * @param stateOfRound the current state of the round
     */

    public void checkStateOfWaiting(StateOfRound stateOfRound){
        if(getRoundsToStay() > 0){
            setRoundsToStay(getRoundsToStay() - 1);
        }
    }

    /**
     * Method checks the state of requests for the round.
     * If the round has requests, it decrements the roundsOfRequests by 1
     *
     * @param stateOfRound the current state of the round
     */
    public void checkStateOfRequests(StateOfRound stateOfRound){
        if(stateOfRound.getRoundsOfRequest() > 0)
            stateOfRound.setRoundsOfRequest(stateOfRound.getRoundsOfRequest() - 1);
    }

    /**
     * Method checks if the chosen cards are correct to be played
     * It checks if the size of chosenCards is not 0 and if all the chosen cards have the same CardValue
     *
     * @param stateOfRound the current state of the round
     * @return boolean indicating whether the chosen cards are correct
     */
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

    /**
     * Method plays the chosen cards
     * If the last card is a jack or an ace, it adds all other cards to the stack and plays the last card
     * Otherwise, it plays all the chosen cards
     *
     * @param stateOfRound the current state of the round
     * @param deckOfCards the current deck of cards
     */
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

    /**
     * Method takes in a Card, StateOfRound, DeckOfCards, AceListener, and JackListener
     * and adds the card to the player's hand. It also iterates cardsToDraw number of times and draws a card
     * from the deck, checking if it is an Ace or Jack card and setting its listener accordingly.
     * The method also sets the possible next cards and cards to draw to 0.
     *
     * @param firstCard The first card to add to the player's hand
     * @param stateOfRound The current state of the round
     * @param deckOfCards The deck of cards to draw from
     * @param aceListener The listener for Ace cards
     * @param jackListener The listener for Jack cards
     */
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

    /**
     * Method takes in StateOfRound, decrements the roundsToStay by 1 and sets the roundsToStay to 0
     * and sets the possible next cards to CardValue.ANYCARD.
     *
     * @param stateOfRound The current state of the round
     */
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
