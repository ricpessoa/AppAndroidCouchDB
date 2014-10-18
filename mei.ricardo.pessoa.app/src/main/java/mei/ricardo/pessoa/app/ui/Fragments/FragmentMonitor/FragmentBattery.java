package mei.ricardo.pessoa.app.ui.Fragments.FragmentMonitor;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Battery;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityMonitorSensorDetail;


public class FragmentBattery extends Fragment {
    private static String TAG = FragmentBattery.class.getName();
    MS_Battery ms_battery;
    private TextView textViewNotification;
    private TextView textViewValue;
    ImageView imageView;

    public FragmentBattery() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_battery, container, false);
        textViewNotification = (TextView) rootView.findViewById(R.id.textViewNotification);
        textViewValue = (TextView) rootView.findViewById(R.id.textViewBatteryValue);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityMonitorSensorDetail activityMonitorSensorDetail = (ActivityMonitorSensorDetail) getActivity();
        ms_battery = activityMonitorSensorDetail.getMs_battery();
        if (ms_battery != null) {
            textViewNotification.setText("["+ms_battery.getNotification()+"]");
            textViewValue.setText(ms_battery.toString());
            imageView.setImageDrawable(ms_battery.getImage());
        } else {
            getView().setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}