import java.util.ArrayList;

public class Game {
    ArrayList<Player> players = new ArrayList<>();
    StateOfRound stateOfRound;
    DeckOfCards deckOfCards;


    public void gameIsOn(){

        int index = 0;

        while (true){
            players.get(index).makeMove(stateOfRound, deckOfCards);
            if(players.get(index).hasPlayerWon()){
                System.out.println("Game is finished");
                break;
            }
            if(stateOfRound.getLastCardValue() == CardValue.KING && stateOfRound.getPossibleNextColour().get(0) == CardColour.SPADES)
                index = (index + players.size() - 1)%players.size();
            else
                index = (index + 1)%players.size();
        }
    }

    public void initializeGame(){
        this.deckOfCards = new DeckOfCards();
        dealCards();
        Card card = deckOfCards.drawRandomCard();
        while(card.getClass()!=Card.class){
            deckOfCards.deckOfCards.add(card);
            card = deckOfCards.drawRandomCard();
        }
        deckOfCards.stack.addCard(card);
    }
    public void dealCards(){
        for(Player player:players){
            for(int i = 0; i<5; i++){
                Card card = deckOfCards.drawLastCard();
                player.hand.cardsInHand.add(card);
            }
        }
    }
}
