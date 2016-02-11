package it.sinergis.sunshine.suggestion.suggestionShelter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Principale_backup {
	
	public static void main(String[] args) {
		Shelter shelter;
		shelter = new Shelter();
		
		Climate climate;
		climate = new Climate();
		
		double uroof;
		double ufloor;
		double uwalls;
		double awall;
		double afloor;
		double aroof;
		double capin;
		double capout;
		int i = 0;
		int j = 0;
		int countcond = 0;
		int countheat = 0;
		double m = 0; // portata d'aria
		double dh = 0; // differenza entalpia
		int a = 0; //variabile per funzionamento impianto raffrescamento
		int b = 0;
		double hourcond = 0;
		double hourheat = 0;
		
		double wind[] = new double[48];
		double qint;
		double uenvelope = 0;
		double tint[] = new double[3600];
		double tint1[] = new double[3600];
		double tsout[] = new double[3600];
		double tsint[] = new double[3600];
		double tsint1[] = new double[3600];
		double tsout1[] = new double[3600];
		
		//Carica dati shelter 
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("db_shelter.csv")));
			String line = null;
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				shelter.qint = Double.parseDouble(values[1]);
				shelter.uwalls = Double.parseDouble(values[2]);
				shelter.uroof = Double.parseDouble(values[3]);
				shelter.ufloor = Double.parseDouble(values[4]);
				shelter.awall = Double.parseDouble(values[5]);
				shelter.aroof = Double.parseDouble(values[6]);
				shelter.vol = Double.parseDouble(values[7]);
				shelter.capin = Double.parseDouble(values[8]);
				shelter.capout = Double.parseDouble(values[9]);
			}
			br.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found1");
		}
		catch (IOException e) {
			System.out.println("IO error");
		}
		
		//Carica dati irradianza oraria futura  
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("db_irr_21_22.csv")));
			String line = null;
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				for (i = 0; i <= 47; i++) {
					climate.irr[i] = Double.parseDouble(values[i]);
				}
			}
			br.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found2");
		}
		catch (IOException e) {
			System.out.println("IO error");
		}
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("db_wind.csv")));
			String line = null;
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				for (i = 0; i <= 47; i++) {
					climate.wind[i] = Double.parseDouble(values[i]);
				}
			}
			br.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found2");
		}
		catch (IOException e) {
			System.out.println("IO error");
		}
		//Rinomino le variabili
		uroof = shelter.uroof;
		uwalls = shelter.uwalls;
		ufloor = shelter.ufloor;
		awall = shelter.awall;
		afloor = shelter.aroof;
		aroof = shelter.aroof;
		qint = shelter.qint;
		
		//Calcolo U-value envelope
		
		uenvelope = ((uroof * aroof) + (ufloor * afloor) * .5 + (4 * awall * uwalls)) / (aroof + afloor + 4 * awall);
		//Inizializzazione dati
		
		capin = shelter.capin;
		capout = shelter.capout;
		
		shelter.tint_mn = 24;
		
		tint[0] = shelter.tint_mn;
		tsout1[0] = tsout[0];
		tsint1[0] = tsint[0];
		tsint[0] = tint[0];
		tint1[0] = tsint[0];
		tsout[0] = climate.temp[0];
		
		//Lettura dati temperature 
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
					"21-22-8-2014_smichele.csv")));
			String line = null;
			while ((line = br.readLine()) != null) {
				String values[] = line.split(";");
				
				//cicli orari. Il numero di giorni dipende dal tempo di anticipo della previsione futura
				for (i = 0; i <= 47; i++) {
					climate.temp[i] = Double.parseDouble(values[i]);
					
					//ciclo per l'andamento orario con passo temporale di un secondo
					
					//Condizione Free Cooling
					for (j = 0; j <= 3598; j++) {
						if (climate.temp[i] > 13 && (tint[j] - climate.temp[i]) >= 2.5 && tint[j] > 24 && tint[j] < 27
								&& a == 0) {
							m = 0.03;
							tsout[j + 1] = tsout[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint[j] - tsout[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint[j + 1] = tsint[j]
									+ (((awall * 4 + aroof) * 8 * (tint[j] - tsint[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout[j] - tsint[j]) + qint * 1) / capin);
							tint[j + 1] = tint[j]
									+ ((awall * 4 + aroof) * 8 * (tsint[j] - tint[j]) + 30 * (tint1[j] - tint[j]) + 1200
											* m * (climate.temp[i] - tint[j])) / 500000;
							
							tsout1[j + 1] = tsout1[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint1[j] - tsout1[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint1[j + 1] = tsint1[j]
									+ (((awall * 4 + aroof) * 8 * (tint1[j] - tsint1[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout1[j] - tsint1[j])) / capin);
							tint1[j + 1] = tint1[j]
									+ ((awall * 4 + aroof) * 8 * (tsint1[j] - tint1[j]) + 30 * (tint[j] - tint1[j]))
									/ 500000;
							
						}
						
						//Condizione funzionamento impianto raffrescamento
						else if (tint[j] > 27) {
							if (i > 23) {
								hourcond = hourcond + 1;
								if (a == 0) {
									countcond = countcond + 1;
								}
							}
							dh = 10.38;
							a = 1;
							m = 0.12;
							//System.out.println("qtot " + qtot[j]);
							tsout[j + 1] = tsout[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint[j] - tsout[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint[j + 1] = tsint[j]
									+ (((awall * 4 + aroof) * 8 * (tint[j] - tsint[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout[j] - tsint[j]) + qint * 1) / capin);
							tint[j + 1] = tint[j]
									+ ((awall * 4 + aroof) * 8 * (tsint[j + 1] - tint[j]) + 30 * (tint1[j] - tint[j]) - (dh
											* m * 1000)) / 500000;
							
							tsout1[j + 1] = tsout1[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint1[j] - tsout1[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint1[j + 1] = tsint1[j]
									+ (((awall * 4 + aroof) * 8 * (tint1[j] - tsint1[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout1[j] - tsint1[j])) / capin);
							tint1[j + 1] = tint1[j]
									+ ((awall * 4 + aroof) * 8 * (tsint1[j] - tint1[j]) + 30 * (tint[j] - tint1[j]))
									/ 500000;
							
						}
						
						//Condizione funzionamento impianto raffrescamento tra i 24 ed i 27 �C
						else if (tint[j] > 24 && a == 1) {
							if (climate.temp[i] > 24) {
								dh = 8.38;
							}
							else {
								dh = 10.38;
							}
							
							m = 0.12;
							//System.out.println("qtot " + qtot[j]);
							tsout[j + 1] = tsout[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint[j] - tsout[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint[j + 1] = tsint[j]
									+ (((awall * 4 + aroof) * 8 * (tint[j] - tsint[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout[j] - tsint[j]) + qint * 1) / capin);
							tint[j + 1] = tint[j]
									+ ((awall * 4 + aroof) * 8 * (tsint[j + 1] - tint[j]) + 30 * (tint1[j] - tint[j]) - (dh
											* m * 1000)) / 500000;
							
							tsout1[j + 1] = tsout1[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint1[j] - tsout1[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint1[j + 1] = tsint1[j]
									+ (((awall * 4 + aroof) * 8 * (tint1[j] - tsint1[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout1[j] - tsint1[j])) / capin);
							tint1[j + 1] = tint1[j]
									+ ((awall * 4 + aroof) * 8 * (tsint1[j] - tint1[j]) + 30 * (tint[j] - tint1[j]))
									/ 500000;
							if (i > 23) {
								
								hourcond = hourcond + 1;
								
							}
							
						}
						
						//Condizione funzionamento impianto riscaldamento
						else if (tint[j] < 5) {
							if (i > 23) {
								hourheat = hourheat + 1;
								if (b == 0) {
									countheat = countheat + 1;
								}
							}
							b = 1;
							m = 0.1;
							//System.out.println("qtot " + qtot[j]);
							tsout[j + 1] = tsout[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint[j] - tsout[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint[j + 1] = tsint[j]
									+ (((awall * 4 + aroof) * 8 * (tint[j] - tsint[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout[j] - tsint[j]) + qint * 1) / capin);
							tint[j + 1] = tint[j]
									+ ((awall * 4 + aroof) * 8 * (tsint[j + 1] - tint[j]) + 30 * (tint1[j] - tint[j]) + 3600)
									/ 500000;
							
							tsout1[j + 1] = tsout1[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint1[j] - tsout1[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint1[j + 1] = tsint1[j]
									+ (((awall * 4 + aroof) * 8 * (tint1[j] - tsint1[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout1[j] - tsint1[j])) / capin);
							tint1[j + 1] = tint1[j]
									+ ((awall * 4 + aroof) * 8 * (tsint1[j] - tint1[j]) + 30 * (tint[j] - tint1[j]))
									/ 500000;
							
						}
						
						//Condizione funzionamento impianto riscaldamento tra i 7 ed i 5 �C
						else if (tint[j] < 7 && b == 1) {
							if (i > 23) {
								hourheat = hourheat + 1;
							}
							m = 0.1;
							//System.out.println("qtot " + qtot[j]);
							tsout[j + 1] = tsout[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint[j] - tsout[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint[j + 1] = tsint[j]
									+ (((awall * 4 + aroof) * 8 * (tint[j] - tsint[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout[j] - tsint[j]) + qint * 1) / capin);
							tint[j + 1] = tint[j]
									+ ((awall * 4 + aroof) * 8 * (tsint[j + 1] - tint[j]) + 30 * (tint1[j] - tint[j]) + 3600)
									/ 500000;
							
							tsout1[j + 1] = tsout1[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint1[j] - tsout1[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint1[j + 1] = tsint1[j]
									+ (((awall * 4 + aroof) * 8 * (tint1[j] - tsint1[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout1[j] - tsint1[j])) / capin);
							tint1[j + 1] = tint1[j]
									+ ((awall * 4 + aroof) * 8 * (tsint1[j] - tint1[j]) + 30 * (tint[j] - tint1[j]))
									/ 500000;
						}
						
						//Condizione senza nessun condizionamento
						else {
							a = 0;
							b = 0;
							tsout[j + 1] = tsout[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint[j] - tsout[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint[j + 1] = tsint[j]
									+ (((awall * 4 + aroof) * 8 * (tint[j] - tsint[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout[j] - tsint[j]) + qint * 1) / capin);
							tint[j + 1] = tint[j]
									+ ((awall * 4 + aroof) * 8 * (tsint[j] - tint[j]) + 30 * (tint1[j] - tint[j]))
									/ 500000;
							
							tsout1[j + 1] = tsout1[j]
									+ (((awall * 4 + aroof) * (4 + 4 * climate.wind[i]) * (climate.temp[i] - tsout[j])
											+ (awall * 4 + aroof) * uenvelope * (tsint1[j] - tsout1[j]) + 0.2
											* climate.irr[i] * aroof) / capout);
							tsint1[j + 1] = tsint1[j]
									+ (((awall * 4 + aroof) * 8 * (tint1[j] - tsint1[j]) + (awall * 4 + aroof)
											* uenvelope * (tsout1[j] - tsint1[j])) / capin);
							tint1[j + 1] = tint1[j]
									+ ((awall * 4 + aroof) * 8 * (tsint1[j] - tint1[j]) + 30 * (tint[j] - tint1[j]))
									/ 500000;
						}
						
						//Condizione senza nessun condizionamento
						if (j == 3598) {
							tint[0] = tint[3599];
							tsint[0] = tint[3599];
							tsout[0] = tsout[3599];
							tint1[0] = tint1[3599];
							tsint1[0] = tint1[3599];
							tsout1[0] = tsout1[3599];
						}
						
						//Visualizzazione temperatura solo per il giorno futuro
						if (i > 23) {
							tint[j] = new BigDecimal(tint[j]).setScale(2, BigDecimal.ROUND_UP).doubleValue();
							final String COMMA_SEPERATED = "###,###.###";
							
							DecimalFormat decimalFormat = new DecimalFormat(COMMA_SEPERATED);
							decimalFormat.applyPattern(COMMA_SEPERATED);
							System.out.println(decimalFormat.format(tint[j]));
							
						}
					}
				}
				
				//Visualizzazione ore
				hourcond = hourcond / 3600;
				hourheat = hourheat / 3600;
				hourcond = new BigDecimal(hourcond).setScale(2, BigDecimal.ROUND_UP).doubleValue();
				hourheat = new BigDecimal(hourheat).setScale(2, BigDecimal.ROUND_UP).doubleValue();
				
				//System.out.println("Ore raffrescamento = " + hourcond + " ore");
				System.out.println("Number of conditioning system  = " + countcond);
				System.out.println("Number of heating system = " + countheat);
			}
			
			br.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		catch (IOException e) {
			System.out.println("IO error");
			
		}
	}
}
