package com.kingocean.warehouseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Timer;

public class VisitorsLogActivity extends AppCompatActivity {

    Button signin;
    Button clear;

    static final int milliseconds = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public String mUsername = "";

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitors_log);

        final int delay = 0;
        final int delay1000 = 1000;

        // -----------------------------------------------------------------------------------------------------------
        // Controls
        // -----------------------------------------------------------------------------------------------------------

        EditText editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        EditText editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        EditText editTextCompany = (EditText) findViewById(R.id.editTextCompany);
        EditText editTextReason = (EditText) findViewById(R.id.editTextReason);

        editTextFirstName.requestFocus();

        // -----------------------------------------------------------------------------------------------------------
        // editTextLastName
        // -----------------------------------------------------------------------------------------------------------

        editTextLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                }
            }

        });

        editTextLastName.addTextChangedListener(new TextWatcher() {

            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if (timer != null) {
                //    timer.cancel();
                //}
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

            }

        });

        // -----------------------------------------------------------------------------------------------------------
        // button_signin
        // -----------------------------------------------------------------------------------------------------------

        signin = findViewById(R.id.button_signin);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editTextFirstName.getText().toString().trim().length() == 0) {
                    editTextFirstName.requestFocus();
                    return;
                }

                if(editTextLastName.getText().toString().trim().length() == 0) {
                    editTextLastName.requestFocus();
                    return;
                }

                if(editTextCompany.getText().toString().trim().length() == 0) {
                    editTextCompany.requestFocus();
                    return;
                }

                if(editTextReason.getText().toString().trim().length() == 0) {
                    editTextReason.requestFocus();
                    return;
                }

                SignInVisitor();

                clearControls();
            }
        });

        // -----------------------------------------------------------------------------------------------------------
        // button_clear
        // -----------------------------------------------------------------------------------------------------------

        clear = findViewById(R.id.btClear);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearControls();
            }
        });

        // -----------------------------------------------------------------------------------------------------------

        SharedPreferences preferences = getSharedPreferences("credentials", MODE_PRIVATE);
        mUsername = preferences.getString("username", "");

    }

    // -----------------------------------------------------------------------------------------------------------

    public void SignInVisitor() {

        //https://kingocean.azurewebsites.net/MSL_WS.asmx?WSDL

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_4_Param_string";
        String METHOD_NAME = "SP_4_Param_string";

        EditText editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        EditText editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        EditText editTextCompany = (EditText) findViewById(R.id.editTextCompany);
        EditText editTextReason = (EditText) findViewById(R.id.editTextReason);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue("Data Source=kingocean.database.windows.net;Initial Catalog=SCSolution;User ID=kingadmin;Password=!King@cean*;Connection Timeout=600");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("spVisitorsLog");
            pi.setType(String.class);
            request.addProperty(pi);

            //-
            pi=new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("DoThis");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1");
            pi.setValue("AddEntry");
            pi.setType(Integer.class);
            request.addProperty(pi);

            //-
            pi=new PropertyInfo();
            pi.setName("Param2_name");
            pi.setValue("Name");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param2");
            pi.setValue(editTextFirstName.getText().toString().trim() + "|" + editTextLastName.getText().toString().trim());
            pi.setType(Integer.class);
            request.addProperty(pi);

            //-
            pi=new PropertyInfo();
            pi.setName("Param3_name");
            pi.setValue("Company");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3");
            pi.setValue(editTextCompany.getText().toString().trim());
            pi.setType(String.class);
            request.addProperty(pi);

            //-
            pi=new PropertyInfo();
            pi.setName("Param4_name");
            pi.setValue("Reason");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param4");
            pi.setValue(editTextReason.getText().toString().trim());
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

                if (ObjectTable.getProperty("Result").toString().toUpperCase().contains("SUCCESS")) {
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

    public void clearControls() {
        EditText editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextFirstName.setText("", TextView.BufferType.EDITABLE);
        EditText editTextLastName = findViewById(R.id.editTextLastName);
        editTextLastName.setText("", TextView.BufferType.EDITABLE);
        EditText editTextCompany = findViewById(R.id.editTextCompany);
        editTextCompany.setText("", TextView.BufferType.EDITABLE);
        EditText editTextReason = findViewById(R.id.editTextReason);
        editTextReason.setText("", TextView.BufferType.EDITABLE);

        editTextFirstName.requestFocus();
    }

    // -----------------------------------------------------------------------------------------------------------

}