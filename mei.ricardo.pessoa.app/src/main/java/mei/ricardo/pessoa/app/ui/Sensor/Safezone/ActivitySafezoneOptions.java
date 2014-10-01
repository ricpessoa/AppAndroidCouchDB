package mei.ricardo.pessoa.app.ui.Sensor.Safezone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Safezone;
import mei.ricardo.pessoa.app.utils.DownloadImageTask;

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
    public static String passVarIsToEditLocation = "passVarIsToEditLocation";
    // The requests codes to startActivityForResult
    static final int valueOnActivityResultCodeChangeName = 1;
    static final int valueOnActivityResultCodeChangeNotifications = 2;

    private String IDSafezone;
    private Safezone safezone;

    public static final String notify = "mei.ricardo.pessoa.app.ui.Sensor.DeviceRow";
    private DeviceBroadcastReceiver deviceBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safezone);

        IDSafezone = getIntent().getExtras().getString(passVarIDSafezone);
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
    protected void onResume() {
        super.onResume();
        deviceBroadcastReceiver = new DeviceBroadcastReceiver();
        registerReceiver(deviceBroadcastReceiver, new IntentFilter(notify));
        refreshActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (deviceBroadcastReceiver != null)
            unregisterReceiver(deviceBroadcastReceiver);
    }

    private void openActivitySafezoneEditMap(boolean isToEditLocation) {
        Intent intent = new Intent(this, ActivitySafezoneEditMap.class);
        intent.putExtra(passVarIDSafezone, safezone.get_id());
        intent.putExtra(passVarIsToEditLocation, isToEditLocation);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonEditSafezone:
                openActivitySafezoneEditMap(true);
                break;
            case R.id.buttonEditSafezoneRadius:
                openActivitySafezoneEditMap(false);
                break;
            case R.id.buttonEditSafezoneName:
                Intent intentEdtiName = new Intent(this, ActivityEditNameSafezone.class);
                intentEdtiName.putExtra(ActivityEditNameSafezone.passVarNameSafezone, safezone.getName());
                startActivityForResult(intentEdtiName, valueOnActivityResultCodeChangeName);
                break;
            case R.id.buttonEditNotification:
                Intent intentEditNotification = new Intent(this, ActivityEditNameSafezone.class);
                intentEditNotification.putExtra(ActivityEditNameSafezone.passVarNotificationSafezone, safezone.getNotificationPosition());
                startActivityForResult(intentEditNotification, valueOnActivityResultCodeChangeNotifications);
                break;
            case R.id.buttonDeleteSafezone:
                boolean deleted = Safezone.delete(safezone.get_id());
                if (deleted)
                    finish();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == valueOnActivityResultCodeChangeName && resultCode == RESULT_OK) {
            //Display the modified values
            String newName = data.getExtras().getString(returnVariableNewName);
            safezone.setName(newName);
            try {
                safezone.saveSafezone(false);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Some error happened when try save safezone", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == valueOnActivityResultCodeChangeNotifications && resultCode == RESULT_OK) {
            int newNotification = data.getExtras().getInt(returnVariableNewNotification);
            safezone.setNotification(Safezone.typeNotifications[newNotification]);
            try {
                safezone.saveSafezone(false);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Some error happened when try save safezone", Toast.LENGTH_SHORT).show();
            }
        }
        refreshActivity();
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class DeviceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "I Receive a broadcast of DeviceRow ", Toast.LENGTH_SHORT).show();
            refreshActivity();
        }
    }
}
