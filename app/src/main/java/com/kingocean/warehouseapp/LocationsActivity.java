package com.kingocean.warehouseapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;

//import android.opengl.Matrix;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.kingocean.warehouseapp.activities.AppsActivity;
import com.kingocean.warehouseapp.activities.DockReceiptsGallery;
import com.kingocean.warehouseapp.ui.login.LoginActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Timer;

public class LocationsActivity extends AppCompatActivity {

    Button logout;

    static final int milliseconds = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public String mUsername = "";

    // ------------------------------------------------------------------------

    Integer mErrorID = 0;
    String mErrorMessage = "";

    String mPortOfLoading = "";
    String mPortOfDischarge = "";
    String mBooking = "";
    String mPackages = "";
    String mWeight = "";
    String mMeasure = "";
    String mHC = "";
    String mLocation = "";
    String mUnitLength = "";
    String mUnitHeight = "";
    String mUnitWidth = "";
    String mUnitPkgs = "";

    String mDR = "";
    String mUnit = "";
    Integer mLocationReadOnly = 0;

    Boolean mCheckedDR = false;
    Boolean mCheckedLocation = false;

    ImageButton btnGallery;
    // ------------------------------------------------------------------------

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        Drawable gradient = getResources().getDrawable(R.drawable.action_bar_gradient);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(gradient);
        }

        final int delay = 0;
        final int delay1000 = 1000;

        // -----------------------------------------------------------------------------------------------------------
        // spWarehouses
        // -----------------------------------------------------------------------------------------------------------

        Spinner spWarehouses = findViewById(R.id.cbWarehouse);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(LocationsActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.warehouses));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spWarehouses.setEnabled(false);
        //spWarehouses.setClickable(false);
        spWarehouses.setAdapter(myAdapter);

        // -----------------------------------------------------------------------------------------------------------
        // Controls
        // -----------------------------------------------------------------------------------------------------------

        EditText editTextDR = findViewById(R.id.editTextDR);
        EditText editTextLoc = findViewById(R.id.editTextLoc);

        EditText editTextExpectedLocation = findViewById(R.id.editTextExpectedLocation);
        editTextExpectedLocation.setEnabled(false);

        EditText editTextUnit = findViewById(R.id.editTextUnit);
        editTextUnit.setEnabled(false);
        EditText editTextPortL = findViewById(R.id.editTextPortL);
        editTextPortL.setEnabled(false);
        EditText editTextPortD = findViewById(R.id.editTextPortD);
        editTextPortD.setEnabled(false);

        EditText editTextPkgs = findViewById(R.id.editTextPkgs);
        editTextPkgs.setEnabled(false);
        EditText editTextDC = findViewById(R.id.editTextDC);
        editTextDC.setEnabled(false);
        EditText editTextWeight = findViewById(R.id.editTextWeight);
        editTextWeight.setEnabled(false);
        EditText editTextMeasure = findViewById(R.id.editTextMeasure);
        editTextMeasure.setEnabled(false);

        EditText editTextLength = findViewById(R.id.editTextLength);
        editTextLength.setEnabled(false);
        EditText editTextWidth = findViewById(R.id.editTextWidth);
        editTextWidth.setEnabled(false);
        EditText editTextHeight = findViewById(R.id.editTextHeight);
        editTextHeight.setEnabled(false);

        editTextDR.setInputType(InputType.TYPE_NULL);
        editTextDR.requestFocus();

        ImageButton btnDrGallery = (ImageButton) findViewById(R.id.btnDockReceiptsGallery);
        btnDrGallery.setEnabled(true);


        btnDrGallery.setOnClickListener(view -> {
            Intent intent = new Intent(this, DockReceiptsGallery.class);
            intent.putExtra("drNumber", Integer.valueOf(editTextDR.getText().toString()));
            intent.putExtra("drUnit", Integer.valueOf(editTextUnit.getText().toString()));
            Log.i("LOCATION", "onCreate: " + Integer.valueOf(editTextUnit.getText().toString()));
            startActivity(intent);
        });

        // -----------------------------------------------------------------------------------------------------------
        // Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs | Dialogs
        // -----------------------------------------------------------------------------------------------------------

        // -----------------------------------------------------------------------------------------------------------
        // editTextDR
        // -----------------------------------------------------------------------------------------------------------

        editTextDR.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextDR.setInputType(InputType.TYPE_NULL);
                    clearControls();
                }
            }

        });

        editTextDR.addTextChangedListener(new TextWatcher() {

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

                if (editTextDR.getText().toString().trim().length() != 0 && !mCheckedDR) {

                    checkDR();
                    mCheckedDR = true;
                    btnDrGallery.setEnabled(true);

                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    AlertDialog.Builder dlgAlert;

                    switch (mErrorID) {
                        case 0:
                        case 2:
                        case 3:
                            // Error: Unit already loaded!

                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER, 3000);
                            dlgAlert = new AlertDialog.Builder(LocationsActivity.this, R.style.DialogAlertThemeRed);
                            dlgAlert.setMessage(Html.fromHtml(mErrorMessage));
                            dlgAlert.setTitle("Location");
                            dlgAlert.setNeutralButton("Ok", null);
                            dlgAlert.setCancelable(false);
                            dlgAlert.create().show();

                            clearControls();

                            editTextDR.setInputType(InputType.TYPE_NULL);
                            editTextDR.requestFocus();

                            break;

                        case 1:
                            // Ok

                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 500);

                            editTextDR.setText(mDR);

                            //editTextLoc.setText(mLocation);
                            editTextExpectedLocation.setText(mLocation);

                            editTextUnit.setText(mUnit);
                            editTextPortL.setText(mPortOfLoading);
                            editTextPortD.setText(mPortOfDischarge);
                            editTextPkgs.setText(mPackages);
                            editTextDC.setText(mHC);
                            editTextWeight.setText(mWeight);
                            editTextMeasure.setText(mMeasure);
                            editTextLength.setText(mUnitLength);
                            editTextWidth.setText(mUnitWidth);
                            editTextHeight.setText(mUnitHeight);

                            //editTextLoc.setEnabled(true);
                            //if (mLocationReadOnly != 0) {
                            // ReadOnly
                            //    editTextLoc.setEnabled(false);
                            //} else {
                            //    editTextLoc.requestFocus();
                            //}

                            editTextLoc.setInputType(InputType.TYPE_NULL);
                            editTextLoc.requestFocus();

                            break;

                    }

                }
            }

        });

        // -----------------------------------------------------------------------------------------------------------
        // editTextLoc
        // -----------------------------------------------------------------------------------------------------------

        editTextLoc.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextLoc.setInputType(InputType.TYPE_NULL);
                    mCheckedLocation = false;
                }
            }

        });

        editTextLoc.addTextChangedListener(new TextWatcher() {

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

                if (editTextLoc.getText().toString().trim().length() != 0 && !mCheckedLocation) {

                    checkLocation();
                    mCheckedLocation = true;

                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    AlertDialog.Builder dlgAlert;

                    switch (mErrorID) {
                        case 0:
                        case 2:
                        case 3:
                            // Error: DR is empty.
                            // Error: Unit is empty.
                            // Error: Location is empty.
                            // Error: Invalid Location in Warehouse.

                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER, 3000);
                            dlgAlert = new AlertDialog.Builder(LocationsActivity.this, R.style.DialogAlertThemeRed);
                            dlgAlert.setTitle("Location Assignment");
                            dlgAlert.setMessage(Html.fromHtml(mErrorMessage));
                            dlgAlert.setNeutralButton("Ok", null);
                            dlgAlert.setCancelable(false);
                            dlgAlert.create().show();

                            mCheckedLocation = false;

                            editTextLoc.setText("", TextView.BufferType.EDITABLE);
                            editTextLoc.setInputType(InputType.TYPE_NULL);
                            editTextLoc.requestFocus();

                            break;

                        case 1:
                            // Ok - Location successfully Assigned/re-Assigned.

                            /*
                            final AlertDialog successDialog  = new AlertDialog.Builder(LocationsActivity.this, R.style.DialogAlertThemeGreen)
                                    .setTitle("Location Assignment")
                                    .setMessage(Html.fromHtml("Location successfully Assigned/re-Assigned."))
                                    .setNeutralButton("Ok", null)
                                    .setCancelable(false).create();

                            successDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    final Button okButton = successDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                                    new CountDownTimer(3000, 1000) {
                                        @Override
                                        public void onTick(long l) {
                                            okButton.setText("OK (" + ((l/1000) + 1) + ")");
                                        }
                                        @Override
                                        public void onFinish() {
                                            if (successDialog.isShowing())
                                                successDialog.dismiss();
                                        }
                                    }.start();
                                }
                            });

                            successDialog.show();
                            */

                            toneGen1.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 500);
                            clearControls();
                            editTextDR.setInputType(InputType.TYPE_NULL);
                            editTextDR.requestFocus();

                            break;

                    }

                } else {
                    //clearControls();
                    //editTextDR.requestFocus();
                }

            }

        });


        // -----------------------------------------------------------------------------------------------------------
        // logoutBtn
        // -----------------------------------------------------------------------------------------------------------

        logout = findViewById(R.id.Locations_logoutBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goAppSelector();
            }
        });

        // -----------------------------------------------------------------------------------------------------------

        SharedPreferences preferences = getSharedPreferences("credentials", MODE_PRIVATE);
        mUsername = preferences.getString("username", "");

    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences("credentials", MODE_PRIVATE);
        String username = preferences.getString("username", "");
        if (username.equalsIgnoreCase("")) {
            goLogin();
        }

    }

    // -----------------------------------------------------------------------------------------------------------
    // Methods For Data Access
    // -----------------------------------------------------------------------------------------------------------

    public void checkDR() {

        mErrorID = 0;
        mErrorMessage = "";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_2_Param_1_int_Param2_string";
        String METHOD_NAME = "SP_2_Param_1_int_Param2_string";
        int TimeOut = 30000;

        EditText editTextDR = (EditText) findViewById(R.id.editTextDR);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi;

            pi = new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spWarehouse_Locations_DR");
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("Warehouse");
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param1");
            Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);
            String value = spWarehouses.getSelectedItem().toString().substring(spWarehouses.getSelectedItem().toString().indexOf("|") + 1);       //String value = spWarehouses.getSelectedItem().toString();
            pi.setValue(value);
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("DR");
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param2");
            pi.setValue(editTextDR.getText().toString());
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

                switch (mErrorID) {
                    case 1:

                        mPortOfLoading = ObjectTable.getProperty("PortOfLoading").toString();
                        mPortOfDischarge = ObjectTable.getProperty("PortOfDischarge").toString();
                        mBooking = ObjectTable.getProperty("Booking").toString();
                        mPackages = ObjectTable.getProperty("Packages").toString();
                        mWeight = ObjectTable.getProperty("Weight").toString();
                        mMeasure = ObjectTable.getProperty("Measure").toString();
                        mHC = ObjectTable.getProperty("HC").toString();
                        mLocation = ObjectTable.getPrimitivePropertyAsString("Location");
                        mUnitLength = ObjectTable.getProperty("UnitLength").toString();
                        mUnitHeight = ObjectTable.getProperty("UnitHeight").toString();
                        mUnitWidth = ObjectTable.getProperty("UnitWidth").toString();
                        mUnitPkgs = ObjectTable.getProperty("UnitPkgs").toString();

                        mDR = ObjectTable.getProperty("DR").toString();
                        mUnit = ObjectTable.getProperty("Unit").toString();
                        mLocationReadOnly = Integer.parseInt(ObjectTable.getProperty("LocationReadOnly").toString());

                        break;
                }

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

    public void checkLocation() {

        mErrorID = 0;
        mErrorMessage = "";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_4_Param_2_int_2_string";
        String METHOD_NAME = "SP_4_Param_2_int_2_string";
        int TimeOut = 30000;

        EditText editTextDR = (EditText) findViewById(R.id.editTextDR);
        EditText editTextLoc = (EditText) findViewById(R.id.editTextLoc);
        EditText editTextUnit = (EditText) findViewById(R.id.editTextUnit);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi;

            pi = new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spWarehouse_Locations_AssignLocation");
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("Warehouse");
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param1");
            Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);
            String value = spWarehouses.getSelectedItem().toString().substring(spWarehouses.getSelectedItem().toString().indexOf("|") + 1);       //String value = spWarehouses.getSelectedItem().toString();
            pi.setValue(value);
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("Unit");
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param2");
            pi.setValue(editTextUnit.getText().toString());
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param3_name");
            pi.setValue("DR");
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param3");
            pi.setValue(editTextDR.getText().toString());
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param4_name");
            pi.setValue("Location");
            pi.setType(String.class);
            request.addProperty(pi);

            pi = new PropertyInfo();
            pi.setName("Param4");
            pi.setValue(editTextLoc.getText().toString() + "|" + mUsername);
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

    public void clearControls() {

        EditText editTextDR = findViewById(R.id.editTextDR);
        editTextDR.setText("", TextView.BufferType.EDITABLE);
        EditText editTextLoc = findViewById(R.id.editTextLoc);
        editTextLoc.setText("", TextView.BufferType.EDITABLE);

        EditText editTextUnit = findViewById(R.id.editTextUnit);
        editTextUnit.setText("", TextView.BufferType.EDITABLE);
        EditText editTextPortL = findViewById(R.id.editTextPortL);
        editTextPortL.setText("", TextView.BufferType.EDITABLE);
        EditText editTextPortD = findViewById(R.id.editTextPortD);
        editTextPortD.setText("", TextView.BufferType.EDITABLE);

        EditText editTextPkgs = findViewById(R.id.editTextPkgs);
        editTextPkgs.setText("", TextView.BufferType.EDITABLE);
        EditText editTextDC = findViewById(R.id.editTextDC);
        editTextDC.setText("", TextView.BufferType.EDITABLE);
        EditText editTextWeight = findViewById(R.id.editTextWeight);
        editTextWeight.setText("", TextView.BufferType.EDITABLE);
        EditText editTextMeasure = findViewById(R.id.editTextMeasure);
        editTextMeasure.setText("", TextView.BufferType.EDITABLE);

        EditText editTextLength = findViewById(R.id.editTextLength);
        editTextLength.setText("", TextView.BufferType.EDITABLE);
        EditText editTextWidth = findViewById(R.id.editTextWidth);
        editTextWidth.setText("", TextView.BufferType.EDITABLE);
        EditText editTextHeight = findViewById(R.id.editTextHeight);
        editTextHeight.setText("", TextView.BufferType.EDITABLE);

        EditText editTextExpectedLocation = findViewById(R.id.editTextExpectedLocation);
        editTextExpectedLocation.setText("", TextView.BufferType.EDITABLE);

        mCheckedDR = false;
        mCheckedLocation = false;

    }

    // -----------------------------------------------------------------------------------------------------------

    public void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // -----------------------------------------------------------------------------------------------------------

    public void goAppSelector() {
        Intent intent = new Intent(this, AppsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // -----------------------------------------------------------------------------------------------------------

}