import java.util.ArrayList;

public class QueenCard extends Card{
    public QueenCard(CardColour cardColour, CardValue cardValue){
        super(cardColour, cardValue);
    }
    @Override
    public void playCard(StateOfRound stateOfRound, Stack stack){
        super.playCard(stateOfRound, stack);
        stateOfRound.setPossibleNextColour(new ArrayList<>() {{add(CardColour.ANYCOLOUR);}});
    }
    @Override
    public boolean isPossibleToPlayCard(StateOfRound stateOfRound){
        ArrayList<CardValue> possibleNextCards = stateOfRound.getPossibleNextCards();
        if(possibleNextCards.get(0) != CardValue.ANYCARD){
            for (CardValue possibleNextCard : possibleNextCards) {
                if (possibleNextCard == getCardValue()) {
                    break;
                }
                return false;
            }
        }
        return true;
    }

}
