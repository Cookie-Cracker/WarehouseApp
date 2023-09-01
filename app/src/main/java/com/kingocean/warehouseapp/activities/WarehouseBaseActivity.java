package com.kingocean.warehouseapp.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kingocean.warehouseapp.utils.Constants;

public class WarehouseBaseActivity extends AppCompatActivity {
    private static final String TAG = "IdleTimeoutExample";
    private static final long IDLE_TIMEOUT = Constants.IDLE_TIMEOUT;
    private static final long NOTIFICATION_INTERVAL = Constants.NOTIFICATION_INTERVAL;
    private CountDownTimer idleTimer;
    private CountDownTimer notificationTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize and start the timers
        initTimers();
    }

    private void initTimers() {
        Log.d(TAG, "Initializing timers");

        // Initialize the idle timer
        idleTimer = new CountDownTimer(IDLE_TIMEOUT, NOTIFICATION_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                Log.d(TAG, "Idle time remaining: " + secondsLeft + " seconds");

                // Example: Show a notification every 10 seconds
                if (secondsLeft <= NOTIFICATION_INTERVAL / 1000) {
                    showNotification("You will be logged out in " + secondsLeft + " seconds.");
                }
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Idle timer finished: User logged out");
                // Example: Log out the user or perform necessary actions
                // logoutUser();
            }
        };

        // Initialize the notification timer
        notificationTimer = new CountDownTimer(IDLE_TIMEOUT, NOTIFICATION_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Not used in this example
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Notification timer finished: Resetting for the next notification");
                // Reset the timer to keep notifying the user
                notificationTimer.start();
            }
        };

        // Start both timers
        idleTimer.start();
        notificationTimer.start();
    }

    private void showNotification(String message) {
        Log.d(TAG, "Showing notification: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
