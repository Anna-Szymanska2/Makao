package makao.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import makao.model.cards.*;
import makao.model.game.*;
import makao.server.Client;
import makao.server.ServerMessage;


import java.net.URL;
import java.util.*;

public class HelloController implements Initializable, AceListener, JackListener, WaitListener {
    private Game game;
    private Player player;
    private final int maxNumberOfCardsInRow = 25;
    private final int maxNumberOfSelectedCards = 4;
    @FXML
    private HBox bottomRowCardsHBox;
    @FXML
    private HBox upRowCardsHBox;
    @FXML
    private HBox selectedCardsHBox;

    @FXML
    private ImageView stackCardImageView;
    @FXML
    private ImageView deckView;
    @FXML
    private ImageView drewCardView;
    @FXML
    private Button playCardsButton;
    private Timer timer;
    @FXML
    private Label cardsToDrawLabel;
    @FXML
    private Label chosenColorLabel;
    @FXML
    private Label requestedValueLabel;
    @FXML
    private Label roundsToWaitLabel;
    @FXML
    private Button waitRoundsButton;
    @FXML
    private Label timerLabel;
    private Alert drawCardAlert;
    private ChoiceDialog choiceDialog;
    private int selectedCardsIndex;
    private int lastSelectedCardIndex;
    private ImageView chosenCardView;
    private boolean isThisPlayerRound = true;
    private int[] playerCardsIndexes = new int[2];
    private boolean wasSelectedCardFromBottomRow = true;
    private Client client;




    public HelloController(Client client){
        this.client = client;
        this.client.setController(this);
        Player maciej = new Player("Maciej");
        Player agata = new Player("Agata");
        Player kuba = new Player("Kuba");
        ArrayList<Player> players = new ArrayList<>();
        players.add(maciej);
        players.add(agata);
        players.add(kuba);
        game = new Game(players);
        game.initializeGame();
        player = maciej;

    }
    public HelloController(){
        Player maciej = new Player("Maciej");
        Player agata = new Player("Agata");
        Player kuba = new Player("Kuba");
        ArrayList<Player> players = new ArrayList<>();
        players.add(maciej);
        players.add(agata);
        players.add(kuba);
        game = new Game(players);
        game.initializeGame();
        player = maciej;
    }
    public static void main(String []arg){


    }

