package mei.ricardo.pessoa.app.ui.MonitoringSensor;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import mei.ricardo.pessoa.app.R;

public class ActivityMonitorSensorPanicButton extends ActionBarActivity {
    private static String TAG = ActivityMonitorSensorPanicButton.class.getName();
    public static String passVariable = "panic_button_id";
    private String macAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_sensor_panic_button);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        macAddress = getIntent().getExtras().getString(passVariable);
        Log.d(TAG,"received the macaddress:"+macAddress);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.monitor_sensor_panic_button, menu);
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
