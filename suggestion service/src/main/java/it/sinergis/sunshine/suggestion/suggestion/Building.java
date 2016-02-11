package it.sinergis.sunshine.suggestion.suggestion;

public class Building {
	public int id_edificio;
	public double volume; //volume
	public double aroof; //area pianta (tetto)
	public double awall; //area muri 
	public double uwall; // u-value muri
	public double uroof; // u-value tetto
	public double ufloor; // u-value pavimento
	public double uwindow;// u-value finestre
	public double awindows; //area finestre
	public int tau; //costante di tempo
	public static String id_material;
	public double adme; //ammettenza
	public double tintfut[] = new double[24]; // temperatura futura
	public double qpast[] = new double[1000];
	public double tint; //temperatura interna
	public int type; //tipo di sistema di condizionamento -> 0 fixed point ; 	1 climatic; 	2 teleheating;
	public int system; //tipo di vettore energetico ( 0 = m^3; 1 = KWh )
	
	public static String createCsv() {
		return null;
		
	}
}
