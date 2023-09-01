package com.kingocean.warehouseapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.data.adapters.DockReceiptsAdapter;
import com.kingocean.warehouseapp.data.adapters.RepackTypeAdapter;
import com.kingocean.warehouseapp.data.model.DockReceipt;
import com.kingocean.warehouseapp.data.model.GeneralResponse;
import com.kingocean.warehouseapp.data.model.RepackType;
import com.kingocean.warehouseapp.services.PreferenceService;
import com.kingocean.warehouseapp.services.RepackService;
import com.kingocean.warehouseapp.utils.PreferenceKeys;
import com.kingocean.warehouseapp.utils.ScannedDrExtractor;
import com.kingocean.warehouseapp.utils.TastyToasty;
import com.kingocean.warehouseapp.utils.TonePlayer;
import com.google.android.material.chip.Chip;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;



public class RepackActivity extends AppCompatActivity {





    ///START-REGION init activity
    private RecyclerView recyclerViewDocReceipts;

    private Spinner spinnerRepackType;
    private DockReceiptsAdapter dockReceiptsAdapter;
    private List<DockReceipt> dockReceipts;
    private RepackService repackService;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        MenuItem loggedUserItem = menu.findItem(R.id.logged_user);
        preferenceService = PreferenceService.getInstance(getApplicationContext(), PreferenceKeys.credentials);
        String username = preferenceService.getString("username", "");
        loggedUserItem.setTitle(username);

        return true;
    }
//    private Button btClear;
//    private EditText etRepackContainer;
//    private EditText etDrToRepack;
//    private int totalPackages;
//    private double totalCubicFeet;
//
//    private Chip chipTotalPackages;
//    private Chip chipTotalCubicFeet;
//
//    private Chip chipRepackCount;
//
//    private TextView etRepackedCount;
//
    PreferenceService preferenceService;
