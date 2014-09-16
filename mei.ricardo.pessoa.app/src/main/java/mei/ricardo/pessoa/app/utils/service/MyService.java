package mei.ricardo.pessoa.app.utils.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.CouchDB;
import mei.ricardo.pessoa.app.couchdb.modal.Settings;
import mei.ricardo.pessoa.app.ui.MainActivity;

/**
 * Created by rpessoa on 16-09-2014.
 * http://www.vogella.com/tutorials/AndroidServices/article.html
 * http://techblogon.com/simple-android-service-example-code-description-start-stop-service/
 */
public class MyService extends Service {
    private static String TAG = MyService.class.getCanonicalName();
    private static final int NOTIFICATION_ID = 12743;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {

        public void run() {
            notifiesTheUser();
            handler.postDelayed(this, 3000);
        }
    };

    private void notifiesTheUser() {
        Log.d(TAG,"notifies User ");
        Toast.makeText(this,"",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");

        Notification notification = new Notification(R.drawable.ic_launcher, "Service GPS status", System.currentTimeMillis());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);

        notification.setLatestEventInfo(this, getString(R.string.str_service_on_running_title),  getString(R.string.str_service_on_running_description), contentIntent);

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         /*Service is restarted if it gets terminated. Intent data passed to the onStartCommand method is null.
         Used for services which manages their own state and do not depend on the Intent data.*/
        runnable.run();

        Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        return Service.START_NOT_STICKY;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        runnable.run();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
