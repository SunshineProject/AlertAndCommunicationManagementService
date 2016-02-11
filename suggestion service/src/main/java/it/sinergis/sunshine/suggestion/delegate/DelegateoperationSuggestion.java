package it.sinergis.sunshine.suggestion.delegate;

import it.sinergis.sunshine.suggestion.map4datapojo.BuildingMap4;
import it.sinergis.sunshine.suggestion.map4datapojo.Buildingmanager;
import it.sinergis.sunshine.suggestion.map4datapojo.Calendar;
import it.sinergis.sunshine.suggestion.map4datapojo.ShelterMap4;
import it.sinergis.sunshine.suggestion.sospojo.Numericvalue;
import it.sinergis.sunshine.suggestion.sospojo.Observation;
import it.sinergis.sunshine.suggestion.utils.Functions;
import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class DelegateoperationSuggestion {
	static final Logger LOGGER = Logger.getLogger(DelegateoperationSuggestion.class);
	
	public List<Map<String, String>> isSuggestionNecessary() {
		//per ogni edificio nella tabella calendar per una determinata data
		Delegatedatabasemap4data map4data = new Delegatedatabasemap4data();
		DelegatedatabaseSos sosdb = new DelegatedatabaseSos();
		List<Calendar> calendarTab = null;
		List<Map<String, String>> needSuggestion = new ArrayList<Map<String, String>>();
		try {
			//giorni che aggiungo alla data di oggi
			int day = 1;
			calendarTab = map4data.readParameterCalendar(new Date(), day, null, null);
		}
		catch (ParseException e) {
			LOGGER.error("Error during reading calendar table!", e);
		}
		
		Map<String, Double> thresholds = null;
		for (Calendar calendarTabiesimo : calendarTab) {
			BuildingMap4 building = map4data.readParameterBuilding((int) calendarTabiesimo.getBuildingid(), null, null,
					calendarTabiesimo.getPilot());
			thresholds = map4data.getThresholds(calendarTabiesimo.getBuildingid(), calendarTabiesimo.getPilot());
			double tTmin = thresholds.get("thresholdsTmin");
			double tTmed = thresholds.get("thresholdsTmed");
			double tTmax = thresholds.get("thresholdsTmax");
			//ora la devo confrontare con la media delle 3 temp + basse
			int offsetDaybefore = 0;
			int offsetDayAfter = 1;
			int offsetPeriod = 1;
			List<Observation> listObBefore = null;
			List<Numericvalue> listnumBefore = null;
			List<Observation> listObAfter = null;
			List<Numericvalue> listnumAfter = null;
			try {
				listObBefore = sosdb.readParameterObservation(building.getTempUri(), offsetDaybefore, offsetPeriod,
						null, null);
				listnumBefore = sosdb.readvaluesNumericValue(listObBefore, null, null);
				Functions.orderListNumericValue(listnumBefore);
				listObAfter = sosdb.readParameterObservation(building.getTempUri(), offsetDayAfter, offsetPeriod, null,
						null);
				listnumAfter = sosdb.readvaluesNumericValue(listObAfter, null, null);
				Functions.orderListNumericValue(listnumAfter);
			}
			catch (ParseException e) {
				LOGGER.error("Error during reading from dbsos", e);
			}
			Double avgDayBeforeMin = Functions.getAvgMinfromOrderList(listnumBefore,
					Integer.parseInt(ReadFromConfig.loadByName("nToAvg")));
			Double avgDayAfterMin = Functions.getAvgMinfromOrderList(listnumAfter,
					Integer.parseInt(ReadFromConfig.loadByName("nToAvg")));
			Double avgDayBeforeMax = Functions.getAvgMaxfromOrderList(listnumBefore,
					Integer.parseInt(ReadFromConfig.loadByName("nToAvg")));
			Double avgDayAfterMax = Functions.getAvgMaxfromOrderList(listnumAfter,
					Integer.parseInt(ReadFromConfig.loadByName("nToAvg")));
			Double avgDayBeforeMed = Functions.getAvgMinfromOrderList(listnumBefore, listnumBefore.size());
			Double avgDayAfterMed = Functions.getAvgMinfromOrderList(listnumAfter, listnumAfter.size());
			//			if (tTmin <= Math.abs(avgDayBeforeMin - avgDayAfterMin)) {
			//				needSuggestion
			//						.add(building.getGid() + ";" + tTmin + ";" + Math.abs(avgDayBeforeMin - avgDayAfterMin));
			//			}
			//			if (tTmax <= Math.abs(avgDayBeforeMax - avgDayAfterMax)) {
			//				needSuggestion
			//						.add(building.getGid() + ";" + tTmin + ";" + Math.abs(avgDayBeforeMax - avgDayAfterMax));
			//			}
			//			if (tTmed <= Math.abs(avgDayBeforeMed - avgDayAfterMed)) {
			//				needSuggestion
			//						.add(building.getGid() + ";" + tTmin + ";" + Math.abs(avgDayBeforeMed - avgDayAfterMed));
			//			}
			Map<String, String> parameter = new HashMap<String, String>();
			parameter.put(
					"Tmin",
					building.getGid() + ";" + calendarTabiesimo.getPilot() + ";" + tTmin + ";"
							+ Math.abs(avgDayBeforeMin - avgDayAfterMin));
			parameter.put(
					"Tmax",
					building.getGid() + ";" + calendarTabiesimo.getPilot() + ";" + tTmax + ";"
							+ Math.abs(avgDayBeforeMax - avgDayAfterMax));
			parameter.put(
					"Tmed",
					building.getGid() + ";" + calendarTabiesimo.getPilot() + ";" + tTmed + ";"
							+ Math.abs(avgDayBeforeMed - avgDayAfterMed));
			needSuggestion.add(parameter);
		}
		
		//controllare che la abs(mediaTempDay+1 - mediaTempDay-1) > soglia
		return needSuggestion;
		
	}
	
	public final ArrayList<Calendar> setupComfortPeriod(Calendar record, String upperday) throws ParseException {
		LOGGER.info("Setup of building " + record.getBuildingid() + " about period " + record.getDay() + " - "
				+ upperday);
		ArrayList<Calendar> responseList = new ArrayList<Calendar>();
		Delegatedatabasemap4data map4datadb = new Delegatedatabasemap4data();
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(outputFormatter.parse(upperday));
		Date dayU = cal.getTime();
		//controllo che il limite superiore non sia minore del limite inferiore
		Date dayB = record.getDay();
		cal.setTime(record.getDay());
		while (dayB.before(dayU) || dayB.equals(dayU)) {
			Serializable calendarid = map4datadb.insert(record, null, null);
			Calendar newrecord = new Calendar();
			newrecord.setCalendarid((Integer) calendarid);
			newrecord.setBuildingid(record.getBuildingid());
			newrecord.setDay(dayB);
			newrecord.setPilot(record.getPilot());
			newrecord.setProfilecomfort(record.getProfilecomfort());
			responseList.add(newrecord);
			cal.add(java.util.Calendar.DATE, 1);
			dayB = cal.getTime();
			record.setDay(dayB);
		}
		return responseList;
	}
	
	public final List<ShelterMap4> getListSchelter() {
		Delegatedatabasemap4data map4datadb = new Delegatedatabasemap4data();
		return map4datadb.getShelter(null, null);
	}
	
	public final List<Buildingmanager> getBuildingManagerRecords(long buildingid, String pilot) {
		Delegatedatabasemap4data dbmap4data = new Delegatedatabasemap4data();
		return dbmap4data.readParameterBuildingmanager(buildingid, null, null, pilot);
	}
}
