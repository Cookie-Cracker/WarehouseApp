package com.kingocean.warehouseapp.fragments;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.data.adapters.ImageGalleryAdapter;
import com.kingocean.warehouseapp.data.model.WarehouseImage;
import com.kingocean.warehouseapp.services.ImageGalleryService;

import java.util.List;


public class WarehouseImagesFragment extends Fragment {

    private int drNumber;
    private int drUnit;
    private RecyclerView recyclerViewImageGallery;
    private ImageGalleryAdapter imageGalleryAdapter;
    private ImageGalleryService imageGalleryService;
    private ProgressBar progressBar;
    private List<WarehouseImage> warehouseImages;
    Chip chipImageCount;
    private Button btnToggle;
    private boolean isGridLayout;

    private TextView tvDrNumber;
    private TextView tvUnitNumber;

    public WarehouseImagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_warehouse_images, container, false);

        imageGalleryService = new ImageGalleryService();

        tvDrNumber = view.findViewById(R.id.tvDrNumberImageGallery);
        tvUnitNumber = view.findViewById(R.id.tvdrUnitImageGallery);

        if (getArguments() != null) {
            drNumber = getArguments().getInt("drNumber");
            drUnit = getArguments().getInt("drUnit");
        }
        tvDrNumber.setText(String.valueOf(drNumber));
        tvUnitNumber.setText(String.valueOf(drUnit));

        recyclerViewImageGallery = view.findViewById(R.id.recyclerImageGallery);

        int spanCount = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), spanCount);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewImageGallery.setLayoutManager(gridLayoutManager);
        recyclerViewImageGallery.setVisibility(View.GONE);

        btnToggle = view.findViewById(R.id.btnTogleImageView);

        chipImageCount = view.findViewById(R.id.chipGalleryImageCount);
        chipImageCount.setText("");

        progressBar = view.findViewById(R.id.progressBarGalley);
        progressBar.setVisibility(View.VISIBLE);

        FetchGalleryImagesTask fetchGalleryImagesTask = new FetchGalleryImagesTask();
        fetchGalleryImagesTask.execute();




        return view;
    }

    private class FetchGalleryImagesTask extends AsyncTask<Void, Void, List<WarehouseImage>> {
        @Override
        protected List<WarehouseImage> doInBackground(Void... voids) {
            return imageGalleryService.fetchDRImagesByUnits(drNumber, drUnit);
        }

        @Override
        protected void onPostExecute(List<WarehouseImage> warehouseImagesResult) {


            warehouseImages = warehouseImagesResult;
//            imageGalleryAdapter = new ImageGalleryAdapter(warehouseImages, isGridLayout, new ImageGalleryAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(Bitmap imageBitmap) {
//                    // Open FullScreenImageFragment and pass the decoded Bitmap
//                    FragmentManager fragmentManager = ((FragmentActivity) requireContext()).getSupportFragmentManager();
//                    FullScreenImageFragment fullScreenImageFragment = FullScreenImageFragment.newInstance(imageBitmap);
//                    fullScreenImageFragment.show(fragmentManager, FullScreenImageFragment.TAG);
//                }
//            });
            imageGalleryAdapter = new ImageGalleryAdapter(warehouseImages, isGridLayout, new ImageGalleryAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Bitmap imageBitmap) {
                    // Open FullScreenImageFragment and pass the decoded Bitmap
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FullScreenImageFragment fullScreenImageFragment = FullScreenImageFragment.newInstance(imageBitmap);

                    fragmentManager.beginTransaction()
                            .replace(android.R.id.content, fullScreenImageFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });
            progressBar.setVisibility(View.GONE);
            recyclerViewImageGallery.setVisibility(View.VISIBLE);
            Log.i("COUNT", "onPostExecute: image count" + warehouseImages.size());
            chipImageCount.setText(String.valueOf(warehouseImages.size()));
            recyclerViewImageGallery.setAdapter(imageGalleryAdapter);

        }
    }
}