package mei.ricardo.pessoa.app.utils.service;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.CouchDB;
import mei.ricardo.pessoa.app.couchdb.modal.Settings;
import mei.ricardo.pessoa.app.ui.MainActivity;

/**
 * Created by rpessoa on 19-09-2014.
 */
public class AppService extends Service {
    private static String TAG = AppService.class.getCanonicalName();
    public static final String notifyService = "mei.ricardo.pessoa.app.notifyService";
    public static final String varStringMsgToPassToService = "varStringMsgToPassToService";
    private ServiceBroadcastReceiver serviceBroadcastReceiver;
    public static AppService appService;
    private static MediaPlayer mMediaPlayer;
    private static Dialog dialog;

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
        appService = this;
        serviceBroadcastReceiver = new ServiceBroadcastReceiver();
        registerReceiver(serviceBroadcastReceiver, new IntentFilter(notifyService));
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
        if (serviceBroadcastReceiver != null)
            unregisterReceiver(serviceBroadcastReceiver);
        Log.i(TAG, "Service Monitoring on Background - stop");
        //Toast.makeText(this, "PAROU", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    private class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "I Receive a broadcast to service ", Toast.LENGTH_SHORT).show();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String msg = bundle.getString(varStringMsgToPassToService);
                createDialog(context, msg);
            }
        }
    }


    public static void createDialog(final Context mContext, String strMsg) {
        if (dialog != null && dialog.isShowing())
            return;

        dialog = new Dialog(mContext);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); //http://jhshi.me/2013/11/03/pop-up-alertdialog-in-system-service/
        //set up dialog
        dialog.setContentView(R.layout.dialog_notification);
        dialog.setTitle("Alert Notification");
        dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!

        //set up text
        TextView text = (TextView) dialog.findViewById(R.id.TextViewNotification);
        text.setText(strMsg);

        //set up image view
        ImageView img = (ImageView) dialog.findViewById(R.id.ImageViewNotification);
        img.setImageResource(R.drawable.ic_notification_nice);

        //set up button
        Button buttonDismiss = (Button) dialog.findViewById(R.id.ButtonDismiss);
        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });
        Button buttonOpenApp = (Button) dialog.findViewById(R.id.buttonOpen);

        if (!mContext.getPackageName().equalsIgnoreCase(((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0).topActivity.getPackageName())) {
            buttonOpenApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    dialog.dismiss();
                }
            });
        } else {
            buttonOpenApp.setVisibility(View.GONE);
        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
            }
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
        if (Settings.getmSettingsinstance().isSounds())
            playSound(mContext);

        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();
    }

    private static void playSound(Context mContext) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.soft_alarm);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }
}
