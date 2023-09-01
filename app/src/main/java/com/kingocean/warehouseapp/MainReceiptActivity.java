package com.kingocean.warehouseapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.ToneGenerator;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.Date;
import java.util.Timer;

public class MainReceiptActivity extends AppCompatActivity {

    Button logout;
    ImageButton takeAPicture;

    static final int milliseconds = 100;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    String mCurrentPhotoPath;
    ImageView mThumbnail;
    Integer mPictureTaken = 0;
    File photoFile;
    String mImageFileName;
    Integer mSeqNumber = 1;

    Bitmap takenImage;

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
        setContentView(R.layout.activity_main_receipt);

        Drawable gradient = getResources().getDrawable(R.drawable.action_bar_gradient);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setBackgroundDrawable(gradient);
        }

        final int delay = 0;
        final int delay1000 = 1000;

        // -----------------------------------------------------------------------------------------------------------
        // spWarehouses
        // -----------------------------------------------------------------------------------------------------------

        Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainReceiptActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.warehouses));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spWarehouses.setEnabled(false);
        //spWarehouses.setClickable(false);
        spWarehouses.setAdapter(myAdapter);

        // -----------------------------------------------------------------------------------------------------------
        // Controls
        // -----------------------------------------------------------------------------------------------------------

        EditText editTextDR = (EditText) findViewById(R.id.editTextDR);
        TextView lbPackagesCnt = (TextView) findViewById(R.id.lbPackagesCnt);

        ImageButton takeAPictureBtn = (ImageButton) findViewById(R.id.takeAPictureBtn);

        editTextDR.setInputType(InputType.TYPE_NULL);
        editTextDR.requestFocus();

        // -----------------------------------------------------------------------------------------------------------
        // editTextDR
        // -----------------------------------------------------------------------------------------------------------

        editTextDR.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    editTextDR.setInputType(InputType.TYPE_NULL);

                    editTextDR.setText("", TextView.BufferType.EDITABLE);
                    lbPackagesCnt.setText("0", TextView.BufferType.EDITABLE);

                    String uri = "@drawable/ic_box2";
                    int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                    Drawable res = getResources().getDrawable(imageResource);

                    mThumbnail = (ImageView) findViewById(R.id.imageView1);
                    mThumbnail.setImageDrawable(res);

                    mPictureTaken = 0;
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
                if(editTextDR.getText().toString().trim().length() != 0) {

                    DRNewMainReceipt();

                    //LinearLayout llCamera = (LinearLayout) findViewById(R.id.llCamera);
                    //llCamera.setVisibility(View.VISIBLE);

                    takeAPictureBtn.setFocusable(true);
                    takeAPictureBtn.setFocusableInTouchMode(true);
                    takeAPictureBtn.requestFocus();

                    takeAPictureBtn.performClick();

                } else {
                    //LinearLayout llCamera = (LinearLayout) findViewById(R.id.llCamera);
                    //llCamera.setVisibility(View.GONE);
                }
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

                    if(editTextDR.getText().toString().trim().length() != 0 && isStringInt(editTextDR.getText().toString())) {

                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        takePictureIntent.putExtra("android.intent.extra.quickCapture", true);

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

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "(2) Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        // -----------------------------------------------------------------------------------------------------------
        // logoutBtn
        // -----------------------------------------------------------------------------------------------------------

        logout = findViewById(R.id.logoutBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clearCredentials();
                //goLogin();
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
        if(username.equalsIgnoreCase(""))
        {
            goLogin();
        }

    }

    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)

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
                EditText editTextDR = (EditText) findViewById(R.id.editTextDR);


                mImageFileName = "DR_" +  editTextDR.getText().toString() + "_" + mSeqNumber.toString() + "_" + timeStamp + ".jpg";

                // Converting 'stream' to Base64-String
                String imgString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                // Send picture to the database
                sendImageToDatabase(mUsername, imgString);

                // Add the photo to a gallery
                //// galleryAddPic();

                // Removing original picture from 'Pictures' directory
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File file = new File(dir, mCurrentPhotoPath.substring(mCurrentPhotoPath.lastIndexOf("/") + 1));
                boolean deleted = file.delete();

                ////--------------------------------------------------------------------------------------------------
                ////getSharedPreferences("credentials", 0).edit().clear().commit();
                ////--------------------------------------------------------------------------------------------------

                // displaying the mThumbnail

                switch(mPictureTaken) {
                    case 1:
                        mThumbnail = (ImageView) findViewById(R.id.imageView1);
                        mThumbnail.setImageBitmap(resizedBitmap);
                        break;
                    default:
                        //
                        break;
                }

                ImageButton takeAPictureBtn = (ImageButton) findViewById(R.id.takeAPictureBtn);
                takeAPictureBtn.performClick();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "(99) Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            EditText editTextDR = (EditText) findViewById(R.id.editTextDR);
            editTextDR.setInputType(InputType.TYPE_NULL);
            editTextDR.requestFocus();
        }

    }

    // -----------------------------------------------------------------------------------------------------------

    public void DRNewMainReceipt() {

        //https://kingocean.azurewebsites.net/MSL_WS.asmx?WSDL

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/SP_4_Param_2_int_2_string";
        String METHOD_NAME = "SP_4_Param_2_int_2_string";
        int TimeOut = 30000;

        EditText editTextDR = (EditText) findViewById(R.id.editTextDR);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("ST_Name");
            pi.setValue("DR_New_Main_Receipt");
            pi.setType(String.class);
            request.addProperty(pi);

            //-
            pi=new PropertyInfo();
            pi.setName("Param1_name");
            pi.setValue("DR");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param1");
            pi.setValue(editTextDR.getText().toString());
            pi.setType(Integer.class);
            request.addProperty(pi);

            //-
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

            //-
            pi=new PropertyInfo();
            pi.setName("Param3_name");
            pi.setValue("Pallet");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param3");
            pi.setValue("");
            pi.setType(String.class);
            request.addProperty(pi);

            //-
            pi=new PropertyInfo();
            pi.setName("Param4_name");
            pi.setValue("Tracking");
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Param4");
            pi.setValue("");
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

                mSeqNumber = 1;
                if (ObjectTable.getProperty("Result").toString().toUpperCase().contains("SUCCESS")) {
                    mSeqNumber = Integer.parseInt(ObjectTable.getProperty("Result").toString().substring(ObjectTable.getProperty("Result").toString().indexOf("|") + 1));
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

    public void sendImageToDatabase(String createdBy, String docImg) {

        //https://kingocean.azurewebsites.net/MSL_WS.asmx?WSDL

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/DR_Document_Small_Parcell";
        String METHOD_NAME = "DR_Document_Small_Parcell";
        int TimeOut = 30000;

        EditText editTextDR = (EditText) findViewById(R.id.editTextDR);

        try {

            //Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo pi=new PropertyInfo();
            pi.setName("strConnect");
            pi.setValue(getResources().getString(R.string.app_connection_string));
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("DR_Warehouse");
            Spinner spWarehouses = (Spinner) findViewById(R.id.cbWarehouse);
            String value = spWarehouses.getSelectedItem().toString().substring(spWarehouses.getSelectedItem().toString().indexOf("|") + 1);       //String value = spWarehouses.getSelectedItem().toString();
            pi.setValue(value);
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("DR_number");
            pi.setValue(Integer.parseInt(editTextDR.getText().toString()));
            pi.setType(Integer.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("FileName");
            pi.setValue(mImageFileName);
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Created_By");
            pi.setValue(createdBy);
            pi.setType(String.class);
            request.addProperty(pi);

            pi=new PropertyInfo();
            pi.setName("Doc_Img");
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

                ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                if (!result.getProperty(0).toString().equals("Success")) {
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER,1000);
                }

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

    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
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

    public void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // -----------------------------------------------------------------------------------------------------------

    public void goAppSelector() {
        Intent intent = new Intent(this, AppsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
    }

}