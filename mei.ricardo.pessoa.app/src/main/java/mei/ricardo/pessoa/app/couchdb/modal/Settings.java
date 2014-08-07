package mei.ricardo.pessoa.app.couchdb.modal;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.Document;

import mei.ricardo.pessoa.app.couchdb.CouchDB;

/**
 * Created by rpessoa on 07/08/14.
 */
public class Settings {
    private static String TAG = Settings.class.getCanonicalName();
    private boolean monitoring;
    private boolean sounds;

    public Settings(boolean monitoring, boolean sounds) {
        this.monitoring = monitoring;
        this.sounds = sounds;
    }

    public static Settings getSettingsOfApp(Context mContext) {
        Document document = CouchDB.getmCouchDBinstance().getDatabase().getExistingDocument("appSettings");
        try {
            Boolean moni = Boolean.parseBoolean(document.getProperty("monitoring").toString());
            Boolean sound = Boolean.parseBoolean(document.getProperty("sound_notification").toString());
            Settings settings = new Settings(moni, sound);
            return settings;
        } catch (Exception ex) {
            Log.e(TAG, "Error trying get setting of app");
            //TODO: create document settings for first time
        }
        return null;
    }

}
