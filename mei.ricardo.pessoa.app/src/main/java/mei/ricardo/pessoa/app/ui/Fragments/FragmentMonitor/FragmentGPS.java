package mei.ricardo.pessoa.app.ui.Fragments.FragmentMonitor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_GPS;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityMonitorSensorDetail;


public class FragmentGPS extends Fragment {
    private static String TAG = FragmentGPS.class.getName();
    private TextView textViewNotification;
    private TextView textViewLocationAddress;
    private TextView textViewLocationCoordinators;
    private Button buttonGoToDirection;
    private ImageView imageView;
    MS_GPS ms_gps;

    public FragmentGPS() {
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gps, container, false);
        textViewNotification = (TextView) rootView.findViewById(R.id.textViewNotification);
        textViewLocationAddress = (TextView) rootView.findViewById(R.id.textViewLocationAddress);
        textViewLocationCoordinators = (TextView) rootView.findViewById(R.id.textViewLocationCoordinators);
        buttonGoToDirection = (Button) rootView.findViewById(R.id.buttonGoToDirection);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActivityMonitorSensorDetail activityMonitorSensorDetail = (ActivityMonitorSensorDetail) getActivity();
        ms_gps = activityMonitorSensorDetail.getMs_gps();

        if (ms_gps != null) {
            textViewNotification.setText("[" + ms_gps.getNotification() + "]");
            textViewLocationAddress.setText("Location Address: " + ms_gps.getAddress());
            textViewLocationCoordinators.setText("Location Coordinators: (" + ms_gps.getLatitude() + "," + ms_gps.getLongitude() + ")");
            imageView.setImageDrawable(ms_gps.getImage());
            initilizeMap(ms_gps);
            buttonGoToDirection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?f=d&daddr=" + ms_gps.getLatitude() + "," + ms_gps.getLongitude()));
                    startActivity(intent);
                }
            });

        } else {
            getView().setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap(MS_GPS ms_gps) {
        GoogleMap googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.mapFragment)).getMap();
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);

        LatLng latLng = new LatLng(ms_gps.getLatitude(), ms_gps.getLongitude());
        //MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(ms_gps.getAddress());
        //Marker marker = googleMap.addMarker(markerOptions);
        // Move the camera instantly to hamburg with a zoom of 15.
        googleMap.addMarker(new MarkerOptions()
                .position(latLng));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // check if map is created successfully or not
        if (googleMap == null) {
            Toast.makeText(getActivity(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();

        }
    }
}