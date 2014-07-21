package mei.ricardo.pessoa.app.ui.Safezone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.InputStream;
import java.util.ArrayList;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Safezone;
import mei.ricardo.pessoa.app.ui.Fragments.Utils.DownloadImageTask;
;

public class ActivityListSafezones extends ActionBarActivity {
    private static String TAG = ActivityListSafezones.class.getCanonicalName();
    public static String varMacAddressOfDevice = "varMacAddressOfDevice";
    private String macAddress;
    private ArrayList<Safezone> arrayList;
    private ListView listViewSafezones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_safezones);

        macAddress = getIntent().getExtras().getString(varMacAddressOfDevice);

//        for (Safezone safezone : arrayList) {
//            Log.d(TAG, "Safezone " + safezone.getDevice() + " - " + safezone.getAddress());
//        }
        listViewSafezones = (ListView) findViewById(R.id.listViewSafezones);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshActivity();
    }

    private void refreshActivity() {
        arrayList = Safezone.getSafezonesOfDeviceGPS(macAddress);

        SafezoneListViewAdapter adapter = new SafezoneListViewAdapter(this, arrayList);
        listViewSafezones.setAdapter(adapter);
        listViewSafezones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Safezone safezone = arrayList.get(i);
                Intent intent = new Intent(getApplicationContext(), ActivitySafezoneOptions.class);
                intent.putExtra(ActivitySafezoneOptions.passVarIDSafezone, safezone.get_id());
                startActivity(intent);
            }
        });
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
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SafezoneListViewAdapter extends ArrayAdapter<Safezone> {
        private Context context;
        public ArrayList<Safezone> items;
        private LayoutInflater vi;
        private int color = Color.TRANSPARENT;

        public SafezoneListViewAdapter(Context context, ArrayList<Safezone> items) {
            super(context, 0, items);
            this.context = context;
            this.items = items;
            vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            try {

                final Safezone i = items.get(position);
                if (i != null) {
                    Safezone safezone = (Safezone) i;
                    v = vi.inflate(R.layout.list_item_entry, null);
                    RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.relative_layout_entry);
                    relativeLayout.setBackgroundColor(color);

                    final ImageView imageView = (ImageView) v.findViewById(R.id.icon);
                    final TextView title = (TextView) v.findViewById(R.id.deviceName);
                    final TextView subtitle = (TextView) v.findViewById(R.id.deviceDescription);
                    if (imageView != null) {
                        imageView.setImageDrawable(null);
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

        public void updateDeviceList(ArrayList<Safezone> results) {
            try {
                items = results;
                //Triggers the list update
                notifyDataSetChanged();
            } catch (Exception ex) {
                Log.e("ERRRORRRR", "wtf happeend???");
            }

        }
    }


}
