package mei.ricardo.pessoa.app.ui.MonitoringSensor;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_GPS;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Temperature;

public class ActivityMonitorSensorPanicButton extends ActionBarActivity {
    private static String TAG = ActivityMonitorSensorPanicButton.class.getName();
    public static String passVariableMacAddress = "panic_button_id";
    public static String passVariableTimestamp = "gps_timestamp";
    private String macAddress;
    private String timestamp;
    private ImageView imageViewMap;
    private TextView textViewTitle;
    private TextView textViewLocationAddress;
    private TextView textViewLocationCoordinators;
    private TextView textViewTemperatureValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_sensor_panic_button);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        macAddress = getIntent().getExtras().getString(passVariableMacAddress);
        timestamp = getIntent().getExtras().getString(passVariableTimestamp);
        Log.d(TAG, "received the macaddress:" + macAddress + " whith timestamp" + timestamp);

        ArrayList<MS_GPS> ms_gpsArrayList = MS_GPS.getSensorGPSByMacAddressTimestamp(macAddress, timestamp, 1);
        ArrayList<MS_Temperature> ms_temperatureArrayList = MS_Temperature.getSensorTemperatureByMacAddressTimestamp(macAddress, timestamp, 1);

        Log.d(TAG, ms_gpsArrayList.toString() + " - " + ms_temperatureArrayList.toString());

        imageViewMap = (ImageView) findViewById(R.id.imageViewMap);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewLocationAddress = (TextView) findViewById(R.id.textViewLocationAddress);
        textViewLocationCoordinators = (TextView) findViewById(R.id.textViewLocationCoordinators);

        if (ms_gpsArrayList.size() > 0) {
            MS_GPS ms_gps = ms_gpsArrayList.get(0);
            textViewLocationAddress.setText("Location Address: " + ms_gps.getAddress());
            textViewLocationCoordinators.setText("Location Coordinators: (" + ms_gps.getLatitude() + "," + ms_gps.getLongitude() + ")");
        } else {
            textViewLocationAddress.setVisibility(View.GONE);
            textViewLocationCoordinators.setVisibility(View.GONE);
        }

        textViewTemperatureValue = (TextView) findViewById(R.id.textViewTemperatureValue);

        if (ms_temperatureArrayList.size() > 0) {
            MS_Temperature ms_temperature = ms_temperatureArrayList.get(0);
            textViewTemperatureValue.setText("Temperature value: " + ms_temperature.getValue() + "ºC");
        } else {
            textViewTemperatureValue.setVisibility(View.GONE);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.monitor_sensor_panic_button, menu);
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
}
