package edu.rit.csci759.jsonrpc.server;

import java.io.Serializable;
import java.util.List;

public class TempLightFuzzyObject implements Serializable {
	
	private static final long serialVersionUID = 7796319961591864995L;
	private String hostName;
	private String timeStamp;
	private double temperature;
	private double ambient;
	private String blindStatus;
	private List<String> rules;
	

	public double getTemperature() {
		return temperature;
	}
	
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public double getAmbient() {
		return ambient;
	}
	public void setAmbient(double ambient) {
		this.ambient = ambient;
	}
	public String getBlindStatus() {
		return blindStatus;
	}
	public void setBlindStatus(String blindStatus) {
		this.blindStatus = blindStatus;
	}
	public List<String> getRules() {
		return rules;
	}
	public void setRules(List<String> rules) {
		this.rules = rules;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return "TempLightFuzzyObject [hostName=" + hostName + ", timeStamp="
				+ timeStamp + ", temperature=" + temperature + ", ambient="
				+ ambient + ", blindStatus=" + blindStatus + ", rules=" + rules
				+ "]";
	}

}
