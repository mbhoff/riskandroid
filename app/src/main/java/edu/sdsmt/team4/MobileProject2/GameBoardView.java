package edu.sdsmt.team4.MobileProject2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author  Mark Buttenhoff, Scott Carda
 * Class containing methods for the GameBoard view
 * Contains functions to initialize a gameboard object, and set its capture and game state,
 * and functions that call the onCapture, draw, save, load and touch event methods of the gameboard.
 */
public class GameBoardView extends View {
    /**
     * The gameboard object
     */
    private final GameBoard board;

    /**
     * GameBoardView Constructors
     */
    public GameBoardView(Context context) {
        super(context);
        board = new GameBoard(getContext(), this);
    }
    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        board = new GameBoard(getContext(), this);
    }
    public GameBoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        board = new GameBoard(getContext(), this);
    }

    /**
     * Sets the gameboard object's capture shape to a shape passed in
     * @param capture the capture shape object to be set to
     */
    public void setCaptureShape(CaptureShape capture) {
        board.setCaptureShape(capture);
    }

    /**
     * Sets the gameboard object's game state to a shape passed in
     * @param game - the game state to be set to
     */
    public void setGameState(GameState game) {
        board.setGameState(game);
    }

    /**
     * Calls the gameboards onCapture function and invalidates the view
     */
    public void onCapture() {
        board.onCapture();
        invalidate();
    }

    /**
     * Saves the relevant information for the view to the bundle.
     * @param bundle The bundle to save on to
     */
    public void saveInstanceState(final Bundle bundle) {
        board.saveInstanceState(bundle);
    }

    /**
     * Loads the relevant information for the view off the bundle.
     * @param bundle The bundle to load off of
     */
    public void loadInstanceState(final Bundle bundle) {
        board.loadInstanceState(bundle);
    }


    /**
     * Redraws the canvas
     * @param canvas - the current canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        board.draw(canvas);
    }

    /**
     * Calls the gameboard object's onTouchEvent method
     * @param event - the touch screen event being handled
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return board.onTouchEvent(this, event);
    }
}
