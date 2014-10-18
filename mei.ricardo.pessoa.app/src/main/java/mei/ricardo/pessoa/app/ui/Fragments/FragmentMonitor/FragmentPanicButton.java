package mei.ricardo.pessoa.app.ui.Fragments.FragmentMonitor;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_PanicButton;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityMonitorSensorDetail;


public class FragmentPanicButton extends Fragment {
    private static String TAG = FragmentPanicButton.class.getName();
    TextView textView;
    boolean ms_panic_button;
    public FragmentPanicButton() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_panic_button, container, false);
        textView = (TextView) rootView.findViewById(R.id.panic_button_fragment_textView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityMonitorSensorDetail activityMonitorSensorDetail = (ActivityMonitorSensorDetail) getActivity();
        ms_panic_button = activityMonitorSensorDetail.isMs_panicButton();
        if (ms_panic_button) {
            textView.setText(MS_PanicButton.toStringStatic());
        } else {
            getView().setVisibility(View.GONE);
        }
    }
}