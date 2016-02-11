package it.sinergis.sunshine.suggestion.Responseobject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "id", "buildingid", "day", "comfortProfile", "pilot" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "building")
public class Objbuilding {
	
	@JsonProperty(value = "id")
	private int id;
	
	@JsonProperty(value = "buildingid")
	private long buildingid;
	
	@JsonProperty(value = "day")
	//	private ObjXmlGroup groupComposition;
	private String day;
	@JsonProperty(value = "profile")
	private String comfortProfile;
	@JsonProperty(value = "pilot")
	private String pilot;
	
	public long getBuildingid() {
		return buildingid;
	}
	
	public void setBuildingid(long buildingid) {
		this.buildingid = buildingid;
	}
	
	public String getComfortProfile() {
		return comfortProfile;
	}
	
	public void setComfortProfile(String comfortProfile) {
		this.comfortProfile = comfortProfile;
	}
	
	public String getPilot() {
		return pilot;
	}
	
	public void setPilot(String pilot) {
		this.pilot = pilot;
	}
	
	public String getDay() {
		return day;
	}
	
	public void setDay(String day) {
		this.day = day;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
}