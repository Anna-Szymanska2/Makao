package makao.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import makao.model.cards.Card;
import makao.model.game.Game;
import makao.model.game.Player;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    private Game game;
    private Player player;
    private final int numberOfCardsInRow = 25;
    private final int maxNumberOfSelectedCards = 4;
    @FXML
    private HBox bottomRowCardsHBox;
    @FXML
    private HBox upRowCardsHBox;
    @FXML
    private HBox selectedCardsHBox;

    @FXML
    private ImageView stackCardImageView;
    //Image image1 = new Image(getClass().getResourceAsStream("2_of_clubs.png"));
    @FXML
    private ImageView deckView;
    @FXML
    private ImageView drewCardView;
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
        ArrayList<Card> cards = player.getCardsInHand();
        for (int i = 0; i < 5; i++) {
            Card card = cards.get(i);
            Image image = new Image(getClass().getResource(card.getImagePath()).toExternalForm());
            ImageView view = (ImageView) bottomRowCardsHBox.getChildren().get(i);
            //ImageView view = (ImageView) upRowCardsHBox.getChildren().get(i);
            view.setImage(image);
            view.setVisible(true);

        }
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
        for(int i = 0; i <numberOfCardsInRow; i++) {
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
        if(selectedCardsIndex == maxNumberOfSelectedCards)
            playCards();
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
        if(playerNumberOfCards > numberOfCardsInRow)
            firstFreeCardView = (ImageView) upRowCardsHBox.getChildren().get(playerNumberOfCards - numberOfCardsInRow);
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
        if(playerNumberOfCards > numberOfCardsInRow){
            for(int i = 0 ; i < playerNumberOfCards - numberOfCardsInRow; i++){
                if(upRowCardsHBox.getChildren().get(i) == chosenCard){
                    wasSelectedCardFromBottomRow = false;
                    return i + numberOfCardsInRow;
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


    }
    public void putBackSelectedCard(){

    }
    public void playCards(){

    }

}