package mei.ricardo.pessoa.app.ui.MonitoringSensor;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_GPS;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.AdapterSectionAndMonitorSensor;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.InterfaceItem;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.Utils.SlidingUpPanelLayout;
import mei.ricardo.pessoa.app.utils.Utilities;

public class ActivityMonitorSensorGPS extends Activity implements SlidingUpPanelLayout.PanelSlideListener {
    private static String TAG = ActivityMonitorSensorGPS.class.getName();
    public static String passVariableID = "gps_id";
    private String macAddress;

    private ListView mListView;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private View mTransparentHeaderView;
    private View mTransparentView;
    private View mSpaceView;

    private MapFragment mMapFragment;

    private GoogleMap mMap;

    public ArrayList<InterfaceItem> monitoringGPSes = new ArrayList<InterfaceItem>();
    private static int previowsSelectMarker = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_sensor_safezones);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        macAddress = getIntent().getExtras().getString(passVariableID);
        Log.d(TAG, "mac address received = " + macAddress);
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

        monitoringGPSes = MonitorSensor.getSensorGPSByMacAddressAndSubtype(macAddress, Device.DEVICESTYPE.GPS.toString(), 30);
        AdapterSectionAndMonitorSensor adapter = new AdapterSectionAndMonitorSensor(this, monitoringGPSes, Color.WHITE);
        mListView.addHeaderView(mTransparentHeaderView);
        mListView.setAdapter(adapter);

        // mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item, testData));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                previowsSelectMarker = position - 1;
                CameraUpdate update = getLastKnownLocation(monitoringGPSes.get(position - 1));
                if (update != null) {
                    mMap.moveCamera(update);
                }
                //getLastKnownLocation(monitoringGPSes.get(position));
                mSlidingUpPanelLayout.collapsePane();

            }
        });
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
                            .title("Address: " + ms_gps.getAddress() + " at " + Utilities.ConvertTimestampToDateFormat(ms_gps.getTimestamp())));

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
}
