package com.kingocean.warehouseapp.data.model;

import java.util.Date;

public class DRImage extends WarehouseImage {
    private String drWarehouse;
    private String drNumber;
    private String docType;

    public DRImage(){
    super();
    }
    public DRImage(int id, String fileName, String createdBy, Date createdOnDate, byte[] imgByte, long imageSize, boolean isDeleted, boolean isValidImage,
                   String drWarehouse, String drNumber, String docType) {
        super(id, fileName, createdBy, createdOnDate, imgByte, imageSize,isDeleted, isValidImage );
        this.drWarehouse = drWarehouse;
        this.drNumber = drNumber;
        this.docType = docType;
    }

    public String getDrWarehouse() {
        return drWarehouse;
    }

    public void setDrWarehouse(String drWarehouse) {
        this.drWarehouse = drWarehouse;
    }

    public String getDrNumber() {
        return drNumber;
    }

    public void setDrNumber(String drNumber) {
        this.drNumber = drNumber;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }
}





