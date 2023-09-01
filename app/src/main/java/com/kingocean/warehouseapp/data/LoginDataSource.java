package com.kingocean.warehouseapp.data;

import android.os.StrictMode;

import com.kingocean.warehouseapp.data.model.LoggedInUser;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        LoggedInUser fakeUser;
        fakeUser = null;

        String NAMESPACE = "http://KO.com/webservices/";
        String URL = "https://kingocean.azurewebsites.net/MSL_WS.asmx";
        String SOAP_ACTION = "http://KO.com/webservices/User_Sign_IN";
        String METHOD_NAME = "User_Sign_IN";
        int TimeOut = 30000;

        String response;

        //Initialize soap request + add parameters
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo pi=new PropertyInfo();
        pi.setName("strConnect");
        pi.setValue("Data Source=kingocean.database.windows.net;Initial Catalog=SCSolution;User ID=kingadmin;Password=!King@cean*;Connection Timeout=600");
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("UserName");
        pi.setValue(username);
        pi.setType(String.class);
        request.addProperty(pi);

        pi=new PropertyInfo();
        pi.setName("psw");
        pi.setValue(password);
        pi.setType(String.class);
        request.addProperty(pi);

        //Declare the version of the SOAP request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject) envelope.bodyIn;

            ////String str = ((SoapObject)result.getProperty(0)).getPropertyAsString(1);

            SoapObject root = (SoapObject) result.getProperty(0);
            SoapObject s_diffgram = (SoapObject) root.getProperty("diffgram");
            SoapObject s_NewDataSet = (SoapObject) s_diffgram.getProperty("NewDataSet");
            //SoapObject s_Table = (SoapObject) s_NewDataSet.getProperty("Table");

            String UserCode = null;
            //String Activated = null;
            //String Administrator = null;

            for (int i = 0; i < s_NewDataSet.getPropertyCount(); i++)
            {
                Object property = s_NewDataSet.getProperty(i);
                if (property instanceof SoapObject)
                {
                    SoapObject category_list = (SoapObject) property;
                    UserCode = category_list.getProperty("UserCode").toString();
                    //Activated = category_list.getProperty("Activated").toString();
                    //Administrator = category_list.getProperty("Administrator").toString();
                }
            }

            if (UserCode != null) {
                fakeUser = new LoggedInUser(UserCode,  username + " " + username);
                return new Result.Success<>(fakeUser);
            } else {
                return new Result.Error(null);
            }

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }

    }

    public void logout() {
        // TODO: revoke authentication
    }

}

/*try {
    // TODO: handle loggedInUser authentication

    String name = "net.sourceforge.jtds.jdbc.Driver";

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);

    @SuppressLint("AuthLeak") String url = "jdbc:jtds:sqlserver://kingocean.database.windows.net:1433;DatabaseName=SCSolution;user=kingadmin@kingocean;password=!King@cean*;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=3000;";

    Connection conn = null;
    PreparedStatement pst = null;
    Statement stmt = null;
    ResultSet rs = null;

    String SQL = "SELECT uid, UserName FROM dbo.Users WHERE UserName = '" + username + "' and Password = '" + password + "'";
    Class.forName(name);
    conn = DriverManager.getConnection(url);

    stmt = conn.createStatement();
    rs = stmt.executeQuery(SQL);

    LoggedInUser fakeUser;
    fakeUser = null;

    if (rs.next()) {
        fakeUser = new LoggedInUser(rs.getString("uid"),  rs.getString("UserName") + " " + rs.getString("UserName"));   ////java.util.UUID.randomUUID().toString()
        conn.close();
        stmt.close();
        return new Result.Success<>(fakeUser);
    } else {
        conn.close();
        stmt.close();
        return new Result.Error(null);
    }

    //

} catch (Exception e) {
    return new Result.Error(new IOException("Error logging in", e));
}*/