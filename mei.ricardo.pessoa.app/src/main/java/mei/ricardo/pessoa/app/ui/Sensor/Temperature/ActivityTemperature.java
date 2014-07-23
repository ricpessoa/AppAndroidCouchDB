package mei.ricardo.pessoa.app.ui.Sensor.Temperature;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.ui.Fragments.Utils.DialogFragmentYesNoOk;
import mei.ricardo.pessoa.app.ui.Sensor.ActivityListSensors;

public class ActivityTemperature extends ActionBarActivity {
    private static String TAG = ActivityTemperature.class.getCanonicalName();
    public static String varPassMinimumTemperature = "varPassMinimumTemperature";
    public static String varPassMaximumTemperature = "varPassMaximumTemperature";

    private String mac_address;
    private float minTemperature, maxTemperature;

    EditText editTextNumberMax, editTextNumberMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;

        mac_address = bundle.getString(ActivityListSensors.varMacAddressOfDevice);
        minTemperature = bundle.getFloat(varPassMinimumTemperature, 0);
        maxTemperature = bundle.getFloat(varPassMaximumTemperature, 0);
        //Device device = Device.getDeviceByID(mac_address);
        // Toast.makeText(this, "Device " + device.getArrayListSensors().size(), Toast.LENGTH_SHORT).show();
        editTextNumberMin = (EditText) findViewById(R.id.editTextNumberMinimum);
        editTextNumberMax = (EditText) findViewById(R.id.editTextNumberMaximum);

        editTextNumberMin.setText((int) minTemperature + "");
        editTextNumberMax.setText((int) maxTemperature + "");

        ((Button) findViewById(R.id.buttonSaveTemperature)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int tempMin, tempMax;
                try {
                    tempMin = Integer.parseInt(editTextNumberMin.getText().toString());
                    tempMax = Integer.parseInt(editTextNumberMax.getText().toString());
                    if (tempMin > tempMax) {
                        showDialogFragment();
                        Log.d(TAG, "error in max or min");
                    } else {
                        if (tempMax != (int) maxTemperature || tempMin != (int) minTemperature) {
                            Toast.makeText(getApplicationContext(), "Some change in temperature", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ActivityListSensors.class);
                            intent.putExtra(varPassMinimumTemperature, tempMin);
                            intent.putExtra(varPassMaximumTemperature, tempMax);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "NONE", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (NumberFormatException ex) {
                    showDialogFragment();
                    Log.e(TAG, "Error in number parser");
                }


            }
        });

    }

    private void showDialogFragment() {
        DialogFragmentYesNoOk dialogFragmentYesNoOk = new DialogFragmentYesNoOk(getApplicationContext(), "The temperature range have an error, please set a valid range.");
        dialogFragmentYesNoOk.setType(1);
        dialogFragmentYesNoOk.setPositiveAndNegative(getString(R.string.fire_ok), "");
        dialogFragmentYesNoOk.setBackToPreviousActivity(true);
        dialogFragmentYesNoOk.show(getFragmentManager(), "");
    }

}
