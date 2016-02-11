package it.sinergis.sunshine.suggestion.velocity;

import it.sinergis.sunshine.suggestion.utils.Functions;

import java.text.DecimalFormat;
import java.util.Hashtable;

import org.apache.log4j.Logger;

public class SetupEmailTemplate {
	private static final Logger LOGGER = Logger.getLogger(SetupEmailTemplate.class);
	private static DecimalFormat df2 = new DecimalFormat(".##");
	
	public final String getTemplateEmail(String dateNow, String temperatureRequest, String Tmin, String Tmed,
			String Tmax, String buildingName, double[] tetailast, int[] modality, String turnOnHour,
			String turnOnValue, String startComfort, String endComfort) {
		VelocityTemplate vt = new VelocityTemplate();
		Hashtable<String, String> hasht = new Hashtable<String, String>();
		String page = "Error creating the Email request";
		
		hasht.put("dateNow", dateNow);
		hasht.put("temperatureRequest", temperatureRequest);
		hasht.put("Tmin", Tmin);
		hasht.put("Tmed", Tmed);
		hasht.put("Tmax", Tmax);
		
		hasht.put("buildingName", buildingName);
		hasht.put("tetailast0", String.valueOf(df2.format(tetailast[0])));
		hasht.put("tetailast1", String.valueOf(df2.format(tetailast[1])));
		hasht.put("tetailast2", String.valueOf(df2.format(tetailast[2])));
		hasht.put("tetailast3", String.valueOf(df2.format(tetailast[3])));
		hasht.put("tetailast4", String.valueOf(df2.format(tetailast[4])));
		hasht.put("tetailast5", String.valueOf(df2.format(tetailast[5])));
		hasht.put("tetailast6", String.valueOf(df2.format(tetailast[6])));
		hasht.put("tetailast7", String.valueOf(df2.format(tetailast[7])));
		hasht.put("tetailast8", String.valueOf(df2.format(tetailast[8])));
		hasht.put("tetailast9", String.valueOf(df2.format(tetailast[9])));
		hasht.put("tetailast10", String.valueOf(df2.format(tetailast[10])));
		hasht.put("tetailast11", String.valueOf(df2.format(tetailast[11])));
		hasht.put("tetailast12", String.valueOf(df2.format(tetailast[12])));
		hasht.put("tetailast13", String.valueOf(df2.format(tetailast[13])));
		hasht.put("tetailast14", String.valueOf(df2.format(tetailast[14])));
		hasht.put("tetailast15", String.valueOf(df2.format(tetailast[15])));
		hasht.put("tetailast16", String.valueOf(df2.format(tetailast[16])));
		hasht.put("tetailast17", String.valueOf(df2.format(tetailast[17])));
		hasht.put("tetailast18", String.valueOf(df2.format(tetailast[18])));
		hasht.put("tetailast19", String.valueOf(df2.format(tetailast[19])));
		hasht.put("tetailast20", String.valueOf(df2.format(tetailast[20])));
		hasht.put("tetailast21", String.valueOf(df2.format(tetailast[21])));
		hasht.put("tetailast22", String.valueOf(df2.format(tetailast[22])));
		hasht.put("tetailast23", String.valueOf(df2.format(tetailast[23])));
		hasht.put("turnOnHour", turnOnHour);
		hasht.put("turnOnValue", turnOnValue);
		hasht.put("startComfort", startComfort);
		hasht.put("endComfort", endComfort);
		hasht.put("stringHtmlModality", Functions.getPeriodFromModality(modality, startComfort, endComfort, turnOnHour));
		LOGGER.info("Creating email template");
		
		//		int ii = 0;
		//		
		//		for (int mod : modality) {
		//			LOGGER.error("Modality " + ii + "  = " + mod);
		//			if (mod == 0) {
		//				hasht.put("modality" + String.valueOf(ii), "OFF");
		//			}
		//			else {
		//				hasht.put("modality" + String.valueOf(ii), "ON");
		//			}
		//			ii = ii + 1;
		//		}
		
		try {
			page = vt.getRequestXmlVelocity("EmailTemplate", hasht);
		}
		catch (Exception e) {
			LOGGER.error("Load of template, unsuccessfully");
			LOGGER.debug(e);
		}
		return page;
	}
}
