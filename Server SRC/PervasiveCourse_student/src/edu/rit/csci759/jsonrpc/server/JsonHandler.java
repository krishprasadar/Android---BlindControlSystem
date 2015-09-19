package edu.rit.csci759.jsonrpc.server;

/**
 * Demonstration of the JSON-RPC 2.0 Server framework usage. The request
 * handlers are implemented as static nested classes for convenience, but in 
 * real life applications may be defined as regular classes within their old 
 * source files.
 *
 * @author Vladimir Dzhuvinov
 * @version 2011-03-05
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinShutdown;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.MessageContext;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

import edu.rit.csci759.fuzzylogic.FuzzyController;
import edu.rit.csci759.rspi.utils.MCP3008ADCReader;

public class JsonHandler {

	private static FuzzyController fuzzy = FuzzyController.getFuzzyControllerInstance();
	private static GpioController gpio = GpioFactory.getInstance();
	private static GpioPinDigitalOutput pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "MyLED1", PinState.LOW);
	private static GpioPinDigitalOutput pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "MyLED2", PinState.LOW);
	private static GpioPinDigitalOutput  pin3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "MyLED3", PinState.LOW);
	private static int flag = 0;
	
	public class TemperatureAmbientHandler implements RequestHandler {

		TempLightFuzzyObject tlfObject = new TempLightFuzzyObject();
		
		
		public TemperatureAmbientHandler() {

		}

		public String[] handledRequests() {

			return new String[] { "Connect","getFuzzyRules","getTemperatureAndAmbient" ,"updateFuzzyRule","addFuzzyRule","removeRule","reset"};
		}

		public JSONRPC2Response process(JSONRPC2Request req, MessageContext ctx) {

			String hostName = "unknown";
			try {
				hostName = InetAddress.getLocalHost().getHostName();
				DateFormat df = DateFormat.getTimeInstance();
				String time = df.format(new Date());
				tlfObject.setHostName(hostName);
				tlfObject.setTimeStamp(time);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

			if (req.getMethod().equals("Connect")) {
				return new JSONRPC2Response("SUCCESS", req.getID());
			}

			else if(req.getMethod().equals("getTemperatureAndAmbient")){
				getDataFromRaspberryPiServer();
				String tmpAmbientValue = getTemperatureAndAmbient();
				return new JSONRPC2Response(tmpAmbientValue, req.getID());
			}

			else if(req.getMethod().equals("getFuzzyRules")){
				List<String> rules = getFuzzyRules();
				return new JSONRPC2Response(rules, req.getID());
			}
			
			else if(req.getMethod().equals("removeRule")){

				List<Object> removeObjectList =  req.getPositionalParams();
				boolean ruleRemoved = removeFuzzyRule(removeObjectList.get(0));
				
				return new JSONRPC2Response(ruleRemoved, req.getID());
			}
			
			else if(req.getMethod().equals("reset")){

				List<String> actualRuleList = resetAllFuzzyRules();
				return new JSONRPC2Response(actualRuleList, req.getID());
			}
			
			else if(req.getMethod().equals("addFuzzyRule")){

				Map<String,Object> returnedMap =  req.getNamedParams();
				boolean rulesUpdated = false ;
				Map<String,String> addRuleMap = new HashMap<String,String>();
				
				for(Entry<String,Object> e : returnedMap.entrySet()){

					String value = (String) e.getValue();
					addRuleMap.put(e.getKey(),value);
				}

				rulesUpdated = (addFuzzyRule(addRuleMap));
				return new JSONRPC2Response(rulesUpdated, req.getID());
			}

			else if(req.getMethod().equals("updateFuzzyRule")){

				Map<String,Object> returnedMap =  req.getNamedParams();
				Map<String, Map<String,String>> updateRuleMap = new HashMap<String, Map<String,String>>();
				boolean rulesUpdated = false ;

				for(Entry<String,Object> e : returnedMap.entrySet()){

					@SuppressWarnings("unchecked")

					Map<String,String> map = (Map<String, String>) e.getValue();

					updateRuleMap.put(e.getKey(), map);
					System.out.println(updateRuleMap);
				}
				rulesUpdated = updateFuzzyRules(updateRuleMap);
				return new JSONRPC2Response(rulesUpdated, req.getID());
			}

			else {
				return new JSONRPC2Response(JSONRPC2Error.METHOD_NOT_FOUND,req.getID());
			}
		}

		/**
		 * reset Fuzzy Rules. Load again from file.
		 * @return
		 */
		private List<String> resetAllFuzzyRules() {
			return fuzzy.resetToInitialRules();
		}

		/**
		 * remove a Fuzzy Rule.
		 * @param object
		 * @return
		 */
		private boolean removeFuzzyRule(Object object) {
			return fuzzy.removeRule(object);
		}

		/**
		 * update A particular Fuzzy Rule.
		 * @param updateRuleMap
		 * @return
		 */
		private boolean updateFuzzyRules(Map< String, Map<String,String>> updateRuleMap) {
			return fuzzy.updateRule(updateRuleMap);
		}

		/**
		 * Add a new Fuzzy Rule.
		 * @param addRuleMap
		 * @return
		 */
		private boolean addFuzzyRule(Map<String,String> addRuleMap) {
			return fuzzy.addRule(addRuleMap);
		}

		/**
		 * get list of Fuzzy Rules.
		 * @return
		 */
		private List<String> getFuzzyRules() {
			return fuzzy.getFuzzyRules();
		}

		/**
		 * get Temperature and Ambient
		 * @return
		 */
		private String getTemperatureAndAmbient() {
			TempLightFuzzyObject tlfObject = getDataFromRaspberryPiServer();
			return tlfObject.getTemperature()+"C"+" "+tlfObject.getAmbient();
		}

		public TempLightFuzzyObject getDataFromRaspberryPiServer(){

			final boolean DEBUG = false;
			
			if(flag==0)
			{		
				MCP3008ADCReader.initSPI(gpio);
				flag++;
			}
			
			int adc_ambient = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH1.ch());
			int ambient = (int)(adc_ambient / 10.24); 

			if (DEBUG){
				System.out.println("readAdc:" + Integer.toString(adc_ambient) + 
						" (0x" + MCP3008ADCReader.lpad(Integer.toString(adc_ambient, 16).toUpperCase(), "0", 2) + 
						", 0&" + MCP3008ADCReader.lpad(Integer.toString(adc_ambient, 2), "0", 8) + ")");        
				System.out.println("Ambient:" + ambient + "/100 (" + adc_ambient + "/1024)");
			}

			int adc_temperature = MCP3008ADCReader.readAdc(MCP3008ADCReader.MCP3008_input_channels.CH0.ch());

			int temperature = (int)(adc_temperature / 10.24); 

			if (DEBUG){
				System.out.println("readAdc:" + Integer.toString(adc_temperature) + 
						" (0x" + MCP3008ADCReader.lpad(Integer.toString(adc_temperature, 16).toUpperCase(), "0", 2) + 
						", 0&" + MCP3008ADCReader.lpad(Integer.toString(adc_temperature, 2), "0", 8) + ")");        
				System.out.println("Temperature:" + temperature + "/100 (" + adc_temperature + "/1024)");
			}

			double tmp36_mVolts =(double) (adc_temperature * (3300.0/1024.0));
			double tempC = (double) (((tmp36_mVolts - 100.0) / 10.0) - 40.0);
			double tempF = (double) ((tempC * 9.0 / 5.0) + 32);

			System.out.println("Ambient:" + ambient + "/100; Temperature:"+temperature+"/100 => "+String.valueOf(tempC)+"C => "+String.valueOf(tempF)+"F");

			try {
				Thread.sleep(500L); 
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace(); 
			}

			String blindStatus = fuzzy.initiateFuzzyProcessAndGetBlindStatus(tempC,ambient);
			System.out.println("Blind Status = " +blindStatus);
			List<String> rules = fuzzy.getFuzzyRules();

			tlfObject.setTemperature(tempC);
			tlfObject.setAmbient(ambient);
			tlfObject.setRules(rules);
			tlfObject.setBlindStatus(blindStatus);

			blinkLEDBasedOnBlindStatus(blindStatus,gpio);
			gpio.shutdown();
			//pin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
			
			return tlfObject;
		}

		private void blinkLEDBasedOnBlindStatus(String blindValue,GpioController gpio) {

			System.out.println(blindValue);
			
			
			if(blindValue == null) {
				pin1.toggle();
			}
			else if(blindValue.equals("open"))
				pin1.high();
			else if(blindValue.equals("half"))
				pin2.high();
			else if(blindValue.equals("close"))
				pin3.high();
			
		}


	}
}

