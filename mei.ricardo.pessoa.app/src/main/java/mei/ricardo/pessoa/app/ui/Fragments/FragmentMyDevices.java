package mei.ricardo.pessoa.app.ui.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.couchdb.CouchDB;
import mei.ricardo.pessoa.app.ui.Device.AddDevice;
import mei.ricardo.pessoa.app.ui.Navigation.MainActivity;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.ui.Sensor.ActivitySensors;

public class FragmentMyDevices extends Fragment {
    public static final String notify = "mei.ricardo.pessoa.app.notify.devices";
    private static String TAG = FragmentMyDevices.class.getName();

    private static ListView listViewDevices;
    private static DeviceListAdapter deviceListAdapter;

    private DeviceBroadcastReceiver deviceBroadcastReceiver = null;

    public FragmentMyDevices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_devices, container, false);

        setHasOptionsMenu(true);

        listViewDevices = (ListView) rootView.findViewById(R.id.listview_device);

        deviceListAdapter = new DeviceListAdapter();

        listViewDevices.setAdapter(deviceListAdapter);

        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                DeviceRow deviceRow = deviceListAdapter.getCodeLearnChapter(arg2);
                Intent intent = new Intent(getActivity(), ActivitySensors.class);
                intent.putExtra(ActivitySensors.var_pass_id_sensor,deviceRow.deviceID);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        deviceBroadcastReceiver = new DeviceBroadcastReceiver();
        getActivity().registerReceiver(deviceBroadcastReceiver, new IntentFilter(notify));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (deviceBroadcastReceiver != null)
            getActivity().unregisterReceiver(deviceBroadcastReceiver);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_my_devices, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_device) {
            Intent intent = new Intent(getActivity(), AddDevice.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private class DeviceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "I Receive a broadcast of devices ", Toast.LENGTH_SHORT).show();
            deviceListAdapter.updateDeviceList(getDevicesOnCouchDB());
        }
    }

    public class DeviceRow {
        String deviceID;
        String deviceName;
        String deviceDescription;
    }

    public class DeviceListAdapter extends BaseAdapter {

        List<DeviceRow> deviceList = getDevicesOnCouchDB();

        public void updateDeviceList(List<DeviceRow> results) {
            deviceList = results;
            //Triggers the list update
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public DeviceRow getItem(int arg0) {
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
                arg1 = inflater.inflate(R.layout.fragment_my_device_item_list, arg2, false);
            }

            TextView chapterName = (TextView) arg1.findViewById(R.id.deviceName);
            TextView chapterDesc = (TextView) arg1.findViewById(R.id.deviceDescription);

            DeviceRow deviceRow = deviceList.get(arg0);

            chapterName.setText(deviceRow.deviceName);
            chapterDesc.setText(deviceRow.deviceDescription);

            return arg1;
        }

        public DeviceRow getCodeLearnChapter(int position) {
            return deviceList.get(position);
        }

    }

    private List<DeviceRow> getDevicesOnCouchDB() {
        List<DeviceRow> deviceRowsList = new ArrayList<DeviceRow>();

        com.couchbase.lite.View view = CouchDB.viewGetDevices;
        Query query = view.createQuery();
        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
                QueryRow row = it.next();

                DeviceRow deviceRow = new DeviceRow();
                Log.d("Document ID:", row.getDocumentId());
                deviceRow.deviceID = row.getDocumentId();
                String nameDevice = "";
                HashMap<Object, Object> numbSensors = new HashMap<Object, Object>();

                try {
                    nameDevice = row.getDocument().getProperty("name_device").toString();
                    numbSensors = (HashMap<Object, Object>) row.getDocument().getProperty("sensors");
                } catch (NullPointerException ex) {
                    nameDevice = "Device " + row.getDocumentId();
                } catch (Exception ex) {
                    Log.e(TAG, "Error in Device _id:" + nameDevice);
                }
                deviceRow.deviceName = nameDevice;
                deviceRow.deviceDescription = "Number of Sensors " + numbSensors.size();
                deviceRowsList.add(deviceRow);

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return deviceRowsList;
    }

}