//
//    private List<DockReceipt> currentRepacked;
//
//    ///END-REGION init activity
//
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (getCurrentFocus() != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_repack);
//
//        Drawable gradient = getResources().getDrawable(R.drawable.action_bar_gradient);
//        ActionBar actionBar = getSupportActionBar();
//
//
//        // Get the app bar instance
//        // Enable the back button
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setBackgroundDrawable(gradient);
//        }
//
//        repackService = new RepackService();
//
////        btRepack = findViewById(R.id.btRepack);
////        btScan = findViewById(R.id.btScan);
//        btClear = findViewById(R.id.btClear);
//
//        etRepackContainer = findViewById(R.id.etDrContainer);
//        etDrToRepack = findViewById(R.id.etDrToRepack);
//
//        spinnerRepackType = findViewById(R.id.spinnerRepackType);
//
//        etRepackedCount = findViewById(R.id.tvRepacked);
//        etRepackedCount.setVisibility(View.INVISIBLE);
//
//        chipTotalPackages = findViewById(R.id.chipTotalPackages);
//        chipTotalCubicFeet = findViewById(R.id.chipTotalCubicFeet);
//        chipRepackCount = findViewById(R.id.chipRepackCount);
//
//        chipTotalCubicFeet.setText("0.0");
//        chipTotalPackages.setText("0");
//
//        dockReceipts = new ArrayList<>();
//        dockReceiptsAdapter = new DockReceiptsAdapter(dockReceipts);
//        recyclerViewDocReceipts = findViewById(R.id.rvDrsRepacked);
//        recyclerViewDocReceipts.setLayoutManager(new LinearLayoutManager(this));
//        recyclerViewDocReceipts.setAdapter(dockReceiptsAdapter);
//
//        // Initialize Spinner
//        ArrayList<RepackType> repackTypes = repackService.getRepackTypes();
//        RepackTypeAdapter adapter = new RepackTypeAdapter(this, repackTypes);
//        spinnerRepackType.setAdapter(adapter);
//
//        etRepackContainer.setInputType(InputType.TYPE_NULL);
//        etRepackContainer.requestFocus();
//
//        // Retrieve the value of "dr_repack_container" from the "repack" preference
////        preferenceService = PreferenceService.getInstance(this, PreferenceKeys.repack);
////        String repackContainer = preferenceService.getString("dr_repack_container", "");
////        if (!TextUtils.isEmpty(repackContainer)) {
////            etRepackContainer.setText(repackContainer);
////            callScanContainer();
////            etDrToRepack.requestFocus();
////        }
//
//        /// START-REGION repack container
//        etRepackContainer.setOnFocusChangeListener((view, hasFocus) -> {
//            if (hasFocus) {
//                if (etRepackContainer.getText().length() > 0) {
//                    resetControls();
//                    etRepackContainer.getText().clear();
//                }
//                etRepackContainer.setInputType(InputType.TYPE_NULL);
////                etRepackContainer.setText("", TextView.BufferType.EDITABLE);
//            }
//
//        });
//
//        etRepackContainer.addTextChangedListener(new TextWatcher() {
//            private boolean executeScanContainerCode = true;
//
//            private Timer timer = new Timer();
//            Handler handler = new Handler(Looper.getMainLooper());
//            Runnable workRunnable;
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (timer != null) {
//                    timer.cancel();
//                }
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable et) {
//                handler.removeCallbacks(workRunnable);
//                workRunnable = () -> callBack(et.toString());
//                handler.postDelayed(workRunnable, 100);
//
//            }
//
//
//            private void callBack(String et) {
//
//                try {
//
//                String scannedContainerValue = etRepackContainer.getText().toString();
//                String extractedValue = ScannedDrExtractor.extractValue(scannedContainerValue);
////                scannedContainerValue = etRepackContainer.getText().toString();
//                Log.e("EXTRACTED", "callBack: " + extractedValue );
//
//
//                if (extractedValue!=null  &&  extractedValue.length() == 8) {
//                    preferenceService.putString("dr_repack_container",extractedValue);
//                    if (isDRAvailableAsContainer(extractedValue)) {
//                        executeScanContainerCode = true;
//                        etRepackContainer.setText("");
//                        etRepackContainer.requestFocus();
//                        etRepackContainer.setInputType(InputType.TYPE_NULL);
//                        return;
//                    }
//
//                    if (executeScanContainerCode) {
//                        etRepackContainer.removeTextChangedListener(this);
//                        Log.e("EXTRACTED", "callBack: " + extractedValue );
//                        etRepackContainer.setText(extractedValue);
//                        etRepackContainer.addTextChangedListener(this);
//                        dockReceipts.clear();
//                        callScanContainer();
//                        etDrToRepack.requestFocus();
//                    }
//                } else {
//                    // Handle the case where the scannedContainerValue does not meet the length requirement
////                    TastyToasty.orange(getApplicationContext(), "nothing happens", R.drawable.ic_check_ok).show();
//                }
//                }
//                catch (Exception ex){
//                    Log.e("EX", "callBack: "+ ex.getMessage() );
//                }
//            }
//
//            private boolean isDRAvailableAsContainer(String scannedDrCode) {
//                TonePlayer tonePlayer = new TonePlayer();
//                AlertDialog.Builder dlgAlert;
//                GeneralResponse<String> isAvailableAsContainer =
//                        repackService.isScannedDRAvailableAsContainer(Integer.parseInt(scannedDrCode));
//                if (isAvailableAsContainer.isSuccess()) {
////                    will be added
//                    return false;
//                }
//                tonePlayer.playErrorSound(500);
//                tonePlayer.release();
//                dlgAlert  = new AlertDialog.Builder(RepackActivity.this, R.style.DialogAlertThemeRed);
//                dlgAlert.setMessage(Html.fromHtml(isAvailableAsContainer.getMessage()));
//                dlgAlert.setTitle("ERROR");
//                dlgAlert.setNeutralButton("Ok", null);
//                dlgAlert.setCancelable(false);
//                dlgAlert.create().show();
////                TastyToasty.red(
////                        getApplicationContext(),
////                        isAvailableAsContainer.getMessage(),
////                        R.drawable.ic_error).show();
//                return true;
//            }
//
//        });
//
//
//        /// END REGION repack container
//
//        /// START REGION dr to repack
//        etDrToRepack.setOnFocusChangeListener((view, hasFocus) -> {
//            if (hasFocus) {
//                if (etDrToRepack.getText().length() > 0) {
//                    resetControls();
//                    etDrToRepack.getText().clear();
//                }
//                etDrToRepack.setInputType(InputType.TYPE_NULL);
////                etDrToRepack.setText("", TextView.BufferType.EDITABLE);
//            }
//
//        });
//
//        etDrToRepack.addTextChangedListener(new TextWatcher() {
//            Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
//            Runnable workRunnable;
//
//            private Timer timer = new Timer();
//
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (timer != null) {
//                    timer.cancel();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable et) {
//                handler.removeCallbacks(workRunnable);
//                workRunnable = () -> callBack(et.toString());
//                handler.postDelayed(workRunnable, 100);
//
//            }
//
//            private void callBack(String scan) {
//
//                String scannedDrToRepack = etDrToRepack.getText().toString();
//                String extractedValue = ScannedDrExtractor.extractValue(scannedDrToRepack);
//
//                if (extractedValue!=null && extractedValue.length() == 8 ) {
//                    if (isDockReceiptAlreadyPresent(extractedValue)) {
//                        etDrToRepack.removeTextChangedListener(this);
//                        etDrToRepack.setText(extractedValue);
//                        etDrToRepack.addTextChangedListener(this);
//                        showDeRepackConfirmation(extractedValue);
//                    } else {
//                        etDrToRepack.removeTextChangedListener(this);
//                        etDrToRepack.setText(extractedValue);
//                        etDrToRepack.addTextChangedListener(this);
//
//                        callRepackOrDeRepack();
//                        etDrToRepack.requestFocus();
//                    }
//                }
//            }
//            private boolean isDockReceiptAlreadyPresent(String drToRepack) {
//                for (DockReceipt receipt : dockReceipts) {
//                    if (receipt.getDrNumber().equals(drToRepack)) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//            private void showDeRepackConfirmation(String drToRepack) {
//                TonePlayer tonePlayer = new TonePlayer();
//                tonePlayer.playErrorSound(1000);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(RepackActivity.this, R.style.CustomAlertDialogStyleAlert);
//                builder.setTitle("De-Repack Confirmation");
//                builder.setMessage("Are you sure you want to DE-REPACK this DR: " + drToRepack);
//                builder.setPositiveButton("DE-REPACK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        // Perform de-repack operation here
//                        callRepackOrDeRepack();
//                        callScanContainer();
//                        etDrToRepack.requestFocus();
//                    }
//                });
//                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
//                    // Handle cancel action here
//                    etDrToRepack.setText("");
//                });
//                builder.show();
//            }
//        });
//
//
//        btClear.setOnClickListener(view -> {
//            resetControls();
//        });
//
//        ItemTouchHelper.SimpleCallback itemTouchCallBack =
//                new ItemTouchHelper.SimpleCallback(
//                        0,
//                        ItemTouchHelper.RIGHT) {
//                    @Override
//                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                        int pos = viewHolder.getAdapterPosition();
//                        DockReceipt removedItem = dockReceipts.get(pos);
//                        showRemoveConfirmation(removedItem, pos);
//                    }
//
//                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                    @Override
//                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//
//                        View itemView = viewHolder.itemView;
//
//                        // Set the red background color when dragging to the right
//                        if (dX > 0 && actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//                            // Calculate the rect coordinates with smoother borders
//                            float left = itemView.getLeft();
//                            float top = itemView.getTop();
//                            float right = left + dX;
//                            float bottom = itemView.getBottom() - dY * 0.5f;
//
//                            // Create a rounded rect shape
//                            float cornerRadius = 16; // Adjust the value as per your preference
//                            Paint backgroundPaint = new Paint();
//                            backgroundPaint.setColor(ContextCompat.getColor(recyclerView.getContext(), R.color.md_red_400)); // Use your desired red color
//                            c.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, backgroundPaint);
//                            // Draw the garbage can icon
//                            Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.trash_can_32);
//
//
//                            if (icon != null) {
//                                int iconSize = icon.getIntrinsicWidth() - 10; // Adjust the size as per your preference
//                                int iconMargin = (itemView.getHeight() - iconSize) / 2;
//                                int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
//                                int iconBottom = iconTop + iconSize;
//                                int iconLeft = (int) (itemView.getLeft() + iconMargin);
//                                int iconRight = (int) (itemView.getLeft() + iconMargin + iconSize);
//
//                                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//                                icon.draw(c);
//                            }
//                        }
//                    }
//                };
//
//
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallBack);
//        itemTouchHelper.attachToRecyclerView(recyclerViewDocReceipts);
//
//    }
//
//    private String clearScannedCode(String scannedContainerValue) {
//        String clean = "";
//        clean =  scannedContainerValue.replace("%U%", "").trim();
//        etRepackContainer.setText(clean);
//        return clean;
//    }
//
//    @Override
//    public void onBackPressed() {
//
//        if (hasValuesToWarn()) {
//            showWarningDialog();
//        } else {
//
//            super.onBackPressed();
//        }
//    }
//    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
//    public void showWarningDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomAlertDialogStyleGeneral);
//        builder.setTitle("Warning");
//        builder.setMessage("You have unsaved changes. Are you sure you want to go back?");
//        builder.setPositiveButton("Yes", (dialog, which) -> {
//            // User confirmed, proceed with back navigation
//            super.onBackPressed();
//        });
//        builder.setNegativeButton("No", null);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
//    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
//    public boolean hasValuesToWarn() {
//        String containerValue = etRepackContainer.getText().toString().trim();
//        String toRepackValue = etDrToRepack.getText().toString().trim();
//
//        return !containerValue.isEmpty() || !toRepackValue.isEmpty();
//    }
//
//    private void callScanContainer() {
//        Log.e("EXTRACTED", "callBack: " + "callScanContainer" );
//
//        TonePlayer tonePlayer = new TonePlayer();
//
//        String drRepackString = etRepackContainer.getText().toString();
//
//        if (drRepackString.length() == 8) {
//            int drRepackContainer = Integer.parseInt(drRepackString);
//
//            RepackType selectedRepackType = (RepackType) spinnerRepackType.getSelectedItem();
//
//            List<DockReceipt> retrievedDockReceipts =
//                    repackService.scanDRContainer(drRepackContainer, selectedRepackType.getId());
//
//            currentRepacked = retrievedDockReceipts;
//            tonePlayer.playSuccessSound(100);
//            tonePlayer.release();
//
//            // Add the retrieved dock receipts to the adapter
//            dockReceipts.addAll(retrievedDockReceipts);
//            dockReceipts.clear();
//            // Clear the existing dock receipts in the adapter
//            if (!retrievedDockReceipts.isEmpty()) {
//                // Add the retrieved dock receipts to the adapter
//                dockReceipts.addAll(retrievedDockReceipts);
//                etRepackedCount.setVisibility(View.VISIBLE);
//                etRepackedCount.setText(String.valueOf(retrievedDockReceipts.size()));
//                chipRepackCount.setText(String.valueOf(retrievedDockReceipts.size()));
//
//
//            } else {
//                etRepackedCount.setVisibility(View.VISIBLE);
//                etRepackedCount.setText("No");
//                chipRepackCount.setText("0");
//
//
//            }
//            dockReceiptsAdapter.notifyDataSetChanged();
//
//
//            totalPackages = getTotalsPackages(retrievedDockReceipts);
//            totalCubicFeet = getTotalCubicFeet(retrievedDockReceipts);
//
//            chipTotalPackages.setText(String.valueOf(totalPackages));
//            chipTotalCubicFeet.setText(String.valueOf(totalCubicFeet));
////                    hideSoftKeyboard(view);
//
//            int containerTypeId = repackService.getRepackContainerType(drRepackContainer);
//            Log.i("CONTAINER", String.valueOf(containerTypeId));
//
//            int position = -1;
//            for (int i = 0; i < spinnerRepackType.getCount(); i++) {
//                RepackType repackType = (RepackType) spinnerRepackType.getItemAtPosition(i);
//                if (repackType.getId() == containerTypeId) {
//                    position = i;
//                    break;
//                }
//            }
//
//            if (position != -1) {
//                spinnerRepackType.setSelection(position);
//            }
//
//            spinnerRepackType.setEnabled(false);
//
//
//        } else {
//            TastyToasty.orange(
//                    getApplicationContext(),
//                    "Please enter a DR RePack number",
//                    R.drawable.ic_question).show();
//        }
//
//
//    }
//
//    private void callRepackOrDeRepack() {
//        TonePlayer tonePlayer = new TonePlayer();
//
//        String repackContainerType = spinnerRepackType.getSelectedItem().toString();
//        String drRepackContainerString = etRepackContainer.getText().toString();
//        String drToRepackString = etDrToRepack.getText().toString();
//        Log.i("REPACK", "callRepackOrDeRepack: ");
//
//        try {
//            // Validate drRepackContainerString separately
//            if (!drRepackContainerString.isEmpty() && drRepackContainerString.length() == 8) {
//                // Continue with the rest of the validation
//
//                // Validate EditText fields
//                if (TextUtils.isEmpty(drToRepackString) && drToRepackString.length() < 8) {
//                    TastyToasty.orange(
//                            getApplicationContext(),
//                            "Please enter a DR RePack number",
//                            R.drawable.ic_question).show();
//                    return;
//                }
//
//                // Parse values as integers
//                int drRepackContainer = Integer.parseInt(drRepackContainerString);
//                int drToRepack = Integer.parseInt(drToRepackString);
//
//
//                preferenceService = PreferenceService.getInstance(getApplicationContext(), PreferenceKeys.credentials);
//                String id = preferenceService.getString("id", "");
//                int idValue = 0;
//                try {
//                    idValue = Integer.parseInt(id);
//                } catch (NumberFormatException e) {
//
//                    Log.e("USERID", e.getMessage());
//                }
//                GeneralResponse<String> response = repackService.repackOrDeRepack(drRepackContainer, drToRepack, idValue);
//
//                if (response.isSuccess()) {
//
//
//                    etDrToRepack.setText("");
//                    etDrToRepack.requestFocus();
//                    etDrToRepack.setInputType(InputType.TYPE_NULL);
//                    TastyToasty.green(
//                            getApplicationContext(),
//                            response.getMessage(),
//                            R.drawable.ic_check_ok).show();
//
//                    List<DockReceipt> retrievedDockReceipts = repackService.getDockReceiptsRepacked(drRepackContainer);
//                    dockReceipts.clear(); // Clear existing dock receipts
//
//                    Log.e("DR", "retrieve" + retrievedDockReceipts.size());
//                    Log.e("DR", "dockreceipts" + retrievedDockReceipts.size());
//
//
//                    if (retrievedDockReceipts.isEmpty()) {
//                        etRepackedCount.setVisibility(View.INVISIBLE);
//                        etRepackedCount.setText("");
//                        chipRepackCount.setText(0);
//                        dockReceipts.clear();
//                    } else {
//                        dockReceipts.clear();
//                        dockReceipts.addAll(retrievedDockReceipts);
//                        etRepackedCount.setVisibility(View.VISIBLE);
//                        etRepackedCount.setText(String.valueOf(retrievedDockReceipts.size()));
//                        chipRepackCount.setText(String.valueOf(retrievedDockReceipts.size()));
//                    }
//
////                    dockReceipts.addAll(retrievedDockReceipts);
//                    dockReceiptsAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
//
//
//                    totalPackages = getTotalsPackages(retrievedDockReceipts);
//                    totalCubicFeet = getTotalCubicFeet(retrievedDockReceipts);
//
//                    chipTotalPackages.setText(String.valueOf(totalPackages));
//                    chipTotalCubicFeet.setText(String.valueOf(totalCubicFeet));
////                        hideSoftKeyboard(view);
//                } else {
//
//                    AlertDialog.Builder dlgAlert;
//
//                    tonePlayer.playErrorSound(1000);
//                    tonePlayer.release();
//
//                    dlgAlert  = new AlertDialog.Builder(RepackActivity.this, R.style.DialogAlertThemeRed);
//                    dlgAlert.setMessage(Html.fromHtml(response.getMessage()));
//                    dlgAlert.setTitle("REPACK ERROR");
//                    dlgAlert.setNeutralButton("Ok", null);
//                    dlgAlert.setCancelable(false);
//                    dlgAlert.create().show();
//
////                    TastyToasty.red(
////                            getApplicationContext(),
////                            response.getMessage(),
////                            R.drawable.ic_error).show();
//                    etDrToRepack.setText("");
//                    etDrToRepack.requestFocus();
//                    etDrToRepack.setInputType(InputType.TYPE_NULL);
//                }
//            } else {
//                tonePlayer.playErrorSound(1000);
//                TastyToasty.green(
//                        getApplicationContext(),
//                        "Value should be 8 characters long.",
//                        R.drawable.ic_question).show();
//            }
//        } catch (Exception e) {
//            Log.e("EXCEPTION", e.getMessage());
//            e.printStackTrace();
//        }
//
//
//    }
//
//    private void hideSoftKeyboard(View v) {
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
//    }
//
//    private int getTotalsPackages(List<DockReceipt> dockReceipts) {
//        int totalPackages = 0;
//
//        for (DockReceipt dockReceipt : dockReceipts) {
//            totalPackages += dockReceipt.getPackages();
//
//        }
//        return totalPackages;
//    }
//
//    private double getTotalCubicFeet(List<DockReceipt> dockReceipts) {
//        double totalCubicFeet = 0;
//        for (DockReceipt dockReceipt : dockReceipts) {
//            totalCubicFeet += dockReceipt.getCubicFeet();
//
//        }
//        return totalCubicFeet;
//    }
//
//    private void showRemoveConfirmation(DockReceipt removedItem, int pos) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        DockReceipt dr = dockReceipts.get(pos);
//        builder.setTitle("Remove DR.");
//        builder.setMessage("Remove DR: " + dr.getDrNumber() + "?");
//
//        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dockReceipts.remove(pos);
//                dockReceiptsAdapter.notifyItemRemoved(pos);
//                TastyToasty.orange(getApplicationContext(), "Item deleted.", R.drawable.ic_check_ok).show();
//
////                ToastUtils.showToast(getApplicationContext(), "Item deleted.", 1);
//                dialogInterface.dismiss();
//            }
//        });
//
//        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dockReceipts.remove(pos);
//                dockReceiptsAdapter.notifyItemRemoved(pos);
//                DockReceipt dr = dockReceipts.get(pos);
//                TastyToasty.orange(getApplicationContext(), "Remove: " + dr.getDrNumber() + " from Repack", R.drawable.ic_check_ok).show();
//                dialogInterface.dismiss();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dockReceiptsAdapter.notifyItemChanged(pos);
//                dialogInterface.dismiss();
//
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
//
//    void resetControls() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(RepackActivity.this, R.style.CustomAlertDialogStyleGeneral);
//        builder.setTitle("Clear Confirmation");
//        builder.setMessage("Are you sure you want to clear the controls?");
//
//
//        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
//            // Reset the text views
//            etRepackContainer.setText("");
//            etDrToRepack.setText("");
//            chipTotalPackages.setText("0");
//            chipTotalCubicFeet.setText("0.0");
//            chipRepackCount.setText("0");
//            etRepackedCount.setVisibility(View.INVISIBLE);
//            spinnerRepackType.setEnabled(true);
//
//            // Select the first value of the spinner
//            spinnerRepackType.setSelection(0);
//
//            // Clear the list of the recycler view
//            dockReceipts.clear();
//            dockReceiptsAdapter.notifyDataSetChanged();
//
//            preferenceService.putString("dr_repack_container", "");
//            TastyToasty.green(getApplicationContext(), "Controls cleared", R.drawable.ic_brush_clear).show();
//            dialogInterface.dismiss();
//
//            // Request focus for etRepackContainer after clearing
//            etRepackContainer.requestFocus();
//            etRepackContainer.setInputType(InputType.TYPE_NULL);
//
//            // Show the soft keyboard
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(etRepackContainer, InputMethodManager.SHOW_IMPLICIT);
//        });
//
//        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//



}
