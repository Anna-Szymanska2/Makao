package makao.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import makao.model.cards.*;
import makao.model.game.*;
import makao.server.Client;
import makao.server.ClientMessage;
import makao.server.ServerMessage;
import makao.view.Main;


import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

public class GameController implements Initializable, AceListener, JackListener, Serializable {
    @FXML
    private transient ImageView thisPlayerView;
    @FXML
    private transient Label playerTurnLabel1;
    @FXML
    private transient Label thisPlayerNick;
    @FXML
    private transient Label playerNick1;
    @FXML
    private transient ImageView playerView1;
    @FXML
    private transient Label playerTurnLabel2;
    @FXML
    private transient Label playerNick2;
    @FXML
    private transient ImageView playerView2;
    @FXML
    private transient Label playerTurnLabel3;
    @FXML
    private transient Label playerNick3;
    @FXML
    private transient ImageView playerView3;
    @FXML
    private transient VBox avatarsVBox;
    @FXML
    private transient Label thisPlayerTurnLabel;
    //private Game game;
    private DeckOfCards deckOfCards;
    private StateOfRound stateOfRound;
    private Player player;
    private final int maxNumberOfCardsInRow = 25;
    private final int maxNumberOfSelectedCards = 4;
    @FXML
    transient private HBox bottomRowCardsHBox;
    @FXML
    transient private HBox upRowCardsHBox;
    @FXML
    transient private HBox selectedCardsHBox;

    @FXML
    transient private ImageView stackCardImageView;
    @FXML
    transient private ImageView deckView;
    @FXML
    transient private ImageView drewCardView;
    @FXML
    transient private Button playCardsButton;
    transient private Timer timer;
    @FXML
    transient private Label cardsToDrawLabel;
    @FXML
    transient private Label chosenColorLabel;
    @FXML
    transient private Label requestedValueLabel;
    @FXML
    transient private Label roundsToWaitLabel;
    @FXML
    transient private Button waitRoundsButton;
    @FXML
    transient private Label timerLabel;
    transient private Alert drawCardAlert;
    @FXML
    transient private Label nickLabel;

    transient private ChoiceDialog choiceDialog;
    private int selectedCardsIndex;
    private int lastSelectedCardIndex;
    transient private ImageView chosenCardView;
    private boolean isThisPlayerRound;
    private int timeOfRound;
    private int[] playerCardsIndexes = new int[2];
    private boolean wasSelectedCardFromBottomRow = true;
    transient private Client client;
    transient private Label[] turnLabels;
    transient private ArrayList<String> playersNames = new ArrayList<>();
    transient private Label[] nickLabels;
    @FXML
    transient private AnchorPane gamePane;




    /*public HelloController(Client client){
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
        player = maciej;*/

    //}
    public GameController(){
        /*Player maciej = new Player("Maciej");
        Player agata = new Player("Agata");
        Player kuba = new Player("Kuba");
        ArrayList<Player> players = new ArrayList<>();
        players.add(maciej);
        players.add(agata);
        players.add(kuba);
        game = new Game(players);
        game.initializeGame();
        player = maciej;*/
    }
    public static void main(String []arg){


    }

    public void setClient(Client client) {
        this.client = client;
        client.setGameController(this);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nickLabels = new Label[]{playerNick1, playerNick2, playerNick3};
        turnLabels = new Label[]{playerTurnLabel1, playerTurnLabel2, playerTurnLabel3};
        isThisPlayerRound = false;
        initSelectedCardHBox();
        waitRoundsButton.setVisible(false);
        initCardsInHandHBox(bottomRowCardsHBox);
        initCardsInHandHBox(upRowCardsHBox);
        playCardsButton.setOnAction((event -> {
            if(isThisPlayerRound){
                tryToPlayCards();
            }
        }));
        deckView.setOnMouseClicked((mouseEvent) -> {
            if(isThisPlayerRound){
                drawCard();
            }
        });
    }

