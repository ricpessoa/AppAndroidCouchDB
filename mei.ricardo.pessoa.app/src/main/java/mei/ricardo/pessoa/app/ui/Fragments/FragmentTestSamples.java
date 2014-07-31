package mei.ricardo.pessoa.app.ui.Fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.utils.Utils;
import mei.ricardo.pessoa.app.ui.MainActivity;


public class FragmentTestSamples extends Fragment {

    private static String TAG = FragmentTestSamples.class.getName();
    private TesteDeviceRegisterTask mRegisterTask = null;
    CheckBox checkBoxBattery, checkBoxGPS, checkBoxPanicButton, checkBoxTemperature;
    EditText editTextMacAddress, editTextLat, editTextLng, editTextValueBattery, editTextValueTemperature;

    public FragmentTestSamples() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_samples, container, false);
        Button testButton = (Button) rootView.findViewById(R.id.buttonEditSafezoneName);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterTask = new TesteDeviceRegisterTask("az"); /*mac address*/
                mRegisterTask.execute((Void) null);
            }
        });
        checkBoxPanicButton = (CheckBox) rootView.findViewById(R.id.checkBoxPanicButton);
        checkBoxGPS = (CheckBox) rootView.findViewById(R.id.checkBoxGPS);
        checkBoxBattery = (CheckBox) rootView.findViewById(R.id.checkBoxBattery);
        checkBoxTemperature = (CheckBox) rootView.findViewById(R.id.checkBoxTemperature);
        editTextMacAddress = (EditText) rootView.findViewById(R.id.editTextMacAddress);
        editTextMacAddress.setText("az");
        editTextLat = (EditText) rootView.findViewById(R.id.editTextLat);
        editTextLat.setText("41.23206");
        editTextLng = (EditText) rootView.findViewById(R.id.editTextLng);
        editTextLng.setText("-8.624164");
        editTextValueBattery = (EditText) rootView.findViewById(R.id.editTextValueBattery);
        editTextValueBattery.setText("10");
        editTextValueTemperature = (EditText) rootView.findViewById(R.id.editTextValueTemperature);
        editTextValueTemperature.setText("24");
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(0);
    }

    private class TesteDeviceRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String macAddress;

        TesteDeviceRegisterTask(String macAddress) {
            this.macAddress = macAddress;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Application.hostUrl + Application.serviceAddDeviceUrl);

            try {
                // Add your data
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
//                nameValuePairs.add(new BasicNameValuePair("mac", macAddress));
//                //rua silva aroso (out put not necessaru save)
//                //nameValuePairs.add(new BasicNameValuePair("latfrom", "41.23206"));
//                //nameValuePairs.add(new BasicNameValuePair("lngfrom", "-8.698981"));
//                // ponte da madalena (fora do raio de minha casa)
//                //nameValuePairs.add(new BasicNameValuePair("latfrom", "41.108071"));
//                //nameValuePairs.add(new BasicNameValuePair("lngfrom", "-8.636909"));
//                //perto da estacao de gaia (fora do raio de casa e dentro do rs catarina)
//                nameValuePairs.add(new BasicNameValuePair("latfrom", "41.126969"));
//                nameValuePairs.add(new BasicNameValuePair("lngfrom", "-8.624164"));
//                //entro dois raios mas fora
//                //nameValuePairs.add(new BasicNameValuePair("latfrom", "41.11845"));
//                //nameValuePairs.add(new BasicNameValuePair("lngfrom", "-8.622257"));
//
//                //send value temperature
//                nameValuePairs.add(new BasicNameValuePair("temp", "25"));
//                //send value panic button
//                //nameValuePairs.add(new BasicNameValuePair("press", "true"));
//                //send value to battery
//                nameValuePairs.add(new BasicNameValuePair("batt", "50"));


                // Add your data
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mac", editTextMacAddress.getText().toString()));
                if (checkBoxPanicButton.isChecked()) {
                    nameValuePairs.add(new BasicNameValuePair("press", "true"));
                }
                if (checkBoxBattery.isChecked()) {
                    nameValuePairs.add(new BasicNameValuePair("batt", editTextValueBattery.getText().toString()));
                }
                if (checkBoxGPS.isChecked()) {
                    nameValuePairs.add(new BasicNameValuePair("latfrom", editTextLat.getText().toString()));
                    nameValuePairs.add(new BasicNameValuePair("lngfrom", editTextLng.getText().toString()));
                }
                if (checkBoxTemperature.isChecked()) {
                    nameValuePairs.add(new BasicNameValuePair("temp", editTextValueTemperature.getText().toString()));
                }
                nameValuePairs.add(new BasicNameValuePair("timestamp", "" + Utils.getTimestamp()));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String json = reader.readLine();
                JSONTokener tokener = new JSONTokener(json);
                try {
                    JSONObject finalResult = new JSONObject(tokener);
                    if (finalResult != null) {
                        Log.d(TAG, "message?" + finalResult.getString("message") + " error?" + finalResult.getString("error"));
                        Boolean loginsuccessfull = !finalResult.getBoolean("error"); //WARNING NOT HAVE ERROR = FALSE
                        return loginsuccessfull;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error JSONException in POST");
                    return false;
                } catch (NullPointerException e) {
                    Log.e(TAG, "Null Point exception on POST");
                    return false;
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "Error ClientProtocolException in POST");
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "Error IOException in POST");
                return false;
            }
            return false;
        }

        /*error -1 This email already exists
        * error -2 A user with this username already exists
        * error -3 Internet connection error
        * error -4 some error occur*/
        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
            if (success) {
                Toast.makeText(Application.getmContext(), "Successful", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(Application.getmContext(), "XUnsuccessful", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            //showProgress(false);
        }
    }
}