package com.kingocean.warehouseapp.fragments;

import static android.app.Activity.RESULT_OK;
import static com.kingocean.warehouseapp.utils.ImageUtils.generateRandomFilename;
import static com.kingocean.warehouseapp.utils.ImageUtils.rotateImage;
import static com.kingocean.warehouseapp.utils.ImageUtils.scaleToFitWidth;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;
//import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.data.adapters.DockReceiptsAdapter;
import com.kingocean.warehouseapp.data.model.DockReceipt;
import com.kingocean.warehouseapp.data.model.GeneralResponse;
import com.kingocean.warehouseapp.data.model.LoggedInUser;
import com.kingocean.warehouseapp.data.model.ScannedDr;
import com.kingocean.warehouseapp.services.CameraService;
import com.kingocean.warehouseapp.services.NewRepackService;
import com.kingocean.warehouseapp.services.PreferenceService;
import com.kingocean.warehouseapp.utils.MethodType;
import com.kingocean.warehouseapp.utils.PreferenceKeys;
import com.kingocean.warehouseapp.utils.ScannedDrExtractor;
import com.kingocean.warehouseapp.utils.TastyToasty;
import com.kingocean.warehouseapp.utils.TonePlayer;
import com.kingocean.warehouseapp.views.RepackProgressBarView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;


public class RepackScanFragment extends Fragment {

    //TODO: PROGRESS
    private LinearLayoutCompat llRepackProgress;
    private RepackProgressBarView repackProgressBarView;
    private int totalItems = 0;
    private int totalAssignedToContainer = 0;
    private int repackedItems = 0;
    private Dialog saveImgDialog;
    private int pendingItems = 0;

    private Button btnDrListToggle;
    private boolean isRepackModeList;
    private TextView tvListType;

    ImageButton btTakePicture;
    String currentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private final String TAG = "TAKEPIC";
    File picFile;

    String imgFileName = "";

    ScannedDr currentScannedDR;
    String currentDR = "";
    NewRepackService repackService;
    CameraService cameraService;


//    SCAN RELATED

    private RecyclerView recyclerViewDocReceipts;
    //    private Spinner spinnerRepackType;
    private EditText etContainerType;
    private DockReceiptsAdapter dockReceiptsAdapter;
    private List<DockReceipt> dockReceipts;

    private Button btClear;
    private EditText etRepackContainer;
    private ImageView ivPointerContainer;

    private EditText etDrToRepack;
    private ImageView ivPointerRepack;


    // Clear buttons
    private ImageButton btnClearContainer;
    private ImageButton btnClearRepackDr;


    // TOTALS
    private int totalPackages;
    private int repackedPackages;
    private String totalCubicFeet;

    //    private Chip chipTotalPackages;
    private TextView tvTotalPkgs;
    private TextView tvRepackedPkgs;
    private TextView chipTotalCubicFeet;
    private TextView chipRepackCount;

    // private TextView etRepackedCount;
    PreferenceService preferenceService;

    private List<DockReceipt> currentRepacked;
    private String containerValue;


    private ProgressBar progressBar;

    public interface RepackContainerValueListener {
        void onRepackContainerValue(String value);
    }


    private RepackContainerValueListener listener;

    private void notifyRepackContainerValue() {
        String value = etRepackContainer.getText().toString();
        Log.i(TAG, "notifyRepackContainerValue: was called!!" + value);
        currentDR = value;
        if (listener != null) {
            Log.e(TAG, value.toString());
            listener.onRepackContainerValue(value);
        }
    }

    private void sendRepackContainerValueToActivity() {
        Log.e(TAG, "sendRepackContainerValueToActivity" + currentDR);
        notifyRepackContainerValue();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (RepackContainerValueListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement RepackContainerValueListener");
        }
    }

    public String getRepackContainerValue() {
        return etRepackContainer.getText().toString();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        preferenceService = PreferenceService.getInstance(requireContext(), PreferenceKeys.credentials);
        // Set the OnTabSelectedListener for the TabLayout
        TabLayout tabLayout = requireActivity().findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

            // Other methods in the OnTabSelectedListener...

        });

        View view = inflater.inflate(R.layout.fragment_repack_scan, container, false);
        // SERVICE
        repackService = new NewRepackService();
        cameraService = new CameraService();
        btClear = view.findViewById(R.id.btClear);
        isRepackModeList = true;


        saveImgDialog = new Dialog(requireContext());
        saveImgDialog.setContentView(R.layout.save_image_progress);
        saveImgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        saveImgDialog.setCancelable(false);
        saveImgDialog.setCanceledOnTouchOutside(false);

        etRepackContainer = view.findViewById(R.id.etDrContainer);
        etDrToRepack = view.findViewById(R.id.etDrToRepack);

        ivPointerContainer = view.findViewById(R.id.pointer_container);
        ivPointerRepack = view.findViewById(R.id.pointer_repack);


//        spinnerRepackType = view.findViewById(R.id.spinnerRepackType);
        etContainerType = view.findViewById(R.id.etContainerType);
        etContainerType.setText("");
        etContainerType.setEnabled(false);

        btnClearContainer = view.findViewById(R.id.btnClearContainer);
        btnClearRepackDr = view.findViewById(R.id.btnClearRepackDr);

//        etRepackedCount = view.findViewById(R.id.tvRepacked);
//        etRepackedCount.setVisibility(View.INVISIBLE);

//        chipTotalPackages = view.findViewById(R.id.chipTotalPackages);
        tvTotalPkgs = view.findViewById(R.id.tvTotalPkgs);
        tvRepackedPkgs = view.findViewById(R.id.tvRepackedPkgs);

        chipTotalCubicFeet = view.findViewById(R.id.chipTotalCubicFeet);
        chipRepackCount = view.findViewById(R.id.chipRepackCount);

        btTakePicture = view.findViewById(R.id.btnTakePic);


        chipTotalCubicFeet.setText("0.0");
