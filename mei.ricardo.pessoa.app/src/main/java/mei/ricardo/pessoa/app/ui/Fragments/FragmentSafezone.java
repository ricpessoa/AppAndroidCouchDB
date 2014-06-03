package mei.ricardo.pessoa.app.ui.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 14/05/14.
 */
public class FragmentSafezone extends Fragment {
    private static String TAG = FragmentSafezone.class.getName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safezone_notifications, container, false);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}