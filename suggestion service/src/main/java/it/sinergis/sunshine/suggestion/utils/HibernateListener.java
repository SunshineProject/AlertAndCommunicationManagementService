package it.sinergis.sunshine.suggestion.utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class HibernateListener implements ServletContextListener {
	
	public void contextInitialized(ServletContextEvent event) {
		HibernateUtil.getSessionFactory(); // Just call the static initializer of that class      
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		HibernateUtil.getSessionFactory().close(); // Free all resources  
	}
}