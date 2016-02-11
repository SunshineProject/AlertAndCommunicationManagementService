package it.sinergis.sunshine.suggestion.timertask;

import it.sinergis.sunshine.suggestion.delegate.DelegateoperationSuggestion;
import it.sinergis.sunshine.suggestion.map4datapojo.ShelterMap4;
import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class ScheduledTaskShelter extends TimerTask {
	static final Logger LOGGER = Logger.getLogger(ScheduledTaskShelter.class);
	Date now; // to display current time
	
	// Add your task here
	public void run() {
		now = new Date(); // initialize date
		LOGGER.info("Run task shelter at :" + now); // Display current time		
		DelegateoperationSuggestion suggestionTrigger = new DelegateoperationSuggestion();
		
		//ora devo far partire gli shelter
		//eseguirlo per ogni shelter, confrontare il risultato ottenuto con la soglia impostata, se la soglia e' minore, mandare un allert
		LOGGER.info("Run suggestion shelter at :" + now); // Display current time
		
		it.sinergis.sunshine.suggestion.suggestionShelter.Principale shelter = new it.sinergis.sunshine.suggestion.suggestionShelter.Principale();
		
		List<ShelterMap4> shelterList = suggestionTrigger.getListSchelter();
		Map<String, Integer> map = null;
		for (ShelterMap4 shelterM4 : shelterList) {
			
			map = shelter.calculate(ReadFromConfig.loadByName("directoryCsv"), shelterM4.getId(), shelterM4.getPilot());
			int ncooling = map.get("nConditioning");
			int nheating = map.get("nHeating");
			LOGGER.info("Cooling Peaks " + ncooling);
			LOGGER.info("Heating Peaks " + nheating);
			//			if (ncooling > shelterM4.getThreshold() || nheating > shelterM4.getThreshold()) {
			//				//TODO, mandare email per ogni record nella tabella buildingmanager con idshelter e pilot
			//				List<Buildingmanager> managerList = suggestionTrigger.getBuildingManagerRecords(shelterM4.getId(),
			//						shelterM4.getPilot());
			//				for (Buildingmanager manager : managerList) {
			//					Email.sendEmail("oscar.benedetti@sinergis.it", manager.getEmail(), "Sunshine suggestion",
			//							"Threshold superata a " + manager.getDescription(), "e mo??");
			//				}
			//				
			//			}
		}
		
	}
}