//        chipTotalPackages.setText("0");
        tvRepackedPkgs.setText("0");
        tvTotalPkgs.setText("0");
        btTakePicture.setEnabled(false);
        btTakePicture.setVisibility(View.INVISIBLE);


        dockReceipts = new ArrayList<>();
        dockReceiptsAdapter = new DockReceiptsAdapter(dockReceipts);
        recyclerViewDocReceipts = view.findViewById(R.id.rvDrsRepacked);
        recyclerViewDocReceipts.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerViewDocReceipts.setAdapter(dockReceiptsAdapter);

        progressBar = view.findViewById(R.id.progressBarScan);


        // region Progress

        llRepackProgress = view.findViewById(R.id.llRepackProgress);
        llRepackProgress.setVisibility(View.GONE);
        repackProgressBarView = view.findViewById(R.id.progressBarView);

        Button btnRepacked = view.findViewById(R.id.btnRepacked);
        Button btnPending = view.findViewById(R.id.btnPendingRepack);
        tvListType = view.findViewById(R.id.lblListType);

        btnDrListToggle = view.findViewById(R.id.btnDrListToggle);

        btnRepacked.setOnClickListener(v -> {
            int containerId = Integer.parseInt(currentDR);
            updateRecyclerViewData(containerId, MethodType.REPACKED);
        });

        btnPending.setOnClickListener(v -> {
            int containerId = Integer.parseInt(currentDR);
            updateRecyclerViewData(containerId, MethodType.PENDING);
        });

        repackProgressBarView.setOnClickListener(v -> {
            int containerId = Integer.parseInt(currentDR);
            updateRecyclerViewData(containerId, MethodType.ASSIGNED);
        });


        btnDrListToggle.setOnClickListener(v -> {

            AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.scale_animation);
            animatorSet.setTarget(v);
            animatorSet.start();

            int containerId = Integer.parseInt(currentDR);
            if (!isRepackModeList) {
                updateRecyclerViewData(containerId, MethodType.REPACKED);
            } else {
                updateRecyclerViewData(containerId, MethodType.PENDING);
            }
            // Toggle the mode
            isRepackModeList = !isRepackModeList;
            // Update button appearance based on new mode
            updateToggleButtonAppearance();
        });

        // endregion Progress

        //region repack container

        etRepackContainer.post(() -> {
            etRepackContainer.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(etRepackContainer, InputMethodManager.SHOW_IMPLICIT);
        });

        etRepackContainer.setOnFocusChangeListener((view1, hasFocus) -> {
            if (hasFocus) {
                if (etRepackContainer.getText().length() > 0) {
//                    resetControls();
//                    etRepackContainer.getText().clear();
                }
                etRepackContainer.setInputType(InputType.TYPE_NULL);
                etRepackContainer.setText("", TextView.BufferType.EDITABLE);
            }

        });
        etRepackContainer.addTextChangedListener(new TextWatcher() {
            private boolean executeScanContainerCode = true;
            private Timer timer = new Timer();
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable workRunnable;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable et) {
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> callBack(et.toString());
                handler.postDelayed(workRunnable, 100);
            }

            private void callBack(String et) {

                try {

                    String scannedContainerValue = etRepackContainer.getText().toString();
                    String extractedValue = ScannedDrExtractor.extractContainerValue(scannedContainerValue);
//                scannedContainerValue = etRepackContainer.getText().toString();
                    Log.e("EXTRACTED", "callBack: " + extractedValue);


                    sendRepackContainerValueToActivity();
                    if (extractedValue != null && extractedValue.length() == 8) {
                        btTakePicture.setEnabled(true);
                        if (btTakePicture.isEnabled()) {
                            btTakePicture.setVisibility(View.VISIBLE);
                        } else {
                            btTakePicture.setVisibility(View.INVISIBLE);
                        }
                        preferenceService.putString("dr_repack_container", extractedValue);

                        if (isDrContainer(extractedValue)) {
                            executeScanContainerCode = true;
//                           etRepackContainer.setText("");
//                           etRepackContainer.requestFocus();
//                           etRepackContainer.setInputType(InputType.TYPE_NULL);
                        } else {

                            executeScanContainerCode = false;
                            etRepackContainer.setText("");
                            btTakePicture.setVisibility(View.INVISIBLE);
                            etRepackContainer.requestFocus();
                            etRepackContainer.setInputType(InputType.TYPE_NULL);
                        }


                        if (executeScanContainerCode) {
                            etRepackContainer.removeTextChangedListener(this);
                            Log.e("EXTRACTED", "callBack: " + extractedValue);
                            etRepackContainer.setText(extractedValue);
                            etRepackContainer.addTextChangedListener(this);
                            dockReceipts.clear();
//
                            progressBar.setVisibility(View.VISIBLE);
                            recyclerViewDocReceipts.setVisibility(View.GONE);
//
                            int drRepackContainer = Integer.parseInt(etRepackContainer.getText().toString());
                            ScanContainerTask scanContainerTask = new ScanContainerTask(requireContext(), drRepackContainer);
                            scanContainerTask.execute();

//                            callScanContainer();
                            llRepackProgress.setVisibility(View.VISIBLE);
                            etRepackContainer.setEnabled(false);
                            etDrToRepack.requestFocus();
//                            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            etDrToRepack.setInputType(InputType.TYPE_NULL);
//                            imm.showSoftInput(etDrToRepack, InputMethodManager.SHOW_IMPLICIT);
                        }


                    } else {
                        // Handle the case where the scannedContainerValue does not meet the length requirement
                        if (scannedContainerValue.length() >= 8) {

                            showInvalidContainerScannedCode(scannedContainerValue, this);
                        }
//                        TastyToasty.orange(requireContext(), "", R.drawable.ic_check_ok).show();
                    }
                } catch (Exception ex) {
                    Log.e("EX", "callBack: " + ex.getMessage());
                }


            }
        });

        //endregion repack container

        //region drToRepack

        etDrToRepack.setOnFocusChangeListener((view1, hasFocus) -> {
            if (hasFocus) {
                if (etRepackContainer.getText().length() == 0) {
                    etRepackContainer.requestFocus();
                    ivPointerContainer.setVisibility(View.VISIBLE);
                    etDrToRepack.setInputType(InputType.TYPE_NULL);
//                    etDrToRepack.setText("", TextView.BufferType.EDITABLE);
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {

                        etDrToRepack.clearFocus();
                        ivPointerContainer.setVisibility(View.INVISIBLE);
                    }, 3000);
                }

                if (etDrToRepack.getText().length() > 0) {
                    resetControls();
                    etDrToRepack.getText().clear();
                }
                // Change the background color to indicate focus


            } else {

                ivPointerContainer.setVisibility(View.INVISIBLE);
            }

        });

        etDrToRepack.addTextChangedListener(new TextWatcher() {
            Handler handler = new Handler(Looper.getMainLooper() /*UI thread*/);
            Runnable workRunnable;

            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable et) {
                handler.removeCallbacks(workRunnable);
                workRunnable = () -> callBack(et.toString());
                handler.postDelayed(workRunnable, 100);

            }

            private void callBack(String scan) {

                String scannedDrToRepack = etDrToRepack.getText().toString();
                ScannedDr scannedDr = ScannedDrExtractor.extractScannedDr(scannedDrToRepack);
                String extractedValue = ScannedDrExtractor.extractValue(scannedDrToRepack);
                ScannedDr scannedDrNumeric = ScannedDrExtractor.extractNumeric(scannedDrToRepack);

//                if (extractedValue != null  && extractedValue.length() == 8) {
                if (extractedValue != null && scannedDr != null) {
//                if (extractedValue != null &&
//                        // TODO: numeric test purposes
//                        scannedDr != null || scannedDrNumeric != null) {
                    if (isDrAlreadyRepackedWithSeq(etRepackContainer.getText().toString(),
                            scannedDr)) {
                        etDrToRepack.removeTextChangedListener(this);
                        etDrToRepack.setText(extractedValue);
                        etDrToRepack.addTextChangedListener(this);
//                        showDeRepackConfirmation(scannedDr.getDrNumber());
                        showDeRepackConfirmationWithSeq(scannedDr);
                    } else {
                        etDrToRepack.removeTextChangedListener(this);
                        etDrToRepack.setText(extractedValue);
                        etDrToRepack.addTextChangedListener(this);


//                        callRepackOrDeRepack();
                        callRepackOrDeRepackWithSeq(scannedDr);
                        etDrToRepack.requestFocus();
                    }
                } else {
                    // Unsuccessful scanning, take appropriate action
                    // For example, show an error message to the user
                    if (scannedDrToRepack.length() >= 8) {
                        showInvalidRepackScannedCode(scannedDrToRepack, this);
//                        Toast.makeText(getContext(), "Scanning failed, please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            private boolean isDockReceiptAlreadyPresent(String drToRepack) {
                for (DockReceipt receipt : dockReceipts) {
                    if (receipt.getDrNumber().equals(drToRepack)) {
                        return true;
                    }
                }
                return false;
            }


            private void showDeRepackConfirmation(String drToRepack) {
                TonePlayer tonePlayer = new TonePlayer();
                tonePlayer.playErrorSound(1000);

                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialogStyleAlert);
                builder.setTitle("De-Repack Confirmation");
                builder.setMessage("DE-REPACK : " + drToRepack + " ?.");
                builder.setPositiveButton("DE-REPACK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Perform de-repack operation here
                        callRepackOrDeRepack();
                        callScanContainerToRefreshList();
                        etDrToRepack.requestFocus();
                    }
                });
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                    // Handle cancel action here
                    etDrToRepack.setText("");
                });
                builder.show();
            }
        });
        // endregion drToRepack

        // region Camera
        btTakePicture.setOnClickListener(view1 -> {
            try {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra("android.intent.extra.quickCapture", true);
                picFile = null;

                picFile = createImageFile(requireContext());

                if (picFile != null) {
                    Uri picUri = FileProvider.getUriForFile(requireContext(), "com.kingocean.warehouseapp.fileprovider", picFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }


            } catch (IOException ex) {
                ex.printStackTrace();
                Log.e(TAG, ex.getMessage());
                TastyToasty.red(requireContext(), "Something went wrong!!", R.drawable.ic_error).show();
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e(TAG, ex.getMessage());
                TastyToasty.red(requireContext(), "Something went wrong!!", R.drawable.ic_error).show();
            }
        });

        // endregion Camera

        // region Clear controls

        btClear.setOnClickListener(view1 -> {
            resetControls();
        });

        btnClearContainer.setOnClickListener(v -> {
            etRepackContainer.setEnabled(true);
            if (etRepackContainer.getText().length() > 0) {

                resetControls();
            }

        });
        btnClearRepackDr.setOnClickListener(v -> {
            etDrToRepack.setText("");

        });


        // endregion Clear controls

        return view;
    }

    private void showInvalidContainerScannedCode(String scannedContainerValue, TextWatcher textWatcher) {
        TonePlayer tonePlayer = new TonePlayer();
        tonePlayer.playErrorSound(1000);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialogStyleAlert);
        builder.setTitle("UNREGISTER BAR CODE");
