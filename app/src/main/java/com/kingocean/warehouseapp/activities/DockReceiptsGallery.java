package com.kingocean.warehouseapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.fragments.WarehouseImagesFragment;

public class DockReceiptsGallery extends WarehouseBaseActivity {
    private static final String TAG = "DockReceiptsGallery";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dock_receipts_gallery);
        int drNumber = getIntent().getIntExtra("drNumber", 0);
        int drUnit = getIntent().getIntExtra("drUnit", 0);


        if (savedInstanceState == null){
            WarehouseImagesFragment imagesFragment = new WarehouseImagesFragment();
            Bundle args = new Bundle();
            args.putInt("drNumber", drNumber);
            args.putInt("drUnit", drUnit);
            imagesFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerImageFragment, imagesFragment)
                    .commit();
        }
    }
}