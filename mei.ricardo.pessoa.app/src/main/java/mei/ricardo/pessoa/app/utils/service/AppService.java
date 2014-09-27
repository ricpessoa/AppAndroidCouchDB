package mei.ricardo.pessoa.app.utils.service;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import mei.ricardo.pessoa.app.couchdb.CouchDB;
import mei.ricardo.pessoa.app.couchdb.modal.Device;

/**
 * Created by rpessoa on 19-09-2014.
 */
public class AppService extends Service {
    private static String TAG = AppService.class.getCanonicalName();
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        AppService getService() {
            return AppService.this;
        }
    }

    @Override
    public void onCreate() {
        CouchDB.getmCouchDBinstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service Monitoring on Background - start");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        // Tell the user we stopped.
        Log.i(TAG, "Service Monitoring on Background - stop");
        Toast.makeText(this, "PAROU", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();
}
