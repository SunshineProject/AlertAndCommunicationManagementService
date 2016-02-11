package it.sinergis.sunshine.suggestion.timertask;

import it.sinergis.sunshine.suggestion.delegate.DelegateoperationSuggestion;
import it.sinergis.sunshine.suggestion.map4datapojo.ShelterMap4;
import it.sinergis.sunshine.suggestion.suggestion.Principale;
import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class ScheduledTask extends TimerTask {
	static final Logger LOGGER = Logger.getLogger(ScheduledTask.class);
	Date now; // to display current time
	
	// Add your task here
	public void run() {
		now = new Date(); // initialize date
		LOGGER.info("Run task at :" + now); // Display current time
		LOGGER.info("Ora bisogna verificare se ci sono le condizioni per far partire il suggestion");
		//queste tre variabili dovranno essere ricavate dalla procedura che verifica se e' necessario far partire il suggestion
		int buildingid = 49146;
		String pilot = "ferrara";
		//qui prendo da delegateSuggestion e gli devo dare la lista di buildinid con pilot
		DelegateoperationSuggestion suggestionTrigger = new DelegateoperationSuggestion();
		List<Map<String, String>> ll = suggestionTrigger.isSuggestionNecessary();
		try {
			for (Map<String, String> param : ll) {
				buildingid = Integer.parseInt(param.get("Tmin").split(";")[0]);
				pilot = param.get("Tmin").split(";")[1];
				double sogliaTmin = Double.parseDouble(param.get("Tmin").split(";")[2]);
				double absTmin = Double.parseDouble(param.get("Tmin").split(";")[3]);
				double sogliaTmax = Double.parseDouble(param.get("Tmax").split(";")[2]);
				double absTmax = Double.parseDouble(param.get("Tmax").split(";")[3]);
				double sogliaTmed = Double.parseDouble(param.get("Tmed").split(";")[2]);
				double absTmed = Double.parseDouble(param.get("Tmed").split(";")[3]);
				Principale suggestion = new Principale();
				try {
					LOGGER.info("SUGGESTION for building " + buildingid + " of pilot " + pilot);
					suggestion.calculate(ReadFromConfig.loadByName("directoryCsv"), buildingid, pilot, sogliaTmin,
							absTmin, sogliaTmax, absTmax, sogliaTmed, absTmed);
				}
				catch (Exception e) {
					LOGGER.error("ERROR for build " + buildingid);
					LOGGER.error(e);
				}
				
			}
		}
		catch (Exception e) {
			LOGGER.warn("ERROR for build " + buildingid);
			LOGGER.warn(e);
		}
		
		//ora devo far partire gli shelter
		//eseguirlo per ogni shelter, confrontare il risultato ottenuto con la soglia impostata, se la soglia e' minore, mandare un allert
		now = new Date();
		LOGGER.info("Run suggestion shelter at :" + now); // Display current time
		
		it.sinergis.sunshine.suggestion.suggestionShelter.Principale shelter = new it.sinergis.sunshine.suggestion.suggestionShelter.Principale();
		
		List<ShelterMap4> shelterList = suggestionTrigger.getListSchelter();
		Map<String, Integer> map = null;
		try {
			for (ShelterMap4 shelterM4 : shelterList) {
				
				map = shelter.calculate(ReadFromConfig.loadByName("directoryCsv"), shelterM4.getId(),
						shelterM4.getPilot());
				int ncooling = map.get("nConditioning");
				int nheating = map.get("nHeating");
				int thresholdWarm = map.get("thresholdWarm");
				int thresholdCold = map.get("thresholdCold");
				if (ncooling > thresholdCold || nheating > thresholdWarm) {
					//TODO, mandare email per ogni record nella tabella buildingmanager con idshelter e pilot
					LOGGER.info("I sent an email to shelter with id " + shelterM4.getId());
					//				List<Buildingmanager> managerList = suggestionTrigger.getBuildingManagerRecords(buildingid, pilot);
					//				for (Buildingmanager manager : managerList) {
					//					Email.sendEmail("oscar.benedetti@sinergis.it", manager.getEmail(), "Sunshine suggestion",
					//							"Threshold superata a " + manager.getDescription(), "e mo??");
					//				}
					
				}
			}
		}
		catch (Exception e) {
			LOGGER.warn("ERROR for shelter ");
		}
		
	}
}