//                builder.setMessage("Can't process scanned the barcode ." + "\n"
//                        + scannedContainerValue);
        builder.setMessage("NO ES UN DR VALIDO" + "\n"
                + scannedContainerValue);
        builder.setPositiveButton("OK!", (dialogInterface, i) -> {
            // Perform de-repack operation here
            //            callRepackOrDeRepackWithSeq(scannedDr);
            //            callScanContainerToRefreshList();
            etRepackContainer.removeTextChangedListener(textWatcher);
            etRepackContainer.setText("");
            etRepackContainer.requestFocus();
            etRepackContainer.addTextChangedListener(textWatcher);
        });
        //        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
        //            // Handle cancel action here
        //            etDrToRepack.setText("");
        //        });
        builder.show();
    }

    private void showInvalidRepackScannedCode(String scannedRepackValue, TextWatcher textWatcher) {
        TonePlayer tonePlayer = new TonePlayer();
        tonePlayer.playErrorSound(1000);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialogStyleAlert);
        builder.setTitle("UNREGISTER BAR CODE");
//                builder.setMessage("Can't process scanned the barcode ." + "\n"
//                        + scannedContainerValue);
        builder.setMessage("NO ES UN DR VALIDO" + "\n"
                + scannedRepackValue);
        builder.setPositiveButton("OK!", (dialogInterface, i) -> {
            // Perform de-repack operation here
            //            callRepackOrDeRepackWithSeq(scannedDr);
            //            callScanContainerToRefreshList();
            etDrToRepack.removeTextChangedListener(textWatcher);
            etDrToRepack.setText("");
            etDrToRepack.requestFocus();
            etDrToRepack.addTextChangedListener(textWatcher);
        });
        //        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
        //            // Handle cancel action here
        //            etDrToRepack.setText("");
        //        });
        builder.show();
    }

    private void showDeRepackConfirmationWithSeq(ScannedDr scannedDr) {
        TonePlayer tonePlayer = new TonePlayer();
        tonePlayer.playErrorSound(1000);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialogStyleAlert);
        builder.setTitle("De-Repack Confirmation");
        builder.setMessage("DR NUMBER: " + scannedDr.getDrNumber() + "\n"
                + "U: " + scannedDr.getUnitSequence() + " ?.");
        builder.setPositiveButton("DE-REPACK", (dialogInterface, i) -> {
            // Perform de-repack operation here
            callRepackOrDeRepackWithSeq(scannedDr);
            callScanContainerToRefreshList();
            etDrToRepack.requestFocus();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            // Handle cancel action here
            etDrToRepack.setText("");
        });
        builder.show();
    }
