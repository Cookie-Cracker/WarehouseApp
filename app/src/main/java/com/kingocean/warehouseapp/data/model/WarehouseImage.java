package com.kingocean.warehouseapp.data.model;

import java.util.Date;

public class WarehouseImage {
    private int id;
    private String fileName;
    private String createdBy;
    private Date createdOnDate;
    private byte[] imgByte;

    private double imageSize;
    private boolean isDeleted;

    private boolean isValidImage;
    public WarehouseImage(){

    }

    public WarehouseImage(int id, String fileName, String createdBy, Date createdOnDate, byte[] imgByte,long imageSize, boolean isDeleted, boolean isValid) {
        this.id = id;
        this.fileName = fileName;
        this.createdBy = createdBy;
        this.createdOnDate = createdOnDate;
        this.imgByte = imgByte;
        this.imageSize = imageSize;
        this.isDeleted = isDeleted;
        this.isValidImage = isValid;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    public byte[] getImgByte() {
        return imgByte;
    }

    public void setImgByte(byte[] imgByte) {
        this.imgByte = imgByte;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isValidImage() {
        return isValidImage;
    }

    public void setValidImage(boolean validImage) {
        isValidImage = validImage;
    }

    public double getImageSize() {
        return imageSize;
    }

    public void setImageSize(double imageSize) {
        this.imageSize = imageSize;
    }
}
