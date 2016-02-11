package it.sinergis.sunshine.suggestion.sospojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "observationhasoffering", schema = "public")
public class Observationhasoffering implements Serializable {
	@Id
	@Column(name = "observationid")
	private long observationid;
	@Id
	@Column(name = "offeringid")
	private long offeringid;
	
	public long getObservationid() {
		return observationid;
	}
	
	public void setObservationid(long observationid) {
		this.observationid = observationid;
	}
	
	public long getOfferingid() {
		return offeringid;
	}
	
	public void setOfferingid(long offeringid) {
		this.offeringid = offeringid;
	}
}