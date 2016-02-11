package it.sinergis.sunshine.suggestion.Responseobject;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "id" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Calendar")
public class ObjCalendarIdList {
	@JsonProperty(value = "calendarid")
	private ArrayList<Integer> id;
	
	public ArrayList<Integer> getId() {
		return id;
	}
	
	public void setId(ArrayList<Integer> id) {
		this.id = id;
	}
	
}
