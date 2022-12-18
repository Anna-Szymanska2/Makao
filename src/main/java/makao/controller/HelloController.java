package makao.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import makao.model.cards.Card;
import makao.model.game.Game;
import makao.model.game.Player;
import makao.view.Main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    private Game game;
    private Player player;
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
        initHBox(bottomRowCardsHBox);
        initHBox(upRowCardsHBox);
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




    public void initHBox(HBox cardsHBox){
        for(int i = 0; i <25; i++) {
            ImageView view = (ImageView) cardsHBox.getChildren().get(i);
            view.setVisible(false);
            view.setOnMouseClicked((mouseEvent) -> {
                chosenCardView = (ImageView)mouseEvent.getSource();
                chooseCard();

            });
        }
    }
    public void chooseCard(){
        ImageView view = (ImageView)selectedCardsHBox.getChildren().get(selectedCardsIndex);
        view.setImage(chosenCardView.getImage());
        view.setVisible(true);
        chosenCardView.setImage(null);
        chosenCardView.setVisible(false);
        chosenCardView.toFront();
        selectedCardsIndex++;
        if(selectedCardsIndex == 4)
            playCards();
    }

    public int findLastSelectedCardIndex(ImageView selectedCard) {
        int playerNumberOfCards = player.getNumberOfCards();
        if(playerNumberOfCards > 25){
            for(int i = 0 ; i < playerNumberOfCards - 25; i++){
                if(upRowCardsHBox.getChildren().get(i) == selectedCard){
                    wasSelectedCardFromBottomRow = false;
                    return i;
                }
            }
        }
        for (int i = 0; i< playerNumberOfCards; i++){
            if(bottomRowCardsHBox.getChildren().get(i) == selectedCard){
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