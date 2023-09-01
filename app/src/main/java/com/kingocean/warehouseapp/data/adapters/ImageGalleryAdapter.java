package com.kingocean.warehouseapp.data.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kingocean.warehouseapp.R;
import com.kingocean.warehouseapp.data.model.WarehouseImage;
import com.kingocean.warehouseapp.fragments.FullScreenImageFragment;

import java.text.SimpleDateFormat;
import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageGalleryViewHolder> {

    private static final int VIEW_TYPE_GRID = 0;
    private static final int VIEW_TYPE_LIST = 1;
    private List<WarehouseImage> warehouseImages;
    private boolean showSmallImages;
    private boolean isGridLayout;
    private OnItemClickListener listener;


    public ImageGalleryAdapter(List<WarehouseImage> imageList, boolean isGridLayout, OnItemClickListener listener) {
        this.warehouseImages = imageList;
        this.isGridLayout = isGridLayout;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutResId = viewType == VIEW_TYPE_GRID ? R.layout.warehouse_image_item : R.layout.item_image_small;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ImageGalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageGalleryViewHolder holder, int position) {
        WarehouseImage warehouseImage = warehouseImages.get(position);
//        if (warehouseImage.isValidImage()) {
//
//            byte[] imgBytes = warehouseImage.getImgByte();
//            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
//            holder.imageView.setImageBitmap(bitmap);
//        } else {
//            holder.imageView.setImageResource(R.drawable.repack);
//        }
        byte[] imgBytes = warehouseImage.getImgByte();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        holder.imageView.setImageBitmap(bitmap);



//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Open FullScreenImageFragment and pass the decoded Bitmap
//                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
//                FullScreenImageFragment fullScreenImageFragment = FullScreenImageFragment.newInstance(bitmap);
//                fullScreenImageFragment.show(fragmentManager, FullScreenImageFragment.TAG);
//            }
//        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open FullScreenImageFragment and pass the decoded Bitmap
                FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                FullScreenImageFragment fullScreenImageFragment = FullScreenImageFragment.newInstance(bitmap);

                fragmentManager.beginTransaction()
                        .replace(android.R.id.content, fullScreenImageFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });




        holder.fileNameTextView.setText(warehouseImage.getFileName());
        holder.createdByTextView.setText(warehouseImage.getCreatedBy());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String createdOnText = dateFormat.format(warehouseImage.getCreatedOnDate());
        holder.createdOnTextView.setText(createdOnText);

        holder.imageSizeTextView.setText(String.valueOf(warehouseImage.getImageSize()));
    }

    @Override
    public int getItemCount() {
        return warehouseImages.size();
    }

    public void setWarehouseImages(List<WarehouseImage> warehouseImages) {
        this.warehouseImages = warehouseImages;
        notifyDataSetChanged();
    }

    public void setGridLayout(boolean isGridLayout) {
        this.isGridLayout = isGridLayout;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Bitmap bitmap);
    }

    public  class ImageGalleryViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView fileNameTextView;
        public TextView createdByTextView;
        public TextView createdOnTextView;

public TextView imageSizeTextView;
        public ImageGalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ibWarehouseImage);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
            createdByTextView = itemView.findViewById(R.id.createdByTextView);
            createdOnTextView = itemView.findViewById(R.id.createdOnTextView);
            imageSizeTextView = itemView.findViewById(R.id.imageSizeTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(
                            BitmapFactory.decodeByteArray(
                                    warehouseImages.get(getAdapterPosition()).getImgByte(),
                                    0,
                                    warehouseImages.get(getAdapterPosition()).getImgByte().length));
                }
            });
        }
    }
}
