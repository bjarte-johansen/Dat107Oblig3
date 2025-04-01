package DAO;

import java.util.List;

import Entities.Ansatt;
import jakarta.persistence.NoResultException;
import main.StaticEMF;

public class AnsattDAO{
	/*
	 * find entity by custom attribute
	 */
		 
	public static <T> Ansatt findOneByColumnEquals(String key, T value) {
		var em = StaticEMF.getNewEM(); 
	    try {
	        Ansatt item = em.createQuery(
	            "SELECT i FROM Ansatt i WHERE i." + key + " = :value", Ansatt.class)
	            .setParameter("value", value)
	            .getSingleResult();
	        em.close();
	        return item;
	    } catch (NoResultException e) {
	    	em.close();
	        return null;
	    }
	}
	
	
	/*
	 * find first
	 */
	
	public static <T> Ansatt findOne() {
		var em = StaticEMF.getNewEM(); 
	    try {
	    	var query = em.createQuery("SELECT i FROM Ansatt i", Ansatt.class);
	    	query.setMaxResults(1);
	    	Ansatt item = query.getSingleResult();
	        return item;
	    } catch (NoResultException e) {
	        return null;
	    } finally {
	    	em.close();	    	
	    }
	}
	
	
	
	/*
	 * 
	 */
	
	public static Ansatt findById(int id) {
		return findOneByColumnEquals("id", id);
	}

	public static Ansatt findByBrukernavn(String brukernavn) {
		return findOneByColumnEquals("brukernavn", brukernavn);
	}
	
	public static List<Ansatt> findByAvdelingId(int id) {
		var em = StaticEMF.getNewEM();
		var query = em.createQuery("SELECT i FROM Ansatt i WHERE i.avdeling.id = :id", Ansatt.class);
		query.setParameter("id", id);
		var item = query.getResultList();
		em.close();
		return item;
	}
}
