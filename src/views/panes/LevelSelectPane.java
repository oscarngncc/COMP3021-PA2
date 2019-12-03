package views.panes;

import controllers.LevelManager;
import controllers.SceneManager;
import io.Deserializer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import models.FXGame;
import views.BigButton;
import views.BigVBox;
import views.SideMenuVBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class LevelSelectPane extends GamePane {

    private SideMenuVBox leftContainer = new SideMenuVBox();
    private BigButton returnButton = new BigButton("Return");
    private BigButton playButton = new BigButton("Play");
    private BigButton playRandom = new BigButton("Generate Map and Play");
    private BigButton chooseMapDirButton = new BigButton("Choose map directory");
    private ListView<String> levelsListView = new ListView<>(LevelManager.getInstance().getLevelNames());
    private BigVBox centerContainer = new BigVBox();
    private Canvas levelPreview = new Canvas();

    public LevelSelectPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void connectComponents() {
        // TODO --wip
        leftContainer.getChildren().add(returnButton);
        leftContainer.getChildren().add(chooseMapDirButton);
        leftContainer.getChildren().add(levelsListView);
        leftContainer.getChildren().add(playButton);
        leftContainer.getChildren().add(playRandom);

        centerContainer.getChildren().add(leftContainer);
        centerContainer.getChildren().add(levelPreview);

        this.setLeft(leftContainer);
        this.setCenter(levelPreview);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO --wip
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        returnButton.setOnAction( event -> {
            /** Return **/
            SceneManager.getInstance().showPane(MainMenuPane.class);
        });
        chooseMapDirButton.setOnAction( event -> {
            /** Choose Map **/
            promptUserForMapDirectory();
        });
        levelsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                onMapSelected(observableValue, oldValue, newValue);
            }
        });

        playButton.setOnAction( event -> {
            startGame(false);
        });
        playRandom.setOnAction( event -> {
            startGame(true);
        });
    }

    /**
     * Starts the game.
     *
     * <p>
     * This method should do everything that is required to initialize and start the game, including loading/generating
     * maps, switching scenes, etc.
     * </p>
     *
     * @param generateRandom Whether to use a generated map.
     */
    private void startGame(final boolean generateRandom) {
        // TODO wip
        if (generateRandom){
            /**Random Map Generated**/
            SceneManager.getInstance().showPane(GameplayPane.class);
        }
        else
        {

        }
    }



    /**
     * Listener method that executes when a map on the list is selected.
     *
     * @param observable Observable value.
     * @param oldValue   Original value.
     * @param newValue   New value.
     */
    private void onMapSelected(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        // TODO
        try{
            var path = Paths.get( LevelManager.getInstance().getCurrentLevelPath().toString(), "\\", newValue );
            Deserializer ds = new Deserializer( path );
            FXGame game = ds.parseFXGame();
            game.renderMap(levelPreview);
        }catch (FileNotFoundException e){ e.printStackTrace();}
    }



    /**
     * Prompts the user for a map directory.
     * <p>
     * Hint:
     * Use {@link DirectoryChooser} to display a folder selection prompt.
     * </p>
     */
    private void promptUserForMapDirectory() {
        // TODO -- wip
        DirectoryChooser chooser = new DirectoryChooser();
        File folder = chooser.showDialog(this.getScene().getWindow());
        commitMapDirectoryChange(folder);
    }



    /**
     * Actually changes the current map directory.
     * @param dir New directory to change to.
     */
    private void commitMapDirectoryChange(File dir) {
        // TODO -- wip
        if ( dir != null ) {
            System.out.println("The Map directory is: " + dir.getAbsolutePath());
            LevelManager.getInstance().setMapDirectory(dir.toPath());
        }
        else System.out.println("Failure of getting map directory");
    }
}