//    private void showInvalidScannedCode(String scanned) {
//        TonePlayer tonePlayer = new TonePlayer();
//        tonePlayer.playErrorSound(1000);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.CustomAlertDialogStyleAlert);
//        builder.setTitle("UNREGISTER BAR CODE");
//        builder.setMessage("Can't process scanned the barcode ." + "\n"
//        + scanned);
//        builder.setPositiveButton("OK!", (dialogInterface, i) -> {
//            // Perform de-repack operation here
////            callRepackOrDeRepackWithSeq(scannedDr);
////            callScanContainerToRefreshList();
//
//            etRepackContainer.requestFocus();
//        });
////        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
////            // Handle cancel action here
////            etDrToRepack.setText("");
////        });
//        builder.show();
//    }

    // region HELPERS
    private void updateToggleButtonAppearance() {
        if (!isRepackModeList) {
            btnDrListToggle.setText("Repacked");
//            btnDrListToggle.setBackgroundColor(getResources().getColor(R.color.md_red_200)); // Use your color resource
        } else {
            btnDrListToggle.setText("Pending?");
//            btnDrListToggle.setBackgroundColor(getResources().getColor(R.color.md_amber_50)); // Use your color resource
        }
    }

    private boolean isDrAlreadyRepacked(String container, String repackToTest, String seq) {

        GeneralResponse<String> response = repackService.isDrAlreadyRepacked(
                Integer.parseInt(container),
                Integer.parseInt(repackToTest),
                Integer.parseInt(seq));
        return response.isSuccess();
    }

    private boolean isDrAlreadyRepackedWithSeq(String container, ScannedDr scannedDr) {

        GeneralResponse<String> response = repackService.isDrAlreadyRepacked(
                Integer.parseInt(container),
                Integer.parseInt(scannedDr.getDrNumber()),
                Integer.parseInt(scannedDr.getUnitSequence()));
        return response.isSuccess();
    }

