package controllers;

import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import views.panes.*;

import java.util.Map;

/**
 * Singleton class for managing scenes.
 */
public class SceneManager {

    /**
     * Singleton instance.
     */
    private static final SceneManager INSTANCE = new SceneManager();

    /**
     * Main menu scene.
     */
    @NotNull
    private final Scene mainMenuScene = new Scene(new MainMenuPane(), Config.WIDTH / 2.0, Config.HEIGHT);
    /**
     * Settings scene.
     */
    @NotNull
    private final Scene settingsScene = new Scene(new SettingsPane(), Config.WIDTH, Config.HEIGHT);
    /**
     * Level select scene.
     */
    @NotNull
    private final Scene levelSelectScene = new Scene(new LevelSelectPane(), Config.WIDTH, Config.HEIGHT);
    /**
     * Gameplay scene.
     */
    @NotNull
    private final Scene gameplayScene = new Scene(new GameplayPane(), Config.WIDTH, Config.HEIGHT);
    /**
     * Level editor scene.
     */
    @NotNull
    private final Scene levelEditorScene = new Scene(new LevelEditorPane(), Config.WIDTH, Config.HEIGHT);
    /**
     * Map for fast lookup of {@link GamePane} to their respective {@link Scene}.
     */
    @NotNull
    private final Map<Class<? extends GamePane>, Scene> scenes = Map.ofEntries(
            Map.entry(MainMenuPane.class, mainMenuScene),
            Map.entry(SettingsPane.class, settingsScene),
            Map.entry(LevelSelectPane.class, levelSelectScene),
            Map.entry(GameplayPane.class, gameplayScene),
            Map.entry(LevelEditorPane.class, levelEditorScene)
    );
    /**
     * Primary stage.
     */
    @Nullable
    private Stage stage;

    private SceneManager() {
        // TODO wip: Add CSS styles to every scene
        this.gameplayScene.getStylesheets().add(models.Config.CSS_STYLES_PATH);
        this.levelEditorScene.getStylesheets().add(models.Config.CSS_STYLES_PATH);
        this.levelSelectScene.getStylesheets().add(models.Config.CSS_STYLES_PATH);
        this.mainMenuScene.getStylesheets().add(models.Config.CSS_STYLES_PATH);
        this.settingsScene.getStylesheets().add(models.Config.CSS_STYLES_PATH);
    }



    /**
     * Sets the primary stage.
     * @param stage Primary stage.
     */
    public void setStage(@NotNull final Stage stage) {
        if (this.stage != null) {
            throw new IllegalStateException("Primary stage is already initialized!");
        }

        this.stage = stage;
    }



    /**
     * Replaces the currently active {@link Scene} with another one.
     * @param scene New scene to display.
     */
    private void showScene(@NotNull final Scene scene) {
        if (stage == null) {
            return;
        }

        stage.hide();
        stage.setScene(scene);
        stage.show();
    }



    /**
     * Replaces the current {@link GamePane} with another one.
     *
     * @param pane New pane to display.
     * @throws IllegalArgumentException If the {@code pane} is not known.
     */
    public void showPane(@NotNull final Class<? extends GamePane> pane) throws IllegalArgumentException {
        // TODO wip -- set Pane and and wrong detection?
        if (pane == MainMenuPane.class){ showScene(mainMenuScene); }
        else if (pane == GameplayPane.class){ showScene(gameplayScene); }
        else if (pane == LevelEditorPane.class){ showScene(levelEditorScene); }
        else if (pane == LevelSelectPane.class){ showScene(levelSelectScene);}
        else if (pane == SettingsPane.class){ showScene(settingsScene);}
        else throw new IllegalArgumentException("The pane is not known");

        /*
        if (pane == MainMenuPane.class || pane == GameplayPane.class || pane == LevelEditorPane.class
                || pane == LevelSelectPane.class || pane == SettingsPane.class){

            if (stage.getScene() == null )
                showScene(mainMenuScene);
            stage.getScene().setRoot(getPane(pane));
        }
        else throw new IllegalArgumentException("The pane is not known");
         */
    }



    /**
     * Retrieves the underlying singleton {@link GamePane} object.
     *
     * @param pane {@link Class} type of pane to retrieve.
     * @param <T>  Actual type of the {@link GamePane} object.
     * @return Handle to the singleton {@link GamePane} object.
     */
    public <T> T getPane(@NotNull final Class<? extends GamePane> pane) {
        //noinspection unchecked
        return (T) scenes.get(pane).getRoot();
    }



    @NotNull
    public static SceneManager getInstance() {
        return INSTANCE;
    }
}
