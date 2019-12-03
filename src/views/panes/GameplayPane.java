package views.panes;

import controllers.AudioManager;
import controllers.LevelManager;
import controllers.SceneManager;
import io.Deserializer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.FXGame;
import models.PipeQueue;
import org.jetbrains.annotations.NotNull;
import views.BigButton;
import views.BigVBox;
import views.GameplayInfoPane;

import java.io.FileNotFoundException;

import static models.Config.TILE_SIZE;

/**
 * Pane for displaying the actual gameplay.
 */
public class GameplayPane extends GamePane {

    private HBox topBar = new HBox(20);
    private VBox canvasContainer = new BigVBox();
    private Canvas gameplayCanvas = new Canvas();
    private HBox bottomBar = new HBox(20);
    private Canvas queueCanvas = new Canvas();
    private Button quitToMenuButton = new BigButton("Quit to menu");

    private FXGame game;

    private final IntegerProperty ticksElapsed = new SimpleIntegerProperty();
    private GameplayInfoPane infoPane = null;

    public GameplayPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void connectComponents() {
        // TODO
        //topBar.getChildren().add(infoPane);
        topBar.setAlignment(Pos.CENTER);
        topBar.getChildren().add(gameplayCanvas);

        bottomBar.getChildren().add(queueCanvas);
        bottomBar.getChildren().add(quitToMenuButton);

        this.setCenter(topBar);
        this.setBottom(bottomBar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO
        bottomBar.setStyle("-fx-background-color: #808080;");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO wip
        quitToMenuButton.setOnAction(event -> {
            doQuitToMenuAction();
        });
    }



    /**
     * Handles events when somewhere on the {@link GameplayPane#gameplayCanvas} is clicked.
     * @param event Event to handle.
     */
    private void onCanvasClicked(MouseEvent event) {
        // TODO
    }



    /**
     * Handles events when a key is pressed.
     *
     * @param event Event to handle.
     */
    private void onKeyPressed(KeyEvent event) {
        // TODO
    }



    /**
     * Creates a popup which tells the player they have completed the map.
     */
    private void createWinPopup() {
        // TODO wip
        ButtonType continueGame = new ButtonType("continue", ButtonBar.ButtonData.YES);
        ButtonType returnMenu = new ButtonType("return", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You Win", continueGame, returnMenu);
        alert.setHeaderText("You Win!");
        alert.setContentText("Want to continue or return?");
        alert.showAndWait();

        if ( alert.getResult().equals(continueGame)){
            /**Continue Game**/
        } else doQuitToMenu();

    }



    /**
     * Loads the next map in the series, or generate a new map if one is not available.
     */
    private void loadNextMap() {
        // TODO
    }


    /**
     * Creates a popup which tells the player they have lost the map.
     */
    private void createLosePopup() {
        // TODO --wip click
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("You Lose!");
        alert.setContentText("Try again later!");
        alert.show();
    }


    /**
     * Creates a popup which prompts the player whether they want to quit.
     */
    private void doQuitToMenuAction() {
        // TODO
        ButtonType giveUp = new ButtonType("Yes", ButtonBar.ButtonData.NO);
        ButtonType stillPlay = new ButtonType("No", ButtonBar.ButtonData.YES);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Wanna Give Up?", stillPlay, giveUp);
        alert.setHeaderText("do you want to quit?");
        alert.setContentText("you will lose your current progress!!");
        alert.showAndWait();

        if ( alert.getResult().equals(giveUp)){
            doQuitToMenu();
        } else return;
    }



    /**
     * Go back to the Level Select scene.
     */
    private void doQuitToMenu() {
        // TODO
        SceneManager.getInstance().showPane(LevelSelectPane.class);
    }



    /**
     * Starts a new game with the given game.
     * @param game New game to start.
     */
    void startGame(@NotNull FXGame game) {
        // TODO
        this.game = game;
        game.renderMap(gameplayCanvas);
        game.renderQueue(queueCanvas);
    }



    /**
     * Cleans up the currently bound game.
     */
    private void endGame() {
        // TODO

    }
}
