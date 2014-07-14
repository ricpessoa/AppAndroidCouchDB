package mei.ricardo.pessoa.app.ui.Navigation;

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
import mei.ricardo.pessoa.app.ui.Fragments.Utils.DialogFragmentYesNoOk;
import mei.ricardo.pessoa.app.ui.SettingsActivity;
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
    //private String dbname;

    //couch internals
    //public static com.couchbase.lite.View viewGetDevices;
    //private LiveQuery liveQuery;

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
                break;
            case 1:
                Log.d(TAG, "Show My Devices");
                fragment = new FragmentMyDevices();
                break;
            case 2:
                Log.d(TAG, "Show My Profile");
                fragment = new FragmentMyProfile();
                break;
            case 3:
                Log.d(TAG, "Show Settings");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case 4:
                Log.d(TAG, "Fragment Test Sample");
                fragment = new FragmentTestSamples();
                break;
            case 5:
                logoutTheUser(true); // logout
                break;
            default:
                Log.d(TAG, "Undefined - show dashboard");
                fragment = new FragmentMyDashboard();
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
        DialogFragmentYesNoOk dialogFragmentYesNoOk = new DialogFragmentYesNoOk(this, "Are you sure you want to exit the application?");
        dialogFragmentYesNoOk.setType(2);
        dialogFragmentYesNoOk.setPositiveAndNegative(getString(R.string.fire_yes), getString(R.string.fire_no));
        dialogFragmentYesNoOk.setBackToPreviousActivity(true);
        dialogFragmentYesNoOk.show(getFragmentManager(), "");
    }
}
