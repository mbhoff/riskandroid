package edu.sdsmt.team4.MobileProject2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Static class that is used to time for timeout logic.
 */
class Timeout {
    /**
     * Number of milliseconds in between timer ticks.
     */
    private static final int TIME_INTERVAL = 100;

    /**
     * Interface for the callback object that will be performed at end of timer thread.
     */
    interface TimeoutCallback {
        void onTimeout();
    }

    /**
     * The callback object that will be performed upon completion of the timer thread.
     */
    private static volatile TimeoutCallback timeoutCallback;

    /**
     * The handler for the thread that will perform the callback.
     */
    private static volatile Handler postThread;

    /**
     * Stored times for timing.
     */
    private static volatile long startTime;
    private static volatile long elapsedTime;
    private static volatile long stopTime;

    /**
     * Whether or not the timer thread is currently running.
     */
    private static volatile boolean active = false;

    /**
     * Starts a timing operation. Does nothing if a timer is already running.
     * @param totalTime How long the timer goes before calling the callback.
     * @param callback The callback object called when the timer is finished.
     */
    static void startTiming(long totalTime, final TimeoutCallback callback) {
        // Set the stopping time and the callback object
        stopTime = totalTime;
        timeoutCallback = callback;

        // set the thread that calls this function as the post thread
        postThread = new Handler(Looper.getMainLooper());

        // Start the timer thread
        new Thread(() -> {
            active = true; // Declare the thread to be active
            elapsedTime = 0L; // Start elapsed time at zero
            startTime = System.currentTimeMillis(); // Start the start time to now

            // Time elapsing loop
            while (active && elapsedTime < stopTime) {
                try {
                    Thread.sleep(TIME_INTERVAL);
                } catch (InterruptedException e) {
                    Log.e("TimeoutSleep", "Timeout thread interrupted on sleep.");
                    return; // end the thread
                }

                // Update elapsed time
                elapsedTime = System.currentTimeMillis() - startTime;
            }

            // Don't post if the thread was 'stopped' instead of ended
            if (active) {
                postThread.post(() -> {
                    // Call the callback
                    timeoutCallback.onTimeout();
                });
            }

            // Declare the thread to be inactive
            active = false;
        }).start();
    }

    /**
     * Will force the timer to end prematurely, causing post to callback.
     */
    public static void forceEnd() {
        stopTime = 0L;
    }

    /**
     * Will force the timer to restart.
     */
    public static void reset() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Will force the timer to end prematurely, preventing post to callback.
     */
    public static void stop() {
        active = false;
    }
}
