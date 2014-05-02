package mei.ricardo.pessoa.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.codehaus.jackson.map.ext.JodaDeserializers;

import mei.ricardo.pessoa.app.Navigation.MainActivity;

/**
 * Created by rpessoa on 29/04/14.
 */
public class Application extends android.app.Application {
    private static String TAG = Application.class.getName();
    public static final String preferenceOfApplication = "preferenceFileCouchDB";
    private static final String usernamedb = "usernameDB";
    public static boolean isLogged=false;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Start Application ");

        String db = loadInSharePreferenceDataOfApplication(this);

        if(db!=null){
            Log.d(TAG,"Need load data from db "+db);
            isLogged = true;
        }else{
            Log.d(TAG,"Need login");
            isLogged =false;
        }
    }

    public static void saveInSharePreferenceDataOfApplication(Context mContext, String username){
        /*Write in share preference*/
        SharedPreferences sharedPref = mContext.getSharedPreferences(preferenceOfApplication, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(usernamedb, username);
        editor.commit();
    }

    public static String loadInSharePreferenceDataOfApplication(Context mContext){
        SharedPreferences sharedPref = mContext.getSharedPreferences(preferenceOfApplication, MODE_PRIVATE);
        //int defaultValue = getResources().getInteger(R.string.saved_high_score_default);
        String db = sharedPref.getString(usernamedb,null);
        return db;
    }


}
