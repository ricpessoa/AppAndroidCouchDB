package mei.ricardo.pessoa.app.ui.Safezone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import mei.ricardo.pessoa.app.R;
import mei.ricardo.pessoa.app.couchdb.modal.Safezone;

public class ActivityEditNameSafezone extends ActionBarActivity {
    private static String TAG = ActivitySafezoneOptions.class.getCanonicalName();
    public static String passVarNameSafezone = "passVarNameSafezone";
    public static String passVarNotificationSafezone = "passVarNotificationSafezone";
    private String previousName;
    private int previousNotification;
    private TextView textViewName;
    private Spinner spinnerNotifications;
    final CharSequence optionNotifications[] = new CharSequence[]{"None", "Check-in only", "Check-out only", "Check-in and Check-out"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name_safezone);
        Bundle extras = getIntent().getExtras();
        previousName = extras.getString(passVarNameSafezone);
        previousNotification = extras.getInt(passVarNotificationSafezone);
        textViewName = (TextView) findViewById(R.id.textViewEditNameSafezone);
        textViewName.setText(previousName);


        spinnerNotifications = (Spinner) findViewById(R.id.spinnerNotifications);
// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.planets_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, optionNotifications);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerNotifications.setAdapter(adapter);

        if (previousNotification != 0) {
            spinnerNotifications.setSelection(previousNotification);
        }

        Log.d(TAG, "Select Spinner" + spinnerNotifications.getSelectedItemPosition());

//        setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    Toast.makeText(getApplicationContext(), "Not valid option", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        textViewNotification = (TextView) findViewById(R.id.editTextNotifications);
//        textViewNotification.setText(Safezone.typeNotifications[previousNotification]);
//        textViewNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                builder.setTitle("Select a Notification Settings");
//                builder.setItems(optionNotifications, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // the user clicked on colors[which]
//                        Toast.makeText(getApplicationContext(), "clicked " + optionNotifications[which], Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent();
//                        intent.putExtra(ActivitySafezoneOptions.returnVariableNewNotification, Safezone.typeNotifications[which]);
//                        setResult(RESULT_OK, intent);
//                        finish();
//                    }
//                });
//                builder.show();
//            }
//        });

        Button buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                if (previousName != null && !previousName.equals(textViewName.getText().toString())) {
                    intent.putExtra(ActivitySafezoneOptions.returnVariableNewName, textViewName.getText().toString());
                    setResult(RESULT_OK, intent);
                } else if (previousNotification != spinnerNotifications.getSelectedItemPosition()) {
                    Toast.makeText(getApplicationContext(), "Change spiner", Toast.LENGTH_SHORT).show();
                    intent.putExtra(ActivitySafezoneOptions.returnVariableNewNotification, spinnerNotifications.getSelectedItemPosition());
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_edit_name_safezone, menu);
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
}
