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
}
