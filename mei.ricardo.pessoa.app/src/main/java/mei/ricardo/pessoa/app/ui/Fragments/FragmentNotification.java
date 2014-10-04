package mei.ricardo.pessoa.app.ui.Fragments;

import mei.ricardo.pessoa.app.Application;

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
    private static View view;
    private static ImageView mImageViewNotification;
    private static TextView mTextViewNotification;
    static boolean isRunning = false;

    private static int indexOfNotificationArrayList = 0;
    private static int indexAtualMonitoring = 0;

    private static Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_navigator_drawer_notification, container, false);

        mImageViewNotification = (ImageView) view.findViewById(R.id.imageViewNotification);
        mTextViewNotification = (TextView) view.findViewById(R.id.textViewNotification);

        mContext = getActivity().getApplicationContext();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillFragmentNotification();
        if (!isRunning) {
            runnable.run();
        }
    }


    private static void fillFragmentNotification() {
        if (mContext == null)
            return;

        Application app = (Application) mContext;

        if (app.getMs_notificationArrayList().size() == 0) {
            mImageViewNotification.setVisibility(View.GONE);
            mTextViewNotification.setText(mContext.getString(R.string.str_notifications_no_notification));
            mTextViewNotification.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            //handler.removeCallbacksAndMessages(runnable);
        } else {
            mTextViewNotification.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }

        if (app.getMs_notificationArrayList() != null && app.getMs_notificationArrayList().size() > 0) {
            //Log.d(TAG, "size:" + app.ms_notificationArrayList.size() + " - " + indexOfNotificationArrayList + " - " + indexAtualMonitoring);

            mImageViewNotification.setVisibility(View.VISIBLE);
            MS_Notification ms_notification = app.getMs_notificationArrayList().get(indexOfNotificationArrayList);

            if (indexAtualMonitoring > ms_notification.getAllMonitorSensors().size() - 1) {
                indexAtualMonitoring = 0;
                indexOfNotificationArrayList++;

                if (indexOfNotificationArrayList > app.getMs_notificationArrayList().size() - 1) {
                    indexOfNotificationArrayList = 0;
                }
            }
            try {
                MonitorSensor monitorSensor = ms_notification.getAllMonitorSensors().get(indexAtualMonitoring);
                mTextViewNotification.setText(monitorSensor.getTextToShowInFragmentNotification());
                mImageViewNotification.setImageDrawable(monitorSensor.getImage());
                indexAtualMonitoring++;
            } catch (IndexOutOfBoundsException ex) {
                Log.d(TAG, "Error on indexAtualMonitoring");
                indexAtualMonitoring = 0;
            }


        }
    }

    public static Handler handler = new Handler();

    private static Runnable runnable = new Runnable() {
        public void run() {
            isRunning = true;
            fillFragmentNotification();
            handler.postDelayed(this, 3500);
        }
    };

}
