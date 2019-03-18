package edu.sdsmt.team4.MobileProject2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * @author Scott Carda
 * The class defining the capture rectangle shape. Implements CaptureShape.
 */
public class CaptureRectangle implements CaptureShape {
    /**
     * Ratio the rectangle's length makes with its width.
     * Remains constant.
     */
    private final static float ASPECT_RATIO = 2.0f;

    /**
     * Relative starting width (X axis) of the rectangle.
     * Remains constant.
     */
    private final static float DEFAULT_WIDTH = 0.25f;

    /**
     * Relative minimum width (X axis) of the rectangle.
     * Remains constant.
     */
    private final static float MINIMUM_WIDTH = 0.21f;

    /**
     * The chance to capture a targeted tile with the default rectangle width.
     * Remains constant.
     */
    private final static float DEFAULT_CHANCE = 0.25f;

    /**
     * Bundle keys for relevant information for this shape.
     */
    private final static String LOC_X_KEY = "rectangle x key";
    private final static String LOC_Y_KEY = "rectangle y key";
    private final static String SCALE_KEY = "rectangle scale key";

    /**
     * Relative x and y coordinates for the location of the shape.
     */
    private float locationX;
    private float locationY;

    /**
     * Scale the shape will have relative to its default width
     */
    private float scale = 1.0f;

    /**
     * Derived values for the x and y coordinates of the edges of the rectangle.
     */
    private float left, top, right, bottom;

    /**
     * Paint objects for drawing the shape.
     */
    private final Paint rectPaint;
    private final Paint rectPaintOutline;

    /**
     * The constructor for the CaptureRectangle class
     * @param x Relative location for the shape along the X axis
     * @param y Relative location for the shape along the Y axis
     */
    CaptureRectangle(float x, float y) {
        // Assign the location of the shape
        locationX = x;
        locationY = y;

        // Define the paint that will be used to draw the shape
        rectPaint = new Paint();
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setARGB(128, 66, 134, 244);

        // Define the paint that will be used to draw the shape outline
        rectPaintOutline = new Paint();
        rectPaintOutline.setStyle(Paint.Style.STROKE);
        rectPaintOutline.setARGB(255, 66, 134, 244);
    }

    /**
     * Translates the shape by the given amount along each axis.
     * @param deltaX Relative amount to move the shape along the X axis
     * @param deltaY Relative amount to move the shape along the Y axis
     */
    public void move(float deltaX, float deltaY) {
        // Calculate min and max x and y for rectangle center
        float minX = DEFAULT_WIDTH / 2.0f * scale;
        float maxX = 1.0f - minX;
        float minY = minX * ASPECT_RATIO;
        float maxY = 1.0f - minY;

        // Update X
        float temp = locationX + deltaX;
        if (temp > maxX) locationX = maxX;
        else if (temp < minX) locationX = minX;
        else locationX = temp;

        // Update Y
        temp = locationY + deltaY;
        if (temp > maxY) locationY = maxY;
        else if (temp < minY) locationY = minY;
        else locationY = temp;

    }

    /**
     * Will reshape the shape according to the rules for this shape.
     * Rectangles can only be resized.
     * @param deltaDistance The change in distance between the two touch points
     * @param deltaAngle The change in angle between the two touch points
     */
    public void reshape(float deltaDistance, float deltaAngle) {
        // Update the scale with the different in distance
        scale += deltaDistance;

        // Calculate the edges
        updateEdges();

        // Size Restrictions
        if (right - left >= 1.0f) { // Don't expand beyond the board's width
            scale = 1.0f / DEFAULT_WIDTH;
        } else if (right - left <= MINIMUM_WIDTH) { // Minimum width for the rectangle
            scale = MINIMUM_WIDTH / DEFAULT_WIDTH;
        } else if (bottom - top >= 1.0f) { // Don't expand beyond the board's height
            scale = 1.0f / (DEFAULT_WIDTH * ASPECT_RATIO);
        }

        // Recalculate the edges
        updateEdges();
        
        // Move the rectangle if expanding while on a board edge
        float moveX = 0.0f;
        float moveY = 0.0f;
        if (top < 0.0f) moveY = -top;
        else if (bottom > 1.0f) moveY = 1.0f - bottom;
        if (left < 0.0f ) moveX = -left;
        else if (right > 1.0f) moveX = 1.0f - right;
        if (moveX != 0.0f || moveY != 0.0f) move(moveX, moveY);
    }

    /**
     * Handles the drawing of the shape.
     * @param canvas The canvas upon which the shape will be drawn
     * @param marginX The horizontal margin for the game board
     * @param marginY The vertical margin for the game board
     * @param boardSize The side length in pixels for the game board
     */
    public void draw(Canvas canvas, int marginX, int marginY, int boardSize) {
        // Calculate where the edges of the rectangle will be
        float left = -DEFAULT_WIDTH / 2.0f;
        float top = left * ASPECT_RATIO;
        float right = DEFAULT_WIDTH / 2.0f;
        float bottom = right * ASPECT_RATIO;

        // The stroke width needs to be scaled here to counteract the scaling done to the shape
        rectPaintOutline.setStrokeWidth(3.0f/((float)boardSize*scale));

        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + locationX * boardSize, marginY + locationY * boardSize);

        // Scale it to the board size
        canvas.scale(boardSize, boardSize);

        // User adjusted scaling
        canvas.scale(scale, scale);

        // Draw the rectangle
        canvas.drawRect(left, top, right, bottom, rectPaint);
        canvas.drawRect(left, top, right, bottom, rectPaintOutline);

        canvas.restore();
    }

    /**
     * Captures tiles from the game board based on the rules for this shape.
     * @param tiles The list of tiles for the game board
     * @return ArrayList of the indexes of the tiles that were captured
     */
    public ArrayList<Integer> capture(ArrayList<GameTile> tiles) {
        // The list of indexes for the captured tiles
        ArrayList<Integer> capturedTiles = new ArrayList<>();

        // Update the derived edge positions of the rectangle
        updateEdges();

        // Calculate the capture chance
        float captureChance = DEFAULT_CHANCE / (scale*scale);

        for (int i = 0; i < tiles.size(); ++i) {
            if (isTileCapture(tiles.get(i))) {
                // Rectangle have a chance to capture based on scale
                if (Math.random() <= captureChance)
                    capturedTiles.add(i);
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
        bundle.putFloat(SCALE_KEY, scale);
    }

    /**
     * Loads the relevant information for this shape off the bundle.
     * @param bundle The bundle to load off of
     */
    public void loadInstanceState(Bundle bundle) {
        locationX = bundle.getFloat(LOC_X_KEY);
        locationY = bundle.getFloat(LOC_Y_KEY);
        scale = bundle.getFloat(SCALE_KEY);
    }

    /**
     * Updates the derived edge values for the rectangle
     * according to the current scale value.
     */
    private void updateEdges() {
        // Calculate the edges of the capture rectangle
        float baseWidth = DEFAULT_WIDTH * scale;
        float baseHeight = baseWidth * ASPECT_RATIO;
        left = locationX - baseWidth / 2.0f;
        top = locationY - baseHeight / 2.0f;
        right = left + baseWidth;
        bottom = top + baseHeight;
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

        // If the tile is completely inside the rectangle, return true
        return
            tileX - halfSize >= left &&
            tileX + halfSize <= right &&
            tileY - halfSize >= top &&
            tileY + halfSize <= bottom;
    }
}
