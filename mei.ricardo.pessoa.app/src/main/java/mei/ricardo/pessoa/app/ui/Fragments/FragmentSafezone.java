package mei.ricardo.pessoa.app.ui.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 14/05/14.
 */
public class FragmentSafezone extends Fragment {
    private static String TAG = FragmentSafezone.class.getName();

    // Google Map
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_safezone, container, false);

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

            addMarker(41.112857,-8.629313);

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(Application.getmContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void addMarker(double latitude, double longitude) {
        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello Maps ");
        // adding marker
        googleMap.addMarker(marker);
    }

    @Override
    public void onResume() {
        super.onResume();
        initilizeMap();
    }
}