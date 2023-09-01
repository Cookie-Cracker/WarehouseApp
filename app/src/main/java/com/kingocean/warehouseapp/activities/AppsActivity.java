package com.kingocean.warehouseapp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.kingocean.warehouseapp.LocationsActivity;
import com.kingocean.warehouseapp.MainReceiptActivity;
import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.ScanActivity;
import com.kingocean.warehouseapp.StuffingActivity;
import com.kingocean.warehouseapp.services.PreferenceService;
import com.kingocean.warehouseapp.ui.login.LoginActivity;
import com.kingocean.warehouseapp.utils.PreferenceKeys;
import com.kingocean.warehouseapp.utils.TonePlayer;

public class AppsActivity extends AppCompatActivity {

    CardView smallParcel;
    CardView storageLocation;
    CardView stuffing;
    CardView mainReceipt;
    CardView repack;
    CardView repackPics;
    Button logout;
    TonePlayer tonePlayer;
    PreferenceService preferenceService;

    @Override
    public void onBackPressed() {
       showConfirmationDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        MenuItem loggedUserItem = menu.findItem(R.id.logged_user);
        preferenceService = PreferenceService.getInstance(getApplicationContext(), PreferenceKeys.credentials);
        String username = preferenceService.getString("username", "");
        loggedUserItem.setTitle(username);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tonePlayer = new TonePlayer();
        setContentView(R.layout.activity_apps);

        Drawable gradient = getResources().getDrawable(R.drawable.action_bar_gradient);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setBackgroundDrawable(gradient);
        }

        logout = findViewById(R.id.btnLogout);

        logout.setOnClickListener(view -> showConfirmationDialog());

        /// Small Parcel
        smallParcel = findViewById(R.id.card_small_parcel);
        smallParcel.setOnClickListener(view -> {
            tonePlayer.playGoToApp(500);

            Intent intent;
            intent = new Intent(AppsActivity.this, ScanActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        /// Storage Location
        storageLocation = findViewById(R.id.card_storage_location);
        storageLocation.setOnClickListener(view -> {
            tonePlayer.playGoToApp(500);

            Intent intent;
            intent = new Intent(AppsActivity.this, LocationsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        /// Stuffing
        stuffing = findViewById(R.id.card_stuffing);
        stuffing.setOnClickListener(view -> {
            tonePlayer.playGoToApp(500);

            Intent intent;
            intent = new Intent(AppsActivity.this, StuffingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        /// Main Receipt
        mainReceipt = findViewById(R.id.card_main_receipt);
        mainReceipt.setOnClickListener(view -> {
            tonePlayer.playGoToApp(500);

            Intent intent;
            intent = new Intent(AppsActivity.this, MainReceiptActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        /// repack
        repack = findViewById(R.id.card_repack);
        repack.setOnClickListener(view -> {
            tonePlayer.playGoToApp(500);

            Intent intent;
            intent = new Intent(AppsActivity.this, RepackActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        /// repack pics
        repackPics = findViewById(R.id.card_repack_pics);
        repackPics.setOnClickListener(view -> {
            tonePlayer.playGoToApp(500);

            Intent intent;
            intent = new Intent(AppsActivity.this, RepackPics.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this,
                R.style.CustomAlertDialogStyleGeneral);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            clearCredentials();
            goLogin();
        });
        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clearCredentials() {
        PreferenceService preferenceService =
                new PreferenceService(
                        getApplicationContext(),
                        PreferenceKeys.credentials);

        preferenceService.putString("id", "");
        preferenceService.putString("username", "");
        preferenceService.putString("password", "");
    }

    public void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}