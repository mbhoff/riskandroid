package edu.sdsmt.team4.MobileProject2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * @author  Mark Buttenhoff, Scott Carda, Krey Warshaw
 * Class containing all of the gameboard relevent variables, tile, capture, and game state objects
 * and their associated functions, as well as functions to handle touch events, and the gameboard
 * draw function.
 */
class GameBoard {
    /**
     * Percentage of the display width or height that
     * is occupied by the board.
     */
    private final static float SCALE_IN_VIEW = 0.9f;

    /**
     * The size of the board in pixels
     */
    private int boardSize;

    /**
     * Left margin in pixels
     */
    private int marginX;
    /**
     * Top margin in pixels
     */
    private int marginY;
    /**
     * Generic shape for gameboard capture
     */
    private CaptureShape capture;
    /**
     * Object storing the game state
     */
    private GameState game;
    /**
     * Single tap ID
     */
    private int touch1Id = -1;
    /**
     * Double tap ID
     */
    private int touch2Id = -1;
    /**
     * X coordinate of first tap
     */
    private float touch1X = 0.0f;
    /**
     * Y coordinate of first tap
     */
    private float touch1Y = 0.0f;
    /**
     * X coordinate of second tap
     */
    private float touch2X = 0.0f;
    /**
     * Y coordinate of second tap
     */
    private float touch2Y = 0.0f;
    /**
     * Distance between two points
     */
    private float touchDist = 0.0f;
    /**
     * Angle between two points
     */
    private float touchAngle = 0.0f;
    /**
     * ArrayList of tiles on the gameboard
     */
    private final ArrayList<GameTile> tiles = new ArrayList<>();

    /**
     * Sets the capture shape to the passed in shape
     */
    void setCaptureShape(CaptureShape captureShape) {
        capture = captureShape;
    }

    /**
     * Sets the game state to the passed in state
     */
    void setGameState(GameState game) {
        this.game = game;
    }

