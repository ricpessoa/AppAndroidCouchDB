package mei.ricardo.pessoa.app.ui.Sensor;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentButtonPanic;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentSafezone;
import mei.ricardo.pessoa.app.ui.Fragments.FragmentTemperature;

public class ActivitySensors extends ActionBarActivity {
    private static String TAG = ActivitySensors.class.getName();
    public static String var_pass_id_sensor = "var_pass_id_sensor";
    private static FragmentButtonPanic fp = null;
    private static FragmentSafezone fs = null;
    private static FragmentTemperature ft = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        Bundle bundle = getIntent().getExtras();
        String ID = bundle.getString(var_pass_id_sensor);
        Toast.makeText(this, "received id document " + ID, Toast.LENGTH_SHORT).show();
        HashMap<String, Object> sensorHashMap = Device.getSensorsByDeviceID(ID);
        boolean showPanicButton, showSafezone, showTemperature;
        showPanicButton = showSafezone = showTemperature = false;

        for (Map.Entry<String, Object> entry : sensorHashMap.entrySet()) {
            String key = entry.getKey();
            HashMap<Integer, String> value = new HashMap<Integer, String>();
            try {
                value = (HashMap<Integer, String>) entry.getValue();
            } catch (ClassCastException ex) {
                Log.d(TAG, "value -> error");
            }

            Log.d(TAG, value.get("type"));
            String type = value.get("type");

            if (type.equals(Device.deviceTypes.panic_button.toString()))
                showPanicButton = true;
            else if (type.equals(Device.deviceTypes.GPS.toString()))
                showSafezone = true;
            else if (type.equals(Device.deviceTypes.temperature.toString()))
                showTemperature = true;
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (showPanicButton) {
            if (fp == null) {
                fp = new FragmentButtonPanic();
                fragmentTransaction.add(R.id.myFragmentPanicButton, fp);
            }
        }
        if (showSafezone) {
            if (fs == null) {
                fs = new FragmentSafezone();
                fragmentTransaction.add(R.id.myFragmentSafezone, fs);
            }
        }
        if (showTemperature) {
            if (ft == null) {
                ft = new FragmentTemperature();
                fragmentTransaction.add(R.id.myFragmentTemperature, ft);
            }
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fp =null;
        fs =null;
        ft = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_sensors, menu);
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
