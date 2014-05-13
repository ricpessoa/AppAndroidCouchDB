package mei.ricardo.pessoa.app.ui.user;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import mei.ricardo.pessoa.app.R;


public class RegisterActivity extends Activity {
    private static String TAG = RegisterActivity.class.getName();

    private AutoCompleteTextView mNameView, mEmailView, mUsernameView;
    private EditText mPasswordView, mPasswordConfirmView;
    private UserRegisterTask mRegisterTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNameView = (AutoCompleteTextView) findViewById(R.id.name);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordConfirmView = (EditText) findViewById(R.id.confirmpassword);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mButtonRegister = (Button) findViewById(R.id.buttonRegister);
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mEmailView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String username = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        //Check for name valid
        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isPasswordValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            mPasswordConfirmView.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirmView;
            cancel = true;
        }

        //check if the password and confirm password match
        if (!TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(password)) {
            if (!password.equals(confirmPassword)) {
                mPasswordConfirmView.setError(getString(R.string.error_dont_match_password));
                focusView = mPasswordConfirmView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mRegisterTask = new UserRegisterTask(name, username, email, password, confirmPassword);
            mRegisterTask.execute((Void) null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class UserRegisterTask extends AsyncTask<Void, Void, Integer> {

        private final String mName;
        private final String mUsername;
        private final String mEmail;
        private final String mPassword;
        private final String mPasswordConfirm;

        UserRegisterTask(String name, String username, String email, String password, String confirmPassword) {
            mName = name;
            mUsername = username;
            mEmail = email;
            mPassword = password;
            mPasswordConfirm = confirmPassword;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Application.hostUrl+Application.serviceRegisterUrl);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("name", mName));
                nameValuePairs.add(new BasicNameValuePair("username", mUsername));
                nameValuePairs.add(new BasicNameValuePair("email", mEmail));
                nameValuePairs.add(new BasicNameValuePair("password", mPassword));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);


                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String json = reader.readLine();
                JSONTokener tokener = new JSONTokener(json);
                try {
                    JSONObject finalResult = new JSONObject(tokener);
                    if (finalResult != null) {
                        Log.d(TAG, "message?" + finalResult.getString("message") + " error?" + finalResult.getString("error") + " code?" + finalResult.getInt("code"));
                        Boolean loginsuccessfull = !finalResult.getBoolean("error"); //WARNING NOT HAVE ERROR = FALSE
                        //if (loginsuccessfull == false) {
                        //    return finalResult.getInt("code");
                       // }
                        //return 1;
                        return finalResult.getInt("code");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error JSONException in Login");
                    return -4;
                } catch (NullPointerException e) {
                    Log.e(TAG, "Null Point exception on Login");
                    return -4;
                }

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "Error ClientProtocolException in Login");
                return -4;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "Error IOException in Login");
                return -3;
            }
            return -4;
        }

        /*error -1 This email already exists
        * error -2 A user with this username already exists
        * error -3 Internet connection error
        * error -4 some error occur*/
        @Override
        protected void onPostExecute(final Integer success) {
            mRegisterTask = null;
            //showProgress(false);
            DialogFragmentYesNoOk newFragment = new DialogFragmentYesNoOk(getApplicationContext());

            if (success == 1) {
                //Toast.makeText(getApplicationContext(),"Register successful",Toast.LENGTH_SHORT).show();
                newFragment.setMessage("Register successful. Go to Login?");
                newFragment.setType(2);
                newFragment.setPositiveAndNegative(getString(R.string.fire_yes), getString(R.string.fire_no));
                newFragment.setBackToPreviousActivity(true);
            } else if (success == -1) {
                //Toast.makeText(getApplicationContext(),"This email already exists",Toast.LENGTH_SHORT).show();
                newFragment.setMessage("This email already exists");
                newFragment.setType(1);
                newFragment.setPositiveAndNegative(getString(R.string.fire_ok), "");

            } else if (success == -2) {
                //Toast.makeText(getApplicationContext(),"A user with this username already exists",Toast.LENGTH_SHORT).show();
                newFragment.setMessage("A user with this username already exists");
                newFragment.setType(1);
                newFragment.setPositiveAndNegative(getString(R.string.fire_ok), "");
            } else if (success == -3) {
                //Toast.makeText(getApplicationContext(),"Verify you connection to internet",Toast.LENGTH_SHORT).show();
                newFragment.setMessage("Internet connection error");
                newFragment.setType(1);
                newFragment.setPositiveAndNegative(getString(R.string.fire_ok), "");
            } else if (success == -4) {
                Toast.makeText(getApplicationContext(), "Some error occur", Toast.LENGTH_SHORT).show();
                newFragment.setMessage("Some error occur try again later");
                newFragment.setType(1);
                newFragment.setPositiveAndNegative(getString(R.string.fire_ok), "");
            }

            newFragment.show(getFragmentManager(), "test");
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            //showProgress(false);
        }
    }
}
