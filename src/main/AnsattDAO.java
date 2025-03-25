package main;

import Entities.Ansatt;
import jakarta.persistence.NoResultException;

public class AnsattDAO{
	/*
	 * find entity by custom attribute
	 */
		 
	public static <T> Ansatt findOneByColumnEquals(String key, T value) {
		var em = StaticEMF.getEM(); 
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
	
	public static Ansatt findById(int id) {
		return findOneByColumnEquals("id", id);
	}

	public static Ansatt findByBrukernavn(String brukernavn) {
		return findOneByColumnEquals("brukernavn", brukernavn);
	}
}
