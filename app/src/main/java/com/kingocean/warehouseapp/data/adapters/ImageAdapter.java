package com.kingocean.warehouseapp.data.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.data.model.RepackImage;

import java.text.SimpleDateFormat;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    // Constants for view types
    private static final int VIEW_TYPE_DEFAULT = 0;
    private static final int VIEW_TYPE_SMALL = 1;

    private List<RepackImage> repackImages;
    private boolean showSmallImages;
    private boolean isGridLayout;

    public ImageAdapter(List<RepackImage> repackImages, boolean isGridLayout) {
        this.repackImages = repackImages;
        this.showSmallImages = false;
        this.isGridLayout = isGridLayout;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = viewType == VIEW_TYPE_DEFAULT ? R.layout.item_image : R.layout.item_image_small;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ImageViewHolder(view);
    }
    public void setRepackImages(List<RepackImage> repackImages) {
        this.repackImages = repackImages;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        RepackImage repackImage = repackImages.get(position);
        byte[] imgBytes = repackImage.getImg();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        holder.imageView.setImageBitmap(bitmap);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String createdOnText = dateFormat.format(repackImage.getCreated_on());
        holder.infoTextView.setText(createdOnText);
    }

    @Override
    public int getItemCount() {
        return repackImages.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        return showSmallImages ? VIEW_TYPE_SMALL : VIEW_TYPE_DEFAULT;
//    }

    public void setShowSmallImages(boolean showSmallImages) {
        this.showSmallImages = showSmallImages;
        notifyDataSetChanged();
    }

    public void setGridLayout(boolean isGridLayout) {
        this.isGridLayout = isGridLayout;
        notifyDataSetChanged();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView infoTextView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            infoTextView = itemView.findViewById(R.id.infoTextView);
        }
    }
}