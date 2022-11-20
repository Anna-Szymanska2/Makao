import java.util.ArrayList;

public class TwoOrThreeCard extends Card{

    public TwoOrThreeCard(CardColour cardColour, CardValue cardValue){
        super(cardColour, cardValue);
    }
    @Override
    public void playCard(StateOfRound stateOfRound){
        super.playCard(stateOfRound);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.THREE); add( CardValue.TWO);}});
        stateOfRound.setCardsToDraw(stateOfRound.getCardsToDraw() + getCardValue().getValueOfCard());
    }
}

