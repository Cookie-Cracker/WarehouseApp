package com.kingocean.warehouseapp.services;

import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import com.kingocean.warehouseapp.data.model.DockReceipt;
import com.kingocean.warehouseapp.data.model.GeneralResponse;
import com.kingocean.warehouseapp.data.model.RepackImage;
import com.kingocean.warehouseapp.data.model.RepackType;
import com.kingocean.warehouseapp.utils.Constants;
import com.kingocean.warehouseapp.utils.SoapRequestHelper;
import com.kingocean.warehouseapp.utils.TastyToasty;
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
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RepackService {
    public static String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
    public static  String NAMESPACE = "http://KO.com/webservices/";

    public SoapObject getSoapResult(SoapSerializationEnvelope envelope) {
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

    public List<DockReceipt> getDockReceiptsRepacked(int drRepack) {
        List<DockReceipt> dockReceipts = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String METHOD_NAME = "SP_1_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        // Initialize soap request + add parameters
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_android_get_repacked_new", String.class) // Modify to match the stored procedure name for verification
                .addProperty("Param_name", "dr_container", String.class)
                .addProperty("Param", String.valueOf(drRepack), String.class);

        request = requestBuilder.build();


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            // This is the actual part that will call the web service
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Check if the response is a SoapFault
            if (envelope.bodyIn instanceof SoapFault) {
                SoapFault fault = (SoapFault) envelope.bodyIn;
                Log.e("SOAP", fault.toString());
                return dockReceipts; // Return an empty list
            }

            // Get the SoapResult from the envelope body.
            SoapObject soapResult = getSoapResult(envelope);

//            SoapObject result = (SoapObject) envelope.bodyIn;
//
//            SoapObject root = (SoapObject) result.getProperty(0);
//            SoapObject s_diffgram = (SoapObject) root.getProperty("diffgram");
//            SoapObject s_NewDataSet = (SoapObject) s_diffgram.getProperty("NewDataSet");

            if (soapResult != null) {

                for (int i = 0; i < soapResult.getPropertyCount(); i++) {
                    SoapObject dockReceiptData = (SoapObject) soapResult.getProperty(i);
                    Log.i("INFO", "getDockReceiptsRepacked: " + i);
                    // Extract the values for each property of the DockReceipt
                    String drNumber = dockReceiptData.getPropertyAsString("DR_NUMBER");
                    String pkgType;
                    try {
                        pkgType = dockReceiptData.getPropertyAsString("PACKAGES");
                        if (pkgType == null || pkgType.isEmpty()) {
                            pkgType = "NA";
                        }
                    } catch (Exception e) {
                        pkgType = "NA";
                        Log.e("ERRORS", "Error retrieving TYPE_OF_PKGS property: " + e);
                        e.printStackTrace();
                    }
                    double unitHeight = Double.parseDouble(dockReceiptData.getPropertyAsString("UNIT_HEIGHT"));
                    double unitLength = Double.parseDouble(dockReceiptData.getPropertyAsString("UNIT_LENGTH"));
                    double unitWidth = Double.parseDouble(dockReceiptData.getPropertyAsString("UNIT_WIDTH"));
                    int unit = Integer.parseInt(dockReceiptData.getPropertyAsString("DR_SEQ"));


                    int packages = Integer.parseInt(dockReceiptData.getPropertyAsString("PACKAGES"));
                    double cubicFeet = Double.parseDouble(dockReceiptData.getPropertyAsString("MEASURE"));
                    double weight = Double.parseDouble(dockReceiptData.getPropertyAsString("WEIGHT"));

                    // Create a new DockReceipt object with the extracted values
                    DockReceipt dockReceipt =
                            new DockReceipt(
                                    drNumber,
                                    pkgType,
                                    1,
                                    unitHeight,
                                    unitLength,
                                    unitWidth,
                                    unit,
                                    packages,
                                    cubicFeet,
                                    weight
                            );

                    // Add the DockReceipt object to the list
                    dockReceipts.add(dockReceipt);
                }
            }
        } catch (Exception e) {
            Log.e("ERRORS", e.toString());
            e.printStackTrace();
        }

        Log.i("INFO","TOTAL" +  String.valueOf(dockReceipts.size()));
        return dockReceipts;
    }

    public GeneralResponse<String> isScannedDRAvailableAsContainer(int drContainer) {
        boolean success = false;
        String msg = "UNKNOWN ERROR";

        ThreadUtils.setThreadPolicyPermitAll();
        String METHOD_NAME = "SP_1_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Log.e("INFO", "Enter Registered");
        // Initialize soap request + add parameters
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_android_scan_dr_available_as_container", String.class) // Modify to match the stored procedure name for verification
                .addProperty("Param_name", "dr_container", String.class)
                .addProperty("Param", String.valueOf(drContainer), String.class);

        request = requestBuilder.build();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            // This is the actual part that will call the web service
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Check if the response is a SoapFault
            if (envelope.bodyIn instanceof SoapFault) {
                SoapFault fault = (SoapFault) envelope.bodyIn;
                Log.e("SOAP", fault.toString());

            }

//            // Get the SoapResult from the envelope body.
//            SoapObject result = (SoapObject) envelope.bodyIn;
//
//            SoapObject root = (SoapObject) result.getProperty(0);
//            SoapObject s_diffgram = (SoapObject) root.getProperty("diffgram");
//            SoapObject s_NewDataSet = (SoapObject) s_diffgram.getProperty("NewDataSet");
            SoapObject soapResult = getSoapResult(envelope);
            if (soapResult!=null){

            SoapObject dataSetProperty = (SoapObject) soapResult.getProperty(0);
            success = Boolean.parseBoolean(dataSetProperty.getPropertyAsString("success"));
            msg = dataSetProperty.getPropertyAsString("msg");


            // Log the message
            Log.i("INFO", "Response: success=" + success + ", msg=" + msg);
}


            // Return the success value



        }
        catch(UnknownHostException e){
            Log.e("ERRORS", "Can't connect to the server.");
            msg = e.toString();
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.e("ERRORS", e.toString());
            msg = e.toString();
            e.printStackTrace();
        }


        GeneralResponse<String> response = new GeneralResponse<>(success, msg);
        return response;

    }

    public List<DockReceipt> scanDRContainer(int drRepackContainer, int containerType) {
        List<DockReceipt> dockReceipts = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String METHOD_NAME = "SP_2_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        // Initialize soap request + add parameters
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_android_scan_container", String.class)
                .addProperty("Param1_name", "dr_container", String.class)
                .addProperty("Param2_name", "repack_id", String.class)
                .addProperty("Param1", String.valueOf(drRepackContainer), String.class)
                .addProperty("Param2", String.valueOf(containerType), String.class);

        request = requestBuilder.build();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;
        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            // This is the actual part that will call the web service
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Check if the response is a SoapFault
            if (envelope.bodyIn instanceof SoapFault) {
                SoapFault fault = (SoapFault) envelope.bodyIn;
                Log.e("SOAP", fault.toString());
                return dockReceipts; // Return an empty list
            }


            SoapObject soapResult = getSoapResult(envelope);

//            // Get the SoapResult from the envelope body.
//            SoapObject result = (SoapObject) envelope.bodyIn;
//
//            SoapObject root = (SoapObject) result.getProperty(0);
//            SoapObject s_diffgram = (SoapObject) root.getProperty("diffgram");
//            SoapObject s_NewDataSet = (SoapObject) s_diffgram.getProperty("NewDataSet");

            if (soapResult != null) {

                for (int i = 0; i < soapResult.getPropertyCount(); i++) {
                    SoapObject dockReceiptData = (SoapObject) soapResult.getProperty(i);

                    // Extract the values for each property of the DockReceipt
                    String drNumber = dockReceiptData.getPropertyAsString("DR_NUMBER");
                    String pkgType;
                    try {
                        pkgType = dockReceiptData.getPropertyAsString("PACKAGES");
//                        pkgType = dockReceiptData.getPropertyAsString("PACKAGES");
                        if (pkgType == null || pkgType.isEmpty()) {
                            pkgType = "NA";
                        }
                    } catch (Exception e) {
                        pkgType = "NA";
                        Log.e("ERRORS", "Error retrieving TYPE_OF_PKGS property: " + e);
                        e.printStackTrace();
                    }

                    double unitHeight = Double.parseDouble(dockReceiptData.getPropertyAsString("UNIT_HEIGHT"));
                    double unitLength = Double.parseDouble(dockReceiptData.getPropertyAsString("UNIT_LENGTH"));
                    double unitWidth = Double.parseDouble(dockReceiptData.getPropertyAsString("UNIT_WIDTH"));
                    int unit = Integer.parseInt(dockReceiptData.getPropertyAsString("DR_SEQ"));


                    int packages = Integer.parseInt(dockReceiptData.getPropertyAsString("PACKAGES"));
                    double cubicFeet = Double.parseDouble(dockReceiptData.getPropertyAsString("MEASURE"));
                    double weight = Double.parseDouble(dockReceiptData.getPropertyAsString("WEIGHT"));

                    // Create a new DockReceipt object with the extracted values
                    DockReceipt dockReceipt =
                            new DockReceipt(
                                    drNumber,
                                    pkgType,
                                    1,
                                    unitHeight,
                                    unitLength,
                                    unitWidth,
                                    unit,
                                    packages,
                                    cubicFeet,
                                    weight
                            );

                    // Add the DockReceipt object to the list
                    dockReceipts.add(dockReceipt);
                }
            }
        }
           catch (Exception e) {
            Log.e("ERRORS",  e.toString());
            e.printStackTrace();
        }

        Log.i("INFO", String.valueOf(dockReceipts.size()));
        return dockReceipts;


    }

    public GeneralResponse<String> repackOrDeRepack(int drRepackContainer, int drToRepack, int userCode) {

        boolean success = false;
        String msg = "UNKNOWN ERROR";
        String action = "";
        ThreadUtils.setThreadPolicyPermitAll();

        String METHOD_NAME = "SP_3_Param_3_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request;

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_android_repack_depack", String.class)
                .addProperty("Param1_name", "dr_container", String.class)
                .addProperty("Param2_name", "dr_to_repack", String.class)
                .addProperty("Param3_name", "user_code", String.class)
                .addProperty("Param1", String.valueOf(drRepackContainer), String.class)
                .addProperty("Param2", String.valueOf(drToRepack), String.class)
                .addProperty("Param3", String.valueOf(userCode), String.class);

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

//            SoapObject result = (SoapObject) envelope.bodyIn;
//
//            SoapObject root = (SoapObject) result.getProperty(0);
//            SoapObject s_diffgram = (SoapObject) root.getProperty("diffgram");
//            SoapObject s_NewDataSet = (SoapObject) s_diffgram.getProperty("NewDataSet");

            if (soapResult != null) {

                for (int i = 0; i < soapResult.getPropertyCount(); i++) {
                    SoapObject response = (SoapObject) soapResult.getProperty(i);
                    success = Boolean.parseBoolean(response.getPropertyAsString("success"));
                    msg = response.getPropertyAsString("msg");
                    action = response.getPropertyAsString("action");
                }
            }


        } catch (Exception e) {
            Log.e("ERRORS", e.toString());
            e.printStackTrace();
        }

        GeneralResponse<String> response = new GeneralResponse<>(success, msg, action);
        Log.e("ACTION", action);

        return response;
    }

    public ArrayList<RepackType> getRepackTypes() {
        ArrayList<RepackType> repackTypes = new ArrayList<>();

        ThreadUtils.setThreadPolicyPermitAll();


        String METHOD_NAME = "SP";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        SoapObject request;

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_get_repack_types", String.class);

        request = requestBuilder.build();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
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

            SoapObject soapResult = getSoapResult(envelope);

//            // Get the SoapResult from the envelope body.
//            SoapObject result = (SoapObject) envelope.bodyIn;
//
//            SoapObject root = (SoapObject) result.getProperty(0);
//            SoapObject s_diffgram = (SoapObject) root.getProperty("diffgram");
//            SoapObject s_NewDataSet = (SoapObject) s_diffgram.getProperty("NewDataSet");

            if (soapResult != null) {

                for (int i = 0; i < soapResult.getPropertyCount(); i++) {
                    SoapObject dockReceiptData = (SoapObject) soapResult.getProperty(i);
                    int containerTypeId = Integer.parseInt(dockReceiptData.getPropertyAsString("id"));
                    String repack_typeName = dockReceiptData.getPropertyAsString("repack_type");
                    RepackType repackType =
                            new RepackType(containerTypeId, repack_typeName);

                    repackTypes.add(repackType);


                }
            }


        }


        catch (Exception e) {
            Log.e("ERRORS", e.toString());
            e.printStackTrace();
        }

        Log.i("INFO", String.valueOf(repackTypes.size()));

        return repackTypes;
    }

    public int getRepackContainerType(int drContainer) {
        int container_type_id = 0;
        boolean success = false;
        String msg = "UNKNOWN ERROR";
        ThreadUtils.setThreadPolicyPermitAll();

        String METHOD_NAME = "SP_1_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request;
        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_android_get_repack_type", String.class)
                .addProperty("Param_name", "dr_container", String.class)
                .addProperty("Param", String.valueOf(drContainer), String.class);

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

            SoapObject soapResult = getSoapResult(envelope);

//            // Get the SoapResult from the envelope body.
//            SoapObject result = (SoapObject) envelope.bodyIn;
//
//            SoapObject root = (SoapObject) result.getProperty(0);
//            SoapObject s_diffgram = (SoapObject) root.getProperty("diffgram");
//            SoapObject s_NewDataSet = (SoapObject) s_diffgram.getProperty("NewDataSet");

            if (soapResult != null) {

                if (soapResult.getPropertyCount() > 0) {

                    SoapObject response = (SoapObject) soapResult.getProperty(0);
                    container_type_id = Integer.parseInt(response.getPropertyAsString("repack_type_id"));

                }
            }


        } catch (Exception e) {
            Log.e("ERRORS", e.toString());
            e.printStackTrace();
        }

        return container_type_id;
    }

    public GeneralResponse<String> saveImgToDatabase(int userCode, String fileName,  String base64Img, int drNumber){
        boolean success = false;
        String msg = "UNKNOWN ERROR";
        String action = "";
        ThreadUtils.setThreadPolicyPermitAll();

        String METHOD_NAME = "SP_4_Param_2_int_2_string";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request;

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_android_insert_repack_image", String.class)
                .addProperty("Param1_name", "dr_number", String.class)
                .addProperty("Param2_name", "bogus", String.class)
                .addProperty("Param3_name", "filename", String.class)
                .addProperty("Param4_name", "img", String.class)
                .addProperty("Param1", String.valueOf(drNumber), String.class)
                .addProperty("Param2", String.valueOf(1), String.class)
                .addProperty("Param3", String.valueOf(fileName), String.class)
                .addProperty("Param4", String.valueOf(base64Img), String.class);

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
        List<RepackImage> repackImages = new ArrayList<>();
        ThreadUtils.setThreadPolicyPermitAll();
        String METHOD_NAME = "SP_1_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        request.addProperty("strConnect", "Data Source=kingocean.database.windows.net;Initial Catalog=SCSolution;User ID=kingadmin;Password=!King@cean*;Connection Timeout=600");
        request.addProperty("ST_Name", "sp_android_get_repack_images");
        request.addProperty("Param_name", "dr_number");
        request.addProperty("Param", drNumber);

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
                        repackImage.setId(Integer.parseInt(response.getPropertyAsString("id")));
                        repackImage.setFilename(response.getPropertyAsString("filename"));

                        // Parse the created_on date string to a Date object using a date format
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date createdOn = dateFormat.parse(response.getPropertyAsString("created_on"));
                        repackImage.setCreated_on(createdOn);

                        // Parse the img byte array from Base64 string
                        String imgBase64 = response.getPropertyAsString("img");
                        byte[] imgBytes = Base64.decode(imgBase64, Base64.DEFAULT);
                        repackImage.setImg(imgBytes);

                        repackImage.setDr_number(Integer.parseInt(response.getPropertyAsString("dr_number")));

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
