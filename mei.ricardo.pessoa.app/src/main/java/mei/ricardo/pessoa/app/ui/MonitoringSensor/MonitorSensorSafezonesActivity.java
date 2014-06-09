package mei.ricardo.pessoa.app.ui.MonitoringSensor;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.CouchDB;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.Utils.SlidingUpPanelLayout;

public class MonitorSensorSafezonesActivity extends Activity implements SlidingUpPanelLayout.PanelSlideListener {

    private ListView mListView;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private View mTransparentHeaderView;
    private View mTransparentView;
    private View mSpaceView;

    private MapFragment mMapFragment;

    private GoogleMap mMap;

    public ArrayList<MonitoringGPS> monitoringGPSes= new ArrayList<MonitoringGPS>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_sensor_safezones);

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

        ArrayList<String> testData = getMonitorSensorsGPS();
        mListView.addHeaderView(mTransparentHeaderView);
        mListView.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item, testData));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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


    private ArrayList<String> getMonitorSensorsGPS(){
        ArrayList<String> deviceRowsList = new ArrayList<String>();

        com.couchbase.lite.View view = CouchDB.viewGetMonitoringSensors;
        Query query = view.createQuery();
        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
                QueryRow row = it.next();
                String subtype = row.getDocument().getProperty("subtype").toString();
                if(subtype.equals(Device.DEVICESTYPE.GPS.toString())) {
                    Log.d("Document ID:", row.getDocumentId());

                    MonitoringGPS mgps = new MonitoringGPS();
                    mgps._id = row.getDocumentId();
                    mgps.mac_address = row.getDocument().getProperty("mac_address").toString();
                    mgps.address = row.getDocument().getProperty("address").toString();
                    mgps.lat = Float.parseFloat(row.getDocument().getProperty("latitude").toString());
                    mgps.lon =  Float.parseFloat(row.getDocument().getProperty("longitude").toString());
                    mgps.notification = row.getDocument().getProperty("notification").toString();
                    mgps.timestamp = row.getDocument().getProperty("timestamp").toString();

                    monitoringGPSes.add(mgps);
                    String nameDevice = row.getDocument().getProperty("mac_address").toString();
                    nameDevice += row.getDocument().getProperty("notification").toString();

                    deviceRowsList.add(nameDevice);
                }

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return deviceRowsList;
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = mMapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                CameraUpdate update = getLastKnownLocation(monitoringGPSes.get(0));
                if (update != null) {
                    mMap.moveCamera(update);
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
/*
    private CameraUpdate getLastKnownLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_LOW);
        String provider = lm.getBestProvider(criteria, true);
        if (provider == null) {
            return null;
        }
        Location loc = lm.getLastKnownLocation(provider);
        if (loc != null) {
            return CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 14.0f));
        }
        return null;
    }
*/
    private CameraUpdate getLastKnownLocation(MonitoringGPS msgps) {

            return CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(new LatLng(msgps.lat, msgps.lon), 14.0f));

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

    public class MonitoringGPS{
        private String _id;
        private String mac_address;
        private String address;
        public float lat;
        public float lon;
        private String notification;
        private String timestamp;

    }
}
