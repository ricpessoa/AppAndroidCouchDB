package mei.ricardo.pessoa.app.couchdb.modal;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Battery;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_GPS;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_PanicButton;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Shoe;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Temperature;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.ui.MainActivity;
import mei.ricardo.pessoa.app.utils.service.AppService;

/**
 * Created by rpessoa on 27/07/14.
 */
public class MS_Notification {
    private MS_PanicButton panicButtonNotification;
    private MS_GPS gpsNotification;
    private MS_Battery batteryNotification;
    private MS_Temperature temperatureNotification;
    private boolean panicButtonPressed;
    private MS_Shoe shoeNotification;
    private boolean shoeRemoved;

    public MS_Notification() {
    }

    public MS_PanicButton getPanicButtonNotification() {
        return panicButtonNotification;
    }

    public void setPanicButtonNotification(MS_PanicButton panicButtonNotification) {
        this.panicButtonNotification = panicButtonNotification;
        this.panicButtonPressed = true;
    }

    public void setShoeNotification(MS_Shoe shoeNotification) {
        this.shoeNotification = shoeNotification;
        this.shoeRemoved = true;
    }

    public MS_Shoe getShoeNotification() {
        return shoeNotification;
    }

    public MS_GPS getGpsNotification() {
        return gpsNotification;
    }

    public void setGpsNotification(MS_GPS gpsNotification) {
        this.gpsNotification = gpsNotification;
    }

    public MS_Battery getBatteryNotification() {
        return batteryNotification;
    }

    public void setBatteryNotification(MS_Battery batteryNotification) {
        this.batteryNotification = batteryNotification;
    }

    public MS_Temperature getTemperatureNotification() {
        return temperatureNotification;
    }

    public void setTemperatureNotification(MS_Temperature temperatureNotification) {
        this.temperatureNotification = temperatureNotification;
    }

    public ArrayList<MonitorSensor> getAllMonitorSensors() {
        ArrayList<MonitorSensor> monitorSensorArrayList = new ArrayList<MonitorSensor>();
        if (getPanicButtonNotification() != null)
            monitorSensorArrayList.add(getPanicButtonNotification());
        if (getShoeNotification() != null)
            monitorSensorArrayList.add(getShoeNotification());
        if (getGpsNotification() != null)
            monitorSensorArrayList.add(getGpsNotification());
        if (getTemperatureNotification() != null)
            monitorSensorArrayList.add(getTemperatureNotification());
        if (getBatteryNotification() != null) {
            monitorSensorArrayList.add(getBatteryNotification());
        }
        return monitorSensorArrayList;
    }

    @Override
    public String toString() {
        String show = "MS_Notification \n";
        if (getPanicButtonNotification() != null)
            show += "panicButtonNotification pressed";
        if (getShoeNotification() != null)
            show += "shoeNotification removed";
        if (getGpsNotification() != null)
            show += "gpsNotification = " + gpsNotification.notification;
        if (getBatteryNotification() != null)
            show += "batteryNotification = " + batteryNotification.getNotification() + " - " + batteryNotification.getValue();
        if (getTemperatureNotification() != null)
            show += "temperatureNotification = " + temperatureNotification.getNotification() + " - " + temperatureNotification.getValue();

        return show;
    }

    public String[] fillNotificationStringArray() {
        ArrayList<String> notificationString = new ArrayList<String>();
        if (panicButtonPressed || shoeRemoved) { // when some button panic or show removed MUST NOTIFY THE USER
            String dialogMSG = "";
            if (getPanicButtonNotification() != null) {
                notificationString.add(getPanicButtonNotification().toString());
                dialogMSG+=getPanicButtonNotification().toString()+"\n";
            }
            if (getShoeNotification() != null) {
                notificationString.add(getShoeNotification().toString());
                dialogMSG+=getShoeNotification().toString()+"\n";
            }
            if (getGpsNotification() != null) {
                Application.setCurrentGPSStatus(getGpsNotification().getNotification());
                notificationString.add(getGpsNotification().toString());
                dialogMSG+=getGpsNotification().toString()+"\n";
            }
            if (getTemperatureNotification() != null) {
                notificationString.add(getTemperatureNotification().toString());
                dialogMSG+=getTemperatureNotification().toString()+"\n";
            }
            if (getBatteryNotification() != null) {
                Application.setCurrentBatteryStatus(getBatteryNotification().getNotification());
                notificationString.add(getBatteryNotification().toString());
                dialogMSG+=getBatteryNotification().toString()+"\n";
            }
            notifyServiceToShowDialog(dialogMSG);
        } else { // individual notification test
            if (getGpsNotification() != null && (Application.getCurrentGPSStatus() == null || !Application.getCurrentGPSStatus().equals(gpsNotification.getNotification()))) {
                Application.setCurrentGPSStatus(gpsNotification.getNotification());
                notificationString.add(getGpsNotification().toString());
            }
            if (getTemperatureNotification() != null && getTemperatureNotification().isNecessaryNotify())
                notificationString.add(getTemperatureNotification().toString());
            if (getBatteryNotification() != null && (Application.getCurrentBatteryStatus() == null || getBatteryNotification().isNecessaryNotify())) {
                Application.setCurrentBatteryStatus(batteryNotification.getNotification());
                notificationString.add(getBatteryNotification().toString());
            }
        }
        return notificationString.toArray(new String[notificationString.size()]);
    }

    private void notifyServiceToShowDialog(String msg) {
        Intent intentService = new Intent();
        intentService.setAction(AppService.notifyService);
        intentService.putExtra(AppService.varStringMsgToPassToService, msg);
        Application.getmContext().sendBroadcast(intentService);
    }

    public static void sendNotificationToUser(Context mContext, List<MS_Notification> MSNotificationList) {
        int notificationID = 512;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBig(mContext, notificationID, MSNotificationList);
        } else {
            //notificationSmall(mContext, notificationID, MSNotificationList.get(0));
        }

    }

    private static void notificationBig(Context mContext, int notificationID, List<MS_Notification> MSNotificationList) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext);

        mBuilder.setContentTitle("New Notification");
        mBuilder.setContentText("You've received new notification.");
        mBuilder.setTicker("New Notification Alert!");
        mBuilder.setSmallIcon(R.drawable.ic_notification_danger);

      /* Increase notification number every time a new notification arrives */
        //mBuilder.setNumber(++numMessages);

//Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = MSNotificationList.get(0).fillNotificationStringArray();

        if (events.length == 0)
            return;

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Monitoring Notification");
        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        mBuilder.setStyle(inboxStyle);
        mBuilder.setSound(soundUri); //This sets the sound to play

      /* Creates an explicit intent for an Activity in your app */
        Intent resultIntent = new Intent(mContext, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

      /* notificationID allows you to update the notification later on. */
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

//    private static void notificationSmall(Context mContext, int notificationID, MS_Notification ms_notification) {
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(mContext)
//                        .setSmallIcon(R.drawable.panic_button)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//// Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(mContext, SettingsActivity.class);
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
//// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(SettingsActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//// mId allows you to update the notification later on.
//        mNotificationManager.notifyDevice(notificationID, mBuilder.build());
//    }

}
