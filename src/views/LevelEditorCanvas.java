package views;

import controllers.Renderer;
import io.Deserializer;
import io.GameProperties;
import io.Serializer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import models.Config;
import models.FXGame;
import models.exceptions.InvalidMapException;
import models.map.cells.Cell;
import models.map.cells.FillableCell;
import models.map.cells.TerminationCell;
import models.map.cells.Wall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;
import util.Direction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import static models.Config.TILE_SIZE;

public class LevelEditorCanvas extends Canvas {

    private static final String MSG_MISSING_SOURCE = "Source tile is missing!";
    private static final String MSG_MISSING_SINK = "Sink tile is missing!";
    private static final String MSG_BAD_DIMS = "Map size must be at least 2x2!";
    private static final String MSG_BAD_DELAY = "Delay must be a positive value!";
    private static final String MSG_SOURCE_TO_WALL = "Source tile is blocked by a wall!";
    private static final String MSG_SINK_TO_WALL = "Sink tile is blocked by a wall!";

    private GameProperties gameProp;

    @Nullable
    private TerminationCell sourceCell;
    @Nullable
    private TerminationCell sinkCell;

    public LevelEditorCanvas(int rows, int cols, int delay) {
        super();
        resetMap(rows, cols, delay);
    }



    /**
     * Changes the attributes of this canvas.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param delay Amount of delay.
     */
    public void changeAttributes(int rows, int cols, int delay) {
        resetMap(rows, cols, delay);
    }



    /**
     * Resets the map with the given attributes.
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param delay Amount of delay.
     */
    private void resetMap(int rows, int cols, int delay) {
        // TODO wip -- wrong initialization?
        Cell[][] cells = new Cell[rows][cols];
        sourceCell = null;
        sinkCell = null;

        for ( int i = 0; i < rows; i++ ) {
            for (int j = 0; j < cols; j++) {
                if ( i == 0 || i == rows - 1 || j == 0 || j == cols -1 ){
                    cells[i][j] = new Wall(new Coordinate(i, j));
                }
                else cells[i][j] = new FillableCell(new Coordinate(i, j));
            }
        }
        gameProp = new GameProperties(rows, cols, cells, delay);

        Renderer.renderMap(this, cells );
    }



    /**
     * Renders the canvas.
     */
    private void renderCanvas() {
        Platform.runLater(() -> Renderer.renderMap(this, gameProp.cells));
    }



    /** Helper Function for removing termination cell **/
    private void checkRemoveTerminationCell( int row, int col ){
        if (sourceCell != null ){
            if ( sourceCell.coord.col == col && sourceCell.coord.row == row ){
                sourceCell = null;
            }
        }
        else if ( sinkCell != null ){
            if (sinkCell.coord.col == col && sinkCell.coord.row == row){
                sinkCell = null;
            }
        }
    }

    /** Helper Function for determining whether termination cell is sink or not (false will be source) **/
    private boolean checkIsSink(Cell cell) throws IllegalArgumentException {
        if ( cell instanceof TerminationCell)
            throw new IllegalArgumentException("Not a Termination CEll!!!");


        if ( cell.coord.row == 0 || cell.coord.row == gameProp.rows -1
        ||   cell.coord.col == 0 || cell.coord.col == gameProp.cols -1)
            return true;
        else return false;
    }





