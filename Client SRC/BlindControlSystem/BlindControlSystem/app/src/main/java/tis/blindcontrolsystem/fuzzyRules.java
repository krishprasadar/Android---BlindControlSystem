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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class fuzzyRules extends ActionBarActivity {

    String server_ip;
    String port;
    Button addButton,refreshButton,removeButton;
    Integer numberOfRules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuzzy_rules);

        /*Intent intent = getIntent();
        server_ip = intent.getExtras().getString("IP");
        port = intent.getExtras().getString("PORT");*/

        server_ip = ((BlindApp)getApplicationContext()).getGlobalIP();
        port = ((BlindApp)getApplicationContext()).getGlobalPORT();



        View.OnClickListener buttonListener = new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(fuzzyRules.this, addRule.class);
                intent.putExtra("RuleNumber", numberOfRules + 1);
                startActivity(intent);
            }
        };

        addButton = (Button)findViewById(R.id.buttonAdd);
        addButton.setOnClickListener(buttonListener);

        View.OnClickListener buttonListener1 = new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                new SendJSONRequestForRefresh().execute();
            }
        };

        refreshButton = (Button)findViewById(R.id.buttonRefresh);
        refreshButton.setOnClickListener(buttonListener1);


        new SendJSONRequest().execute();
    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fuzzy_rules, menu);
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
        ArrayList<String> fuzzyRulesList;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            fuzzyRulesList = (ArrayList<String>)JSONHandler.testJSONRequest(server_ip+":"+port, "getFuzzyRules", null, null, null );
            numberOfRules = fuzzyRulesList.size();
            return fuzzyRulesList.toString();
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            Log.d("debug", result);
            ListAdapter listAdapter = new ArrayAdapter<String>(fuzzyRules.this, android.R.layout.simple_list_item_1, fuzzyRulesList);
            final ListView list = (ListView)findViewById(R.id.listView);
            list.setAdapter(listAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object o = list.getItemAtPosition(position);
                    String rule = (String)o;
                    List<String> ruleList = Arrays.asList(rule.split(" "));

                    Intent intent = new Intent(fuzzyRules.this,updateRule.class);

                    //BlindApp blinds = (BlindApp)getApplicationContext();

                    if((ruleList.contains(new Object() {
                        @Override
                        public boolean equals(Object obj) {
                            return obj.toString().contains("temperature");
                        }
                    })))
                    {
                        String tempValue =   ruleList.get(ruleList.indexOf(new Object() {
                            @Override
                            public boolean equals(Object obj) {
                                return obj.toString().contains("temperature");
                            }
                        }) + 2).replace(")","");
                        for(BlindApp.tempLinguisticTerm var : BlindApp.tempLinguisticTerm.values())
                        {
                                if(var.toString().equals(tempValue))
                                {
                                    intent.putExtra(BlindApp.variables.Temperature.toString(), var.toString());
                                }
                        }
                    }

                    if(ruleList.contains(new Object() {
                        @Override
                        public boolean equals(Object obj) {
                            return obj.toString().contains("ambient");
                        }
                    }))
                    {
                        String ambValue =  ruleList.get(ruleList.indexOf(new Object() {
                            @Override
                            public boolean equals(Object obj) {
                                return obj.toString().contains("ambient");
                            }
                        }) + 2).replace(")", "");
                        for(BlindApp.ambLinguisticTerm var : BlindApp.ambLinguisticTerm.values())
                        {
                            if(var.toString().equals(ambValue))
                            {
                                intent.putExtra(BlindApp.variables.Ambient.toString(), var.toString());
                            }
                        }
                    }

                    if(ruleList.contains(new Object() {
                        @Override
                        public boolean equals(Object obj) {
                            return obj.toString().contains("blind");
                        }
                    }))
                    {
                        String blindValue =  ruleList.get(ruleList.indexOf(new Object() {
                            @Override
                            public boolean equals(Object obj) {
                                return obj.toString().contains("blind");
                            }
                        }) + 2).replace(";", "");
                        for(BlindApp.blindLinguisticTerm var : BlindApp.blindLinguisticTerm.values())
                        {
                            if(var.toString().equals(blindValue))
                            {
                                intent.putExtra(BlindApp.variables.Blind.toString(), var.toString());
                            }
                        }
                    }


                    if(ruleList.contains("AND"))
                    {
                        intent.putExtra("condition","AND");
                    }
                    else if(ruleList.contains("OR"))
                    {
                        intent.putExtra("condition","OR");
                    }else
                    {
                        intent.putExtra("condition","NONE");
                    }

                    intent.putExtra("RuleNumber", position + 1);

                    Toast.makeText(getApplicationContext(),rule,Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            });

        }

    }

    class SendJSONRequestForRefresh extends AsyncTask<Void, String, String> {
        String response_txt;
        ArrayList<String> fuzzyRulesList;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            fuzzyRulesList = (ArrayList<String>)JSONHandler.testJSONRequest(server_ip+":"+port, "reset", null, null, null );
            return fuzzyRulesList.toString();
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            Log.d("debug", result);
            ListAdapter listAdapter = new ArrayAdapter<String>(fuzzyRules.this, android.R.layout.simple_list_item_1, fuzzyRulesList);
            final ListView list = (ListView)findViewById(R.id.listView);
            list.setAdapter(listAdapter);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object o = list.getItemAtPosition(position);
                    String rule = (String)o;
                    List<String> ruleList = Arrays.asList(rule.split(" "));

                    Intent intent = new Intent(fuzzyRules.this,updateRule.class);

                    //BlindApp blinds = (BlindApp)getApplicationContext();

                    if((ruleList.contains(new Object() {
                        @Override
                        public boolean equals(Object obj) {
                            return obj.toString().contains("temperature");
                        }
                    })))
                    {
                        String tempValue =   ruleList.get(ruleList.indexOf(new Object() {
                            @Override
                            public boolean equals(Object obj) {
                                return obj.toString().contains("temperature");
                            }
                        }) + 2).replace(")","");
                        for(BlindApp.tempLinguisticTerm var : BlindApp.tempLinguisticTerm.values())
                        {
                            if(var.toString().equals(tempValue))
                            {
                                intent.putExtra(BlindApp.variables.Temperature.toString(), var.toString());
                            }
                        }
                    }

                    if(ruleList.contains(new Object() {
                        @Override
                        public boolean equals(Object obj) {
                            return obj.toString().contains("ambient");
                        }
                    }))
                    {
                        String ambValue =  ruleList.get(ruleList.indexOf(new Object() {
                            @Override
                            public boolean equals(Object obj) {
                                return obj.toString().contains("ambient");
                            }
                        }) + 2).replace(")", "");
                        for(BlindApp.ambLinguisticTerm var : BlindApp.ambLinguisticTerm.values())
                        {
                            if(var.toString().equals(ambValue))
                            {
                                intent.putExtra(BlindApp.variables.Ambient.toString(), var.toString());
                            }
                        }
                    }

                    if(ruleList.contains(new Object() {
                        @Override
                        public boolean equals(Object obj) {
                            return obj.toString().contains("blind");
                        }
                    }))
                    {
                        String blindValue =  ruleList.get(ruleList.indexOf(new Object() {
                            @Override
                            public boolean equals(Object obj) {
                                return obj.toString().contains("blind");
                            }
                        }) + 2).replace(";", "");
                        for(BlindApp.blindLinguisticTerm var : BlindApp.blindLinguisticTerm.values())
                        {
                            if(var.toString().equals(blindValue))
                            {
                                intent.putExtra(BlindApp.variables.Blind.toString(), var.toString());
                            }
                        }
                    }

                    if(ruleList.contains("AND"))
                    {
                        intent.putExtra("condition","AND");
                    }
                    else if(ruleList.contains("OR"))
                    {
                        intent.putExtra("condition","OR");
                    }

                    intent.putExtra("RuleNumber", position + 1);

                    Toast.makeText(getApplicationContext(),rule,Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
            });

        }

    }

}
