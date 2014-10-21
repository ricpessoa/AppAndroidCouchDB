package mei.ricardo.pessoa.app.ui.Sensor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;

import java.util.ArrayList;
import java.util.List;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Sensor;
import mei.ricardo.pessoa.app.couchdb.modal.SensorBattery;
import mei.ricardo.pessoa.app.couchdb.modal.SensorTemperature;
import mei.ricardo.pessoa.app.ui.Sensor.Battery.ActivityBattery;
import mei.ricardo.pessoa.app.ui.Sensor.Safezone.ActivityListSafezones;
import mei.ricardo.pessoa.app.ui.Sensor.Temperature.ActivityTemperature;

public class ActivityListSensors extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener {
    private static String TAG = ActivityListSensors.class.getName();
    public static String var_pass_id_sensor = "var_pass_id_sensor";
    public static String varMacAddressOfDevice = "varMacAddressOfDevice";
    private static Device mDevice;

    static final int valueOnActivityResultCodeTemperature = 1;
    static final int valueOnActivityResultCodeBattery = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sensors);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Switch mSwitchMonitoringDevice = (Switch) findViewById(R.id.switchMonitoringDevice);
        mSwitchMonitoringDevice.setOnCheckedChangeListener(this);

        ListView mListViewSensors = (ListView) findViewById(R.id.listViewSensors);

        Bundle bundle = getIntent().getExtras();
        String ID = bundle.getString(var_pass_id_sensor);
        //Toast.makeText(this, "received id document " + ID, Toast.LENGTH_SHORT).show();
        mDevice = Device.getDeviceByID(ID);
        setTitle(mDevice.getName_device() + " " + getTitle());

        SensorListAdapter adapterSensor = new SensorListAdapter(mDevice.getArrayListSensors());
        mListViewSensors.setAdapter(adapterSensor);

        mListViewSensors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mDevice.getArrayListSensors() != null) {
                    Sensor sensor = mDevice.getArrayListSensors().get(i);
                    if (sensor.getType() == Device.DEVICESTYPE.GPS.toString()) {
                        Intent intent = new Intent(getApplicationContext(), ActivityListSafezones.class);
                        intent.putExtra(varMacAddressOfDevice, mDevice.getMac_address());
                        startActivity(intent);
                    } else if (sensor.getType() == Device.DEVICESTYPE.temperature.toString()) {
                        Intent intent = new Intent(getApplicationContext(), ActivityTemperature.class);
                        SensorTemperature sensorTemperature = (SensorTemperature) sensor;
                        intent.putExtra(varMacAddressOfDevice, mDevice.getMac_address());
                        intent.putExtra(ActivityTemperature.varPassMinimumTemperature, sensorTemperature.getMin_temperature());
                        intent.putExtra(ActivityTemperature.varPassMaximumTemperature, sensorTemperature.getMax_temperature());
                        startActivityForResult(intent, valueOnActivityResultCodeTemperature);
                    } else if (sensor.getType() == Device.DEVICESTYPE.battery.toString()) {
                        Intent intent = new Intent(getApplicationContext(), ActivityBattery.class);
                        SensorBattery sensorBattery = (SensorBattery) sensor;
                        intent.putExtra(varMacAddressOfDevice, mDevice.getMac_address());
                        intent.putExtra(ActivityBattery.varPassLowBattery, sensorBattery.getLow_battery());
                        intent.putExtra(ActivityBattery.varPassCriticalBattery, sensorBattery.getCritical_battery());
                        startActivityForResult(intent, valueOnActivityResultCodeBattery);
                    } else {
                        Toast.makeText(getApplicationContext(), mDevice.getArrayListSensors().get(i).getName_sensor() + " don't have settings", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "The sensor" + mDevice.getArrayListSensors().get(i).getType() + " don't have settings");
                    }
                }

            }
        });
        mSwitchMonitoringDevice.setChecked(mDevice.isMonitoring());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mDevice.setMonitoring(isChecked);
        try {
            if (mDevice.isMonitoring() != isChecked)
                mDevice.saveDevice(false);
            Toast.makeText(this, "Successfully Monitoring Device changes", Toast.LENGTH_SHORT);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, "Some error trying change Monitoring Device", Toast.LENGTH_SHORT);
            mDevice.setMonitoring(!mDevice.isMonitoring());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //ArrayList<Sensor> arrayList = mDevice.getArrayListSensors();
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();

            if (requestCode == valueOnActivityResultCodeTemperature) {
                if (bundle != null) {
                    Integer index = mDevice.getIndexOfSensorBasedInType(Device.DEVICESTYPE.temperature.toString());
                    if (index == null)
                        return; // WTF now exist???
                    int minTemperature = bundle.getInt(ActivityTemperature.varPassMinimumTemperature);
                    int maxTemperature = bundle.getInt(ActivityTemperature.varPassMaximumTemperature);
                    SensorTemperature sensorTemperature = (SensorTemperature) mDevice.getArrayListSensors().get(index);
                    sensorTemperature.setMin_temperature(minTemperature);
                    sensorTemperature.setMax_temperature(maxTemperature);
                    //Toast.makeText(this, "Receive something from temperature " + minTemperature + " " + maxTemperature, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == valueOnActivityResultCodeBattery) {
                if (bundle != null) {
                    Integer index = mDevice.getIndexOfSensorBasedInType(Device.DEVICESTYPE.battery.toString());
                    if (index == null)
                        return; // WTF now exist???
                    int lowBattery = bundle.getInt(ActivityBattery.varPassLowBattery);
                    int criticalBattery = bundle.getInt(ActivityBattery.varPassCriticalBattery);
                    SensorBattery sensorBattery = (SensorBattery) mDevice.getArrayListSensors().get(index);
                    sensorBattery.setLow_battery(lowBattery);
                    sensorBattery.setCritical_battery(criticalBattery);
                    //Toast.makeText(this, "Receive something from battery " + lowBattery + " " + criticalBattery, Toast.LENGTH_SHORT).show();
                }
            }
            try {
                //mDevice.setArrayListSensors(arrayList);
                mDevice.saveDevice(true);
                Toast.makeText(this, getString(R.string.str_list_sensor_successful), Toast.LENGTH_SHORT).show();
            } catch (CouchbaseLiteException e) {
                Log.d(TAG, "device: " + mDevice.getMac_address() + " error trying save");
                Toast.makeText(this, getString(R.string.str_list_sensor_error), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public class SensorListAdapter extends BaseAdapter {
        List<Sensor> sensor;

        public SensorListAdapter(ArrayList<Sensor> deviceList) {
            this.sensor = deviceList;
        }

        public void updateDeviceList(List<Sensor> results) {
            sensor = results;
            //Triggers the list update
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return sensor.size();
        }

        @Override
        public Sensor getItem(int arg0) {
            return sensor.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {

            if (arg1 == null) {
                LayoutInflater inflater = (LayoutInflater) Application.getmContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                arg1 = inflater.inflate(R.layout.list_item_entry, arg2, false);
            }

            TextView chapterName = (TextView) arg1.findViewById(R.id.deviceName);
            ImageView imageView = (ImageView) arg1.findViewById(R.id.icon);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_sensor));
            Sensor sensorRow = sensor.get(arg0);

            chapterName.setText(sensorRow.getName_sensor());

            return arg1;
        }
    }
}