    /**
     * Sets a tile on the map.
     * <p>
     * Hint:
     * You may need to check/compute some attribute in order to create the new {@link Cell} object.
     *
     * @param sel Selected {@link CellSelection}.
     * @param x   X-coordinate relative to the canvas.
     * @param y   Y-coordinate relative to the canvas.
     */
    public void setTile(@NotNull CellSelection sel, double x, double y) {
        // TODO wip -- what attribute condition?
        if ( x < 0 || y  < 0 )
            return;

        System.out.println( "setTile: x is " + x + " and y is " + y);

        int xPos = (int) x /TILE_SIZE;
        int yPos = (int) y /TILE_SIZE;

        if ( sel == CellSelection.CELL ){
            checkRemoveTerminationCell(xPos, yPos);
            gameProp.cells[yPos][xPos] = new FillableCell(new Coordinate(xPos, yPos));
            renderCanvas();
        }
        else if ( sel == CellSelection.TERMINATION_CELL ){
            /**Check is it a sink **/
            if ( yPos == 0 || yPos == gameProp.rows -1  ||   xPos == 0 || xPos == gameProp.cols -1 ){

                if ((xPos == 0 || xPos == gameProp.cols -1) && (yPos == 0 || yPos == gameProp.rows -1 ))
                    return;

                Direction d = Direction.UP;
                if (xPos == gameProp.cols -1 )
                    d = Direction.LEFT;
                else if ( yPos == 0 )
                    d = Direction.DOWN;
                else if ( xPos == 0 )
                    d = Direction.RIGHT;
                else if (yPos == gameProp.rows -1 )
                    d = Direction.UP;

                gameProp.cells[yPos][xPos] = new TerminationCell( new Coordinate(xPos, yPos), d, TerminationCell.Type.SINK );
                sinkCell = (TerminationCell) gameProp.cells[yPos][xPos];
                renderCanvas();

            }
            else { //is source
                gameProp.cells[yPos][xPos] = new TerminationCell( new Coordinate(xPos, yPos), Direction.UP, TerminationCell.Type.SOURCE );
                sourceCell = (TerminationCell) gameProp.cells[yPos][xPos];
                renderCanvas();
            }
        }
        else if ( sel == CellSelection.WALL ){
            checkRemoveTerminationCell(xPos, yPos);
            gameProp.cells[yPos][xPos] = new Wall(new Coordinate(xPos, yPos));
            renderCanvas();
        }

    }



    /**
     * Sets a tile on the map.
     * <p>
     * Hint:
     * You will need to make sure that there is only one source/sink cells in the map.
     * @param cell The {@link Cell} object to set.
     */
    private void setTileByMapCoord(@NotNull Cell cell) {
        // TODO - wip ( what about replacement?)

        if ( cell instanceof TerminationCell ){
            boolean isSink = false;
            if ( cell.coord.row == 0 || cell.coord.row == 0 ||
                    cell.coord.row == FXGame.getDefaultRows() - 1 || cell.coord.col == FXGame.getDefaultCols() -1)
                isSink = true;
            if ( isSink && sinkCell == null) {
                sinkCell = (TerminationCell) cell;
                gameProp.cells[cell.coord.row][cell.coord.col] = cell;
            }else if (!isSink && sourceCell == null ){
                sourceCell = (TerminationCell) cell;
                gameProp.cells[cell.coord.row][cell.coord.col] = cell;
            }
        }
        else{
            checkRemoveTerminationCell(cell.coord.row, cell.coord.col);
            gameProp.cells[cell.coord.row][cell.coord.col] = cell;
        }
    }



    /**
     * Toggles the rotation of the source tile clockwise.
     */
    public void toggleSourceTileRotation() {
        // TODO wip
        if ( sourceCell != null ){
            //sourceCell.pointingTo = sourceCell.pointingTo.rotateCW();
            Cell[][] cellMap = gameProp.cells.clone();
            cellMap[sourceCell.coord.row][sourceCell.coord.col] = new TerminationCell( sourceCell.coord, sourceCell.pointingTo.rotateCW(), sourceCell.type );

            sourceCell = (TerminationCell) cellMap[sourceCell.coord.row][sourceCell.coord.col];
            gameProp.cells = cellMap;
            renderCanvas();
        }
    }



    /**
     * Loads a map from a file.
     * <p>
     * Prompts the player if they want to discard the changes, displays the file chooser prompt, and loads the file.
     * @return {@code true} if the file is loaded successfully.
     */
    public boolean loadFromFile() {
        // TODO wip
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Load a map from file?");
        alert.setContentText("All Progress may be lost.");
        alert.showAndWait();

        if ( alert.getResult() == ButtonType.OK ){
            File lfile = getTargetLoadFile();
            if (lfile != null ){
                try {
                    /** Some Action here **/
                    System.out.println("loading from file");
                    return loadFromFile(lfile.toPath());
                }
                catch(Exception e) { return false; }
            }
        }
        return false;
    }



