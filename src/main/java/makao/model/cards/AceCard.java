package makao.model.cards;

import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;

import java.util.Scanner;

public class AceCard extends Card {
    public AceCard(CardColour cardColour, CardValue cardValue){
        super(cardColour, cardValue);
    }
    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextColour(chooseColour());
    }

    public CardColour chooseColour(){
        System.out.println("Choose colour (1 - Hearts, 2 - Spades, 3 - Clubs, 4 - Diamonds)");
        Scanner scanner = new Scanner(System.in);
        int chosenNumber = scanner.nextInt();
        CardColour chosenColour = null;

        switch (chosenNumber){
            case 1 -> chosenColour = CardColour.HEARTS;
            case 2 -> chosenColour = CardColour.SPADES;
            case 3 -> chosenColour = CardColour.CLUBS;
            case 4 -> chosenColour = CardColour.DIAMONDS;
        }
        return chosenColour;
    }
}
