package it.sinergis.sunshine.suggestion.map4datapojo;

// Generated 3-lug-2015 13.13.11 by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Thermalzone generated by hbm2java
 */
@Entity
@Table(name = "thermalzone"/* , schema = "ferrara" */)
public class Thermalzone implements java.io.Serializable {
	
	private int id;
	private Integer idBuilding;
	//	private Serializable thermTyp;
	private boolean validation;
	private Integer heatStart;
	private Integer heatEnd;
	private String comfUri;
	private Double comfAvg;
	private String perfMethod;
	private Integer perfDate;
	private Double perfValue;
	private Double perfValueTop;
	private Double perfValueCen;
	private Double perfValueBas;
	private String perfUom;
	//	private Serializable perfClass;
	private String corrTempCons;
	
	public Thermalzone() {
	}
	
	public Thermalzone(int id, Serializable thermTyp, boolean validation) {
		this.id = id;
		//		this.thermTyp = thermTyp;
		this.validation = validation;
	}
	
	public Thermalzone(int id, Integer idBuilding, Serializable thermTyp, boolean validation, Integer heatStart,
			Integer heatEnd, String comfUri, Double comfAvg, String perfMethod, Integer perfDate, Double perfValue,
			Double perfValueTop, Double perfValueCen, Double perfValueBas, String perfUom, Serializable perfClass,
			String corrTempCons) {
		this.id = id;
		this.idBuilding = idBuilding;
		//		this.thermTyp = thermTyp;
		this.validation = validation;
		this.heatStart = heatStart;
		this.heatEnd = heatEnd;
		this.comfUri = comfUri;
		this.comfAvg = comfAvg;
		this.perfMethod = perfMethod;
		this.perfDate = perfDate;
		this.perfValue = perfValue;
		this.perfValueTop = perfValueTop;
		this.perfValueCen = perfValueCen;
		this.perfValueBas = perfValueBas;
		this.perfUom = perfUom;
		//		this.perfClass = perfClass;
		this.corrTempCons = corrTempCons;
	}
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "id_building")
	public Integer getIdBuilding() {
		return this.idBuilding;
	}
	
	public void setIdBuilding(Integer idBuilding) {
		this.idBuilding = idBuilding;
	}
	
	//	@Column(name = "therm_typ", nullable = false)
	//	public Serializable getThermTyp() {
	//		return this.thermTyp;
	//	}
	//	
	//	public void setThermTyp(Serializable thermTyp) {
	//		this.thermTyp = thermTyp;
	//	}
	//	
	@Column(name = "validation", nullable = false)
	public boolean isValidation() {
		return this.validation;
	}
	
	public void setValidation(boolean validation) {
		this.validation = validation;
	}
	
	@Column(name = "heat_start")
	public Integer getHeatStart() {
		return this.heatStart;
	}
	
	public void setHeatStart(Integer heatStart) {
		this.heatStart = heatStart;
	}
	
	@Column(name = "heat_end")
	public Integer getHeatEnd() {
		return this.heatEnd;
	}
	
	public void setHeatEnd(Integer heatEnd) {
		this.heatEnd = heatEnd;
	}
	
	@Column(name = "comf_uri")
	public String getComfUri() {
		return this.comfUri;
	}
	
	public void setComfUri(String comfUri) {
		this.comfUri = comfUri;
	}
	
	@Column(name = "comf_avg", precision = 17, scale = 17)
	public Double getComfAvg() {
		return this.comfAvg;
	}
	
	public void setComfAvg(Double comfAvg) {
		this.comfAvg = comfAvg;
	}
	
	@Column(name = "perf_method", length = 50)
	public String getPerfMethod() {
		return this.perfMethod;
	}
	
	public void setPerfMethod(String perfMethod) {
		this.perfMethod = perfMethod;
	}
	
	@Column(name = "perf_date")
	public Integer getPerfDate() {
		return this.perfDate;
	}
	
	public void setPerfDate(Integer perfDate) {
		this.perfDate = perfDate;
	}
	
	@Column(name = "perf_value", precision = 17, scale = 17)
	public Double getPerfValue() {
		return this.perfValue;
	}
	
	public void setPerfValue(Double perfValue) {
		this.perfValue = perfValue;
	}
	
	@Column(name = "perf_value_top", precision = 17, scale = 17)
	public Double getPerfValueTop() {
		return this.perfValueTop;
	}
	
	public void setPerfValueTop(Double perfValueTop) {
		this.perfValueTop = perfValueTop;
	}
	
	@Column(name = "perf_value_cen", precision = 17, scale = 17)
	public Double getPerfValueCen() {
		return this.perfValueCen;
	}
	
	public void setPerfValueCen(Double perfValueCen) {
		this.perfValueCen = perfValueCen;
	}
	
	@Column(name = "perf_value_bas", precision = 17, scale = 17)
	public Double getPerfValueBas() {
		return this.perfValueBas;
	}
	
	public void setPerfValueBas(Double perfValueBas) {
		this.perfValueBas = perfValueBas;
	}
	
	@Column(name = "perf_uom", length = 40)
	public String getPerfUom() {
		return this.perfUom;
	}
	
	public void setPerfUom(String perfUom) {
		this.perfUom = perfUom;
	}
	
	//	@Column(name = "perf_class")
	//	public Serializable getPerfClass() {
	//		return this.perfClass;
	//	}
	//	
	//	public void setPerfClass(Serializable perfClass) {
	//		this.perfClass = perfClass;
	//	}
	
	@Column(name = "corr_temp_cons", length = 50)
	public String getCorrTempCons() {
		return this.corrTempCons;
	}
	
	public void setCorrTempCons(String corrTempCons) {
		this.corrTempCons = corrTempCons;
	}
	
}
