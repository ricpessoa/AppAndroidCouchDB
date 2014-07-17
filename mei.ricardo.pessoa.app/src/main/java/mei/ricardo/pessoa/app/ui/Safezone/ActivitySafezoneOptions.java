package mei.ricardo.pessoa.app.ui.Safezone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Safezone;
import mei.ricardo.pessoa.app.ui.Fragments.Utils.DownloadImageTask;

/**
 * References :
 * startActivityForResult -  //http://rominirani.com/android-activity-call-and-wait/
 * Dialog to picker one option - http://stackoverflow.com/questions/16389581/android-create-a-popup-that-has-multiple-selection-options
 */
public class ActivitySafezoneOptions extends ActionBarActivity implements View.OnClickListener {
    private static String TAG = ActivitySafezoneOptions.class.getCanonicalName();
    public static String passVarIDSafezone = "passVarIDSafezone";
    public static String returnVariableNewName = "returnVariableNewName";
    public static String returnVariableNewNotification = "returnVariableNewNotification";
    // The requests codes to startActivityForResult
    static final int valueOnActivityResultCodeChangeName = 1;
    static final int valueOnActivityResultCodeChangeNotifications = 2;
    static final int valueOnActivityResultCodeChangeRadius = 3;
    static final int valueOnActivityResultCodeChangeLocation = 3;

    private String IDSafezone;
    private Safezone safezone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safezone);

        IDSafezone = getIntent().getExtras().getString(passVarIDSafezone);
        refreshActivity();
        Button buttonEditLocation = (Button) findViewById(R.id.buttonEditSafezone);
        buttonEditLocation.setOnClickListener(this);
        Button buttonEditNotification = (Button) findViewById(R.id.buttonEditNotification);
        buttonEditNotification.setOnClickListener(this);
        Button buttonEditRadius = (Button) findViewById(R.id.buttonEditSafezoneRadius);
        buttonEditRadius.setOnClickListener(this);
        Button buttonEditName = (Button) findViewById(R.id.buttonEditSafezoneName);
        buttonEditName.setOnClickListener(this);
        Button buttonDelete = (Button) findViewById(R.id.buttonDeleteSafezone);
        buttonDelete.setOnClickListener(this);
    }

    private void refreshActivity() {
        safezone = Safezone.getSafezoneByID(IDSafezone);
        ImageView imageViewSafezone = (ImageView) findViewById(R.id.imageViewSafezoneStreetView);
        new DownloadImageTask(imageViewSafezone)
                .execute("https://cbks0.google.com/cbk?output=thumbnail&w=400&h=400&ll=" + safezone.getLatitude() + "," + safezone.getLongitude() + "&thumb=0");

        ((TextView) findViewById(R.id.textViewAddressSafezone)).setText(safezone.getAddress());
        if (!safezone.getName().equals(safezone.getAddress())) {
            TextView TextViewname = (TextView) findViewById(R.id.textViewNameSafezone);
            TextViewname.setText(safezone.getName());
            TextViewname.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.safezone, menu);
        return true;
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
    public void onClick(View view) {
        if (view.getId() == R.id.buttonEditSafezone) {
            Log.d(TAG, "Clicked buttonEditSafezone");
            Intent intent = new Intent(this,ActivitySafezoneEditMap.class);
            intent.putExtra(passVarIDSafezone,safezone.get_id());
            startActivity(intent);
        } else if (view.getId() == R.id.buttonEditSafezoneRadius) {
            Log.d(TAG, "Clicked buttonEditSafezoneRadius");
        } else if (view.getId() == R.id.buttonEditSafezoneName) {
            Intent intent = new Intent(this, ActivityEditNameSafezone.class);
            intent.putExtra(ActivityEditNameSafezone.passVarNameSafezone, safezone.getName());
            startActivityForResult(intent, valueOnActivityResultCodeChangeName);
        } else if (view.getId() == R.id.buttonEditNotification) {
            Intent intent = new Intent(this, ActivityEditNameSafezone.class);
            intent.putExtra(ActivityEditNameSafezone.passVarNotificationSafezone, safezone.getNotificationPosition());
            startActivityForResult(intent, valueOnActivityResultCodeChangeNotifications);
        } else if (view.getId() == R.id.buttonDeleteSafezone) {
            boolean deleted = Safezone.delete(safezone.get_id());
            if (deleted)
                finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == valueOnActivityResultCodeChangeName && resultCode == RESULT_OK) {
            //Display the modified values
            String newName = data.getExtras().getString(returnVariableNewName);
            Toast.makeText(this, "TODO: UPDATE SAFEZONE NAME " + newName, Toast.LENGTH_SHORT).show();
            safezone.setName(newName);
            try {
                safezone.saveSafezone();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Some error happened when try save safezone", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == valueOnActivityResultCodeChangeNotifications && resultCode == RESULT_OK) {
            int newNotification = data.getExtras().getInt(returnVariableNewNotification);
            Toast.makeText(this, "TODO: UPDATE SAFEZONE Notification " + newNotification, Toast.LENGTH_SHORT).show();
            safezone.setNotification(Safezone.typeNotifications[newNotification]);
            try {
                safezone.saveSafezone();
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Some error happened when try save safezone", Toast.LENGTH_SHORT).show();
            }
        }
        refreshActivity();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
