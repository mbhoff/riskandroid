package edu.sdsmt.team4.MobileProject2;

import android.graphics.Canvas;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * @author  Scott Carda
 * The interface for capture shape objects. These are used my players to
 * capture game tiles on the game board.
 */
interface CaptureShape {
    /**
     * Translates the shape by the given amount along each axis.
     * @param deltaX Relative amount to move the shape along the X axis
     * @param deltaY Relative amount to move the shape along the Y axis
     */
    void move(float deltaX, float deltaY);

    /**
     * Will reshape the shape according to the rules for this shape.
     * @param deltaDistance The change in distance between the two touch points
     * @param deltaAngle The change in angle between the two touch points
     */
    void reshape(float deltaDistance, float deltaAngle);

    /**
     * Handles the drawing of the shape.
     * @param canvas The canvas upon which the shape will be drawn
     * @param marginX The horizontal margin for the game board
     * @param marginY The vertical margin for the game board
     * @param boardSize The side length in pixels for the game board
     */
    void draw(Canvas canvas, int marginX, int marginY, int boardSize);

    /**
     * Captures tiles from the game board based on the rules for this shape.
     * @param tiles The list of tiles for the game board
     * @return ArrayList of the indexes of the tiles that were captured
     */
    ArrayList<Integer> capture(ArrayList<GameTile> tiles);

    /**
     * Saves the relevant information for this shape to the bundle.
     * @param bundle The bundle to save on to
     */
    void saveInstanceState(Bundle bundle);

    /**
     * Loads the relevant information for this shape off the bundle.
     * @param bundle The bundle to load off of
     */
    void loadInstanceState(Bundle bundle);
}
