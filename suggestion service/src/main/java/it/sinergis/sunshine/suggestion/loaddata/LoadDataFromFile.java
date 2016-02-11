package it.sinergis.sunshine.suggestion.loaddata;

import it.sinergis.sunshine.suggestion.delegate.DelegatedatabaseSos;
import it.sinergis.sunshine.suggestion.delegate.Delegatedatabasemap4data;
import it.sinergis.sunshine.suggestion.map4datapojo.Calendar;
import it.sinergis.sunshine.suggestion.map4datapojo.Officialarea;
import it.sinergis.sunshine.suggestion.sospojo.Numericvalue;
import it.sinergis.sunshine.suggestion.sospojo.Observation;
import it.sinergis.sunshine.suggestion.suggestion.Building;
import it.sinergis.sunshine.suggestion.suggestion.Climate;
import it.sinergis.sunshine.suggestion.suggestion.Material;
import it.sinergis.sunshine.suggestion.utils.Functions;
import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class LoadDataFromFile {
	static final Logger LOGGER = Logger.getLogger(LoadDataFromFile.class);
	
	public final Map loadCsvPoint(String directoryCsv, Climate climate, Building edificio, int n)
			throws NumberFormatException, IOException {
		Map<String, Double> mMap = new HashMap<String, Double>();
		Double sumx = 0.0;
		Double sumy = 0.0;
		int nn = n;
		BufferedReader br = Functions.getBufferedReader(directoryCsv + ReadFromConfig.loadByName("nameCsvPoint"));
		String line = null;
		while ((line = br.readLine()) != null) {
			String values[] = line.split(";");
			climate.tempextpast[nn] = Double.parseDouble(values[0]);
			
			edificio.qpast[nn] = Double.parseDouble(values[1]);
			
			sumx += climate.tempextpast[nn];
			sumy += edificio.qpast[nn];
			nn++;
		}
		br.close();
		mMap.put("sumx", sumx);
		mMap.put("sumy", sumy);
		mMap.put("n", (double) nn);
		return mMap;
	}
	
	public final Map loadCsvBuilding(String directoryCsv, Building edificio, boolean fileexist, String pilot)
			throws NumberFormatException, IOException {
		Map<String, Double> mMap = new HashMap<String, Double>();
		Double h_t = 0.0;
		Double floor = 0.0;
		if (fileexist) {
			BufferedReader br = Functions
					.getBufferedReader(directoryCsv + ReadFromConfig.loadByName("nameCsvBuilding"));
			
			String line = null;
			
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				double id = Double.parseDouble(values[0]);
				if (id == edificio.id_edificio) {
					edificio.volume = Double.parseDouble(values[1]);
					edificio.aroof = Double.parseDouble(values[2]);
					edificio.awall = Double.parseDouble(values[3]);
					edificio.uwall = Double.parseDouble(values[4]);
					edificio.uroof = Double.parseDouble(values[5]);
					edificio.ufloor = Double.parseDouble(values[6]);
					edificio.uwindow = Double.parseDouble(values[7]);
					edificio.awindows = Double.parseDouble(values[8]);
					edificio.id_material = values[9];
					edificio.type = Integer.parseInt(values[10]);
					edificio.system = Integer.parseInt(values[11]);
					
				}
			}
			br.close();
			
		}
		else {
			Delegatedatabasemap4data dbMap4data = new Delegatedatabasemap4data();
			it.sinergis.sunshine.suggestion.map4datapojo.BuildingMap4 buildingMap4 = dbMap4data.readParameterBuilding(
					edificio.id_edificio, null, null, pilot);
			Officialarea officialArea = dbMap4data.readParameterOfficialArea(edificio.id_edificio, null, null, pilot);
			//edificio.volume       -> recupero da heat_vol nel db map4data tab building
			//edificio.aroof        -> "clge_value where area_ref = 'externalarea'"
			//edificio.awall        -> "Derivato: (perimeter - per_common) * height_val * (1 - p_win)"
			//edificio.uwall        -> u_wall 
			//edificio.uroof        -> u_roof
			//edificio.ufloor       -> u_floor
			//edificio.uwindow      -> u_window 
			//edificio.awindows     -> "Derivato: (perimeter - per_common) * height_val * p_win"
			//edificio.id_material  -> DA ELIMINARE
			//edificio.type         -> heat_ctr
			//edificio.system       -> DA ELIMINARE
			edificio.volume = buildingMap4.getHeatVol();
			edificio.aroof = officialArea.getClgeValue();
			edificio.awall = (buildingMap4.getPerimeter() - buildingMap4.getPerCommon()) * buildingMap4.getHeightVal()
					* (1 - buildingMap4.getPWin() / 100);
			edificio.uwall = buildingMap4.getUWall();
			edificio.uroof = buildingMap4.getURoof();
			edificio.ufloor = buildingMap4.getUFloor();
			edificio.uwindow = buildingMap4.getUWin();
			edificio.awindows = (buildingMap4.getPerimeter() - buildingMap4.getPerCommon())
					* buildingMap4.getHeightVal() * (buildingMap4.getPWin() / 100.0);
			//edificio.id_material = values[9]; // non serve xchè leggiamo ro,c e lambda da building
			//			edificio.type = edificio.system = Integer.parseInt(values[11]); // serve solo in fase di calibrazione
			
		}
		h_t = ((edificio.uwall * edificio.awall + edificio.uwindow * edificio.awindows + edificio.uroof
				* edificio.aroof + edificio.ufloor * edificio.aroof * .5) + 0.1 * (edificio.awall + edificio.awindows
				+ edificio.aroof + edificio.aroof));
		
		floor = edificio.volume / (3 * 0.7 * edificio.aroof);
		mMap.put("h_t", h_t);
		mMap.put("floor", floor);
		return mMap;
	}
	
	public final Double loadCsvIrradiation(String directoryCsv, Climate climate, boolean fileexist, String identifier)
			throws NumberFormatException, IOException, ParseException {
		Double qavesolfuture = 0.0;
		if (fileexist) {
			BufferedReader br = Functions.getBufferedReader(directoryCsv
					+ ReadFromConfig.loadByName("nameCsvIrradiation"));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				for (int i = 0; i <= 23; i++) {
					climate.qsolfuture[i] = Double.parseDouble(values[i]);
					qavesolfuture = (qavesolfuture + climate.qsolfuture[i]);
				}
			}
			br.close();
		}
		else {
			
			//			-- query per estrarre l'irraggiamento previsioni (db_qsolfuture8.csv)
			//			-- valore dell'irraggiamento arrotondato (esportazione del csv interpreta male il separatore decimale) 
			//			select round(value), phenomenontimestart 
			//			from offering, observation, numericvalue, observationhasoffering 
			//			where offering.identifier like '%FER-F00_IRRA_WM2_1h' and
			//			offering.offeringid = observationhasoffering.offeringid and
			//			observation.observationid = numericvalue.observationid and 
			//			observation.observationid = observationhasoffering.observationid and
			//			phenomenontimestart >= '2015-04-16' and phenomenontimestart < '2015-04-17' 
			//			order by phenomenontimestart
			
			DelegatedatabaseSos dbsos = new DelegatedatabaseSos();
			int dayOffset = 1;
			int dayOffsetPeriod = 1;
			List<Observation> listOb = dbsos.readParameterObservation(identifier, dayOffset, dayOffsetPeriod, null,
					null);
			List<Numericvalue> listNum = dbsos.readvaluesNumericValue(listOb, null, null);
			int i = 0;
			for (Numericvalue numv : listNum) {
				climate.qsolfuture[i] = numv.getValue();
				qavesolfuture = qavesolfuture + numv.getValue();
				i = i + 1;
			}
			
		}
		qavesolfuture = qavesolfuture / 24;
		
		return qavesolfuture;
	}
	
	public final void loadClimateDataShelter(String directoryCsv,
			it.sinergis.sunshine.suggestion.suggestionShelter.Climate climate, boolean fileexist,
			String identifierIrradiation, String identifierWind, String identifierTemp) throws NumberFormatException,
			IOException, ParseException {
		if (fileexist) {
			BufferedReader br = Functions.getBufferedReader(directoryCsv
					+ ReadFromConfig.loadByName("nameCsvIrradiationShelter"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				for (int i = 0; i <= 47; i++) {
					climate.irr[i] = Double.parseDouble(values[i]);
				}
			}
			br.close();
			
			//carico i dati del vento
			br = Functions.getBufferedReader(directoryCsv + ReadFromConfig.loadByName("nameCsvWindShelter"));
			line = null;
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				for (int i = 0; i <= 47; i++) {
					climate.wind[i] = Double.parseDouble(values[i]);
				}
			}
			br.close();
			
			//carico le temp
			br = Functions.getBufferedReader(directoryCsv + ReadFromConfig.loadByName("nameCsvTempShelter"));
			line = null;
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				
				//cicli orari. Il numero di giorni dipende dal tempo di anticipo della previsione futura
				for (int i = 0; i <= 47; i++) {
					climate.temp[i] = Double.parseDouble(values[i]);
				}
			}
		}
		else {
			//TODO: 2 giorni di dati SOS
			DelegatedatabaseSos dbsos = new DelegatedatabaseSos();
			int dayOffset = 1;
			int dayOffsetPeriod = 2;
			//irradiation
			List<Observation> listOb = dbsos.readParameterObservation(identifierIrradiation, dayOffset,
					dayOffsetPeriod, null, null);
			List<Numericvalue> listNum = dbsos.readvaluesNumericValue(listOb, null, null);
			int i = 0;
			for (Numericvalue numv : listNum) {
				climate.irr[i] = numv.getValue();
				i = i + 1;
			}
			
			//wind
			listOb = dbsos.readParameterObservation(identifierWind, dayOffset, dayOffsetPeriod, null, null);
			listNum = dbsos.readvaluesNumericValue(listOb, null, null);
			i = 0;
			for (Numericvalue numv : listNum) {
				climate.wind[i] = numv.getValue();
				i = i + 1;
			}
			
			//temp
			listOb = dbsos.readParameterObservation(identifierTemp, dayOffset, dayOffsetPeriod, null, null);
			listNum = dbsos.readvaluesNumericValue(listOb, null, null);
			i = 0;
			for (Numericvalue numv : listNum) {
				climate.temp[i] = numv.getValue();
				i = i + 1;
			}
		}
	}
	
	public final Map<String, Integer> loadCsvCodizionamentoFuturo(String directoryCsv, Building edificio,
			boolean fileexist, String pilot) throws NumberFormatException, IOException, JSONException {
		// qui bisogna estrarre da DB i profili di comfort per quel determinato edificio
		if (fileexist) {
			BufferedReader br = Functions.getBufferedReader(directoryCsv
					+ ReadFromConfig.loadByName("nameCsvInternaFutura"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				for (int i = 0; i <= 23; i++) {
					try {
						edificio.tintfut[i] = Double.parseDouble(values[i]);
					}
					catch (Exception e) {
						edificio.tintfut[i] = 0.0;
					}
				}
			}
			br.close();
			return null;
		}
		else {
			Delegatedatabasemap4data dbMap4data = new Delegatedatabasemap4data();
			DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
			String day = outputFormatter.format(new Date());
			
			java.util.Calendar cal = java.util.Calendar.getInstance();
			try {
				cal.setTime(outputFormatter.parse(day));
			}
			catch (ParseException e) {
				LOGGER.error("Error in parsing format date [profile]", e);
			}
			cal.add(java.util.Calendar.DATE, 1);
			day = outputFormatter.format(cal.getTime());
			Calendar calendar = null;
			try {
				calendar = dbMap4data.readParameterCalendar(edificio.id_edificio, cal.getTime(), pilot, null, null);
			}
			catch (ParseException e) {
				LOGGER.error("Error in parse date", e);
			}
			JSONObject profilecomfort = new JSONObject(calendar.getProfilecomfort());
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
			Functions.setProfileEdificio(edificio, listFrom_hour, listFrom_min, listTo_hour, listTo_min, temperature);
			LOGGER.info("Done setting profile.");
			Map<String, Integer> mMap = new HashMap<String, Integer>();
			mMap.put("from_hh", ss.getInt("from_hh"));
			mMap.put("from_mm", ss.getInt("from_mm"));
			mMap.put("to_hh", ss.getInt("to_hh"));
			mMap.put("to_mm", ss.getInt("to_mm"));
			return mMap;
			
		}
		
	}
	
	public final void loadCsvMaterials(String directoryCsv, Material material, Building edificio, boolean fileexist,
			it.sinergis.sunshine.suggestion.map4datapojo.BuildingMap4 buildingMap4) throws NumberFormatException,
			IOException {
		if (fileexist) {
			BufferedReader br = Functions.getBufferedReader(directoryCsv
					+ ReadFromConfig.loadByName("nameCsvMaterials"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				material.id_material = values[0];
				
				if (material.id_material.equals(edificio.id_material)) {
					material.ro = Double.parseDouble(values[1]);
					material.c = Double.parseDouble(values[2]);
					material.lambda_material = Double.parseDouble(values[3]);
				}
			}
			br.close();
		}
		else {
			Delegatedatabasemap4data dbMap4data = new Delegatedatabasemap4data();
			material.ro = buildingMap4.getDenWall();
			material.c = buildingMap4.getSpHeatWall();
			material.lambda_material = buildingMap4.getThermCondWall();
		}
		
	}
	
	public final Map loadCsvTemperature(String directoryCsv, Climate climate, Building edificio, double r2,
			Double avetext, double[] p_future, Double beta0, Double beta1, int timeon_int, boolean fileexist,
			String identifier, int cooling) throws NumberFormatException, IOException, ParseException {
		
		String line = null;
		double p_future_lim = 0.0;
		Map<String, Double> mMap = new HashMap<String, Double>();
		if (fileexist) {
			BufferedReader br = Functions.getBufferedReader(directoryCsv
					+ ReadFromConfig.loadByName("nameCsvTemperatura"));
			
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				
				//calcolo potenza impianto per ogni temperatura esterna ad ogni i
				r2 = (int) (r2 * 100);
				//System.out.println("Affidability = " +  R2 + " %" );
				for (int i = 0; i <= 23; i++) {
					climate.tempextfuture[i] = Double.parseDouble(values[i]);
					//System.out.println("climate.tempextfuture[i] = " +  climate.tempextfuture[i] );
					//if ((-(edificio.tintfut[timeon_int]+4)* beta1)>edificio.tintfut[timeon_int]+4){beta0=-(edificio.tintfut[timeon_int]+4)* beta1;}
					//beta0=-(edificio.tintfut[timeon_int]+4)* beta1;
					avetext = avetext + climate.tempextfuture[i];
					p_future_lim = beta1 * edificio.tintfut[timeon_int] + beta0;
					p_future[i] = beta1 * (climate.tempextfuture[i] - 1) + beta0;
					//System.out.println(climate.tempextfuture[i]);
					if (p_future[i] < p_future_lim) {
						p_future[i] = p_future_lim;
					}
					
					//System.out.println("p_future["+i+"] = "+ p_future[i]);
					//	System.out.println(p_future_lim);
					if (p_future[i] < 0) {
						p_future[i] = 0.0;
					}
					if (climate.tempextfuture[i] > edificio.tintfut[timeon_int] + 4) {
						p_future[i] = 0.0;
					}
				}
			}
			br.close();
		}
		else {
			DelegatedatabaseSos dbsos = new DelegatedatabaseSos();
			int dayOffset = 1;
			int dayOffsetPeriod = 1;
			List<Observation> listOb = dbsos.readParameterObservation(identifier, dayOffset, dayOffsetPeriod, null,
					null);
			List<Numericvalue> listNum = dbsos.readvaluesNumericValue(listOb, null, null);
			//calcolo potenza impianto per ogni temperatura esterna ad ogni i
			r2 = (int) (r2 * 100);
			int i = 0;
			for (Numericvalue numv : listNum) {
				climate.tempextfuture[i] = numv.getValue();
				avetext = avetext + climate.tempextfuture[i];
				i = i + 1;
			}
			
			if (cooling == 0) {
				i = 0;
				for (Numericvalue numv : listNum) {
					p_future_lim = beta1 * edificio.tintfut[timeon_int] + beta0;
					p_future[i] = beta1 * (climate.tempextfuture[i] - 1) + beta0;
					//System.out.println(climate.tempextfuture[i]);
					if (p_future[i] < p_future_lim) {
						p_future[i] = p_future_lim;
					}
					
					if (p_future[i] < 0) {
						p_future[i] = 0.0;
					}
					if (climate.tempextfuture[i] > edificio.tintfut[timeon_int] + 4) {
						p_future[i] = 0.0;
					}
					i = i + 1;
				}
			}
			
		}
		
		try {
			avetext = avetext / 24;
		}
		catch (Exception e) {
			LOGGER.warn("It is possible that there aren't the weather data");
		}
		mMap.put("avetext", avetext);
		mMap.put("R2", (double) r2);
		return mMap;
		
	}
}
