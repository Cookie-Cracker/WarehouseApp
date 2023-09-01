package com.kingocean.warehouseapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.data.adapters.ImageAdapter;
import com.kingocean.warehouseapp.data.model.RepackImage;
import com.kingocean.warehouseapp.services.CameraService;

import java.util.ArrayList;
import java.util.List;

public class RepackDocsFragment extends Fragment {
    private int drNumber;
    private RecyclerView recyclerViewGallery;
    private ImageAdapter imageAdapter;
//    private RepackService repackService;
    private CameraService repackService;
    private ProgressBar progressBar;
    private Chip chipDockReceipt;
    private Chip chipPicCount;
    private List<RepackImage> repackImages;
    private Button btnToggleLayout;
    private boolean isGridLayout = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repack_docs, container, false);

        progressBar = view.findViewById(R.id.progressBarPics);
        recyclerViewGallery = view.findViewById(R.id.recyclerViewGallery);
        chipDockReceipt = view.findViewById(R.id.chipContainer);
        chipPicCount = view.findViewById(R.id.chipPicCount);

        if (getArguments() != null) {
            drNumber = getArguments().getInt("dr_number");
            chipDockReceipt.setText(String.valueOf(drNumber));
        }

        int spanCount = 1; // Number of columns in the grid
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), spanCount);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerViewGallery.setLayoutManager(layoutManager);

        repackService = new CameraService();

        // Show the loading component initially
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewGallery.setVisibility(View.GONE);

        // Fetch repack images asynchronously using AsyncTask
        FetchRepackImagesTask fetchRepackImagesTask = new FetchRepackImagesTask();
        fetchRepackImagesTask.execute();

        @SuppressLint("UseCompatLoadingForDrawables") Drawable listIcon = getResources().getDrawable(R.drawable.ic_list_view);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable gridIconStart = getResources().getDrawable(R.drawable.ic_grid_view);


        btnToggleLayout = view.findViewById(R.id.btnToggleLayout);

        isGridLayout = true;


//        setGridLayout(1);
        btnToggleLayout.setText("Grid");
        btnToggleLayout.setCompoundDrawablesWithIntrinsicBounds(gridIconStart, null, null, null);


        btnToggleLayout.setOnClickListener(v -> {
            isGridLayout = !isGridLayout;
            int spanCount1 = isGridLayout ? 2 : 1;
            setGridLayout(spanCount1);
            btnToggleLayout.setText(isGridLayout ? "List" : "Grid");
            Drawable gridIcon = getResources().getDrawable(R.drawable.ic_grid_view);
            Drawable leftIcon = isGridLayout ? listIcon : gridIcon;
            btnToggleLayout.setCompoundDrawablesWithIntrinsicBounds(leftIcon, null, null, null);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        if (getArguments() != null) {
            int newDrNumber;
            newDrNumber = getArguments().getInt("dr_number");
            drNumber = newDrNumber;
                chipDockReceipt.setText(String.valueOf(drNumber));
                if (TextUtils.isEmpty(String.valueOf(drNumber))) {
                    // Clear the repackImages list and update the adapter
                    repackImages = new ArrayList<>();
                    imageAdapter.setRepackImages(repackImages);
                    imageAdapter.notifyDataSetChanged();
                    chipPicCount.setText("0");
                } else {
                    // Update the data only if the drNumber is not empty
                    FetchRepackImagesTask fetchRepackImagesTask = new FetchRepackImagesTask();
                    fetchRepackImagesTask.execute();
                }

        }
    }
    private void setGridLayout(int spanCount) {
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), spanCount);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerViewGallery.setLayoutManager(layoutManager);
        imageAdapter.setGridLayout(isGridLayout);
    }

    // AsyncTask to fetch repack images
    private class FetchRepackImagesTask extends AsyncTask<Void, Void, List<RepackImage>> {
        @Override
        protected List<RepackImage> doInBackground(Void... voids) {
            return repackService.getRepackImages(drNumber);
        }

        @Override
        protected void onPostExecute(List<RepackImage> result) {
            repackImages = result;

            // Update the UI with the obtained data
            progressBar.setVisibility(View.GONE);
            recyclerViewGallery.setVisibility(View.VISIBLE);
            chipPicCount.setText(String.valueOf(repackImages.size()));
            imageAdapter = new ImageAdapter(repackImages, isGridLayout);

            recyclerViewGallery.setAdapter(imageAdapter);
        }
    }
}
