package mei.ricardo.pessoa.app.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.awt.font.TextAttribute;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Settings;
import mei.ricardo.pessoa.app.utils.DialogFragmentYesNoOk;

/**
 * Created by rpessoa on 22/05/14.
 */
public class SettingsActivity extends PreferenceActivity {
    private static String TAG = SettingsActivity.class.getCanonicalName();

    public static final String notify = "mei.ricardo.pessoa.app.ui.Settings";
    private SettingsBroadcastReceiver deviceBroadcastReceiver = null;

    CheckBoxPreference checkBoxPreferenceNotifications;
    CheckBoxPreference checkBoxPreferenceSound;
    Preference preferenceLogout;

    Settings settingsOfAppM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        settingsOfAppM = Settings.getmSettingsinstance();
        setupPreferences();
    }


    @Override
    protected void onResume() {
        super.onResume();
        deviceBroadcastReceiver = new SettingsBroadcastReceiver();
        registerReceiver(deviceBroadcastReceiver, new IntentFilter(notify));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (deviceBroadcastReceiver != null)
            unregisterReceiver(deviceBroadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    private void setupPreferences() {

        checkBoxPreferenceNotifications = (CheckBoxPreference) findPreference("checkbox_preference_notifications");
        checkBoxPreferenceSound = (CheckBoxPreference) findPreference("checkbox_preference_sound");

        checkBoxPreferenceNotifications.setChecked(Settings.getmSettingsinstance().isMonitoring());
        checkBoxPreferenceSound.setChecked(Settings.getmSettingsinstance().isSounds());


        checkBoxPreferenceNotifications.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                settingsOfAppM.setMonitoring(!settingsOfAppM.isMonitoring());
                return false;
            }
        });

        checkBoxPreferenceSound.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                settingsOfAppM.setSounds(!settingsOfAppM.isSounds());
                return false;
            }
        });
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equalsIgnoreCase("preference_logout")) {
            //Toast.makeText(getApplicationContext(), "logout clicked", Toast.LENGTH_SHORT).show();

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

    private class SettingsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "I Receive a broadcast of Settings ", Toast.LENGTH_SHORT).show();
            setupPreferences();
        }
    }
}
