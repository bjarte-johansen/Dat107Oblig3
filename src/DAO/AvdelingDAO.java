package DAO;

import Entities.Avdeling;
import jakarta.persistence.NoResultException;
import main.StaticEMF;

public class AvdelingDAO{
	/*
	 * find entity by custom attribute
	 */
	
	public static <T> Avdeling findOneByColumnEquals(String key, T value) {
		var em = StaticEMF.getNewEM(); 
	    try {
	    	Avdeling item = em.createQuery(
	            "SELECT i FROM Avdeling i WHERE i." + key + " = :value", Avdeling.class)
	            .setParameter("value", value)
	            .getSingleResult();
	        em.close();
	        return item;
	    } catch (NoResultException e) {
	    	em.close();
	        return null;
	    }
	}
	
	public static Avdeling findById(int id) {
		return findOneByColumnEquals("id", id);
	}

	public static Avdeling findByLeaderId(int id) {
		return findOneByColumnEquals("leder.id", id);
	}
}