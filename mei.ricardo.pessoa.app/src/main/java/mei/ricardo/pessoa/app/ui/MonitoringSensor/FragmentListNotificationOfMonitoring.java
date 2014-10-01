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

package mei.ricardo.pessoa.app.ui.MonitoringSensor;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.couchdb.modal.Monitoring.MonitorSensor;
import mei.ricardo.pessoa.app.utils.InterfaceItem;
import mei.ricardo.pessoa.app.utils.SectionItem;
import mei.ricardo.pessoa.app.utils.Adapters.CustomAdapter;

public class FragmentListNotificationOfMonitoring extends ListFragment {
    public static String passIDOfDevice = "passIDOfDevice";
    private CustomAdapter adapter;
    private String deviceId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_notification_of_monitoring, container, false);
        deviceId = getArguments().getString(passIDOfDevice);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Populates list with our static array

        adapter = new CustomAdapter(getActivity());

        setListAdapter(adapter);
        PopulateTheView p = new PopulateTheView(adapter, deviceId);
        p.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * Background Async Task to Load all INBOX messages by making HTTP Request
     */
    class PopulateTheView extends AsyncTask<String, String, ArrayList<InterfaceItem>> {
        // private final ArrayAdapter<String> adapter;
        private final CustomAdapter adapter;
        private final String deviceID;

        public PopulateTheView(final CustomAdapter adapter, String deviceID) {
            this.adapter = adapter;
            this.deviceID = deviceID;
        }

        protected ArrayList<InterfaceItem> doInBackground(String... args) {
            // Building Parameters
            ArrayList<InterfaceItem> arrayOfMonitoring = new ArrayList<InterfaceItem>();

            Device tempDevice = Device.getSensorsToSearch(deviceID);
            if (tempDevice == null)
                return null;
            ArrayList<InterfaceItem> arrayList;
            if (tempDevice.isShowPanicButton()) {
                arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "panic_button", 1);
                if (arrayList.size() > 0) {
                    arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[0], Device.DEVICESTYPE.panic_button.toString(), deviceID, arrayList));
                    arrayOfMonitoring.addAll(arrayList);
                }
            }
            if (tempDevice.isShowShoe()) {
                arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "shoe", 1);
                if (arrayList.size() > 0) {
                    arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[4], Device.DEVICESTYPE.shoe.toString(), deviceID, arrayList));
                    arrayOfMonitoring.addAll(arrayList);
                }
            }
            if (tempDevice.isShowSafezone()) {
                arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "GPS", 4);
                if (arrayList.size() > 0) {
                    arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[1], Device.DEVICESTYPE.GPS.toString(), deviceID, arrayList));
                    arrayOfMonitoring.addAll(arrayList);
                }
            }
            if (tempDevice.isShowTemperature()) {
                arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "temperature", 4);
                if (arrayList.size() > 0) {
                    arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[2], Device.DEVICESTYPE.temperature.toString(), deviceID, arrayList));
                    arrayOfMonitoring.addAll(arrayList);
                }
            }
            if (tempDevice.isShowBattery()) {
                arrayList = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "battery", 1);
                if (arrayList.size() > 0) {
                    arrayOfMonitoring.add(new SectionItem(MonitorSensor.subtypeSections[3], Device.DEVICESTYPE.battery.toString(), deviceID, arrayList));
                    arrayOfMonitoring.addAll(arrayList);
                }
            }
            return arrayOfMonitoring;
            //return arrayList;

        }

        /**
         * After completing background task Dismiss the progress dialog
         * *
         */
        protected void onPostExecute(final ArrayList<InterfaceItem> itemArrayList) {
            // updating UI from Background Thread

            if (itemArrayList == null || itemArrayList.size() == 0)
                return;

            for (InterfaceItem item : itemArrayList) {
                try {
                    MonitorSensor monitorSensor = (MonitorSensor) item;
                    adapter.addItem(monitorSensor);
                } catch (ClassCastException ex) {
                    SectionItem si = (SectionItem) item;
                    adapter.addSectionHeaderItem(si);
                }

            }
        }
    }
}