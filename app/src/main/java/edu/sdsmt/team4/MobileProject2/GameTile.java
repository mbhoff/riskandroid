package edu.sdsmt.team4.MobileProject2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author  Mark Buttenhoff, Scott Carda
 * Class containing the game tile object and it's associated functions, including
 * the draw function, and accessor and mutator functions for the x y coordinates and the size
 */
class GameTile {
    // Tile coordinates and size
    private float x, y, size;

    // Paint for Tile fill and border
    private final Paint tileFillPaint, tileBorderPaint;


    /**
     * Constructor for the tile object. Initializes the coordinates and size
     * of the tile based on the parameter given, sets the tiles paint
     * to a grey fill, and black border
     *
     * @param context The current context
     * @param initialX initial x coordinate of the tile
     * @param initialY initial y coordinate of the tile
     * @param initialSize the initial size of the tile
     */
    GameTile(Context context, float initialX, float initialY, float initialSize) {
        x = initialX;
        y = initialY;
        size = initialSize;

        tileFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tileFillPaint.setStyle(Paint.Style.FILL);
        tileFillPaint.setColor(Color.GRAY);

        tileBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tileBorderPaint.setStyle(Paint.Style.STROKE);
        tileBorderPaint.setColor(Color.BLACK);
    }


    /**
     * Draw function for the tile object. Takes in the parameters for the margins
     * of the screen, the gameboard size, and fill color for the tile
     * @param canvas The current canvas
     * @param marginX x margin of the screen
     * @param marginY y margin of the screen
     * @param color color to fill the tile with
     */
    void draw(Canvas canvas, int marginX, int marginY, int boardSize, int color) {
        float left = -size / 2.0f;
        float top = left;
        float right = size / 2.0f;
        float bottom = right;

        tileBorderPaint.setStrokeWidth(3.0f / boardSize);
        tileFillPaint.setColor(color);

        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + x * boardSize, marginY + y * boardSize);

        // Scale it to the board size
        canvas.scale(boardSize, boardSize);

        // Draw the capture
        canvas.drawRect(left, top, right, bottom, tileFillPaint);
        canvas.drawRect(left, top, right, bottom, tileBorderPaint);

        canvas.restore();
    }

    /**
     * Mutator function for the x value of the tile
     * @param value value to set the x coordinate to
     */
    public void setX(float value) {
        x = value;
    }

    /**
     * Mutator function for the y value of the tile
     * @param value value to set the y coordinate to
     */
    public void setY(float value) {
        y = value;
    }

    /**
     * Mutator function for the size of the tile
     * @param value value to set the size to
     */
    public void setSize(float value) {
        size = value;
    }

    /**
     * Accessor function for the x coordinate of the tile
     * @return x - x coordinate of the tile
     */
    float getX() {
        return x;
    }

    /**
     * Accessor function for the y coordinate of the tile
     * @return y - y coordinate of the tile
     */
    float getY() {
        return y;
    }

    /**
     * Accessor function for the size of the tile
     * @return size - size of the tile
     */
    float getSize() {
        return size;
    }
}
