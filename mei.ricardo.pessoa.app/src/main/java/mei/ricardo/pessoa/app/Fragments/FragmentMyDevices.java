package mei.ricardo.pessoa.app.Fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.Iterator;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.Navigation.MainActivity;
import mei.ricardo.pessoa.app.R;

public class FragmentMyDevices extends Fragment {
    public static final String notify = "mei.ricardo.pessoa.app.notify.devices";
    private static String TAG = FragmentMyDevices.class.getName();
    private TextView mDevices;

    private DeviceBroadcastReceiver deviceBroadcastReceiver = null;

    public FragmentMyDevices() {
    }

    @Override
    public void onPause() {
        super.onPause();
        if (deviceBroadcastReceiver != null)
            getActivity().unregisterReceiver(deviceBroadcastReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceBroadcastReceiver = new DeviceBroadcastReceiver();
        getActivity().registerReceiver(deviceBroadcastReceiver, new IntentFilter(notify));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_devices, container, false);

        mDevices = (TextView) rootView.findViewById(R.id.textViewDevices);
        mDevices.setText("empty Devices");

        getDevicesOnCouchDB();

        return rootView;
    }

    private void getDevicesOnCouchDB() {
        com.couchbase.lite.View view = MainActivity.viewItemsByDate;
        Query query = view.createQuery();
        mDevices.setText("");
        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext();) {
                QueryRow row = it.next();
                Log.d("Document ID:", row.getDocumentId());
                mDevices.setText(mDevices.getText().toString()+"\n"+"Device: " +row.getDocumentId());
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(1);
    }

    private class DeviceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //float temp= intent.getFloatExtra(VAR_NAME,0);
            Toast.makeText(context, "I Receive a broadcast of devices ", Toast.LENGTH_SHORT).show();
            //mDevices.setText(mDevices.getText().toString() + " +1 ");
            getDevicesOnCouchDB();
        }
    }
}