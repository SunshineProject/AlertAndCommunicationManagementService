package it.sinergis.sunshine.suggestion.suggestion;

import it.sinergis.sunshine.suggestion.suggestionShelter.Principale;
import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;

public class TestPrincipaleShelter {
	
	public static void main(String[] args) {
		Principale princ = new Principale();
		princ.calculate(ReadFromConfig.loadByName("directoryCsv"), 1, "tnet");
		
	}
	
}
