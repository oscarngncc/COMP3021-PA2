package controllers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import models.map.cells.Cell;
import models.map.cells.TerminationCell;
import models.pipes.Pipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static models.Config.TILE_SIZE;

/**
 * Helper class for render operations on a {@link Canvas}.
 */
public class Renderer {

    /*** Padding between two tiles in a queue.*/
    private static final int QUEUE_TILE_PADDING = 8;

    /*** An image of a cell, with support for rotated images.*/
    public static class CellImage {

        /*** Image of the cell.*/
        @NotNull
        final Image image;

        /*** Rotation of the image. */
        final float rotation;

        /**
         * @param image    Image of the cell.
         * @param rotation Rotation of the image.
         */
        public CellImage(@NotNull Image image, float rotation) {
            this.image = image;
            this.rotation = rotation;
        }
    }



    /**
     * Sets the current rotation of a {@link GraphicsContext}.
     *
     * @param gc     Target Graphics Context.
     * @param angle  Angle to rotate the context by.
     * @param pivotX X-coordinate of the pivot point.
     * @param pivotY Y-coordinate of the pivot point.
     */
    private static void rotate(@NotNull GraphicsContext gc, double angle, double pivotX, double pivotY) {
        final var r = new Rotate(angle, pivotX, pivotY);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }



    /**
     * Draws a rotated image onto a {@link GraphicsContext}.
     *
     * @param gc    Target Graphics Context.
     * @param image Image to draw.
     * @param angle Angle to rotate the image by.
     * @param x     X-coordinate relative to the graphics context to draw the top-left of the image.
     * @param y     Y-coordinate relative to the graphics context to draw the top-left of the image.
     */
    private static void drawRotatedImage(@NotNull GraphicsContext gc, @NotNull Image image, double angle, double x, double y) {
        // TODO wip -- Rotate gc?
        /**Credit: https://stackoverflow.com/questions/40059836/rotating-image-in-javafx */
        ImageView iv = new ImageView(image);
        iv.setRotate(angle);
        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
        gc.drawImage( iv.snapshot(params, null), x, y );

        /** Code that suppose to work but can't**/
        //gc.save();
        //rotate(gc, angle, 0 , 0 );
        //gc.drawImage(image, x, y);
        //gc.restore();
    }



    /**
     * Renders a map into a {@link Canvas}.
     * @param canvas Canvas to render to.
     * @param map    Map to render.
     */
    public static void renderMap(@NotNull Canvas canvas, @NotNull Cell[][] map) {
        // TODO wip -- positioning and rotation
        final int Row = map.length;
        final int Col = map[0].length;

        /*
        System.out.println("The loading is: ");
        for ( int i = 0; i < Row; i++ ) {
            for (int j = 0; j < Col; j++) {
                System.out.print(map[i][j].toSerializedRep());
            }
            System.out.println();
        }*/

        canvas.setHeight(Row * TILE_SIZE);
        canvas.setWidth(Col * TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        for ( int i = 0; i < Row; i++ ){
            for ( int j = 0; j < Col; j++ ){
                Image image = map[i][j].getImageRep().image;

                if (map[i][j] instanceof TerminationCell){
                    TerminationCell tCell = (TerminationCell) map[i][j];
                    drawRotatedImage(gc, image, tCell.getImageRep().rotation, TILE_SIZE * j, TILE_SIZE * i  );
                }
                else {
                    gc.drawImage( image, TILE_SIZE * j, TILE_SIZE * i);
                }

            }
        }


    }



    /**
     * Renders a pipe queue into a {@link Canvas}.
     *
     * @param canvas    Canvas to render to.
     * @param pipeQueue Pipe queue to render.
     */
    public static void renderQueue(@NotNull Canvas canvas, @NotNull List<Pipe> pipeQueue) {
        // TODO wip -- positioning?
        canvas.setHeight(TILE_SIZE);
        canvas.setWidth(TILE_SIZE * pipeQueue.size() );
        GraphicsContext gc = canvas.getGraphicsContext2D();

        for ( int i = 0; i < pipeQueue.size(); i++ ){
            gc.drawImage( pipeQueue.get(i).getImageRep().image, TILE_SIZE * i, 0);
        }
    }
}
