package mei.ricardo.pessoa.app.ui.MonitoringSensor;

import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Battery;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_GPS;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Temperature;

public class ActivityMonitorSensorPanicButton extends ActionBarActivity {
    private static String TAG = ActivityMonitorSensorPanicButton.class.getName();
    public static String passVariableMacAddress = "panic_button_id";
    public static String passVariableTimestamp = "gps_timestamp";
    private String macAddress;
    private String timestamp;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private TextView textViewTitle;
    private TextView textViewLocationAddress;
    private TextView textViewLocationCoordinators;
    private TextView textViewTemperatureValue;
    private TextView textViewBatteryValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_sensor_panic_button);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        macAddress = getIntent().getExtras().getString(passVariableMacAddress);
        timestamp = getIntent().getExtras().getString(passVariableTimestamp);
        //Log.d(TAG, "received the macaddress:" + macAddress + " whith timestamp" + timestamp);

        ArrayList<MS_GPS> ms_gpsArrayList = MS_GPS.getSensorGPSByMacAddressTimestamp(macAddress, timestamp, 1);
        ArrayList<MS_Temperature> ms_temperatureArrayList = MS_Temperature.getSensorTemperatureByMacAddressTimestamp(macAddress, timestamp, 1);
        ArrayList<MS_Battery> ms_batteryArrayList = MS_Battery.getSensorBatteryByMacAddressTimestamp(macAddress, timestamp, 1);


        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewLocationAddress = (TextView) findViewById(R.id.textViewLocationAddress);
        textViewLocationCoordinators = (TextView) findViewById(R.id.textViewLocationCoordinators);
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        Button buttonGoToDirection = (Button) findViewById(R.id.buttonGoToDirection);

        if (ms_gpsArrayList.size() > 0) {
            final MS_GPS ms_gps = ms_gpsArrayList.get(0);
            initilizeMap(ms_gps);
            textViewLocationAddress.setText("Location Address: " + ms_gps.getAddress());
            textViewLocationCoordinators.setText("Location Coordinators: (" + ms_gps.getLatitude() + "," + ms_gps.getLongitude() + ")");
            buttonGoToDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?f=d&daddr="+ms_gps.getLatitude()+","+ms_gps.getLongitude()));
                    startActivity(intent);
                }
            });
        } else {
            textViewLocationAddress.setVisibility(View.GONE);
            textViewLocationCoordinators.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.GONE);
            buttonGoToDirection.setVisibility(View.GONE);
        }

        textViewTemperatureValue = (TextView) findViewById(R.id.textViewTemperatureValue);

        if (ms_temperatureArrayList.size() > 0) {
            MS_Temperature ms_temperature = ms_temperatureArrayList.get(0);
            textViewTemperatureValue.setText("Temperature value: " + ms_temperature.getValue() + "ºC");
        } else {
            textViewTemperatureValue.setVisibility(View.GONE);

        }

        textViewBatteryValue = (TextView) findViewById(R.id.textViewBatteryValue);

        if (ms_batteryArrayList.size() > 0) {
            MS_Battery ms_battery = ms_batteryArrayList.get(0);
            textViewBatteryValue.setText("Battery level: " + ms_battery.getValue() + "%");
        } else {
            textViewBatteryValue.setVisibility(View.GONE);
        }
    }


    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap(MS_GPS ms_gps) {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            googleMap.getUiSettings().setAllGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            LatLng latLng = new LatLng(ms_gps.getLatitude(), ms_gps.getLongitude());
            //MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(ms_gps.getAddress());
            //Marker marker = googleMap.addMarker(markerOptions);
            // Move the camera instantly to hamburg with a zoom of 15.
            googleMap.addMarker(new MarkerOptions()
                    .position(latLng));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