    /**
     * The constructor for the GameBoard class
     * @param context the current context
     * @param view the current view
     */
    GameBoard(Context context, GameBoardView view) {
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 10; ++j) {
               tiles.add(new GameTile(context, (float)i/10 + 0.05f, (float)j/10 + 0.05f, 0.1f));
            }
        }
    }

    /**
     * Draws the board on the canvas
     * @param canvas The canvas that the board is drawn on
     */
    void draw(Canvas canvas) {
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions
        int minDim = wid < hit ? wid : hit;

        boardSize = (int)(minDim * SCALE_IN_VIEW);

        // Compute the margins so we center the puzzle
        marginX = (wid - boardSize) / 2;
        marginY = (hit - boardSize) / 2;

        // Set gameTile to color of player who captured
        for (int i = 0; i < tiles.size(); ++i) {
            int color = Color.GRAY;
            if (game.whoOwns(i) ==  0)
                color = Color.RED;
            else if (game.whoOwns(i) == 1)
                color = Color.BLUE;
            tiles.get(i).draw(canvas, marginX, marginY, boardSize, color);
        }

        capture.draw(canvas, marginX, marginY, boardSize);
    }

    /**
     * Updates the list of captured tiles on the grid and
     * increments the score
     */
    void onCapture() {
        ArrayList<Integer> capturedTiles = capture.capture(tiles);
        for (Integer i: capturedTiles) {
            if (game.whoOwns(i) == 2) {
                game.setMyScore(game.getMyScore() + 1);
            } else if (game.whoOwns(i) != GameState.whoAmI) {
                game.setMyScore(game.getMyScore() + 1);
                game.setOpponentScore(game.getOpponentScore() - 1);
            }
            game.capZone(i);
        }
    }

    /**
     * Handle a touch event from the view.
     * @param view The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled (it always is)
     */
    boolean onTouchEvent(final View view, final MotionEvent event) {
        final int id = event.getPointerId(event.getActionIndex());

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touch1Id = id;
                touch2Id = -1;
                getPositions(event);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (touch1Id >= 0 && touch2Id < 0) {
                    touch2Id = id;
                    getPositions(event);
                    touchDist = pointDistance(touch1X, touch1Y, touch2X, touch2Y);
                    touchAngle = pointAngle(touch1X, touch1Y, touch2X, touch2Y);
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touch1Id = -1;
                touch2Id = -1;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (id == touch1Id) {
                    touch1Id = touch2Id;
                    touch1X = touch2X;
                    touch1Y = touch2Y;
                }
                if (id == touch1Id || id == touch2Id) {
                    touch2Id = -1;
                    touchDist = 0.0f;
                    touchAngle = 0.0f;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2 && touch1Id >= 0 && touch2Id >= 0)
                    onReshape(event);
                else if (event.getPointerCount() == 1 && touch1Id >= 0)
                    onDragged(event);

                view.invalidate();
                break;
        }

        return true;
    }

    /**
     * Handle the moving a capture shape.
     * @param event Motion event
     */
    private void onDragged(final MotionEvent event) {
        final float relX =  (event.getX() - marginX) / boardSize;
        final float relY =  (event.getY() - marginY) / boardSize;

        capture.move(relX - touch1X, relY - touch1Y);
        touch1X = relX;
        touch1Y = relY;
    }

    /**
     * Handle the reshaping of a capture shape.
     * @param event Motion event
     */
    private void onReshape(final MotionEvent event) {
        float new1X = 0.0f;
        float new1Y = 0.0f;
        float new2X = 0.0f;
        float new2Y = 0.0f;

        // get coordinates of the touch ids
        for (int i = 0; i < event.getPointerCount(); ++i) {
            final int id = event.getPointerId(i);
            if (id == touch1Id) {
                new1X = (event.getX(i) - marginX) / boardSize;
                new1Y = (event.getY(i) - marginY) / boardSize;
            } else if (id == touch2Id) {
                new2X = (event.getX(i) - marginX) / boardSize;
                new2Y = (event.getY(i) - marginY) / boardSize;
            }
        }

        // get distance and angle of the coordinates on touch and release
        final float dist = pointDistance(new1X, new1Y, new2X, new2Y);
        final float angle = pointAngle(new1X, new1Y, new2X, new2Y);

        // reshape the shape by subtracting distance and angle
        capture.reshape(dist - touchDist, angle - touchAngle);
        touchDist = dist;
        touchAngle = angle;
        touch1X = new1X;
        touch1Y = new1Y;
        touch2X = new2X;
        touch2Y = new2Y;
    }

    /**
     * Saves the relevant information for the board to the bundle.
     * @param bundle The bundle to save on to
     */
    void saveInstanceState(Bundle bundle) {
        capture.saveInstanceState(bundle);
    }

    /**
     * Loads the relevant information for the board off the bundle.
     * @param bundle The bundle to load off of
     */
    void loadInstanceState(Bundle bundle) {
        capture.loadInstanceState(bundle);
    }


    /**
     * Gets the coordinates of a first or second screen tap
     * @param event touch motion event
     */
    private void getPositions(final MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); ++i) {
            int id = event.getPointerId(i);
            if (id == touch1Id) {
                touch1X = (event.getX(i) - marginX) / boardSize;
                touch1Y = (event.getY(i) - marginY) / boardSize;
            } else if (id == touch2Id) {
                touch2X = (event.getX(i) - marginX) / boardSize;
                touch2Y = (event.getY(i) - marginY) / boardSize;
            }
        }
    }


    /**
     * Calculates the distance between two points
     * @param x1 x coordinate of first point
     * @param y1 y coordinate of first point
     * @param x2 x coordinate of second point
     * @param y2 y coordinate of second point
     * @return distance
     */
    private float pointDistance(float x1, float y1, float x2, float y2) {
        float deltaX = x1 - x2;
        float deltaY = y1 - y2;
        return (float)Math.sqrt(deltaX*deltaX + deltaY*deltaY);
    }

    /**
     * Calculates the angle between two points
     * @param x1 x coordinate of first point
     * @param y1 y coordinate of first point
     * @param x2 x coordinate of second point
     * @param y2 y coordinate of second point
     * @return angle in degrees
     */
    private float pointAngle(float x1, float y1, float x2, float y2) {
        float deltaX = x1 - x2;
        float deltaY = y1 - y2;
        return (float)(360.0 * Math.atan2(deltaY,deltaX) / (2.0 * Math.PI));
    }
}
