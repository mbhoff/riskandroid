package edu.sdsmt.team4.MobileProject2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class WaitActivity extends AppCompatActivity {
    private final static String GAME_KEY = "state";

    private static final int TIMEOUT_TIME = 2*60*1000; // 2 minutes
    //private static final int TIMEOUT_TIME = 5000; // 5 seconds

    private static boolean isTimedOut;
    private static Monitor _monitor;
    private static GameState _gameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
    }

    @Override
    public void onResume() {
        super.onResume();
        MonitorCloud.getMonitorRef().addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void onMonitorGet(Monitor monitor) {
        // Make new GS with only this player's name in it and logged in
        _gameState = new GameState();

        if (monitor != null && !monitor.isGameStarted()) {
            GameState.whoAmI = 1; // second player
            _monitor = monitor;
            _monitor.startGame();
            MonitorCloud.putMonitor(_monitor);
            goToActivity(MonitorActivity.class);
        } else {
            // make new gs and upload it
            GameState.whoAmI = 0; // first player
            _monitor = new Monitor();
            MonitorCloud.uploadGame(_gameState);
            MonitorCloud.putMonitor(_monitor);
            waitForPlayer();
        }
    }

    private void waitForPlayer() {
        final Handler UIThread = new Handler();

        isTimedOut = false;
        Timeout.startTiming(TIMEOUT_TIME, () -> isTimedOut = true);

        MonitorCloud.getMonitorRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                _monitor = Monitor.fromJSON(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("monitor_state data", "The read failed: " + databaseError.getCode());
            }
        });

        new Thread(() -> {
            while (!_monitor.isGameStarted() && !isTimedOut) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            UIThread.post(() -> {
                if (isTimedOut) goToActivity(ScoreActivity.class);
                else goToActivity(CaptureActivity.class);
            });
        }).start();
    }

    private void goToActivity(final Class activity) {
        Intent intent = new Intent(this, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        _gameState.startGame();
        intent.putExtra(GAME_KEY, _gameState);
        startActivity(intent);
    }
}
