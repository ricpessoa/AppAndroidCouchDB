package mei.ricardo.pessoa.app.ui.Fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.ui.Fragments.Utils.DialogFragmentYesNoOk;
import mei.ricardo.pessoa.app.ui.Navigation.MainActivity;
import mei.ricardo.pessoa.app.R;


public class FragmentMyDashboard extends Fragment {

    private static String TAG = FragmentMyDashboard.class.getName();
    private TesteDeviceRegisterTask mRegisterTask = null;

    public FragmentMyDashboard() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_dashboard, container, false);
        Button testButton = (Button) rootView.findViewById(R.id.button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterTask = new TesteDeviceRegisterTask("az");
                mRegisterTask.execute((Void) null);
            }
        });
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
            HttpPost httppost = new HttpPost(Application.hostUrl+Application.serviceAddDeviceUrl);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("mac", macAddress));
                //nameValuePairs.add(new BasicNameValuePair("latfrom", "41.23206"));
                //nameValuePairs.add(new BasicNameValuePair("lngfrom", "-8.698981"));
                nameValuePairs.add(new BasicNameValuePair("latfrom", "41.108071"));
                nameValuePairs.add(new BasicNameValuePair("lngfrom", "-8.636909"));

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
                    Log.e(TAG, "Error JSONException in Login");
                    return false;
                } catch (NullPointerException e) {
                    Log.e(TAG, "Null Point exception on Login");
                    return false;
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "Error ClientProtocolException in Login");
                return  false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "Error IOException in Login");
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
                Toast.makeText(Application.getmContext(), "V successful", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(Application.getmContext(), "X unsuccessful", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            //showProgress(false);
        }
    }
}