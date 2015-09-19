package tis.blindcontrolsystem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Krishna on 3/16/2015.
 */
public class PlaceholderFragment extends Fragment {
    public static int num = 1;
    public static List<String> data;
    public static ArrayAdapter<String> myArrayAdapter;
    public static View rootView;
    static final Handler handler = new Handler();
    public static Timer timer;
    public static  TimerTask task;
    String server_ip;
    String port;
    public static double temperature = -200;
    public List<String> tempList;
    public static Integer tempThreshold;

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feed_page, container, false);
        getActivity().getApplicationContext();

        server_ip = ((BlindApp)getActivity().getApplicationContext()).getGlobalIP();
        port = ((BlindApp)getActivity().getApplicationContext()).getGlobalPORT();
        tempList = new ArrayList<>();
        temperature = -200;
        BlindApp blinds = (BlindApp)getActivity().getApplicationContext();
        tempThreshold = blinds.getGlobalTempThreshold();
        update();

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onPause() {
        super.onStop();
        timer.cancel();
    }


    public class FetchSensorData extends AsyncTask<Void, Void, List<String>>
    {
        String response_txt;
        //private FragmentActivity fa;
        public FetchSensorData() {
            super();
            //this.fa = fa;
        }

        @Override
        protected List<String> doInBackground(Void... params) {

            response_txt = (String)JSONHandler.testJSONRequest(server_ip+":"+port, "getTemperatureAndAmbient", null, null, null);
            return Arrays.asList(response_txt);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            Double tt = Double.parseDouble(response_txt.split(" ")[0].substring(0,response_txt.split(" ")[0].length() - 1));
            if(Math.abs(tt-temperature)>=tempThreshold)
            {
                temperature = tt;
                tempList.add("Temperature - " + tt.toString().substring(0,6) + " Time - " + (new SimpleDateFormat("HH:mm:ss").format(new Date())));
                myArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,tempList);
                ListView listView = (ListView) rootView.findViewById(R.id.listFeed);
                listView.setAdapter(myArrayAdapter);
            }

        }
    }

    private void update()
    {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new FetchSensorData().execute();
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }


}

