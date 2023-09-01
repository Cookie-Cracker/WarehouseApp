package com.kingocean.warehouseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.kingocean.warehouseapp.activities.AppsActivity;
import com.kingocean.warehouseapp.ui.login.LoginActivity;

import java.sql.Connection;

public class MainActivity extends AppCompatActivity {

    Connection connect;
    String ConnectionResult = "";

//    private static final long IDLE_TIMEOUT = 60 * 1000;
//    private static final long NOTIFICATION_INTERVAL = 10 * 1000;
//    private CountDownTimer idleTimer;
//    private CountDownTimer notificationTimer;

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*setContentView(R.layout.activity_main);*/
        String TAG = "IDLE";
        Log.d(TAG, "Activity created");

//        idleTimer = new CountDownTimer(IDLE_TIMEOUT, NOTIFICATION_INTERVAL) {
//            @Override
//            public void onTick(long l) {
//                // Notify the user every NOTIFICATION_INTERVAL
//                long secondsLeft = l / 1000;
//                Log.d(TAG, "Idle time remaining: " + secondsLeft + " seconds");
//                if (secondsLeft <= NOTIFICATION_INTERVAL / 1000) {
//                    showNotification("You will be logged out in " + secondsLeft + " seconds.");
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                Log.d(TAG, "Idle timer finished: User logged out");
//            }
//        };
//        // Initialize the notification timer
//        notificationTimer = new CountDownTimer(IDLE_TIMEOUT, NOTIFICATION_INTERVAL) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                // Not used in this example
//            }
//
//            @Override
//            public void onFinish() {
//                Log.d(TAG, "Notification timer finished: Resetting for the next notification");
//                // Reset the timer to keep notifying the user
//                notificationTimer.start();
//            }
//        };
//        idleTimer.start();
//        notificationTimer.start();
//
//        getWindow().getDecorView().setOnTouchListener((View v, MotionEvent event) -> {
//            Log.i(TAG, "onCreate:" + event.getAction());
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//                resetIdleTime();
//            }
//            return false;
//        });


        SharedPreferences preferences = getSharedPreferences("credentials", MODE_PRIVATE);
        //String id = preferences.getString("id", "");
        String username = preferences.getString("username", "");
        //String password = preferences.getString("password", "");

        // ------------------------------------------------------------------------------------------------------------
        // When building the WAREHOUSE app, uncomment the six lines below
        // ------------------------------------------------------------------------------------------------------------
        if (username.equalsIgnoreCase("")) {
            goLogin();
        } else {
            goAppSelector();
        }


        // ------------------------------------------------------------------------------------------------------------
        // When building the RECEPTION/ENTRANCE app, comment the six lines above and uncomment the two lines below
        // ------------------------------------------------------------------------------------------------------------

        //Intent intent = new Intent(this, VisitorsLogActivity.class);
        //startActivity(intent);

//    goRepack();
    }

    private void showNotification(String message) {
        Log.d("IDLE", "Showing notification: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

//    private void resetIdleTime() {
//        Log.d("IDLE", "User interacted: Resetting timers");
//        idleTimer.cancel();
//        idleTimer.start();
//        notificationTimer.cancel();
//        notificationTimer.start();
//    }

//    public void goRepack(){
//        Intent intent = new Intent(this, RepackActivity.class);
//        startActivity(intent);
//    }

    public void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        //overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    public void goAppSelector() {
//        Intent intent = new Intent(this, AppSelectorActivity.class);
//        startActivity(intent);

        Intent intent = new Intent(this, AppsActivity.class);
        startActivity(intent);

        //overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
    }

}
