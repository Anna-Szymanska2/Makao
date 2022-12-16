package makao.model.cards;

import  makao.model.game.Stack;
import  makao.model.game.StateOfRound;

import java.util.ArrayList;
import java.util.Scanner;

public class JackCard extends Card {
    public JackCard(CardColour cardColour, CardValue cardValue){
        super(cardColour, cardValue);
    }

    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        CardValue chosenValue = chooseValue();
        if(chosenValue != CardValue.ANYCARD){
            stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.JACK);add(chosenValue);}});
            stateOfRound.setRoundsOfRequest(stateOfRound.getPlayers() + 1);
            stateOfRound.setRequestedValue(chosenValue);
            stateOfRound.setPossibleNextColour(CardColour.ANYCOLOUR);
        }
    }
    public CardValue chooseValue(){
        System.out.println("Choose card value (1 - five, 2 - six, 3 - seven, 4 - eight, 5 - nine, 6 - ten, 7 - no request)");
        Scanner scanner = new Scanner(System.in);
        int chosenNumber = scanner.nextInt();
        CardValue chosenValue = null;

        switch (chosenNumber){
            case 1 -> chosenValue = CardValue.FIVE;
            case 2 -> chosenValue = CardValue.SIX;
            case 3 -> chosenValue = CardValue.SEVEN;
            case 4 -> chosenValue = CardValue.EIGHT;
            case 5 -> chosenValue = CardValue.NINE;
            case 6 -> chosenValue = CardValue.TEN;
            case 7 -> chosenValue = CardValue.ANYCARD;
        }
        return chosenValue;
    }
}
