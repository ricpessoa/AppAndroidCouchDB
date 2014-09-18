package mei.ricardo.pessoa.app.ui.Fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mei.ricardo.pessoa.app.couchdb.modal.Profile;
import mei.ricardo.pessoa.app.ui.MainActivity;
import mei.ricardo.pessoa.app.R;

public class FragmentMyProfile extends Fragment {
    private static String TAG = FragmentMyProfile.class.getName();
    TextView textViewUsername,textViewName, textViewEmail, textViewMobilePhone, textViewCountry, textViewNumberOfDevices;

    public FragmentMyProfile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        Profile profile = Profile.getProfile();
        textViewUsername = (TextView) rootView.findViewById(R.id.textViewUsername);
        textViewName = (TextView) rootView.findViewById(R.id.textViewName);
        textViewEmail = (TextView) rootView.findViewById(R.id.textViewEmail);
        textViewMobilePhone = (TextView) rootView.findViewById(R.id.textViewMobilePhone);
        textViewCountry = (TextView) rootView.findViewById(R.id.textViewCountry);
        textViewNumberOfDevices = (TextView) rootView.findViewById(R.id.textViewNumberOfDevices);

        if(profile!=null){
            textViewUsername.setText(profile.getName());
            textViewName.setText(profile.getFull_name());
            textViewEmail.setText(profile.getEmail());
            textViewMobilePhone.setText(profile.getMobile_phone());
            textViewCountry.setText(profile.getCountry());
            textViewNumberOfDevices.setText("TODO: getDeviceNumber.. BTW = 1");
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(2);
    }
}