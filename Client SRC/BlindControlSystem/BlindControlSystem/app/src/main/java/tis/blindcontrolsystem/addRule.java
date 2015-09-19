package tis.blindcontrolsystem;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class addRule extends ActionBarActivity {
    String server_ip;
    String port;
    Button addRuleButton;
    Spinner tempSpinner,ambSpinner,blindSpinner,conditionSpinner;
    TextView conditionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rule);

        server_ip = ((BlindApp)getApplicationContext()).getGlobalIP();
        port = ((BlindApp)getApplicationContext()).getGlobalPORT();

        View.OnClickListener buttonListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!(tempSpinner.getSelectedItem().equals("none") && ambSpinner.getSelectedItem().equals("none"))) {
                    new SendJSONRequest().execute();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Invalid Rule Format", Toast.LENGTH_SHORT).show();
                }

            }
        };

        addRuleButton = (Button)findViewById(R.id.buttonAddRule);
        addRuleButton.setOnClickListener(buttonListener);

        tempSpinner = (Spinner)findViewById(R.id.spinnerTemp);
        ambSpinner = (Spinner)findViewById(R.id.spinnerAmbient);
        blindSpinner = (Spinner)findViewById(R.id.spinnerBlind);
        conditionSpinner = (Spinner)findViewById(R.id.spinnerCondition);
        conditionView = (TextView)findViewById(R.id.textViewCondition);

        tempSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(tempSpinner.getSelectedItem().toString().equals("none") || ambSpinner.getSelectedItem().toString().equals("none")){
                        conditionSpinner.setVisibility(View.GONE);
                        conditionView.setVisibility(View.GONE);
                }
                else
                {
                    conditionView.setVisibility(View.VISIBLE);
                    conditionSpinner.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        ambSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(ambSpinner.getSelectedItem().toString().equals("none") || tempSpinner.getSelectedItem().toString().equals("none") ){
                    conditionSpinner.setVisibility(View.GONE);
                    conditionView.setVisibility(View.GONE);
                }
                else
                {
                    conditionView.setVisibility(View.VISIBLE);
                    conditionSpinner.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_rule, menu);
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
            Map<String,Object> addRuleMap = new HashMap<String, Object>();

            if(!tempSpinner.getSelectedItem().toString().equals("none")) {
                addRuleMap.put(BlindApp.variables.Temperature.toString(), tempSpinner.getSelectedItem().toString());
            }
            if(!ambSpinner.getSelectedItem().toString().equals("none")) {
                addRuleMap.put(BlindApp.variables.Ambient.toString(), ambSpinner.getSelectedItem().toString());
            }
            if(!blindSpinner.getSelectedItem().toString().equals("none")) {
                addRuleMap.put(BlindApp.variables.Blind.toString(), blindSpinner.getSelectedItem().toString());
            }

            if(!addRuleMap.isEmpty()) {

                addRuleMap.put("condition",conditionSpinner.getSelectedItem().toString());

                Boolean response = (Boolean) JSONHandler.testJSONRequest(server_ip + ":" + port, "addFuzzyRule", addRuleMap, null, null );
                response_txt = response == true ? "Add Rule Success" : "Rule exists!";
            }
            else {
                response_txt = "No values selected";
            }
            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            Log.d("debug", result);
            Toast.makeText(getApplicationContext(), response_txt, Toast.LENGTH_SHORT).show();
            if(response_txt.contains("Success")) {
                Intent intent = new Intent(addRule.this, fuzzyRules.class);
                startActivity(intent);
            }
        }

    }

}
