package mei.ricardo.pessoa.app.Fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mei.ricardo.pessoa.app.Navigation.MainActivity;
import mei.ricardo.pessoa.app.R;


public class FragmentMyDashboard extends Fragment {

    private static String TAG = FragmentMyDashboard.class.getName();

    public FragmentMyDashboard() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_dashboard, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(0);
    }
}