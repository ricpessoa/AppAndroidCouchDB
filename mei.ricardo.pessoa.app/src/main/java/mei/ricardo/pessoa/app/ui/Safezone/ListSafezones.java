package mei.ricardo.pessoa.app.ui.Safezone;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.codehaus.jackson.map.ext.JodaDeserializers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.InterfaceItem;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.SectionItem;
import mei.ricardo.pessoa.app.couchdb.modal.Safezone;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityMonitorSensorGPS;
import mei.ricardo.pessoa.app.utils.Utilities;

public class ListSafezones extends ActionBarActivity {
    private static String TAG = ListSafezones.class.getCanonicalName();
    public static String varMacAddressOfDevice = "varMacAddressOfDevice";
    private String macAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_safezones);

        macAddress = getIntent().getExtras().getString(varMacAddressOfDevice);

        ArrayList<Safezone> arrayList = Safezone.getSafezonesOfDeviceGPS(macAddress);
        for (Safezone safezone : arrayList) {
            Log.d(TAG, "Safezone " + safezone.getDevice() + " - " + safezone.getAddress());
        }
        ListView listViewSafezones = (ListView) findViewById(R.id.listViewSafezones);
        SafezoneListViewAdapter adapter = new SafezoneListViewAdapter(this, arrayList);
        listViewSafezones.setAdapter(adapter);
        listViewSafezones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

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
        if (id == R.id.action_settings) {
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
                    Safezone monitorSensor = (Safezone) i;
                    v = vi.inflate(R.layout.list_item_entry, null);
                    RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.relative_layout_entry);
                    relativeLayout.setBackgroundColor(color);

                    final ImageView imageView = (ImageView) v.findViewById(R.id.icon);
                    final TextView title = (TextView) v.findViewById(R.id.deviceName);
                    final TextView subtitle = (TextView) v.findViewById(R.id.deviceDescription);
                    if (imageView != null)
                        imageView.setImageDrawable(null);
                    new DownloadImageTask(imageView)
                            .execute("https://cbks0.google.com/cbk?output=thumbnail&w=120&h=120&ll=" + monitorSensor.getLatitude() + "," + monitorSensor.getLongitude() + "&thumb=0");
                    if (title != null)
                        title.setText(monitorSensor.getName());
                    if (subtitle != null)
                        subtitle.setText(monitorSensor.getAddress());
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
