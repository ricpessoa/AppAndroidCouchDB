package mei.ricardo.pessoa.app.ui.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.ui.Device.AddDevice;
import mei.ricardo.pessoa.app.ui.MainActivity;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.ui.Sensor.ActivityListSensors;
import mei.ricardo.pessoa.app.utils.DeviceRow;

public class FragmentMyDevices extends Fragment {
    public static final String notify = "mei.ricardo.pessoa.app.notifyDevice.devices";
    private static String TAG = FragmentMyDevices.class.getName();

    private static ListView listViewDevices;
    private static DeviceListAdapter deviceListAdapter;

    private DeviceBroadcastReceiver deviceBroadcastReceiver = null;
    private ArrayList<DeviceRow> deviceList;
    private TextView textViewNoDevices;

    public FragmentMyDevices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceList = Device.getAllDevicesNotDeleted();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_devices, container, false);

        setHasOptionsMenu(true);

        listViewDevices = (ListView) rootView.findViewById(R.id.listview_device);

        textViewNoDevices = (TextView) rootView.findViewById(R.id.textViewNoDevices);

        deviceListAdapter = new DeviceListAdapter(deviceList);

        listViewDevices.setAdapter(deviceListAdapter);

        listViewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                DeviceRow deviceRow = deviceListAdapter.getCodeLearnChapter(arg2);
                Intent intent = new Intent(getActivity(), ActivityListSensors.class);
                intent.putExtra(ActivityListSensors.var_pass_id_sensor, deviceRow.deviceID);
                startActivity(intent);
            }
        });

        listViewDevices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                removeItemFromList(i);
                return true;
            }
        });

        if (deviceList != null && deviceList.size() == 0) {
            listViewDevices.setEmptyView(textViewNoDevices);
        }
        return rootView;
    }

    // method to remove list item
    protected void removeItemFromList(int position) {
        final int deletePosition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.str_title_delete_dialog));
        builder.setMessage(getString(R.string.str_delete_device))
                .setPositiveButton(getString(R.string.fire_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "delete device " + deletePosition, Toast.LENGTH_SHORT).show();

                        Device.deleteDevice(deviceList.get(deletePosition).deviceID);
                    }
                })
                .setNegativeButton(getString(R.string.fire_no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        // Create the AlertDialog object and return it
        builder.show();
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
            deviceListAdapter.updateDeviceList(Device.getAllDevicesNotDeleted());
        }
    }

    public class DeviceListAdapter extends BaseAdapter {
        List<DeviceRow> deviceList;

        public DeviceListAdapter(ArrayList<DeviceRow> deviceList) {
            this.deviceList = deviceList;
        }

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
                arg1 = inflater.inflate(R.layout.list_item_entry, arg2, false);
            }

            TextView chapterName = (TextView) arg1.findViewById(R.id.deviceName);
            TextView chapterDesc = (TextView) arg1.findViewById(R.id.deviceDescription);
            ImageView imageView = (ImageView) arg1.findViewById(R.id.icon);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_device));
            DeviceRow deviceRow = deviceList.get(arg0);

            chapterName.setText(deviceRow.deviceName);
            chapterDesc.setText(deviceRow.deviceDescription);


            return arg1;
        }


        public DeviceRow getCodeLearnChapter(int position) {
            return deviceList.get(position);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (deviceList.size() == 0) {
                listViewDevices.setEmptyView(textViewNoDevices);
            }
        }
    }

}