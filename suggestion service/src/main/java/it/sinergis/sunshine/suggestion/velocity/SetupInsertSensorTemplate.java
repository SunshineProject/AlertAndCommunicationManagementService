package it.sinergis.sunshine.suggestion.velocity;

import java.util.Hashtable;

import org.apache.log4j.Logger;

public class SetupInsertSensorTemplate {
	private static final Logger LOGGER = Logger.getLogger(SetupInsertSensorTemplate.class);
	
	//ReadFromConfig.loadByName("hostGeo")+ "ows?service=WFS&version=1.1.0&request=GetFeature&typeName=opere&srsName=EPSG:4326&outputFormat=json"
	public final String getTemplateInsertObservation(String offering, String observationidentifier, String codespace,
			String procedure, String property, String foiidentifier, String phentime, String resulttime, String uom,
			String value) {
		VelocityTemplate vt = new VelocityTemplate();
		Hashtable<String, String> hasht = new Hashtable<String, String>();
		String page = "Error creating the insertSensor request";
		
		hasht.put("offering", offering);
		hasht.put("observationidentifier", observationidentifier);
		hasht.put("codespace", codespace);
		hasht.put("procedure", procedure);
		hasht.put("property", property);
		hasht.put("foiidentifier", foiidentifier);
		hasht.put("procedure", procedure);
		hasht.put("phentime", phentime);
		hasht.put("resulttime", resulttime);
		hasht.put("uom", uom);
		hasht.put("value", value);
		
		try {
			page = vt.getRequestXmlVelocity("InsertObservationTemplateXml", hasht);
		}
		catch (Exception e) {
			LOGGER.error("Load of template, unsuccessfully");
			LOGGER.debug(e);
		}
		return page;
	}
}
