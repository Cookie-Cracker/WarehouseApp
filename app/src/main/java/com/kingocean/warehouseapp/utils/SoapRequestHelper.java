package com.kingocean.warehouseapp.utils;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public class SoapRequestHelper {
    private SoapObject request;

    public SoapRequestHelper(String namespace, String methodName) {
        request = new SoapObject(namespace, methodName);
    }




    public SoapRequestHelper addProperty(String name, String value, Class<?> type) {
        PropertyInfo pi = new PropertyInfo();
        pi.setName(name);
        pi.setValue(value);
        pi.setType(type);
        request.addProperty(pi);
        return this;
    }

    public SoapObject build() {
        return request;
    }


}
