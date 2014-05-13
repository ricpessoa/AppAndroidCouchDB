package mei.ricardo.pessoa.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.couchbase.lite.support.CouchbaseLiteApplication;

import mei.ricardo.pessoa.app.couchdb.CouchDB;

/**
 * Created by rpessoa on 29/04/14.
 */
public class Application extends CouchbaseLiteApplication {
    private static String TAG = Application.class.getName();

    private static Context mContext;

    public static final String filenameSharePreference = "preferenceFileCouchDB";
    private static final String varSharePreference = "usernameDB";
    private static String dbname;
    public static boolean isLogged = false;

    public static String hostUrl = "http://192.168.255.94";
    public static String couchDBHostUrl = "http://192.168.255.94:5984";
    public static String serviceLoginUrl = "/PhpProjectCouchDB/applogin";
    public static String serviceRegisterUrl = "/PhpProjectCouchDB/appregister";

    //public static Database database = null;
    //public static Manager mCouchManager = null;

   // private static CouchDB mCouchDBinstance;

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

    public static Context getmContext(){
        return mContext;
    }

    public static String getDbname(){
        if(dbname==null) {
            dbname = loadInSharePreferenceDataOfApplication();
        }
        return dbname;
    }

    public static CouchDB getmCouchDBinstance(){
        return CouchDB.getmCouchDBinstance();
    }

}
