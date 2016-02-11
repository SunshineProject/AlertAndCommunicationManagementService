package it.sinergis.sunshine.suggestion.Responseobject;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gson.annotations.SerializedName;

@XmlType(propOrder = { "temp", "comfortArray" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "PROFILE")
public class ObjJsonComfortProfile {
	
	@JsonProperty(value = "T")
	@SerializedName("T")
	private int temp;
	
	@JsonProperty(value = "INT")
	@SerializedName("INT")
	private List<ObjComfortArray> comfortArray;
	
	public int getTemp() {
		return temp;
	}
	
	public void setTemp(int temp) {
		this.temp = temp;
	}
	
	public List<ObjComfortArray> getComfortArray() {
		return comfortArray;
	}
	
	public void setComfortArray(List<ObjComfortArray> comfortArray) {
		this.comfortArray = comfortArray;
	}
	
}