    public void init(ServerMessage msgFromServer, String name){
        player = new Player(name);
        player.setHand(msgFromServer.getNewHand());

        // player.setListener(this);
        timeOfRound = msgFromServer.getStateOfRound().getTimeOfRound();
       // player.setListener(this);
        //stateOfRound = msgFromServer.getStateOfRound();
       // deckOfCards = msgFromServer.getDeckOfCards();
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
                //updateStateOfRoundLabels(stateOfRound);
                for(int i = 0; i < 12; i++){
                    avatarsVBox.getChildren().get(i).setVisible(false);
                }
                showCards();
                playerCardsIndexes[0] = 0;
                playerCardsIndexes[1] = 4;
                nickLabel.setText(name);
                Image stackImage = new Image(getClass().getResource(msgFromServer.getCardOnTopOfTheStack().getImagePath()).toExternalForm());
                stackCardImageView.setImage(stackImage);
                initAvatarsHBox(msgFromServer, name);
            }
        });


    }
    public void updateThisPlayerTurnLabel(){
        for(Label turnLabel: turnLabels){
            turnLabel.setVisible(false);
        }
        thisPlayerTurnLabel.setText("Your turn");
        thisPlayerTurnLabel.setVisible(true);
    }

    public int returnPlayerIndex(String name, ArrayList<String> names){
        int thisPlayerIndex;
        for(thisPlayerIndex = 0; thisPlayerIndex <  names.size(); thisPlayerIndex++){
            if(names.get(thisPlayerIndex).equals(name))
                break;
        }
        return thisPlayerIndex;
    }

    public void updateTurnLabels(String name){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int index = returnPlayerIndex(name, playersNames);
                for(Label turnLabel: turnLabels){
                    turnLabel.setVisible(false);
                }
                turnLabels[index].setText("is playing");
                turnLabels[index].setVisible(true);
                thisPlayerTurnLabel.setVisible(false);
            }
        });

    }

    public void initAvatarsHBox(ServerMessage msgFromServer, String name){
        thisPlayerNick.setText(name);
        thisPlayerNick.setVisible(true);
        ImageView[] avatarViews = {playerView1, playerView2, playerView3};
        ArrayList<String> avatars = new ArrayList<>();
        int thisPlayerIndex = returnPlayerIndex(name, msgFromServer.getPlayersNames());
        for(thisPlayerIndex = 0; thisPlayerIndex <  msgFromServer.getPlayersNames().size(); thisPlayerIndex++){
            if(msgFromServer.getPlayersNames().get(thisPlayerIndex).equals(name))
                break;
        }
        for(int i = thisPlayerIndex + 1;  i <  msgFromServer.getPlayersNames().size(); i++){
            playersNames.add(msgFromServer.getPlayersNames().get(i));
            avatars.add(msgFromServer.getPlayersAvatars().get(i));
        }
        for(int i = 0; i < thisPlayerIndex; i++){
            playersNames.add(msgFromServer.getPlayersNames().get(i));
            avatars.add(msgFromServer.getPlayersAvatars().get(i));
        }
        Collections.reverse(avatars);
        Collections.reverse(playersNames);
        for(int i = 0; i < avatars.size(); i++){
            Image avatar = new Image(getClass().getResource(avatars.get(i)).toExternalForm());
            avatarViews[i].setImage(avatar);
            avatarViews[i].setVisible(true);
            nickLabels[i].setText(playersNames.get(i));
            nickLabels[i].setVisible(true);
        }
        Image thisPlayerAvatar = new Image(getClass().getResource(msgFromServer.getPlayersAvatars().get(thisPlayerIndex)).toExternalForm());
        thisPlayerView.setImage(thisPlayerAvatar);
        thisPlayerView.setVisible(true);
    }

    public  void nextPlayerMove(ServerMessage msgFromServer){
        updateStateOfRoundLabels(msgFromServer.getStateOfRound());
        updateStackView(msgFromServer.getStateOfRound());
        updateTurnLabels(msgFromServer.getWhoseTurn());
    }

    public void nextThisPlayerMove(ServerMessage msgFromServer){
    //nwm czy to ptzrebne
        if(timer != null)
            timer.cancel();
        isThisPlayerRound = true;
        deckOfCards = msgFromServer.getDeckOfCards();
        stateOfRound = msgFromServer.getStateOfRound();
        player.checkStateOfRequests(stateOfRound);
        if(player.getRoundsToStay() > 0){
            player.checkStateOfWaiting(stateOfRound);
            playerWaitsInThisRound(player.getRoundsToStay());
            return;
        }

        updateStateOfRoundLabels(stateOfRound);
        updateStackView(stateOfRound);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateThisPlayerTurnLabel();
                if(stateOfRound.getRoundsToStay() > 0){
                    waitRoundsButton.setVisible(true);
                    deckView.setVisible(false);
                }
                timer = new Timer();
                timerLabel.setStyle(("-fx-text-fill: black"));

                TimerTask task = new TimerTask() {
                    int timeLeft = timeOfRound;


                    @Override
                    public void run() {

                        Platform.runLater(()->{
                            timeLeft=timeLeft-1000;
                            int minutes = (timeLeft/60000);
                            int seconds = (timeLeft/1000) % 60;
                            String seconds_string = String.format("%02d", seconds);
                            String minutes_string = String.format("%02d", minutes);
                            timerLabel.setStyle("-fx-font-family: Consolas");
                            if(timeLeft <= 10000){
                                timerLabel.setStyle(("-fx-text-fill: red; -fx-font-family: Consolas"));

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
                                endOfThisPlayerRound();



                            }

                        });


                    }
                };
                timer.scheduleAtFixedRate(task, 0, 1000);
            }
        });





    }

    public void updateStateOfRoundLabels(StateOfRound stateOfRound){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                cardsToDrawLabel.setText("Cards to draw - " + stateOfRound.getCardsToDraw());
                if(stateOfRound.getChosenColor() == null)
                    chosenColorLabel.setText("Chosen color - None");
                else
                    chosenColorLabel.setText("Chosen color - " + stateOfRound.getChosenColor());
                if(stateOfRound.getRoundsOfRequest() > 0)
                    requestedValueLabel.setText("Requested value - " + stateOfRound.getRequestedValue());
                else
                    requestedValueLabel.setText("Requested value - None");
                roundsToWaitLabel.setText("Rounds to wait - " + stateOfRound.getRoundsToStay());
            }
        });


    }
    public void updateDeckView(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                deckView.setVisible(true);
                waitRoundsButton.setVisible(false);
            }
        });


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
        Card firstCard = deckOfCards.drawLastCard();
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
        GameController controller = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
                    if(!firstCard.isPossibleToPlayCard(stateOfRound)){
                        player.getChosenCards().remove(firstCard);
                        player.takeDrewCards(null, stateOfRound, deckOfCards, controller, controller);
                        showCards();
                        updateStateOfRoundLabels(stateOfRound);
                        endOfThisPlayerRound();


                    }
                } else {
                    player.takeDrewCards(firstCard, stateOfRound, deckOfCards, controller, controller);
                    showCards();
                    updateStateOfRoundLabels(stateOfRound);
                    endOfThisPlayerRound();
                }
                drewCardView.setImage(null);
            }
        });



    }

    public void endOfGame(ServerMessage msgFromServer){

        /*Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                if(msgFromServer.getWhoseTurn().equals(player.getNick()))
                    alert.setContentText("Congratulations, you have won the game!!!");
                else
                    alert.setContentText(msgFromServer.getWhoseTurn() + " has won the game");
                alert.showAndWait();
            }
        });*/
    }

    public void changeSceneToRanking(ServerMessage msgFromServer) throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) gamePane.getScene().getWindow();
                stage.setOnCloseRequest(new EventHandler<WindowEvent>()
                {
                    public void handle(WindowEvent e){
                        System.out.print("ranking");
                        ClientMessage clientMessage = new ClientMessage(client.getName(),"DISCONNECTED");
                        client.sendMessage(clientMessage);
                        client.closeEverything(client.getSocket(),client.getIn(),client.getOut());
                        try {
                            Platform.exit();
                        }
                        catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                stage.close();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("game_ending_scene.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage.setScene(scene);
                GameEndingController gameEndingController = fxmlLoader.<GameEndingController>getController();
                gameEndingController.setClient(client);
                client.setGameEndingController(gameEndingController);
                gameEndingController.setWinnerLabel(msgFromServer.getWhoseTurn());
                gameEndingController.addRanking(msgFromServer.getRanking());
                stage.show();
            }
        });

    }

    public void changeSceneToQuit() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) gamePane.getScene().getWindow();
                stage.setOnCloseRequest(new EventHandler<WindowEvent>()
                {
                    public void handle(WindowEvent e){
                        System.out.print("quit");
                        ClientMessage clientMessage = new ClientMessage(client.getName(),"DISCONNECTED");
                        client.sendMessage(clientMessage);
                        client.closeEverything(client.getSocket(),client.getIn(),client.getOut());
                        try {
                            Platform.exit();
                        }
                        catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                stage.close();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("game_quit_scene.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage.setScene(scene);
                GameQuitController gameQuitController = fxmlLoader.<GameQuitController>getController();
                gameQuitController.setClient(client);
                client.setGameQuitController(gameQuitController);
                stage.show();
            }
        });

    }
    public void endOfThisPlayerRound(){
        isThisPlayerRound = false;
        timer.cancel();
        showSelectedCards();;
        showCards();
        ClientMessage clientMessage;
        if(player.hasPlayerWon()){
             clientMessage = new ClientMessage(client.getName(),stateOfRound,"WIN", deckOfCards);
        }
        else {
             clientMessage = new ClientMessage(client.getName(),stateOfRound,"END", deckOfCards);
        }
        if(!client.getSocket().isClosed())
            client.sendMessage(clientMessage);
    }
    public void tryToPlayCards(){
        if(player.areChosenCardsCorrect(stateOfRound)){
            player.playChosenCards(stateOfRound, deckOfCards);
            showCards();;
            showSelectedCards();
            updateStackView(stateOfRound);
            updateStateOfRoundLabels(stateOfRound);
            updateDeckView();
            //end of round, send message
            //endOfThisPlayerRound();
            endOfThisPlayerRound();

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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Card card = stateOfRound.getLastCard();
                Image image = new Image(getClass().getResource(card.getImagePath()).toExternalForm());
                stackCardImageView.setImage(image);
            }
        });

    }

    public void showSelectedCards(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                int chosenCardsNumber = player.getChosenCards().size();
                ArrayList<Card> cards = player.getChosenCards();
                for (int i = 0; i < chosenCardsNumber; i++) {
                    Card card = cards.get(i);
                    setView(card, selectedCardsHBox, i);

                }
                clearViews(chosenCardsNumber, maxNumberOfSelectedCards, selectedCardsHBox);
            }
        });


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
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
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
        });

    }
    public void waitRounds(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                player.waitRounds(stateOfRound);
                updateDeckView();
                endOfThisPlayerRound();
            }
        });

        // end of round

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


    public void playerWaitsInThisRound(int roundsToStay) {
        GameController controller = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("You wait in this round. Rounds to wait left: " + roundsToStay);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(controller.stackCardImageView.getScene().getWindow());
                alert.show();
                //timer.cancel();
                //end of round
                endOfThisPlayerRound();
            }
        });

    }
}