    /**
     * Prompts the user for the file to load.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     * @return {@link File} to load, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetLoadFile() {
        // TODO
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("Map Files", "*.map"));
        File selectedFile = fileChooser.showOpenDialog(this.getScene().getWindow());
        return selectedFile;
    }



    /**
     * Loads the file from the given path and replaces the current {@link LevelEditorCanvas#gameProp}.
     * <p>
     * Hint:
     * You should handle any exceptions which arise from loading in this method.
     *
     * @param path Path to load the file from.
     * @return {@code true} if the file is loaded successfully, {@code false} otherwise.
     */
    private boolean loadFromFile(@NotNull Path path) {
        // TODO -- wip
        try {
            Deserializer deserializer = new Deserializer(path);
            GameProperties gameProperties = deserializer.parseGameFile();
            this.gameProp = gameProperties;

            /*** UI Update **/
            sourceCell = null;
            sinkCell = null;

            for ( int i = 0; i < getNumOfRows(); i++ ){
                for ( int j = 0; j < getNumOfCols(); j++ ){

                    System.out.print(gameProp.cells[i][j].toSingleChar());

                    if (gameProp.cells[i][j] instanceof TerminationCell ){

                        if ( i == 0 || j == 0 || i ==  gameProp.rows - 1 || j == gameProp.cols - 1 ){
                            sinkCell = (TerminationCell) gameProp.cells[i][j];
                        }
                        else { sourceCell = (TerminationCell) gameProp.cells[i][j];  }
                    }
                }
            }

            renderCanvas();
            return true;
        }
        catch (FileNotFoundException e ){ System.out.println("File not found"); return false; }
    }



    /**
     * Checks the validity of the map, prompts the player for the target save directory, and saves the file.
     */
    public void saveToFile() {
        // TODO - wip
        if (checkValidity().isEmpty() ){
            File sfile = getTargetSaveDirectory();
            /** Missing Action here **/
            exportToFile(sfile.toPath());
            System.out.println("Save to file success");
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Map Validation Failed");
            alert.setContentText(checkValidity().orElse("Some sort of Validation error"));
            alert.show();
        }
    }



    /**
     * Prompts the user for the directory and filename to save as.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     * @return {@link File} to save to, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetSaveDirectory() {
        // TODO wip
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("Map Files", "*.map"));
        File selectedFile = fileChooser.showSaveDialog(this.getScene().getWindow());
        return selectedFile;
    }



    /**
     * Exports the current map to a file.
     * <p>
     * Hint:
     * You should handle any exceptions which arise from saving in this method.
     * @param p Path to export to.
     */
    private void exportToFile(@NotNull Path p) {
        // TODO
        try {
            Serializer serializer = new Serializer(p);
            serializer.serializeGameProp(gameProp);
        }
        catch (Exception e ){ System.out.println("Error in exporting to File");}
    }



    /**
     * Checks whether the current map and its properties are valid.
     * <p>
     * Hint:
     * You should check for the following conditions:
     * <ul>
     * <li>Source cell is present</li>
     * <li>Sink cell is present</li>
     * <li>Minimum map size is 2x2</li>
     * <li>Flow delay is at least 1</li>
     * <li>Source/Sink tiles are not blocked by walls</li>
     * </ul>
     *
     * @return {@link Optional} containing the error message, or an empty {@link Optional} if the map is valid.
     */
    private Optional<String> checkValidity() {
        // TODO
        if ( sourceCell == null )
            return  Optional.of(MSG_MISSING_SOURCE);
        else if ( sinkCell == null )
            return Optional.of(MSG_MISSING_SINK);
        else if (getNumOfCols() < 2 || getNumOfCols() < 2 )
            return Optional.of(MSG_BAD_DIMS);
        else if (getAmountOfDelay() < 1 )
            return Optional.of(MSG_BAD_DELAY);
        else if ( /** sink block by wall **/ false )
            return Optional.of(MSG_SINK_TO_WALL);
        else if ( /** source block by wall**/ false )
            return Optional.of(MSG_SOURCE_TO_WALL);
        else
            return Optional.empty();
    }


    public int getNumOfRows() {
        return gameProp.rows;
    }


    public int getNumOfCols() {
        return gameProp.cols;
    }

    public int getAmountOfDelay() {
        return gameProp.delay;
    }

    public void setAmountOfDelay(int delay) {
        gameProp.delay = delay;
    }

    public enum CellSelection {
        WALL("Wall"),
        CELL("Cell"),
        TERMINATION_CELL("Source/Sink");

        private String text;

        CellSelection(@NotNull String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
