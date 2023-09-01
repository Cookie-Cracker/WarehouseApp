package com.kingocean.warehouseapp.data.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class RepackPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> fragmentTitles = new ArrayList<>();
    private final List<Integer> fragmentIcons = new ArrayList<>();
    private final boolean useIcons;

    public RepackPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, boolean useIcons) {
        super(fragmentManager, lifecycle);
        this.useIcons = useIcons;
    }

    public void addFragment(Fragment fragment, String title, int iconResId) {
        fragments.add(fragment);
        fragmentTitles.add(title);
        fragmentIcons.add(iconResId);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public CharSequence getPageTitle(int position) {
        if (useIcons) {
            return null; // Return null for icons-only tabs
        } else {
            return fragmentTitles.get(position); // Return title for text-only tabs
        }
    }

    public int getPageIcon(int position) {
        if (useIcons) {
            return fragmentIcons.get(position); // Return icon resource ID for icons-only tabs
        } else {
            return 0; // Return 0 for text-only tabs
        }
    }
}