package it.sinergis.sunshine.suggestion.suggestion;

import it.sinergis.sunshine.suggestion.delegate.DelegatedatabaseSos;
import it.sinergis.sunshine.suggestion.delegate.Delegatedatabasemap4data;
import it.sinergis.sunshine.suggestion.sospojo.Featureofinterest;

import java.text.ParseException;
import java.util.Date;

public class TestHibernate {
	
	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		//		Date ff = new Date();
		//		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		//		String output = outputFormatter.format(ff); // Output : 01/20/2012
		//		
		//		System.out.println(output);
		//		
		//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//		Calendar cal = Calendar.getInstance();
		//		cal.setTime(dateFormat.parse(output));
		//		cal.add(Calendar.DATE, 1);
		//		output = outputFormatter.format(cal.getTime()); // Output : 01/20/2012
		//		System.out.println(output);
		
		//		Delegatedatabasemap4data d = new Delegatedatabasemap4data();
		//		d.readparameter();
		
		//		Delegatedatabasemap4data dbmap4data = new Delegatedatabasemap4data();
		//		List<Buildingmanager> buildManager = dbmap4data.readParameterBuildingmanager((long) 49146, null, null,
		//				"ferrara");
		
		//		String propertyStato = ReadFromConfig.loadByName("propertyModality");
		
//		Delegatedatabasemap4data map4 = new Delegatedatabasemap4data();
//		map4.readParameterCalendar(new Date(), 1, null, null);
		
		DelegatedatabaseSos dbsos = new DelegatedatabaseSos();
		Featureofinterest foi = dbsos.readFeatureofInterest(2, null, null);
		System.out.println(foi.getIdentifier());
		//		DelegatedatabaseSos sosTask = new DelegatedatabaseSos();
		//		Map<String, String> map = Functions.getSetupSos("ferrara");
		//		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		//		Calendar cal = Calendar.getInstance();
		//		cal.setTime(new Date());
		//		cal.add(Calendar.DATE, 4);
		//		cal.set(Calendar.MINUTE, 0);
		//		cal.set(Calendar.SECOND, 0);
		//		cal.set(Calendar.HOUR_OF_DAY, 0);
		//		System.out.print(cal.getTime());
		//		
		//		String belowlimit = outputFormatter.format(cal.getTime());
		//		List<String> listIdentifier = new ArrayList<String>();
		//		listIdentifier.add(ReadFromConfig.loadByName("radiceObservation") + map.get("offering"));
		//		listIdentifier.add(ReadFromConfig.loadByName("radiceObservation") + map.get("offeringStato"));
		//		sosTask.delete(listIdentifier, cal.getTime());
	}
}
