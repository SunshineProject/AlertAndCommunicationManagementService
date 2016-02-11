package it.sinergis.sunshine.suggestion.Responseobject;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "thresholds" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "thresholds")
public class ObjThresholdList {
	@JsonProperty(value = "thresholds")
	private List<ObjShelter> thresholds;
	
	public List<ObjShelter> getThresholds() {
		return thresholds;
	}
	
	public void setThresholds(List<ObjShelter> thresholds) {
		this.thresholds = thresholds;
	}
	
}
