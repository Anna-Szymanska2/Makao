import java.util.ArrayList;
import java.util.Scanner;

public class Player {
    Hand hand = new Hand();
    //ArrayList<Card> cardsInHand = new ArrayList<>();
    ArrayList<Card> chosenCards = new ArrayList<>();
    int roundsToStay = 0;
    String nick;

    Player(String nick){
        this.nick = nick;
    }

    public void setRoundsToStay(int roundsToStay) {
        this.roundsToStay = roundsToStay;
    }

    public int getRoundsToStay() {
        return roundsToStay;
    }

    public void makeMove(StateOfRound stateOfRound, DeckOfCards deckOfCards){
        greetPlayer();
        hand.displayCardsInHand();
        if(getRoundsToStay() > 0){
            setRoundsToStay(getRoundsToStay() - 1);
            System.out.println("This player waits in this round");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        boolean isChoosingCardsInProgress = true;
        if(stateOfRound.getRoundsToStay() != 0){
            while(isChoosingCardsInProgress){
                System.out.println("Choose action 1-play card(s), 2-wait round(s)");
                int chosenNumber = scanner.nextInt();
                if(chosenNumber == 1){
                    isChoosingCardsInProgress = isChoosingCardsInProgress(stateOfRound,deckOfCards);
                }else{
                    waitRounds(stateOfRound);
                    break;
                }
            }
        }else{
            while(isChoosingCardsInProgress){
                System.out.println("Choose action 1-play card(s), 2-draw card(s)");
                int chosenNumber = scanner.nextInt();
                if(chosenNumber == 1){
                    isChoosingCardsInProgress = isChoosingCardsInProgress(stateOfRound,deckOfCards);
                }else{
                    drawCard(stateOfRound, deckOfCards);
                    break;
                }
            }
        }

    }

    boolean areChosenCardsCorrect(StateOfRound stateOfRound){
        CardValue cardValue = chosenCards.get(0).getCardValue();
        for(int i = 1; i < chosenCards.size(); i++){
            if(chosenCards.get(i).getCardValue() != cardValue)
                return false;
        }
        return chosenCards.get(0).isPossibleToPlayCard(stateOfRound);
    }
    public void chooseCards(){
        Scanner scanner = new Scanner(System.in);
        while (true){
            System.out.println("Give number of card that you want to choose or press q to play all cards");
            hand.displayCardsInHand();
            String answer = scanner.next();
            if(!answer.equals("q"))
                chosenCards.add(hand.getCard(Integer.parseInt(answer)-1));
            else
                break;
        }
    }

//    public void displayCardsInHand(){
//        for(int i = 0; i < cardsInHand.size(); i++){
//            System.out.println((i+1) + ". " + cardsInHand.get(i).toString());
//        }
//    }

    public void playChosenCards(StateOfRound stateOfRound, DeckOfCards deckOfCards){
        for (Card card : chosenCards) {
            card.playCard(stateOfRound);
            hand.removeCard(card);
            deckOfCards.stack.addCard(card);
        }
        chosenCards.clear();
    }

    public boolean isChoosingCardsInProgress(StateOfRound stateOfRound, DeckOfCards deckOfCards){
        chooseCards();
        if(areChosenCardsCorrect(stateOfRound)){
            playChosenCards(stateOfRound,deckOfCards);
            return false;
        }
        else
            System.out.println("you cannot play those cards");
        return true;
    }

    public void drawCard(StateOfRound stateOfRound, DeckOfCards deckOfCards){
        Scanner scanner = new Scanner(System.in);
        Card firstCard = deckOfCards.drawLastCard();
        System.out.println("Choose action 1-play this card, 2-don't play this card");
        int chosenNumber = scanner.nextInt();
        if(chosenNumber == 1){
            if(firstCard.isPossibleToPlayCard(stateOfRound))
                firstCard.playCard(stateOfRound);
            else{
                System.out.println("you can't use this card");
                hand.addCard(firstCard);
            }

        }
        else{
            int cardsToDraw = stateOfRound.getCardsToDraw();
            for(int i = 0; i < cardsToDraw -1; i++)
                hand.addCard(deckOfCards.drawLastCard());
            stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
            stateOfRound.setCardsToDraw(0);
        }


    }

    public void waitRounds(StateOfRound stateOfRound){
        setRoundsToStay(stateOfRound.getRoundsToStay() - 1);
        stateOfRound.setRoundsToStay(0);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});

    }
    public boolean hasPlayerWon(){
        return hand.getCardCount() == 0;
    }

    public void greetPlayer(){
        System.out.println("Hi " + nick + " those are your cards");
    }



}
