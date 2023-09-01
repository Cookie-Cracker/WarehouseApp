package com.kingocean.warehouseapp.services;

import android.util.Base64;
import android.util.Log;

import com.kingocean.warehouseapp.data.model.GeneralResponse;
import com.kingocean.warehouseapp.data.model.LoggedInUser;
import com.kingocean.warehouseapp.data.model.RepackImage;
import com.kingocean.warehouseapp.utils.Constants;
import com.kingocean.warehouseapp.utils.SoapRequestHelper;
import com.kingocean.warehouseapp.utils.ThreadUtils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CameraService extends BaseService {

    public GeneralResponse<String> saveImgToDatabase(int drNumber, String fileName, String base64Img, LoggedInUser user) {
        boolean success = false;
        String msg = "UNKNOWN ERROR";
        String action = "";
        ThreadUtils.setThreadPolicyPermitAll();

        String METHOD_NAME = "SP_5_Param_2_int_3_String";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request;

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aadroid_add_repack_image", String.class)
                .addProperty("Param1_name", "dr_number", String.class)
                .addProperty("Param2_name", "user_code", String.class)
                .addProperty("Param3_name", "file_name", String.class)
                .addProperty("Param4_name", "img", String.class)
                .addProperty("Param5_name", "user_name", String.class)
                .addProperty("Param1", String.valueOf(drNumber), String.class)
                .addProperty("Param2", String.valueOf(user.getUserId()), String.class)
                .addProperty("Param3", String.valueOf(fileName), String.class)
                .addProperty("Param4", String.valueOf(base64Img), String.class)
                .addProperty("Param5", String.valueOf(user.getDisplayName()), String.class);

        request = requestBuilder.build();
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            // This is the actual part that will call the web service
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Check if the response is a SoapFault
            if (envelope.bodyIn instanceof SoapFault) {
                SoapFault fault = (SoapFault) envelope.bodyIn;
                Log.e("SOAP", "Fault code: " + fault.faultcode);
                Log.e("SOAP", "Fault string: " + fault.faultstring);
                Log.e("SOAP", "Fault actor: " + fault.faultactor);

                // Retrieve fault detail as a string
                String faultDetail = "";
                if (fault.detail != null) {
                    Node detailNode = fault.detail;
                    StringWriter stringWriter = new StringWriter();
                    try {
                        XmlSerializer serializer = XmlPullParserFactory.newInstance().newSerializer();
                        serializer.setOutput(stringWriter);
                        detailNode.write(serializer);
                        serializer.flush();
                        faultDetail = stringWriter.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.e("SOAP", "Fault detail: " + faultDetail);

            }

            // Get the SoapResult from the envelope body.
            SoapObject soapResult = getSoapResult(envelope);


            if (soapResult != null) {

                for (int i = 0; i < soapResult.getPropertyCount(); i++) {
                    SoapObject response = (SoapObject) soapResult.getProperty(i);
                    success = Boolean.parseBoolean(response.getPropertyAsString("success"));
                    msg = response.getPropertyAsString("msg");
                }
            }


        } catch (Exception e) {
            Log.e("ERRORS", e.toString());
            e.printStackTrace();
        }

        GeneralResponse<String> response = new GeneralResponse<>(success, msg);

        return response;

    }

    public List<RepackImage> getRepackImages(int drNumber) {
        Log.i("IMG", "getRepackImages: for" + drNumber);
        List<RepackImage> repackImages = new ArrayList<>();
        ThreadUtils.setThreadPolicyPermitAll();
        String METHOD_NAME = "SP_1_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request;
        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aadroid_get_repack_images", String.class)
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

                        // Map SOAP response properties to RepackImage attributes
                        RepackImage repackImage = new RepackImage();
                        repackImage.setId(Integer.parseInt(response.getPropertyAsString("uid")));
                        repackImage.setFilename(response.getPropertyAsString("FileName"));

                        // Parse the created_on date string to a Date object using a date format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date createdOn = dateFormat.parse(response.getPropertyAsString("Created_On"));
                        repackImage.setCreated_on(createdOn);

                        // Parse the img byte array from Base64 string
                        String imgBase64 = response.getPropertyAsString("Doc_Img");
                        byte[] imgBytes = Base64.decode(imgBase64, Base64.DEFAULT);
                        repackImage.setImg(imgBytes);

                        repackImage.setDr_number(Integer.parseInt(response.getPropertyAsString("DR_number")));

                        repackImages.add(repackImage);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("ERRORS", e.toString());
            e.printStackTrace();
        }

        return repackImages;
    }

}
