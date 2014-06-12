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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MS_PanicButton;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.AdapterSectionAndMonitorSensor;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.InterfaceItem;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils.SectionItem;
import mei.ricardo.pessoa.app.ui.MonitoringSensor.ActivityMonitorSensorPanicButton;

public class FragmentNotificationOfMonitoring extends Fragment implements AdapterView.OnItemClickListener {
	private static final String ARG_POSITION = "position";
	private String deviceID;
    private static FragmentNotificationOfMonitoring f;
    ListView listView;
    ArrayList<InterfaceItem> arrayOfMonitoring;
    private PopulateTheView p;
    int color = Color.TRANSPARENT;
    private Handler handler;
    private AdapterSectionAndMonitorSensor adapter;

	public static FragmentNotificationOfMonitoring newInstance(String device_ID) {
		f = new FragmentNotificationOfMonitoring();
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
    public void onResume() {
        super.onResume();

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
        handler = new Handler();
        adapter = new AdapterSectionAndMonitorSensor(view.getContext(), arrayOfMonitoring);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        p = new PopulateTheView(adapter);
        p.execute();
        return view;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MonitorSensor monitorSensor = (MonitorSensor)arrayOfMonitoring.get(position);
        if(monitorSensor.getClass() == MS_PanicButton.class){
            MS_PanicButton ms_panicButton = (MS_PanicButton) monitorSensor;
            Toast.makeText(getActivity(), "You clicked " + monitorSensor.getTitle(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ActivityMonitorSensorPanicButton.class);
            intent.putExtra(ActivityMonitorSensorPanicButton.passVariableMacAddress,ms_panicButton.getMac_address());
            intent.putExtra(ActivityMonitorSensorPanicButton.passVariableTimestamp,ms_panicButton.getTimestamp());
            this.startActivity(intent);
        }
    }

    /**
     * Background Async Task to Load all INBOX messages by making HTTP Request
     * */
    class PopulateTheView extends AsyncTask<String, String, ArrayList<InterfaceItem>> {
       private final AdapterSectionAndMonitorSensor adapter;

        public PopulateTheView(final AdapterSectionAndMonitorSensor adapter){
            this.adapter = adapter;
        }

        protected ArrayList<InterfaceItem> doInBackground(String... args) {
            // Building Parameters
            //arrayOfMonitoring = new ArrayList<Item>();

            Device tempDevice = Device.getSensorsToSearch(deviceID);
            if(tempDevice==null)
                return null;
            ArrayList<InterfaceItem> arrayList;
            if(tempDevice.isShowPanicButton()){
                arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "panic_button", 1);
                if(arrayList.size()>0) {
                    arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[0],Device.DEVICESTYPE.panic_button.toString(),deviceID));
                    arrayOfMonitoring.addAll(arrayList);
                }
            }
            if(tempDevice.isShowSafezone()) {
                arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "GPS", 5);
                if (arrayList.size() > 0) {
                    arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[1],Device.DEVICESTYPE.GPS.toString(),deviceID));
                    arrayOfMonitoring.addAll(arrayList);
                }
            }
            if(tempDevice.isShowTemperature()) {
                arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "temperature", 5);
                if (arrayList.size() > 0) {
                    arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[2],Device.DEVICESTYPE.temperature.toString(),deviceID));
                    arrayOfMonitoring.addAll(arrayList);
                }
            }
            return arrayOfMonitoring;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(final ArrayList<InterfaceItem> itemArrayList) {
            // updating UI from Background Thread
            if (itemArrayList==null)
                return;

            if (itemArrayList.size()>0) {
                final Activity act = getActivity(); //only neccessary if you use fragments
                if (act != null)
                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.updateDeviceList(itemArrayList);
                        }
                    });

//                try {
//                    getActivity().runOnUiThread(new Runnable() { //force the UIThread refresh the list view
//
//                        public void run() {
//                            //adapter.items = itemArrayList;
//                            //adapter.notifyDataSetChanged();
//                            adapter.updateDeviceList(itemArrayList);
//                        }
//                    });
//                } catch (Exception ex) {
//                    Log.e("ERRRORRRR", "wtf happeend???" + ex.toString());
//                }
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.updateDeviceList(itemArrayList);
//                    }
//                });


            }
        }

    }

}