package mei.ricardo.pessoa.app.ui.Safezone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mei.ricardo.pessoa.app.R;

public class ActivityEditNameSafezone extends ActionBarActivity {

    public static String passVarAddressName = "passVarAddressName";
    public static String passVarNameSafezone = "passVarNameSafezone";
    private String previousName;
    private TextView textViewName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name_safezone);
        previousName = getIntent().getExtras().getString(passVarNameSafezone);

        textViewName = (TextView) findViewById(R.id.textViewEditNameSafezone);
        textViewName.setText(previousName);

        Button buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!previousName.equals(textViewName.getText().toString())) {
                    Intent intent = new Intent();
                    intent.putExtra(ActivitySafezoneOptions.returnVariableNewName, textViewName.getText().toString());
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
