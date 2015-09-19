package edu.rit.csci759.jsonrpc.client;

//The Client sessions package
import java.net.MalformedURLException;
//For creating URLs
import java.net.URL;


//The Base package for representing JSON-RPC 2.0 messages
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;
//The JSON Smart package for JSON encoding/decoding (optional)

import edu.rit.csci759.jsonrpc.server.TempLightFuzzyObject;



public class JsonRPCClient {


	public static void main(String[] args) {

		URL serverURL = null;

		try {
			serverURL = new URL("http://10.10.10.103:8080");

		} catch (MalformedURLException e) {
		}

		JSONRPC2Session mySession = new JSONRPC2Session(serverURL);

		String method = "getDataFromRashberryPi";
		int requestID = 0;
		JSONRPC2Request request = new JSONRPC2Request(method, requestID);

		// Send request
		JSONRPC2Response response = null;

		try {
			response = mySession.send(request);

		} catch (JSONRPC2SessionException e) {

		System.err.println(e.getMessage());
		}

		if (response.indicatesSuccess()){
			TempLightFuzzyObject tlfclientObject = (TempLightFuzzyObject)response.getResult();
			System.out.println(tlfclientObject);
		}else
			System.out.println(response.getError().getMessage());
	
	}
}