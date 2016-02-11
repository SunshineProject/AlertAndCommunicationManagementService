package it.sinergis.sunshine.suggestion.utils;

import it.sinergis.sunshine.suggestion.Responseobject.ObjComfortArray;
import it.sinergis.sunshine.suggestion.Responseobject.ObjJsonComfortProfile;
import it.sinergis.sunshine.suggestion.delegate.Delegatedatabasemap4data;
import it.sinergis.sunshine.suggestion.map4datapojo.Calendar;
import it.sinergis.sunshine.suggestion.sospojo.Numericvalue;
import it.sinergis.sunshine.suggestion.suggestion.Building;
import it.sinergis.sunshine.suggestion.velocity.SetupEmailTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public final class Functions {
	final static Logger LOGGER = Logger.getLogger(Functions.class);
	
	//	private static DecimalFormat df2 = new DecimalFormat(".##");
	
	public static String convertStreamToString(InputStream is) {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		}
		catch (IOException e) {
			LOGGER.error(e);
		}
		finally {
			try {
				is.close();
			}
			catch (IOException e) {
				LOGGER.error(e);
			}
		}
		return sb.toString();
	}
	
	public static Boolean fileExists(String path) {
		return new File(path).exists();
	}
	
	public static String addSeparator(String path) {
		if (path.charAt(path.length() - 1) != File.separatorChar) {
			path += File.separator;
		}
		return path;
	}
	
	public static String loadStream(InputStream s) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(s));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line).append("\n");
		return sb.toString();
	}
	
	public static String addUrlSection(String url, String section) {
		if (url.substring(url.length() - 1) == "/") {
			if (section.substring(0, 1) == "/") {
				return url.concat(section.substring(1, section.length() - 1));
			}
			return url.concat(section);
		}
		else {
			if (section.substring(0, 1) == "/") {
				return url.concat(section);
			}
			return url + "/" + section;
		}
		
	}
	
	public static BufferedReader getBufferedReader(String uriFile) throws FileNotFoundException {
		try {
			return new BufferedReader(new InputStreamReader(new FileInputStream(uriFile)));
		}
		catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
	}
	
	public static void setProfileEdificio(Building edificio, List<Integer> listFrom_hour, List<Integer> listFrom_min,
			List<Integer> listTo_hour, List<Integer> listTo_min, double temp) {
		boolean active = false; // variabili on/off che mi monitora se sono nell'intervallo o sono fuori
		for (int indexProfile = 0; indexProfile < listFrom_hour.size(); indexProfile++) {
			for (int i = 0; i < 24; i++) {
				if (i == listFrom_hour.get(indexProfile)) {
					active = true;
					edificio.tintfut[i] = temp;
				}
				else {
					if (i == listTo_hour.get(indexProfile)) {
						active = false;
						if (listTo_min.get(indexProfile) > 0) { //se il profilo di temp si deve bloccare dopo l'ora esatta
							edificio.tintfut[i] = temp;
							if (i < 23) {
								edificio.tintfut[i + 1] = 0.0;
							}
							i++;
						}
						else {
							edificio.tintfut[i] = 0.0;
						}
						
					}
					else {
						if (active) {
							edificio.tintfut[i] = temp;
						}
						else {
							edificio.tintfut[i] = 0.0;
						}
					}
				}
			}
		}
	}
	
	public static Map getSetupSos(String pilot) {
		Map<String, String> mMap = new HashMap<String, String>();
		switch (pilot) {
		
			case "ferrara":
				mMap.put("offering", ReadFromConfig.loadByName("offeringFerrara"));
				mMap.put("offeringStato", ReadFromConfig.loadByName("offeringFerraraStato"));
				mMap.put("codespace", ReadFromConfig.loadByName("codespaceFerrara"));
				mMap.put("procedure", ReadFromConfig.loadByName("procedureFerrara"));
				mMap.put("property", ReadFromConfig.loadByName("propertyFerrara"));
				mMap.put("foiidentifier", ReadFromConfig.loadByName("foiidentifierFerrara"));
				break;
			
			case "lamia":
				mMap.put("offering", ReadFromConfig.loadByName("offeringLamia"));
				mMap.put("offeringStato", ReadFromConfig.loadByName("offeringLamiaStato"));
				mMap.put("codespace", ReadFromConfig.loadByName("codespaceLamia"));
				mMap.put("procedure", ReadFromConfig.loadByName("procedureLamia"));
				mMap.put("property", ReadFromConfig.loadByName("propertyLamia"));
				mMap.put("foiidentifier", ReadFromConfig.loadByName("foiidentifierLamia"));
				break;
			
			case "croatia":
				mMap.put("offering", ReadFromConfig.loadByName("offeringCroatia"));
				mMap.put("offeringStato", ReadFromConfig.loadByName("offeringCroatiaStato"));
				mMap.put("codespace", ReadFromConfig.loadByName("codespaceCroatia"));
				mMap.put("procedure", ReadFromConfig.loadByName("procedureCroatia"));
				mMap.put("property", ReadFromConfig.loadByName("propertyCroatia"));
				mMap.put("foiidentifier", ReadFromConfig.loadByName("foiidentifierCroatia"));
				break;
			
			case "cles":
				mMap.put("offering", ReadFromConfig.loadByName("offeringCles"));
				mMap.put("offeringStato", ReadFromConfig.loadByName("offeringClesStato"));
				mMap.put("codespace", ReadFromConfig.loadByName("codespaceCles"));
				mMap.put("procedure", ReadFromConfig.loadByName("procedureCles"));
				mMap.put("property", ReadFromConfig.loadByName("propertyCles"));
				mMap.put("foiidentifier", ReadFromConfig.loadByName("foiidentifierCles"));
				break;
			
			default:
				mMap.put("offering", null);
				mMap.put("codespace", null);
				mMap.put("procedure", null);
				mMap.put("property", null);
				mMap.put("foiidentifier", null);
		}
		return mMap;
	}
	
	public static String getStringFromOutputSuggestion(double[] tetailast, String timeJson, int[] modality,
			String pilot, Integer buildingid, double sogliaTmin, double absTmin, double sogliaTmax, double absTmax,
			double sogliaTmed, double absTmed, String buildingName, String startComfort, String endComfort, Integer temp) {
		
		//qui devo recuperare il template e restituire l'email
		SetupEmailTemplate templateEmail = new SetupEmailTemplate();
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(java.util.Calendar.DATE, 1);
		String dateNow = outputFormatter.format(cal.getTime());
		String temperatureRequest = String.valueOf(temp);
		//TODO TEMP
		LOGGER.info("Get template email");
		String message = templateEmail.getTemplateEmail(dateNow, temperatureRequest, String.valueOf(sogliaTmin),
				String.valueOf(sogliaTmed), String.valueOf(sogliaTmax), buildingName, tetailast, modality, timeJson,
				"ON", startComfort, endComfort);
		String response = "Suggestion building " + String.valueOf(buildingid) + " of pilot " + pilot + "\n";
		
		//		for (double tetaiesimo : tetailast) {
		//			response = response + String.valueOf(df2.format(tetaiesimo)) + "\n";
		//		}
		//		response = response + "Turn on at " + timeJson + "\n";
		//		for (int modalitiesimo : modality) {
		//			response = response + String.valueOf(df2.format(modalitiesimo)) + "\n";
		//		}
		
		//return response;
		return message;
		
	}
	
	public static int getBuildingType(String heatCtr) {
		
		//		0 fixed point ;  1 climatic/teleheating;"
		int response = 0;
		
		switch (heatCtr) {
			case "fixedpoint":
				response = 1;
				break;
			case "climatic":
				response = 1;
				break;
			case "teleheating":
				response = 1;
				break;
			default:
				response = 1;
		}
		return response;
	}
	
	//restituisce la media delle 3 misure + piccole
	public static double getAvgfrom3Min(List<Numericvalue> listnum) {
		double min = listnum.get(0).getValue();
		double minmin = listnum.get(0).getValue();
		double minminmin = listnum.get(0).getValue();
		double temp;
		double temp2;
		for (int i = 1; i < listnum.size(); i++) {
			if (listnum.get(i).getValue() <= minminmin) {
				temp = minminmin;
				minminmin = listnum.get(i).getValue();
				if (temp <= minmin) {
					temp2 = minmin;
					minmin = temp;
					temp = temp2;
					if (temp < min) {
						min = temp;
					}
				}
				else {
					if (min < temp) {
						min = temp;
					}
				}
				
			}
			else {
				if (listnum.get(i).getValue() <= minmin) {
					temp = minmin;
					minmin = listnum.get(i).getValue();
					if (temp < min) {
						min = temp;
					}
				}
				else {
					if (listnum.get(i).getValue() < min) {
						min = listnum.get(i).getValue();
					}
				}
			}
		}
		
		return (min + minmin + minminmin) / 3;
	}
	
	public static double getAvgMinfromOrderList(List<Numericvalue> listnum, int nToGet) {
		double tot = 0.0;
		if (listnum.size() < nToGet) {
			nToGet = listnum.size();
		}
		for (int i = 0; i < nToGet; i++) {
			tot = tot + listnum.get(i).getValue();
		}
		
		return tot / nToGet;
	}
	
	public static double getAvgMaxfromOrderList(List<Numericvalue> listnum, int nToGet) {
		double tot = 0.0;
		if (listnum.size() < nToGet) {
			nToGet = listnum.size();
		}
		for (int i = nToGet - 1; i >= 0; i--) {
			tot = tot + listnum.get(i).getValue();
		}
		
		return tot / nToGet;
		
	}
	
	public static void orderListNumericValue(List<Numericvalue> list) {
		Collections.sort(list, new Comparator<Numericvalue>() {
			
			@Override
			public int compare(Numericvalue o1, Numericvalue o2) {
				return o1.getValue().compareTo(o2.getValue());
				
			}
		});
	}
	
	public static JSONObject getJsonFromString(String jsonBody) {
		JSONObject obj = null;
		try {
			obj = new JSONObject(jsonBody);
		}
		catch (JSONException e) {
			LOGGER.warn("Profile comfort not present.");
		}
		return obj;
	}
	
	public static String getStringFromJsonObject(JSONObject obj) {
		return obj.toString();
	}
	
	public static Map<String, Integer> getTimeFromJsonComfortProfile(int idBuilding, Date day, String pilot)
			throws ParseException, JSONException {
		Delegatedatabasemap4data dbmap4data = new Delegatedatabasemap4data();
		
		Calendar calendar = dbmap4data.readParameterCalendar(idBuilding, day, pilot, null, null);
		JSONObject profilecomfort = null;
		try {
			profilecomfort = new JSONObject(calendar.getProfilecomfort());
		}
		catch (Exception e) {
			LOGGER.warn("No profile comfort for buildingid " + idBuilding);
			return null;
		}
		//			JSONObject profilecomfort = calendar.getProfilecomfort();
		double temperature = profilecomfort.getDouble("T");
		JSONArray profile = profilecomfort.getJSONArray("INT");
		JSONObject ss = profile.getJSONObject(0);
		List<Integer> listFrom_hour = new ArrayList<Integer>();
		List<Integer> listFrom_min = new ArrayList<Integer>();
		List<Integer> listTo_hour = new ArrayList<Integer>();
		List<Integer> listTo_min = new ArrayList<Integer>();
		for (int i = 0; i < profile.length(); i = i + 4) {
			listFrom_hour.add(ss.getInt("from_hh"));
			listFrom_min.add(ss.getInt("from_mm"));
			listTo_hour.add(ss.getInt("to_hh"));
			listTo_min.add(ss.getInt("to_mm"));
		}
		
		Map<String, Integer> mMap = new HashMap<String, Integer>();
		mMap.put("from_hh", ss.getInt("from_hh"));
		mMap.put("from_mm", ss.getInt("from_mm"));
		mMap.put("to_hh", ss.getInt("to_hh"));
		mMap.put("to_mm", ss.getInt("to_mm"));
		return mMap;
		
	}
	
	public static ObjJsonComfortProfile ObjJsonComfortProfileRemoveNull(ObjJsonComfortProfile json) {
		int i = 0;
		List<Integer> listNull = new ArrayList<Integer>();
		List<ObjComfortArray> listComfort = json.getComfortArray();
		for (ObjComfortArray profile : listComfort) {
			if (profile == null || profile.getAh() == null || profile.getAm() == null || profile.getDah() == null
					|| profile.getDam() == null) {
				listNull.add(i);
			}
			i = i + 1;
		}
		for (int ii : listNull) {
			listComfort.remove(ii);
		}
		json.setComfortArray(listComfort);
		return json;
	}
	
	public static String getNormalizeHours(double hour) {
		DecimalFormat df2 = new DecimalFormat("##");
		String stringHour = String.valueOf(hour);
		String[] split = stringHour.split("\\.");
		String response = split[0];
		String tmpdec = null;
		if (split.length > 1) {
			String dd = split[1];
			//			if (split[1].length() == 1) {
			//				dd = "0" + dd;
			//			}
			double decimal = Double.parseDouble("0." + dd);
			int dec = (int) (60 * decimal);
			tmpdec = String.valueOf(dec);
			if (tmpdec.length() > 2) {
				tmpdec = tmpdec.substring(0, 2);
			}
			if (tmpdec.length() == 1) {
				response = response + ":0" + tmpdec;
			}
			else {
				response = response + ":" + tmpdec;
			}
			
		}
		else {
			response = response + ":00";
		}
		return response;
		
	}
	
	public static String getPeriodFromModality(int[] modality, String start, String end, String turnOn) {
		List<String> response = new ArrayList<String>();
		int ii = 0;
		int lastState = 0;
		int lastOFF = 0;
		boolean firsttime = true;
		int endint = Integer.parseInt(end.split(":")[0]);
		for (int mod : modality) {
			if (mod == 0) {
				if (lastState != mod && endint > ii) {
					response.add("OFF;" + String.valueOf(ii));//aggiungo 1 perche' e' una classe
					lastState = 0;
					lastOFF = ii;
				}
			}
			else {
				if (lastState != mod) {
					if (firsttime && Integer.parseInt(turnOn.split(":")[0]) <= ii) {
						response.add("ON;" + turnOn);
					}
					else {
						response.add("ON;" + String.valueOf(ii));
					}
					lastState = 1;
					firsttime = false;
				}
			}
			ii = ii + 1;
		}
		if (lastOFF < endint) {
			response.add("OFF;" + String.valueOf(endint));
		}
		
		String resp = "";
		for (String r : response) {
			if (r.split(";")[0].equals("OFF")) {
				resp = resp + "<b>Turn off " + r.split(";")[1] + ":00 </b></br>";
			}
			else {
				if (r.split(";")[0].equals("ON")) {
					if (r.split(";")[1].length() >= 3) {
						resp = resp + "<b>Turn on " + r.split(";")[1] + " </b></br>";
					}
					else {
						resp = resp + "<b>Turn on " + r.split(";")[1] + ":00 </b></br>";
					}
					
				}
			}
		}
		
		return resp;
	}
	
	public static ArrayList<String> getPeriodFromModalityArray(int[] modality, String end, String turnOn) {
		List<String> response = new ArrayList<String>();
		ArrayList<String> output = new ArrayList<String>();
		int ii = 0;
		int lastState = 0;
		int lastOFF = 0;
		boolean firsttime = true;
		int endint = Integer.parseInt(end.split(":")[0]);
		for (int mod : modality) {
			if (mod == 0) {
				if (lastState != mod && endint > ii) {
					response.add("OFF;" + String.valueOf(ii));
					lastState = 0;
					lastOFF = ii;
				}
			}
			else {
				if (lastState != mod) {
					if (firsttime && Integer.parseInt(turnOn.split(":")[0]) <= ii) {
						response.add("ON;" + turnOn);
					}
					else {
						response.add("ON;" + String.valueOf(ii));
					}
					lastState = 1;
					firsttime = false;
				}
			}
			ii = ii + 1;
		}
		if (lastOFF < endint) {
			response.add("OFF;" + String.valueOf(endint));
		}
		
		for (String r : response) {
			if (r.split(";")[0].equals("OFF")) {
				output.add(r.split(";")[1] + ":00");
			}
			else {
				if (r.split(";")[0].equals("ON")) {
					if (r.split(";")[1].length() >= 3) {
						output.add(r.split(";")[1]);
					}
					else {
						output.add(r.split(";")[1] + ":00");
					}
					
				}
			}
		}
		
		return output;
	}
	
	public static String getPosition(int[] modality, int element) {
		
		int i = 0;
		while (i < modality.length) {
			if (modality[i] == element) {
				
				return String.valueOf(i);
			}
			
			i = i + 1;
			
		}
		return null;
		
	}
	
}
