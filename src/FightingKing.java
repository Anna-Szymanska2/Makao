import java.util.ArrayList;

public class FightingKing extends Card{
    public FightingKing(CardColour cardColour, CardValue cardValue){
        super(cardColour, cardValue);
    }

    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.KING);}});
       // stateOfRound.setPossibleNextColour(new ArrayList<>() {{add(CardColour.SPADES); add(CardColour.HEARTS);}});
        stateOfRound.setCardsToDraw(stateOfRound.getCardsToDraw() + 5);
    }
}
