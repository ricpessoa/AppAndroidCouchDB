package mei.ricardo.pessoa.app.ui.Device;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;

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
import mei.ricardo.pessoa.app.couchdb.modal.Device;
import mei.ricardo.pessoa.app.utils.DialogFragmentYesNoOk;
import mei.ricardo.pessoa.app.utils.Utils;

public class AddDevice extends ActionBarActivity implements View.OnClickListener {
    private static String TAG = AddDevice.class.getName();

    private EditText nameDevice, macAddress;
    private Button saveDevice;
    private TaskAddNewDevice mRegisterTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        nameDevice = (EditText) findViewById(R.id.name_device);
        macAddress = (EditText) findViewById(R.id.mac_address_device);

        saveDevice = (Button) findViewById(R.id.button_save_device);
        saveDevice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == saveDevice.getId()) {
            boolean validNameDevice = false, validMacAddress = false, saveDevice = false;
            String tmpNameDevice = nameDevice.getText().toString();
            String tmpMacDevice = macAddress.getText().toString();
            if (tmpNameDevice.trim().length() > 0) {
                validNameDevice = true;
            }

            if (tmpMacDevice.trim().length() > 0) {
                //TODO: MAC Address Expression Regular validation
                validMacAddress = true;
            }
            Device device = Device.getDeviceByID(tmpMacDevice);


            if (device != null && validNameDevice) {
                device.setName_device(tmpNameDevice);
                saveDevice = true;
            }
            //create device
            if (device != null && validMacAddress) {
                if (device != null) {
                    if (device.isDeleted()) {
                        device.setDeleted(false);
                        saveDevice = true;
                    }
                }

                if (device != null && saveDevice) {
                    try {
                        device.saveDevice(false);
                        //finish();
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                }
            } else if (validMacAddress) {
                Toast.makeText(this, "No exist in db " + macAddress.getText().toString(), Toast.LENGTH_SHORT).show();
                if (!Utils.isNetworkAvailable(getApplicationContext())) {
                    showDialogFragment(getString(R.string.str_error_internet_connection));
                } else {
                    //user have internet connection
                    Log.d(TAG, "Execute Task");
                    mRegisterTask = new TaskAddNewDevice(Application.getDbname(), tmpMacDevice, tmpNameDevice); /*mac address*/
                    mRegisterTask.execute((Void) null);
                }
            }

        }
    }

    private void showDialogFragment(String msgNotification) {
        DialogFragmentYesNoOk dialogFragmentYesNoOk = new DialogFragmentYesNoOk(getApplicationContext(), getString(R.string.str_title_information_dialog), msgNotification);
        dialogFragmentYesNoOk.setType(1);
        dialogFragmentYesNoOk.setPositiveAndNegative(getString(R.string.fire_ok), "");
        dialogFragmentYesNoOk.setBackToPreviousActivity(true);
        dialogFragmentYesNoOk.show(getFragmentManager(), "");
    }

    private class TaskAddNewDevice extends AsyncTask<Void, Void, Integer> {

        private final String macAddress;
        private final String nameDevice;
        private final String username;

        TaskAddNewDevice(String username, String macAddress, String nameDevice) {
            this.username = username;
            this.macAddress = macAddress;
            this.nameDevice = nameDevice;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Application.hostUrl + Application.serviceAddAddNewDevice);

            try {

                // Add your data
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("mac_address", macAddress));
                if (!nameDevice.equals("")) {
                    nameValuePairs.add(new BasicNameValuePair("name_device", nameDevice));
                }

                nameValuePairs.add(new BasicNameValuePair("username", username));

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
                        Boolean insertsuccessfull = !finalResult.getBoolean("error"); //WARNING NOT HAVE ERROR = FALSE
                        if (insertsuccessfull)
                            return 1;
                        else
                            return -1;

                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error JSONException in POST");
                    return -3;
                } catch (NullPointerException e) {
                    Log.e(TAG, "Null Point exception on POST");
                    return -3;
                }

            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error ClientProtocolException in POST");
                return -2;
            } catch (IOException e) {
                Log.e(TAG, "Error IOException in POST");
                return -3;
            }
            return -3;
        }

        /*  return 1 - device save successful
                * error -1 Device not found
                * error -2 Internet connection error
                * error -3 some error occur*/
        @Override
        protected void onPostExecute(final Integer success) {
            mRegisterTask = null;
            switch (success) {
                case 1:
                    Toast.makeText(Application.getmContext(), "Add new device successful", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -1:
                    showDialogFragment(getString(R.string.str_error_device_not_found));
                    break;
                case -2:
                    showDialogFragment(getString(R.string.str_error_internet_connection));
                    break;
                case -3:
                    showDialogFragment(getString(R.string.str_error_insert_new_device));
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            //showProgress(false);
        }
    }
}


