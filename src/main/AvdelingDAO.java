package main;

import Entities.Avdeling;
import jakarta.persistence.NoResultException;

public class AvdelingDAO{
	public static <T> Avdeling findByColumnEquals(String key, T value) {
		var em = EMF.getEM(); 
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
		return findByColumnEquals("id", id);
	}	
}