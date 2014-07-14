package mei.ricardo.pessoa.app.ui.Safezone;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.model.people.Person;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Safezone;
import mei.ricardo.pessoa.app.ui.Fragments.Utils.DownloadImageTask;

public class ActivitySafezoneOptions extends ActionBarActivity implements View.OnClickListener {
    private static String TAG = ActivitySafezoneOptions.class.getCanonicalName();
    public static String passVarIDSafezone = "passVarIDSafezone";
    public static String returnVariableNewName = "returnVariableNewName";
    private String IDSafezone;
    private Safezone safezone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safezone);

        IDSafezone = getIntent().getExtras().getString(passVarIDSafezone);

        safezone = Safezone.getSafezoneByID(IDSafezone);
        ImageView imageViewSafezone = (ImageView) findViewById(R.id.imageViewSafezoneStreetView);
        new DownloadImageTask(imageViewSafezone)
                .execute("https://cbks0.google.com/cbk?output=thumbnail&w=400&h=400&ll=" + safezone.getLatitude() + "," + safezone.getLongitude() + "&thumb=0");

        ((TextView) findViewById(R.id.textViewAddressSafezone)).setText(safezone.getAddress());
        if (!safezone.getName().equals(safezone.getAddress())) {
            TextView TextViewname = (TextView) findViewById(R.id.textViewNameSafezone);
            TextViewname.setText(safezone.getName());
            TextViewname.setVisibility(View.VISIBLE);
        }

        Button buttonEditLocation = (Button) findViewById(R.id.buttonEditSafezone);
        buttonEditLocation.setOnClickListener(this);
        Button buttonEditRadius = (Button) findViewById(R.id.buttonEditSafezoneRadius);
        buttonEditRadius.setOnClickListener(this);
        Button buttonEditName = (Button) findViewById(R.id.buttonEditSafezoneName);
        buttonEditName.setOnClickListener(this);
        Button buttonDelete = (Button) findViewById(R.id.buttonDeleteSafezone);
        buttonDelete.setOnClickListener(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.safezone, menu);
        return true;
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

    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonDeleteSafezone) {
            Log.d(TAG, "Clicked buttonDeleteSafezone");
        } else if (view.getId() == R.id.buttonEditSafezone) {
            Log.d(TAG, "Clicked buttonEditSafezone");
        } else if (view.getId() == R.id.buttonEditSafezoneName) {
            Log.d(TAG, "Clicked buttonEditSafezoneName");
            Intent intent = new Intent(this, ActivityEditNameSafezone.class);
            //intent.putExtra(ActivityEditNameSafezone.passVarAddressName, safezone.getAddress());
            intent.putExtra(ActivityEditNameSafezone.passVarNameSafezone, safezone.getName());
            startActivityForResult(intent, PICK_CONTACT_REQUEST);
        } else if (view.getId() == R.id.buttonEditSafezoneRadius) {
            Log.d(TAG, "Clicked buttonEditSafezoneRadius");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            //Display the modified values
            Toast.makeText(this, "TODO: UPDATE SAFEZONE NAME " + data.getExtras().getString(returnVariableNewName), Toast.LENGTH_SHORT).show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
