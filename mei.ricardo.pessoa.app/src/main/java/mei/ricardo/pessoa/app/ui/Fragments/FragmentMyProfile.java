package mei.ricardo.pessoa.app.ui.Fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fr.tkeunebr.gravatar.Gravatar;
import mei.ricardo.pessoa.app.couchdb.modal.Profile;
import mei.ricardo.pessoa.app.ui.MainActivity;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.utils.DownloadImageTask;

public class FragmentMyProfile extends Fragment {
    private static String TAG = FragmentMyProfile.class.getName();
    TextView textViewUsername, textViewName, textViewEmail, textViewMobilePhone, textViewCountry, textViewNumberOfDevices;
    ImageView imageViewUser;

    public FragmentMyProfile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        Profile profile = Profile.getProfile();
        imageViewUser = (ImageView) rootView.findViewById(R.id.imageViewUser);
        textViewUsername = (TextView) rootView.findViewById(R.id.textViewUsername);
        textViewName = (TextView) rootView.findViewById(R.id.textViewName);
        textViewEmail = (TextView) rootView.findViewById(R.id.textViewEmail);
        textViewMobilePhone = (TextView) rootView.findViewById(R.id.textViewMobilePhone);
        textViewCountry = (TextView) rootView.findViewById(R.id.textViewCountry);
        textViewNumberOfDevices = (TextView) rootView.findViewById(R.id.textViewNumberOfDevices);

        if (profile != null) {
            textViewUsername.setText(profile.getName());
            textViewName.setText(profile.getFull_name());
            textViewEmail.setText(profile.getEmail());
            if (imageViewUser != null) {
                imageViewUser.setImageDrawable(getResources().getDrawable(R.drawable.ic_img_not_found));
                String gravatarUrl = Gravatar.init().with(profile.getEmail().toLowerCase()).force404().size(150).build();
                new DownloadImageTask(imageViewUser)
                        .execute(gravatarUrl);

            }
            if (profile.getMobile_phone() != null && !profile.getMobile_phone().trim().equals("")) {
                textViewMobilePhone.setText(profile.getMobile_phone());
            } else {
                textViewMobilePhone.setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.textViewTMP)).setVisibility(View.GONE);
            }
            if (profile.getCountry() != null && !profile.getCountry().trim().equals("")) {
                textViewCountry.setText(profile.getCountry());
            } else {
                textViewCountry.setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.textViewTC)).setVisibility(View.GONE);
            }
            if (profile.getNumberOfDevice() != null) {
                textViewNumberOfDevices.setText(profile.getNumberOfDevice()+"");
            } else {
                textViewNumberOfDevices.setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.textViewTND)).setVisibility(View.GONE);
            }
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(2);
    }
}