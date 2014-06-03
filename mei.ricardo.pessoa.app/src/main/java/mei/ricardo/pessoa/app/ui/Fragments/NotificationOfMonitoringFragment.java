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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.MonitorSensor;

public class NotificationOfMonitoringFragment extends Fragment {
	private static final String ARG_POSITION = "position";
	private String deviceID;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate (R.layout.fragment_notifications_on_dashboard, container,false);
        TextView textView= (TextView) view.findViewById (R.id.textViewDevice);
        textView.setText (deviceID);
        //LinearLayout linearLayoutSafezone = (LinearLayout) view.findViewById(R.id.myFragmentTemperature);

        List<String> arrayOfMonitoring = MonitorSensor.getMonitoringSensorByMacAddressAndSubtype(deviceID, "GPS");
        ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                arrayOfMonitoring );

        listView.setAdapter(arrayAdapter);
       /* FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (showPanicButton) {
            if (fp == null) {
                fp = new FragmentButtonPanic();
                fragmentTransaction.add(R.id.myFragmentPanicButton, fp);
            }
        }
        if (showSafezone) {
            if (fs == null) {
                fs = new FragmentSafezone();
                fragmentTransaction.add(R.id.myFragmentSafezone, fs);
            }
        }
        if (showTemperature) {
            if (ft == null) {
                ft = new FragmentTemperature();
                fragmentTransaction.add(R.id.myFragmentTemperature, ft);
            }
        }
        fragmentTransaction.commit();*/

        return view;
	}


}