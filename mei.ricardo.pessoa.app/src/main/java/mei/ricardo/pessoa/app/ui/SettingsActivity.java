package mei.ricardo.pessoa.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.utils.DialogFragmentYesNoOk;

/**
 * Created by rpessoa on 22/05/14.
 */
public class SettingsActivity extends PreferenceActivity {

    CheckBoxPreference checkBoxPreferenceNotifications;
    CheckBoxPreference checkBoxPreferenceSound;
    Preference preferenceLogout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    private void setupPreferences() {
        //checkBoxPreferenceNotifications = (CheckBoxPreference)  find
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equalsIgnoreCase("preference_logout")) {
            Toast.makeText(getApplicationContext(), "logout clicked", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.str_title_attention_dialog));
            builder.setMessage(getString(R.string.str_logout_msg))
                    .setPositiveButton(getString(R.string.fire_yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.fire_no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.show();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
