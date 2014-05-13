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
import java.util.Iterator;
import java.util.List;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.couchdb.CouchDB;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.ui.Navigation.MainActivity;
import mei.ricardo.pessoa.app.R;

public class FragmentMyDevices extends Fragment {
    public static final String notify = "mei.ricardo.pessoa.app.notify.devices";
    private static String TAG = FragmentMyDevices.class.getName();
    private TextView mDevices;

    private ListView listViewDevices;
    DeviceListAdapter deviceListAdapter;

    private DeviceBroadcastReceiver deviceBroadcastReceiver = null;

    public FragmentMyDevices() {
    }

    @Override
    public void onPause() {
        super.onPause();
        if (deviceBroadcastReceiver != null)
            getActivity().unregisterReceiver(deviceBroadcastReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceBroadcastReceiver = new DeviceBroadcastReceiver();
        getActivity().registerReceiver(deviceBroadcastReceiver, new IntentFilter(notify));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_devices, container, false);

        listViewDevices = (ListView) rootView.findViewById(R.id.listview_device);

        deviceListAdapter = new DeviceListAdapter();

        //ListView codeLearnLessons = (ListView)findViewById(R.id.listView1);
        listViewDevices.setAdapter(deviceListAdapter);

        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                DeviceRow deviceRow = deviceListAdapter.getCodeLearnChapter(arg2);

                Toast.makeText(Application.getmContext(), deviceRow.deviceName, Toast.LENGTH_LONG).show();

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }

    private class DeviceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //float temp= intent.getFloatExtra(VAR_NAME,0);
            Toast.makeText(context, "I Receive a broadcast of devices ", Toast.LENGTH_SHORT).show();
            //mDevices.setText(mDevices.getText().toString() + " +1 ");
            getDevicesOnCouchDB();
        }
    }

    public class DeviceRow {
        String deviceName;
        String deviceDescription;
    }

    public class DeviceListAdapter extends BaseAdapter {

        List<DeviceRow> deviceList = getDevicesOnCouchDB();

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return deviceList.size();
        }

        @Override
        public DeviceRow getItem(int arg0) {
            // TODO Auto-generated method stub
            return deviceList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_view_with_simple_adapter, menu);
        return true;
    }*/

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
                String nameDevice = row.getDocument().getProperty("name_device").toString();
                if (nameDevice == null || nameDevice.equals("")) {
                    nameDevice = row.getDocumentId();
                }
                deviceRow.deviceName = "Device " + nameDevice;
                deviceRow.deviceDescription = "description device TODO ";
                deviceRowsList.add(deviceRow);

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return deviceRowsList;
    }

}