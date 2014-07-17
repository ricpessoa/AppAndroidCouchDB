package mei.ricardo.pessoa.app.ui.Safezone;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Safezone;

public class ActivitySafezoneEditMap extends ActionBarActivity {
    private static String TAG = ActivitySafezoneOptions.class.getCanonicalName();
    // Google Map
    private GoogleMap googleMap;
    private String safezoneID;
    private Safezone safezone;
    Circle circle;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safezone_edit_map);

        safezoneID = getIntent().getExtras().getString(ActivitySafezoneOptions.passVarIDSafezone);
        safezone = Safezone.getSafezoneByID(safezoneID);

        if (safezoneID != null && safezone != null) {
            Log.d(TAG, "Safezone to edit");
        } else {
            Log.d(TAG, "Safezone to insert");
        }
        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.setMyLocationEnabled(true); // false to disable
            googleMap.getUiSettings().setZoomControlsEnabled(false); // true to enable
            googleMap.getUiSettings().setCompassEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                    circle.setCenter(marker.getPosition());
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    //marker.setTitle();
                    new ReverseGeocodeLookupTask(marker.getPosition()).execute();
                }
            });
            if (safezone != null) {
                // create marker
                Log.d(TAG, "Lat:" + safezone.getLatitude() + " Lng:" + safezone.getLongitude());
                MarkerOptions markerOptions= new MarkerOptions().position(new LatLng(safezone.getLatitude(), safezone.getLongitude())).title(safezone.getAddress());
                // Add a circle in Sydney
                circle = googleMap.addCircle(new CircleOptions()
                        .center(new LatLng(safezone.getLatitude(), safezone.getLongitude()))
                        .radius(safezone.getRadius())
                        .strokeWidth(3)
                        .strokeColor(Color.RED)
                        .fillColor(getResources().getColor(R.color.safezone_fillColor)));
// Changing marker icon
                //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon)));

// adding marker
                markerOptions.draggable(true);

                marker = googleMap.addMarker(markerOptions);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12.0f));
            }

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_safezone_edit_map, menu);
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

    /**
     * lookup safezone address thread *
     */
    public class ReverseGeocodeLookupTask extends AsyncTask<Void, Void, List<Address>> {
        private final LatLng latLng;

        public ReverseGeocodeLookupTask(LatLng position) {
            latLng = position;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected List<Address> doInBackground(Void... params) {
            List<Address> adds = null;

            try {
                //adds = AlzNav.gpsManager.getGeocoder().getFromLocation(mLat, mLon, 1);
                Log.d(TAG, "new Location: " + latLng.latitude + " , " + latLng.longitude);
                Geocoder geo = new Geocoder(getBaseContext(), Locale.getDefault());
                adds = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);//1 max result
            } catch (Exception e) {
                Log.d(TAG, "Geocoder unavailable: " + e.getMessage());
                this.cancel(true);
            }

            return adds;
        }

        @Override
        protected void onPostExecute(List<Address> adds) { // runs on the UI thread
            if (adds == null || adds.size() == 0) {
                Log.d(TAG, "address Safezone not find");
                return;
            }
            if (!this.isCancelled()) {
                //setAddress(AlzNav.gpsManager.getAddressString(adds.get(0)));
                String newAddress = getAddressString(adds.get(0));
                safezone.setAddress(newAddress);
                Log.d(TAG, "Safezone address updated to: " + newAddress);
                marker.setTitle(newAddress);
                   //safezone.setAddress();
            }
            Log.d(TAG, "Finished task");
        }
    }

    private String getAddressString(Address add) {
        return add != null && add.getAddressLine(0) != null ? add.getAddressLine(0) + getAddressCity(add) : "";
    }

    private String getAddressCity(Address add) {
        return (add != null ? (add.getLocality() != null ? (", " + add.getLocality()) : "") : "");
    }
}
