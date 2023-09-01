package com.kingocean.warehouseapp.data.model;

public class DockReceipt {
    private String drNumber;

    private String pkgType;
    private int drSeq;
    private int packages;
    private double weight;
    private double cubicFeet;

    private double unitHeight;
    private double unitLength;
    private double unitWidth;

    private int unit;

    public DockReceipt(
            String drNumber,
            String pkgType,
            int drSeq,
            double unit_height,
            double unit_length,
            double unit_width,
            int unit,
            int packages,
            double cubicFeet,
            double weight

    ) {
        this.drNumber = drNumber;
        this.pkgType = pkgType;
        this.drSeq = drSeq;
        this.unitHeight = unit_height;
        this.unitLength = unit_length;
        this.unitWidth = unit_width;
        this.unit = unit;

        this.packages = packages;
        this.cubicFeet = cubicFeet;
        this.weight = weight;

    }


    public String getDrNumber() {
        return drNumber;
    }

    public String getPkgType() {
        return pkgType;
    }


    public int getUnit() {
        return unit;
    }

    public int getPackages() {
        return packages;
    }

    public int getDrSeq(){
        return drSeq;
    }

    public double getWeight() {
        return weight;
    }

    public double getCubicFeet() {
        return cubicFeet;
    }

    public double getUnitHeight() {
        return unitHeight;
    }

    public void setUnitHeight(double unitHeight) {
        this.unitHeight = unitHeight;
    }

    public double getUnitLength() {
        return unitLength;
    }

    public void setUnitLength(double unitLength) {
        this.unitLength = unitLength;
    }

    public double getUnitWidth() {
        return unitWidth;
    }

    public void setUnitWidth(double unitWidth) {
        this.unitWidth = unitWidth;
    }
}