package it.sinergis.sunshine.suggestion.Responseobject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "id", "day", "warmthreshold", "coldthreshold" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "shelter")
public class ObjShelter {
	
	@JsonProperty(value = "shelterid")
	private long id;
	
	@JsonProperty(value = "day")
	//	private ObjXmlGroup groupComposition;
	private String day;
	@JsonProperty(value = "warmthreshold")
	private Integer warmthreshold;
	@JsonProperty(value = "coldthreshold")
	private Integer coldthreshold;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getDay() {
		return day;
	}
	
	public void setDay(String day) {
		this.day = day;
	}
	
	public Integer getWarmthreshold() {
		return warmthreshold;
	}
	
	public void setWarmthreshold(Integer warmthreshold) {
		this.warmthreshold = warmthreshold;
	}
	
	public Integer getColdthreshold() {
		return coldthreshold;
	}
	
	public void setColdthreshold(Integer coldthreshold) {
		this.coldthreshold = coldthreshold;
	}
	
}