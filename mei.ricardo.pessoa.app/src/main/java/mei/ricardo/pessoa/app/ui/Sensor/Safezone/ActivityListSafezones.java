package mei.ricardo.pessoa.app.ui.Sensor.Safezone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Safezone;
import mei.ricardo.pessoa.app.utils.DownloadImageTask;
import mei.ricardo.pessoa.app.ui.Sensor.ActivityListSensors;

public class ActivityListSafezones extends ActionBarActivity {
    private static String TAG = ActivityListSafezones.class.getCanonicalName();
    private String macAddress;
    /*private ArrayList<DeviceRow> safezoneArrayList;
    private ListView listViewSafezones;
    private TextView textViewNoSafezones;
    public static final String notify = "mei.ricardo.pessoa.app.ui.Sensor.DeviceRow";
    private SafezoneBroadcastReceiver safezoneBroadcastReceiver = null;
    SafezoneListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_list_safezones);

        macAddress = getIntent().getExtras().getString(ActivityListSensors.varMacAddressOfDevice);

        listViewSafezones = (ListView) findViewById(R.id.listViewSafezones);
        textViewNoSafezones = (TextView) findViewById(R.id.textViewNoSafezones);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshActivity();
        safezoneBroadcastReceiver = new SafezoneBroadcastReceiver();
        registerReceiver(safezoneBroadcastReceiver, new IntentFilter(notify));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (safezoneBroadcastReceiver != null)
            unregisterReceiver(safezoneBroadcastReceiver);
    }

    private void refreshActivity() {
        safezoneArrayList = DeviceRow.getSafezonesOfDeviceGPS(macAddress);
        adapter = new SafezoneListViewAdapter(this, safezoneArrayList);
        listViewSafezones.setAdapter(adapter);
        if (safezoneArrayList != null && safezoneArrayList.size() > 0) {
            listViewSafezones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    try {
                        DeviceRow safezone = safezoneArrayList.get(i);
                        Intent intent = new Intent(getApplicationContext(), ActivitySafezoneOptions.class);
                        intent.putExtra(ActivitySafezoneOptions.passVarIDSafezone, safezone.get_id());
                        startActivity(intent);
                    } catch (IndexOutOfBoundsException ex) {
                        refreshActivity();
                    }
                }
            });
        } else {
            listViewSafezones.setEmptyView(textViewNoSafezones);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_safezones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_addSafezone) {
            Intent intent = new Intent(this, ActivitySafezoneEditMap.class);
            intent.putExtra(ActivityListSensors.varMacAddressOfDevice, macAddress);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SafezoneListViewAdapter extends ArrayAdapter<DeviceRow> {
        private Context context;
        public ArrayList<DeviceRow> safezoneArrayList;
        private LayoutInflater vi;
        private int color = Color.TRANSPARENT;

        public SafezoneListViewAdapter(Context context, ArrayList<DeviceRow> items) {
            super(context, 0, items);
            this.context = context;
            this.safezoneArrayList = items;
            vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void updateSafezoneList(ArrayList<DeviceRow> results) {
            safezoneArrayList = results;
            //Triggers the list update
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {

                final DeviceRow i = safezoneArrayList.get(position);
                if (i != null) {
                    DeviceRow safezone = (DeviceRow) i;
                    v = vi.inflate(R.layout.list_item_entry, null);
                    RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.relative_layout_entry);
                    relativeLayout.setBackgroundColor(color);

                    final ImageView imageView = (ImageView) v.findViewById(R.id.icon);
                    final TextView title = (TextView) v.findViewById(R.id.deviceName);
                    final TextView subtitle = (TextView) v.findViewById(R.id.deviceDescription);
                    if (imageView != null) {
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_img_not_found));
                        new DownloadImageTask(imageView)
                                .execute("https://cbks0.google.com/cbk?output=thumbnail&w=120&h=120&ll=" + safezone.getLatitude() + "," + safezone.getLongitude() + "&thumb=0");
                    }
                    if (!safezone.getName().trim().equals("")) {
                        title.setText(safezone.getName());
                        subtitle.setText(safezone.getAddress());
                    } else {
                        title.setText(safezone.getAddress());
                        subtitle.setText("");
                    }

                }
            } catch (Exception ex) {
                Log.e(TAG, "error fill the safezone listview");
            }
            return v;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (safezoneArrayList.size() == 0) {
                listViewSafezones.setEmptyView(textViewNoSafezones);
            }
        }

    }

    private class SafezoneBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "I Receive a broadcast of Safezones ", Toast.LENGTH_SHORT).show();
            adapter.updateSafezoneList(DeviceRow.getSafezonesOfDeviceGPS(macAddress));

        }
    }
*/
    private static ListView listViewSafezones;
    private static SafezoneListAdapter safezoneListAdapter;
    public static final String notify = "mei.ricardo.pessoa.app.ui.Sensor.DeviceRow";

