package com.kingocean.warehouseapp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.kingocean.warehouseapp.R;

/**
 * TODO: document your custom view class.
 */
public class RepackProgressBarView extends View {
    private int totalItems;
    private int repackedItems;

    private int pendingItems;
    private boolean colorWhenFull = false;

    private Paint backgroundColorPaint;
    private Paint indicatorColorPaint;
    private Paint textPaint;

    private String totalItemsLabel;
    private String loadedItemsLabel;

    private Drawable backgroundDrawable;

    public RepackProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // Load the background drawable
        backgroundDrawable = getResources().getDrawable(R.drawable.rounded_background);
    }

    private void init() {
        backgroundColorPaint = new Paint();
        backgroundColorPaint.setColor(ContextCompat.getColor(getContext(), R.color.gray_400));


//        backgroundColorPaint.setColor(ContextCompat.getColor(getContext(), R.color.progressBackgroundColor));

        indicatorColorPaint = new Paint();
        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.progressTextColor));
        textPaint.setTextSize(28); // Adjust text size as needed
    }

    public void setProgress(int loadedItems, int pendingItems, int totalItems) {
        this.repackedItems = loadedItems;
        this.pendingItems = pendingItems;
        this.totalItems = totalItems;
        invalidate(); // Redraw the view
    }

    public void setLabels(String totalItemsLabel, String loadedItemsLabel) {
        this.totalItemsLabel = totalItemsLabel;
        this.loadedItemsLabel = loadedItemsLabel;
        invalidate(); // Redraw the view
    }

    public void setColorWhenFull(boolean colorWhenFull) {
        this.colorWhenFull = colorWhenFull;
        invalidate(); // Redraw the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        backgroundDrawable.setBounds(0, 0, getWidth(), getHeight());
        backgroundDrawable.draw(canvas);
        // Draw background bar
//        canvas.drawRect(0, 0, width, height, backgroundColorPaint);

        // Calculate progress width
        int progressWidth = (int) ((repackedItems / (float) totalItems) * width);

        // Draw progress indicator
        if (colorWhenFull && repackedItems == totalItems) {
            indicatorColorPaint.setColor(ContextCompat.getColor(getContext(), R.color.progressFullColor));
        } else {
            indicatorColorPaint.setColor(ContextCompat.getColor(getContext(), R.color.md_orange_800));
        }
        canvas.drawRect(0, 0, progressWidth, height, indicatorColorPaint);

        // Draw total and loaded items labels
        if (totalItemsLabel != null && loadedItemsLabel != null) {
            int textMargin = 16; // Adjust as needed
            int textHeight = 40; // Adjust as needed

            // Draw loaded items label
            canvas.drawText(loadedItemsLabel, textMargin, textHeight, textPaint);

            // Draw total items label
            float loadedLabelWidth = textPaint.measureText(totalItemsLabel);
            canvas.drawText(totalItemsLabel, getWidth() - textMargin - loadedLabelWidth, textHeight, textPaint);
        }
    }

}