//    private void isDrAlreadyRepackedInBackground(final String container, final String repackToTest) {
//        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                // Call the isDrAlreadyRepacked method here and return the result
//                return isDrAlreadyRepacked(container, repackToTest);
//            }
//
//            @Override
//            protected void onPostExecute(Boolean isRepacked) {
//                super.onPostExecute(isRepacked);
//                // Update the UI based on the result
//                if (isRepacked) {
//                    // Handle case where DR is already repacked
//                } else {
//                    // Handle case where DR is not yet repacked
//                }
//            }
//        };
//
//        // Execute the AsyncTask
//        task.execute();
//    }

    private void updateRecyclerViewData(int containerId, MethodType methodType) {
        List<DockReceipt> updatedDockReceipts;

        switch (methodType) {
            case REPACKED:
                updatedDockReceipts = repackService.getDockReceiptsRepacked(containerId);
                repackedItems = updatedDockReceipts.size();
                tvListType.setText("Repacked");

                break;
            case PENDING:
                updatedDockReceipts = repackService.getDockReceiptsPending(containerId);
                pendingItems = updatedDockReceipts.size();

                tvListType.setText("Pending");

                break;
            case ASSIGNED:
                updatedDockReceipts = repackService.getDockReceiptsAssignedToContainer(containerId);
                tvListType.setText("Assigned");
                totalItems = updatedDockReceipts.size();
                break;
            default:
                updatedDockReceipts = new ArrayList<>(); // Handle default case
                break;
        }
        updateCompletionBar();
        dockReceipts.clear();
        dockReceipts.addAll(updatedDockReceipts);
        chipRepackCount.setText(String.valueOf(updatedDockReceipts.size()));
        totalPackages = getTotalsPackages(updatedDockReceipts);
        totalCubicFeet = getFormattedTotalCubicFeet(updatedDockReceipts);

        tvRepackedPkgs.setText(String.valueOf(updatedDockReceipts.size()));


//        tvTotalPkgs.setText(String.valueOf(totalPackages));
//        chipTotalPackages.setText(String.valueOf(totalPackages));
        chipTotalCubicFeet.setText(String.valueOf(totalCubicFeet));
        dockReceiptsAdapter.notifyDataSetChanged();
    }

    private void callScanContainerToRefreshList() {
        Log.e("EXTRACTED", "callBack: " + "callScanContainer");

        TonePlayer tonePlayer = new TonePlayer();

        String drRepackString = etRepackContainer.getText().toString();

        if (drRepackString.length() == 8) {
            int drRepackContainer = Integer.parseInt(drRepackString);

//            RepackType selectedRepackType = (RepackType) spinnerRepackType.getSelectedItem();

            List<DockReceipt> retrievedDockReceipts =
                    repackService.getDockReceiptsPending(drRepackContainer);
            tvListType.setText("Pending");
            currentRepacked = retrievedDockReceipts;
            tonePlayer.playSuccessSound(100);
            tonePlayer.release();

            // Add the retrieved dock receipts to the adapter
            dockReceipts.addAll(retrievedDockReceipts);
            dockReceipts.clear();
            // Clear the existing dock receipts in the adapter
            if (!retrievedDockReceipts.isEmpty()) {
                // Add the retrieved dock receipts to the adapter
                dockReceipts.addAll(retrievedDockReceipts);
//                etRepackedCount.setVisibility(View.VISIBLE);
//                etRepackedCount.setText(String.valueOf(retrievedDockReceipts.size()));
                chipRepackCount.setText(String.valueOf(retrievedDockReceipts.size()));
                tvRepackedPkgs.setText(String.valueOf(retrievedDockReceipts.size()));


            } else {
//                etRepackedCount.setVisibility(View.VISIBLE);
//                etRepackedCount.setText("No");
                chipRepackCount.setText("0");
                tvRepackedPkgs.setText("0");


            }
            dockReceiptsAdapter.notifyDataSetChanged();


            totalPackages = getTotalsPackages(retrievedDockReceipts);
            totalCubicFeet = getFormattedTotalCubicFeet(retrievedDockReceipts);

//            chipTotalPackages.setText(String.valueOf(totalPackages));
//            tvTotalPkgs.setText(String.valueOf(totalPackages));
            chipTotalCubicFeet.setText(totalCubicFeet);
//                    hideSoftKeyboard(view);


        } else {
            TastyToasty.orange(
                    requireContext(),
                    "Please enter a DR RePack number",
                    R.drawable.ic_question).show();
        }

    }

    private void callRepackOrDeRepack() {
        TonePlayer tonePlayer = new TonePlayer();

//        String repackContainerType = spinnerRepackType.getSelectedItem().toString();
        String drRepackContainerString = etRepackContainer.getText().toString();
        String drToRepackString = etDrToRepack.getText().toString();


        try {
            // Validate drRepackContainerString separately
            if (drRepackContainerString.length() == 8) {
                // Continue with the rest of the validation

                // Validate EditText fields
                if (TextUtils.isEmpty(drToRepackString) && drToRepackString.length() < 8) {
                    TastyToasty.orange(
                            requireContext(),
                            "Please enter a DR RePack number",
                            R.drawable.ic_question).show();
                    return;
                }

                // Parse values as integers
                int drRepackContainer = Integer.parseInt(drRepackContainerString);
                int drToRepack = Integer.parseInt(drToRepackString);


                preferenceService = PreferenceService.getInstance(requireContext(), PreferenceKeys.credentials);
                String id = preferenceService.getString("id", "");
                int idValue = 0;
                try {
                    idValue = Integer.parseInt(id);
                } catch (NumberFormatException e) {

                    Log.e("USERID", e.getMessage());
                }
                GeneralResponse<String> response = repackService.repackOrDeRepack(drRepackContainer, drToRepack, idValue);

                if (response.isSuccess()) {


                    etDrToRepack.setText("");
                    etDrToRepack.requestFocus();
                    etDrToRepack.setInputType(InputType.TYPE_NULL);
                    TastyToasty.green(
                            requireContext(),
                            response.getMessage(),
                            R.drawable.ic_check_ok).show();

                    List<DockReceipt> retrievedDockReceipts = repackService.getDockReceiptsRepacked(drRepackContainer);
                    tvListType.setText("Repacked");
                    dockReceipts.clear(); // Clear existing dock receipts

                    Log.e("DR", "retrieve" + retrievedDockReceipts.size());
                    Log.e("DR", "dockreceipts" + retrievedDockReceipts.size());


                    if (retrievedDockReceipts.isEmpty()) {
//                        etRepackedCount.setVisibility(View.INVISIBLE);
//                        etRepackedCount.setText("");
                        chipRepackCount.setText(0);
                        tvRepackedPkgs.setText(0);
                        dockReceipts.clear();
                    } else {
                        dockReceipts.clear();
                        dockReceipts.addAll(retrievedDockReceipts);
                        // rerere
                        UpdateItemsTask updateItemsTask = new UpdateItemsTask();
                        updateItemsTask.execute(drRepackContainer);
//                        etRepackedCount.setVisibility(View.VISIBLE);
//                        etRepackedCount.setText(String.valueOf(retrievedDockReceipts.size()));
                        chipRepackCount.setText(String.valueOf(retrievedDockReceipts.size()));
                        tvRepackedPkgs.setText(String.valueOf(retrievedDockReceipts.size()));
                    }

//                    dockReceipts.addAll(retrievedDockReceipts);
                    dockReceiptsAdapter.notifyDataSetChanged(); // Refresh the RecyclerView


                    totalPackages = getTotalsPackages(retrievedDockReceipts);
                    totalCubicFeet = getFormattedTotalCubicFeet(retrievedDockReceipts);

//                    chipTotalPackages.setText(String.valueOf(totalPackages));
                    chipTotalCubicFeet.setText(String.valueOf(totalCubicFeet));
//                        hideSoftKeyboard(view);
                } else {

                    AlertDialog.Builder dlgAlert;

                    tonePlayer.playErrorSound(1000);
                    tonePlayer.release();

                    dlgAlert = new AlertDialog.Builder(requireContext(), R.style.DialogAlertThemeRed);
                    Log.i(TAG, "callRepackOrDeRepack: " + response.getMessage());
                    dlgAlert.setMessage(Html.fromHtml(response.getMessage()));
                    dlgAlert.setTitle("REPACK ERROR");
                    dlgAlert.setNeutralButton("Ok", null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.create().show();

//                    TastyToasty.red(
//                            getApplicationContext(),
//                            response.getMessage(),
//                            R.drawable.ic_error).show();
                    etDrToRepack.setText("");
                    etDrToRepack.requestFocus();
                    etDrToRepack.setInputType(InputType.TYPE_NULL);
                }
            } else {
                tonePlayer.playErrorSound(1000);
                TastyToasty.green(
                        requireContext(),
                        "Value should be 8 characters long.",
                        R.drawable.ic_question).show();
            }
        } catch (Exception e) {
            Log.e("EXCEPTION", e.getMessage());
            e.printStackTrace();
        }

    }

    private void callRepackOrDeRepackWithSeq(ScannedDr scannedDr) {
        TonePlayer tonePlayer = new TonePlayer();

//        String repackContainerType = spinnerRepackType.getSelectedItem().toString();
        String drRepackContainerString = etRepackContainer.getText().toString();
        String drToRepackString = scannedDr.getDrNumber();
        String seqString = scannedDr.getUnitSequence();


        try {
            // Validate drRepackContainerString separately
            if (!drRepackContainerString.isEmpty() && drRepackContainerString.length() == 8) {
                // Continue with the rest of the validation

                // Validate EditText fields
                if (TextUtils.isEmpty(drToRepackString) && drToRepackString.length() < 8) {
                    TastyToasty.orange(
                            requireContext(),
                            "Please enter a DR RePack number",
                            R.drawable.ic_question).show();
                    return;
                }
                if (TextUtils.isEmpty(seqString)) {
                    TastyToasty.orange(
                            requireContext(),
                            "No Unit Sequence Found in the scanned Label",
                            R.drawable.ic_question).show();
                    return;
                }

                // Parse values as integers
                int drRepackContainer = Integer.parseInt(drRepackContainerString);


                preferenceService = PreferenceService.getInstance(requireContext(), PreferenceKeys.credentials);
                String id = preferenceService.getString("id", "");
                int userId = 0;
                try {
                    userId = Integer.parseInt(id);
                } catch (NumberFormatException e) {

                    Log.e("USERID", e.getMessage());
                }
                GeneralResponse<String> response = repackService.repackOrDeRepackWithSeq(drRepackContainer, scannedDr, userId);

                if (response.isSuccess()) {


                    etDrToRepack.setText("");
                    etDrToRepack.requestFocus();
                    etDrToRepack.setInputType(InputType.TYPE_NULL);
                    TastyToasty.green(
                            requireContext(),
                            response.getMessage(),
                            R.drawable.ic_check_ok).show();

                    List<DockReceipt> retrievedDockReceipts = repackService.getDockReceiptsRepacked(drRepackContainer);
                    tvListType.setText("Repacked");
                    dockReceipts.clear(); // Clear existing dock receipts

                    Log.e("DR", "retrieve" + retrievedDockReceipts.size());
                    Log.e("DR", "dockreceipts" + retrievedDockReceipts.size());


                    if (retrievedDockReceipts.isEmpty()) {
//                        etRepackedCount.setVisibility(View.INVISIBLE);
//                        etRepackedCount.setText("");
                        chipRepackCount.setText(0);
                        tvRepackedPkgs.setText(0);
                        dockReceipts.clear();
                    } else {
                        dockReceipts.clear();
                        dockReceipts.addAll(retrievedDockReceipts);
                        // rerere
                        UpdateItemsTask updateItemsTask = new UpdateItemsTask();
                        updateItemsTask.execute(drRepackContainer);
//                        etRepackedCount.setVisibility(View.VISIBLE);
//                        etRepackedCount.setText(String.valueOf(retrievedDockReceipts.size()));
                        chipRepackCount.setText(String.valueOf(retrievedDockReceipts.size()));
                        tvRepackedPkgs.setText(String.valueOf(retrievedDockReceipts.size()));
                    }

//                    dockReceipts.addAll(retrievedDockReceipts);
                    dockReceiptsAdapter.notifyDataSetChanged(); // Refresh the RecyclerView


                    totalPackages = getTotalsPackages(retrievedDockReceipts);
                    totalCubicFeet = getFormattedTotalCubicFeet(retrievedDockReceipts);

//                    chipTotalPackages.setText(String.valueOf(totalPackages));
                    chipTotalCubicFeet.setText(totalCubicFeet);
//                        hideSoftKeyboard(view);
                } else {

                    AlertDialog.Builder dlgAlert;

                    tonePlayer.playErrorSound(1000);
                    tonePlayer.release();

                    dlgAlert = new AlertDialog.Builder(requireContext(), R.style.DialogAlertThemeRed);
                    Log.i(TAG, "callRepackOrDeRepack: " + response.getMessage());
                    dlgAlert.setMessage(Html.fromHtml(response.getMessage()));
                    dlgAlert.setTitle("REPACK ERROR");
                    dlgAlert.setNeutralButton("Ok", null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.create().show();

//                    TastyToasty.red(
//                            getApplicationContext(),
//                            response.getMessage(),
//                            R.drawable.ic_error).show();
                    etDrToRepack.setText("");
                    etDrToRepack.requestFocus();
                    etDrToRepack.setInputType(InputType.TYPE_NULL);
                }
            } else {
                tonePlayer.playErrorSound(1000);
                TastyToasty.green(
                        requireContext(),
                        "Value should be 8 characters long.",
                        R.drawable.ic_question).show();
            }
        } catch (Exception e) {
            Log.e("EXCEPTION", e.getMessage());
            e.printStackTrace();
        }

    }

    private boolean isDrContainer(String extractedValue) {

        GeneralResponse<String> isContainer =
                repackService.isScannedDrValidContainer(Integer.parseInt(extractedValue));
        if (isContainer.isSuccess()) {
//                    will be added
            Log.i(TAG, "isDrContainer: yes");
            etContainerType.setText(isContainer.getContainerType());
            return true;
        }
        Log.i(TAG, "isDrContainer: no");

        TonePlayer tonePlayer = new TonePlayer();
        AlertDialog.Builder dlgAlert;
        tonePlayer.playErrorSound(500);
        tonePlayer.release();
        dlgAlert = new AlertDialog.Builder(requireContext(), R.style.DialogAlertThemeRed);
        dlgAlert.setMessage(Html.fromHtml(isContainer.getMessage()));
        dlgAlert.setTitle("ERROR");
        dlgAlert.setNeutralButton("Ok", null);
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();

        return false;
    }

    private void resetControls() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogStyleGeneral);
        builder.setTitle("Clear Confirmation");
        builder.setMessage("Are you sure you want to clear the controls?");

        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            // Reset the text views
            etRepackContainer.setText("");
            etContainerType.setText("");
            etDrToRepack.setText("");
            tvTotalPkgs.setText("0");
//            chipTotalPackages.setText("0");
            chipTotalCubicFeet.setText("0.0");
            chipRepackCount.setText("0");
            tvRepackedPkgs.setText("0");
//            etRepackedCount.setVisibility(View.INVISIBLE);
//            spinnerRepackType.setEnabled(true);
//            TastyToasty.red(requireContext(), "LISTENER" + etRepackContainer.getText(), R.drawable.ic_cancel).show();
            sendRepackContainerValueToActivity();


            llRepackProgress.setVisibility(View.GONE);

            // Select the first value of the spinner
//            spinnerRepackType.setSelection(0);


            // Clear the list of the recycler view
            dockReceipts.clear();
            dockReceiptsAdapter.notifyDataSetChanged();

            preferenceService.putString("dr_repack_container", "");
            TastyToasty.green(requireContext(), "Controls cleared", R.drawable.ic_brush_clear).show();
            dialogInterface.dismiss();

            // Request focus for etRepackContainer after clearing
            etRepackContainer.requestFocus();
            etRepackContainer.setInputType(InputType.TYPE_NULL);

            btTakePicture.setEnabled(false);
            btTakePicture.setVisibility(View.INVISIBLE);
            // TODO: KEYBOARD
            // Show the soft keyboard
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(etRepackContainer, InputMethodManager.SHOW_IMPLICIT);
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> {


            dialogInterface.dismiss();
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateCompletionBar() {
        repackProgressBarView.setProgress(repackedItems, pendingItems, totalItems);

        String totalItemsLabel = "Total: " + totalItems;
        String loadedItemsLabel;

        if (repackedItems == totalItems) {
            loadedItemsLabel = " ✅ Completed";
        } else {
            loadedItemsLabel = "Repacked: " + repackedItems;
        }

        repackProgressBarView.setLabels(totalItemsLabel, loadedItemsLabel);

        repackProgressBarView.setColorWhenFull(repackedItems == totalItems);
    }

    private int getTotalsPackages(List<DockReceipt> dockReceipts) {
        int totalPackages = 0;

        for (DockReceipt dockReceipt : dockReceipts) {
            totalPackages += dockReceipt.getPackages();

        }
        return totalPackages;
    }

    private String getFormattedTotalCubicFeet(List<DockReceipt> dockReceipts) {
        double totalCubicFeet = 0;

        for (DockReceipt dockReceipt : dockReceipts) {
            totalCubicFeet += dockReceipt.getCubicFeet();
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(totalCubicFeet);
    }

    // endregion HELPERS

    // region CAMERA HELPERS
    private File createImageFile(Context context) throws IOException {

        // TODO: need to add a seq number here
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Get the external storage directory
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) {
            return null;
        }

        // Create the image file
        File imageFile = File.createTempFile(
                imageFileName,  // Prefix
                ".jpg",         // Suffix
                storageDir      // Directory
        );

        // Save a file path for use with ACTION_VIEW intents
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

                Log.i(TAG, "onActivityResult: Image TAKEN" + "RESULT-CODE: " + resultCode);

                Bitmap picSnap;
                Bitmap resizedPicSnap;

                picSnap = BitmapFactory.decodeFile(picFile.getAbsolutePath());

                Log.i(TAG, "onActivityResult: Image TAKEN" + "DENSITY: " + picSnap.getDensity());
                Log.i(TAG, "onActivityResult: Image TAKEN" + "FILE PATH: " + picFile.getAbsolutePath());

                // ORIENTATION. Refactor

                ExifInterface ei = new ExifInterface(picFile.getAbsolutePath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap rotatedBitmap = null;

                // Setting photo orientation.
                switch (orientation) {
                    // 1
                    case ExifInterface.ORIENTATION_NORMAL:
                        rotatedBitmap = picSnap;
                        break;
                    // LANDSCAPE = 6
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(picSnap, 90);
                        break;
                    // LANDSCAPE_PRIMARY = 3
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(picSnap, 180);
                        break;
                    // NATURAL = 8
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(picSnap, 270);
                        break;
                }

                picSnap = rotatedBitmap;

                // Scale the image. Refactor
                resizedPicSnap = scaleToFitWidth(picSnap, 250);

                // Compress the original picture into 'stream'. Refactor
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                picSnap.compress(Bitmap.CompressFormat.JPEG, 45, stream);

                // Create a new file name for the resized bitmap for sending it to the database.Refactor
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                // TODO: EDIT TEXT
                //  EditText editTextDR = (EditText) findViewById(R.id.editTextDR);
                String randomFileName = generateRandomFilename(10);

                imgFileName = "DR_" + currentDR + "_" + timeStamp + ".jpg";

                // Converting 'stream' to Base64-String
                String base64ImgString = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                // TODO: get actual user code
                int userCode = 4;

                // TODO: Save image to database
//                savePicToDatabase(userCode, imgFileName, base64ImgString, Integer.parseInt(currentDR));

                showPreviewDialog(picSnap, userCode, imgFileName, base64ImgString, Integer.parseInt(currentDR));
                // Remove picture from 'Pictures' directory
//                File picturesDirectory = Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                File delFile = new File(
//                        picturesDirectory,
//                        currentPhotoPath.substring(currentPhotoPath.lastIndexOf("/") + 1));
//                delFile.delete();


            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e(TAG, "onActivityResult: " + ex.getMessage());
        }
    }

    private void showPreviewDialog(Bitmap takenImage, int userCode, String imgFileName, String base64ImgString, int drNumber) {
        // Create a custom dialog or inflate a custom view
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_image_preview);

        // Get the ImageView from the dialog view
        ImageView previewImageView = dialog.findViewById(R.id.previewImageView);
        // Set the takenImage bitmap to the ImageView
        previewImageView.setImageBitmap(takenImage);

        // Get the save and cancel buttons from the dialog view
        ImageButton saveButton = dialog.findViewById(R.id.saveButton);
        ImageButton cancelButton = dialog.findViewById(R.id.cancelButton);

        // Set the click listeners for save and cancel buttons
        saveButton.setOnClickListener(v -> {
            dialog.dismiss();
            // Enable the progress indicator
//            savePicToDatabase(drNumber, imgFileName, base64ImgString);
            TonePlayer tonePlayer = new TonePlayer();

//            progressBar.setVisibility(View.VISIBLE);
            saveImgDialog.show();
            SavePicToDatabase savePicToDatabaseTask = new SavePicToDatabase(drNumber, imgFileName,base64ImgString, cameraService, tonePlayer, requireContext());
            savePicToDatabaseTask.execute();
            removeFromStorage();

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // Handle the cancel action, such as showing a toast or taking another picture
            }
        });

        dialog.show();
    }

    //    private void savePicToDatabase(int userCode, String imgFileName, String base64ImgString, int drNumber) {
    private void savePicToDatabase(int drNumber, String fileName, String base64ImgString) {
//        TastyToasty.green(requireContext(), "will save to db", R.drawable.ic_check_ok).show();
        LoggedInUser user = getLoggedInUser();


        Log.d(TAG, "savePicToDatabase: drNumber=" + drNumber +
                        ", fileName=" + fileName +
//                ", base64ImgString=" + base64ImgString +
                        ", user=" + user.getUserId() +
                        ", name=" + user.getDisplayName()
        );
        GeneralResponse<String> response = cameraService.saveImgToDatabase(drNumber, fileName, base64ImgString, user);
        Log.e(TAG, "savePicToDatabase: " + response.isSuccess() + " " + response.getMessage());
        Context context = requireContext().getApplicationContext();

        if (response.isSuccess()) {
            TastyToasty.green(context, response.getMessage() + "!!", R.drawable.ic_check_ok).show();
        } else {
            TastyToasty.red(context, response.getMessage() + "!!", R.drawable.ic_error).show();
        }

//        dialog.dismiss();
    }

    public class SavePicToDatabase extends AsyncTask<Void, Void, GeneralResponse> {
        private int drNumber;
        private String fileName;
        private String base64ImgString;
        private CameraService cameraService;
        private TonePlayer tonePlayer;
        private Context context;

        private Dialog saveDialog;

        public SavePicToDatabase(int drNumber, String fileName, String base64ImgString, CameraService cameraService, TonePlayer tonePlayer, Context context) {
            this.drNumber = drNumber;
            this.fileName = fileName;
            this.base64ImgString = base64ImgString;
            this.cameraService = cameraService;
            this.tonePlayer = tonePlayer;
            this.context = context;
        }

        @Override
        protected GeneralResponse doInBackground(Void... voids) {
//            TastyToasty.green(requireContext(), "will save to db", R.drawable.ic_check_ok).show();
            LoggedInUser user = getLoggedInUser();


            Log.d(TAG, "savePicToDatabase: drNumber=" + drNumber +
                            ", fileName=" + fileName +
//                ", base64ImgString=" + base64ImgString +
                            ", user=" + user.getUserId() +
                            ", name=" + user.getDisplayName()
            );
            GeneralResponse<String> response = cameraService.saveImgToDatabase(drNumber, fileName, base64ImgString, user);
            Log.e(TAG, "savePicToDatabase: " + response.isSuccess() + " " + response.getMessage());
            Context context = requireContext().getApplicationContext();



            return response;
        }

        @Override
        protected void onPostExecute(GeneralResponse response) {


            if (response.isSuccess()) {
                TastyToasty.green(context, response.getMessage() + "!!", R.drawable.ic_check_ok).show();
            } else {
                TastyToasty.red(context, response.getMessage() + "!!", R.drawable.ic_error).show();
            }
         saveImgDialog.dismiss();
        }

    }


    @NonNull
    private LoggedInUser getLoggedInUser() {
        preferenceService = PreferenceService.getInstance(requireContext(), PreferenceKeys.credentials);
        String user_code = preferenceService.getString("id", "");
        String user_name = preferenceService.getString("username", "");
        Log.i(TAG, "Credentials: " + user_code + " | " + user_name);
        return new LoggedInUser(user_code, user_name);
    }

    private void removeFromStorage() {
        File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File delFile = new File(picturesDirectory, currentPhotoPath.substring(currentPhotoPath.lastIndexOf("/") + 1));
        if (delFile.exists()) {
            boolean deleted = delFile.delete();

            if (deleted) {
                Log.d("FileDeletion", "File deleted successfully: " + delFile.getAbsolutePath());
            } else {
                Log.e("FileDeletion", "Failed to delete file: " + delFile.getAbsolutePath());
            }
        } else {
            Log.w("FileDeletion", "File not found: " + delFile.getAbsolutePath());
        }

    }

    private Dialog showCustomProgressDialog() {
        Dialog dialog = new Dialog(getContext());
        Log.e(TAG, "showCustomProgressDialog:");
        dialog.setContentView(R.layout.save_image_progress);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }
