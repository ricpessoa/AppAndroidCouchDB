package mei.ricardo.pessoa.app.ui.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 22/05/14.
 */
public class FragmentSettings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
