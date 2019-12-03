package views.panes;

import controllers.SceneManager;
import io.Deserializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.Config;
import models.FXGame;
import models.FlowTimer;
import views.*;

import java.util.Arrays;
import java.util.concurrent.Flow;

/**
 * Pane for the Level Editor.
 */
public class LevelEditorPane extends GamePane {

    private final LevelEditorCanvas levelEditor = new LevelEditorCanvas(FXGame.getDefaultRows(), FXGame.getDefaultCols(), FlowTimer.getDefaultDelay());
    private final VBox leftContainer = new SideMenuVBox();

    private final Button returnButton = new BigButton("Return");

    private Label rowText = new Label("Rows");
    private NumberTextField rowField = new NumberTextField(String.valueOf(levelEditor.getNumOfRows()));
    private BorderPane rowBox = new BorderPane(null, null, rowField, null, rowText);

    private Label colText = new Label("Columns");
    private NumberTextField colField = new NumberTextField(String.valueOf(levelEditor.getNumOfCols()));
    private BorderPane colBox = new BorderPane(null, null, colField, null, colText);

    private Button newGridButton = new BigButton("New Grid");

    private Label delayText = new Label("Delay");
    private NumberTextField delayField = new NumberTextField(String.valueOf(levelEditor.getAmountOfDelay()));
    private BorderPane delayBox = new BorderPane(null, null, delayField, null, delayText);

    private ObservableList<LevelEditorCanvas.CellSelection> cellList = FXCollections.observableList(Arrays.asList(LevelEditorCanvas.CellSelection.values()));
    private ListView<LevelEditorCanvas.CellSelection> selectedCell = new ListView<>();

    private Button toggleRotationButton = new BigButton("Toggle Source Rotation");
    private Button loadButton = new BigButton("Load");
    private Button saveButton = new BigButton("Save As");

    private VBox centerContainer = new BigVBox();

    public LevelEditorPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void connectComponents() {
        // TODO wip -- Cell LIST
        leftContainer.getChildren().add(returnButton);
        leftContainer.getChildren().add(rowBox);
        leftContainer.getChildren().add(colBox);
        leftContainer.getChildren().add(newGridButton);
        leftContainer.getChildren().add(delayBox);
        leftContainer.getChildren().add(selectedCell);  /**CELL List? */
        leftContainer.getChildren().add(toggleRotationButton);
        leftContainer.getChildren().add(loadButton);
        leftContainer.getChildren().add(saveButton);
        leftContainer.setAlignment(Pos.CENTER);

        selectedCell.setItems(cellList);


        centerContainer.getChildren().add(leftContainer);
        centerContainer.getChildren().add(levelEditor);
        //this.setCenter(centerContainer);

        this.setLeft(leftContainer);
        this.setCenter(levelEditor);
    }



    /**
     * {@inheritDoc}
     * {@link LevelEditorPane#selectedCell} should have cell heights of {@link Config#LIST_CELL_HEIGHT}.
     */
    @Override
    void styleComponents() {
        // TODO --wip
        selectedCell.setPrefHeight(Config.LIST_CELL_HEIGHT * 5);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
        returnButton.setOnAction( event -> {
            SceneManager.getInstance().showPane(MainMenuPane.class);
        });
        newGridButton.setOnAction( event -> {
            /**Re-draw Canvas**/
            levelEditor.changeAttributes(rowField.getValue(), colField.getValue(), delayField.getValue() );
        });
        toggleRotationButton.setOnAction( event -> {
            /** toggle */
            levelEditor.toggleSourceTileRotation();
        });
        loadButton.setOnAction( event -> {
            /** Loading **/
            levelEditor.loadFromFile();
            rowField.setText(Integer.toString(levelEditor.getNumOfRows()));
            colField.setText(Integer.toString(levelEditor.getNumOfCols()));
            delayField.setText(Integer.toString(levelEditor.getAmountOfDelay()));
        });
        saveButton.setOnAction( event -> {
            /** Saving **/
            levelEditor.saveToFile();
        });
    }
}
