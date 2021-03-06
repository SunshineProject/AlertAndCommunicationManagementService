package it.sinergis.sunshine.suggestion.map4datapojo;

// Generated 6-lug-2015 9.26.45 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

/**
 * Calendar generated by hbm2java
 */
@Entity
@Table(name = "calendar", schema = "suggestion", uniqueConstraints = @UniqueConstraint(columnNames = { "buildingid",
		"day", "pilot" }))
public class Calendar implements java.io.Serializable {
	
	private int calendarid;
	private long buildingid;
	private Date day;
	private String profilecomfort;
	private String pilot;
	
	public Calendar() {
	}
	
	public Calendar(int calendarid, long buildingid, Date day, String pilot) {
		this.calendarid = calendarid;
		this.buildingid = buildingid;
		this.day = day;
		this.pilot = pilot;
	}
	
	public Calendar(int calendarid, long buildingid, Date day, String profilecomfort, String pilot) {
		this.calendarid = calendarid;
		this.buildingid = buildingid;
		this.day = day;
		this.profilecomfort = profilecomfort;
		this.pilot = pilot;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devices_seq_gen")
	@SequenceGenerator(name = "devices_seq_gen", sequenceName = "suggestion.sequence_calendarid_calendar", allocationSize = 1)
	@Column(name = "calendarid", unique = true, nullable = false)
	public int getCalendarid() {
		return this.calendarid;
	}
	
	public void setCalendarid(int calendarid) {
		this.calendarid = calendarid;
	}
	
	@Column(name = "buildingid", nullable = false)
	public long getBuildingid() {
		return this.buildingid;
	}
	
	public void setBuildingid(long buildingid) {
		this.buildingid = buildingid;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "day", nullable = false, length = 13)
	public Date getDay() {
		return this.day;
	}
	
	public void setDay(Date day) {
		this.day = day;
	}
	
	//	@Type(type = "StringJsonObject")
	@Column(name = "profilecomfort")
	public String getProfilecomfort() {
		return this.profilecomfort.toString();
	}
	
	public void setProfilecomfort(String profilecomfort) {
		this.profilecomfort = profilecomfort;
	}
	
	@Column(name = "pilot", nullable = false)
	public String getPilot() {
		return this.pilot;
	}
	
	public void setPilot(String pilot) {
		this.pilot = pilot;
	}
	
}
