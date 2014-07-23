package mei.ricardo.pessoa.app.ui.Sensor.Safezone;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Safezone;
import mei.ricardo.pessoa.app.ui.Sensor.ActivityListSensors;

public class ActivitySafezoneEditMap extends Activity {
    private static String TAG = ActivitySafezoneOptions.class.getCanonicalName();
    // Google Map
    private GoogleMap googleMap;
    private String safezoneID;
    private Safezone safezone;
    private String mac_AddressOfDevice;
    private boolean insertNewSafezone = false;
    private boolean editLocation;
    Circle circle;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safezone_edit_map);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            safezoneID = bundle.getString(ActivitySafezoneOptions.passVarIDSafezone);
            editLocation = bundle.getBoolean(ActivitySafezoneOptions.passVarIsToEditLocation);
            mac_AddressOfDevice = bundle.getString(ActivityListSensors.varMacAddressOfDevice);
            safezone = Safezone.getSafezoneByID(safezoneID);
        }

        if (safezoneID != null && safezone != null) {
            Log.d(TAG, "Safezone to edit");
        } else {
            Log.d(TAG, "Safezone to insert");
            insertNewSafezone = true;
        }
        try {
            // Loading map
            if (!insertNewSafezone && !editLocation)
                initSeekBar();

            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initSeekBar() {
        RelativeLayout relativeLayoutRadius = (RelativeLayout) findViewById(R.id.relative_layout_radius);
        relativeLayoutRadius.setVisibility(View.VISIBLE);

        SeekBar seekBarRadius = (SeekBar) findViewById(R.id.seek_bar_radius);
        seekBarRadius.setProgress(500-safezone.getRadius());
        final TextView textViewRadius = (TextView) findViewById(R.id.textView2);
        textViewRadius.setText(safezone.getRadius() + " m");
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewRadius.setText(i + 500 + " m");
                circle.setRadius(i + 500);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                safezone.setRadius(seekBar.getProgress()+500);
            }
        });
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
                insertSafezoneInMap(safezone.getLatitude(), safezone.getLongitude(), safezone.getRadius(), safezone.getAddress(), true);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_safezone_edit_map, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        // Associate searchable configuration with the SearchView

        MenuItem menuItemUpdateSafezone = menu.findItem(R.id.saveSafezone);

        if (safezoneID == null) {
            //menuItemUpdateSafezone.setVisible(false);
            menuItemUpdateSafezone.setTitle(getApplicationContext().getResources().getString(R.string.str_button_save));
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "handleIntent search:" + query, Toast.LENGTH_SHORT).show();
            // Do work using string
            new GeocodeLookupTask().execute(query);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.saveSafezone) {
            try {
                safezone.saveSafezone(insertNewSafezone);
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Error inserting/update safezone");
                e.printStackTrace();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * lookup safezone address BASED IN LATLNG thread *
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
                Log.e(TAG, "Geocoder unavailable: " + e.getMessage());
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

                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                    marker.showInfoWindow();
                }
            }
            Log.d(TAG, "Finished task");
        }
    }

    /**
     * lookup latitude and Longitude Base in ADDRESS  thread *
     */

    public class GeocodeLookupTask extends AsyncTask<String, Void, Address> {
        @Override
        protected Address doInBackground(String... params) {
            if (params.length < 1)
                return null;
            List<Address> addresses = null;

            try {
                Geocoder geo = new Geocoder(getBaseContext(), Locale.getDefault());
                addresses = geo.getFromLocationName(params[0], 1);
            } catch (IOException e) {
                Log.e(TAG, "GEOCODER IO: " + e.getMessage());
                //TODO create a dialog instead
                this.cancel(true);
            }
            if (addresses != null) {
                return addresses.get(0);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Address location) { // runs on the UI thread
            if (location == null && !this.isCancelled()) {
                Log.d(TAG, "found no location for this address :(");
                return;
            } else if (this.isCancelled()) {
                Log.d(TAG, "task canceled");
                return;
            }

            Double lat = location.getLatitude();
            Double lng = location.getLongitude();

            String newAddressName = getAddressString(location);

            if (insertNewSafezone) {
                insertNewSafezoneInMap(lat, lng, 500, newAddressName, true);
            } else if (safezone != null) {
                insertSafezoneInMap(lat, lng, safezone.getRadius(), newAddressName, true);
            }


        }
    }

    private void removeAllMarkers() {
        googleMap.clear();
    }

    private void insertMarker() {

    }

    private void insertNewSafezoneInMap(Double lat, Double lng, int radius, String address, boolean isDraggable) {
        String dateTimestamp = new Date().getTime() + "";
        safezone = new Safezone("safezone_" + dateTimestamp, address, address, lat.toString(), lng.toString(), radius + "", Safezone.typeNotifications[3], dateTimestamp, mac_AddressOfDevice);
        insertSafezoneInMap(lat, lng, radius, address, isDraggable);
    }

    private void insertSafezoneInMap(Double lat, Double lng, int radius, String address, boolean isDraggable) {
        // create marker
        removeAllMarkers();
        Log.d(TAG, "Lat:" + lat + " Lng:" + lng);
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(address);
        // Add a circle in Sydney
        circle = googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeWidth(3)
                .strokeColor(Color.RED)
                .fillColor(getResources().getColor(R.color.safezone_fillColor)));
// Changing marker icon
        //marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon)));

// adding marker
        markerOptions.draggable(isDraggable);

        marker = googleMap.addMarker(markerOptions);
        marker.setTitle(address);

        if (insertNewSafezone) {
            initSeekBar();
        }


        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
    }

    private String getAddressString(Address add) {
        return add != null && add.getAddressLine(0) != null ? add.getAddressLine(0) + getAddressCity(add) : "";
    }

    private String getAddressCity(Address add) {
        return (add != null ? (add.getLocality() != null ? (", " + add.getLocality()) : "") : "");
    }


}