//    private Dialog showCustomProgressDialog() {
//        Dialog dialog = new Dialog(requireContext());
//        Log.e(TAG, "showCustomProgressDialog:" );
//        dialog.setContentView(R.layout.save_image_progress);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//        return dialog;
//    }

    // endregion CAMERA HELPERS

    // region TASKS
    public class ScanContainerTask extends AsyncTask<Void, Void, List<DockReceipt>> {

        private int drRepackContainer;
        private NewRepackService repackService;
        private TonePlayer tonePlayer;
        private Context context;

        public ScanContainerTask(Context context, int drRepackContainer) {
            this.context = context;
            this.drRepackContainer = drRepackContainer;
//            this.selectedRepackType = selectedRepackType;
            this.repackService = new NewRepackService();
            this.tonePlayer = new TonePlayer();
        }

        @Override
        protected List<DockReceipt> doInBackground(Void... voids) {
            List<DockReceipt> retrievedDockReceipts = new ArrayList<>();

            try {
                if (drRepackContainer == 0) {
                    return retrievedDockReceipts;
                }
                retrievedDockReceipts = repackService.getDockReceiptsRepacked(drRepackContainer);
            } catch (Exception ex) {
                Log.e("ScanContainerTask", "Error while scanning container: " + ex.getMessage());
            }

            return retrievedDockReceipts;

        }

        @Override
        protected void onPostExecute(List<DockReceipt> retrievedDockReceipts) {
            progressBar.setVisibility(View.GONE);
            recyclerViewDocReceipts.setVisibility(View.VISIBLE);

            if (retrievedDockReceipts.isEmpty()) {
                tonePlayer.playErrorSound(100);
                TastyToasty.orange(context, "Please enter a valid DR RePack number", R.drawable.ic_error).show();
                return;
            }
            // Call the service methods to get dock receipts assigned, repacked, and remaining
            List<DockReceipt> assignedToContainer = repackService.getDockReceiptsAssignedToContainer(drRepackContainer);
            List<DockReceipt> repackedDockReceipts = repackService.getDockReceiptsRepacked(drRepackContainer);
            List<DockReceipt> remainingDockReceipts = repackService.getDockReceiptsPending(drRepackContainer);
            totalItems = assignedToContainer.size();
            repackedItems = repackedDockReceipts.size();
            updateCompletionBar();

            tonePlayer.playSuccessSound(100);

            dockReceipts.clear();
            dockReceipts.addAll(retrievedDockReceipts);
//            etRepackedCount.setVisibility(View.VISIBLE);
//            etRepackedCount.setText(String.valueOf(retrievedDockReceipts.size()));
            chipRepackCount.setText(String.valueOf(retrievedDockReceipts.size()));

            dockReceiptsAdapter.notifyDataSetChanged();

            totalPackages = getTotalsPackages(retrievedDockReceipts);
            totalCubicFeet = getFormattedTotalCubicFeet(retrievedDockReceipts);

            tvTotalPkgs.setText(String.valueOf(totalItems));
            tvRepackedPkgs.setText(String.valueOf(retrievedDockReceipts.size()));
            //            chipTotalPackages.setText(String.valueOf(totalPackages));
            chipTotalCubicFeet.setText(String.valueOf(totalCubicFeet));
        }
    }

    private class UpdateItemsTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... containerIds) {
            int containerId = containerIds[0];

            List<DockReceipt> updatedDockReceipts;

            updatedDockReceipts = repackService.getDockReceiptsRepacked(containerId);
            repackedItems = updatedDockReceipts.size();

            updatedDockReceipts = repackService.getDockReceiptsPending(containerId);
            pendingItems = updatedDockReceipts.size();


            updatedDockReceipts = repackService.getDockReceiptsAssignedToContainer(containerId);
            totalItems = updatedDockReceipts.size();


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            updateCompletionBar();
            // Update UI elements
        }
    }

    // endregion TASKS

}