package edu.sdsmt.team4.MobileProject2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * @author Scott Carda, Krey Warsaw
 * This activity allows the user to select what capture
 * shape they will use in the game activty to capture game tiles.
 */
public class CaptureActivity extends AppCompatActivity {
    /**
     * Enum symbols for the different kinds of capture shapes.
     */
    public final static int CAPTURE_POINT = 0;
    public final static int CAPTURE_LINE = 1;
    public final static int CAPTURE_RECTANGLE = 2;

    /**
     * Key strings for bundles.
     */
    public final static String CAPTURE_KEY = "Capture";
    private final static String GAME_KEY = "state";

    /**
     * Which capture shape variety is selected.
     */
    private int captureSelected = CAPTURE_POINT;

    /**
     * Object for storing the game state
     */
    private GameState game;

    /**
     * Handles the On Create event for the activity.
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        // Get the game state from the last activity
        game = getIntent().getExtras().getParcelable(GAME_KEY);

        // Set up the radio buttons to change the captureSelected member.
        RadioGroup rg = findViewById(R.id.CaptureSelection);
        rg.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i)   {
                case R.id.LineCaptureSelect:
                    captureSelected = CAPTURE_LINE;
                    break;
                case R.id.RectCaptureSelect:
                    captureSelected = CAPTURE_RECTANGLE;
                    break;
                case R.id.PointCaptureSelect:
                default:
                    captureSelected = CAPTURE_POINT;
                    break;
            }
        });
    }

    /**
     * Handles when a new intent is passed to the capture activity.
     * @param intent the current intent
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * Handles the On Resume event for this activity.
     */
    @Override
    public void onResume() {
        super.onResume();
        game = getIntent().getExtras().getParcelable(GAME_KEY);

        game.getMonitor().Activity = getString(R.string.CaptureActivity);
        game.uploadMonitor();

        // Set the turn label to the current player's name
        ((TextView) findViewById(R.id.PlayerTurn)).setText(String.format(getString(R.string.PlayerTurn), game.getCurrRound()));
    }

    /**
     * Handles the back button being pressed when on this activity.
     */
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.back_title);
        builder.setMessage(R.string.back_msg);
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            Intent intent = new Intent(this, WaitActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    /**
     * Handles the On Click event for the Select Capture button.
     * @param view the current view
     */
    public void onNextButtonClick(View view) {
        String captureStr = "";

        switch (captureSelected) {
            case CAPTURE_LINE:
                captureStr = getString(R.string.Line);
                break;
            case CAPTURE_POINT:
                captureStr = getString(R.string.Point);
                break;
            case CAPTURE_RECTANGLE:
                captureStr = getString(R.string.Rectangle);
                break;
        }

        game.getMonitor().CapturePiece = captureStr;
        game.uploadMonitor();

        Intent intent = new Intent(this, GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(CAPTURE_KEY, captureSelected);
        intent.putExtra(GAME_KEY, game);
        startActivity(intent);
    }
}
