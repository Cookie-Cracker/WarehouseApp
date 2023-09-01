package com.kingocean.warehouseapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.ToneGenerator;
import android.net.Uri;

//import android.opengl.Matrix;
import android.graphics.Matrix;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kingocean.warehouseapp.activities.AppsActivity;
import com.kingocean.warehouseapp.ui.login.LoginActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class StuffingActivity extends AppCompatActivity {

    RadioGroup rgTrackingType;
    RadioButton radioButton;
    Button logout;
    Button pendingDR;
    ImageButton takeAPicture;

    static final int milliseconds = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    String mCurrentPhotoPath;
    ImageView mThumbnail;
    Integer mPictureTaken = 0;
    File photoFile;
    String mImageFileName;

    Bitmap takenImage;

    public String mUsername = "";

    // ------------------------------------------------------------------------

    Integer mErrorID = 0;
    String mErrorMessage = "";
    SimpleAdapter adapter;

    Boolean scannedDR = true;

    String mSailing = "";

    Integer mBooking = 0;
    String mContainer = "";
    String mDRStuffed = "";

    String mStuffing = "";
    String mEquipmentType = "";

    Integer mPortOfLoading = 0;
    Integer mPortOfDischarge = 0;
    Integer mBookingSeq = 1;

    // ------------------------------------------------------------------------

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    // ------------------------------------------------------------------------

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuffing);

        Drawable gradient = getResources().getDrawable(R.drawable.action_bar_gradient);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setBackgroundDrawable(gradient);
        }

        final int delay = 0;
        final int delay1000 = 1000;

        rgTrackingType = findViewById(R.id.rgTrackingType);

        // -----------------------------------------------------------------------------------------------------------
        // spWarehouses
        // -----------------------------------------------------------------------------------------------------------

        Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(StuffingActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.warehouses));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spWarehouses.setEnabled(false);
        //spWarehouses.setClickable(false);
        spWarehouses.setAdapter(myAdapter);

        // -----------------------------------------------------------------------------------------------------------
        // Controls
        // -----------------------------------------------------------------------------------------------------------

        EditText editTextBK = (EditText) findViewById(R.id.editTextBK);
        EditText editTextStuffing = (EditText) findViewById(R.id.editTextStuffing);
        editTextStuffing.setEnabled(false);
        EditText editTextContainer = (EditText) findViewById(R.id.editTextContainer);
        EditText editTextEqType = (EditText) findViewById(R.id.editTextEqType);
        editTextEqType.setEnabled(false);
        EditText editTextDRStuffed = (EditText) findViewById(R.id.editTextDRStuffed);
        EditText editTextTotWeight = (EditText) findViewById(R.id.editTextTotWeight);
        editTextTotWeight.setEnabled(false);
        EditText editTextTotalWeight = (EditText) findViewById(R.id.editTextTotalWeight);
        editTextTotalWeight.setEnabled(false);
        EditText editTextTotalMeasure = (EditText) findViewById(R.id.editTextTotalMeasure);
        editTextTotalMeasure.setEnabled(false);

        editTextBK.setInputType(InputType.TYPE_NULL);
        editTextBK.requestFocus();

        // -----------------------------------------------------------------------------------------------------------
        // Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs
        // -----------------------------------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------------------------------
        // Create a new stuffing ?
        // -----------------------------------------------------------------------------------------------------------

        DialogInterface.OnClickListener dialogClickListener_NewStuffing = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        createNewStuffing();

                        switch (mErrorID){
                            case 0:
                                // Other cases
                                break;
                            case 1:
                                editTextStuffing.setText(mStuffing);
                                editTextEqType.setText(mEquipmentType);

                                editTextDRStuffed.setInputType(InputType.TYPE_NULL);
                                editTextDRStuffed.requestFocus();
                                break;
                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //Toast.makeText(getApplicationContext(), "NO clicked!", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        // -----------------------------------------------------------------------------------------------------------
        // This Container/Booking was stuffed. Do you want to continue ?
        // -----------------------------------------------------------------------------------------------------------

        DialogInterface.OnClickListener dialogClickListener_AlreadyStuffed = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        switch (mErrorID){
                            case 2:
                                if (editTextStuffing.getText().toString().trim().length() != 0) {
                                    doResearchDR();
                                    editTextDRStuffed.setInputType(InputType.TYPE_NULL);
                                    editTextDRStuffed.requestFocus();
                                }
                                break;
                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        mContainer = "";
                        editTextContainer.setText("", TextView.BufferType.EDITABLE);
                        editTextContainer.setInputType(InputType.TYPE_NULL);
                        editTextContainer.requestFocus();
                        break;
                }
            }
        };

        // -----------------------------------------------------------------------------------------------------------
        // Do you want to remove this DR from stuffing ?
        // -----------------------------------------------------------------------------------------------------------

        DialogInterface.OnClickListener dialogClickListener_RemoveDRFromStuffing = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        removeDRFromStuffing();

                        switch (mErrorID){
                            case 0:
                                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                                toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER,1000);
                                Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                if (editTextStuffing.getText().toString().trim().length() != 0) {
                                    doResearchDR();
                                }
                                editTextDRStuffed.setText("", TextView.BufferType.EDITABLE);
                                editTextDRStuffed.setInputType(InputType.TYPE_NULL);
                                editTextDRStuffed.requestFocus();
                                break;
                        }

                        editTextDRStuffed.setText("", TextView.BufferType.EDITABLE);
                        editTextDRStuffed.setInputType(InputType.TYPE_NULL);
                        editTextDRStuffed.requestFocus();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        editTextDRStuffed.setText("", TextView.BufferType.EDITABLE);
                        editTextDRStuffed.setInputType(InputType.TYPE_NULL);
                        editTextDRStuffed.requestFocus();

                        break;
                }
            }
        };

        // -----------------------------------------------------------------------------------------------------------
        // editTextBK
        // -----------------------------------------------------------------------------------------------------------

        editTextBK.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //clearControls();
                    editTextBK.setInputType(InputType.TYPE_NULL);
                }
            }

        });

        editTextBK.addTextChangedListener(new TextWatcher() {

            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
            Runnable workRunnable;

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> doSmth(s.toString());
                handler.postDelayed(workRunnable, milliseconds);
            }

            private final void doSmth(String str) {
                if(!editTextBK.getText().toString().equalsIgnoreCase("")) {

                    checkBooking();

                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    if (mErrorID != 1) {

                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER,1000);
                        Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_LONG).show();

                        editTextBK.setText("", TextView.BufferType.EDITABLE);
                        mBooking = 0;
                        editTextBK.setInputType(InputType.TYPE_NULL);
                        editTextBK.requestFocus();

                    } else {
                        toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,150);
                        editTextContainer.setInputType(InputType.TYPE_NULL);
                        editTextContainer.requestFocus();
                    }

                }
            }

        });

        // -----------------------------------------------------------------------------------------------------------
        // editTextStuffing
        // -----------------------------------------------------------------------------------------------------------

        editTextStuffing.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
            Runnable workRunnable;

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> doSmth(s.toString());
                handler.postDelayed(workRunnable, milliseconds);
            }

            private final void doSmth(String str) {
                if (editTextStuffing.getText().toString().trim().length() != 0) {
                    LinearLayout llCamera = (LinearLayout) findViewById(R.id.llCamera);
                    llCamera.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout llCamera = (LinearLayout) findViewById(R.id.llCamera);
                    llCamera.setVisibility(View.GONE);
                }
            }

        });

        // -----------------------------------------------------------------------------------------------------------
        // editTextContainer
        // -----------------------------------------------------------------------------------------------------------

        editTextContainer.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //clearControls();
                    editTextContainer.setInputType(InputType.TYPE_NULL);
                }
            }

        });

        editTextContainer.addTextChangedListener(new TextWatcher() {

            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
            Runnable workRunnable;

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> doSmth(s.toString());
                handler.postDelayed(workRunnable, milliseconds);
            }

            private final void doSmth(String str) {

                if(!editTextContainer.getText().toString().equalsIgnoreCase("")) {

                    checkContainer();

                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    AlertDialog.Builder dlgAlert;

                    switch (mErrorID){
                        case 0:
                            // Equipment-Type not found!
                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER,1000);
                            Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_LONG).show();

                            mContainer = "";
                            editTextContainer.setText("", TextView.BufferType.EDITABLE);
                            editTextContainer.setInputType(InputType.TYPE_NULL);
                            editTextContainer.requestFocus();

                            break;

                        case 1:
                            // Do you want to create a new stuffing ?

                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,150);

                            dlgAlert = new AlertDialog.Builder(StuffingActivity.this);
                            dlgAlert.setMessage(mErrorMessage);
                            dlgAlert.setTitle("Stuffing");
                            dlgAlert.setNegativeButton("No", dialogClickListener_NewStuffing);
                            dlgAlert.setPositiveButton("Yes", dialogClickListener_NewStuffing);
                            dlgAlert.setCancelable(false);
                            dlgAlert.create().show();

                            break;

                        case 2:
                            // This Container/Booking was stuffed. Do you want to continue ?

                            editTextStuffing.setText(mStuffing);
                            editTextEqType.setText(mEquipmentType);

                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,150);
                            dlgAlert = new AlertDialog.Builder(StuffingActivity.this);
                            dlgAlert.setMessage(mErrorMessage);
                            dlgAlert.setTitle("Stuffing");
                            dlgAlert.setNegativeButton("No", dialogClickListener_AlreadyStuffed);
                            dlgAlert.setPositiveButton("Yes", dialogClickListener_AlreadyStuffed);
                            dlgAlert.setCancelable(false);
                            dlgAlert.create().show();

                            break;

                        case 3:
                            // Booking/Container of the Stuffing #: [uid] POSTED.

                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,150);
                            dlgAlert  = new AlertDialog.Builder(StuffingActivity.this);
                            dlgAlert.setMessage(mErrorMessage);
                            dlgAlert.setTitle("Stuffing");
                            dlgAlert.setNeutralButton("Ok", null);
                            dlgAlert.setCancelable(false);
                            dlgAlert.create().show();

                            mContainer = "";
                            editTextContainer.setText("", TextView.BufferType.EDITABLE);
                            mEquipmentType = "";
                            editTextEqType.setText("", TextView.BufferType.EDITABLE);
                            mDRStuffed = "";
                            editTextDRStuffed.setText("", TextView.BufferType.EDITABLE);
                            mStuffing = "";
                            editTextStuffing.setText("", TextView.BufferType.EDITABLE);
                            mBooking = 0;
                            editTextBK.setText("", TextView.BufferType.EDITABLE);
                            editTextContainer.setInputType(InputType.TYPE_NULL);
                            editTextContainer.requestFocus();

                            clearControls();

                            break;

                        case 4:
                            // Container not found!

                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER,1000);
                            dlgAlert  = new AlertDialog.Builder(StuffingActivity.this);
                            dlgAlert.setMessage(mErrorMessage);
                            dlgAlert.setTitle("Stuffing");
                            dlgAlert.setNeutralButton("Ok", null);
                            dlgAlert.setCancelable(false);
                            dlgAlert.create().show();

                            mContainer = "";
                            editTextContainer.setText("", TextView.BufferType.EDITABLE);
                            editTextContainer.requestFocus();

                            break;
                    }

                }
            }

        });

        // -----------------------------------------------------------------------------------------------------------
        // editTextDRStuffed
        // -----------------------------------------------------------------------------------------------------------

        editTextDRStuffed.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //clearControls();
                    editTextDRStuffed.setInputType(InputType.TYPE_NULL);
                }
            }

        });

        editTextDRStuffed.addTextChangedListener(new TextWatcher() {

            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
            Runnable workRunnable;

            @Override
            public void afterTextChanged(Editable s) {
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> doSmth(s.toString());
                handler.postDelayed(workRunnable, milliseconds);
            }

            private final void doSmth(String str) {

                if(!editTextDRStuffed.getText().toString().equalsIgnoreCase("")) {

                    checkDRStuffed();

                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    AlertDialog.Builder dlgAlert;

                    switch (mErrorID){
                        case 0:

                            // Invalid Booking.
                            // Invalid DR. Ports Not Matching Booking's Ports.
                            // Invalid/Incorrect DR or DR not ready for stuffing.
                            // DR is not ready for stuffing.
                            // Warehouse|DR Not Found

                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER,1000);
                            dlgAlert  = new AlertDialog.Builder(StuffingActivity.this);
                            dlgAlert.setMessage(mErrorMessage);
                            dlgAlert.setTitle("Stuffing");
                            dlgAlert.setNeutralButton("Ok", null);
                            dlgAlert.setCancelable(false);
                            dlgAlert.create().show();

                            editTextDRStuffed.setText("", TextView.BufferType.EDITABLE);
                            mDRStuffed = "";
                            editTextDRStuffed.setInputType(InputType.TYPE_NULL);
                            editTextDRStuffed.requestFocus();

                            break;

                        case 1:

                            // Do you want to remove this DR from stuffing ?

                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,150);
                            dlgAlert = new AlertDialog.Builder(StuffingActivity.this);
                            dlgAlert.setMessage(mErrorMessage);
                            dlgAlert.setTitle("Stuffing");
                            dlgAlert.setNegativeButton("No", dialogClickListener_RemoveDRFromStuffing);
                            dlgAlert.setPositiveButton("Yes", dialogClickListener_RemoveDRFromStuffing);
                            dlgAlert.setCancelable(false);
                            dlgAlert.create().show();

                            break;

                        case 2:

                            doDRStuffing();

                            if (editTextStuffing.getText().toString().trim().length() != 0) {
                                doResearchDR();
                            }

                            editTextDRStuffed.setText("", TextView.BufferType.EDITABLE);
                            mDRStuffed = "";
                            editTextDRStuffed.setInputType(InputType.TYPE_NULL);
                            editTextDRStuffed.requestFocus();

                            break;

                    }

                }
            }

        });

        // -----------------------------------------------------------------------------------------------------------
        // pendingDRBtn
        // -----------------------------------------------------------------------------------------------------------

        pendingDR = findViewById(R.id.pendingDRBtn);

        pendingDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scannedDR) {
                    doPendingDR();
                    scannedDR = false;
                    pendingDR.setText("Scanned ?");
                } else {

                    if (editTextStuffing.getText().toString().trim().length() != 0) {
                        doResearchDR();
                    }
                    scannedDR = true;
                    pendingDR.setText("Pending ?");

                }
            }
        });

        // -----------------------------------------------------------------------------------------------------------
        // logoutBtn
        // -----------------------------------------------------------------------------------------------------------

        logout = findViewById(R.id.Stuffing_logoutBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clearCredentials();
                //goLogin();
                goAppSelector();
            }
        });

        // -----------------------------------------------------------------------------------------------------------
        // takeAPictureBtn
        // -----------------------------------------------------------------------------------------------------------

        takeAPicture = findViewById(R.id.takeAPictureBtn);

        takeAPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Development based on: https://developer.android.com/training/camera/photobasics#java

                try {

                    if(!editTextBK.getText().toString().equalsIgnoreCase("") && isStringInt(editTextBK.getText().toString())) {

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureIntent.putExtra("android.intent.extra.quickCapture", true);

                        /*if (takePictureIntent.resolveActivity(getPackageManager()) != null) {*/   // Ensure that there's a camera activity to handle the intent

                        // Create the File where the photo should go
                        photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(getBaseContext(), "com.kingocean.warehouseapp.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }

                        /*}*/

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "(2) Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        // -----------------------------------------------------------------------------------------------------------

        SharedPreferences preferences = getSharedPreferences("credentials", MODE_PRIVATE);
        mUsername = preferences.getString("username", "");

    }

    // -----------------------------------------------------------------------------------------------------------

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("credentials", MODE_PRIVATE);
        String username = preferences.getString("username", "");
        if(username.equalsIgnoreCase(""))
        {
            goLogin();
        }

    }

    // -----------------------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            try {

                // by this point we have the camera photo on disk
                // mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));

                mPictureTaken = 1;

                Bitmap takenImage;
                Bitmap resizedBitmap;

                takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                // Setting photo orientation (start) -----------------------------------------------------------------------------------

                ExifInterface ei = new ExifInterface(photoFile.getAbsolutePath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap rotatedBitmap = null;

                switch(orientation) {
                    // 1
                    case ExifInterface.ORIENTATION_NORMAL:
                        rotatedBitmap = takenImage;
                        break;
                    // LANDSCAPE = 6
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(takenImage, 90);
                        break;
                    // LANDSCAPE_PRIMARY = 3
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(takenImage, 180);
                        break;
                    // NATURAL = 8
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(takenImage, 270);
                        break;
                }

                takenImage = rotatedBitmap;

                // Setting photo orientation (end) ------------------------------------------------------------------------------------

                resizedBitmap = scaleToFitWidth(takenImage, 100);

                // Configure output stream
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                // Compress the original picture into 'stream'
                takenImage.compress(Bitmap.CompressFormat.JPEG, 45, stream);     // resizedBitmap

                // Create a new file name for the resized bitmap for sending it to the database
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                EditText editTextBK = (EditText) findViewById(R.id.editTextBK);
                mImageFileName = "STF_" +  editTextBK.getText().toString() + "_" + timeStamp + ".jpg";

                // Converting 'stream' to Base64-String
                String imgString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                // Send picture to the database
                sendImageToDatabase(imgString);

                // Removing original picture from 'Pictures' directory
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(dir, mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf("/") + 1));
                boolean deleted = file.delete();

                ////--------------------------------------------------------------------------------------------------
                ////getSharedPreferences("credentials", 0).edit().clear().commit();
                ////--------------------------------------------------------------------------------------------------

                // displaying the mThumbnail

                mThumbnail = (ImageView) findViewById(R.id.imageView1);
                mThumbnail.setImageBitmap(resizedBitmap);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "(99) Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }

    public static Bitmap scaleToFitWidth(Bitmap b, int width)
    {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }

    // -----------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------
    // Methods For Data Access
    // -----------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------

    public void checkBooking() {

        mErrorID = 0;
        mErrorMessage = "";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_1_Param_int";
        String METHOD_NAME = "SP_1_Param_int";
        int TimeOut = 30000;

        EditText editTextBK = (EditText) findViewById(R.id.editTextBK);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi;

            pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spWarehouse_Stuffing_Booking");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param_name");
            pi.setValue("BK");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param");
            pi.setValue(editTextBK.getText().toString());
            pi.setType(String.class);
            request.addProperty(pi);

            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {

                SoapObject root = (SoapObject) result.getProperty(0);
                SoapObject diffGram = (SoapObject) root.getProperty("diffgram");
                SoapObject table = (SoapObject) diffGram.getProperty(0);
                SoapObject ObjectTable = (SoapObject) table.getProperty(0);

                mErrorID = Integer.parseInt(ObjectTable.getProperty("Code").toString());
                mErrorMessage = ObjectTable.getProperty("Message").toString();

                //mBooking = Integer.parseInt(ObjectTable.getProperty("Booking").toString());
                mSailing = ObjectTable.getProperty("Sailing").toString();
                mPortOfLoading = Integer.parseInt(ObjectTable.getProperty("PortOfLoading").toString());
                mPortOfDischarge = Integer.parseInt(ObjectTable.getProperty("PortOfDischarge").toString());

            } else {
                mErrorID = 9;
                mErrorMessage = "Error: result is null!";
            }

            result = null;
            envelope = null;

        } catch (Exception e) {

            mErrorID = 9;
            mErrorMessage = "Error: " + e.getMessage();
        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public void checkContainer() {

        mErrorID = 0;
        mErrorMessage = "";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_2_Param_1_int_Param2_string";
        String METHOD_NAME = "SP_2_Param_1_int_Param2_string";
        int TimeOut = 30000;

        EditText editTextBK = (EditText) findViewById(R.id.editTextBK);
        EditText editTextContainer = (EditText) findViewById(R.id.editTextContainer);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi;

            pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spWarehouse_Stuffing_Container");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("BK");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1");
            pi.setValue(editTextBK.getText().toString());
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("Container");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2");
            pi.setValue(editTextContainer.getText().toString());
            pi.setType(String.class);
            request.addProperty(pi);

            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {

                SoapObject root = (SoapObject) result.getProperty(0);
                SoapObject diffGram = (SoapObject) root.getProperty("diffgram");
                SoapObject table = (SoapObject) diffGram.getProperty(0);
                SoapObject ObjectTable = (SoapObject) table.getProperty(0);

                mErrorID = Integer.parseInt(ObjectTable.getProperty("Code").toString());
                mErrorMessage = ObjectTable.getProperty("Message").toString();

                if (mErrorID == 2) {
                    mStuffing = ObjectTable.getProperty("Stuffing").toString();
                    mEquipmentType = ObjectTable.getProperty("EquipmentType").toString();
                }

                result = null;
                envelope = null;

            } else {
                mErrorID = 9;
                mErrorMessage = "Error: Result is null!";
            }

        } catch (Exception e) {
            mErrorID = 9;
            mErrorMessage = "Error: " + e.getMessage();
        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public void checkDRStuffed() {

        mErrorID = 0;
        mErrorMessage = "";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_3_Param_1_String_2_int";
        String METHOD_NAME = "SP_3_Param_1_String_2_int";
        int TimeOut = 30000;

        EditText editTextBK = (EditText) findViewById(R.id.editTextBK);
        EditText editTextDRStuffed = (EditText) findViewById(R.id.editTextDRStuffed);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi;

            pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spWarehouse_Stuffing_DR");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("DRStuffed");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1");
            pi.setValue(editTextDRStuffed.getText().toString());
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("Warehouse");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2");
            Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);
            String value = spWarehouses.getSelectedItem().toString().substring(spWarehouses.getSelectedItem().toString().indexOf("|") + 1);       //String value = spWarehouses.getSelectedItem().toString();
            pi.setValue(value);
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3_name");
            pi.setValue("BK");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3");
            pi.setValue(editTextBK.getText().toString());
            pi.setType(Integer.class);
            request.addProperty(pi);

            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {

                SoapObject root = (SoapObject) result.getProperty(0);
                SoapObject diffGram = (SoapObject) root.getProperty("diffgram");
                SoapObject table = (SoapObject) diffGram.getProperty(0);
                SoapObject ObjectTable = (SoapObject) table.getProperty(0);

                mErrorID = Integer.parseInt(ObjectTable.getProperty("Code").toString());
                mErrorMessage = ObjectTable.getProperty("Message").toString();

                result = null;
                envelope = null;

            } else {
                mErrorID = 9;
                mErrorMessage = "Error: Result is null";
            }

        } catch (Exception e) {
            mErrorID = 9;
            mErrorMessage = "Error: " + e.getMessage();
        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public void createNewStuffing() {

        mErrorID = 0;
        mErrorMessage = "";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_3_Param_1_String_2_int";
        String METHOD_NAME = "SP_3_Param_1_String_2_int";
        int TimeOut = 30000;

        EditText editTextBK = (EditText) findViewById(R.id.editTextBK);
        EditText editTextContainer = (EditText) findViewById(R.id.editTextContainer);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi;

            pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spWarehouse_Stuffing_CreateNewStuffing");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("Container");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1");
            pi.setValue(editTextContainer.getText().toString() + "|" + mUsername);  // Because of the lack of parameters, I'm sending in 'Container' parameter both, the container number plus the user logged.
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("Warehouse");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2");
            Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);
            String value = spWarehouses.getSelectedItem().toString().substring(spWarehouses.getSelectedItem().toString().indexOf("|") + 1);       //String value = spWarehouses.getSelectedItem().toString();
            pi.setValue(value);
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3_name");
            pi.setValue("BK");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3");
            pi.setValue(editTextBK.getText().toString());
            pi.setType(Integer.class);
            request.addProperty(pi);

            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {

                SoapObject root = (SoapObject) result.getProperty(0);
                SoapObject diffGram = (SoapObject) root.getProperty("diffgram");
                SoapObject table = (SoapObject) diffGram.getProperty(0);
                SoapObject ObjectTable = (SoapObject) table.getProperty(0);

                mErrorID = Integer.parseInt(ObjectTable.getProperty("Code").toString());
                mErrorMessage = ObjectTable.getProperty("Message").toString();

                mStuffing = ObjectTable.getProperty("Stuffing").toString();
                mEquipmentType = ObjectTable.getProperty("EquipmentType").toString();
                mBookingSeq = Integer.parseInt(ObjectTable.getProperty("BookingSeq").toString());

                result = null;
                envelope = null;

            } else {
                mErrorID = 9;
                mErrorMessage = "Error: Result is null";
            }

        } catch (Exception e) {
            //e.printStackTrace();
            mErrorID = 9;
            mErrorMessage = "Error: " + e.getMessage();
        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public void doResearchDR() {

        mErrorID = 0;
        mErrorMessage = "";

        scannedDR = true;

        EditText editTextBK = (EditText) findViewById(R.id.editTextBK);
        EditText editTextContainer = (EditText) findViewById(R.id.editTextContainer);
        EditText editTextStuffing = (EditText) findViewById(R.id.editTextStuffing);
        TextView lbPackagesCnt = (TextView) findViewById(R.id.lbPackagesCnt);
        TextView lbPackagesPendingCnt = (TextView) findViewById(R.id.lbPackagesPendingCnt);
        EditText editTextTotalMeasure = (EditText) findViewById(R.id.editTextTotalMeasure);
        EditText editTextTotalWeight = (EditText) findViewById(R.id.editTextTotalWeight);
        EditText editTextDRStuffed = (EditText) findViewById(R.id.editTextDRStuffed);

        /* GridView */
        int itemsCount = 0;
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        GridView list = (GridView)findViewById(R.id.gridview1);

        String[] from = {"DR", "Length", "Height", "Width", "Nro"};
        int[] to = {R.id.DR, R.id.Length, R.id.Height, R.id.Width, R.id.Nro};
        /* GridView */

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_4_Param_3_int_1_string";
        String METHOD_NAME = "SP_4_Param_3_int_1_string";
        int TimeOut = 30000;

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi;

            pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spWarehouse_Stuffing_ResearchDR");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("Warehouse");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1");
            Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);
            String value = spWarehouses.getSelectedItem().toString().substring(spWarehouses.getSelectedItem().toString().indexOf("|") + 1);       //String value = spWarehouses.getSelectedItem().toString();
            pi.setValue(value);
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("Stuffing");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2");
            pi.setValue(editTextStuffing.getText().toString());     // mStuffing
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3_name");
            pi.setValue("BK");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3");
            pi.setValue(editTextBK.getText().toString());
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param4_name");
            pi.setValue("Container");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param4");
            pi.setValue(editTextContainer.getText().toString());
            pi.setType(String.class);
            request.addProperty(pi);

            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {

                SoapObject root = (SoapObject) result.getProperty(0);
                SoapObject diffGram = (SoapObject) root.getProperty("diffgram");
                SoapObject tables = (SoapObject) diffGram.getProperty(0);
                SoapObject oTable;

                for (int i = 0; i < tables.getPropertyCount(); i++)
                {
                    oTable = (SoapObject) tables.getProperty(i);
                    switch(tables.getPropertyInfo(i).name) {
                        case "Table":
                            lbPackagesCnt.setText(oTable.getProperty("T_Pkgs").toString(), TextView.BufferType.EDITABLE);
                            break;
                        case "Table1":
                            lbPackagesCnt.setText(oTable.getProperty("T_Pkgs_Pend").toString(), TextView.BufferType.EDITABLE);
                            break;
                        case "Table2":
                            lbPackagesPendingCnt.setText(oTable.getProperty("T_Pkgs_Pend").toString(), TextView.BufferType.EDITABLE);
                            break;
                        case "Table3":
                            editTextTotalMeasure.setText(oTable.getProperty("Tot").toString(), TextView.BufferType.EDITABLE);
                            break;
                        case "Table4":
                            editTextTotalWeight.setText(oTable.getProperty("Tot").toString(), TextView.BufferType.EDITABLE);
                            break;
                        case "Table5":
                            //mErrorID = Integer.parseInt(oTable.getProperty("DR").toString());
                            Map<String, String> tab = new HashMap<String, String>();
                            tab.put("DR", oTable.getProperty("DR").toString());
                            tab.put("Length", oTable.getProperty("Length").toString());
                            tab.put("Height", oTable.getProperty("Height").toString());
                            tab.put("Width", oTable.getProperty("Width").toString());
                            tab.put("Nro", oTable.getProperty("Nro").toString());
                            data.add(tab);
                            itemsCount = itemsCount + 1;
                            break;
                        default:
                            //pi.setValue(1);
                            break;
                    }
                }

                if (itemsCount > 0) {
                    adapter = new SimpleAdapter(this, data, R.layout.gridviewlayout, from, to);
                    list.setAdapter(adapter);
                }

                editTextDRStuffed.setInputType(InputType.TYPE_NULL);
                editTextDRStuffed.requestFocus();

                result = null;
                envelope = null;

            } else {
                mErrorID = 9;
                mErrorMessage = "Error: Result is null";
            }

        } catch (Exception e) {

            if (itemsCount == 0) {
                adapter = new SimpleAdapter(this, data, R.layout.gridviewlayout, from, to);
                list.setAdapter(adapter);
            }

            mErrorID = 9;
            mErrorMessage = "Error: " + e.getMessage();
        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public void doPendingDR() {

        mErrorID = 0;
        mErrorMessage = "";

        EditText editTextBK = (EditText) findViewById(R.id.editTextBK);
        EditText editTextDRStuffed = (EditText) findViewById(R.id.editTextDRStuffed);

        /* GridView */
        int itemsCount = 0;
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        GridView list = (GridView)findViewById(R.id.gridview1);

        String[] from = {"DR", "Length", "Height", "Width", "Nro"};
        int[] to = {R.id.DR, R.id.Length, R.id.Height, R.id.Width, R.id.Nro};
        /* GridView */

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_2_Param_int";
        String METHOD_NAME = "SP_2_Param_int";
        int TimeOut = 30000;

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi;

            pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spWarehouse_Stuffing_PendingDR");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("Warehouse");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1");
            Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);
            String value = spWarehouses.getSelectedItem().toString().substring(spWarehouses.getSelectedItem().toString().indexOf("|") + 1);       //String value = spWarehouses.getSelectedItem().toString();
            pi.setValue(value);
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("BK");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2");
            pi.setValue(editTextBK.getText().toString());
            pi.setType(Integer.class);
            request.addProperty(pi);

            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {

                SoapObject root = (SoapObject) result.getProperty(0);
                SoapObject diffGram = (SoapObject) root.getProperty("diffgram");
                SoapObject tables = (SoapObject) diffGram.getProperty(0);
                SoapObject oTable;

                for (int i = 0; i < tables.getPropertyCount(); i++)
                {
                    oTable = (SoapObject) tables.getProperty(i);
                    switch(tables.getPropertyInfo(i).name) {
                        case "Table":
                            Map<String, String> tab = new HashMap<String, String>();
                            tab.put("DR", oTable.getProperty("DR").toString());
                            tab.put("Length", oTable.getProperty("Length").toString());
                            tab.put("Height", oTable.getProperty("Height").toString());
                            tab.put("Width", oTable.getProperty("Width").toString());
                            tab.put("Nro", oTable.getProperty("Nro").toString());
                            data.add(tab);
                            itemsCount = itemsCount + 1;
                            break;
                        default:
                            //pi.setValue(1);
                            break;
                    }
                }

                if (itemsCount > 0) {
                    adapter = new SimpleAdapter(this, data, R.layout.gridviewlayout, from, to);
                    list.setAdapter(adapter);
                }

                editTextDRStuffed.setInputType(InputType.TYPE_NULL);
                editTextDRStuffed.requestFocus();

                result = null;
                envelope = null;

            } else {
                mErrorID = 9;
                mErrorMessage = "Error: Result is null";
            }

        } catch (Exception e) {

            if (itemsCount == 0) {
                adapter = new SimpleAdapter(this, data, R.layout.gridviewlayout, from, to);
                list.setAdapter(adapter);
            }

            mErrorID = 9;
            mErrorMessage = "Error: " + e.getMessage();

        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public void removeDRFromStuffing() {

        mErrorID = 0;
        mErrorMessage = "";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_3_Param_1_String_2_int";
        String METHOD_NAME = "SP_3_Param_1_String_2_int";
        int TimeOut = 30000;

        EditText editTextBK = (EditText) findViewById(R.id.editTextBK);
        EditText editTextContainer = (EditText) findViewById(R.id.editTextContainer);
        EditText editTextDRStuffed = (EditText) findViewById(R.id.editTextDRStuffed);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi;

            pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spWarehouse_Stuffing_RemoveDRFromStuffing");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("DRStuffed");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1");
            pi.setValue(editTextDRStuffed.getText().toString());
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("Warehouse");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2");
            Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);
            String value = spWarehouses.getSelectedItem().toString().substring(spWarehouses.getSelectedItem().toString().indexOf("|") + 1);       //String value = spWarehouses.getSelectedItem().toString();
            pi.setValue(value);
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3_name");
            pi.setValue("BK");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3");
            pi.setValue(editTextBK.getText().toString());
            pi.setType(Integer.class);
            request.addProperty(pi);

            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {

                SoapObject root = (SoapObject) result.getProperty(0);
                SoapObject diffGram = (SoapObject) root.getProperty("diffgram");
                SoapObject table = (SoapObject) diffGram.getProperty(0);
                SoapObject ObjectTable = (SoapObject) table.getProperty(0);

                mErrorID = Integer.parseInt(ObjectTable.getProperty("Code").toString());
                mErrorMessage = ObjectTable.getProperty("Message").toString();

                result = null;
                envelope = null;

            } else {
                mErrorID = 9;
                mErrorMessage = "Error: Result is null";
            }

        } catch (Exception e) {
            //e.printStackTrace();
            mErrorID = 9;
            mErrorMessage = "Error: " + e.getMessage();
        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public void doDRStuffing() {

        mErrorID = 0;
        mErrorMessage = "";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_4_Param_3_int_1_string";
        String METHOD_NAME = "SP_4_Param_3_int_1_string";
        int TimeOut = 30000;

        EditText editTextBK = (EditText) findViewById(R.id.editTextBK);
        EditText editTextStuffing = (EditText) findViewById(R.id.editTextStuffing);
        EditText editTextContainer = (EditText) findViewById(R.id.editTextContainer);
        EditText editTextDRStuffed = (EditText) findViewById(R.id.editTextDRStuffed);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi;

            pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spWarehouse_Stuffing_DoDRStuffing");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("Warehouse");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1");
            Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);
            String value = spWarehouses.getSelectedItem().toString().substring(spWarehouses.getSelectedItem().toString().indexOf("|") + 1);       //String value = spWarehouses.getSelectedItem().toString();
            pi.setValue(value);
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("BK");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2");
            pi.setValue(editTextBK.getText().toString());
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3_name");
            pi.setValue("Stuffing");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3");
            pi.setValue(editTextStuffing.getText().toString());
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param4_name");
            pi.setValue("DRStuffed_Container_BKSeq");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param4");
            pi.setValue(editTextDRStuffed.getText().toString() + "|" + editTextContainer.getText().toString() + "|" + mBookingSeq.toString());
            pi.setType(String.class);
            request.addProperty(pi);

            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {

                SoapObject root = (SoapObject) result.getProperty(0);
                SoapObject diffGram = (SoapObject) root.getProperty("diffgram");
                SoapObject table = (SoapObject) diffGram.getProperty(0);
                SoapObject ObjectTable = (SoapObject) table.getProperty(0);

                mErrorID = Integer.parseInt(ObjectTable.getProperty("Code").toString());
                mErrorMessage = ObjectTable.getProperty("Message").toString();

                result = null;
                envelope = null;

            } else {
                mErrorID = 9;
                mErrorMessage = "Error: Result is null";
            }

        } catch (Exception e) {
            mErrorID = 9;
            mErrorMessage = "Error: " + e.getMessage();
        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public void sendImageToDatabase(String docImg) {

        //https://kingocean.azurewebsites.net/MSL_WS.asmx?WSDL

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_4_Param_2_int_2_string";
        String METHOD_NAME = "SP_4_Param_2_int_2_string";
        int TimeOut = 30000;

        EditText editTextStuffing = (EditText) findViewById(R.id.editTextStuffing);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            // Dim ds = ws.SP_4_Param_2_int_2_string(md.strConnect, "Stuffing_Documents", "@dummyInt1", "@dummyInt2", "@DoThis", "@documents", 0, 0, "Set_Stuffing_Documents", xmlData)

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("Stuffing_Documents");
            pi.setType(String.class);
            request.addProperty(pi);

            //-
            pi=new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("Stuffing");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1");
            pi.setValue(editTextStuffing.getText().toString());
            pi.setType(Integer.class);
            request.addProperty(pi);

            //-
            pi=new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("dummyInt2");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2");
            pi.setValue(0);
            pi.setType(Integer.class);
            request.addProperty(pi);

            //-
            pi=new PropertyInfo();
            pi.setName("Param3_name");
            pi.setValue("DoThis");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3");
            pi.setValue("Set_Stuffing_Single_Document" + "|" + mUsername + "|" + mImageFileName);
            pi.setType(String.class);
            request.addProperty(pi);

            //-
            pi=new PropertyInfo();
            pi.setName("Param4_name");
            pi.setValue("Doc_Img");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param4");
            pi.setValue(docImg);
            pi.setType(String.class);
            request.addProperty(pi);

            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            if (result != null) {
                //Get the first property and change the label text

                SoapObject root = (SoapObject) result.getProperty(0);
                SoapObject diffGram = (SoapObject) root.getProperty("diffgram");
                SoapObject table = (SoapObject) diffGram.getProperty(0);
                SoapObject ObjectTable = (SoapObject) table.getProperty(0);

                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                if (ObjectTable.getProperty("Result").toString().equals("Success")) {
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE,150);
                } else {
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER,1000);
                }
                //Toast.makeText(getApplicationContext(), "(1) " + result.getProperty(0).toString(), Toast.LENGTH_SHORT).show();

            } else {
                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER,1000);
                Toast.makeText(getApplicationContext(), "(2-Sending Picture) " + "No Response", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            //e.printStackTrace();
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER,1000);
            Toast.makeText(getApplicationContext(), "(3-Sending Picture) " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public static boolean isStringInt(String s) {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }


    // -----------------------------------------------------------------------------------------------------------

    public void clearControls() {
        TextView lbPackagesCnt = (TextView) findViewById(R.id.lbPackagesCnt);
        lbPackagesCnt.setText("", TextView.BufferType.EDITABLE);
        TextView lbPackagesPendingCnt = (TextView) findViewById(R.id.lbPackagesPendingCnt);
        lbPackagesPendingCnt.setText("", TextView.BufferType.EDITABLE);
        EditText editTextTotalMeasure = (EditText) findViewById(R.id.editTextTotalMeasure);
        editTextTotalMeasure.setText("", TextView.BufferType.EDITABLE);
        EditText editTextTotalWeight = (EditText) findViewById(R.id.editTextTotalWeight);
        editTextTotalWeight.setText("", TextView.BufferType.EDITABLE);
        EditText editTextTotWeight = (EditText) findViewById(R.id.editTextTotWeight);
        editTextTotWeight.setText("", TextView.BufferType.EDITABLE);
    }

    // -----------------------------------------------------------------------------------------------------------

    public void goAppSelector() {
        Intent intent = new Intent(this, AppsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // -----------------------------------------------------------------------------------------------------------

    public void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // -----------------------------------------------------------------------------------------------------------
    // Not used...
    // -----------------------------------------------------------------------------------------------------------

    public void clearCredentials() {
        SharedPreferences preferences = getSharedPreferences("credentials", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", "");
        editor.putString("username", "");
        editor.putString("password", "");
        editor.apply();
    }

    // -----------------------------------------------------------------------------------------------------------

}