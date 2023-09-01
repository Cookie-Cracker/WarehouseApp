package com.kingocean.warehouseapp.services;

import android.util.Log;

import com.kingocean.warehouseapp.utils.Constants;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class BaseService {
    protected String URL = Constants.URL;
    protected String NAMESPACE = Constants.NAMESPACE;

    protected SoapObject getSoapResult(SoapSerializationEnvelope envelope) {
        try {
            SoapObject result = (SoapObject) envelope.bodyIn;
            SoapObject root = (SoapObject) result.getProperty(0);
            SoapObject s_diffgram = (SoapObject) root.getProperty("diffgram");
            return (SoapObject) s_diffgram.getProperty("NewDataSet");
        } catch (Exception e) {
            Log.e("ERRORS", e.toString());
            e.printStackTrace();
        }

        return null;
    }
}
