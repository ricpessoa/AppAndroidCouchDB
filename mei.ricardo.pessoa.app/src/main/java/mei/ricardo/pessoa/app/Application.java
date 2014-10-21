package mei.ricardo.pessoa.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.couchbase.lite.support.CouchbaseLiteApplication;

import java.util.ArrayList;
import java.util.List;

import mei.ricardo.pessoa.app.couchdb.CouchDB;
import mei.ricardo.pessoa.app.couchdb.modal.MS_Notification;
import mei.ricardo.pessoa.app.couchdb.modal.Settings;

/**
 * Created by rpessoa on 29/04/14.
 */
public class Application extends CouchbaseLiteApplication {
    private static String TAG = Application.class.getName();

    private static Context mContext;

    public static final String filenameSharePreference = "preferenceFileCouchDB";
    private static final String varSharePreference = "usernameDB";
    private static String dbname;
    private static String currentGPSStatus; //public enum Status {CHECK_IN, CHECK_OUT}
    private static String currentBatteryStatus;

    public static boolean isLogged = false;

    public static String hostUrl = "http://195.23.102.92:8080"; //Centi server url
    public static String couchDBHostUrl = "http://195.23.102.92" + ":5984";
    public static String serviceLoginUrl = "/PhpProjectCouchDB/applogin";
    public static String serviceRegisterUrl = "/PhpProjectCouchDB/appregister";
    public static String serviceAddAddNewDevice = "/PhpProjectCouchDB/appAddNewDevice";
    public static String serviceAddMonitoringDeviceUrl = "/PhpProjectCouchDB/devicepost";

    private static ArrayList<MS_Notification> ms_notificationArrayList = new ArrayList<MS_Notification>();


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        if (getDbname() != null) {
            Log.d(TAG, "user session = " + getDbname());
            isLogged = true;
        } else {
            Log.d(TAG, "No user session need login");
            isLogged = false;
        }
    }

    /*Write in share preference*/
    public static void saveInSharePreferenceDataOfApplication(String username) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(filenameSharePreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(varSharePreference, username);
        editor.commit();
    }

    /*Load in share preference*/
    public static String loadInSharePreferenceDataOfApplication() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(filenameSharePreference, MODE_PRIVATE);
        String db = sharedPref.getString(varSharePreference, null);
        return db;
    }

    public static Context getmContext() {
        return mContext;
    }

    public static String getDbname() {
        if (dbname == null) {
            dbname = loadInSharePreferenceDataOfApplication();
        }
        return dbname;
    }

    public static void setDbname(String dbname) {
        Application.dbname = dbname;
    }

    /**
     * this method create a singleton instance of couchDB
     * - object CouchDBManager
     * - object dataBase
     */
    public static CouchDB getmCouchDBinstance() {
        return CouchDB.getmCouchDBinstance();
    }

    public static Settings getmSettingsinstance() {
        return Settings.getmSettingsinstance();
    }

    public static void addNotificationToShowToUser(List<MS_Notification> arrayListMonitoring) {
        Application app = (Application) mContext;
        app.ms_notificationArrayList.addAll(arrayListMonitoring);

        if (app.ms_notificationArrayList.size() > 5) {
            do {
                app.ms_notificationArrayList.remove(0);
            } while (app.ms_notificationArrayList.size() < 5);
        }
    }

    public static ArrayList<MS_Notification> getMs_notificationArrayList() {
        return ms_notificationArrayList;
    }

    public static String getCurrentGPSStatus() {
        return currentGPSStatus;
    }

    public static String getCurrentBatteryStatus() {
        return currentBatteryStatus;
    }

    public static void setCurrentGPSStatus(String currentGPSStatus) {
        Application.currentGPSStatus = currentGPSStatus;
    }

    public static void setCurrentBatteryStatus(String currentBatteryStatus) {
        Application.currentBatteryStatus = currentBatteryStatus;
    }
}
