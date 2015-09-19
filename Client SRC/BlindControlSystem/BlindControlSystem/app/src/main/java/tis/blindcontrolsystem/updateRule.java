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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.*;


public class updateRule extends ActionBarActivity {

    String server_ip;
    String port;
    Spinner tempSpinner,ambSpinner,blindSpinner,conditionSpinner;
    Button updateButton,removeButton;
    TextView tempView,ambView,blindView,conditionView;
    List<String> ruleValues = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_rule);

        server_ip = ((BlindApp)getApplicationContext()).getGlobalIP();
        port = ((BlindApp)getApplicationContext()).getGlobalPORT();

        Intent intent = getIntent();
        tempSpinner = (Spinner)findViewById(R.id.spinnerTemp);
        tempView = (TextView)findViewById(R.id.textViewTemp);
        ambSpinner = (Spinner)findViewById(R.id.spinnerAmbient);
        ambView = (TextView)findViewById(R.id.textViewAmbient);
        blindSpinner = (Spinner)findViewById(R.id.spinnerBlind);
        blindView = (TextView)findViewById(R.id.textViewBlind);
        conditionSpinner = (Spinner)findViewById(R.id.spinnerCondition);
        conditionView = (TextView)findViewById(R.id.textViewCondition);


        final String tempValue = (String)intent.getExtras().get(BlindApp.variables.Temperature.toString());
        if(tempValue == null) {
            tempView.setVisibility(GONE);
            tempSpinner.setVisibility(GONE);
        }
        else {
            tempView.setVisibility(VISIBLE);
            tempSpinner.setVisibility(VISIBLE);
            tempSpinner.setSelection(getIndex(tempSpinner, tempValue));
            ruleValues.add(tempValue);
        }

        String ambValue = (String)intent.getExtras().get(BlindApp.variables.Ambient.toString());
        if(ambValue == null) {
            ambView.setVisibility(GONE);
            ambSpinner.setVisibility(GONE);
        }
        else {
            ambView.setVisibility(VISIBLE);
            ambSpinner.setVisibility(VISIBLE);
            ambSpinner.setSelection(getIndex(ambSpinner, ambValue));
            ruleValues.add(ambValue);
        }

        String blindValue = (String)intent.getExtras().get(BlindApp.variables.Blind.toString());
        if(blindValue == null) {
            blindView.setVisibility(GONE);
            blindSpinner.setVisibility(GONE);
        }
        else {
            blindView.setVisibility(VISIBLE);
            blindSpinner.setVisibility(VISIBLE);
            blindSpinner.setSelection(getIndex(blindSpinner, blindValue ));
            ruleValues.add(blindValue);
        }

        String condition = (String)intent.getExtras().get("condition");
        if(condition.equals("NONE")) {
            conditionView.setVisibility(GONE);
            conditionSpinner.setVisibility(GONE);
        }
        else {
            conditionView.setVisibility(VISIBLE);
            conditionSpinner.setVisibility(VISIBLE);
            conditionSpinner.setSelection(getIndex(conditionSpinner, condition));
            ruleValues.add(condition);
        }



        OnClickListener buttonListener = new OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if((tempSpinner.getVisibility() == VISIBLE && !tempSpinner.getSelectedItem().equals("none")) || (ambSpinner.getVisibility() == VISIBLE && !ambSpinner.getSelectedItem().equals("none"))) {
                    List<String> selectedRuleValues = new ArrayList<>();
                    if (tempSpinner.getVisibility() == VISIBLE) {
                        selectedRuleValues.add(tempSpinner.getSelectedItem().toString());
                    }
                    if (ambSpinner.getVisibility() == VISIBLE) {
                        selectedRuleValues.add(ambSpinner.getSelectedItem().toString());
                    }
                    if (blindSpinner.getVisibility() == VISIBLE) {
                        selectedRuleValues.add(blindSpinner.getSelectedItem().toString());
                    }
                    if (conditionSpinner.getVisibility() == VISIBLE) {
                        selectedRuleValues.add(conditionSpinner.getSelectedItem().toString());
                    }
                    selectedRuleValues.removeAll(ruleValues);

                    if (!selectedRuleValues.isEmpty()) {

                        new SendJSONRequestForUpdate().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Rule Values has not been changed", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Invalid Rule Format", Toast.LENGTH_SHORT).show();
                }
            }
        };

        updateButton = (Button) findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(buttonListener);

        OnClickListener buttonListener1 = new OnClickListener(){
            @Override
            public void onClick(View v)
            {
                new SendJSONRequestForRemove().execute();
            }
        };

        removeButton = (Button) findViewById(R.id.buttonRemove);
        removeButton.setOnClickListener(buttonListener1);

    }

    public int getIndex(Spinner spin, String value)
    {
        int index = 0;

        for (int i = 0; i < spin.getCount(); i++){
            if (spin.getItemAtPosition(i).toString().equalsIgnoreCase(value)){
                index = i;
                break;
            }
        }
        return index;
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_rule, menu);
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
    }
*/
     class SendJSONRequestForUpdate extends AsyncTask<Void, String, String> {
        String response_txt;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            Map<String, Object> updateRuleMap = new HashMap<String, Object>();
            Map<String,String> rule = new HashMap<String, String>();

            if(tempView.getVisibility() == VISIBLE) {
                rule.put(BlindApp.variables.Temperature.toString(), tempSpinner.getSelectedItem().toString());
            }
            if(ambView.getVisibility() == VISIBLE) {
                rule.put(BlindApp.variables.Ambient.toString(), ambSpinner.getSelectedItem().toString());
            }
            if(blindView.getVisibility() == VISIBLE) {
                rule.put(BlindApp.variables.Blind.toString(), blindSpinner.getSelectedItem().toString());
            }

            rule.put("condition", conditionSpinner.getSelectedItem().toString());

            updateRuleMap.put(getIntent().getExtras().get("RuleNumber").toString(), rule);


            Boolean response = (Boolean)JSONHandler.testJSONRequest(server_ip+":"+port, "updateFuzzyRule", updateRuleMap, null, null);
            response_txt = response == true ? "Update Success" : "Update Failed! Try again.";
            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            Log.d("debug", result);
            Toast.makeText(getApplicationContext(), response_txt, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(updateRule.this,fuzzyRules.class);
            startActivity(intent);
        }

    }
    class SendJSONRequestForRemove extends AsyncTask<Void, String, String> {
        String response_txt;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            List<Object> ruleNumber = new ArrayList<Object>();
            ruleNumber.add(getIntent().getExtras().get("RuleNumber").toString());

            Boolean response = (Boolean)JSONHandler.testJSONRequest(server_ip+":"+port, "removeRule", null, ruleNumber, null );
            response_txt = response == true ? "Remove Success" : "Remove Failed! Try again.";
            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            Log.d("debug", result);
            Toast.makeText(getApplicationContext(), response_txt, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(updateRule.this,fuzzyRules.class);
            startActivity(intent);
        }

    }

}
