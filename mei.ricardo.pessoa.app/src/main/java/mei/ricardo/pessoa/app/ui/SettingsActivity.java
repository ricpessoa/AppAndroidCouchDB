package mei.ricardo.pessoa.app.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 22/05/14.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
