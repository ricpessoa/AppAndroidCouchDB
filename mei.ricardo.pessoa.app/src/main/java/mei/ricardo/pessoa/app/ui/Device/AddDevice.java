package mei.ricardo.pessoa.app.ui.Device;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Status;

import java.util.ArrayList;
import java.util.HashMap;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Sensor;
import mei.ricardo.pessoa.app.couchdb.modal.SensorTemperature;

public class AddDevice extends ActionBarActivity implements View.OnClickListener {
    private static String TAG = AddDevice.class.getName();

    private EditText nameDevice, macAddress;
    private Button saveDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        nameDevice = (EditText) findViewById(R.id.name_device);
        macAddress = (EditText) findViewById(R.id.mac_address_device);

        saveDevice = (Button) findViewById(R.id.button_save_device);
        saveDevice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == saveDevice.getId()) {
            boolean validNameDevice = false, validMacAddress = false, atLeastOneSensor = false;
            String tmpNameDevice = nameDevice.getText().toString();
            String tmpMacDevice = macAddress.getText().toString();
            if (tmpNameDevice.trim().length() > 0) {
                validNameDevice = true;
            }

            if (tmpMacDevice.trim().length() > 0) {
                //TODO: MAC Address Expression Regular validation
                validMacAddress = true;
            }

            CheckBox checkBoxTemperature = (CheckBox) findViewById(R.id.checkBoxTemperature);
            CheckBox checkBoxGPS = (CheckBox) findViewById(R.id.checkBoxGPS);
            CheckBox checkBoxPanicButton = (CheckBox) findViewById(R.id.checkBoxPanicButton);

            HashMap<Integer, Sensor> arraySensors = new HashMap<Integer, Sensor>();

            if (checkBoxPanicButton.isChecked()) {
                atLeastOneSensor = true;
                Sensor s = new Sensor("Sensor Panic Button", "panic_button");
                arraySensors.put(arraySensors.size(),s);
            }

            if (checkBoxGPS.isChecked()) {
                atLeastOneSensor = true;
                Sensor s = new Sensor("Sensor GPS", "GPS");
                arraySensors.put(arraySensors.size(),s);
            }

            if (checkBoxTemperature.isChecked()) {
                atLeastOneSensor = true;
                SensorTemperature s = new SensorTemperature("Sensor Temperature", "temperature");
                arraySensors.put(arraySensors.size(),s);
            }

            if (atLeastOneSensor && validMacAddress && validNameDevice) {
                Device device = new Device(macAddress.getText().toString(), nameDevice.getText().toString(), arraySensors);
                try {
                    device.saveDevice();
                } catch (CouchbaseLiteException e) {
                    if (e.getCBLStatus().getCode() == Status.CONFLICT) {
                        Log.e(TAG, "Error Conflict try save device");
                    } else if (e.getCBLStatus().getCode() == Status.CREATED) {
                        Log.e(TAG, "Error try create device");
                    } else {
                        Log.e(TAG, "Error :S");
                    }
                    e.printStackTrace();
                }
            }
        }
    }
}
