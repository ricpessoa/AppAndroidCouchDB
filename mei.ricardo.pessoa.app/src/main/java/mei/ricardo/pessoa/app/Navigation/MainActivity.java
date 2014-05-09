package mei.ricardo.pessoa.app.Navigation;

import android.app.ProgressDialog;
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

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.support.CouchbaseLiteApplication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.Fragments.FragmentMyDashboard;
import mei.ricardo.pessoa.app.Fragments.FragmentMyDevices;
import mei.ricardo.pessoa.app.Fragments.FragmentMyProfile;
import mei.ricardo.pessoa.app.User.LoginActivity;
import mei.ricardo.pessoa.app.R;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Replication.ChangeListener {
    private static String TAG = MainActivity.class.getName();
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String dbname;

    //couch internals
    public static com.couchbase.lite.View viewItemsByDate;
    private LiveQuery liveQuery;

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
        dbname = Application.loadInSharePreferenceDataOfApplication(this.getApplicationContext());
        if (dbname==null){
            logoutTheUser(false); // need authentication
        }

        Log.d(TAG,"read db from "+dbname);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        try {
            startCBLite();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error Initializing CBLIte, see logs for details", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error initializing CBLite", e);
        }

    }

    protected void startCBLite() throws Exception {

        Application.mCouchManager = new Manager(getApplicationContext().getFilesDir(), Manager.DEFAULT_OPTIONS);

        //install a view definition needed by the application
        Application.database = Application.mCouchManager.getDatabase(dbname);

        String designDocName = "appViewDevices";
        String byDateViewName = "getAllDevices";

        viewItemsByDate = Application.database.getView(String.format("%s/%s", designDocName, byDateViewName));
        viewItemsByDate.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Object objDevice = document.get("type");
                if (objDevice != null && objDevice.equals("device")) {
                    emitter.emit(objDevice.toString(), null);
                }
            }
        }, "1.0");

        CouchbaseLiteApplication application = (CouchbaseLiteApplication) getApplication();
        application.setManager(Application.mCouchManager);

        //startLiveQuery(viewItemsByDate);

        startLiveQuery(viewItemsByDate);

        startSync();

        //getAllDevices();
        //getAllDocs();
    }
/*
    private void getAllDevices(){
        com.couchbase.lite.View view = database.getView("_design/application/_view/getDevices");
        Query queryDevices = view.createQuery();

        try {
            QueryEnumerator rowEnum = queryDevices.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext();) {
                QueryRow row = it.next();
                Log.d("Document ID:", row.getDocumentId());

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

    }
*/
/*
    private void getAllDocs(){
        Query query = database.createAllDocumentsQuery();
        query.setDescending(true);
        int i = 0;
        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext();) {
                QueryRow row = it.next();
                Log.d("Document ID:", row.getDocumentId());
                i++;
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"Num of docs in local db"+i);
    }
*/
    private void startSync() {

        URL syncUrl;
        try {
            syncUrl = new URL(Application.couchDBHostUrl+"/"+dbname);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Replication pullReplication = Application.database.createPullReplication(syncUrl);
        pullReplication.setContinuous(true);

        Replication pushReplication = Application.database.createPushReplication(syncUrl);
        pushReplication.setContinuous(true);

        pullReplication.start();
        pushReplication.start();

        pullReplication.addChangeListener(this);
        pushReplication.addChangeListener(this);

    }

    private void startLiveQuery(com.couchbase.lite.View view) throws Exception {

        //final ProgressDialog progressDialog = showLoadingSpinner();

        if (liveQuery == null) {

            liveQuery = view.createQuery().toLiveQuery();

            liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
                @Override
                public void changed(LiveQuery.ChangeEvent event) {
                  displayRows(event.getRows());

                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });*/

                }
            });

            liveQuery.start();

        }

    }

    private void displayRows(QueryEnumerator queryEnumerator) {

       Log.d(TAG, "Change show");
        Intent intent = new Intent();

        intent.setAction(FragmentMyDevices.notify);
        //intent.putExtra(TemperatureActivity.VAR_NAME, finalX);
        getApplicationContext().sendBroadcast(intent);

    }

    @Override
    public void changed(Replication.ChangeEvent event) {

        Replication replication = event.getSource();
        Log.d(TAG, "Replication : " + replication + " changed.");
        if (!replication.isRunning()) {
            String msg = String.format("Replicator %s not running", replication);
            Log.d(TAG, msg);
        }
        else {
            int processed = replication.getCompletedChangesCount();
            int total = replication.getChangesCount();
            String msg = String.format("Replicator processed %d / %d", processed, total);
            Log.d(TAG, msg);
        }

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
