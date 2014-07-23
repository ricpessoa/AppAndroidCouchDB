package mei.ricardo.pessoa.app.ui.Sensor.Safezone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import mei.ricardo.pessoa.app.R;

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
