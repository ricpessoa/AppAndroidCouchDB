/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mei.ricardo.pessoa.app.ui.Fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.awt.font.TextAttribute;
import java.util.ArrayList;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.AdapterSectionAndMonitorSensor;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.InterfaceItem;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.SectionItem;

public class NotificationOfMonitoringFragment extends Fragment implements AdapterView.OnItemClickListener {
	private static final String ARG_POSITION = "position";
	private String deviceID;
    ArrayList<InterfaceItem> arrayOfMonitoring;
    PopulateTheView p;

	public static NotificationOfMonitoringFragment newInstance(String device_ID) {
		NotificationOfMonitoringFragment f = new NotificationOfMonitoringFragment();
		Bundle b = new Bundle();
		b.putString(ARG_POSITION, device_ID);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        deviceID = getArguments().getString(ARG_POSITION);

	}

    @Override
    public void onPause() {
        super.onPause();
        //p.cancel(true);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate (R.layout.fragment_notifications_on_dashboard, container,false);

        ListView listView = (ListView) view.findViewById(R.id.listViewMonitoring);

        arrayOfMonitoring = new ArrayList<InterfaceItem>();

        AdapterSectionAndMonitorSensor adapter = new AdapterSectionAndMonitorSensor(getActivity(), arrayOfMonitoring);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        p = new PopulateTheView(adapter);
        p.execute();
        return view;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MonitorSensor monitorSensor = (MonitorSensor)arrayOfMonitoring.get(position);
        Toast.makeText(getActivity(), "You clicked " + monitorSensor.getTitle(), Toast.LENGTH_SHORT).show();
    }

    /**
     * Background Async Task to Load all INBOX messages by making HTTP Request
     * */
    class PopulateTheView extends AsyncTask<String, String, ArrayList<InterfaceItem>> {
        AdapterSectionAndMonitorSensor adapter;

        public PopulateTheView(AdapterSectionAndMonitorSensor adapter){
            this.adapter = adapter;
        }

        protected ArrayList<InterfaceItem> doInBackground(String... args) {
            // Building Parameters
            //arrayOfMonitoring = new ArrayList<Item>();
            ArrayList<InterfaceItem> arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "panic_button", 1);
            if(arrayList.size()>0) {
                arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[0]));
                arrayOfMonitoring.addAll(arrayList);
            }
            arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "GPS", 5);
            if(arrayList.size()>0) {
                arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[1]));
                arrayOfMonitoring.addAll(arrayList);
            }
            arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "temperature",5);

            if(arrayList.size()>0) {
                arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[2]));
                arrayOfMonitoring.addAll(arrayList);
            }
            return arrayOfMonitoring;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(final ArrayList<InterfaceItem> itemArrayList) {
            // updating UI from Background Thread
            try {
                getActivity().runOnUiThread(new Runnable() { //force the UIThread refresh the list view
                public void run() {
                    adapter.updateDeviceList(itemArrayList);
                }});
            }catch (IllegalStateException ex){
                Log.e("ERRRORRRR","wtf happeend???");
            }
        }

    }

}