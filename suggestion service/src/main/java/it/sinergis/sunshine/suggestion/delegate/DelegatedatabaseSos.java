package it.sinergis.sunshine.suggestion.delegate;

import it.sinergis.sunshine.suggestion.sospojo.Featureofinterest;
import it.sinergis.sunshine.suggestion.sospojo.Numericvalue;
import it.sinergis.sunshine.suggestion.sospojo.Observation;
import it.sinergis.sunshine.suggestion.sospojo.Observationhasoffering;
import it.sinergis.sunshine.suggestion.sospojo.TaskStatus;
import it.sinergis.sunshine.suggestion.utils.HibernateUtil;
import it.sinergis.sunshine.suggestion.utils.ReadFromConfig;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class DelegatedatabaseSos {
	static final Logger LOGGER = Logger.getLogger(DelegatedatabaseSos.class);
	private static final ThreadLocal<Session> sessionThread = new ThreadLocal<Session>();
	
	public final List<Observation> readParameterObservation(String identifier, int offsetDay, int offsetDayPeriod,
			Session session, Transaction tx) throws ParseException {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Observation.class);
		//criteria.add(Restrictions.eq("identifier", identifier));
		String belowlimit = null;
		String upperlimit = null;
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat outputFormatterHour = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		belowlimit = outputFormatter.format(new Date());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(outputFormatter.parse(belowlimit));
		cal.add(Calendar.DATE, offsetDay);
		belowlimit = outputFormatter.format(cal.getTime());
		cal.add(Calendar.DATE, offsetDayPeriod);
		cal.add(Calendar.MINUTE, -1);
		upperlimit = outputFormatterHour.format(cal.getTime());
		
		criteria.add(Restrictions.like("identifier", identifier, MatchMode.ANYWHERE)).add(
				Restrictions.between("phenomenontimestart", outputFormatter.parseObject(belowlimit),
						outputFormatterHour.parseObject(upperlimit)));
		
		List<Observation> result = criteria.list();
		try {
			result.remove(24 * offsetDayPeriod);
		}
		catch (Exception e) {
			LOGGER.info(e);
		}
		session.close();
		return result;
	}
	
	public final boolean delete(List<String> listIdentifier, Date resultime) {
		Transaction tx = null;
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			//criteria.add(Restrictions.eq("identifier", identifier));
			//identifier = identifier.concat("%");
			
			for (String identifier : listIdentifier) {
				Criteria criteria = session.createCriteria(Observation.class);
				criteria.add(Restrictions.conjunction()
						.add(Restrictions.like("identifier", identifier, MatchMode.START))
						.add(Restrictions.lt("resulttime", resultime)));
				
				List<?> result = criteria.list();
				List<Observation> resultOb = (List<Observation>) result;
				for (Observation ob : resultOb) {
					Criteria criteriaNumeric = session.createCriteria(Numericvalue.class);
					criteriaNumeric.add(Restrictions.eq("observationid", ob.getObservationid()));
					List<?> resultNum = criteriaNumeric.list();
					List<Numericvalue> resultNumeric = (List<Numericvalue>) resultNum;
					
					Criteria criteriaObserHasOff = session.createCriteria(Observationhasoffering.class);
					criteriaObserHasOff.add(Restrictions.eq("observationid", ob.getObservationid()));
					List<?> resultObHo = criteriaObserHasOff.list();
					List<Observationhasoffering> resultObHasOff = (List<Observationhasoffering>) resultObHo;
					
					for (Numericvalue numV : resultNumeric) {
						session.delete(numV);
						//						session.flush();
					}
					for (Observationhasoffering obhas : resultObHasOff) {
						session.delete(obhas);
						//						session.flush();
					}
					session.delete(ob);
					//					session.flush();
				}
			}
			session.close();
			closeSession();
		}
		catch (HibernateException e) {
			System.out.println(e);
			if (tx != null)
				tx.rollback();
			LOGGER.error(e);
			return false;
		}
		finally {
			if (!tx.wasCommitted()) {
				
				tx.commit();
			}
		}
		return true;
	}
	
	public final List<Numericvalue> readvaluesNumericValue(List<Observation> obList, Session session, Transaction tx) {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Numericvalue.class);
		//controllare se basta aggiungere con add oppure bisogna usare il verbo OR
		List<Long> obidlist = new ArrayList<Long>();
		for (Observation ob : obList) {
			obidlist.add(ob.getObservationid());
		}
		if (obidlist.isEmpty()) {
			session.close();
			return null;
		}
		criteria.add(Restrictions.in("observationid", obidlist));
		List<Numericvalue> result = criteria.list();
		session.close();
		return result;
	}
	
	public final Featureofinterest readFeatureofInterest(Long foiid, Session session, Transaction tx) {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Featureofinterest.class);
		//controllare se basta aggiungere con add oppure bisogna usare il verbo OR
		criteria.add(Restrictions.eq("featureofinterestid", foiid));
		List<Featureofinterest> result = criteria.list();
		session.close();
		return result.get(0);
	}
	
	public final TaskStatus writeTaskStatusStart(Session session, Transaction tx) {
		LOGGER.info("Save start task");
		if (session == null) {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
		}
		Date startDate = new Date();
		TaskStatus task = new TaskStatus();
		task.setIdProcess(Long.parseLong((ReadFromConfig.loadByName("idProcessTask"))));
		task.setDescription(ReadFromConfig.loadByName("startDescription"));
		task.setStatus(ReadFromConfig.loadByName("startStatus"));
		task.setStartProcess(startDate);
		task.setLastUpdate(startDate);
		Serializable id = session.save(task);
		task.setId((Long) id);
		session.close();
		return task;
	}
	
	public final TaskStatus writeTaskStatusUpdate(Session session, Transaction tx, TaskStatus task, String status,
			String description) {
		LOGGER.info("Save start task");
		if (session == null) {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
		}
		Date updateDate = new Date();
		task.setIdProcess(Long.parseLong((ReadFromConfig.loadByName("idProcessTask"))));
		task.setDescription(ReadFromConfig.loadByName("startDescription"));
		task.setStatus(ReadFromConfig.loadByName("startStatus"));
		task.setLastUpdate(updateDate);
		session.saveOrUpdate(task);
		session.close();
		return task;
	}
	
	public final TaskStatus writeTaskStatusEnd(Session session, Transaction tx, TaskStatus task) {
		LOGGER.info("Save start task");
		if (session == null) {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
		}
		Date endDate = new Date();
		task.setDescription(ReadFromConfig.loadByName("startDescription"));
		task.setStatus(ReadFromConfig.loadByName("endStatus"));
		task.setLastUpdate(endDate);
		task.setEndProcess(endDate);
		session.saveOrUpdate(task);
		session.close();
		return task;
	}
	
	private static void closeSession() throws HibernateException {
		Session s = sessionThread.get();
		if (s != null) {
			s.close();
			sessionThread.remove();
		}
	}
	
	/**
	 * @param identifier
	 *            identificativo per le osservazioni
	 * @param pilot
	 * @param nTot
	 *            n tot di osservazioni da coinvolgere
	 * @param order
	 *            eventuale ordinamento
	 * @param parameterOrder
	 *            parametro su cui ordinare
	 * @return
	 */
	public final double getAvgObservations(String identifier, String pilot, int nTot, String order,
			String parameterOrder) {
		
		return 0.0;
	}
}
