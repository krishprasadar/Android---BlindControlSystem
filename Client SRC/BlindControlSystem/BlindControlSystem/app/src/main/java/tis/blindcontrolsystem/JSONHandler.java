package tis.blindcontrolsystem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class JSONHandler {

    public static Object testJSONRequest(String server_URL_text, String method, Map<String, Object> ruleMap, List<Object> ruleNumber, Map<String, String> addRule){
        // Creating a new session to a JSON-RPC 2.0 web service at a specified URL

        Log.d("Debug serverURL", server_URL_text);

        // The JSON-RPC 2.0 server URL
        URL serverURL = null;

        try {
            serverURL = new URL("http://"+server_URL_text);

        } catch (MalformedURLException e) {
            // handle exception...
        }

        // Create new JSON-RPC 2.0 client session
        JSONRPC2Session mySession = new JSONRPC2Session(serverURL);


        // Once the client session object is created, you can use to send a series
        // of JSON-RPC 2.0 requests and notifications to it.

        // Sending an example "getTime" request:
        // Construct new request

        int requestID = 0;
        JSONRPC2Request request = null;
        // Send request
        JSONRPC2Response response = null;

        try {
            if(method != null) {
                switch (method) {
                    case "updateFuzzyRule":
                        request = new JSONRPC2Request(method, ruleMap, requestID);
                        break;
                    case "removeRule":
                        request = new JSONRPC2Request(method, ruleNumber, requestID);
                        break;
                    case "addFuzzyRule":
                        request = new JSONRPC2Request(method, ruleMap, requestID);
                        break;
                    default:
                        request = new JSONRPC2Request(method, requestID);
                        break;
                }
            }
            response = mySession.send(request);

        } catch (JSONRPC2SessionException e) {
            if(e != null) {
                Log.e("error", e.getMessage().toString());
            }
            else
                Log.e("error", "Unhandled Exception");
            // handle exception...
        }

        // Print response result / error
        if(response != null) {

            if (response.indicatesSuccess()) {
                Log.d("debug", response.getResult().toString());
                String[] methodNames = {"Connect", "getFuzzyRules", "getTemperatureAndAmbient", "updateFuzzyRule", "addFuzzyRule", "removeRule", "reset"};
                if (Arrays.asList(methodNames).contains(method))
                    return response.getResult();
                else
                    return "Unknown error";
            }
            else
            {
                Log.e("error", response.getError().getMessage().toString());
                return "Connection was established but encountered problems. Check logs. :(";
            }
        }
        else
            return "Wrong IP/PORT. Try again!";
    }

}