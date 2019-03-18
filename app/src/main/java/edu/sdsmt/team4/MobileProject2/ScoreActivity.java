package edu.sdsmt.team4.MobileProject2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * @author Krey Warshaw, Scott Carda, Mark Buttenhoff
 * Class to initialize the Score Activity, displaying the
 * winners and scores of each player.
 */
public class ScoreActivity extends AppCompatActivity {
    /**
     * Key for storing into the intent
     */
    private final static String GAME_KEY = "state";

    /**
     * Creates the Activity
     * <p>
     * Creates the activity and sets the text of text views in the activity
     * for the winner and the players' scores
     * @param savedInstanceState a bundle to build the activity from
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // Get the current game state
        GameState game = getIntent().getExtras().getParcelable(GAME_KEY);
        assert game != null;

        // Reset a timed-out game
        game.startGame();
        game.uploadMonitor();

        TextView temp = findViewById(R.id.winnerMsg);
        if (game.didIWin())
            temp.setText(getString(R.string.winning_msg));
        else
            temp.setText(getString(R.string.losing_msg));

        temp = findViewById(R.id.YourScore);
        temp.setText(String.format(getString(R.string.score_string), getString(R.string.Name_of_You), game.getMyScore()));

        temp = findViewById(R.id.TheirScore);
        temp.setText(String.format(getString(R.string.score_string), getString(R.string.Name_of_They), game.getOpponentScore()));
    }
    /**
     * Back Button Override
     * <p>
     * prompts user to go back to main menu instead of allowing user to go back to
     * the game activity
     */
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.back_title);
        builder.setMessage(R.string.back_msg);
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> goToWait());

        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    /**
     * handles next button click
     * <p>
     * Sends game back to Main activity
     */
    public void onNextButtonClick(View view) {
        goToWait();
    }

    /**
     * Return to main activity
     * <p>
     * Goes back to main activity using single top
     */
    private void goToWait(){
        Intent intent = new Intent(this, WaitActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
