import java.util.ArrayList;

public class FourCard extends Card{
    public FourCard(CardColour cardColour, CardValue cardValue){
        super(cardColour, cardValue);
    }

    @Override
    public void playCard(StateOfRound stateOfRound){
        super.playCard(stateOfRound);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.FOUR);}});
        stateOfRound.setRoundsToStay(stateOfRound.getRoundsToStay() + 1);
    }
}
