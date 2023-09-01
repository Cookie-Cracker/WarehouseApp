//package com.kingocean.warehouseapp;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.media.AudioManager;
//import android.media.ToneGenerator;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageButton;
//
//import com.kingocean.warehouseapp.activities.RepackActivity;
//import com.kingocean.warehouseapp.ui.login.LoginActivity;
//
//public class AppSelectorActivity extends AppCompatActivity {
//
//    //RadioGroup rgAppSelector;
//    //RadioButton radioButton;
//    Button logout;
//    ImageButton smallParcelBtn;
//    ImageButton storageLocationBtn;
//    ImageButton stuffingBtn;
//    ImageButton mainReceiptBtn;
//
//    ImageButton repackBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_appselector);
//
//        // -----------------------------------------------------------------------------------------------------------
//        // logoutBtn
//        // -----------------------------------------------------------------------------------------------------------
//
//        logout = findViewById(R.id.AppSelector_logoutBtn);
//
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                clearCredentials();
//                goLogin();
//            }
//        });
//
//        // -----------------------------------------------------------------------------------------------------------
//        // Small Parcel Btn
//        // -----------------------------------------------------------------------------------------------------------
//
//        smallParcelBtn = findViewById(R.id.smallParcelBtn);
//
//        smallParcelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//                toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,500);
//
//                Intent intent;
//                intent = new Intent(AppSelectorActivity.this, ScanActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//
//        // -----------------------------------------------------------------------------------------------------------
//        // Storage Location Btn
//        // -----------------------------------------------------------------------------------------------------------
//
//        storageLocationBtn = findViewById(R.id.storageLocationBtn);
//
//        storageLocationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//                toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,500);
//
//                Intent intent;
//                intent = new Intent(AppSelectorActivity.this, LocationsActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//
//        // -----------------------------------------------------------------------------------------------------------
//        // Stuffing Btn
//        // -----------------------------------------------------------------------------------------------------------
//
//        stuffingBtn = findViewById(R.id.stuffingBtn);
//
//        stuffingBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//                toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,500);
//
//                Intent intent;
//                intent = new Intent(AppSelectorActivity.this, StuffingActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//
//        // -----------------------------------------------------------------------------------------------------------
//        // Main Receipt Btn (The Mule)
//        // -----------------------------------------------------------------------------------------------------------
//
//        mainReceiptBtn = findViewById(R.id.mainReceiptBtn);
//
//        mainReceiptBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//                toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,500);
//
//                Intent intent;
//                intent = new Intent(AppSelectorActivity.this, MainReceiptActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//            }
//        });
//
//        // -----------------------------------------------------------------------------------------------------------
//
//        // -----------------------------------------------------------------------------------------------------------
//        // Repack
//        // -----------------------------------------------------------------------------------------------------------
//    repackBtn = findViewById(R.id.repackBtn);
//    repackBtn.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
//            toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,500);
//
//            Intent intent;
//            intent = new Intent(AppSelectorActivity.this, RepackActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//        }
//    });
//
//
//        // -----------------------------------------------------------------------------------------------------------
//    }
//
//    //rgAppSelector = findViewById(R.id.rgAppSelector);
//
//    // -----------------------------------------------------------------------------------------------------------
//    // Radio-Button Click
//    // -----------------------------------------------------------------------------------------------------------
//
//    /*
//    public void checkAppSelection(View v) {
//        int radioId = rgAppSelector.getCheckedRadioButtonId();
//        radioButton = findViewById(radioId);
//
//        Intent intent;
//
//        switch (radioButton.getText().toString()){
//            case "Small Parcel Scanning Tool":
//                intent = new Intent(this, ScanActivity.class);
//                startActivity(intent);
//                break;
//            case "Storage Location Tool":
//                intent = new Intent(this, LocationsActivity.class);
//                startActivity(intent);
//                break;
//            case "Stuffing Tool":
//                intent = new Intent(this, StuffingActivity.class);
//                startActivity(intent);
//                break;
//        }
//
//    }
//    */
//
//    // -----------------------------------------------------------------------------------------------------------
//
//    public void clearCredentials() {
//        SharedPreferences preferences = getSharedPreferences("credentials", MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("id", "");
//        editor.putString("username", "");
//        editor.putString("password", "");
//        editor.apply();
//    }
//
//    // -----------------------------------------------------------------------------------------------------------
//
//    public void goLogin() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//    }
//
//    // -----------------------------------------------------------------------------------------------------------
//
//}