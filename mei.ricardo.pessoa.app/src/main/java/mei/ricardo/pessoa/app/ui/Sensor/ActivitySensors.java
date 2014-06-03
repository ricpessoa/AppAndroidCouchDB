package mei.ricardo.pessoa.app.ui.Sensor;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;

import java.util.Objects;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;

public class ActivitySensors extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener {
    private static String TAG = ActivitySensors.class.getName();
    public static String var_pass_id_sensor = "var_pass_id_sensor";
    private static Device mDevice;
    private Switch mSwitchMonitoringDevice;
    private ListView mListViewSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        mSwitchMonitoringDevice = (Switch) findViewById(R.id.switchMonitoringDevice);
        mSwitchMonitoringDevice.setOnCheckedChangeListener(this);

        mListViewSensors = (ListView) findViewById(R.id.listViewSensors);


        Bundle bundle = getIntent().getExtras();
        String ID = bundle.getString(var_pass_id_sensor);
        Toast.makeText(this, "received id document " + ID, Toast.LENGTH_SHORT).show();
        mDevice = Device.getDeviceByID(ID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, mDevice.getSensors());
        // Assign adapter to ListView
        mListViewSensors.setAdapter(adapter);
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
            mDevice.saveDevice();
            Toast.makeText(this,"change Monitoring Device successful",Toast.LENGTH_SHORT);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, "Some error trying change Monitoring Device", Toast.LENGTH_SHORT);
            mDevice.setMonitoring(!mDevice.isMonitoring());
        }
    }
}
