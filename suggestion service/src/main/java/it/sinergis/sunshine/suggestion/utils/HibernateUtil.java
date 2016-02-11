package it.sinergis.sunshine.suggestion.utils;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	
	private static final SessionFactory sessionFactory = buildSessionFactory();
	private static final SessionFactory sessionFactoryMap4data = buildSessionFactoryMap4data("public");
	private static Map<String, SessionFactory> mapSession;
	
	private static SessionFactory buildSessionFactory() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			return new Configuration().configure().buildSessionFactory();
		}
		catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation SOS failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	private static SessionFactory buildSessionFactoryMap4data(String pilot) {
		try {
			if (mapSession == null) {
				mapSession = new HashMap<String, SessionFactory>();
			}
			switch (pilot) {
				case "ferrara":
					if (mapSession.get(pilot) == null) {
						mapSession.put(pilot, new Configuration().configure("hibernateMap4data.cfg.xml")
								.buildSessionFactory());
					}
					return mapSession.get(pilot);
				case "lamia":
					if (mapSession.get(pilot) == null) {
						mapSession.put(pilot, new Configuration().configure("hibernateMap4dataLamia.cfg.xml")
								.buildSessionFactory());
					}
					return mapSession.get(pilot);
				case "trento":
					
					if (mapSession.get(pilot) == null) {
						mapSession.put(pilot, new Configuration().configure("hibernateMap4dataTrento.cfg.xml")
								.buildSessionFactory());
					}
					return mapSession.get(pilot);
				case "croatia":
					
					if (mapSession.get(pilot) == null) {
						mapSession.put(pilot, new Configuration().configure("hibernateMap4dataCroatia.cfg.xml")
								.buildSessionFactory());
					}
					return mapSession.get(pilot);
				case "cles":
					
					if (mapSession.get(pilot) == null) {
						mapSession.put(pilot, new Configuration().configure("hibernateMap4dataCles.cfg.xml")
								.buildSessionFactory());
					}
					return mapSession.get(pilot);
					
				default:
					
					if (mapSession.get(pilot) == null) {
						mapSession.put(pilot, new Configuration().configure("hibernateMap4data.cfg.xml")
								.buildSessionFactory());
					}
					return mapSession.get(pilot);
			}
			
		}
		catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation Map4data failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static SessionFactory getSessionFactoryMap4data() {
		return sessionFactoryMap4data;
	}
	
	public static SessionFactory getSessionFactoryMap4data(String pilot) {
		return buildSessionFactoryMap4data(pilot);
	}
	
}
