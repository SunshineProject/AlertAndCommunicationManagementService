// package it.sinergis.sunshine.suggestion.suggestion;
//
// import it.sinergis.sunshine.suggestion.delegate.Delegatecsv;
// import it.sinergis.sunshine.suggestion.loaddata.LoadDataFromFile;
// import it.sinergis.sunshine.suggestion.utils.Functions;
// import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;
//
// import java.io.IOException;
// import java.math.BigDecimal;
// import java.util.Map;
//
// import org.apache.log4j.Logger;
//
// public class Principale_backup {
// static final Logger LOGGER = Logger.getLogger(Principale_backup.class);
// static double time[] = new double[24];
// static double timeon;
// static int timeoff;
//
// static double time_h[] = new double[24];
// static double casual_gain[] = new double[24];
// static double p_future[] = new double[24];
// static int count = 0;
// static int i = 0;
// static int ii = 0;
//
// //variabili edificio
// static double h_t;
// static double h_v;
// static double qint; //calore interno
//
// static int timeon_int;
// static int timeoff_int;
// //variabili futuro
// static double qtotfuture[] = new double[24];
// static double qavesolfuture = 0;
// static double avetext;
// static double vent = 0;
// static double fi = 0;
// static double floor = 0;
// static int n = 0;
//
// static double thick = 0;
// static double sumx = 0.0;
// static double sumy = 0.0;
// static double sumx2 = 0.0;
// static int cooling = 0;
//
// public static void stampaRigheMatrice(double[][] A) {
// for (int i = 0; i < A.length; i++) { // scandisce righe
// for (int j = 0; j < A[0].length; j++)
// // scandisce elementi riga i
// System.out.print(A[i][j] + " "); // stampa elemento riga
// System.out.println(); // fine riga
// }
// }
//
// public static double[][] prodottoMatrici(double[][] A, double[][] B) {
// double[][] C = new double[A.length][A[0].length];
// for (int i = 0; i < A.length; i++)
// for (int j = 0; j < A[0].length; j++) {
// C[i][j] = 0;
// for (int k = 0; k < A[0].length; k++)
// C[i][j] += A[i][k] * B[k][j];
// }
// return C;
// }
//
// public static double[][] prodottoMatrici1(double[][] C, double[][] D) {
// double[][] E = new double[C.length][C[0].length];
// for (int i = 0; i < C.length; i++)
// for (int j = 0; j < C[0].length; j++) {
// E[i][j] = 0;
// for (int k = 0; k < C[0].length; k++)
// E[i][j] += C[i][k] * D[k][j];
//
// }
// return E;
// }
//
// public final boolean calculate() {
//
// Building edificio;
// edificio = new Building();
// Climate climate;
// climate = new Climate();
// Material material;
// material = new Material();
//
// //TODO da capire come dare i parametri in ingresso..se leggerli da conf ma con gerarchia per ogni edificio?
// edificio.id_edificio = 13; // indica l'edificio
// timeon = 7.45; // indica l'orario di accensione (regolazione impianto)
// timeoff = 18; //indica l'orario di spegnimento (regolazione impianto)
// edificio.type = 1;
// edificio.system = 1;
// int ora = (int) timeon;
// double sec = (timeon - ora) * 100 / 60;
// double timeon = ora + sec;
// timeon_int = (int) Math.round(timeon);
// timeoff_int = (int) Math.ceil(timeoff);
//
// //controllo se i file csv ci sono nella directory
// if (!(Functions.fileExists(ReadFromConfig.loadByName("directoryCsv")
// + ReadFromConfig.loadByName("nameCsvPoint"))
// && Functions.fileExists(ReadFromConfig.loadByName("directoryCsv")
// + ReadFromConfig.loadByName("nameCsvBuilding"))
// && Functions.fileExists(ReadFromConfig.loadByName("directoryCsv")
// + ReadFromConfig.loadByName("nameCsvIrradiation"))
// && Functions.fileExists(ReadFromConfig.loadByName("directoryCsv")
// + ReadFromConfig.loadByName("nameCsvInternaFutura"))
// && Functions.fileExists(ReadFromConfig.loadByName("directoryCsv")
// + ReadFromConfig.loadByName("nameCsvMaterials")) && Functions.fileExists(ReadFromConfig
// .loadByName("directoryCsv") + ReadFromConfig.loadByName("nameCsvTemperatura")))) {
// //se almeno uno di questi file non esiste
//
// //creo i csv se ce ne fosse bisogno
// Delegatecsv.createFileCsv();
//
// }
//
// //PRIMO PASSO LOAD DEI PUNTI
// LoadDataFromFile loadData = new LoadDataFromFile();
// Map<String, Double> mapPoint = null;
// try {
// mapPoint = loadData.loadCsvPoint(climate, edificio, n);
// }
// catch (NumberFormatException e1) {
// // TODO Auto-generated catch block
// LOGGER.error(e1);
// }
// catch (IOException e) {
// // TODO Auto-generated catch block
// LOGGER.error(e);
// }
//
// //OPERAZIONI SULLE COORD APPENA RICAVATE
// n = mapPoint.get("n").intValue();
// double xbar = mapPoint.get("sumx") / n;
// double ybar = mapPoint.get("sumy") / n;
//
// // second pass: compute summary statistics
// double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
// for (int i = 0; i < n; i++) {
// xxbar += (climate.tempextpast[i] - xbar) * (climate.tempextpast[i] - xbar);
// yybar += (edificio.qpast[i] - ybar) * (edificio.qpast[i] - ybar);
// xybar += (climate.tempextpast[i] - xbar) * (edificio.qpast[i] - ybar);
// }
// // double beta1 = xybar / xxbar;
// // double beta0 = ybar - beta1 * xbar;
// double beta1 = Double.valueOf(ReadFromConfig.loadByName("beta1"));
// double beta0 = Double.valueOf(ReadFromConfig.loadByName("beta0"));
//
// // print results
// ///System.out.println("y  = " + beta0 + " "+ beta1 +"x");
//
// // analyze results
// int df = n - 2;
// double rss = 0.0; // residual sum of squares
// double ssr = 0.0; // regression sum of squares
// for (int i = 0; i < n; i++) {
// double fit = beta1 * climate.tempextpast[i] + beta0;
// rss += (fit - edificio.qpast[i]) * (fit - edificio.qpast[i]);
// ssr += (fit - ybar) * (fit - ybar);
// }
// // double R2 = ssr / yybar;
// double R2 = Double.valueOf(ReadFromConfig.loadByName("R2"));
// // double svar = rss / df;
// // double svar1 = svar / xxbar;
// // double svar0 = svar / n + xbar * xbar * svar1;
//
// //Carica dati building
// //PASSO2 : CARICHIAMO I DATI DEI BUILDING
//
// Map<String, Double> mapBuilding = null;
// try {
// mapBuilding = loadData.loadCsvBuilding(edificio);
// }
// catch (NumberFormatException e1) {
// // TODO Auto-generated catch block
// e1.printStackTrace();
// }
// catch (IOException e) {
// // TODO Auto-generated catch block
// LOGGER.error(e);
// }
// h_t = mapBuilding.get("h_t");
// floor = mapBuilding.get("floor");
//
// //PASSO 3 : CARICHIAMO I DATI DELL'IRRADIATION
//
// try {
// qavesolfuture = loadData.loadCsvIrradiation(climate);
// }
// catch (NumberFormatException e1) {
// // TODO Auto-generated catch block
// e1.printStackTrace();
// }
// catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// //PASSO 4 : CARICHIAMO I DATI DEL CONDIZIONAMENTO FUTURO
// //TODO controllare che l'edificio abbia subito il condizionamento
// try {
// loadData.loadCsvCodizionamentoFuturo(edificio);
// }
// catch (NumberFormatException e1) {
// // TODO Auto-generated catch block
// e1.printStackTrace();
// }
// catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// //PASSO 5 : CARICO I DATI DA MATERIALS
// //TODO controllare che material abbia mantenuto i parametri
// try {
// loadData.loadCsvMaterials(material, edificio);
// }
// catch (NumberFormatException e1) {
// // TODO Auto-generated catch block
// e1.printStackTrace();
// }
// catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// double[][] A = { // crea matrice A con dimensione 3x3
// { 1, -0.04 }, // riga 0 di A (array di 3 double)
// { 0, 1 }, // riga 1 di A (array di 3 double)
// };
//
// double[][] D = { // crea matrice A con dimensione 3x3
// { 1, -0.13 }, // riga 0 di A (array di 3 double)
// { 0, 1 }, // riga 1 di A (array di 3 double)
// };
// thick = material.lambda_material / edificio.uwall;
// double delta = Math.sqrt((material.lambda_material * 86400) / (Math.PI * material.ro * material.c));
// double eps = thick / delta;
// double z11r = Math.cosh(eps) * Math.cos(eps);
// double z11im = Math.sinh(eps) * Math.sin(eps);
// double z12r = -(delta / (2 * material.lambda_material))
// * (Math.sinh(eps) * Math.cos(eps) + Math.cosh(eps) * Math.sin(eps));
// double z12im = -(delta / (2 * material.lambda_material))
// * (Math.cosh(eps) * Math.sin(eps) - Math.sinh(eps) * Math.cos(eps));
// double z21r = -(material.lambda_material / delta)
// * (Math.sinh(eps) * Math.cos(eps) - Math.cosh(eps) * Math.sin(eps));
// double z21im = -(material.lambda_material / delta)
// * (Math.sinh(eps) * Math.cos(eps) + Math.cosh(eps) * Math.sin(eps));
// double z22r = z11r;
// double z22im = z11im;
// // double z12r_k = z12r;
// // double z12im_k = z12im;
// // double z11r_k = z11r - 1;
// // double z11im_k = z11im;
//
// // double k = (3600 * 12 / Math.PI)
// // * (Math.sqrt(Math.pow(
// // ((z11r_k * z12r_k + z11im_k * z12im_k) / (((Math.pow(z12r, 2)) + (Math.pow(z12r, 2))))), 2)
// // + Math.pow(
// // ((z11r_k * z12im_k + z12r_k * z11im_k) / ((Math.pow(z12r, 2)) + (Math.pow(z12r, 2)))),
// // 2)));
//
// double[][] B = { { z11r, z12r }, { z21r, z22r }, };
//
// double[][] BB = { { z11im, z12im }, { z21im, z22im }, };
//
// double[][] C = prodottoMatrici(A, B);
// double[][] CC = prodottoMatrici(A, BB);
// double[][] E = prodottoMatrici1(C, D);
// double[][] EE = prodottoMatrici1(CC, D);
// double y11 = Math.sqrt((Math.pow(E[0][0], 2) + Math.pow(EE[0][0], 2))
// / (Math.pow(E[0][1], 2) + Math.pow(EE[0][1], 2)));
// double decr_factor = (1 / (Math.sqrt((Math.pow(E[0][1], 2) + Math.pow(EE[0][1], 2))))) / edificio.uwall;
// //System.out.println(" decr_factor = " + decr_factor );
// double fire = -(E[0][1]);
// double fimm = -(EE[0][1]);
// if (fimm == 0) {
// fi = 0;
// }
// else if (fire > 0 && fimm > 0) {
// fi = ((12 / Math.PI) * (Math.atan(fimm / fire)));
// }
// else if (fire == 0 && fimm > 0) {
// fi = 0.5 * Math.PI;
// }
// else if (fire < 0 && fimm > 0) {
// fi = ((12 / Math.PI) * (Math.atan(fimm / fire) + Math.PI));
// }
// else if (fire > 0 && fimm < 0) {
// fi = ((12 / Math.PI) * (Math.atan(fimm / fire) + 2 * Math.PI));
// }
// else if (fire == 0 && fimm < 0) {
// fi = ((12 / Math.PI) * (Math.atan(fimm / fire) + 1.5 * Math.PI));
// }
// else if (fire < 0 && fimm < 0) {
// fi = ((12 / Math.PI) * (Math.atan(fimm / fire) + Math.PI));
// }
// else if (fire < 0 && fimm < 0) {
// fi = ((12 / Math.PI) * (Math.atan(fimm / fire) + Math.PI));
// }
//
// edificio.tau = (int) ((fi * edificio.awall) / (edificio.awall + edificio.awindows));
// //System.out.println("f (h)= " + fi);
//
// edificio.adme = y11;
// //System.out.println("admittance= " + y11);
// //System.out.println("material.lambda_material = " + material.lambda_material);
// //System.out.println("thick= "+ thick);
// //System.out.println("edificio.awall = " + edificio.awall);
// //System.out.println("material.ro= " + material.ro);
// //System.out.println("material.c= "+ material.c);
// vent = 0.5;
// h_v = edificio.volume * 0.34 * vent;
//
// double qint = 0.7 * edificio.aroof * 8 * floor;
//
// //PASSO 6 : CARICO I DATI DELLA TEMPERATURA
// Map<String, Double> mapTemp = null;
// try {
// mapTemp = loadData.loadCsvTemperature(climate, edificio, R2, avetext, p_future, beta0, beta1, timeon_int);
// }
// catch (NumberFormatException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// catch (IOException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
//
// avetext = mapTemp.get("avetext");
// R2 = mapTemp.get("R2");
//
// //System.out.println("ht= " + h_t + " ; " + "hv= " + h_v+ " ; " + "qint =" + qint);
// double qAves = 0.6 * 0.7 * .9 * 0.75 * (edificio.awindows / 4) * (qavesolfuture);
//
// // System.out.println("qAves= "+qAves);
// //mean casual gains
// double qMeanTot = qAves + qint;
// // System.out.println("qMeanTot = "+qMeanTot);
// //internal environmental temperature
// double tetailast[] = new double[24];
// System.out.println("avetext = " + avetext);
// double tetai = avetext + (qMeanTot) / (h_t + h_v);
// System.out.println("tetai = " + tetai);
// double effSolHeat[] = new double[24];
// double strutGain[] = new double[24];
// double gainAir[] = new double[24];
// System.out.println();
// // System.out.println("edificio.tau= "+edificio.tau);
// //Ciclo per calcolo temperatura nel periodo di accensione compreso il contributo dinamico
// for (i = 0; i <= 23; i++) {
// //swing in effective solar heat gain
// effSolHeat[i] = 0.6 * 0.7 * .9 * 0.75 * (edificio.awindows / 4) * (climate.qsolfuture[i] - qavesolfuture);
// // System.out.println("effSolHeat["+i+"]= "+effSolHeat[i]);
// //struttural gain
//
// if (i >= edificio.tau) {
// strutGain[i] = decr_factor * h_t * (climate.tempextfuture[i - edificio.tau] - avetext);
// }
// else {
// strutGain[i] = decr_factor * h_t * (climate.tempextfuture[i] - avetext);
// }
//
// //swing in gain, air-to-air
// gainAir[i] = (edificio.uwindow * edificio.awindows + h_v) * (climate.tempextfuture[i] - avetext);
//
// //contributo calore interno
// if (i < timeon_int) {
// casual_gain[i] = (0 - qint);
// }
// else {
// casual_gain[i] = 0;
// }
//
// // System.out.println("casual_gain["+i+"]= "+ casual_gain[i]);
// qtotfuture[i] = (effSolHeat[i] + strutGain[i] + gainAir[i] + casual_gain[i]);
//
// // System.out.println("qtotfuture["+i+"] = " + qtotfuture[i]);
// //peak internal environmental temperature
// tetailast[i] = tetai + qtotfuture[i] / (h_v + edificio.awall * edificio.adme);
//
// System.out.println("tetai [" + i + "] = " + tetailast[i]);
// }
//
// int ultimoIndiceNone = 0;
// int ultimoIndiceHeat = 0;
// int modality[] = new int[24];
//
// if (cooling == 0) {
// for (i = 1; i <= 23; i++) {
// int t = 1;
// //calcolo tempo di accensione e controllo
// if (i >= edificio.tau) {
// t = i - edificio.tau;
// }
// else {
// t = 1;
// }
// double q1 = edificio.uwall * (tetailast[i] - climate.tempextfuture[i]);
// double t1 = tetailast[i] - q1 / 8;
// double q2 = edificio.uwall * (edificio.tintfut[timeon_int] - climate.tempextfuture[i]);
// double t2 = edificio.tintfut[timeon_int] - q2 / 8;
// // double dt = t2 - t1;
// // System.out.println("dt= "+dt);
//
// if (p_future[i] > 0) {
// if (edificio.system == 0) {
// time[i] = ((((edificio.volume * 1.29 * 1005 * (edificio.tintfut[timeon_int] - tetailast[i]))) / ((0.7 * p_future[i])
// * 10.35 * 1000)));
// }
// else {
// time[i] = ((((edificio.volume * 1.29 * 1005 * (edificio.tintfut[timeon_int] - tetailast[i]) + 3600
// * (h_t)
// * (((edificio.tintfut[timeon_int] + tetailast[i]) / 2) - climate.tempextfuture[i])) / ((0.7 * p_future[i]) *
// 1000))));
//
// }
//
// time_h[i] = (time[i]) / 3600;
// double time_hint = Math.ceil(time_h[i]);
//
// if (time[i] < 0) {
// time_h[i] = 0;
// }
//
// // time_h[i]= time[i]/3600;
// //System.out.println(" time["+i+"]= "+ time_h[i]);
// if ((time_hint + i) == (timeon_int)) {
//
// double time = (timeon - time_h[i]);
//
// // System.out.println(" ");
// System.out.println();
// ora = (int) time;
// sec = (time - ora) * 60 / 100;
// time = ora + sec;
// time = new BigDecimal(time).setScale(2, BigDecimal.ROUND_UP).doubleValue();
// System.out.println("Turn on at " + (time));
//
// break;
// }
// if ((time_hint + i) > (timeon_int)) {
//
// // double time = (i - time_h[i]);
// }
// }
// }
//
// //controllo per suggestion durante il giorno a seconda del tipo di termoregolazione
// if (edificio.type == 0) {
// for (i = (timeon_int); i <= (timeoff_int); i++) {
// if (tetailast[i] >= edificio.tintfut[i]) {
//
// modality[i] = 0;
// ultimoIndiceNone = i;
// }
//
// else if (tetailast[i] < edificio.tintfut[i]) {
//
// modality[i] = 1;
// // System.out.println("== Nessuna variazione == ");
// ultimoIndiceHeat = i;
// }
// //System.out.println( modality[i]) ;
// }
//
// for (i = (timeon_int + 1); i <= (timeoff_int - 1); i++) {
// if (modality[i] == 0) {
// if (modality[i] != modality[i - 1] && modality[i] == modality[i + 1]) {
//
// if (ultimoIndiceNone == timeoff_int - 1) {
// System.out.println(" == Alle " + i + " non è necessario il riscaldamento == ");
// break;
// }
//
// else if (ultimoIndiceNone != timeoff_int - 1) {
// System.out.println("== Dalle " + i + " alle " + ultimoIndiceNone
// + "  sono sufficienti 17 °C == ");
// System.out.println("== Alle " + (ultimoIndiceNone + 1) + "  sono necessari "
// + edificio.tintfut[ultimoIndiceHeat] + " °C == ");
// }
// }
// }
// }
// }
// else if (edificio.type == 1) {
// for (i = (timeon_int); i <= (timeoff_int - 1); i++) {
//
// //Suggestion
// if (tetailast[i] >= edificio.tintfut[i]) {
// if (i == timeon_int) {
// System.out.println(" == Alle " + i + " non è necessario il riscaldamento == ");
// }
// modality[i] = 0;
// ultimoIndiceNone = i;
// }
//
// else if (tetailast[i] < edificio.tintfut[i]) {
//
// modality[i] = 1;
// ultimoIndiceHeat = i;
// }
//
// System.out.println("Work system at [" + i + "]= " + modality[i]);
// }
// for (i = (timeon_int + 1); i <= (timeoff_int - 1); i++) {
// if (modality[i] == 0) {
// if (modality[i] != modality[i - 1] && modality[i] == modality[i + 1]) {
//
// if (ultimoIndiceNone == timeoff_int - 1) {
// System.out.println(" == Alle " + i + " non è necessario il riscaldamento == ");
// break;
// }
//
// else if (ultimoIndiceNone != timeoff_int - 1) {
// System.out.println(" == Alle " + i + " non è necessario il riscaldamento == ");
// }
// }
// }
// else if (modality[i] == 1) {
// if (modality[i] != modality[i - 1] && modality[i] == modality[i + 1]
// && modality[i - 1] == modality[i - 2]) {
//
// double time = (i - time_h[i]);
// ora = (int) time;
// sec = (time - ora) * 60 / 100;
// time = ora + sec;
// timeon_int = (int) Math.round(timeon);
// time = new BigDecimal(time).setScale(2, BigDecimal.ROUND_UP).doubleValue();
// System.out
// .println("== Alle " + time + " sono necessari " + edificio.tintfut[i] + " °C == ");
// break;
// }
// }
// }
// }
// else if (edificio.type == 2) {
// {
// System.out.println("== Suggestion == ");
// }
// }
// }
// else {
//
// //------------------------------
//
// //controllo per suggestion durante il giorno a seconda del tipo di termoregolazione
// for (i = (timeon_int); i <= (timeoff_int); i++) {
// if (tetailast[i] <= edificio.tintfut[i]) {
//
// modality[i] = 0;
// ultimoIndiceNone = i;
//
// }
//
// else if (tetailast[i] > edificio.tintfut[i]) {
//
// modality[i] = 1;
// // System.out.println("== Nessuna variazione == ");
// ultimoIndiceHeat = i;
// }
// //System.out.println( modality[i]) ;
// }
//
// for (i = (timeon_int + 1); i <= (timeoff_int - 1); i++) {
// if (modality[i] == 0) {
//
// if (ultimoIndiceNone == timeoff_int) {
// System.out.println(" == Alle " + i + "non è necessario il raffrescamento == ");
// break;
// }
// }
// }
//
// //---------------------
// }
//
// return false;
// }
//}
