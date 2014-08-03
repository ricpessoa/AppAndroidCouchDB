package mei.ricardo.pessoa.app.ui.Sensor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;

import java.util.ArrayList;
import java.util.HashMap;

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
    private Switch mSwitchMonitoringDevice;
    private ListView mListViewSensors;

    static final int valueOnActivityResultCodeTemperature = 1;
    static final int valueOnActivityResultCodeBattery = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sensors);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mSwitchMonitoringDevice = (Switch) findViewById(R.id.switchMonitoringDevice);
        mSwitchMonitoringDevice.setOnCheckedChangeListener(this);

        mListViewSensors = (ListView) findViewById(R.id.listViewSensors);
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
                        Toast.makeText(getApplicationContext(), "Sensor :" + mDevice.getArrayListSensors().get(i).getType() + " dont have settings", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        Bundle bundle = getIntent().getExtras();
        String ID = bundle.getString(var_pass_id_sensor);
        Toast.makeText(this, "received id document " + ID, Toast.LENGTH_SHORT).show();
        mDevice = Device.getDeviceByID(ID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mDevice.getSensorsToShowInArrayString());
        // Assign adapter to ListView
        mListViewSensors.setAdapter(adapter);
        mSwitchMonitoringDevice.setChecked(mDevice.isMonitoring());


//        Log.d(TAG, "o que vou fazer??? " + mDevice.getSensors());
//        HashMap<Integer, Object> hashMap = mDevice.getSensors();
//        for (Integer key : hashMap.keySet()) {
//            //Capturamos o valor a partir da chave
//            Object sensor = (Object) hashMap.get(key);
//            System.out.println(key + " = " + sensor);
//        }

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
            Toast.makeText(this, "change Monitoring Device successful", Toast.LENGTH_SHORT);
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
                    int index = 2;
                    int minTemperature = bundle.getInt(ActivityTemperature.varPassMinimumTemperature);
                    int maxTemperature = bundle.getInt(ActivityTemperature.varPassMaximumTemperature);
                    SensorTemperature sensorTemperature = (SensorTemperature) mDevice.getArrayListSensors().get(index);
                    sensorTemperature.setMin_temperature(minTemperature);
                    sensorTemperature.setMax_temperature(maxTemperature);
                    Toast.makeText(this, "Receive something from temperature " + minTemperature + " " + maxTemperature, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == valueOnActivityResultCodeBattery) {
                if (bundle != null) {
                    int index = 3;
                    int lowBattery = bundle.getInt(ActivityBattery.varPassLowBattery);
                    int criticalBattery = bundle.getInt(ActivityBattery.varPassCriticalBattery);
                    SensorBattery sensorBattery = (SensorBattery) mDevice.getArrayListSensors().get(index);
                    sensorBattery.setLow_battery(lowBattery);
                    sensorBattery.setCritical_battery(criticalBattery);
                    Toast.makeText(this, "Receive something from battery " + lowBattery + " " + criticalBattery, Toast.LENGTH_SHORT).show();
                }
            }

            try {
                //mDevice.setArrayListSensors(arrayList);
                mDevice.saveDevice(true);
            } catch (CouchbaseLiteException e) {
                Log.d(TAG, "device: " + mDevice.getMac_address() + " error trying save");
                e.printStackTrace();
            }
        }
    }
}
