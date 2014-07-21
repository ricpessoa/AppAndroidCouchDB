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
        previousNotification = extras.getInt(passVarNotificationSafezone, -1);

        initNameOfSafezone();
        initNotification();

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

    private void initNotification() {
        spinnerNotifications = (Spinner) findViewById(R.id.spinnerNotifications);
        if (previousNotification != -1) {
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
        } else {
            spinnerNotifications.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.textView2)).setVisibility(View.GONE);
        }
    }

    private void initNameOfSafezone() {
        textViewName = (TextView) findViewById(R.id.textViewEditNameSafezone);
        if (previousName != null) {
            textViewName.setText(previousName);
        } else {
            textViewName.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.textView)).setVisibility(View.GONE);
        }
    }
}
