package mei.ricardo.pessoa.app.ui.Sensor.Battery;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.utils.DialogFragmentYesNoOk;
import mei.ricardo.pessoa.app.ui.Sensor.ActivityListSensors;

public class ActivityBattery extends ActionBarActivity {
    private static String TAG = ActivityBattery.class.getCanonicalName();
    public static String varPassCriticalBattery = "varPassCriticalBattery";
    public static String varPassLowBattery = "varPassLowBattery";
    private float lowBattery, criticalBattery;

    EditText editTextNumberCritical, editTextNumberLow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_settings);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null)
            return;

        lowBattery = bundle.getFloat(varPassLowBattery, 0);
        criticalBattery = bundle.getFloat(varPassCriticalBattery, 0);

        editTextNumberLow = (EditText) findViewById(R.id.editTextNumberLow);
        editTextNumberCritical = (EditText) findViewById(R.id.editTextNumberCritical);

        editTextNumberLow.setText((int) lowBattery + "");
        editTextNumberCritical.setText((int) criticalBattery + "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_edit_safezone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveSafezone:
                int batLow, batCritical;
                try {
                    batLow = Integer.parseInt(editTextNumberLow.getText().toString());
                    batCritical = Integer.parseInt(editTextNumberCritical.getText().toString());
                    if (batLow < batCritical) {
                        showDialogFragment();
                        Log.e(TAG, "Battery error in low or critical");
                    } else {
                        if (batCritical != (int) criticalBattery || batLow != (int) lowBattery) {
                            //Toast.makeText(getApplicationContext(), "Some change in battery", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), ActivityListSensors.class);
                            intent.putExtra(varPassLowBattery, batLow);
                            intent.putExtra(varPassCriticalBattery, batCritical);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            //Toast.makeText(getApplicationContext(), "NONE", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Battery error in NONE");
                        }
                    }
                } catch (NumberFormatException ex) {
                    showDialogFragment();
                    Log.e(TAG, "Error in number parser");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogFragment() {
        DialogFragmentYesNoOk dialogFragmentYesNoOk = new DialogFragmentYesNoOk(getApplicationContext(), getResources().getString(R.string.str_title_information_dialog), getResources().getString(R.string.str_error_battery_range));
        dialogFragmentYesNoOk.setType(1);
        dialogFragmentYesNoOk.setPositiveAndNegative(getString(R.string.fire_ok), "");
        dialogFragmentYesNoOk.setBackToPreviousActivity(true);
        dialogFragmentYesNoOk.show(getFragmentManager(), "");
    }
}
