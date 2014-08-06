package mei.ricardo.pessoa.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.couchdb.CouchDB;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentMyDashboard;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentMyDevices;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentMyProfile;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentTestSamples;
import mei.ricardo.pessoa.app.utils.DialogFragmentYesNoOk;
import mei.ricardo.pessoa.app.ui.Navigation.NavigationDrawerFragment;
import mei.ricardo.pessoa.app.ui.user.LoginActivity;
import mei.ricardo.pessoa.app.R;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private static String TAG = MainActivity.class.getName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    /**
     * to receive notification on Device change and refresh Dashboard/TabHost
     */
    public static final String notifyDevice = "mei.ricardo.pessoa.app.notifyDevice.devices";
    private DeviceBroadcastReceiver deviceBroadcastReceiver = null;
    private boolean showTheDashboard = false;


    public static final String notifyMonitorSensor = "mei.ricardo.pessoa.app.notifyDevice.mydashboard";
    private MonitorSensorBroadcastReceiver monitorSensorBroadcastReceiver = null;


    public void logoutTheUser(boolean logout) {
        if (logout == true) {
            Log.d(TAG, "Logout the user session");
            Application.saveInSharePreferenceDataOfApplication(null);
            Application.isLogged = false;
        } else {
            Log.d(TAG, "The user remember is null - need authentication");
        }
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Application.getDbname() == null) {
            logoutTheUser(false); // need authentication
        }

        Log.d(TAG, "read db from " + Application.getDbname());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        CouchDB.getmCouchDBinstance();

    }

    /*THIS METHOD WHERE ADD THE FRAGMENTS OR ACTIVITIES TO NAVIGATE WHEN SELECTED*/
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                Log.d(TAG, "Show My Dashboard");
                fragment = new FragmentMyDashboard();
                showTheDashboard = true;
                break;
            case 1:
                Log.d(TAG, "Show My Devices");
                fragment = new FragmentMyDevices();
                showTheDashboard = false;
                break;
            case 2:
                Log.d(TAG, "Show My Profile");
                fragment = new FragmentMyProfile();
                showTheDashboard = false;
                break;
            case 3:
                Log.d(TAG, "Show Settings");
                Intent intent = new Intent(this, SettingsActivity.class);
                showTheDashboard = false;
                startActivity(intent);
                break;
            case 4:
                Log.d(TAG, "Fragment Test Sample");
                showTheDashboard = false;
                fragment = new FragmentTestSamples();
                break;
            case 5:
                logoutTheUser(true); // logout
                showTheDashboard = false;
                break;
            default:
                Log.d(TAG, "Undefined - show dashboard");
                fragment = new FragmentMyDashboard();
                showTheDashboard = true;
                break;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }
    }

    /*THIS METHOD IS TO SHOW THE TITLE OF VIEW OR FRAGMENT*/
    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.str_title_my_dashboard);
                break;
            case 1:
                mTitle = getString(R.string.title_activity_fragment_my_devices);
                break;
            case 2:
                mTitle = getString(R.string.str_title_my_profile);
                break;
            /*case 3:
                mTitle = getString(R.string.str_title_logout);
                break;*/
            default:
                mTitle = getString(R.string.str_title_my_dashboard);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // your code.
        DialogFragmentYesNoOk dialogFragmentYesNoOk = new DialogFragmentYesNoOk(this, getResources().getString(R.string.str_title_information_dialog), getResources().getString(R.string.str_error_exit_application));
        dialogFragmentYesNoOk.setType(2);
        dialogFragmentYesNoOk.setPositiveAndNegative(getString(R.string.fire_yes), getString(R.string.fire_no));
        dialogFragmentYesNoOk.setBackToPreviousActivity(true);
        dialogFragmentYesNoOk.show(getFragmentManager(), "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        deviceBroadcastReceiver = new DeviceBroadcastReceiver();
        registerReceiver(deviceBroadcastReceiver, new IntentFilter(notifyDevice));

        monitorSensorBroadcastReceiver = new MonitorSensorBroadcastReceiver();
        registerReceiver(monitorSensorBroadcastReceiver, new IntentFilter(notifyMonitorSensor));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (deviceBroadcastReceiver != null)
            unregisterReceiver(deviceBroadcastReceiver);
        if (monitorSensorBroadcastReceiver != null)
            unregisterReceiver(monitorSensorBroadcastReceiver);
    }

    private class DeviceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "I Receive a broadcast of devices ", Toast.LENGTH_SHORT).show();
            if (showTheDashboard) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = new FragmentMyDashboard();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).commit();
            }
        }
    }

    private class MonitorSensorBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (showTheDashboard) {
                String mac_address = intent.getExtras().getString(FragmentMyDashboard.passVariableMacAddress);
                Toast.makeText(context, "I Receive a broadcast of monitor sensor to update " + mac_address, Toast.LENGTH_SHORT).show();
                //showTabHostofMonitorSensorReceived(mac_address);
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = new FragmentMyDashboard();
                Bundle bundle = new Bundle();
                bundle.putString(FragmentMyDashboard.passVariableMacAddress, mac_address);
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment).commit();
            }
        }
    }
}
