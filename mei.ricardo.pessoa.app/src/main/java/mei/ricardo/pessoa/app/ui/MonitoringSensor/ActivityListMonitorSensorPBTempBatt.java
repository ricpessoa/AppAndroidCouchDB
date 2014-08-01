package mei.ricardo.pessoa.app.ui.MonitoringSensor;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.utils.InterfaceItem;
import mei.ricardo.pessoa.app.utils.Utils;

public class ActivityListMonitorSensorPBTempBatt extends ActionBarActivity implements AdapterView.OnItemClickListener {
    private static String TAG = ActivityListMonitorSensorPBTempBatt.class.getCanonicalName();
    public static String passVariableIDOfDevice = "passVariableIDOfDevice";
    public static String passVariableTypeSensor = "passVariableTypeSensor";

    private String typeDevice;
    private String macAddressDevice;
    private ListView listViewOfMonitoringSensors;
    private ArrayList<InterfaceItem> arrayListMonitorSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_monitor_sensor_pbtemp_batt);
        Bundle bundle = getIntent().getExtras();

        if (bundle == null)
            return;

        macAddressDevice = bundle.getString(passVariableIDOfDevice);
        typeDevice = bundle.getString(passVariableTypeSensor);

        listViewOfMonitoringSensors = (ListView) findViewById(R.id.listOfMonitorSensor);

        arrayListMonitorSensor = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(macAddressDevice, typeDevice, 30);
        Log.d(TAG, "Received " + macAddressDevice + " " + typeDevice + " " + arrayListMonitorSensor);
        MonitorSensorListAdapter adapter = new MonitorSensorListAdapter(arrayListMonitorSensor);
        listViewOfMonitoringSensors.setAdapter(adapter);
        listViewOfMonitoringSensors.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public class MonitorSensorListAdapter extends BaseAdapter {
        ArrayList<InterfaceItem> deviceList = new ArrayList<InterfaceItem>();

        public MonitorSensorListAdapter(ArrayList<InterfaceItem> results) {
            deviceList = results;
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public InterfaceItem getItem(int arg0) {
            return deviceList.get(arg0);
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

            RelativeLayout relativeLayout = (RelativeLayout) arg1.findViewById(R.id.relative_layout_entry);
            relativeLayout.setBackgroundColor(Color.WHITE);

            ImageView imageView = (ImageView) arg1.findViewById(R.id.icon);
            TextView rowTitle = (TextView) arg1.findViewById(R.id.deviceName);
            TextView rowDesc = (TextView) arg1.findViewById(R.id.deviceDescription);
            MonitorSensor ms = (MonitorSensor) deviceList.get(arg0);

            rowTitle.setText(ms.getTitle());
            rowDesc.setText(Utils.ConvertTimestampToDateFormat(ms.getTimestamp()));
            imageView.setImageDrawable(ms.getImage());
            return arg1;
        }
    }
}
