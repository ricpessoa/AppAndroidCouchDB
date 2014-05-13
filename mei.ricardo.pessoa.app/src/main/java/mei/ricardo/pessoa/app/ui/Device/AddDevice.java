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

import java.util.ArrayList;

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
            boolean validNameDevice = false, validMacAddress=false, atLeastOneSensor = false;
            String tmpNameDevice = nameDevice.getText().toString();
            String tmpMacDevice = macAddress.getText().toString();
            String str = "";
            if(tmpNameDevice.trim().length()>0){
                str+="validName ";
                validNameDevice = true;
            }

            if(tmpMacDevice.trim().length()>0){
                str+="validMacAdress ";
                //TODO: MAC Address Expression Regular validation
                validMacAddress = true;
            }

            CheckBox checkBoxTemperature = (CheckBox) findViewById(R.id.checkBoxTemperature);
            CheckBox checkBoxGPS = (CheckBox) findViewById(R.id.checkBoxGPS);
            CheckBox checkBoxPanicButton = (CheckBox) findViewById(R.id.checkBoxPanicButton);

            str+= "checked? ";
            ArrayList<Sensor> arraySensors = new ArrayList<Sensor>();

            if (checkBoxTemperature.isChecked()) {
                str += " Temperature";
                atLeastOneSensor = true;
                SensorTemperature s = new SensorTemperature("Sensor Temperature","Temperature");
                arraySensors.add(s);
            }
            if (checkBoxGPS.isChecked()) {
                str += " GPS";
                atLeastOneSensor = true;
                Sensor s = new Sensor("Sensor GPS", "gps");
                arraySensors.add(s);
            }
            if (checkBoxPanicButton.isChecked()) {
                str += " Panic Button";
                atLeastOneSensor = true;
                Sensor s = new Sensor("Sensor Panic Button", "panic_button");
                arraySensors.add(s);
            }

            if (atLeastOneSensor && validMacAddress && validNameDevice) {
                Log.d(TAG,"Valid form =)"+str);
                Device device = new Device(macAddress.getText().toString(),nameDevice.getText().toString());
                try {
                    device.saveDevice();
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
