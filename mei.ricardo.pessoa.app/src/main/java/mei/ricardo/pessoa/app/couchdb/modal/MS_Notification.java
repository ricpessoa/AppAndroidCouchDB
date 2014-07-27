package mei.ricardo.pessoa.app.couchdb.modal;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Battery;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_GPS;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_PanicButton;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Temperature;
import mei.ricardo.pessoa.app.ui.Navigation.MainActivity;
import mei.ricardo.pessoa.app.ui.SettingsActivity;

/**
 * Created by rpessoa on 27/07/14.
 */
public class MS_Notification {
    private MS_PanicButton panicButtonNotification;
    private MS_GPS gpsNotification;
    private MS_Battery batteryNotification;
    private MS_Temperature temperatureNotification;
    private boolean panicButtonPressed;

    public MS_Notification() {
    }

    public MS_PanicButton getPanicButtonNotification() {
        return panicButtonNotification;
    }

    public void setPanicButtonNotification(MS_PanicButton panicButtonNotification) {
        this.panicButtonNotification = panicButtonNotification;
        this.panicButtonPressed = true;
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

    @Override
    public String toString() {
        String show = "MS_Notification \n";
        if (getPanicButtonNotification() != null)
            show += "panicButtonNotification pressed";
        if (getGpsNotification() != null)
            show += "gpsNotification = " + gpsNotification.notification;
        if (getBatteryNotification() != null)
            show += "batteryNotification = " + batteryNotification.getNotifification() + " - " + batteryNotification.getValue();
        if (getTemperatureNotification() != null)
            show += "temperatureNotification = " + temperatureNotification.getNotifification() + " - " + temperatureNotification.getValue();

        return show;
    }

    private String[] fillNotificationStriongArray() {
        ArrayList<String> notificationString = new ArrayList<String>();
        if (panicButtonPressed) {
            if (getPanicButtonNotification() != null)
                notificationString.add("Button Panic Button was pressed");
            if (getGpsNotification() != null)
                notificationString.add("GPS: " + gpsNotification.getNotification() + " - " + gpsNotification.getAddress());
            if (getTemperatureNotification() != null)
                notificationString.add("Temperature: " + temperatureNotification.getNotifification() + " - " + temperatureNotification.getValue());
            if (getBatteryNotification() != null)
                notificationString.add("Battery: " + batteryNotification.getNotifification() + " - " + batteryNotification.getValue());
        } else {
            if (getGpsNotification() != null)
                notificationString.add("GPS: " + gpsNotification.getNotification() + " - " + gpsNotification.getAddress());
            if (getTemperatureNotification() != null && !getGpsNotification().equals(MS_Temperature.NOTIFICATIONTYPE.RANGE))
                notificationString.add("Temperature: " + temperatureNotification.getNotifification() + " - " + temperatureNotification.getValue());
            if (getBatteryNotification() != null && !getBatteryNotification().equals(MS_Battery.NOTIFICATIONTYPE.NORMAL))
                notificationString.add("Battery: " + batteryNotification.getNotifification() + " - " + batteryNotification.getValue());
        }
        return notificationString.toArray(new String[notificationString.size()]);
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
        mBuilder.setSmallIcon(R.drawable.panic_button_small);

      /* Increase notification number every time a new notification arrives */
        //mBuilder.setNumber(++numMessages);


      /* Add Big View Specific Configuration */
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        String[] events = new String[6];
        for (int i = 0; i < MSNotificationList.size(); i++) {
            events = MSNotificationList.get(i).fillNotificationStriongArray();
        }

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("Monitoring Notification");
        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        mBuilder.setStyle(inboxStyle);


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
//                        .setSmallIcon(R.drawable.panic_button_small)
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
//        mNotificationManager.notify(notificationID, mBuilder.build());
//    }
}
