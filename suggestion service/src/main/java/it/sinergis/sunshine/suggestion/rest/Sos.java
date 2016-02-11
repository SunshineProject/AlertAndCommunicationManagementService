package it.sinergis.sunshine.suggestion.rest;

import it.sinergis.sunshine.suggestion.delegate.DelegatedatabaseSos;
import it.sinergis.sunshine.suggestion.delegate.Delegatedatabasemap4data;
import it.sinergis.sunshine.suggestion.map4datapojo.BuildingMap4;
import it.sinergis.sunshine.suggestion.map4datapojo.ShelterMap4;
import it.sinergis.sunshine.suggestion.sospojo.TaskStatus;
import it.sinergis.sunshine.suggestion.utils.Functions;
import it.sinergis.sunshine.suggestion.utils.Httprequest;
import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class Sos {
	static final Logger LOGGER = Logger.getLogger(Sos.class);
	
	public boolean writeObservation(double[] tetailast, int[] modality, String timeTurnOn, String pilot,
			TaskStatus task, BuildingMap4 building, String foiUri, String endTimeComfort) {
		boolean responseStatus = true;
		Httprequest client = new Httprequest();
		Map<String, String> map = Functions.getSetupSos(pilot);
		String offering = ReadFromConfig.loadByName("radiceOffering") + building.getTempOutputUri();
		String offeringStato = ReadFromConfig.loadByName("radiceOffering") + building.getOnofOutputUri();
		String codespace = map.get("codespace");
		String procedure = ReadFromConfig.loadByName("procedureradix") + building.getTempOutputUri().split("_")[0];
		String foiidentifier = foiUri;
		String uom = ReadFromConfig.loadByName("uomTetai");
		String property = ReadFromConfig.loadByName("propertyTetai");
		String propertyStato = ReadFromConfig.loadByName("propertyModality");
		String uomStato = ReadFromConfig.loadByName("uomModality");
		
		//"http://www.sunshineproject.eu/swe/observation/LAM-M03_IRRA_WM2_1h/2015-07-19 19:00:00"
		DateFormat outputFormatterObseravtion = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//		DateFormat outputFormatterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		DateFormat outputFormatterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Calendar cal = java.util.Calendar.getInstance();
		//		TimeZone tz = TimeZone.getTimeZone("GMT+0");
		//		cal.setTimeZone(tz);
		String phentime = outputFormatterObseravtion.format(new Date());
		try {
			cal.setTime(outputFormatterObseravtion.parse(phentime));
		}
		catch (ParseException e) {
			LOGGER.error("Error in parsing format date [profile]", e);
		}
		cal.add(java.util.Calendar.DATE, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		
		DelegatedatabaseSos sosdb = new DelegatedatabaseSos();
		
		for (Double tetaiesimo : tetailast) {
			phentime = outputFormatterObseravtion.format(cal.getTime());
			String phentimeT = outputFormatterDate.format(cal.getTime());
			cal.add(Calendar.HOUR_OF_DAY, 1);
			String resulttime = phentimeT; //dupplico per chiarezza nel passaggio parametri
			String observationidentifier = ReadFromConfig.loadByName("radiceObservation") + building.getTempOutputUri()
					+ "/" + phentime;
			String result = null;
			try {
				boolean done = false;
				int count = 0;
				int limit = Integer.parseInt(ReadFromConfig.loadByName("maxCount"));
				while (!done && count < limit) {
					result = client.postInsertObservation(ReadFromConfig.loadByName("uriSos"), offering,
							observationidentifier, codespace, procedure, property, foiidentifier, phentimeT,
							resulttime, uom, String.valueOf(tetaiesimo));
					if (result != null) {
						done = true;
					}
					else {
						count = count + 1;
					}
				}
				
			}
			catch (Exception e) {
				LOGGER.error("Connection refused.", e);
			}
			LOGGER.info("Sos response");
			LOGGER.info(result);
			//			if (!result.equalsIgnoreCase(ReadFromConfig.loadByName("resultSos"))) {
			//				sosdb.writeTaskStatusUpdate(null, null, task, "error", result);
			//				responseStatus = false;
			//			}
		}
		phentime = outputFormatterObseravtion.format(new Date());
		try {
			cal.setTime(outputFormatterObseravtion.parse(phentime));
		}
		catch (ParseException e) {
			LOGGER.error("Error in parsing format date [profile]", e);
		}
		cal.add(java.util.Calendar.DATE, 1);
		
		if (timeTurnOn != null) { // se il turn on non c'e', significa che non occorre scrivere nulla sul sos
			ArrayList<String> coupleTime = Functions.getPeriodFromModalityArray(modality, endTimeComfort,
					Functions.getNormalizeHours(Double.parseDouble(timeTurnOn)));
			int value = 1;
			for (int hh = 0; hh < coupleTime.size(); hh++) {
				String hour = coupleTime.get(hh);
				cal.set(Calendar.MINUTE, Integer.parseInt(hour.split(":")[1]));
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.split(":")[0]));
				//				cal.set(Calendar.HOUR, Integer.parseInt(hour.split(":")[0]));
				phentime = outputFormatterObseravtion.format(cal.getTime());
				String phentimeT = outputFormatterDate.format(cal.getTime());
				String resulttime = phentimeT; //dupplico per chiarezza nel passaggio parametri
				String observationidentifier = ReadFromConfig.loadByName("radiceObservation")
						+ building.getOnofOutputUri() + "/" + phentime;
				String result = null;
				try {
					boolean done = false;
					int count = 0;
					int limit = Integer.parseInt(ReadFromConfig.loadByName("maxCount"));
					while (!done && count < limit) {
						result = client.postInsertObservation(ReadFromConfig.loadByName("uriSos"), offeringStato,
								observationidentifier, codespace, procedure, propertyStato, foiidentifier, phentimeT,
								resulttime, uomStato, String.valueOf(value));
						if (result != null) {
							done = true;
						}
						else {
							count = count + 1;
						}
					}
				}
				catch (Exception e) {
					LOGGER.error("Connection refused.", e);
				}
				if (value == 0) {
					value = 1;
				}
				else {
					value = 0;
				}
				
				LOGGER.info("Sos response state");
				LOGGER.info(result);
			}
		}
		else {
			LOGGER.info("timeTurnOn not present for building " + building.getGid() + " - " + building.getName());
		}
		
		//		for (Integer modalityesimo : modality) {
		//			phentime = outputFormatterObseravtion.format(cal.getTime());
		//			cal.add(Calendar.HOUR_OF_DAY, 1);
		//			String phentimeT = outputFormatterDate.format(cal.getTime());
		//			String resulttime = phentimeT; //dupplico per chiarezza nel passaggio parametri
		//			String observationidentifier = ReadFromConfig.loadByName("radiceObservation") + building.getOnofOutputUri()
		//					+ "/" + phentime;
		//			String result = null;
		//			try {
		//				result = client.postInsertObservation(ReadFromConfig.loadByName("uriSos"), offeringStato,
		//						observationidentifier, codespace, procedure, propertyStato, foiidentifier, phentimeT,
		//						resulttime, uomStato, String.valueOf(modalityesimo));
		//			}
		//			catch (Exception e) {
		//				LOGGER.error("Connection refused.", e);
		//			}
		//			
		//			LOGGER.info("Sos response state");
		//			LOGGER.info(result);
		//			//			if (!result.equalsIgnoreCase(ReadFromConfig.loadByName("resultSos"))) {
		//			//				sosdb.writeTaskStatusUpdate(null, null, task, "error", result);
		//			//				responseStatus = false;
		//			//			}
		//			
		//		}
		//		//Ora scrivo il timeTurnOn che fa sempre parte del modality
		//		
		//		if (timeTurnOn != null) {
		//			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeTurnOn.split("\\.")[0]) * 10);
		//			cal.set(Calendar.MINUTE, Integer.parseInt(timeTurnOn.split("\\.")[1]) * 10);
		//			String phentimeT = outputFormatterDate.format(cal.getTime());
		//			String resulttime = phentimeT; //dupplico per chiarezza nel passaggio parametri
		//			String observationidentifier = ReadFromConfig.loadByName("radiceObservation") + map.get("offeringStato")
		//					+ "/" + phentime;
		//			String result = null;
		//			try {
		//				result = client.postInsertObservation(ReadFromConfig.loadByName("uriSos"), offeringStato,
		//						observationidentifier, codespace, procedure, propertyStato, foiidentifier, phentimeT,
		//						resulttime, uomStato, "1");
		//			}
		//			catch (Exception e) {
		//				LOGGER.error("Connection refused.", e);
		//			}
		//			LOGGER.info("Sos response TurnOntime");
		//			LOGGER.info(result);
		//			if (!result.equalsIgnoreCase(ReadFromConfig.loadByName("resultSos"))) {
		//				sosdb.writeTaskStatusUpdate(null, null, task, "error", result);
		//				responseStatus = false;
		//			}
		//}
		
		return responseStatus;
	}
	
	public boolean writeObservationShelter(int shelterid, String pilot, int countcond, int countheat,
			List<Double> listaTemp) {
		boolean responseStatus = true;
		
		Delegatedatabasemap4data dbmap4 = new Delegatedatabasemap4data();
		ShelterMap4 shelterMap = dbmap4.readParameterShelter(shelterid, null, null, pilot);
		Httprequest client = new Httprequest();
		String offering = ReadFromConfig.loadByName("radiceOffering") + shelterMap.getUriofferingcountcold();
		String codespace = ReadFromConfig.loadByName("codespaceShelter");
		String procedure = ReadFromConfig.loadByName("radiceProcedure") + shelterMap.getUriprocedure();
		String foiidentifier = shelterMap.getUrifoi();
		String uom = ReadFromConfig.loadByName("uomCont");
		String property = ReadFromConfig.loadByName("propertyContCold");
		DateFormat outputFormatterObseravtion = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//		DateFormat outputFormatterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		DateFormat outputFormatterDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Calendar cal = java.util.Calendar.getInstance();
		//		TimeZone tz = TimeZone.getTimeZone("GMT+0");
		//		cal.setTimeZone(tz);
		String phentime = outputFormatterObseravtion.format(new Date());
		String phentimeT = outputFormatterDate.format(cal.getTime());
		String resulttime = phentimeT; //dupplico per chiarezza nel passaggio parametri
		try {
			cal.setTime(outputFormatterObseravtion.parse(phentime));
		}
		catch (ParseException e) {
			LOGGER.error("Error in parsing format date [profile]", e);
		}
		cal.add(java.util.Calendar.DATE, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		phentime = outputFormatterObseravtion.format(cal.getTime());
		phentimeT = outputFormatterDate.format(cal.getTime());
		resulttime = phentimeT;
		
		DelegatedatabaseSos sosdb = new DelegatedatabaseSos();
		String observationidentifier = ReadFromConfig.loadByName("radiceObservation")
				+ shelterMap.getUriofferingcountcold() + "/" + phentime;
		//insert del n di rinfrescamenti
		String result = client.postInsertObservation(ReadFromConfig.loadByName("uriSos"), offering,
				observationidentifier, codespace, procedure, property, foiidentifier, phentimeT, resulttime, uom,
				String.valueOf(countcond));
		LOGGER.info("Sos response count cooling");
		LOGGER.info(result);
		
		offering = ReadFromConfig.loadByName("radiceOffering") + shelterMap.getUriofferingcountheat();
		property = ReadFromConfig.loadByName("propertyContHot");
		observationidentifier = ReadFromConfig.loadByName("radiceObservation") + shelterMap.getUriofferingcountheat()
				+ "/" + phentime;
		String resultheat = client.postInsertObservation(ReadFromConfig.loadByName("uriSos"), offering,
				observationidentifier, codespace, procedure, property, foiidentifier, phentimeT, resulttime, uom,
				String.valueOf(countheat));
		LOGGER.info("Sos response count heating");
		LOGGER.info(resultheat);
		
		int ore = 0;
		int minuti = 0;
		offering = ReadFromConfig.loadByName("radiceOffering") + shelterMap.getUriprocedure() + "_"
				+ ReadFromConfig.loadByName("tempSuggestionOffering");
		property = ReadFromConfig.loadByName("propertyTetai");
		uom = ReadFromConfig.loadByName("uomTetai");
		String responseInsertTemp = null;
		for (int i = 0; i < listaTemp.size(); i = i + 900) {
			LOGGER.info("hour " + outputFormatterObseravtion.format(cal.getTime()) + " " + listaTemp.get(i));
			phentime = outputFormatterObseravtion.format(cal.getTime());
			phentimeT = outputFormatterDate.format(cal.getTime());
			resulttime = phentimeT;
			observationidentifier = ReadFromConfig.loadByName("radiceObservation") + shelterMap.getUriprocedure() + "_"
					+ ReadFromConfig.loadByName("tempSuggestionOffering") + "/" + phentime;
			boolean done = false;
			int count = 0;
			int limit = Integer.parseInt(ReadFromConfig.loadByName("maxCount"));
			while (!done && count < limit) {
				responseInsertTemp = client.postInsertObservation(ReadFromConfig.loadByName("uriSos"), offering,
						observationidentifier, codespace, procedure, property, foiidentifier, phentimeT, resulttime,
						uom, String.valueOf(listaTemp.get(i)));
				if (responseInsertTemp != null) {
					done = true;
				}
				else {
					count = count + 1;
				}
			}
			
			LOGGER.info(responseInsertTemp);
			cal.add(Calendar.MINUTE, 15);
		}
		
		return true;
	}
}
