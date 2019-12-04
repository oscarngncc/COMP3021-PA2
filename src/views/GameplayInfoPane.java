package views;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Displays info about the current level being played by the user.
 */
public class GameplayInfoPane extends BigVBox {
    private final Label levelNameLabel = new Label();
    private final Label timerLabel = new Label();
    private final Label numMovesLabel = new Label();
    private final Label numUndoLabel = new Label();

    public GameplayInfoPane(StringProperty levelNameProperty, IntegerProperty timerProperty, IntegerProperty numMovesProperty, IntegerProperty numUndoProperty) {
        // TODO -- wip
        bindTo(levelNameProperty, timerProperty, numMovesProperty, numUndoProperty);
        this.getChildren().add(levelNameLabel);
        this.getChildren().add(timerLabel);
        this.getChildren().add(numMovesLabel);
        this.getChildren().add(numUndoLabel);
    }


    /**
     * @param s Seconds duration
     * @return A string that formats the duration stopwatch style
     */
    private static String format(int s) {
        final var d = Duration.of(s, SECONDS);

        int seconds = d.toSecondsPart();
        int minutes = d.toMinutesPart();

        return String.format("%02d:%02d", minutes, seconds);
        // Uncomment next line for JDK 8
//        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }


    /**
     * Binds all properties to their respective UI elements.
     *
     * @param levelNameProperty Level Name Property
     * @param timerProperty Timer Property
     * @param numMovesProperty Number of Moves Property
     * @param numUndoProperty Number of Undoes Property
     */
    private void bindTo(StringProperty levelNameProperty, IntegerProperty timerProperty, IntegerProperty numMovesProperty, IntegerProperty numUndoProperty) {
        // TODO wip -- time
        if ( levelNameProperty.getValue() == null || levelNameProperty.getValue().isEmpty() )
            levelNameProperty.setValue("Generated");
        levelNameLabel.textProperty().bind(Bindings.concat("Level: ").concat(levelNameProperty));


        StringProperty time = new SimpleStringProperty();
        Bindings.bindBidirectional(timerLabel.textProperty(), timerProperty, new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return "Time: " + format(number.intValue());
            }

            @Override
            public Number fromString(String s) {
                return 0;
            }
        });


        StringProperty intToS = new SimpleStringProperty();
        intToS.bindBidirectional(numMovesProperty, new NumberStringConverter());
        numMovesLabel.textProperty().bind( Bindings.concat("Moves: ").concat(intToS) );

        StringProperty inToS2 = new SimpleStringProperty();
        inToS2.bindBidirectional(numUndoProperty, new NumberStringConverter());
        numUndoLabel.textProperty().bind( Bindings.concat("Undo Count:").concat(inToS2));
    }
}
