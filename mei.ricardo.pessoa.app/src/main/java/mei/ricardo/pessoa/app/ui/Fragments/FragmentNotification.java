package mei.ricardo.pessoa.app.ui.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.MS_Notification;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;

/**
 * Created by rpessoa on 14/05/14.
 * this class is only to show the notification on navigation drawer (last)
 */
public class FragmentNotification extends Fragment {
    private static String TAG = FragmentNotification.class.getCanonicalName();
    public static Handler handler = new Handler();

    private static FragmentNotification fragmentNotification;

    public static FragmentNotification getFragmentNotification() {
        return fragmentNotification;
    }

    private static ImageView mImageViewNotification;
    private static TextView mTextViewNotification;


    public static final String notify = "mei.ricardo.pessoa.app.ui.Monitoring";
    private DeviceBroadcastReceiver deviceBroadcastReceiver = null;

    static boolean isRunning = false;

    private static int indexOfNotificationArrayList = 0;
    private static int indexAtualMonitoring = 0;
    private static ArrayList<MS_Notification> ms_notificationArrayList = new ArrayList<MS_Notification>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigator_drawer_notification, container, false);

        mImageViewNotification = (ImageView) view.findViewById(R.id.imageViewNotification);
        mTextViewNotification = (TextView) view.findViewById(R.id.textViewNotification);
        fragmentNotification = this;
        fillFragmentNotification();

        if (!isRunning) {
            runnable.run();
        }
        return view;
    }

    private static void fillFragmentNotification() {

        if (ms_notificationArrayList.size() == 0) {
            mImageViewNotification.setVisibility(View.GONE);
            mTextViewNotification.setText(fragmentNotification.getString(R.string.str_notifications_no_notification));
            mTextViewNotification.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            handler.removeCallbacksAndMessages(runnable);
        } else {
            mTextViewNotification.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }

        if (ms_notificationArrayList != null && ms_notificationArrayList.size() > 0) {
            Log.d(TAG, "size:" + ms_notificationArrayList.size() + " - " + indexOfNotificationArrayList + " - " + indexAtualMonitoring);

            mImageViewNotification.setVisibility(View.VISIBLE);
            MS_Notification ms_notification = ms_notificationArrayList.get(indexOfNotificationArrayList);

            if (indexAtualMonitoring > ms_notification.getAllMonitorSensors().size() - 1) {
                indexAtualMonitoring = 0;
                indexOfNotificationArrayList++;

                if (indexOfNotificationArrayList > ms_notificationArrayList.size() - 1) {
                    indexOfNotificationArrayList = 0;
                }

            }

            MonitorSensor monitorSensor = ms_notification.getAllMonitorSensors().get(indexAtualMonitoring);

            fragmentNotification.mTextViewNotification.setText(monitorSensor.getTextToShowInFragmentNotification());
            fragmentNotification.mImageViewNotification.setImageDrawable(monitorSensor.getImage());
            indexAtualMonitoring++;
        }
    }


    private static Runnable runnable = new Runnable() {
        public void run() {
            isRunning = true;
            fillFragmentNotification();
            handler.postDelayed(this, 3500);
        }
    };

    public void addNotificationToShowToUser(List<MS_Notification> arrayListMonitoring) {
        ms_notificationArrayList.addAll(arrayListMonitoring);

        if (ms_notificationArrayList.size() > 5) {
            do {
                ms_notificationArrayList.remove(0);
            } while (ms_notificationArrayList.size() < 5);
        }
        indexOfNotificationArrayList = ms_notificationArrayList.size()-1;//to show the last one inserted
        indexAtualMonitoring = 0;
        if (!isRunning) {
            runnable.run();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        deviceBroadcastReceiver = new DeviceBroadcastReceiver();
        getActivity().registerReceiver(deviceBroadcastReceiver, new IntentFilter(notify));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (deviceBroadcastReceiver != null)
            getActivity().unregisterReceiver(deviceBroadcastReceiver);
    }

    private class DeviceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "I Receive a broadcast of Monitoring ", Toast.LENGTH_SHORT).show();
        }
    }
}