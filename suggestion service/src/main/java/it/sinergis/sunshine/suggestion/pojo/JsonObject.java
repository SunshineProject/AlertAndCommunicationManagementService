package it.sinergis.sunshine.suggestion.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "avetext", "tetaiAvg", "turnOn", "arrayTetai", "arrayWorkSystem" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "suggestion")
public class JsonObject {
	@JsonProperty(value = "avetext")
	private String avetext;
	@JsonProperty(value = "tetaiAvg")
	private String tetaiAvg;
	@JsonProperty(value = "turnOn")
	private String turnOn;
	@JsonProperty(value = "arrayTetai")
	private double[] arrayTetai;
	@JsonProperty(value = "arrayWorkSystem")
	private List<String> arrayWorkSystem;
	
	public String getAvetext() {
		return avetext;
	}
	
	public void setAvetext(String avetext) {
		this.avetext = avetext;
	}
	
	public String getTetaiAvg() {
		return tetaiAvg;
	}
	
	public void setTetaiAvg(String tetaiAvg) {
		this.tetaiAvg = tetaiAvg;
	}
	
	public String getTurnOn() {
		return turnOn;
	}
	
	public void setTurnOn(String turnOn) {
		this.turnOn = turnOn;
	}
	
	public List<String> getArrayWorkSystem() {
		return arrayWorkSystem;
	}
	
	public void setArrayWorkSystem(List<String> arrayWorkSystem) {
		this.arrayWorkSystem = arrayWorkSystem;
	}
	
	public double[] getArrayTetai() {
		return arrayTetai;
	}
	
	public void setArrayTetai(double[] arrayTetai) {
		this.arrayTetai = arrayTetai;
	}
	
}
