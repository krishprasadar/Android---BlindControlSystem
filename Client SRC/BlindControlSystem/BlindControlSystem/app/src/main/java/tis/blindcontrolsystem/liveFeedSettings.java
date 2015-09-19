package tis.blindcontrolsystem;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class liveFeedSettings extends ActionBarActivity {

    Button saveButton;
    EditText thresholdValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_feed_settings);

        BlindApp blinds = (BlindApp)getApplicationContext();
        if(blinds.getGlobalTempThreshold() == null)
        {
            thresholdValue = (EditText)findViewById(R.id.editTextThresholdValue);
            thresholdValue.setText(blinds.getGlobalTempThreshold());
        }

        View.OnClickListener buttonListener = new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                TextView thresholdValue = (TextView)findViewById(R.id.editTextThresholdValue);

                if(thresholdValue.getText().toString().matches("\\d+")) {
                    Intent intent = new Intent(liveFeedSettings.this, home.class);
                    BlindApp blinds = (BlindApp) getApplicationContext();
                    blinds.setGlobalTempThreshold(Integer.parseInt(thresholdValue.getText().toString()));
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Wrong Format.Try again", Toast.LENGTH_SHORT).show();
                }

            }
        };
        saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(buttonListener);
    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_live_feed_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
