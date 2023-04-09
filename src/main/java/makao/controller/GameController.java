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

/**
 * GameController is the main class responsible for controlling the game logic and communication between the server and the client.
 * It implements Initializable, AceListener, JackListener, and Serializable interfaces.
 *
 */
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
    private boolean isThisPlayerRound;
    private int timeOfRound;
    private boolean wasSelectedCardFromBottomRow = true;
    transient private Client client;
    transient private Label[] turnLabels;
    transient private ArrayList<String> playersNames = new ArrayList<>();
    transient private Label[] nickLabels;
    @FXML
    transient private AnchorPane gamePane;


    public void setClient(Client client) {
        this.client = client;
        client.setGameController(this);
    }

    /**
     * Initializes the game controller
     *
     * @param url
     * @param resourceBundle
     */
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

    /**
     * Initializes the game by setting up the player's information and updating the UI elements.
     *
     * @param msgFromServer The message received from the server containing information about the game state.
     * @param name The name of the current player.
     */
    public void init(ServerMessage msgFromServer, String name){
        player = new Player(name);
        player.setHand(msgFromServer.getNewHand());
        timeOfRound = msgFromServer.getStateOfRound().getTimeOfRound();

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
                for(int i = 0; i < 12; i++){
                    avatarsVBox.getChildren().get(i).setVisible(false);
                }
                showCards();
                nickLabel.setText(name);
                Image stackImage = new Image(getClass().getResource(msgFromServer.getCardOnTopOfTheStack().getImagePath()).toExternalForm());
                stackCardImageView.setImage(stackImage);
                initAvatarsHBox(msgFromServer, name);
            }
        });
    }

    /**
     * Updates the label indicating that it is the current player's turn.
     */
    public void updateThisPlayerTurnLabel(){
        for(Label turnLabel: turnLabels){
            turnLabel.setVisible(false);
        }
        thisPlayerTurnLabel.setText("Your turn");
        thisPlayerTurnLabel.setVisible(true);
    }

    /**
     * Returns the index of the current player in the list of player names.
     *
     * @param name The name of the current player.
     * @param names The list of player names.
     * @return The index of the current player in the list of player names.
     */
    public int returnPlayerIndex(String name, ArrayList<String> names){
        int thisPlayerIndex;
        for(thisPlayerIndex = 0; thisPlayerIndex <  names.size(); thisPlayerIndex++){
            if(names.get(thisPlayerIndex).equals(name))
                break;
        }
        return thisPlayerIndex;
    }

    /**
     * Updates the turn labels to indicate whose turn it is.
     *
     * @param name The name of the player whose turn it is.
     */
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

    /**
     * Initializes the avatars for the players in the game.
     *
     * @param msgFromServer The message from the server containing information about the players in the game.
     * @param name The name of the player using this client.
     */
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

    /**
     * Updates the game state for the next player's move.
     * @param msgFromServer The message from the server containing information about the current game state.
     */
    public  void nextPlayerMove(ServerMessage msgFromServer){
        updateStateOfRoundLabels(msgFromServer.getStateOfRound());
        updateStackView(msgFromServer.getStateOfRound());
        updateTurnLabels(msgFromServer.getWhoseTurn());
    }

    /**
     * Updates the game state for the next move of the player using this client.
     * @param msgFromServer The message from the server containing information about the current game state.
     */
    public void nextThisPlayerMove(ServerMessage msgFromServer){
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

    /**
     * Updates the state of round labels by setting the text of the "cards to draw" label to the number of
     * cards to draw, the "chosen color" label to the chosen color, the "requested value" label to the requested value,
     * and the "rounds to wait" label to the number of rounds to wait.
     *
     * @param stateOfRound the current state of the round
     */
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

    /**
     * Updates the deck view by making it visible and hiding the "wait rounds" button.
     */
    public void updateDeckView(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                deckView.setVisible(true);
                waitRoundsButton.setVisible(false);
            }
        });
    }

    /**
     * Initializes the selected card HBox by making each ImageView in the HBox invisible,
     * and adding a mouse click event to each ImageView that calls the "chooseSelectedCard" method.
     */
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

    /**
     * Initializes the cards in hand HBox by making each ImageView in the HBox invisible,
     * and adding a mouse click event to each ImageView that calls the "chooseCard" method if it is
     * the current player's round.
     * @param chosenCardView cards in the player's hand
     */
    public void chooseCard(ImageView chosenCardView ){
        int lastSelectedCardIndex = findLastChosenCardIndex(chosenCardView);
        player.addToChosen(lastSelectedCardIndex);
        showCards();
        showSelectedCards();
        if(player.getChosenCards().size() == maxNumberOfSelectedCards)
            tryToPlayCards();

    }

    /**
     * Method that allows to choose selected card.
     * @param chosenCardView ImageView of the selected card
     */
    public void chooseSelectedCard(ImageView chosenCardView){
        int number = findLastSelectedCardIndex(chosenCardView);
        player.removeFromChosen(number);
        showSelectedCards();;
        showCards();
    }

    /**
     * Method that finds the last selected card index.
     *
     * @param selectedCard ImageView of the selected card
     * @return int index of the selected card
     */
    public int findLastSelectedCardIndex(ImageView selectedCard) {
        for (int i = 0; i < maxNumberOfSelectedCards; i++) {
            if (selectedCardsHBox.getChildren().get(i) == selectedCard) {
                return i;
            }

        }
        return 100; //something went wrong
    }

    /**
     * Method that finds the last chosen card index.
     *
     * @param chosenCard ImageView of the chosen card
     * @return int index of the chosen card
     */
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

    /**
     * Method that draws first card.
     *
     * @return Card first drawn card
     */
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

    /**
     * The method is used to draw a card from the deck and update the state of the game accordingly.
     * The method updates the GUI by setting the image of the card and showing the options to the user.
     * It also updates the state of the round and the cards of the player.
     */
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

    /**
     * The method is used to change the scene to the ranking scene.
     * The method updates the GUI by closing the current window and opening the ranking scene window.
     * It also updates the state of the ranking and the client.
     * @param msgFromServer ServerMessage object which contains the ranking and winner of the game.
     * @throws IOException
     */
    public void changeSceneToRanking(ServerMessage msgFromServer) throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) gamePane.getScene().getWindow();
                stage.setOnCloseRequest(new EventHandler<WindowEvent>()
                {
                    public void handle(WindowEvent e){
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

    /**
     *  The method is used to change the scene to the quit scene.
     *  The method updates the GUI by closing the current window and opening the quit scene window.
     * @throws IOException
     */
    public void changeSceneToQuit() throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage stage = (Stage) gamePane.getScene().getWindow();
                stage.setOnCloseRequest(new EventHandler<WindowEvent>()
                {
                    public void handle(WindowEvent e){
                        //System.out.print("quit");
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

    /**
     * Ends the current player round and sends a message to the client indicating if the player won or not.
     */
    public void endOfThisPlayerRound(){
        isThisPlayerRound = false;
        if(timer != null){
            timer.cancel();
        }
        showSelectedCards();
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

    /**
     * Attempts to play the chosen cards.
     * If the cards are not allowed to be played, an alert is shown and the cards are put back.
     */
    public void tryToPlayCards(){
        if(player.areChosenCardsCorrect(stateOfRound)){
            player.playChosenCards(stateOfRound, deckOfCards);
            showCards();;
            showSelectedCards();
            updateStackView(stateOfRound);
            updateStateOfRoundLabels(stateOfRound);
            updateDeckView();
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

    /**
     * Updates the stack view to display the last card played.
     *
     * @param stateOfRound The current state of the round
     */
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

    /**
     * Shows the cards that the player has selected.
     */
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

    /**
     * Shows the cards in the hand of the player.
     * It gets the cards in hand of the player and sets the number of cards in the bottom row.
     * It clears the views for the up row and bottom row and sets the view for each card.
     */
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

    /**
     * Used to wait for the player's round.
     * It calls the waitRounds method on the player and updates the deck view.
     * It then calls the endOfThisPlayerRound method.
     */
    public void waitRounds(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                player.waitRounds(stateOfRound);
                updateDeckView();
                endOfThisPlayerRound();
            }
        });

    }

    /**
     * Called when an Ace is played
     * It creates a ChoiceDialog to ask the player what color they want and returns the chosen color.
     *
     * @param event the event that triggers the method
     * @return the chosen color
     */
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

    /**
     * The jackWasPlayed method is called when a jack card is played. It creates a choice dialog that prompts the user to select
     * a card value from a list of predefined values. The selected value is then returned.
     *
     * @param event the event that triggers this method
     * @return the selected card value
     */
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

    /**
     * The playerWaitsInThisRound method is called when a player chooses to wait during their turn. It displays an alert dialog
     * with information about how many rounds the player has to wait.
     * 
     * @param roundsToStay the number of rounds the player has to wait
     */
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
                endOfThisPlayerRound();
            }
        });

    }
}