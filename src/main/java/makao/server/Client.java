package makao.server;

import makao.model.cards.Card;
import makao.model.cards.CardValue;
import makao.model.game.DeckOfCards;
import makao.model.game.Hand;
import makao.model.game.StateOfRound;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String name;
    private boolean gameIsOn = false;
    private boolean turnIsOn = false;
    Hand hand = new Hand();
    ArrayList<Card> chosenCards = new ArrayList<>();
    int roundsToStay = 0;

    public Client(Socket socket, String name){
        try{
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.name = name;

    }catch  (IOException e) {
        closeEverything(socket, in, out);
    }
    }

    public void sendMessage(ClientMessage clientMessage) {
        try {
            out.writeObject(clientMessage);
            out.flush();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                ServerMessage messageFromServer;

                while (socket.isConnected()){
                    try{
                        messageFromServer = (ServerMessage) in.readObject();

                        switch(messageFromServer.getActionID()){
                            case "WELCOME":
                                System.out.println("Welcome, the game is starting soon ");
                                break;
                            case "END":
                                if(name != messageFromServer.getWhoseTurn())
                                    System.out.println(messageFromServer.getWhoseTurn() + " has won the game");
                                else
                                    System.out.println("Congratulations, you have won the game!!!");
                                gameIsOn = false;
                                break;
                            case "DRAW":
                                hand = messageFromServer.getNewHand();
                                drawCard(messageFromServer.getStateOfRound());
                                break;
                            case "DEFAULT":
                                System.out.println("Card on top of the stack: " + messageFromServer.getCardOnTopOfTheStack().toString());
                                hand = messageFromServer.getNewHand();
                                if(messageFromServer.getWhoseTurn().equals(name))
                                    makeMove(messageFromServer.getStateOfRound());
                                else
                                    System.out.println(messageFromServer.getWhoseTurn() + " is making their move");
                                break;
                            case "DRAW_MORE":
                                hand = messageFromServer.getNewHand();
                                messageFromServer.getStateOfRound().setCardsToDraw(0);
                                messageFromServer.getStateOfRound().setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
                                ClientMessage clientMessage = new ClientMessage(name,messageFromServer.getStateOfRound(),"END");
                                try {
                                    out.writeObject(clientMessage);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                        }
                    }catch (IOException e){
                        closeEverything(socket, in, out);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        try{
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void readMessageFromServer(ServerMessage serverMessage){

    }
    public void makeMove(StateOfRound stateOfRound){
        System.out.println("Its your turn");
        hand.displayCardsInHand();
        if(stateOfRound.getRoundsOfRequest() > 0)
            stateOfRound.setRoundsOfRequest(stateOfRound.getRoundsOfRequest() - 1);

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
                    isChoosingCardsInProgress = isChoosingCardsInProgress(stateOfRound);
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
                    isChoosingCardsInProgress = isChoosingCardsInProgress(stateOfRound);
                }else{
                    ClientMessage clientMessage = new ClientMessage(name,stateOfRound,"DRAW");
                    try {
                        out.writeObject(clientMessage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //drawCard(stateOfRound, deckOfCards);
                    break;
                }
            }
        }

    }
    public void drawCard(StateOfRound stateOfRound){
        Scanner scanner = new Scanner(System.in);
        Card firstCard = hand.getCard(hand.getCardCount()-1);
        System.out.println(firstCard.toString());
        System.out.println("Choose action 1-play this card, 2-don't play this card");
        int chosenNumber = scanner.nextInt();
        int cardsToDraw = stateOfRound.getCardsToDraw();
        if(chosenNumber == 1){
            if(firstCard.isPossibleToPlayCard(stateOfRound)) {
                chosenCards.clear();
                chosenCards.add(firstCard);
                hand.removeCard(firstCard);
                ClientMessage clientMessage = new ClientMessage(name,stateOfRound,"PLAY",0,chosenCards);
                try {
                    out.writeObject(clientMessage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                chosenCards.clear();
            }
            else{
                System.out.println("you can't use this card");
                if(cardsToDraw - 1 <= 0){
                    stateOfRound.setCardsToDraw(0);
                    stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
                    ClientMessage clientMessage = new ClientMessage(name,stateOfRound,"END");
                    try {
                        out.writeObject(clientMessage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    stateOfRound.setCardsToDraw(cardsToDraw-1);
                    stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
                    ClientMessage clientMessage = new ClientMessage(name, stateOfRound, "DRAW_MORE");
                    try {
                        out.writeObject(clientMessage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
//                for(int i = 0; i < cardsToDraw -1; i++)
//                    hand.addCard(deckOfCards.drawLastCard());
//                stateOfRound.setCardsToDraw(0);
//                stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
            }

        }
        else{
            {
                stateOfRound.setCardsToDraw(cardsToDraw-1);
                ClientMessage clientMessage = new ClientMessage(name, stateOfRound, "DRAW_MORE");
                try {
                    out.writeObject(clientMessage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

    boolean areChosenCardsCorrect(StateOfRound stateOfRound){
        if(chosenCards.size() == 0)
            return false;
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
            System.out.println("Give number of card that you want to choose or press q to play all chosen cards");
            hand.displayCardsInHand();
            String answer = scanner.next();
            if(!answer.equals("q"))
                chosenCards.add(hand.getCard(Integer.parseInt(answer)-1));
            else
                break;
        }
    }

    public void playChosenCards(StateOfRound stateOfRound){
        for (Card card : chosenCards) {
            hand.removeCard(card);
        }
        ClientMessage clientMessage = new ClientMessage(name,stateOfRound,"PLAY",0,chosenCards);
        try {
            out.writeObject(clientMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        chosenCards.clear();
    }

    public boolean isChoosingCardsInProgress(StateOfRound stateOfRound){
        chooseCards();
        if(areChosenCardsCorrect(stateOfRound)){
            playChosenCards(stateOfRound);
            return false;
        }
        else{
            System.out.println("you cannot play those cards");
            chosenCards.clear();
        }
        return true;
    }

    public void waitRounds(StateOfRound stateOfRound){
        setRoundsToStay(stateOfRound.getRoundsToStay() - 1);
        stateOfRound.setRoundsToStay(0);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
        ClientMessage clientMessage = new ClientMessage(name,stateOfRound,"WAIT");
        try {
            out.writeObject(clientMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public int getRoundsToStay() {
        return roundsToStay;
    }

    public void setRoundsToStay(int roundsToStay) {
        this.roundsToStay = roundsToStay;
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String name = scanner.nextLine();
        Socket socket = new Socket("localhost", 4444);
        Client client = new Client(socket, name);
        ClientMessage clientMessage = new ClientMessage(client.name);
        client.sendMessage(clientMessage);
        client.listenForMessage();
    }
}
