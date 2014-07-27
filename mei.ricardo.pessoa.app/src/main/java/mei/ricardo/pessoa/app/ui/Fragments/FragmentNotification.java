package mei.ricardo.pessoa.app.ui.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;

/**
 * Created by rpessoa on 14/05/14.
 */
public class FragmentNotification extends Fragment {
    private static String TAG = FragmentNotification.class.getCanonicalName();
    //public static Handler handler = new Handler();
    private static ImageView mImageViewNotification;
    private static TextView mTextViewNotification;
    private static String[] notificationTitles;
    private static TypedArray notificationDrawerIcons;
    private static int indexImage = 0;

    public static final String notify = "mei.ricardo.pessoa.app.ui.Monitoring";
    private DeviceBroadcastReceiver deviceBroadcastReceiver = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigator_drawer_notification, container, false);
        notificationTitles = getResources().getStringArray(R.array.notifications_drawer_items);
        notificationDrawerIcons = getResources().obtainTypedArray(R.array.notifications_drawer_icons);

        mImageViewNotification = (ImageView) view.findViewById(R.id.imageViewNotification);
        mTextViewNotification = (TextView) view.findViewById(R.id.textViewNotification);

        //runnable.run();

        return view;
    }

//    private static Runnable runnable = new Runnable() {
//
//        public void run() {
//
//            if (indexImage > notificationDrawerIcons.length() - 1) {
//                indexImage = 0;
//            }
//
//            mImageViewNotification.setImageDrawable(notificationDrawerIcons.getDrawable(indexImage));
//            mTextViewNotification.setText(notificationTitles[indexImage]);
//            indexImage++;
//            handler.postDelayed(this, 3000);
//
//        }
//    };

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
            //MonitorSensor.getTheLastmonitoringSensors();
            //refreshActivity();
        }
    }
}