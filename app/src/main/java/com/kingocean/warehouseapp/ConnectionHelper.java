package com.kingocean.warehouseapp;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

//public class ConnectionHelper {
//
//    Connection con;
//    String username, password, ip, port, database;
//
//    public Connection connectionClass() {
//
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//
//        Connection connection = null;
//        String ConnectionURL = null;
//
//        try {
//            Class.forName("net.sourceforge.jtds.jdbc.Driver");
//            ConnectionURL = "jdbc:jtds:sqlserver://kingocean.database.windows.net:1433;DatabaseName=SCSolution;user=kingadmin@kingocean;password=!King@cean*;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=3000;";
//            connection = DriverManager.getConnection(ConnectionURL);
//        }
//        catch (Exception ex) {
//            Log.e("Error ", ex.getMessage());
//        }
//
//        return connection;
//
//    }
//
//
//}
