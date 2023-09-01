package com.kingocean.warehouseapp.services;

import static com.kingocean.warehouseapp.utils.Constants.*;
import static com.kingocean.warehouseapp.utils.Constants.NAMESPACE;

import android.os.StrictMode;
import android.util.Log;

import com.kingocean.warehouseapp.data.model.DockReceipt;
import com.kingocean.warehouseapp.data.model.GeneralResponse;
import com.kingocean.warehouseapp.data.model.ScannedDr;
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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class NewRepackService {


    private SoapObject getSoapResult(SoapSerializationEnvelope envelope) {
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

    public List<DockReceipt> getDockReceiptsAssignedToContainer(int containerId) {
        List<DockReceipt> dockReceipts = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String METHOD_NAME = "SP_1_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                //TODO: implement sp for getDockReceiptsRepacked
                .addProperty("strConnect", CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aadroid_get_dr_assigned_to_container", String.class) // Modify to match the stored procedure name for verification
                .addProperty("Param_name", "dr_container", String.class)
                .addProperty("Param", String.valueOf(containerId), String.class);

        SoapObject request = requestBuilder.build();


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


            if (soapResult != null) {

                for (int i = 0; i < soapResult.getPropertyCount(); i++) {
                    SoapObject dockReceiptData = (SoapObject) soapResult.getProperty(i);
                    Log.i("INFO", "getDockReceiptsRepacked: " + i);
                    // Extract the values for each property of the DockReceipt
                    String drNumber = dockReceiptData.getPropertyAsString("DR_NUMBER");
                    String pkgType;
                    try {
                        pkgType = dockReceiptData.getPropertyAsString("TYPE_OF_PKGS");
                        if (pkgType == null || pkgType.isEmpty()) {
                            pkgType = "-";
                        }
                    } catch (Exception e) {
                        pkgType = "NA";
                        Log.e("ERRORS", "Error retrieving TYPE_OF_PKGS property: " + e);
                        e.printStackTrace();
                    }
                    double unitHeight = Double.parseDouble(dockReceiptData.getPropertyAsString("UNIT_HEIGHT"));
                    double unitLength = Double.parseDouble(dockReceiptData.getPropertyAsString("UNIT_LENGTH"));
                    double unitWidth = Double.parseDouble(dockReceiptData.getPropertyAsString("UNIT_WIDTH"));
                    int drSeq = Integer.parseInt(dockReceiptData.getPropertyAsString("DR_SEQ"));


                    int packages = Integer.parseInt(dockReceiptData.getPropertyAsString("PACKAGES"));
                    double cubicFeet = Double.parseDouble(dockReceiptData.getPropertyAsString("MEASURE"));
                    double weight = Double.parseDouble(dockReceiptData.getPropertyAsString("WEIGHT"));

                    // Create a new DockReceipt object with the extracted values
                    DockReceipt dockReceipt =
                            new DockReceipt(
                                    drNumber,
                                    pkgType,
                                    drSeq,
                                    unitHeight,
                                    unitLength,
                                    unitWidth,
                                    8,
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

        Log.i("INFO","TOTAL" + dockReceipts.size());
        return dockReceipts;

    }

    public List<DockReceipt> getDockReceiptsRepacked(int containerId){

        List<DockReceipt> dockReceipts = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String METHOD_NAME = "SP_1_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                //TODO: implement sp for getDockReceiptsRepacked
                .addProperty("strConnect", CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aadroid_get_drs_repacked", String.class) // Modify to match the stored procedure name for verification
                .addProperty("Param_name", "dr_container", String.class)
                .addProperty("Param", String.valueOf(containerId), String.class);

        SoapObject request = requestBuilder.build();


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


            if (soapResult != null) {

                for (int i = 0; i < soapResult.getPropertyCount(); i++) {
                    SoapObject dockReceiptData = (SoapObject) soapResult.getProperty(i);
                    Log.i("INFO", "getDockReceiptsRepacked: " + i);
                    // Extract the values for each property of the DockReceipt
                    String drNumber = dockReceiptData.getPropertyAsString("DR_NUMBER");
                    String pkgType;
                    try {
                        pkgType = dockReceiptData.getPropertyAsString("TYPE_OF_PKGS");
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
                    int drSeq = Integer.parseInt(dockReceiptData.getPropertyAsString("DR_SEQ"));
                    Log.i("SEQ", "getDockReceiptsRepacked: " + drSeq);


                    int packages = Integer.parseInt(dockReceiptData.getPropertyAsString("PACKAGES"));
                    double cubicFeet = Double.parseDouble(dockReceiptData.getPropertyAsString("MEASURE"));
                    double weight = Double.parseDouble(dockReceiptData.getPropertyAsString("WEIGHT"));

                    // Create a new DockReceipt object with the extracted values
                    DockReceipt dockReceipt =
                            new DockReceipt(
                                    drNumber,
                                    pkgType,
                                    drSeq,
                                    unitHeight,
                                    unitLength,
                                    unitWidth,
                                    8,
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

        Log.i("INFO","TOTAL" + dockReceipts.size());
        return dockReceipts;

    }

    public List<DockReceipt> getDockReceiptsPending(int containerId){
        List<DockReceipt> dockReceipts = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String METHOD_NAME = "SP_1_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                //TODO: implement sp for getDockReceiptsRepacked
                .addProperty("strConnect", CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aadroid_get_drs_remaining", String.class) // Modify to match the stored procedure name for verification
                .addProperty("Param_name", "dr_container", String.class)
                .addProperty("Param", String.valueOf(containerId), String.class);

        SoapObject request = requestBuilder.build();


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


            if (soapResult != null) {

                for (int i = 0; i < soapResult.getPropertyCount(); i++) {
                    SoapObject dockReceiptData = (SoapObject) soapResult.getProperty(i);
                    Log.i("INFO", "getDockReceiptsRepacked: " + i);
                    // Extract the values for each property of the DockReceipt
                    String drNumber = dockReceiptData.getPropertyAsString("DR_NUMBER");
                    String pkgType;
                    try {
                        pkgType = dockReceiptData.getPropertyAsString("TYPE_OF_PKGS");
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
                    int drSeq = Integer.parseInt(dockReceiptData.getPropertyAsString("DR_SEQ"));


                    int packages = Integer.parseInt(dockReceiptData.getPropertyAsString("PACKAGES"));
                    double cubicFeet = Double.parseDouble(dockReceiptData.getPropertyAsString("MEASURE"));
                    double weight = Double.parseDouble(dockReceiptData.getPropertyAsString("WEIGHT"));

                    // Create a new DockReceipt object with the extracted values
                    DockReceipt dockReceipt =
                            new DockReceipt(
                                    drNumber,
                                    pkgType,
                                    drSeq,
                                    unitHeight,
                                    unitLength,
                                    unitWidth,
                                    8,
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

        Log.i("INFO","TOTAL" + dockReceipts.size());
        return dockReceipts;

    }

    public GeneralResponse<String> isScannedDrValidContainer(int drContainer) {
        boolean success = false;
        String msg = "UNKNOWN ERROR";
        String containerType = "";


        ThreadUtils.setThreadPolicyPermitAll();
        String METHOD_NAME = "SP_1_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        Log.e("INFO", "Enter Registered");
        // Initialize soap request + add parameters
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aandroid_scanned_label_is_container", String.class) // Modify to match the stored procedure name for verification
                .addProperty("Param_name", "dr_container", String.class)
                .addProperty("Param", String.valueOf(drContainer), String.class);

        request = requestBuilder.build();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            Log.e("INFO", "Enter SOAP TRY ");
            // This is the actual part that will call the web service
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Check if the response is a SoapFault
            if (envelope.bodyIn instanceof SoapFault) {
                SoapFault fault = (SoapFault) envelope.bodyIn;
                Log.e("SOAP", fault.toString());

            }

            SoapObject soapResult = getSoapResult(envelope);
            if (soapResult!=null){

                SoapObject dataSetProperty = (SoapObject) soapResult.getProperty(0);
                success = Boolean.parseBoolean(dataSetProperty.getPropertyAsString("success"));
                msg = dataSetProperty.getPropertyAsString("msg");
                containerType = dataSetProperty.getPropertyAsString("container_type");


                // Log the message
                Log.i("INFO", "Response: success=" + success + ", msg=" + msg + ", container_type=" + containerType); // Update this line
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
        response.setContainerType(containerType);
        return response;

    }

    public GeneralResponse<String> repackOrDeRepack(int drContainer, int drRepack, int userCode){
        boolean success = false;
        String msg = "UNKNOWN ERROR";
        String action = "";
        ThreadUtils.setThreadPolicyPermitAll();

        String METHOD_NAME = "SP_3_Param_3_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request;
        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aadroid_repack_depack", String.class)
                .addProperty("Param1_name", "dr_container", String.class)
                .addProperty("Param2_name", "dr_to_repack", String.class)
                .addProperty("Param3_name", "user_code", String.class)
                .addProperty("Param1", String.valueOf(drContainer), String.class)
                .addProperty("Param2", String.valueOf(drRepack), String.class)
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
    public GeneralResponse<String> repackOrDeRepackWithSeq(int drContainer, ScannedDr scannedDr, int userCode){
        boolean success = false;
        String msg = "UNKNOWN ERROR";
        String action = "";
        ThreadUtils.setThreadPolicyPermitAll();

        String METHOD_NAME = "SP_4_Param_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        SoapObject request;
        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aadroid_repack_depack_seq", String.class)
                .addProperty("Param1_name", "dr_container", String.class)
                .addProperty("Param2_name", "dr_to_repack", String.class)
                .addProperty("Param3_name", "seq", String.class)
                .addProperty("Param4_name", "user_code", String.class)
                .addProperty("Param1", String.valueOf(drContainer), String.class)
                .addProperty("Param2", String.valueOf(scannedDr.getDrNumber()), String.class)
                .addProperty("Param3", String.valueOf(scannedDr.getUnitSequence()), String.class)
                .addProperty("Param4", String.valueOf(userCode), String.class);

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


    public GeneralResponse<String> isDrAlreadyRepacked(int drContainer, int drToTest, int seq){
        boolean success = false;
        String msg = "UNKNOWN ERROR";

        ThreadUtils.setThreadPolicyPermitAll();
        String METHOD_NAME = "SP_3_Param_3_int";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        // Initialize soap request + add parameters
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        SoapRequestHelper requestBuilder = new SoapRequestHelper(NAMESPACE, METHOD_NAME)
                .addProperty("strConnect", Constants.CONNECTION_STRING, String.class)
                .addProperty("ST_Name", "sp_aadroid_is_dr_already_repacked", String.class) // Modify to match the stored procedure name for verification
                .addProperty("Param1_name", "dr_container", String.class)
                .addProperty("Param2_name", "dr_to_repack", String.class)
                .addProperty("Param3_name", "seq", String.class)
                .addProperty("Param1", String.valueOf(drContainer), String.class)
                .addProperty("Param2", String.valueOf(drToTest), String.class)
                .addProperty("Param3", String.valueOf(seq), String.class);


        request = requestBuilder.build();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            Log.e("INFO", "Enter SOAP TRY ");
            // This is the actual part that will call the web service
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Check if the response is a SoapFault
            if (envelope.bodyIn instanceof SoapFault) {
                SoapFault fault = (SoapFault) envelope.bodyIn;
                Log.e("SOAP", fault.toString());

            }

            SoapObject soapResult = getSoapResult(envelope);
            if (soapResult!=null){

                SoapObject dataSetProperty = (SoapObject) soapResult.getProperty(0);
                success = Boolean.parseBoolean(dataSetProperty.getPropertyAsString("success"));
                msg = dataSetProperty.getPropertyAsString("msg");


                // Log the message
                Log.i("INFO", "Response: success=" + success + ", msg=" + msg); // Update this line
            }


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
        Log.i("REPACKED", success + msg);
        return new GeneralResponse<>(success, msg);


    }

}
