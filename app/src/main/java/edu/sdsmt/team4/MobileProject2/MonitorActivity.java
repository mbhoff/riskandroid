package edu.sdsmt.team4.MobileProject2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MonitorActivity extends AppCompatActivity {
    private static final int TIMEOUT_TIME = 2*60*1000; // 2 minutes
    //private static final int TIMEOUT_TIME = 5000;

    private final static String GAME_KEY = "state";

    private static boolean isTimedOut;
    private static Monitor _monitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
    }

    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.StartTurnButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.StartTurnButton).invalidate();

        isTimedOut = false;
        Timeout.startTiming(TIMEOUT_TIME, this::timeout);

        MonitorCloud.getMonitorRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                onMonitorGet(Monitor.fromJSON(snapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("monitor_state data", "The read failed: " + databaseError.getCode());
            }
        });
    }

    private void timeout() {
        Toast.makeText(this, "The game has Timed Out", Toast.LENGTH_SHORT).show();
        isTimedOut = true;
        findViewById(R.id.StartTurnButton).setVisibility(View.VISIBLE);
        findViewById(R.id.StartTurnButton).invalidate();
    }

    private void onMonitorGet(Monitor monitor) {
        _monitor = monitor;

        if (monitor.PlayerTurn == GameState.whoAmI) {
            findViewById(R.id.StartTurnButton).setVisibility(View.VISIBLE);
            findViewById(R.id.StartTurnButton).invalidate();
        }

        int myScore, theirScore;

        if (GameState.whoAmI == 0) {
            myScore = _monitor.Player1Score;
            theirScore = _monitor.Player2Score;
        } else {
            myScore = _monitor.Player2Score;
            theirScore = _monitor.Player1Score;
        }

        ((TextView)findViewById(R.id.PlayerScore)).setText(
                String.format(
                        getString(R.string.MonitorScoreString),
                        getString(R.string.Name_of_Your),
                        myScore
                        ));

        ((TextView)findViewById(R.id.OpponentScore)).setText(
                String.format(
                        getString(R.string.MonitorScoreString),
                        getString(R.string.Name_of_Their),
                        theirScore
                ));

        ((TextView)findViewById(R.id.OpponentActivity)).setText(_monitor.Activity);
        ((TextView)findViewById(R.id.OpponentShape)).setText(_monitor.CapturePiece);
    }

    public void StartTurn_onClick(View view) {
        MonitorCloud.getGameRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                GameState gs = GameState.fromJSON(snapshot);
                assert gs != null;
                gs.setMonitor(_monitor);
                if (_monitor.GameOver || isTimedOut) goToActivity(ScoreActivity.class, gs);
                else goToActivity(CaptureActivity.class, gs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("monitor_state data", "The read failed: " + databaseError.getCode());
            }
        });
    }

    private void goToActivity(final Class activity, final GameState gs) {
        final Intent intent = new Intent(this, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(GAME_KEY,gs);
        startActivity(intent);
    }
}
