package makao.server;

import makao.controller.*;
import makao.model.cards.Card;
import makao.model.cards.CardValue;
import makao.model.game.DeckOfCards;
import makao.model.game.Hand;
import makao.model.game.StateOfRound;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client implements Serializable{

    transient private Socket socket;
    transient private ObjectOutputStream out;
    transient private ObjectInputStream in;
    transient private GameController gameController;
    transient private RegisterController registerController;
    transient private LoggingController loggingController;
    transient private ChoosingRoomController choosingRoomController;
    transient private RoomController roomController;
    transient private GameEndingController gameEndingController;
    transient private GameQuitController gameQuitController;
    private String name;
    private String path;
    private String password;
    private boolean gameIsOn = false;
    private boolean turnIsOn = false;
    private boolean gameClosed = false;
    Hand hand = new Hand();
    ArrayList<Card> chosenCards = new ArrayList<>();
    int roundsToStay = 0;

    public Client(Socket socket, String name, String password) {
        this.socket = socket;
        this.name = name;
        this.password = password;
        try{
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        }catch  (IOException e) {
        closeEverything(socket, in, out);
    }

    }

    public Client(Socket socket, String name, String password, String path) {
        this.socket = socket;
        this.name = name;
        this.password = password;
        this.path = path;
        try{
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        }catch  (IOException e) {
            closeEverything(socket, in, out);
        }

    }

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

    public String getPath() {
        return path;
    }

    public void setGameEndingController(GameEndingController gameEndingController) {
        this.gameEndingController = gameEndingController;
    }

    public void setChoosingRoomController(ChoosingRoomController choosingRoomController) {
        this.choosingRoomController = choosingRoomController;
    }

    public void setLoggingController(LoggingController loggingController) {
        this.loggingController = loggingController;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getPassword() {
        return password;
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

                while (!socket.isClosed()){
                    try{
                        /*if(gameClosed){
                            System.out.println("test");
                            closeEverything(socket, in, out);
                            return;
                        }*/
                        messageFromServer = (ServerMessage) in.readObject();

                        switch(messageFromServer.getActionID()){
                            case "WELCOME":
                                System.out.println("Welcome, the game is starting soon ");
                                break;
                            case "INIT":
                                System.out.println("INIT");
                                roomController.changeScene(messageFromServer, name);
                                //gameController.init(messageFromServer, name);
                                break;
                            case "END":
                                //controller.endOfGame(messageFromServer);
                                gameController.changeSceneToRanking(messageFromServer);
                                //zakomentowane bo poki działa w petli jest dosyć mordercze
                                if(!name.equals(messageFromServer.getWhoseTurn()))
                                    System.out.println(messageFromServer.getWhoseTurn() + " has won the game");
                                else
                                    System.out.println("Congratulations, you have won the game!!!");
                                gameIsOn = false;
                                break;
                            case "REGISTER_OK":
                                registerController.registerOK();
                                break;
                            case "REGISTER_WRONG":
                                registerController.registerFailed();
                                break;
                            case "LOGIN_OK":
                                loggingController.loginCorrect();
                                break;
                            case "LOGIN_WRONG":
                                loggingController.loginFailed();
                                break;
                            case "ROOM_STARTED":
                            case "ROOM_JOINED":
                                choosingRoomController.changeToWaitingScene(messageFromServer.getCode());
                                Thread.sleep(100);
                                break;
                            /*case "GAME_ALREADY_STATED":
                                break;*/
                            case "GAME_NOT_EXISTS":
                                choosingRoomController.wrongRoom();
                                break;
                            case "GAME_EXITED":
                                gameController.changeSceneToQuit();
                                break;
                            case "DEFAULT":
                                System.out.println("Card on top of the stack: " + messageFromServer.getCardOnTopOfTheStack().toString());
                                /*if(hand.getCardCount() == 0)
                                    hand = messageFromServer.getNewHand();*/
                                if(messageFromServer.getWhoseTurn().equals(name))
                                    gameController.nextThisPlayerMove(messageFromServer);
                                else{
                                    System.out.println(messageFromServer.getWhoseTurn() + " is making their move");
                                    gameController.nextPlayerMove(messageFromServer);
                                }


                                break;
                        }
                    }catch (IOException e){
                        closeEverything(socket, in, out);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
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
    public void makeMove(StateOfRound stateOfRound, DeckOfCards deckOfCards){
        System.out.println("Its your turn");
        hand.displayCardsInHand();
        if(stateOfRound.getRoundsOfRequest() > 0)
            stateOfRound.setRoundsOfRequest(stateOfRound.getRoundsOfRequest() - 1);

        if(getRoundsToStay() > 0){
            setRoundsToStay(getRoundsToStay() - 1);
            System.out.println("This player waits in this round");
            ClientMessage clientMessage = new ClientMessage(name,stateOfRound,"END", deckOfCards);
            sendMessage(clientMessage);
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
                    waitRounds(stateOfRound,deckOfCards);
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
                    ClientMessage clientMessage = new ClientMessage(name,stateOfRound,"END", deckOfCards);
                    sendMessage(clientMessage);
                    break;
                }
            }
        }

    }
  public void drawCard(StateOfRound stateOfRound, DeckOfCards deckOfCards){
        Scanner scanner = new Scanner(System.in);
        Card firstCard = deckOfCards.drawLastCard();
        System.out.println(firstCard.toString());
        System.out.println("Choose action 1-play this card, 2-don't play this card");
        int chosenNumber = scanner.nextInt();
        int cardsToDraw = stateOfRound.getCardsToDraw();
        if(chosenNumber == 1){
            if(firstCard.isPossibleToPlayCard(stateOfRound)) {
                firstCard.playCard(stateOfRound, deckOfCards.stack);
            }
            else{
                System.out.println("you can't use this card");
                hand.addCard(firstCard);
                for(int i = 0; i < cardsToDraw -1; i++)
                    hand.addCard(deckOfCards.drawLastCard());
                stateOfRound.setCardsToDraw(0);
                stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});

                /*if(lastCard.getClass() == FightingKing.class)
                    stateOfRound.setPossibleNextColour(new ArrayList<>() {{add(lastCard.getCardColour());}});*/
            }

        }
        else{
            hand.addCard(firstCard);
            for(int i = 0; i < cardsToDraw -1; i++)
                hand.addCard(deckOfCards.drawLastCard());
            stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
            /*if(lastCard.getClass() == FightingKing.class)
                stateOfRound.setPossibleNextColour(new ArrayList<>() {{add(lastCard.getCardColour());}});*/
            stateOfRound.setCardsToDraw(0);

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

//    public void displayCardsInHand(){
//        for(int i = 0; i < cardsInHand.size(); i++){
//            System.out.println((i+1) + ". " + cardsInHand.get(i).toString());
//        }
//    }

    public void playChosenCards(StateOfRound stateOfRound, DeckOfCards deckOfCards){
        Card lastCard = chosenCards.get(chosenCards.size() - 1);
        boolean isJackOrAce = false;
        if(lastCard.getCardValue() == CardValue.JACK || lastCard.getCardValue() == CardValue.ACE)
            isJackOrAce = true;

        if(isJackOrAce) {
            for (Card card : chosenCards) {
                if(!card.equals(lastCard))
                    deckOfCards.stack.addCard(card);
                hand.removeCard(card);
            }
            lastCard.playCard(stateOfRound, deckOfCards.stack);
        }
        else{
            for (Card card : chosenCards) {
                card.playCard(stateOfRound, deckOfCards.stack);
                hand.removeCard(card);
            }
        }
        chosenCards.clear();
        ClientMessage clientMessage = new ClientMessage(name,stateOfRound,"END", deckOfCards);
        sendMessage(clientMessage);
    }

    public boolean isChoosingCardsInProgress(StateOfRound stateOfRound, DeckOfCards deckOfCards){
        chooseCards();
        if(areChosenCardsCorrect(stateOfRound)){
            playChosenCards(stateOfRound,deckOfCards);
            return false;
        }
        else{
            System.out.println("you cannot play those cards");
            chosenCards.clear();
        }
        return true;
    }

    public void waitRounds(StateOfRound stateOfRound, DeckOfCards deckOfCards){
        setRoundsToStay(stateOfRound.getRoundsToStay() - 1);
        stateOfRound.setRoundsToStay(0);
        stateOfRound.setPossibleNextCards(new ArrayList<>() {{add(CardValue.ANYCARD);}});
        ClientMessage clientMessage = new ClientMessage(name,stateOfRound,"END", deckOfCards);
        sendMessage(clientMessage);

    }

    public int getRoundsToStay() {
        return roundsToStay;
    }

    public String getName() {
        return name;
    }

    public void setRoundsToStay(int roundsToStay) {
        this.roundsToStay = roundsToStay;
    }

    public void setRegisterController(RegisterController registerController) {
        this.registerController = registerController;
    }

    public void setRoomController(RoomController roomController) {
        this.roomController = roomController;
    }

    public void setGameQuitController(GameQuitController gameQuitController) {
        this.gameQuitController = gameQuitController;
    }
    /* public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String name = scanner.nextLine();
        Socket socket = new Socket("localhost", 4444);
        Client client = new Client(socket, name);
        ClientMessage clientMessage = new ClientMessage(client.name);
        client.sendMessage(clientMessage);
        client.listenForMessage();
    }*/

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
    public void setGameClosed(boolean gameClosed) {
        this.gameClosed = gameClosed;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }
}
