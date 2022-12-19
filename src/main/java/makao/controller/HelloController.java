package makao.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import makao.model.cards.Card;
import makao.model.game.Game;
import makao.model.game.Player;
import makao.model.game.StateOfRound;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
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
    private int selectedCardsIndex;
    private int lastSelectedCardIndex;
    private ImageView chosenCardView;
    private int[] playerCardsIndexes = new int[2];
    private boolean wasSelectedCardFromBottomRow = true;


    public static void main(String []arg){

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCardsInHandHBox(bottomRowCardsHBox);
        initCardsInHandHBox(upRowCardsHBox);
        initSelectedCardHBox();
        /*ArrayList<Card> cards = player.getCardsInHand();
        for (int i = 0; i < 5; i++) {
            Card card = cards.get(i);
            Image image = new Image(getClass().getResource(card.getImagePath()).toExternalForm());
            ImageView view = (ImageView) bottomRowCardsHBox.getChildren().get(i);
            //ImageView view = (ImageView) upRowCardsHBox.getChildren().get(i);
            view.setImage(image);
            view.setVisible(true);

        }*/
        showCards();
        playerCardsIndexes[0] = 0;
        playerCardsIndexes[1] = 4;
        Image image = new Image(getClass().getResource(game.getStack().getLastCard().getImagePath()).toExternalForm());
        stackCardImageView.setImage(image);

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
                ImageView chosenCardView = (ImageView)mouseEvent.getSource();
                chooseCard(chosenCardView);

            });
        }
    }
    public void chooseCard(ImageView chosenCardView ){
        ImageView view = (ImageView)selectedCardsHBox.getChildren().get(selectedCardsIndex);
        int lastSelectedCardIndex = findLastChosenCardIndex(chosenCardView);
        swapCardsInViews(chosenCardView, view);
        selectedCardsIndex++;
        player.addToChosen(lastSelectedCardIndex);
        player.displayCards();
        showCards();
        if(selectedCardsIndex == maxNumberOfSelectedCards)
            tryToPlayCards();

    }

    public void swapCardsInViews(ImageView sourceView, ImageView destinationView){
        destinationView.setImage(sourceView.getImage());
        destinationView.setVisible(true);
        sourceView.setImage(null);
        sourceView.setVisible(false);
        sourceView.toFront();
    }
    public void chooseSelectedCard(ImageView chosenCardView){
        int playerNumberOfCards = player.getNumberOfCards();
        ImageView firstFreeCardView;
        if(playerNumberOfCards >= maxNumberOfCardsInRow)
            firstFreeCardView = (ImageView) upRowCardsHBox.getChildren().get(playerNumberOfCards - maxNumberOfCardsInRow);
        else
            firstFreeCardView = (ImageView) bottomRowCardsHBox.getChildren().get(playerNumberOfCards);
        int number = findLastSelectedCardIndex(chosenCardView);
        player.removeFromChosen(number);
        swapCardsInViews(chosenCardView, firstFreeCardView);
        selectedCardsIndex--;
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

    public void drawCard(){
        putBackSelectedCards();
        Card firstCard = game.getDeckOfCards().drawLastCard();
        Image image = new Image(getClass().getResource(firstCard.getImagePath()).toExternalForm());
        drewCardView.setImage(image);
        drewCardView.setVisible(true);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText("You drew " + firstCard + ". What do you want to do?");

        ButtonType buttonTypeOne = new ButtonType("Try to play it");
        ButtonType buttonTypeTwo = new ButtonType("Take it");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            player.getChosenCards().add(firstCard);
            tryToPlayCards();
            if(!firstCard.isPossibleToPlayCard(game.getStateOfRound())){
                player.getChosenCards().remove(firstCard);
                player.takeDrewCards(firstCard, game.getStateOfRound(), game.getDeckOfCards());
                showCards();

            }
        } else {
            player.takeDrewCards(firstCard, game.getStateOfRound(), game.getDeckOfCards());
            showCards();
        }
        drewCardView.setImage(null);



    }
    public void putBackSelectedCards(){
        int numberOfSelectedCards = selectedCardsIndex;
        for(int i = 0; i < numberOfSelectedCards; i++){
            ImageView view = (ImageView) selectedCardsHBox.getChildren().get(0);
            chooseSelectedCard(view);
        }
        selectedCardsIndex = 0;

    }

    public void removePlayedCards(){
        for(int i = 0; i< selectedCardsIndex; i++){
            ImageView view = (ImageView)  selectedCardsHBox.getChildren().get(i);
            view.setVisible(false);
            view.setImage(null);
        }
        selectedCardsIndex = 0;

    }
    public void tryToPlayCards(){
        if(player.areChosenCardsCorrect(game.getStateOfRound())){
            player.playChosenCards(game.getStateOfRound(), game.getDeckOfCards());
            removePlayedCards();
            updateStackView(game.getStateOfRound());
            //end of round, send message
        }

        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("Playing those cards is againts the rules!");
            alert.showAndWait();
            putBackSelectedCards();
        }


    }

    public void updateStackView(StateOfRound stateOfRound){
        Card card = stateOfRound.getLastCard();
        Image image = new Image(getClass().getResource(card.getImagePath()).toExternalForm());
        stackCardImageView.setImage(image);
    }

    public void showCards(){
        ArrayList<Card> cards = player.getCardsInHand();
        int numberOfCardsInBottomRow = 0;
        for(int i = 0; i < maxNumberOfSelectedCards; i++){
            ImageView view = (ImageView) upRowCardsHBox.getChildren().get(i);
            view.setImage(null);
            view.setVisible(false);
        }
        if(player.getNumberOfCards() > maxNumberOfCardsInRow){
            numberOfCardsInBottomRow = maxNumberOfCardsInRow;
            for(int i = 0; i < player.getNumberOfCards() - maxNumberOfCardsInRow; i++){
                Card card = cards.get(i + maxNumberOfCardsInRow);
                Image image = new Image(getClass().getResource(card.getImagePath()).toExternalForm());
                ImageView view = (ImageView) upRowCardsHBox.getChildren().get(i);
                view.setImage(image);
                view.setVisible(true);
            }

        }
        else
            numberOfCardsInBottomRow = player.getNumberOfCards();
        for (int i = 0; i < numberOfCardsInBottomRow; i++) {
            Card card = cards.get(i);
            Image image = new Image(getClass().getResource(card.getImagePath()).toExternalForm());
            ImageView view = (ImageView) bottomRowCardsHBox.getChildren().get(i);
            view.setImage(image);
            view.setVisible(true);

        }
        for(int i = numberOfCardsInBottomRow; i < maxNumberOfCardsInRow; i++){
            ImageView view = (ImageView) bottomRowCardsHBox.getChildren().get(i);
            view.setImage(null);
            view.setVisible(false);
        }

    }

}