package com.kingocean.warehouseapp.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.data.adapters.RepackPagerAdapter;
import com.kingocean.warehouseapp.fragments.RepackDocsFragment;
import com.kingocean.warehouseapp.fragments.RepackScanFragment;
import com.kingocean.warehouseapp.services.PreferenceService;
import com.kingocean.warehouseapp.utils.PreferenceKeys;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RepackPics extends AppCompatActivity implements RepackScanFragment.RepackContainerValueListener {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private RepackPagerAdapter pagerAdapter;
    PreferenceService preferenceService;

    private final String TAG = "RepackPics";
    private int drNumber = -1;

    private final int[] fragmentIcons = {
            R.drawable.baseline_document_scanner_24,
            R.drawable.ic_camera_folder};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repack_pics);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPagerRepack);

        pagerAdapter = new RepackPagerAdapter(getSupportFragmentManager(), getLifecycle(), true);

        pagerAdapter.addFragment(new RepackScanFragment(), "Repack", fragmentIcons[0]);
        pagerAdapter.addFragment(new RepackDocsFragment(), "Pics", fragmentIcons[1]);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (pagerAdapter.getPageIcon(position) != 0) {
                        tab.setIcon(pagerAdapter.getPageIcon(position));
                    } else {
                        tab.setText(pagerAdapter.getPageTitle(position));
                    }
                    if (position == 1) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("dr_number", drNumber);
                        Log.i(TAG, "LISTENER1: " + String.valueOf(drNumber));
                        pagerAdapter.createFragment(position).setArguments(bundle);
                    }
                }
        ).attach();

        setupActionBar();

    }

    private void setupActionBar() {
        Drawable gradient = getResources().getDrawable(R.drawable.action_bar_gradient);
        ActionBar actionBar = getSupportActionBar();
        // Get the app bar instance
        // Enable the back button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(gradient);
        }
    }

    @Override
    public void onRepackContainerValue(String value) {
        try {
            Log.i(TAG, "onRepackContainerValue: " + value);
            // Check if the value is not empty before parsing it as an integer
            if (!TextUtils.isEmpty(value)) {
                drNumber = Integer.parseInt(value);
//            TastyToasty.green(getApplicationContext(), "LISTENER" + String.valueOf(drNumber), R.drawable.ic_cancel).show();
            } else {
                // If the value is empty, set the drNumber to 0 or any default value you desire
                drNumber = 0;
            }

            // Pass the value to RepackDocsFragment
            if (pagerAdapter != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("dr_number", drNumber);
                pagerAdapter.createFragment(1).setArguments(bundle);
            }
        } catch (NumberFormatException e) {
            // Handle the case when the value is not a valid integer (e.g., when it's an empty string)
            e.printStackTrace();
            Log.e("ERROR", "onRepackContainerValue: " + e.getMessage());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);

        MenuItem loggedUserItem = menu.findItem(R.id.logged_user);
        preferenceService = PreferenceService.getInstance(getApplicationContext(), PreferenceKeys.credentials);
        String username = preferenceService.getString("username", "");
        loggedUserItem.setTitle(username);

        return true;
    }

    @Override
    public void onBackPressed() {

//        if (hasValuesToWarn()) {
//            showWarningDialog();
//        } else {

        super.onBackPressed();
//        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
