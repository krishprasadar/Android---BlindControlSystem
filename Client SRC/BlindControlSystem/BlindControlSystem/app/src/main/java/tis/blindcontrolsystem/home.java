package tis.blindcontrolsystem;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


public class home extends ActionBarActivity {
    String server_ip;
    String port;
    TextView tempView;
    TextView ambientView;
    Button fuzzyRulesButton,buttonLiveFeed, buttonLiveFeedSettings;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tempView =(TextView) findViewById(R.id.tempValue);
        ambientView = (TextView)findViewById(R.id.ambientValue);
        fuzzyRulesButton = (Button)findViewById(R.id.buttonfuzzyRules);

        //Toast.makeText(getApplicationContext(),mainConnect.server_ip.getText().toString(),Toast.LENGTH_SHORT).show();

        server_ip = ((BlindApp)getApplicationContext()).getGlobalIP();
        port = ((BlindApp)getApplicationContext()).getGlobalPORT();

        View.OnClickListener buttonListener = new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(home.this, fuzzyRules.class);
                startActivity(intent);
            }
        };

        fuzzyRulesButton = (Button) findViewById(R.id.buttonfuzzyRules);
        fuzzyRulesButton.setOnClickListener(buttonListener);

        View.OnClickListener buttonListener1 = new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(home.this, feedPage.class);
                startActivity(intent);
            }
        };

        buttonLiveFeed = (Button) findViewById(R.id.buttonLiveFeed);
        buttonLiveFeed.setOnClickListener(buttonListener1);

        View.OnClickListener buttonListener2 = new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(home.this, liveFeedSettings.class);
                startActivity(intent);
            }
        };

        buttonLiveFeedSettings = (Button) findViewById(R.id.buttonLiveFeedSettings);
        buttonLiveFeedSettings.setOnClickListener(buttonListener2);

        new SendJSONRequest().execute();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    class SendJSONRequest extends AsyncTask<Void, String, String> {
        String response_txt;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            response_txt = (String)JSONHandler.testJSONRequest(server_ip+":"+port, "getTemperatureAndAmbient", null, null, null );
            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            Log.d("debug", result);
            Log.d("debug", response_txt);
            response_txt = response_txt.replace("C","");
            Double temp = Double.parseDouble(response_txt.split(" ")[0]);
            temp = Math.round(temp * 100D) / 100D;

            tempView.setText(temp.toString() + " F");
            ambientView.setText(response_txt.split(" ")[1]);
        }

    }

}
