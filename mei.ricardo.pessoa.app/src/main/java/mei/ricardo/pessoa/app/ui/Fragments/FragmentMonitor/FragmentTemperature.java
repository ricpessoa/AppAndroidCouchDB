package mei.ricardo.pessoa.app.ui.Fragments.FragmentMonitor;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_GPS;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Temperature;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityMonitorSensorDetail;


public class FragmentTemperature extends Fragment {
    private static String TAG = FragmentTemperature.class.getName();
    TextView textView;
    MS_Temperature ms_temperatures;
    public FragmentTemperature() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_temperature, container, false);
        textView = (TextView)rootView.findViewById(R.id.temperature_fragment_textView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityMonitorSensorDetail activityMonitorSensorDetail = (ActivityMonitorSensorDetail) getActivity();
        ms_temperatures = activityMonitorSensorDetail.getMs_temperature();
        if (ms_temperatures != null) {
            textView.setText(ms_temperatures.toString());
        } else {
            getView().setVisibility(View.GONE);
        }
    }

}