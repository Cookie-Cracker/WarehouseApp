package com.kingocean.warehouseapp.data.model;

import java.util.Date;

public class StuffingImage extends WarehouseImage {
    private String typeAttached;

    public StuffingImage(int id, String fileName, String createdBy, Date createdOnDate, byte[] imgByte, long imageSize, boolean isDeleted, boolean isValidImage, String typeAttached) {
        super(id, fileName, createdBy, createdOnDate, imgByte, imageSize, isDeleted, isValidImage);
        this.typeAttached = typeAttached;
    }

    public String getTypeAttached() {
        return typeAttached;
    }
}
