package edu.sdsmt.team4.MobileProject2;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * @author Scott Carda
 * The class defining the capture line shape. Implements CaptureShape.
 */
class CaptureLine implements CaptureShape {
    /**
     * Relative length of the line; remains constant.
     */
    private final static float DEFAULT_LENGTH = 0.30f;

    /**
     * Lines have a 50% chance to capture; remains constant.
     */
    private final static float CAPTURE_CHANCE = 0.50f;

    /**
     * Relative x and y coordinates for the location of the shape.
     */
    private float locationX;
    private float locationY;

    /**
     * Angle the line takes with the X axis.
     */
    private float angle = 0.0f;

    /**
     * Bundle keys for relevant information for this shape.
     */
    private final static String LOC_X_KEY = "line x key";
    private final static String LOC_Y_KEY = "line y key";
    private final static String ANGLE_KEY = "line angle key";

    /**
     * Derived values stored as data members to make the capture logic more efficient.
     * These are the x and y coordinates of the endpoints of the line,
     * and the slope of the line.
     */
    private float x1, y1, x2, y2, slope;

    /**
     * Paint object for drawing the shape.
     */
    private final Paint linePaint;

    /**
     * The constructor for the CaptureLine class
     * @param x Relative location for the shape along the X axis
     * @param y Relative location for the shape along the Y axis
     */
    CaptureLine(float x, float y) {
        // Assign the location of the shape
        locationX = x;
        locationY = y;

        // Define the paint that will be used to draw the shape
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setARGB(255, 66, 134, 244);
    }

    /**
     * Translates the shape by the given amount along each axis.
     * @param deltaX Relative amount to move the shape along the X axis
     * @param deltaY Relative amount to move the shape along the Y axis
     */
    public void move(float deltaX, float deltaY) {
        float angleRad = angle/180f*(float)Math.PI;
        float minX = Math.abs(DEFAULT_LENGTH / 2.0f * (float)Math.cos(angleRad));
        float maxX = 1.0f - minX;
        float minY = Math.abs(DEFAULT_LENGTH / 2.0f * (float)Math.sin(angleRad));
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
     * Lines can only be rotated.
     * @param deltaDistance The change in distance between the two touch points
     * @param deltaAngle The change in angle between the two touch points
     */
    public void reshape(float deltaDistance, float deltaAngle) {
        angle += deltaAngle; // The change in angle causes the shape to rotate
        move( 0.0f, 0.0f ); // Move the line back into the board if it rotated out
    }

    /**
     * Handles the drawing of the shape.
     * @param canvas The canvas upon which the shape will be drawn
     * @param marginX The horizontal margin for the game board
     * @param marginY The vertical margin for the game board
     * @param boardSize The side length in pixels for the game board
     */
    public void draw(Canvas canvas, int marginX, int marginY, int boardSize) {
        // Calculate where the line will start and stop
        float startX = -DEFAULT_LENGTH / 2.0f;
        float startY = 0.0f;
        float stopX = DEFAULT_LENGTH / 2.0f;
        float stopY = 0.0f;

        // The stroke width needs to be scaled here to counteract the scaling done to the shape
        linePaint.setStrokeWidth(3.0f/(float)boardSize);

        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + locationX * boardSize, marginY + locationY * boardSize);

        // User adjusted rotating
        canvas.rotate(angle);

        // Scale it to the board size
        canvas.scale(boardSize, boardSize);

        // Draw the line
        canvas.drawLine(startX, startY, stopX, stopY, linePaint);

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

        // Calculate the endpoints of the capture line
        float angleRad = angle/180f*(float)Math.PI;
        x1 = locationX - DEFAULT_LENGTH / 2.0f * (float)Math.cos(angleRad);
        y1 = locationY - DEFAULT_LENGTH / 2.0f * (float)Math.sin(angleRad);
        x2 = locationX + DEFAULT_LENGTH / 2.0f * (float)Math.cos(angleRad);
        y2 = locationY + DEFAULT_LENGTH / 2.0f * (float)Math.sin(angleRad);

        // Calculate the slope of the capture line
        slope = (float)Math.sin(angleRad) / (float)Math.cos(angleRad);

        for (int i = 0; i < tiles.size(); ++i) {
            if (isTileCapture(tiles.get(i))) {
                // Lines have a 50% chance to capture
                if (Math.random() <= CAPTURE_CHANCE)
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
        bundle.putFloat(ANGLE_KEY, angle);
    }

    /**
     * Loads the relevant information for this shape off the bundle.
     * @param bundle The bundle to load off of
     */
    public void loadInstanceState(Bundle bundle) {
        locationX = bundle.getFloat(LOC_X_KEY);
        locationY = bundle.getFloat(LOC_Y_KEY);
        angle = bundle.getFloat(ANGLE_KEY);
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

        // Find the min and max x's and y's
        float minX = Math.min(x1, x2);
        float maxX = Math.max(x1, x2);
        float minY = Math.min(y1, y2);
        float maxY = Math.max(y1, y2);

        // Check if tile is beyond bounds for line segment
        if ( tileX + halfSize < minX ) return false;
        if ( tileX - halfSize >= maxX ) return false;
        if ( tileY + halfSize < minY ) return false;
        if ( tileY - halfSize >= maxY ) return false;

        // If line passes through left edge of tile
        float calcY = slope * ( tileX - halfSize - x1 ) + y1;
        if ( calcY <= tileY + halfSize && calcY > tileY - halfSize ) return true;

        // If line passes through right edge of tile
        calcY = slope * ( tileX + halfSize - x1 ) + y1;
        if ( calcY <= tileY + halfSize && calcY > tileY - halfSize ) return true;

        // If line passes through top edge of tile
        float calcX = ( tileY - halfSize - y1 ) / slope + x1;
        if ( calcX <= tileX + halfSize && calcX > tileX - halfSize ) return true;

        // if line passes through bottom edge of tile
        calcX = ( tileY + halfSize - y1 ) / slope + x1;
        return calcX <= tileX + halfSize && calcX > tileX - halfSize;
    }
}
