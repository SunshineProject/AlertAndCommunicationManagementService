package it.sinergis.sunshine.suggestion.Responseobject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gson.annotations.SerializedName;

@XmlType(propOrder = { "dah", "dam", "ah", "am" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ARRAYPROFILE")
public class ObjComfortArray {
	
	//	@JsonProperty(value = "dah")
	@JsonProperty(value = "from_hh")
	@SerializedName("from_hh")
	private Integer dah;
	//	@JsonProperty(value = "dam")
	@JsonProperty(value = "from_mm")
	@SerializedName("from_mm")
	private Integer dam;
	//	@JsonProperty(value = "ah")
	@JsonProperty(value = "to_hh")
	@SerializedName("to_hh")
	private Integer ah;
	//	@JsonProperty(value = "am")
	@JsonProperty(value = "to_mm")
	@SerializedName("to_mm")
	private Integer am;
	
	public Integer getDah() {
		return dah;
	}
	
	public void setDah(Integer dah) {
		this.dah = dah;
	}
	
	public Integer getDam() {
		return dam;
	}
	
	public void setDam(Integer dam) {
		this.dam = dam;
	}
	
	public Integer getAh() {
		return ah;
	}
	
	public void setAh(Integer ah) {
		this.ah = ah;
	}
	
	public Integer getAm() {
		return am;
	}
	
	public void setAm(Integer am) {
		this.am = am;
	}
}
