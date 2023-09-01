package com.kingocean.warehouseapp.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.kingocean.warehouseapp.R;


public class FullScreenImageFragment extends Fragment {
    public static final String TAG = "FullScreenImageFragment";
    private static final String ARG_IMAGE_URL_OR_RESOURCE = "image_url_or_resource";

    public FullScreenImageFragment() {
        // Required empty public constructor
    }


    public static FullScreenImageFragment newInstance(Bitmap imageBitmap) {
        FullScreenImageFragment fragment = new FullScreenImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE_URL_OR_RESOURCE, imageBitmap);
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);

        PhotoView photoView = view.findViewById(R.id.photo_view);
        Bitmap imageBitmap = getArguments().getParcelable(ARG_IMAGE_URL_OR_RESOURCE);
        if (imageBitmap !=null){
            photoView.setImageBitmap(imageBitmap);
        }

        return view;
    }
}