    public void setClient(Client client) {
        this.client = client;
        client.setController(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initSelectedCardHBox();
        waitRoundsButton.setVisible(false);
        initCardsInHandHBox(bottomRowCardsHBox);
        initCardsInHandHBox(upRowCardsHBox);
        deckView.setOnMouseClicked((mouseEvent) -> {
            if(isThisPlayerRound){
                drawCard();
            }
        });
    }

    public void init(ServerMessage msgFromServer, String name){
        player = new Player(name);
        player.setHand(msgFromServer.getNewHand());
        player.setListener(this);
        for(Card card: player.getCardsInHand()){
            if(card.getCardValue() == CardValue.ACE){
                AceCard cardCasted = (AceCard) card;
                cardCasted.setListener(this);
            }
            if(card.getCardValue() == CardValue.JACK){
                JackCard cardCasted = (JackCard) card;
                cardCasted.setListener(this);
            }
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateStateOfRoundLabels(msgFromServer.getStateOfRound());
                showCards();
                playerCardsIndexes[0] = 0;
                playerCardsIndexes[1] = 4;
                Image image = new Image(getClass().getResource(msgFromServer.getDeckOfCards().getStack().getLastCard().getImagePath()).toExternalForm());
                stackCardImageView.setImage(image);
            }
        });


    }

    public  void nextPlayerMove(ServerMessage msgFromServer){
        updateStateOfRoundLabels(msgFromServer.getStateOfRound());
        updateStackView(msgFromServer.getStateOfRound());
    }

    public void nextThisPlayerMove(StateOfRound stateOfRound){
        if(timer != null)
            timer.cancel();
        updateStateOfRoundLabels(game.getStateOfRound());
        player.checkStateOfRound(stateOfRound);
        if(stateOfRound.getRoundsToStay() > 0){
            waitRoundsButton.setVisible(true);
            deckView.setVisible(false);
        }
        timer = new Timer();
        timerLabel.setStyle(("-fx-text-fill: black"));

        TimerTask task = new TimerTask() {
            int timeLeft = 120000;


            @Override
            public void run() {

                Platform.runLater(()->{
                    timeLeft=timeLeft-1000;
                    int minutes = (timeLeft/60000);
                    int seconds = (timeLeft/1000) % 60;
                    String seconds_string = String.format("%02d", seconds);
                    String minutes_string = String.format("%02d", minutes);
                    if(timeLeft == 10000){
                        timerLabel.setStyle(("-fx-text-fill: red"));

                    }
                    timerLabel.setText(minutes_string+":"+seconds_string);

                    if(timeLeft == 0){
                        timer.cancel();
                        if(choiceDialog!= null) {
                            choiceDialog.close();
                        }
                        else if(drawCardAlert != null){
                            drawCardAlert.setResult(new ButtonType("Take it"));
                            drawCardAlert.close();
                        } else if (stateOfRound.getRoundsToStay() > 0) {
                            waitRounds();
                        } else{
                            Card firstCard = drawFirstCard();
                            player.addToHand(firstCard);
                        }
                        //end of round
                        showCards();
                        showSelectedCards();
                        nextThisPlayerMove(game.getStateOfRound());



                    }

                });


            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);



    }

    public void updateStateOfRoundLabels(StateOfRound stateOfRound){
        cardsToDrawLabel.setText("Cards to draw - " + stateOfRound.getCardsToDraw());
        if(stateOfRound.getChosenColor() == null)
            chosenColorLabel.setText("Chosen color - None");
        else
            chosenColorLabel.setText("Chosen color - " + stateOfRound.getChosenColor());
        if(stateOfRound.getRoundsOfRequest() > 1)
            requestedValueLabel.setText("Requested value - " + stateOfRound.getRequestedValue());
        else
            requestedValueLabel.setText("Requested value - None");
        roundsToWaitLabel.setText("Rounds to wait - " + stateOfRound.getRoundsToStay());

    }


    public void initSelectedCardHBox(){
        for(int i = 0; i <maxNumberOfSelectedCards; i++) {
            ImageView view = (ImageView) selectedCardsHBox.getChildren().get(i);
            view.setVisible(false);
            view.setOnMouseClicked((mouseEvent) -> {
                ImageView chosenCardView = (ImageView)mouseEvent.getSource();
                chooseSelectedCard(chosenCardView);

            });
        }

    }
    public void initCardsInHandHBox(HBox cardsHBox){
        for(int i = 0; i < maxNumberOfCardsInRow; i++) {
            ImageView view = (ImageView) cardsHBox.getChildren().get(i);
            view.setVisible(false);
            view.setOnMouseClicked((mouseEvent) -> {
                if(isThisPlayerRound){
                    ImageView chosenCardView = (ImageView)mouseEvent.getSource();
                    chooseCard(chosenCardView);
                }


            });
        }
    }
    public void chooseCard(ImageView chosenCardView ){
        int lastSelectedCardIndex = findLastChosenCardIndex(chosenCardView);
        player.addToChosen(lastSelectedCardIndex);
        player.displayCards();
        showCards();
        showSelectedCards();
        if(player.getChosenCards().size() == maxNumberOfSelectedCards)
            tryToPlayCards();

    }

    public void chooseSelectedCard(ImageView chosenCardView){
        int number = findLastSelectedCardIndex(chosenCardView);
        player.removeFromChosen(number);
        showSelectedCards();;
        showCards();
        player.displayCards();
    }
    public int findLastSelectedCardIndex(ImageView selectedCard) {
        for (int i = 0; i < maxNumberOfSelectedCards; i++) {
            if (selectedCardsHBox.getChildren().get(i) == selectedCard) {
                return i;
            }

        }
        return 100; //something went wrong
    }

    public int findLastChosenCardIndex(ImageView chosenCard) {
        int playerNumberOfCards = player.getNumberOfCards();
        if(playerNumberOfCards > maxNumberOfCardsInRow){
            for(int i = 0; i < playerNumberOfCards - maxNumberOfCardsInRow; i++){
                if(upRowCardsHBox.getChildren().get(i) == chosenCard){
                    wasSelectedCardFromBottomRow = false;
                    return i + maxNumberOfCardsInRow;
                }
            }
        }
        for (int i = 0; i< playerNumberOfCards; i++){
            if(bottomRowCardsHBox.getChildren().get(i) ==chosenCard){
                wasSelectedCardFromBottomRow = true;
                return i;
            }
        }
        return 100; //something went wrong then

    }
    Card drawFirstCard(){
        player.putBackChosenCards();
        showCards();
        showSelectedCards();
        Card firstCard = game.getDeckOfCards().drawLastCard();
        if(firstCard.getCardValue() == CardValue.ACE){
            AceCard firstCardCasted = (AceCard) firstCard;
            firstCardCasted.setListener(this);
        }
        if(firstCard.getCardValue() == CardValue.JACK){
            JackCard firstCardCasted = (JackCard) firstCard;
            firstCardCasted.setListener(this);
        }
        return firstCard;
    }


    public void drawCard(){
        Card firstCard = drawFirstCard();
        Image image = new Image(getClass().getResource(firstCard.getImagePath()).toExternalForm());
        drewCardView.setImage(image);
        drewCardView.setVisible(true);

        drawCardAlert = new Alert(Alert.AlertType.CONFIRMATION);
        drawCardAlert.setTitle(null);
        drawCardAlert.setHeaderText(null);
        drawCardAlert.setContentText("You drew " + firstCard + ". What do you want to do?");

        ButtonType buttonTypeOne = new ButtonType("Try to play it");
        ButtonType buttonTypeTwo = new ButtonType("Take it");

        drawCardAlert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = drawCardAlert.showAndWait();
        drawCardAlert = null;
        if (result.get() == buttonTypeOne){
            player.getChosenCards().add(firstCard);
            tryToPlayCards();
            if(!firstCard.isPossibleToPlayCard(game.getStateOfRound())){
                player.getChosenCards().remove(firstCard);
                player.takeDrewCards(null, game.getStateOfRound(), game.getDeckOfCards(), this, this);
                showCards();
                updateStateOfRoundLabels(game.getStateOfRound());
                // end of round
                //endOfThisPlayerRound();
                nextThisPlayerMove(game.getStateOfRound());


            }
        } else {
            player.takeDrewCards(firstCard, game.getStateOfRound(), game.getDeckOfCards(), this, this);
            showCards();
            updateStateOfRoundLabels(game.getStateOfRound());
            // end of round
            //endOfThisPlayerRound();
            nextThisPlayerMove(game.getStateOfRound());
        }
        drewCardView.setImage(null);


    }
    public void endOfThisPlayerRound(){
        isThisPlayerRound = false;
        timer.cancel();
        showSelectedCards();;
        showCards();
    }
    public void tryToPlayCards(){
        if(player.areChosenCardsCorrect(game.getStateOfRound())){
            player.playChosenCards(game.getStateOfRound(), game.getDeckOfCards());
            showCards();;
            showSelectedCards();
            updateStackView(game.getStateOfRound());
            updateStateOfRoundLabels(game.getStateOfRound());
            //end of round, send message
            //endOfThisPlayerRound();
            nextThisPlayerMove(game.getStateOfRound());

        }

        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Playing those cards is against the rules!");
            alert.showAndWait();
            player.putBackChosenCards();
            showSelectedCards();;
            showCards();
        }


    }

    public void updateStackView(StateOfRound stateOfRound){
        Card card = stateOfRound.getLastCard();
        Image image = new Image(getClass().getResource(card.getImagePath()).toExternalForm());
        stackCardImageView.setImage(image);
    }

    public void showSelectedCards(){
        int chosenCardsNumber = player.getChosenCards().size();
        ArrayList<Card> cards = player.getChosenCards();
        for (int i = 0; i < chosenCardsNumber; i++) {
            Card card = cards.get(i);
            setView(card, selectedCardsHBox, i);

        }
        clearViews(chosenCardsNumber, maxNumberOfSelectedCards, selectedCardsHBox);
    }

    public void clearViews(int firstView, int lastView, HBox viewsHBox){
        for(int i = firstView; i < lastView; i++){
            ImageView view = (ImageView) viewsHBox.getChildren().get(i);
            view.setImage(null);
            view.setVisible(false);
        }
    }

    public void setView(Card card, HBox viewsHBox, int index){
        Image image = new Image(getClass().getResource(card.getImagePath()).toExternalForm());
        ImageView view = (ImageView) viewsHBox.getChildren().get(index);
        view.setImage(image);
        view.setVisible(true);
    }

    public void showCards(){
        ArrayList<Card> cards = player.getCardsInHand();
        int numberOfCardsInBottomRow = 0;
        clearViews(0, maxNumberOfCardsInRow, upRowCardsHBox);
        if(player.getNumberOfCards() > maxNumberOfCardsInRow){
            numberOfCardsInBottomRow = maxNumberOfCardsInRow;
            for(int i = 0; i < player.getNumberOfCards() - maxNumberOfCardsInRow; i++){
                Card card = cards.get(i + maxNumberOfCardsInRow);
                setView(card, upRowCardsHBox, i);
            }
        }
        else
            numberOfCardsInBottomRow = player.getNumberOfCards();
        for (int i = 0; i < numberOfCardsInBottomRow; i++) {
            Card card = cards.get(i);
            setView(card, bottomRowCardsHBox, i);

        }
        clearViews(numberOfCardsInBottomRow, maxNumberOfCardsInRow, bottomRowCardsHBox);
    }
    public void waitRounds(){
        player.waitRounds(game.getStateOfRound());
        waitRoundsButton.setVisible(false);
        deckView.setVisible(true);
        // end of round
        nextThisPlayerMove(game.getStateOfRound());
    }

    @Override
    public CardColour aceWasPlayed(ActionEvent event) {
        CardColour[]  colors= { CardColour.HEARTS, CardColour.SPADES, CardColour.CLUBS, CardColour.DIAMONDS};
        choiceDialog = new ChoiceDialog(colors[0], colors);
        choiceDialog.getDialogPane().getButtonTypes().remove(1,2 );
        choiceDialog.setContentText("What color do you want?");
        choiceDialog.setTitle(null);
        choiceDialog.setHeaderText(null);
        choiceDialog.showAndWait();
        CardColour chosenColor = (CardColour) choiceDialog.getSelectedItem();
        choiceDialog = null;

        return chosenColor;
    }

    @Override
    public CardValue jackWasPlayed(ActionEvent event) {
        CardValue[]  values = { CardValue.FIVE, CardValue.SIX, CardValue.SEVEN, CardValue.EIGHT, CardValue.NINE, CardValue.TEN, CardValue.ANYCARD};
        choiceDialog = new ChoiceDialog(values[6], values);
        choiceDialog.getDialogPane().getButtonTypes().remove(1,2 );
        choiceDialog.setContentText("What is your requested value?");
        choiceDialog.setTitle(null);
        choiceDialog.setHeaderText(null);
        choiceDialog.showAndWait();
        CardValue chosenValue = (CardValue) choiceDialog.getSelectedItem();
       choiceDialog = null;

        return chosenValue;
    }

    @Override
    public void playerWaitsInThisRound(int roundsToStay) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText("You wait in this round. Rounds to wait left: " + roundsToStay);
        alert.show();
        //end of round
        nextThisPlayerMove(game.getStateOfRound());
    }
}