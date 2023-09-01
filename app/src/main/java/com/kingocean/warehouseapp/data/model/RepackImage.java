package com.kingocean.warehouseapp.data.model;

import java.util.Date;

public class RepackImage {
    private int id;
    private String filename;
    private Date created_on;
    private byte[] img;
    private int dr_number;

    public RepackImage() {
    }

    public RepackImage(int id, String filename, Date created_on, byte[] img, int dr_number) {
        this.id = id;
        this.filename = filename;
        this.created_on = created_on;
        this.img = img;
        this.dr_number = dr_number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public int getDr_number() {
        return dr_number;
    }

    public void setDr_number(int dr_number) {
        this.dr_number = dr_number;
    }

    // Optional: Override toString() method for debugging or display purposes
    @Override
    public String toString() {
        return "RepackImage{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", created_on=" + created_on +
                ", img=" + img +
                ", dr_number=" + dr_number +
                '}';
    }
}