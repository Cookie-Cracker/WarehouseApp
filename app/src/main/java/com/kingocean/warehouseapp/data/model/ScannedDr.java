package com.kingocean.warehouseapp.data.model;

import androidx.annotation.NonNull;

public class ScannedDr {
    private String drNumber;
    private String unitSequence;

    private String rawScan;

    public ScannedDr(String drNumber, String unitSequence) {
        this.drNumber = drNumber;
        this.unitSequence = unitSequence;
    }

    public String getDrNumber() {
        return drNumber;
    }

    public String getUnitSequence() {
        return unitSequence;
    }

    @NonNull
    @Override
    public String toString() {
        return "ScannedDr{" +
                "drNumber='" + drNumber + '\'' +
                ", unitSequence='" + unitSequence + '\'' +
                '}';
    }
}