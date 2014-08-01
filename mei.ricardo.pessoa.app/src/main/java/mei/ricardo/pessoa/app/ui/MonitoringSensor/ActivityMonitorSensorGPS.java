package mei.ricardo.pessoa.app.ui.MonitoringSensor;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_GPS;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.Utils.SlidingUpPanelLayout;
import mei.ricardo.pessoa.app.utils.InterfaceItem;
import mei.ricardo.pessoa.app.utils.Utils;

public class ActivityMonitorSensorGPS extends Activity implements SlidingUpPanelLayout.PanelSlideListener, AdapterView.OnItemClickListener {
    private static String TAG = ActivityMonitorSensorGPS.class.getName();
    public static String passVariableIDOfDevice = "passVariableIDOfDevice";
    private String macAddressDevice;

    private ListView mListView;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private View mTransparentHeaderView;
    private View mTransparentView;
    private View mSpaceView;

    private MapFragment mMapFragment;

    private GoogleMap mMap;

    public ArrayList<MS_GPS> monitoringGPSes = new ArrayList<MS_GPS>();
    private static int previowsSelectMarker = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_sensor_safezones);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        macAddressDevice = getIntent().getExtras().getString(passVariableIDOfDevice);

        mListView = (ListView) findViewById(R.id.list);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(mListView, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // transparent view at the top of ListView
        mTransparentView = findViewById(R.id.transparentView);

        // init header view for ListView
        mTransparentHeaderView = LayoutInflater.from(this).inflate(R.layout.transparent_header_view, null, false);
        mSpaceView = mTransparentHeaderView.findViewById(R.id.space);

        monitoringGPSes = MS_GPS.getSensorGPSByMacAddressAndSubtype(macAddressDevice, Device.DEVICESTYPE.GPS.toString(), 30);
        MS_GPSListAdapter adapter = new MS_GPSListAdapter(monitoringGPSes);
        mListView.addHeaderView(mTransparentHeaderView);
        mListView.setAdapter(adapter);

        // mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item, testData));
        mListView.setOnItemClickListener(this);
        collapseMap();

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
        fragmentTransaction.commit();

        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mMapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                //mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                CameraUpdate update = getLastKnownLocation(monitoringGPSes.get(0));
                if (update != null) {
                    mMap.moveCamera(update);
                }
                for (InterfaceItem interfaceItem : monitoringGPSes) {
                    MS_GPS ms_gps = (MS_GPS) interfaceItem;
                    BitmapDescriptor bitmapDescriptorMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    if (ms_gps.getNotification().equals(MS_GPS.NOTIFICATIONTYPE[0])) {
                        bitmapDescriptorMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    }
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(ms_gps.latitude, ms_gps.longitude))
                            .icon(bitmapDescriptorMarker)
                            .title("Monitoring GPS at " + Utils.ConvertTimestampToDateFormat(ms_gps.getTimestamp()))
                            .snippet("Address " + ms_gps.getAddress()));

                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // In case Google Play services has since become available.
        setUpMapIfNeeded();
    }

    private CameraUpdate getLastKnownLocation(InterfaceItem msgps) {
        MS_GPS ms_gps = (MS_GPS) msgps;
        return CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(ms_gps.latitude, ms_gps.longitude), 14.0f));
    }

    private void collapseMap() {
        mSpaceView.setVisibility(View.VISIBLE);
        mTransparentView.setVisibility(View.GONE);
    }

    private void expandMap() {
        mSpaceView.setVisibility(View.GONE);
        mTransparentView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f), 1000, null);
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        previowsSelectMarker = position - 1;
        CameraUpdate update = getLastKnownLocation(monitoringGPSes.get(position - 1));
        if (update != null) {
            mMap.moveCamera(update);
        }
        //getLastKnownLocation(monitoringGPSes.get(position));
        mSlidingUpPanelLayout.collapsePane();
    }

    public class MS_GPSListAdapter extends BaseAdapter {
        ArrayList<MS_GPS> deviceList = new ArrayList<MS_GPS>();

        public MS_GPSListAdapter(ArrayList<MS_GPS> results) {
            deviceList = results;
        }

        @Override
        public int getCount() {
            return deviceList.size();
        }

        @Override
        public MS_GPS getItem(int arg0) {
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
            TextView RowName = (TextView) arg1.findViewById(R.id.deviceName);
            TextView RowDesc = (TextView) arg1.findViewById(R.id.deviceDescription);
            MS_GPS ms_gpsRow = deviceList.get(arg0);

            RowName.setText(ms_gpsRow.getAddress());
            RowDesc.setText(Utils.ConvertTimestampToDateFormat(ms_gpsRow.getTimestamp()));
            imageView.setImageDrawable(ms_gpsRow.getImage());
            return arg1;
        }
    }
}
