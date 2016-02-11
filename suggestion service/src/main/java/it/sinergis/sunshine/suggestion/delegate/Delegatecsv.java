package it.sinergis.sunshine.suggestion.delegate;

import it.sinergis.sunshine.suggestion.map4datapojo.BuildingMap4;
import it.sinergis.sunshine.suggestion.utils.HibernateUtil;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class Delegatecsv {
	
	public static boolean createFileCsvBuilding(long idBuilding, Session session, Transaction tx) {
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
		
		if (tx == null) {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
		}
		
		Criteria criteria = session.createCriteria(BuildingMap4.class);
		criteria.add(Restrictions.eq("gid", idBuilding));
		List<BuildingMap4> result = criteria.list();
		return false;
	}
}
