package mei.ricardo.pessoa.app.ui.Fragments.FragmentMonitor;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Shoe;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityMonitorSensorDetail;


public class FragmentShoe extends Fragment {
    private static String TAG = FragmentShoe.class.getName();
    TextView textView;
    boolean ms_shoe;
    public FragmentShoe() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shoe, container, false);
        textView = (TextView) rootView.findViewById(R.id.shoe_fragment_textView);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityMonitorSensorDetail activityMonitorSensorDetail = (ActivityMonitorSensorDetail) getActivity();
        ms_shoe = activityMonitorSensorDetail.isMs_shoe();
        if (ms_shoe) {
            textView.setText(MS_Shoe.toStringStatic());
        } else {
            getView().setVisibility(View.GONE);
        }
    }

}