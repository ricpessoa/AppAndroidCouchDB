package mei.ricardo.pessoa.app.Navigation;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.Fragments.FragmentMyDashboard;
import mei.ricardo.pessoa.app.Fragments.FragmentMyDevices;
import mei.ricardo.pessoa.app.Fragments.FragmentMyProfile;
import mei.ricardo.pessoa.app.LoginActivity;
import mei.ricardo.pessoa.app.R;

import com.couchbase.lite.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

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

    public void logoutTheUser(boolean logout) {
        if (logout == true){
            Log.d(TAG, "Logout the user session");
            Application.saveInSharePreferenceDataOfApplication(this, null);
            Application.isLogged = false;
        }else{
            Log.d(TAG,"The user remember is null - need authentication");
        }
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String db = Application.loadInSharePreferenceDataOfApplication(this.getApplicationContext());
        if (db==null){
            logoutTheUser(false); // need authentication
        }

        Log.d(TAG,"read db from "+db);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        /**
         * SAMPLE CODE*/

/*         Log.d(TAG, "Begin Hello World App");

        // create a manager
        Manager manager = null;
        try {
            manager = new Manager(getApplicationContext().getFilesDir(), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            com.couchbase.lite.util.Log.e(TAG, "Cannot create manager object");
            return;
        }



        // create a name for the database and make sure the name is legal
        String dbname = "hello";
        if (!Manager.isValidDatabaseName(dbname)) {
            Log.e(TAG, "Bad database name");
            return;
        }



        // create a new database
        Database database = null;
        try {
            database = manager.getDatabase(dbname);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot get database");
            return;
        }



        // get the current date and time
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());



        // create an object that contains data for a document
        Map<String, Object> docContent = new HashMap<String, Object>();
        docContent.put("message", "Hello Couchbase Lite");
        docContent.put("creationDate", currentTimeString);



        // display the data for the new document
        Log.d(TAG, "docContent=" + String.valueOf(docContent));



        // create an empty document
        Document document = database.createDocument();



        // write the document to the database
        try {
            document.putProperties(docContent);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
        }



        // save the ID of the new document
        String docID = document.getId();



        // retrieve the document from the database
        Document retrievedDocument = database.getDocument(docID);



        // display the retrieved document
        Log.d(TAG, "retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));



        Log.d(TAG, "End Hello World App");


*/

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
                mTitle = getString(R.string.str_title_my_devices);
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

}
