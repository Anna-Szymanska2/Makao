package makao.model.game;

import makao.model.cards.Card;
import makao.model.cards.CardColour;
import makao.model.cards.CardValue;

import java.util.ArrayList;

public class Game {
    ArrayList<Player> players;
    StateOfRound stateOfRound;
    DeckOfCards deckOfCards;

    public Game(ArrayList<Player> players) {
        this.players = players;
        this.stateOfRound = new StateOfRound(players.size());
        this.deckOfCards = new DeckOfCards();
    }

    public void gameIsOn(){

        int index = 0;

        while (true){
            deckOfCards.stack.printStack();
            players.get(index).makeMove(stateOfRound, deckOfCards);
            if(players.get(index).hasPlayerWon()) {
                System.out.println("Congratulation " + players.get(index).nick + " you have won the game");
                System.out.println("makao.model.game.Game is finished");
                break;
            }
            Card lastCard = stateOfRound.getLastCard();
            if(lastCard.getCardValue() == CardValue.KING && lastCard.getCardColour() == CardColour.SPADES && stateOfRound.getCardsToDraw() > 0)
                index = (index + players.size() - 1)%players.size();
            else
                index = (index + 1)%players.size();
        }
    }

    public void initializeGame(){
        deckOfCards.shuffle();
        dealCards();
        Card card = deckOfCards.drawRandomCard();
        while(card.getClass()!= Card.class){
            deckOfCards.deckOfCards.add(card);
            card = deckOfCards.drawRandomCard();
        }
        card.playCard(stateOfRound, deckOfCards.stack );
    }
    public void dealCards(){
        for(Player player:players){
            for(int i = 0; i<5; i++){
                Card card = deckOfCards.drawLastCard();
                player.hand.cardsInHand.add(card);
            }
        }
    }

    public Stack getStack(){
        return deckOfCards.stack;
    }

    public static void main(String[] arg){
        Player maciej = new Player("Maciej");
        Player agata = new Player("Agata");
        Player kuba = new Player("Kuba");
        ArrayList<Player> players = new ArrayList<>();
        players.add(maciej);
        players.add(agata);
        players.add(kuba);
        Game game = new Game(players);
        game.initializeGame();
        game.gameIsOn();
    }
}