    private SafezoneBroadcastReceiver safezoneBroadcastReceiver = null;
    private ArrayList<mei.ricardo.pessoa.app.couchdb.modal.Safezone> safezoneArrayList;
    private TextView textViewNoSafezones;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_list_safezones);

        macAddress = getIntent().getExtras().getString(ActivityListSensors.varMacAddressOfDevice);

        safezoneArrayList = Safezone.getSafezonesOfDeviceGPS(macAddress);

        listViewSafezones = (ListView) findViewById(R.id.listViewSafezones);
        textViewNoSafezones = (TextView) findViewById(R.id.textViewNoSafezones);

        safezoneListAdapter = new SafezoneListAdapter(safezoneArrayList);

        listViewSafezones.setAdapter(safezoneListAdapter);

        listViewSafezones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Safezone safezone = safezoneArrayList.get(i);
                    Intent intent = new Intent(getApplicationContext(), ActivitySafezoneOptions.class);
                    intent.putExtra(ActivitySafezoneOptions.passVarIDSafezone, safezone.get_id());
                    startActivity(intent);
                } catch (IndexOutOfBoundsException ex) {
                    Log.e(TAG, "Error select safezone");
                }
            }
        });

        if (safezoneArrayList != null && safezoneArrayList.size() == 0) {
            listViewSafezones.setEmptyView(textViewNoSafezones);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        safezoneBroadcastReceiver = new SafezoneBroadcastReceiver();
        registerReceiver(safezoneBroadcastReceiver, new IntentFilter(notify));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (safezoneBroadcastReceiver != null)
            unregisterReceiver(safezoneBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_safezones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_addSafezone) {
            Intent intent = new Intent(this, ActivitySafezoneEditMap.class);
            intent.putExtra(ActivityListSensors.varMacAddressOfDevice, macAddress);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SafezoneBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "I Receive a broadcast of safezones ", Toast.LENGTH_SHORT).show();
            safezoneListAdapter.updateSafezoneList(Safezone.getSafezonesOfDeviceGPS(macAddress));
        }
    }

    public class SafezoneListAdapter extends BaseAdapter {
        ArrayList<Safezone> safezoneList;

        public SafezoneListAdapter(ArrayList<Safezone> safezoneList) {
            this.safezoneList = safezoneList;
        }

        public void updateSafezoneList(ArrayList<Safezone> results) {
            safezoneList = results;
            safezoneArrayList = results;
            //Triggers the list update
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return safezoneList.size();
        }

        @Override
        public Safezone getItem(int arg0) {
            return safezoneList.get(arg0);
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
            Safezone safezone = safezoneList.get(arg0);
            if (imageView != null) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_img_not_found));
                new DownloadImageTask(imageView)
                        .execute("https://cbks0.google.com/cbk?output=thumbnail&w=120&h=120&ll=" + safezone.getLatitude() + "," + safezone.getLongitude() + "&thumb=0");
            }
            if (!safezone.getName().trim().equals("")) {
                chapterName.setText(safezone.getName());
                chapterDesc.setText(safezone.getAddress());
            } else {
                chapterName.setText(safezone.getAddress());
                chapterDesc.setText("");
            }
            return arg1;
        }


        public Safezone getCodeLearnChapter(int position) {
            return safezoneList.get(position);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (safezoneList.size() == 0) {
                listViewSafezones.setEmptyView(textViewNoSafezones);
            }
        }
    }


}
