package com.kingocean.warehouseapp.data.model;

public class GeneralResponse<T> {
    private boolean success;
    private String message;
    private String error;

    private String action;
    private T data;
    private String containerType;

    //    public GeneralResponse(boolean success, String message, T data) {
//        this(success, message, null, data);
//    }
    public GeneralResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.error = error;
    }
    public GeneralResponse(boolean success, String message,  String action) {
        this.success = success;
        this.message = message;
        this.action = action;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public String getAction(){return action;}

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }
}
