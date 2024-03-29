package mei.ricardo.pessoa.app.ui.MonitoringSensor;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Battery;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_GPS;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_Temperature;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.utils.Utils;

public class ActivityMonitorSensorDetail extends ActionBarActivity {
    private static String TAG = ActivityMonitorSensorDetail.class.getName();
    public static String passVariableMacAddress = "passVariableMacAddress";
    public static String passVariableTimestamp = "passVariableTimestamp";
    public static String passVariableSubtypeSensor = "passVariableSubtypeSensor";

    private String macaddress;
    private String timestamp;

    boolean ms_panicButton;
    boolean ms_shoe;

    MS_GPS ms_gps;
    MS_Temperature ms_temperature;
    MS_Battery ms_battery;


    private ImageView imageView_ms_monitor_sensor;
    private TextView textViewLocationAddress;
    private TextView textViewLocationCoordinators;
    private TextView textViewTemperatureValue;
    private TextView textViewBatteryValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_sensor_detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }

        macaddress = bundle.getString(passVariableMacAddress);
        timestamp = bundle.getString(passVariableTimestamp);
        String subtype = bundle.getString(passVariableSubtypeSensor);

        TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText(textViewTitle.getText() + " " + Device.getDeviceByID(macaddress).getNameOrMacAdress());
        TextView textViewTimestamp = (TextView) findViewById(R.id.textViewTimestamp);
        textViewTimestamp.setText(Utils.ConvertTimestampToDateFormat(timestamp));

        if (subtype != null && subtype.equals(MonitorSensor.SUBTYPE.panic_button.toString())) {
            //show all information
            ms_panicButton = true;
            getAllTypeOfMonitoringToShow();
        } else if (subtype != null && subtype.equals(MonitorSensor.SUBTYPE.shoe.toString())) {
            ms_shoe = true;
            getAllTypeOfMonitoringToShow();
        } else if (subtype != null && subtype.equals(MonitorSensor.SUBTYPE.GPS.toString())) {
            //show onlyGPS
            ms_gps = MS_GPS.getSensorGPSByMacAddressTimestamp(macaddress, timestamp, 1).get(0);
        } else if (subtype != null && subtype.equals(MonitorSensor.SUBTYPE.temperature.toString())) {
            ms_temperature = MS_Temperature.getSensorTemperatureByMacAddressTimestamp(macaddress, timestamp, 1).get(0);
        } else {
            //Battery
            ms_battery = MS_Battery.getSensorBatteryByMacAddressTimestamp(macaddress, timestamp, 1).get(0);
        }
    }

    private void getAllTypeOfMonitoringToShow() {
        try {
            ms_gps = MS_GPS.getSensorGPSByMacAddressTimestamp(macaddress, timestamp, 1).get(0);
        } catch (IndexOutOfBoundsException ex) {
            Log.d(TAG, "not exit any sensor GPS");
        }
        try {
            ms_temperature = MS_Temperature.getSensorTemperatureByMacAddressTimestamp(macaddress, timestamp, 1).get(0);
        } catch (IndexOutOfBoundsException ex) {
            Log.d(TAG, "not exit any sensor Temperature");
        }
        try {
            ms_battery = MS_Battery.getSensorBatteryByMacAddressTimestamp(macaddress, timestamp, 1).get(0);
        } catch (IndexOutOfBoundsException ex) {
            Log.d(TAG, "not exit any sensor Battery");
        }
    }

//    private void initView() {
//        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
//        imageView_ms_monitor_sensor = (ImageView) findViewById(R.id.imageView_ms_monitor_sensor);
//        if (ms_panicButton) {
//            textViewTitle.setText(Device.devicesTypesString[0] + " Details");
//            imageView_ms_monitor_sensor.setImageDrawable(Application.getmContext().getResources().getDrawable(R.drawable.ic_notification_danger));
//        } else if (ms_shoe) {
//            textViewTitle.setText(Device.devicesTypesString[4] + " Details");
//            imageView_ms_monitor_sensor.setImageDrawable(Application.getmContext().getResources().getDrawable(R.drawable.ic_notification_danger));
//        } else if (ms_gps != null) {
//            textViewTitle.setText(Device.devicesTypesString[1] + " Details");
//            imageView_ms_monitor_sensor.setImageDrawable(ms_gps.getImage());
//        } else if (ms_temperature != null) {
//            textViewTitle.setText(Device.devicesTypesString[2] + " Details");
//            imageView_ms_monitor_sensor.setImageDrawable(ms_temperature.getImage());
//        } else if (ms_battery != null) {
//            textViewTitle.setText(Device.devicesTypesString[3] + " Details");
//            imageView_ms_monitor_sensor.setImageDrawable(ms_battery.getImage());
//        }
//        textViewTimestamp = (TextView) findViewById(R.id.textViewTimestamp);
//        textViewTimestamp.setText("on " + Utils.ConvertTimestampToDateFormat(timestamp));
//
//        //GPS
//        textViewLocationAddress = (TextView) findViewById(R.id.textViewLocationAddress);
//        textViewLocationCoordinators = (TextView) findViewById(R.id.textViewLocationCoordinators);
//        Button buttonGoToDirection = (Button) findViewById(R.id.buttonGoToDirection);
//        //mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment));
//        RelativeLayout relativeLayoutMap = (RelativeLayout) findViewById(R.id.relativeLayoutMap);
//        if (ms_gps == null) {
//            textViewLocationAddress.setVisibility(View.GONE);
//            textViewLocationCoordinators.setVisibility(View.GONE);
//            relativeLayoutMap.setVisibility(View.GONE);
//            buttonGoToDirection.setVisibility(View.GONE);
//        } else {
//            initilizeMap(ms_gps);
//            textViewLocationAddress.setText("Location Address: " + ms_gps.getAddress());
//            textViewLocationCoordinators.setText("Location Coordinators: (" + ms_gps.getLatitude() + "," + ms_gps.getLongitude() + ")");
//            buttonGoToDirection.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                            Uri.parse("http://maps.google.com/maps?f=d&daddr=" + ms_gps.getLatitude() + "," + ms_gps.getLongitude()));
//                    startActivity(intent);
//                }
//            });
//        }
//        //temperature
//        textViewTemperatureValue = (TextView) findViewById(R.id.textViewTemperatureValue);
//        if (ms_temperature == null) {
//            textViewTemperatureValue.setVisibility(View.GONE);
//        } else {
//            textViewTemperatureValue.setText("Temperature value: " + ms_temperature.getValue() + "ºC");
//        }
//        //battery
//        textViewBatteryValue = (TextView) findViewById(R.id.textViewBatteryValue);
//        if (ms_battery == null) {
//            textViewBatteryValue.setVisibility(View.GONE);
//        } else {
//            textViewBatteryValue.setText("Battery level: " + ms_battery.getValue() + "%");
//        }
//    }


    public MS_GPS getMs_gps() {
        return ms_gps;
    }

    public boolean isMs_panicButton() {
        return ms_panicButton;
    }

    public MS_Temperature getMs_temperature() {
        return ms_temperature;
    }

    public MS_Battery getMs_battery() {
        return ms_battery;
    }

    public boolean isMs_shoe() {
        return ms_shoe;
    }
}
