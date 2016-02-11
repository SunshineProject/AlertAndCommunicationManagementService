package it.sinergis.sunshine.suggestion.timertask;

import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class SchedulerMain implements ServletContextListener {
	static final Logger LOGGER = Logger.getLogger(SchedulerMain.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ServletContextListener destroyed");
	}
	
	//Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		LOGGER.info("ServletContextListener started");
		try {
			ServletContext ctx = arg0.getServletContext();
			int hoursTask = Integer.parseInt(ctx.getInitParameter("hoursTask"));
			int minutesTask = Integer.parseInt(ctx.getInitParameter("minutesTask"));
			int secondsTask = Integer.parseInt(ctx.getInitParameter("secondsTask"));
			LOGGER.info("Task schedule at " + hoursTask + ":" + minutesTask + ":" + secondsTask);
			runTask(hoursTask, minutesTask, secondsTask);
		}
		catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}
	
	private void runTask(int hoursTask, int minutesTask, int secondsTask) throws InterruptedException {
		LOGGER.info("runTask: setup date");
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, hoursTask);
		today.set(Calendar.MINUTE, minutesTask);
		today.set(Calendar.SECOND, secondsTask);
		
		Timer timer = new Timer();
		ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
		timer.schedule(st, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
		//aggiungo 10 minuti per la schedulazione degli shelter
		//		Calendar todayAdd10mm = today;
		//		todayAdd10mm.add(Calendar.MINUTE, 5);
		//		ScheduledTaskShelter stSchelter = new ScheduledTaskShelter(); // Instantiate SheduledTask class
		//		timer.schedule(stSchelter, todayAdd10mm.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
		//		timer.schedule(st, 0, 1000); // Create Repetitively task for every 1 secs
	}
}