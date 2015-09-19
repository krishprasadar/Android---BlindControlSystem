package tis.blindcontrolsystem;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class mainConnect extends ActionBarActivity {
    Button btn_Connect;
    static EditText server_ip;
    static EditText port;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_connect);

        Log.d("debug","main activity launched");

        server_ip = (EditText) findViewById(R.id.editTextIp);
        port = (EditText) findViewById(R.id.editTextPort);
        status = (TextView) findViewById(R.id.textStatus);



        //Toast.makeText(getApplicationContext(),((BlindApp)getApplication()).getGlobalIP(), Toast.LENGTH_SHORT).show();

        View.OnClickListener buttonListener = new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                BlindApp blinds = (BlindApp)getApplicationContext();
                blinds.setGlobalIP(server_ip.getText().toString());
                blinds.setGlobalPORT(port.getText().toString());
                new SendJSONRequest().execute();
            }
        };

        btn_Connect = (Button) findViewById(R.id.btnConnect);
        btn_Connect.setOnClickListener(buttonListener);
    }

    class SendJSONRequest extends AsyncTask<Void, String, String> {
        String response_txt;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {
            String serverIp = server_ip.getText().toString();
            String portNumber = port.getText().toString();

            response_txt = (String)JSONHandler.testJSONRequest(serverIp+":"+portNumber, "Connect", null, null,null );
            return response_txt;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(String result) {
            Log.d("debug", result);
            Log.d("debug", response_txt);
            status = (TextView) findViewById(R.id.textStatus);
            status.setText(response_txt);
            status.setVisibility(View.VISIBLE);
            if(response_txt.contains("SUCCESS")) {
                Intent intent = new Intent(mainConnect.this, home.class);
                startActivity(intent);
            }
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_connect, menu);
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

    public void redirectToHome(View view)
    {
        Intent intent = new Intent(this, home.class);
        startActivity(intent);
    }

    /*public class blindApp extends Application
    {
        private String globalIP;
        private String globalPORT;

        public String getGlobalIP() {
            return globalIP;
        }

        public void setGlobalIP(String str) {
            globalIP = str;
        }

        public String getGlobalPORT() {
            return globalPORT;
        }

        public void setGlobalPORT(String str) {
            globalPORT = str;
        }
    }*/

}


