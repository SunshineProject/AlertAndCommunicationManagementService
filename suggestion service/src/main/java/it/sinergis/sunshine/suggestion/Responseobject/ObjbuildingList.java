package it.sinergis.sunshine.suggestion.Responseobject;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "buildings" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "buildings")
public class ObjbuildingList {
	
	@JsonProperty(value = "buildings")
	private List<Objbuilding> buildings;
	
	public List<Objbuilding> getBuildings() {
		return buildings;
	}
	
	public void setBuildings(List<Objbuilding> buildings) {
		this.buildings = buildings;
	}
	
}