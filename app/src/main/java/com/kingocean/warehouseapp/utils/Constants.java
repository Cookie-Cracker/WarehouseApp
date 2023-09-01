package com.kingocean.warehouseapp.utils;

public class Constants {
    public static String NAMESPACE = "http://KO.com/webservices/";
    public static String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
//    public static String SOAP_ACTION = "http://KO.com/webservices/User_Sign_IN";
//    public  static String METHOD_NAME = "User_Sign_IN";
    public static int TIMEOUT = 30000;

    // region IDLE
    public static long IDLE_TIMEOUT = 60 * 1000 * 15; // 15 minute in milliseconds
    public static long NOTIFICATION_INTERVAL = 60 * 1000 * 1; // 5 seconds in milliseconds
    // endregion IDLE

    public static String CONNECTION_STRING = "Data Source=kingocean.database.windows.net;Initial Catalog=SCSolution;User ID=kingadmin;Password=!King@cean*;Connection Timeout=600";
}

