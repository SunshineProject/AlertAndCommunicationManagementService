package it.sinergis.sunshine.suggestion.delegate;

import it.sinergis.sunshine.suggestion.map4datapojo.BuildingMap4;
import it.sinergis.sunshine.suggestion.map4datapojo.Buildingmanager;
import it.sinergis.sunshine.suggestion.map4datapojo.Calendar;
import it.sinergis.sunshine.suggestion.map4datapojo.Calendarshelter;
import it.sinergis.sunshine.suggestion.map4datapojo.ClimaticZones;
import it.sinergis.sunshine.suggestion.map4datapojo.Energyamount;
import it.sinergis.sunshine.suggestion.map4datapojo.Heatingsystem;
import it.sinergis.sunshine.suggestion.map4datapojo.Officialarea;
import it.sinergis.sunshine.suggestion.map4datapojo.ShelterMap4;
import it.sinergis.sunshine.suggestion.map4datapojo.Thermalzone;
import it.sinergis.sunshine.suggestion.utils.HibernateUtil;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class Delegatedatabasemap4data {
	private static final Logger LOGGER = Logger.getLogger(Delegatedatabasemap4data.class);
	
	public void readparameter(String pilot) {
		Session session = HibernateUtil.getSessionFactoryMap4data(pilot).getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(BuildingMap4.class);
		criteria.add(Restrictions.eq("gid", 15352));
		List<?> result = criteria.list();
		List<BuildingMap4> resultCtrl = (List<BuildingMap4>) result;
		session.close();
	}
	
	public final BuildingMap4 readParameterBuilding(int idBuilding, Session session, Transaction tx, String pilot) {
		//var di cui ho bisogno
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
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data(pilot).getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(BuildingMap4.class);
		criteria.add(Restrictions.eq("gid", idBuilding));
		List<BuildingMap4> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result.get(0);
	}
	
	public final BuildingMap4 readParameterBuilding(long foiid, Session session, Transaction tx, String pilot) {
		//var di cui ho bisogno
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
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data(pilot).getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(BuildingMap4.class);
		criteria.add(Restrictions.eq("foiid", foiid));
		List<BuildingMap4> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result.get(0);
	}
	
	public final Officialarea readParameterOfficialArea(int idBuilding, Session session, Transaction tx, String pilot) {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data(pilot).getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Officialarea.class);
		criteria.add(Restrictions.eq("idBuilding", idBuilding));
		List<Officialarea> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result.get(0);
	}
	
	public final Thermalzone readParameterThermalZone(int idBuilding, Session session, Transaction tx, String pilot) {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data(pilot).getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Thermalzone.class);
		criteria.add(Restrictions.eq("idBuilding", idBuilding));
		List<Thermalzone> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result.get(0);
	}
	
	public final Calendar readParameterCalendar(int idBuilding, Date day, String pilot, Session session, Transaction tx)
			throws ParseException {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data(pilot).getCurrentSession();
			session.beginTransaction();
		}
		String belowlimit = null;
		String upperlimit = null;
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		belowlimit = outputFormatter.format(day);
		
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(outputFormatter.parse(belowlimit));
		cal.add(java.util.Calendar.DATE, 1);
		upperlimit = outputFormatter.format(cal.getTime());
		Criteria criteria = session.createCriteria(Calendar.class);
		criteria.add(Restrictions.eq("buildingid", (long) idBuilding))
				.add(Restrictions.eq("pilot", pilot))
				.add(Restrictions.between("day", outputFormatter.parseObject(belowlimit),
						outputFormatter.parseObject(upperlimit)));
		List<Calendar> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result.get(0);
	}
	
	public final List<Calendar> readParameterCalendar(long idBuilding, String pilot, Session session, Transaction tx)
			throws ParseException {
		
		if (session == null) {
			//e' cablato ferrara perche' la tabella calendar e' unica
			session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Calendar.class);
		criteria.add(Restrictions.eq("buildingid", idBuilding)).add(Restrictions.eq("pilot", pilot));
		List<Calendar> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result;
	}
	
	public final Calendar readParameterCalendarFromId(int idProfile, String pilot, Session session, Transaction tx)
			throws ParseException {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Calendar.class);
		criteria.add(Restrictions.eq("calendarid", idProfile)).add(Restrictions.eq("pilot", pilot));
		List<Calendar> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result.get(0);
	}
	
	public final List<Calendar> readParameterCalendar(Date day, int offset, Session session, Transaction tx)
			throws ParseException {
		
		//per la sessione metto il pilot di ferrara tanto per la tabella calendar non fa importanza
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
			session.beginTransaction();
		}
		String belowlimit = null;
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		belowlimit = outputFormatter.format(day);
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTime(outputFormatter.parse(belowlimit));
		//aggiungo o tolgo offset giornate
		cal.add(java.util.Calendar.DATE, offset);
		belowlimit = outputFormatter.format(cal.getTime());
		Criteria criteria = session.createCriteria(Calendar.class);
		criteria.add(Restrictions.eq("day", outputFormatter.parseObject(belowlimit)));
		List<Calendar> result = criteria.list();
		if (result.isEmpty()) {
			//			criteria = session.createCriteria(Calendar.class);
			//			criteria.addOrder(Order.desc("day")).setMaxResults(1);
			//			result = criteria.list();
			session.close();
			return null;
		}
		
		session.close();
		return result;
	}
	
	public final List<Buildingmanager> readParameterBuildingmanager(long idBuilding, Session session, Transaction tx,
			String pilot) {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Buildingmanager.class);
		criteria.add(Restrictions.eq("buildingid", idBuilding)).add(Restrictions.eq("pilot", pilot));
		List<Buildingmanager> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result;
	}
	
	public final List<Heatingsystem> readParameterHeatingsystemr(int idThermalzone, Session session, Transaction tx,
			String pilot) {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data(pilot).getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Heatingsystem.class);
		criteria.add(Restrictions.eq("idThermalzone", idThermalzone));
		List<Heatingsystem> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result;
	}
	
	public final ClimaticZones readParameterClimaticZones(int climaticZoneid, Session session, Transaction tx,
			String pilot) {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data(pilot).getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(ClimaticZones.class);
		criteria.add(Restrictions.eq("id", climaticZoneid));
		List<ClimaticZones> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result.get(0);
	}
	
	public final Map<String, Double> getThresholds(long idBuilding, String pilot) {
		Map<String, Double> thresholds = new HashMap<String, Double>();
		//mi basta prendere il primo per sapere l'id della climaticzone
		Buildingmanager buildManager = readParameterBuildingmanager(idBuilding, null, null, pilot).get(0);
		ClimaticZones climaticZ = readParameterClimaticZones(buildManager.getClimaticzoneid(), null, null, pilot);
		//ora devo restituire
		thresholds.put("thresholdsTmin", climaticZ.getThresholdTmin());
		thresholds.put("thresholdsTmed", climaticZ.getThresholdTmed());
		thresholds.put("thresholdsTmax", climaticZ.getThresholdTmax());
		return thresholds;
	}
	
	public final Integer save(Calendar table, Transaction tx, Session session) {
		try {
			if (tx == null) {
				session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
				tx = session.beginTransaction();
			}
			session.saveOrUpdate(table);
			if (!tx.wasCommitted()) {
				tx.commit();
			}
		}
		catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			LOGGER.error(e);
			return null;
		}
		return table.getCalendarid();
	}
	
	public final Serializable insert(Calendar table, Transaction tx, Session session) {
		
		Serializable tableId = null;
		try {
			if (tx == null) {
				session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
				tx = session.beginTransaction();
			}
			tableId = checkExistsRecord(table, tx, session);
			if (tableId == null) {
				tableId = session.save(table);
				if (!tx.wasCommitted()) {
					tx.commit();
				}
			}
		}
		catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			LOGGER.error(e);
			return null;
		}
		finally {
			try {
				session.close();
			}
			catch (Exception e) {
				LOGGER.info("Session already close.");
			}
		}
		
		return tableId;
	}
	
	public final boolean delete(int idCalendar, String pilot, Transaction tx, Session session) {
		
		try {
			if (tx == null) {
				session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
				tx = session.beginTransaction();
			}
			Criteria criteria = session.createCriteria(Calendar.class);
			criteria.add(Restrictions.eq("calendarid", idCalendar)).add(Restrictions.eq("pilot", pilot));
			List<Calendar> result = criteria.list();
			if (result.isEmpty()) {
				session.close();
				return false;
			}
			session.delete(result.get(0));
			tx.commit();
		}
		catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			LOGGER.error(e);
			return false;
		}
		
		finally {
			try {
				session.close();
			}
			catch (Exception e) {
				LOGGER.info("Session already close.");
			}
		}
		
		return true;
	}
	
	public final Serializable checkExistsRecord(Calendar table, Transaction tx, Session session) {
		// da controllare, buildingid, day e pilot
		Criteria criteria = session.createCriteria(Calendar.class);
		criteria.add(Restrictions.eq("buildingid", table.getBuildingid())).add(Restrictions.eq("day", table.getDay()))
				.add(Restrictions.eq("pilot", table.getPilot()));
		List<Calendar> result = criteria.list();
		if (!result.isEmpty()) {
			return result.get(0).getCalendarid();
		}
		else
			return null;
	}
	
	public final ShelterMap4 readParameterShelter(int idShelter, Session session, Transaction tx, String pilot) {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data(pilot).getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(ShelterMap4.class);
		criteria.add(Restrictions.eq("id", idShelter)).add(Restrictions.eq("pilot", pilot));
		List<ShelterMap4> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result.get(0);
	}
	
	public final List<ShelterMap4> getShelter(Session session, Transaction tx) {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(ShelterMap4.class);
		List<ShelterMap4> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result;
	}
	
	public final Calendarshelter readParameterCalendarShelterFromId(long id, String pilot, Session session,
			Transaction tx, Date mese) throws ParseException {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Calendarshelter.class);
		criteria.add(Restrictions.eq("calendarshelterid", id));
		// incongruenza tra mese e giorno deriva dal fatto che esiste un solo record per mese per un determinato shelter, ma in fase di inserimento db il giorno e' valorizzato a 1
		if (mese != null) {
			criteria.add(Restrictions.eq("day", mese));
		}
		List<Calendarshelter> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		Criteria criteriaShelter = session.createCriteria(ShelterMap4.class);
		criteriaShelter.add(Restrictions.eq("pilot", pilot));
		List<ShelterMap4> resultShelter = criteriaShelter.list();
		ArrayList<Integer> idShelter = new ArrayList<Integer>();
		for (ShelterMap4 cal : resultShelter) {
			idShelter.add(cal.getId());
		}
		session.close();
		if (idShelter.contains(result.get(0).getShelterid())) {
			return result.get(0);
		}
		return null;
		
	}
	
	public final Energyamount readParameterEnergyFromThermalZoneid(int idThermal, String pilot, Session session,
			Transaction tx) {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data(pilot).getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(Energyamount.class);
		criteria.add(Restrictions.eq("idThermalzone", idThermal));
		List<Energyamount> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return result.get(0);
		
	}
	
	public final List<Calendarshelter> readParameterCalendarShelterFromName(String foiUri, String pilot,
			Session session, Transaction tx) throws ParseException {
		
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
			session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(ShelterMap4.class);
		criteria.add(Restrictions.eq("urifoi", foiUri)).add(Restrictions.eq("pilot", pilot));
		List<ShelterMap4> result = criteria.list();
		if (result.isEmpty()) {
			session.close();
			return null;
		}
		Criteria criteriaCalendar = session.createCriteria(Calendarshelter.class);
		criteriaCalendar.add(Restrictions.eq("shelterid", result.get(0).getId()));
		List<Calendarshelter> resultCal = criteriaCalendar.list();
		if (resultCal.isEmpty()) {
			session.close();
			return null;
		}
		session.close();
		return resultCal;
	}
	
	public final Calendarshelter readParameterCalendarShelterFromDate(String foiUri, String pilot, Date day,
			Session session, Transaction tx) throws ParseException {
		
		//per la sessione metto il pilot di ferrara tanto per la tabella calendar non fa importanza
		if (session == null) {
			session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
			session.beginTransaction();
		}
		String belowlimit = null;
		DateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
		belowlimit = outputFormatter.format(day);
		
		Criteria criteriaShelter = session.createCriteria(ShelterMap4.class);
		criteriaShelter.add(Restrictions.eq("pilot", pilot)).add(Restrictions.eq("urifoi", foiUri));
		List<ShelterMap4> resultShelter = criteriaShelter.list();
		if (resultShelter.isEmpty()) {
			session.close();
			return null;
		}
		int idShelterMap = resultShelter.get(0).getId();
		
		Criteria criteria = session.createCriteria(Calendarshelter.class);
		criteria.add(Restrictions.eq("day", outputFormatter.parseObject(belowlimit))).add(
				Restrictions.eq("shelterid", idShelterMap));
		List<Calendarshelter> result = criteria.list();
		if (result.isEmpty()) {
			//			criteria = session.createCriteria(Calendar.class);
			//			criteria.addOrder(Order.desc("day")).setMaxResults(1);
			//			result = criteria.list();
			session.close();
			return null;
		}
		
		session.close();
		return result.get(0);
	}
	
	public final Long save(Calendarshelter table, Transaction tx, Session session) {
		try {
			if (tx == null) {
				session = HibernateUtil.getSessionFactoryMap4data("ferrara").getCurrentSession();
				tx = session.beginTransaction();
			}
			session.saveOrUpdate(table);
			if (!tx.wasCommitted()) {
				tx.commit();
			}
		}
		catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			LOGGER.error(e);
			return null;
		}
		return table.getCalendarshelterid();
	}
	
}
