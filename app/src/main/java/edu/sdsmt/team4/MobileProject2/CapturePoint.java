package edu.sdsmt.team4.MobileProject2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * @author Scott Carda
 * The class defining the capture point shape. Implements CaptureShape.
 */
class CapturePoint implements CaptureShape {
    /**
     * Relative x and y coordinates for the location of the shape.
     */
    private float locationX;
    private float locationY;

    /**
     * Bundle keys for relevant information for this shape.
     */
    private final static String LOC_X_KEY = "point x key";
    private final static String LOC_Y_KEY = "point y key";

    /**
     * Paint object for drawing the shape.
     */
    private final Paint pointPaint;

    /**
     * The constructor for the CapturePoint class
     * @param x Relative location for the shape along the X axis
     * @param y Relative location for the shape along the Y axis
     */
    CapturePoint(float x, float y) {
        // Assign the location of the shape
        locationX = x;
        locationY = y;

        // Define the paint that will be used to draw the shape
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setARGB(255, 66, 134, 244);
    }

    /**
     * Translates the shape by the given amount along each axis.
     * @param deltaX Relative amount to move the shape along the X axis
     * @param deltaY Relative amount to move the shape along the Y axis
     */
    public void move(float deltaX, float deltaY) {
        // Update X
        float temp = locationX + deltaX;
        if (temp > 1.0f) locationX = 1.0f;
        else if (temp < 0.0f) locationX = 0.0f;
        else locationX = temp;

        // Update Y
        temp = locationY + deltaY;
        if (temp > 1.0f) locationY = 1.0f;
        else if (temp < 0.0f) locationY = 0.0f;
        else locationY = temp;
    }

    /**
     * Will reshape the shape according to the rules for this shape.
     * Points can't be reshaped.
     * @param deltaDistance The change in distance between the two touch points
     * @param deltaAngle The change in angle between the two touch points
     */
    public void reshape(float deltaDistance, float deltaAngle) {
        // Points do not reshape
    }

    /**
     * Handles the drawing of the shape.
     * @param canvas The canvas upon which the shape will be drawn
     * @param marginX The horizontal margin for the game board
     * @param marginY The vertical margin for the game board
     * @param boardSize The side length in pixels for the game board
     */
    public void draw(Canvas canvas, int marginX, int marginY, int boardSize) {
        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + locationX * boardSize, marginY + locationY * boardSize);

        // Scale it to the board size
        canvas.scale(boardSize, boardSize);

        // Draw the point, which is actually a small circle
        canvas.drawCircle(0.0f, 0.0f, 0.01f, pointPaint);

        canvas.restore();
    }

    /**
     * Captures tiles from the game board based on the rules for this shape.
     * @param tiles The list of tiles for the game board
     * @return ArrayList of the indexes of the tiles that were captured
     */
    public ArrayList<Integer> capture(final ArrayList<GameTile> tiles) {
        // The list of indexes for the captured tiles
        ArrayList<Integer> capturedTiles = new ArrayList<>();

        for (int i = 0; i < tiles.size(); ++i) {
            if (isTileCapture(tiles.get(i))) {
                capturedTiles.add(i);
                break; // The point only captures one tile
            }
        }

        return capturedTiles;
    }

    /**
     * Saves the relevant information for this shape to the bundle.
     * @param bundle The bundle to save on to
     */
    public void saveInstanceState(Bundle bundle) {
        bundle.putFloat(LOC_X_KEY, locationX);
        bundle.putFloat(LOC_Y_KEY, locationY);
    }

    /**
     * Loads the relevant information for this shape off the bundle.
     * @param bundle The bundle to load off of
     */
    public void loadInstanceState(Bundle bundle) {
        locationX = bundle.getFloat(LOC_X_KEY);
        locationY = bundle.getFloat(LOC_Y_KEY);
    }

    /**
     * Determines if the given tile is captured by the shape.
     * @param tile The Game Tile being tested for capture
     * @return True if tile is captured, else False
     */
    private boolean isTileCapture(GameTile tile) {
        // Holders for tile data members
        float tileX = tile.getX();
        float tileY = tile.getY();
        float halfSize = tile.getSize() / 2.0f;

        // If the point is inside the tile, return true
        return
            tileX - halfSize <= locationX &&
            tileX + halfSize >= locationX &&
            tileY - halfSize <= locationY &&
            tileY + halfSize >= locationY;
    }
}
