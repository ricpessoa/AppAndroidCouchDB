package mei.ricardo.pessoa.app.couchdb.modal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;

import org.apache.http.auth.NTUserPrincipal;

import java.util.HashMap;
import java.util.Map;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.couchdb.CouchDB;
import mei.ricardo.pessoa.app.utils.service.AppService;

/**
 * Created by rpessoa on 07/08/14.
 */
public class Settings {
    private static String TAG = Settings.class.getCanonicalName();
    public static String type = "settings";

    private static Settings mSettingsinstance = null;
    private boolean monitoring;
    private boolean sounds;

    /**
     * this constructor is used when app not have settings
     */
    private Settings() {
        this.monitoring = true;
        this.sounds = true;
    }

    private Settings(boolean monitoring, boolean sounds) {
        this.monitoring = monitoring;
        this.sounds = sounds;
    }

    public static Settings getmSettingsinstance() {
        if (mSettingsinstance != null)
            return mSettingsinstance;
        else {
            //for the first time try get from db
            Settings temSetting = getSettingsOfApp();
            if (temSetting == null) {
                //not exist in db so... create standard settings
                mSettingsinstance = new Settings();
                mSettingsinstance.saveInDB();
            }
            mSettingsinstance = temSetting;
            return temSetting;
        }
    }

    private void saveInDB() {
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getDocument("appSettings");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("monitoring", this.monitoring);
        properties.put("sound_notification", this.sounds);
        properties.put("type", "settings");
        properties.put("timestamp", System.currentTimeMillis());
        if (document.getCurrentRevisionId() != null) {
            properties.put("_rev", document.getCurrentRevisionId());//get last rev document
        }
        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Log.e(TAG, "Some error trying save settings");
        }
    }

    public static Settings getSettingsOfApp() {
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getExistingDocument("appSettings");
        try {
            Boolean moni = Boolean.parseBoolean(document.getProperty("monitoring").toString());
            Boolean sound = Boolean.parseBoolean(document.getProperty("sound_notification").toString());
            Settings settings = new Settings(moni, sound);
            return settings;
        } catch (Exception ex) {
            Log.e(TAG, "Error trying get setting of app");
        }
        return null;
    }

    public boolean isMonitoring() {
        return monitoring;
    }

    public void setMonitoring(boolean monitoring) {
        this.monitoring = monitoring;
        if (monitoring == false) {
            Log.d(TAG, "monitoring false");
            if (AppService.appService != null) {
                Log.d(TAG, "stopping service");
                AppService.appService.stopSelf();
            }
        } else {
            Log.d(TAG, "monitoring true = start service");
            Application.getmContext().startService(new Intent(Application.getmContext(), AppService.class)); //TODO: RP verify if service already running
        }
        getmSettingsinstance().saveInDB();
    }

    public boolean isSounds() {
        return sounds;
    }

    public void setSounds(boolean sounds) {
        this.sounds = sounds;
        getmSettingsinstance().saveInDB();
    }

    @Override
    public String toString() {
        return "Settings{" +
                "monitoring=" + monitoring +
                ", sounds=" + sounds +
                '}';
    }
}
