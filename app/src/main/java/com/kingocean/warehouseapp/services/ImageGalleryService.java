package com.kingocean.warehouseapp.services;

import static com.kingocean.warehouseapp.utils.ImageUtils.isJPEG;

import android.util.Base64;
import android.util.Log;

import com.kingocean.warehouseapp.data.model.DRImage;
import com.kingocean.warehouseapp.data.model.RepackImage;
import com.kingocean.warehouseapp.data.model.WarehouseImage;
import com.kingocean.warehouseapp.utils.Constants;
import com.kingocean.warehouseapp.utils.SoapRequestHelper;
import com.kingocean.warehouseapp.utils.ThreadUtils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImageGalleryService extends BaseService {

    public List<WarehouseImage> fetchDRImages(int drNumber) {
        Log.i("IMG", "getRepackImages: for" + drNumber);
        List<WarehouseImage> warehouseImages = new ArrayList<>();
        ThreadUtils.setThreadPolicyPermitAll();
        String METHOD_NAME = "SP_1_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request;
        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aadroid_get_dr_images", String.class)
                .addProperty("Param_name", "dr_number", String.class)
                .addProperty("Param", String.valueOf(drNumber), android.R.string.class);
        request = requestBuilder.build();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Check if the response is a SoapFault
            if (envelope.bodyIn instanceof SoapFault) {
                SoapFault fault = (SoapFault) envelope.bodyIn;
                Log.e("SOAP", "Fault code: " + fault.faultcode);
                Log.e("SOAP", "Fault string: " + fault.faultstring);
                Log.e("SOAP", "Fault actor: " + fault.faultactor);
            } else {
                // Get the SoapResult from the envelope body.
                SoapObject soapResult = getSoapResult(envelope);

                if (soapResult != null) {
                    for (int i = 0; i < soapResult.getPropertyCount(); i++) {
                        SoapObject response = (SoapObject) soapResult.getProperty(i);

                        DRImage drImage = new DRImage();
                        drImage.setId(Integer.parseInt(response.getPropertyAsString("uid")));
                        drImage.setFileName(response.getPropertyAsString("FileName"));
                        drImage.setDrWarehouse(response.getPropertyAsString("DR_Warehouse"));
                        drImage.setDrNumber(response.getPropertyAsString("DR_number"));
//                        drImage.setDocType(response.getPropertyAsString("Doc_Type"));

                        // Parse the created_on date string to a Date object using a date format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date createdOn = dateFormat.parse(response.getPropertyAsString("Created_On"));
                        drImage.setCreatedOnDate(createdOn);

                        // Parse the img byte array from Base64 string
                        String imgBase64 = response.getPropertyAsString("Doc_Img");
                        byte[] imgBytes = Base64.decode(imgBase64, Base64.DEFAULT);

                        drImage.setImgByte(imgBytes);

//                        if (isJPEG(imgBytes)) {
//                            drImage.setImgByte(imgBytes);
//                            drImage.setValidImage(true);
//                        } else {
//                            Log.e("ERROR", "Invalid JPEG image");
//                            drImage.setValidImage(false);
//                        }

//
                        drImage.setDrNumber(response.getPropertyAsString("DR_number"));

                        warehouseImages.add(drImage);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ERRORS", e.toString());
            e.printStackTrace();
        }

        return warehouseImages;

    }
    public List<WarehouseImage> fetchDRImagesByUnits(int drNumber, int drUnit) {
        Log.i("IMG", "getRepackImages: for" + drNumber);
        List<WarehouseImage> warehouseImages = new ArrayList<>();
        ThreadUtils.setThreadPolicyPermitAll();
        String METHOD_NAME = "SP_2_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request;
        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aadroid_get_dr_images_by_unit", String.class)
                .addProperty("Param1_name", "dr_number", String.class)
                .addProperty("Param2_name", "dr_unit", String.class)
                .addProperty("Param1", String.valueOf(drNumber), android.R.string.class)
                .addProperty("Param2", String.valueOf(drUnit), android.R.string.class);

        request = requestBuilder.build();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Check if the response is a SoapFault
            if (envelope.bodyIn instanceof SoapFault) {
                SoapFault fault = (SoapFault) envelope.bodyIn;
                Log.e("SOAP", "Fault code: " + fault.faultcode);
                Log.e("SOAP", "Fault string: " + fault.faultstring);
                Log.e("SOAP", "Fault actor: " + fault.faultactor);
            } else {
                // Get the SoapResult from the envelope body.
                SoapObject soapResult = getSoapResult(envelope);

                if (soapResult != null) {
                    for (int i = 0; i < soapResult.getPropertyCount(); i++) {
                        SoapObject response = (SoapObject) soapResult.getProperty(i);

                        DRImage drImage = new DRImage();
                        drImage.setId(Integer.parseInt(response.getPropertyAsString("uid")));
                        drImage.setFileName(response.getPropertyAsString("FileName"));
                        drImage.setDrWarehouse(response.getPropertyAsString("DR_Warehouse"));
                        drImage.setDrNumber(response.getPropertyAsString("DR_number"));
                        drImage.setCreatedBy(response.getPropertyAsString("Created_By"));
//                        drImage.setDocType(response.getPropertyAsString("Doc_Type"));

                        // Parse the created_on date string to a Date object using a date format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date createdOn = dateFormat.parse(response.getPropertyAsString("Created_On"));
                        drImage.setCreatedOnDate(createdOn);

                        // Parse the img byte array from Base64 string
                        String imgBase64 = response.getPropertyAsString("Doc_Img");
                        byte[] imgBytes = Base64.decode(imgBase64, Base64.DEFAULT);

                        drImage.setImgByte(imgBytes);

                        // Calculate image size in MB
                        double imageSizeMB = (double) imgBytes.length / (1024.0 * 1024.0);

                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        double roundedImageSize = Math.round(imageSizeMB * 100.0) / 100.0;
// Set the image size in MB in your DRImage object
                        drImage.setImageSize(roundedImageSize);



//                        if (isJPEG(imgBytes)) {
//                            drImage.setImgByte(imgBytes);
//                            drImage.setValidImage(true);
//                        } else {
//                            Log.e("ERROR", "Invalid JPEG image");
//                            drImage.setValidImage(false);
//                        }

//
                        drImage.setDrNumber(response.getPropertyAsString("DR_number"));

                        warehouseImages.add(drImage);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ERRORS", e.toString());
            e.printStackTrace();
        }

        Log.i("COUNT", "fetchDRImagesByUnits: " + warehouseImages.size());
        return warehouseImages;

    }
}
