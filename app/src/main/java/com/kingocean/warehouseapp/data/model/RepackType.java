package com.kingocean.warehouseapp.data.model;

public class RepackType {
    private int id;
    private String repackType;

    public RepackType(int id, String repackType) {
        this.id = id;
        this.repackType = repackType;
    }
    public int getId() {
        return id;
    }
    public String getRepackType() {
        return repackType;
    }
    @Override
    public String toString() {
        return repackType;
    }
}
