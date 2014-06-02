package mei.ricardo.pessoa.app.ui.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;

import java.util.Iterator;


import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.CouchDB;


public class FragmentMyDashboard extends Fragment {
   public static final String TAG = FragmentMyDashboard.class
            .getSimpleName();
    private static ArrayList<DeviceRow> arrayTabs ;

    public static FragmentMyDashboard newInstance() {
        return new FragmentMyDashboard();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_dashboard, container, false);

        arrayTabs =  getDevicesOnCouchDB();
        //arrayTabs = arrayList.toArray(new String[arrayList.size()]);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view
                .findViewById(R.id.tabs);

        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);


    }


    private ArrayList<DeviceRow> getDevicesOnCouchDB() {
        ArrayList<DeviceRow> deviceRowsList = new ArrayList<DeviceRow>();
        deviceRowsList.add(new DeviceRow("All Devices",""));
        com.couchbase.lite.View view = CouchDB.viewGetDevicesMonitoring;
        Query query = view.createQuery();
        try {
            QueryEnumerator rowEnum = query.run();
            for (Iterator<QueryRow> it = rowEnum; it.hasNext(); ) {
                QueryRow row = it.next();

                DeviceRow deviceRow = new DeviceRow();
                Log.d("Document ID:", row.getDocumentId());
                deviceRow.deviceID = row.getDocumentId();
                deviceRow.deviceName = row.getDocument().getProperty("name_device").toString();
                deviceRowsList.add(deviceRow);

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return deviceRowsList;
    }


    public class DeviceRow {
        public String deviceID;
        public String deviceName;

        public DeviceRow(){}

        public DeviceRow(String _id,String deviceName){
            this.deviceID = _id;
            this.deviceName = deviceName;

        }
    }
    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if(!arrayTabs.get(position).deviceName.equals(""))
                return arrayTabs.get(position).deviceName;
            else
                return arrayTabs.get(position).deviceID;
        }

        @Override
        public int getCount() {
            return arrayTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            return NotificationOfMonitoringFragment.newInstance(position);
        }

    }
}
