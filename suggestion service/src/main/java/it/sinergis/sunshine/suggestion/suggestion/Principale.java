package it.sinergis.sunshine.suggestion.suggestion;

import it.sinergis.sunshine.suggestion.delegate.DelegatedatabaseSos;
import it.sinergis.sunshine.suggestion.delegate.Delegatedatabasemap4data;
import it.sinergis.sunshine.suggestion.loaddata.LoadDataFromFile;
import it.sinergis.sunshine.suggestion.map4datapojo.Buildingmanager;
import it.sinergis.sunshine.suggestion.map4datapojo.Heatingsystem;
import it.sinergis.sunshine.suggestion.map4datapojo.Thermalzone;
import it.sinergis.sunshine.suggestion.pojo.JsonObject;
import it.sinergis.sunshine.suggestion.rest.Sos;
import it.sinergis.sunshine.suggestion.sospojo.Featureofinterest;
import it.sinergis.sunshine.suggestion.sospojo.TaskStatus;
import it.sinergis.sunshine.suggestion.utils.Email;
import it.sinergis.sunshine.suggestion.utils.Functions;
import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Principale {
	static final Logger LOGGER = Logger.getLogger(Principale.class);
	private double time[] = new double[24];
	private double timeon;
	private int timeoff;
	
	private double time_h[] = new double[24];
	private double casual_gain[] = new double[24];
	private double p_future[] = new double[24];
	private int count = 0;
	private int i = 0;
	private int ii = 0;
	
	//variabili edificio 
	private double h_t;
	private double h_v;
	private double qint; //calore interno
	
	private int timeon_int;
	private int timeoff_int;
	//variabili futuro
	private double qtotfuture[] = new double[24];
	private double qavesolfuture = 0;
	private double avetext;
	private double vent = 0;
	private double fi = 0;
	private double floor = 0;
	private int n = 0;
	
	private double thick = 0;
	private double sumx = 0.0;
	private double sumy = 0.0;
	private double sumx2 = 0.0;
	private int cooling = 0;
	
	public static void stampaRigheMatrice(double[][] A) {
		for (int i = 0; i < A.length; i++) { // scandisce righe
			for (int j = 0; j < A[0].length; j++)
				// scandisce elementi riga i
				System.out.print(A[i][j] + " "); // stampa elemento riga
			System.out.println(); // fine riga
		}
	}
	
	public static double[][] prodottoMatrici(double[][] A, double[][] B) {
		double[][] C = new double[A.length][A[0].length];
		for (int i = 0; i < A.length; i++)
			for (int j = 0; j < A[0].length; j++) {
				C[i][j] = 0;
				for (int k = 0; k < A[0].length; k++)
					C[i][j] += A[i][k] * B[k][j];
			}
		return C;
	}
	
	public static double[][] prodottoMatrici1(double[][] C, double[][] D) {
		double[][] E = new double[C.length][C[0].length];
		for (int i = 0; i < C.length; i++)
			for (int j = 0; j < C[0].length; j++) {
				E[i][j] = 0;
				for (int k = 0; k < C[0].length; k++)
					E[i][j] += C[i][k] * D[k][j];
				
			}
		return E;
	}
	
	public final JsonObject calculate(String directoryCsv, Integer buildingid, String pilot, double sogliaTmin,
			double absTmin, double sogliaTmax, double absTmax, double sogliaTmed, double absTmed) {
		
		DelegatedatabaseSos sosTask = new DelegatedatabaseSos();
		TaskStatus task = sosTask.writeTaskStatusStart(null, null);
		
		JsonObject JO = new JsonObject();
		String timeJson = null;
		Building edificio;
		edificio = new Building();
		Climate climate;
		climate = new Climate();
		Material material;
		material = new Material();
		boolean fileexist = false;
		Session session = null;
		Transaction tx = null;
		it.sinergis.sunshine.suggestion.map4datapojo.BuildingMap4 buildingMap4 = null;
		Thermalzone thermalzone = null;
		Heatingsystem heatingsystem = null;
		
		//TODO da capire come dare i parametri in ingresso..se leggerli da conf ma con gerarchia per ogni edificio?
		if (buildingid == null) {
			edificio.id_edificio = Integer.parseInt(ReadFromConfig.loadByName("idEdificio")); // indica l'edificio
		}
		else {
			edificio.id_edificio = buildingid;
		}
		
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
		//timeon = 7.45; // indica l'orario di accensione (regolazione impianto)
		//		timeon = Double.parseDouble(ReadFromConfig.loadByName("timeon"));
		//		timeoff = Integer.parseInt(ReadFromConfig.loadByName("timeoff"));
		//timeoff = 18; //indica l'orario di spegnimento (regolazione impianto)
		Map<String, Integer> mapTime = null;
		try {
			mapTime = Functions.getTimeFromJsonComfortProfile(buildingid, cal.getTime(), pilot);
		}
		catch (ParseException | JSONException e2) {
			LOGGER.error("No profile");
			return null;
		}
		if (mapTime != null) {
			timeon = Double.parseDouble(mapTime.get("from_hh") + "." + mapTime.get("from_mm"));
			timeoff = mapTime.get("to_hh");
		}
		else {
			timeon = Double.parseDouble(ReadFromConfig.loadByName("timeon"));
			timeoff = Integer.parseInt(ReadFromConfig.loadByName("timeoff"));
		}
		edificio.type = Integer.parseInt(ReadFromConfig.loadByName("typeEdificio"));
		
		//		edificio.system = 1;
		int ora = (int) timeon;
		double sec = (timeon - ora) * 100 / 60;
		double timeon = ora + sec;
		timeon_int = (int) Math.round(timeon);
		timeoff_int = (int) Math.ceil(timeoff);
		
		//controllo se il parametro directoryCsv e' stato passato 
		if (directoryCsv == null) {
			directoryCsv = ReadFromConfig.loadByName("directoryCsv");
		}
		
		//controllo se i file csv ci sono nella directory
		if ((Functions.fileExists(directoryCsv + ReadFromConfig.loadByName("nameCsvPoint"))
				&& Functions.fileExists(directoryCsv + ReadFromConfig.loadByName("nameCsvBuilding"))
				&& Functions.fileExists(directoryCsv + ReadFromConfig.loadByName("nameCsvIrradiation"))
				&& Functions.fileExists(directoryCsv + ReadFromConfig.loadByName("nameCsvInternaFutura"))
				&& Functions.fileExists(directoryCsv + ReadFromConfig.loadByName("nameCsvMaterials")) && Functions
					.fileExists(ReadFromConfig.loadByName("directoryCsv")
							+ ReadFromConfig.loadByName("nameCsvTemperatura")))) {
			//se almeno uno di questi file non esiste
			
			fileexist = true;
			
		}
		else {
			Delegatedatabasemap4data dbMap4data = new Delegatedatabasemap4data();
			buildingMap4 = dbMap4data.readParameterBuilding(buildingid, session, tx, pilot);
			thermalzone = dbMap4data.readParameterThermalZone(buildingid, session, tx, pilot);
			heatingsystem = dbMap4data.readParameterHeatingsystemr(thermalzone.getId(), session, tx, pilot).get(0);
			edificio.type = Functions.getBuildingType(heatingsystem.getHeatCtr());
			if (dbMap4data.readParameterEnergyFromThermalZoneid(thermalzone.getId(), pilot, null, null).getESource()
					.equals("warmwaterorstream")) {
				edificio.system = 1;
			}
			else {
				edificio.system = 0;
			}
			
		}
		
		//PRIMO PASSO LOAD DEI PUNTI
		LoadDataFromFile loadData = new LoadDataFromFile();
		
		String constantCalibration = thermalzone.getCorrTempCons();
		double beta0 = 0;
		double beta1 = 0;
		double R2 = 0;
		if (!constantCalibration.equals("cooling")) {
			beta0 = Double.parseDouble(constantCalibration.split(";")[0]);
			beta1 = Double.parseDouble(constantCalibration.split(";")[1]);
			R2 = Double.parseDouble(constantCalibration.split(";")[2]);
		}
		else {
			cooling = 1;
		}
		
		//Carica dati building 
		//PASSO2 : CARICHIAMO I DATI DEI BUILDING
		
		Map<String, Double> mMap = null;
		try {
			mMap = loadData.loadCsvBuilding(directoryCsv, edificio, fileexist, pilot);
		}
		catch (NumberFormatException e1) {
			LOGGER.error("Error in load building data");
			LOGGER.error(e1);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		}
		h_t = mMap.get("h_t");
		floor = mMap.get("floor");
		
		//PASSO 3 : CARICHIAMO I DATI DELL'IRRADIATION
		
		try {
			qavesolfuture = loadData.loadCsvIrradiation(directoryCsv, climate, fileexist, buildingMap4.getIrradUri());
		}
		catch (NumberFormatException e1) {
			LOGGER.error("Error in load irradiation data");
			LOGGER.error(e1);
		}
		catch (IOException e) {
			LOGGER.error("Error in load irradiation data");
			LOGGER.error(e);
			
		}
		catch (ParseException e) {
			LOGGER.error("Error in load irradiation data");
			LOGGER.error(e);
		}
		
		//PASSO 4 : CARICHIAMO I DATI DEL CONDIZIONAMENTO FUTURO
		//TODO controllare che l'edificio abbia subito il condizionamento
		try {
			Map<String, Integer> mMapCond = loadData.loadCsvCodizionamentoFuturo(directoryCsv, edificio, fileexist,
					pilot);
			
		}
		catch (NumberFormatException e1) {
			LOGGER.error("Error in load profile comfort data");
			LOGGER.error(e1);
		}
		catch (IOException e) {
			LOGGER.error("Error in load profile comfort data");
			LOGGER.error(e);
			
		}
		catch (JSONException e) {
			LOGGER.error("Error in load profile comfort data");
			LOGGER.error(e);
		}
		
		//PASSO 5 : CARICO I DATI DA MATERIALS
		try {
			loadData.loadCsvMaterials(directoryCsv, material, edificio, fileexist, buildingMap4);
		}
		catch (NumberFormatException e1) {
			LOGGER.error("Error in load materials data");
			LOGGER.error(e1);
		}
		catch (IOException e) {
			LOGGER.error(e);
			
		}
		
		double[][] A = { // crea matrice A con dimensione 3x3
		{ 1, -0.04 }, // riga 0 di A (array di 3 double)
				{ 0, 1 }, // riga 1 di A (array di 3 double)
		};
		
		double[][] D = { // crea matrice A con dimensione 3x3
		{ 1, -0.13 }, // riga 0 di A (array di 3 double)
				{ 0, 1 }, // riga 1 di A (array di 3 double)
		};
		thick = material.lambda_material / edificio.uwall;
		double delta = Math.sqrt((material.lambda_material * 86400) / (Math.PI * material.ro * material.c));
		double eps = thick / delta;
		double z11r = Math.cosh(eps) * Math.cos(eps);
		double z11im = Math.sinh(eps) * Math.sin(eps);
		double z12r = -(delta / (2 * material.lambda_material))
				* (Math.sinh(eps) * Math.cos(eps) + Math.cosh(eps) * Math.sin(eps));
		double z12im = -(delta / (2 * material.lambda_material))
				* (Math.cosh(eps) * Math.sin(eps) - Math.sinh(eps) * Math.cos(eps));
		double z21r = -(material.lambda_material / delta)
				* (Math.sinh(eps) * Math.cos(eps) - Math.cosh(eps) * Math.sin(eps));
		double z21im = -(material.lambda_material / delta)
				* (Math.sinh(eps) * Math.cos(eps) + Math.cosh(eps) * Math.sin(eps));
		double z22r = z11r;
		double z22im = z11im;
		
		double[][] B = { { z11r, z12r }, { z21r, z22r }, };
		
		double[][] BB = { { z11im, z12im }, { z21im, z22im }, };
		
		double[][] C = prodottoMatrici(A, B);
		double[][] CC = prodottoMatrici(A, BB);
		double[][] E = prodottoMatrici1(C, D);
		double[][] EE = prodottoMatrici1(CC, D);
		double y11 = Math.sqrt((Math.pow(E[0][0], 2) + Math.pow(EE[0][0], 2))
				/ (Math.pow(E[0][1], 2) + Math.pow(EE[0][1], 2)));
		double decr_factor = (1 / (Math.sqrt((Math.pow(E[0][1], 2) + Math.pow(EE[0][1], 2))))) / edificio.uwall;
		//System.out.println(" decr_factor = " +  decr_factor );   
		double fire = -(E[0][1]);
		double fimm = -(EE[0][1]);
		if (fimm == 0) {
			fi = 0;
		}
		else if (fire > 0 && fimm > 0) {
			fi = ((12 / Math.PI) * (Math.atan(fimm / fire)));
		}
		else if (fire == 0 && fimm > 0) {
			fi = 0.5 * Math.PI;
		}
		else if (fire < 0 && fimm > 0) {
			fi = ((12 / Math.PI) * (Math.atan(fimm / fire) + Math.PI));
		}
		else if (fire > 0 && fimm < 0) {
			fi = ((12 / Math.PI) * (Math.atan(fimm / fire) + 2 * Math.PI));
		}
		else if (fire == 0 && fimm < 0) {
			fi = ((12 / Math.PI) * (Math.atan(fimm / fire) + 1.5 * Math.PI));
		}
		else if (fire < 0 && fimm < 0) {
			fi = ((12 / Math.PI) * (Math.atan(fimm / fire) + Math.PI));
		}
		else if (fire < 0 && fimm < 0) {
			fi = ((12 / Math.PI) * (Math.atan(fimm / fire) + Math.PI));
		}
		
		edificio.tau = (int) ((fi * edificio.awall) / (edificio.awall + edificio.awindows));
		//System.out.println("f (h)= " + fi);    
		
		double ydin = 1 / Math.sqrt(Math.pow(E[0][1], 2) + Math.pow(EE[0][1], 2));
		
		edificio.adme = y11;
		
		vent = 0.5;
		h_v = edificio.volume * 0.34 * vent;
		
		Double heatFlowOccupants = 7.0;
		if (buildingMap4.getHeatFlowOccupants() != null) {
			heatFlowOccupants = buildingMap4.getHeatFlowOccupants();
		}
		else {
			LOGGER.error("HeatFlowOccupants value not present. Will be set to 7");
		}
		
		double qint = 0.7 * edificio.aroof * heatFlowOccupants * floor;
		
		//PASSO 6 : CARICO I DATI DELLA TEMPERATURA
		Map<String, Double> mapTemp = null;
		
		try {
			mapTemp = loadData.loadCsvTemperature(directoryCsv, climate, edificio, R2, avetext, p_future, beta0, beta1,
					timeon_int, fileexist, buildingMap4.getTempUri(), cooling);
		}
		catch (NumberFormatException e) {
			LOGGER.error("Error in load profile comfort data");
			LOGGER.error(e);
			
		}
		catch (IOException e) {
			LOGGER.error(e);
			
		}
		catch (ParseException e) {
			LOGGER.error("Error in load profile comfort data");
			LOGGER.error(e);
		}
		//TODO SOGLIAAAAAA
		avetext = mapTemp.get("avetext");
		R2 = mapTemp.get("R2");
		
		double calore_sinistra = edificio.volume * 1.29 * 1005;
		double calore_destra = 3600 * (ydin * h_t + h_v);
		double tint = (calore_sinistra * edificio.tintfut[timeon_int] + calore_destra * avetext)
				/ (calore_sinistra + calore_destra);
		//System.out.println("ht= " +  h_t + " ; " + "hv= " + h_v+ " ; " + "qint =" + qint);
		double qAves = 0.6 * 0.7 * .9 * 0.75 * (edificio.awindows / 4) * (qavesolfuture);
		
		// System.out.println("qAves= "+qAves);
		//mean casual gains
		double qMeanTot = qAves + qint;
		//  System.out.println("qMeanTot = "+qMeanTot);
		//internal environmental temperature
		double tetailast[] = new double[24];
		System.out.println("avetext = " + avetext);
		double tetai = avetext + (qMeanTot) / (h_t + h_v);
		System.out.println("tetai = " + tetai);
		double effSolHeat[] = new double[24];
		double strutGain[] = new double[24];
		double gainAir[] = new double[24];
		System.out.println();
		// System.out.println("edificio.tau= "+edificio.tau);
		//Ciclo per calcolo temperatura nel periodo di accensione compreso il contributo dinamico
		for (i = 0; i <= 23; i++) {
			//swing in effective solar heat gain
			effSolHeat[i] = 0.6 * 0.7 * .9 * 0.75 * (edificio.awindows / 4) * (climate.qsolfuture[i] - qavesolfuture);
			//  System.out.println("effSolHeat["+i+"]= "+effSolHeat[i]);
			//struttural gain
			
			if (i >= edificio.tau) {
				strutGain[i] = decr_factor * h_t * (climate.tempextfuture[i - edificio.tau] - avetext);
			}
			else {
				strutGain[i] = decr_factor * h_t * (climate.tempextfuture[i] - avetext);
			}
			
			//swing in gain, air-to-air
			gainAir[i] = (edificio.uwindow * edificio.awindows + h_v) * (climate.tempextfuture[i] - avetext);
			
			//contributo calore interno
			if (i < timeon_int) {
				casual_gain[i] = (0 - qint);
			}
			else {
				casual_gain[i] = 0;
			}
			
			// System.out.println("casual_gain["+i+"]= "+ casual_gain[i]);
			qtotfuture[i] = (effSolHeat[i] + strutGain[i] + gainAir[i] + casual_gain[i]);
			
			// System.out.println("qtotfuture["+i+"] = " + qtotfuture[i]);
			//peak internal environmental temperature 
			tetailast[i] = tint + qtotfuture[i] / (h_v + edificio.awall * edificio.adme);
			
			System.out.println("tetai [" + i + "] = " + tetailast[i]);
		}
		
		int ultimoIndiceNone = 0;
		int ultimoIndiceHeat = 0;
		int modality[] = new int[24];
		
		if (cooling == 0) {
			for (i = 1; i <= 23; i++) {
				int t = 1;
				//calcolo tempo di accensione e controllo
				if (i >= edificio.tau) {
					t = i - edificio.tau;
				}
				else {
					t = 1;
				}
				double q1 = edificio.uwall * (tetailast[i] - climate.tempextfuture[i]);
				double t1 = tetailast[i] - q1 / 8;
				double q2 = edificio.uwall * (edificio.tintfut[timeon_int] - climate.tempextfuture[i]);
				double t2 = edificio.tintfut[timeon_int] - q2 / 8;
				//				double dt = t2 - t1;
				// System.out.println("dt= "+dt); 
				
				if (p_future[i] > 0) {
					if (edificio.system == 0) {
						time[i] = ((((edificio.volume * 1.29 * 1005 * (edificio.tintfut[timeon_int] - tetailast[i]) + 3600
								* (ydin * h_t + h_v)
								* (((edificio.tintfut[timeon_int] + tetailast[i]) / 2) - climate.tempextfuture[i])) / ((0.7 * p_future[i]) * 10.35 * 1000))));
						
					}
					else {
						time[i] = ((((edificio.volume * 1.29 * 1005 * (edificio.tintfut[timeon_int] - tetailast[i]) + 3600
								* (ydin * h_t + h_v)
								* (((edificio.tintfut[timeon_int] + tetailast[i]) / 2) - climate.tempextfuture[i])) / ((0.7 * p_future[i]) * 1000))));
						
					}
					
					time_h[i] = (time[i]) / 3600;
					double time_hint = Math.ceil(time_h[i]);
					
					if (time[i] < 0) {
						time_h[i] = 0;
					}
					
					// time_h[i]= time[i]/3600;
					//System.out.println(" time["+i+"]= "+   time_h[i]); 
					if ((time_hint + i) >= (timeon_int)) {
						
						double time = (timeon - time_h[i]);
						
						// System.out.println(" "); 
						System.out.println();
						ora = (int) time;
						sec = (time - ora) * 60 / 100;
						time = ora + sec;
						time = new BigDecimal(time).setScale(2, BigDecimal.ROUND_UP).doubleValue();
						System.out.println("Turn on at " + (time));
						timeJson = Double.toString(time);
						break;
					}
					if ((time_hint + i) > (timeon_int)) {
						
						//						double time = (i - time_h[i]);
					}
				}
			}
			
			//controllo per suggestion durante il giorno a seconda del tipo di termoregolazione
			if (edificio.type == 0) {
				for (i = (timeon_int); i <= (timeoff_int); i++) {
					if (tetailast[i] >= edificio.tintfut[i]) {
						
						modality[i] = 0;
						ultimoIndiceNone = i;
					}
					
					else if (tetailast[i] < edificio.tintfut[i]) {
						
						modality[i] = 1;
						System.out.println("== Nessuna variazione == ");
						ultimoIndiceHeat = i;
					}
					System.out.println(modality[i]);
				}
				
				for (i = (timeon_int + 1); i <= (timeoff_int - 1); i++) {
					if (modality[i] == 0) {
						if (modality[i] != modality[i - 1] && modality[i] == modality[i + 1]) {
							
							if (ultimoIndiceNone == timeoff_int - 1) {
								System.out.println(" == Alle " + i + " non è necessario il riscaldamento == ");
								break;
							}
							
							else if (ultimoIndiceNone != timeoff_int - 1) {
								System.out.println("== Dalle " + i + " alle " + ultimoIndiceNone
										+ "  sono sufficienti 17 °C == ");
								System.out.println("== Alle " + (ultimoIndiceNone + 1) + "  sono necessari "
										+ edificio.tintfut[ultimoIndiceHeat] + " °C == ");
							}
						}
					}
				}
			}
			else if (edificio.type == 1) {
				for (i = (timeon_int); i <= (timeoff_int - 1); i++) {
					
					//Suggestion
					if (tetailast[i] >= edificio.tintfut[i]) {
						if (i == timeon_int) {
							System.out.println(" == Alle " + i + " non è necessario il riscaldamento == ");
						}
						modality[i] = 0;
						ultimoIndiceNone = i;
					}
					
					else if (tetailast[i] < edificio.tintfut[i]) {
						
						modality[i] = 1;
						ultimoIndiceHeat = i;
					}
					
					System.out.println("Work system at [" + i + "]= " + modality[i]);
				}
				for (i = (timeon_int + 1); i <= (timeoff_int - 1); i++) {
					if (modality[i] == 0) {
						if (modality[i] != modality[i - 1] && modality[i] == modality[i + 1]) {
							
							if (ultimoIndiceNone == timeoff_int - 1) {
								System.out.println(" == Alle " + i + " non è necessario il riscaldamento == ");
								break;
							}
							
							else if (ultimoIndiceNone != timeoff_int - 1) {
								System.out.println(" == Alle " + i + " non è necessario il riscaldamento == ");
							}
						}
					}
					else if (modality[i] == 1) {
						if (modality[i] != modality[i - 1] && modality[i] == modality[i + 1]
								&& modality[i - 1] == modality[i - 2]) {
							
							double time = (i - time_h[i]);
							ora = (int) time;
							sec = (time - ora) * 60 / 100;
							time = ora + sec;
							timeon_int = (int) Math.round(timeon);
							time = new BigDecimal(time).setScale(2, BigDecimal.ROUND_UP).doubleValue();
							System.out
									.println("== Alle " + time + " sono necessari " + edificio.tintfut[i] + " °C == ");
							break;
						}
					}
				}
			}
			else if (edificio.type == 2) {
				{
					System.out.println("== Suggestion == ");
				}
			}
		}
		else {
			
			//------------------------------
			
			//controllo per suggestion durante il giorno a seconda del tipo di termoregolazione
			for (i = (timeon_int); i <= (timeoff_int - 1); i++) {
				if (tetailast[i] <= edificio.tintfut[i]) {
					
					modality[i] = 0;
					ultimoIndiceNone = i;
					
				}
				
				else if (tetailast[i] > edificio.tintfut[i]) {
					
					modality[i] = 1;
					// System.out.println("== Nessuna variazione == ");
					ultimoIndiceHeat = i;
				}
				//System.out.println( modality[i])	;	
			}
			
			for (i = (timeon_int); i <= (timeoff_int - 1); i++) {
				if (modality[i] == 0) {
					
					if (ultimoIndiceNone == timeoff_int - 1) {
						System.out.println(" == Alle " + i + "non è necessario il raffrescamento == ");
						break;
					}
				}
			}
			
			for (int i = 0; i < modality.length; i++) {
				if (modality[i] == 1) {
					timeJson = String.valueOf(i);
				}
			}
			
			//---------------------
		}
		if (timeJson == null) {
			timeJson = Functions.getPosition(modality, 1);
		}
		JO.setAvetext(Double.toString(avetext));
		JO.setTetaiAvg(Double.toString(tetai));
		JO.setTurnOn(timeJson);
		JO.setArrayTetai(tetailast);
		List<String> modalityString = new ArrayList<String>();
		for (double modalitiesimo : modality) {
			modalityString.add(String.valueOf(modalitiesimo));
		}
		JO.setArrayWorkSystem(modalityString);
		
		DelegatedatabaseSos dbsos = new DelegatedatabaseSos();
		Featureofinterest foi = dbsos.readFeatureofInterest(buildingMap4.getFoiid(), null, null);
		
		Sos sos = new Sos();
		//		boolean positiveResponse = sos.writeObservation(tetailast, pilot, task);
		//		if (positiveResponse) {
		//			sosTask.writeTaskStatusEnd(null, null, task);
		//		}
		
		sos.writeObservation(tetailast, modality, timeJson, pilot, task, buildingMap4, foi.getIdentifier(),
				Functions.getNormalizeHours(timeoff));
		
		LOGGER.info("Observations submitted");
		Map<String, String> map = Functions.getSetupSos(pilot);
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		//		List<String> listIdentifier = new ArrayList<String>();
		//		listIdentifier.add(ReadFromConfig.loadByName("radiceObservation") + map.get("offering"));
		//		listIdentifier.add(ReadFromConfig.loadByName("radiceObservation") + map.get("offeringStato"));
		//sosTask.delete(listIdentifier, cal.getTime());
		//		sosTask.writeTaskStatusEnd(null, null, task);
		Delegatedatabasemap4data dbmap4data = new Delegatedatabasemap4data();
		List<Buildingmanager> buildManager = dbmap4data.readParameterBuildingmanager(buildingid, null, null, pilot);
		if (timeJson == null) {
			timeJson = "/";
			LOGGER.info("NO TURN_ON");
		}
		else {
			timeJson = Functions.getNormalizeHours(Double.parseDouble(timeJson));
		}
		LOGGER.info("Manager:");
		for (Buildingmanager buildManagerIesimo : buildManager) {
			LOGGER.info(buildManagerIesimo.getName() + " : build id  " + buildManagerIesimo.getBuildingid());
		}
		it.sinergis.sunshine.suggestion.map4datapojo.Calendar calendarMap4 = null;
		Integer temp = null;
		try {
			LOGGER.info("Read parameter calendar: ");
			calendarMap4 = dbmap4data.readParameterCalendar(buildingid, cal.getTime(), pilot, null, null);
		}
		catch (ParseException e1) {
			// TODO Auto-generated catch block
			temp = 20;
			LOGGER.error("Temperature of profile comfort not read.", e1);
		}
		try {
			JSONObject profile = Functions.getJsonFromString(calendarMap4.getProfilecomfort());
			temp = profile.getInt("T");
		}
		catch (JSONException e) {
			temp = 20;
			// TODO Auto-generated catch block
			LOGGER.error("Temperature of profile comfort not read.", e);
		}
		//verifico se la soglia minima e' stata superata dalla media delle 5 temp minime abs(avgDayBeforeMin - avgDayAfterMin)
		//if (sogliaTmin < absTmin) {
		LOGGER.info("Starting process sending email..");
		
		if (sogliaTmin < absTmin) {
			String responseEmail = Functions.getStringFromOutputSuggestion(tetailast, timeJson, modality, pilot,
					buildingid, sogliaTmin, absTmin, sogliaTmax, absTmax, sogliaTmed, absTmed, buildingMap4.getName(),
					Functions.getNormalizeHours(timeon), Functions.getNormalizeHours(timeoff), temp);
			LOGGER.info("Email to sent");
			LOGGER.info(responseEmail);
			for (Buildingmanager buildingmanageriesimo : buildManager) {
				LOGGER.info("Sending email to " + buildingmanageriesimo.getEmail() + "[idbuilding : "
						+ buildingmanageriesimo.getBuildingid() + "]");
				Email.sendEmail(ReadFromConfig.loadByName("fromEmailSuggestion"), buildingmanageriesimo.getEmail(),
						null, "Suggestion building " + buildingMap4.getName() + " - " + pilot, responseEmail);
			}
		}
		
		return JO;
	}
}
