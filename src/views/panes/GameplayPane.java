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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import models.FXGame;
import models.FlowTimer;
import models.PipeQueue;
import org.jetbrains.annotations.NotNull;
import views.BigButton;
import views.BigVBox;
import views.GameplayInfoPane;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

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

    //Helper
    private boolean EnableInput = true;


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
        this.getChildren().clear();

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
        gameplayCanvas.setOnMouseClicked(MouseEvent ->{
            if (EnableInput)
                onCanvasClicked(MouseEvent);
        });
        this.setOnKeyPressed(keyEvent -> {
            if (EnableInput)
                onKeyPressed(keyEvent);
        });


    }



    /**
     * Handles events when somewhere on the {@link GameplayPane#gameplayCanvas} is clicked.
     * @param event Event to handle.
     */
    private void onCanvasClicked(MouseEvent event) {
        // TODO
        AudioManager.getInstance().playSound(AudioManager.SoundRes.MOVE);

        int xPos = (int) event.getX() /TILE_SIZE;
        int yPos = (int) event.getY() /TILE_SIZE;
        game.placePipe(yPos, xPos);
        game.renderMap(gameplayCanvas);
        game.renderQueue(queueCanvas);
    }



    /**
     * Handles events when a key is pressed.
     *
     * @param event Event to handle.
     */
    private void onKeyPressed(KeyEvent event) {
        // TODO
        if (event.getCode() == KeyCode.S ){
            //skip
            game.skipPipe();
            game.renderMap(gameplayCanvas);
            game.renderQueue(queueCanvas);
        }
        else if ( event.getCode() == KeyCode.U ){
            //undo
            game.undoStep();
            game.renderMap(gameplayCanvas);
            game.renderQueue(queueCanvas);
        }
        else if (event.getCode() == KeyCode.SPACE ){
            //quit
            doQuitToMenuAction();
        }
    }



    /**
     * Creates a popup which tells the player they have completed the map.
     */
    private void createWinPopup() {
        // TODO wip
        AudioManager.getInstance().playSound(AudioManager.SoundRes.WIN);
        EnableInput = false;

        ButtonType continueGame = new ButtonType("continue", ButtonBar.ButtonData.YES);
        ButtonType returnMenu = new ButtonType("return", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You Win", continueGame, returnMenu);
        alert.setHeaderText("You Win!");
        alert.setContentText("Want to continue or return?");
        alert.showAndWait();

        if ( alert.getResult().equals(continueGame)){
            loadNextMap();
        } else doQuitToMenu();

    }



    /**
     * Loads the next map in the series, or generate a new map if one is not available.
     */
    private void loadNextMap() {
        // TODO -- start game?
        String Level = LevelManager.getInstance().getAndSetNextLevel();
        if ( Level == null ){
            startGame(new FXGame());
        }
        else {
            try{
                var path = Paths.get( LevelManager.getInstance().getCurrentLevelPath().toString(), "\\", Level );
                Deserializer ds = new Deserializer( path );
                startGame(ds.parseFXGame());
            }catch (FileNotFoundException e){ e.printStackTrace();}
        }
    }




    /**
     * Creates a popup which tells the player they have lost the map.
     */
    private void createLosePopup() {
        // TODO --wip click
        AudioManager.getInstance().playSound(AudioManager.SoundRes.LOSE);
        EnableInput = false;


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
        endGame();
        SceneManager.getInstance().showPane(LevelSelectPane.class);
    }





    /**
     * Starts a new game with the given game.
     * @param game New game to start.
     */
    void startGame(@NotNull FXGame game) {
        // TODO
        endGame();
        EnableInput = true;

        this.game = game;
        game.renderMap(gameplayCanvas);
        game.renderQueue(queueCanvas);

        connectComponents();

        //setUp the GameInfoPane
        ticksElapsed.setValue(0);
        var levelProp = LevelManager.getInstance().getCurrentLevelProperty();
        var timeProp = ticksElapsed;
        var numMovProp = game.getNumOfSteps();
        var numUndoProp = game.getNumOfUndo();
        infoPane = new GameplayInfoPane(levelProp, timeProp, numMovProp, numUndoProp);
        topBar.getChildren().add(0, infoPane);
        infoPane.setMaxHeight(50);

        /**Timer**/
        game.addOnTickHandler(new Runnable() {
            @Override
            public void run() {
                /***For Debugging***
                Platform.runLater(()->{
                    game.stopCountdown();
                    System.out.println("DONT Forget your debugging!!!");
                    createLosePopup();
                });
                ******/
                Platform.runLater(()->{ticksElapsed.setValue(ticksElapsed.getValue() + 1); });

                if ( game.hasWon() ) {
                    game.stopCountdown();
                    Platform.runLater(() ->{
                        game.fillAllPipes();
                        game.renderMap(gameplayCanvas);
                        createWinPopup();
                    });
                }
            }
        });
        game.addOnFlowHandler(new Runnable() {
            @Override
            public void run() {
                game.updateState();
                Platform.runLater(()->game.renderMap(gameplayCanvas));

                if ( game.hasLost() ){
                    game.stopCountdown();
                    Platform.runLater( () -> createLosePopup() );
                }
            }
        });

        game.startCountdown();
    }



    /**
     * Cleans up the currently bound game.
     */
    private void endGame() {
        // TODO wip
        topBar.getChildren().clear();
        bottomBar.getChildren().clear();
        canvasContainer.getChildren().clear();

        if ( game != null )
            game.stopCountdown();
    }
}
