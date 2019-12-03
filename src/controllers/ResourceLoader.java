package controllers;

import javafx.scene.image.Image;
import models.exceptions.ResourceNotFoundException;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;

/**
 * Helper class for loading resources from the filesystem.
 */
public class ResourceLoader {

    /**Path to the resources directory.*/
    @NotNull
    private static final Path RES_PATH;

    static {
        // TODO: Initialize RES_PATH
        RES_PATH = Paths.get("resources");
    }

    /**
     * Retrieves a resource file from the resource directory.
     *
     * @param relativePath Path to the resource file, relative to the root of the resource directory.
     * @return Absolute path to the resource file.
     * @throws ResourceNotFoundException If the file cannot be found under the resource directory.
     */
    @NotNull
    public static String getResource(@NotNull final String relativePath) throws ResourceNotFoundException {
        // TODO - wip
        Path absolutePath = Paths.get( RES_PATH.toString(), relativePath);
        String absolutePathString = absolutePath.toAbsolutePath().toString();
        //System.out.println("The absolute path is: " + absolutePathString);

        if ( ! new File( absolutePathString ).exists() ) {
            throw new ResourceNotFoundException("file not found under resource directory!");
        }


        /**weird issue reading absolutePath for Java.FX**/
        try {
            return new File(absolutePathString).toURI().toURL().toExternalForm();
        } catch (Exception e){ throw new ResourceNotFoundException("JavaFX resource not found under resource directory!");}
    }
}
