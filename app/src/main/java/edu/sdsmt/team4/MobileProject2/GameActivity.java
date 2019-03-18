package edu.sdsmt.team4.MobileProject2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static edu.sdsmt.team4.MobileProject2.CaptureActivity.CAPTURE_KEY;
import static edu.sdsmt.team4.MobileProject2.CaptureActivity.CAPTURE_LINE;
import static edu.sdsmt.team4.MobileProject2.CaptureActivity.CAPTURE_POINT;
import static edu.sdsmt.team4.MobileProject2.CaptureActivity.CAPTURE_RECTANGLE;

/**
 * @author Krey Warshaw, Scott Carda, Mark Buttenhoff
 * Class to initialize the GameActivity, and handle saving and button press events.
 */
public class GameActivity extends AppCompatActivity {
    /**
     * The gameboard view
     */
    private GameBoardView gameBoardView;

    /**
     * Button that advances the game
     */
    private Button nextButton;

    /**
     * Boolean indicating if in the capture phase of the game
     */
    private Boolean capturePhase;

    /**
     * Object for storing the game state
     */
    private GameState game;

    /**
     * Label for the Game_KEY intent
     */
    private final static String GAME_KEY = "state";

    /**
     * Label for the PHASE_KEY intent
     */
    private final static String PHASE_KEY = "phase";

    /**
     * Handles game activity launch.
     * Initializes the game activity. Sets the content View,
     * and receives an intent that contains updated state of
     * the captured tiles on the gameboard.
     * The turn view is set to the current players turn.
     * A CaptureShape object is created and the constructor
     * of the corresponding child class for the type of shape is called.
     * gameBoardView's capture shape and game state are updated.
     * If there is a bundle already created it's state is loaded.
     * The nextButton's label is changed based on the phase of the game.
     * @param bundle Game activity state
     */
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game);

        Intent receivedIntent = getIntent();
        int captureIndicator = receivedIntent.getIntExtra(CAPTURE_KEY, CAPTURE_POINT);
        game = receivedIntent.getExtras().getParcelable(GAME_KEY);

        game.getMonitor().Activity = getString(R.string.GameActivity);
        game.uploadMonitor();

        // Set the turn label to the current player's name
        ((TextView) findViewById(R.id.TurnText)).setText(String.format(getString(R.string.PlayerTurn), game.getCurrRound()));

        CaptureShape captureShape;
        float startX = 0.5f;
        float startY = 0.5f;
        switch(captureIndicator) {
            case CAPTURE_LINE:
                captureShape = new CaptureLine(startX, startY);
                break;
            case CAPTURE_RECTANGLE:
                captureShape = new CaptureRectangle(startX, startY);
                break;
            case CAPTURE_POINT:
            default:
                captureShape = new CapturePoint(startX, startY);
        }

        gameBoardView = this.findViewById(R.id.gameBoardView);
        gameBoardView.setCaptureShape(captureShape);
        gameBoardView.setGameState(game);

        // Load from the bundle if there is one
        if (bundle != null) {
            capturePhase = bundle.getBoolean(PHASE_KEY);
            gameBoardView.loadInstanceState(bundle);
        } else capturePhase = true;

        nextButton = this.findViewById(R.id.nextButton);
        if (capturePhase)
            nextButton.setText(R.string.Capture);
        else
            nextButton.setText(R.string.EndTurn);
    }

    /**
     * Handles game state saving.
     * Saves the instance state to a bundle, and sets the boolean
     * capturePhase to true or false based on the current game phase
     * @param bundle Game activity state
     */
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(PHASE_KEY, capturePhase);
        gameBoardView.saveInstanceState(bundle);
    }

    /**
     * Handles the back button being pressed when on this activity.
     */
    @Override
    public void onBackPressed() {
        if (capturePhase) {
            goToCapture();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.back_title);
            builder.setMessage(R.string.back_msg);
            builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {});
            builder.setNegativeButton(R.string.cancel, null);
            builder.show();
        }
    }

    /**
     * Handles button press for the next button.
     * If in the capture phase, the button's text is changed
     * to "End Turn" and the capturePhase boolean is set to false.
     * If in the selection phase, and there is another round left,
     * the next player is loaded, and a new Capture Activity Intent is created and started.
     * @param view game board View
     */
    public void onNextButtonClick(View view) {
        Intent intent;

        if (capturePhase) {
            gameBoardView.onCapture();
            nextButton.setText(R.string.EndTurn);
            capturePhase = false;
        } else {
            game.nextPlayer();

            if (game.getCurrRound() > game.getRounds()) {
                intent = new Intent(this, ScoreActivity.class);
                game.endGame();
            }
            else
                intent = new Intent(this, MonitorActivity.class);

            game.writeJSON(MonitorCloud.getGameRef());
            game.uploadMonitor();

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(GAME_KEY, game);
            startActivity(intent);
        }
    }

    /**
     * Navigates to the Capture Activity.
     */
    private void goToCapture(){
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(GAME_KEY, game);
        startActivity(intent);
    }
}
