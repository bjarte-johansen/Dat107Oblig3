package DAO;

import java.util.List;

import Entities.Ansatt;
import Entities.AnsattProsjektPivot;
import Entities.Prosjekt;
import jakarta.persistence.NoResultException;
import main.StaticEMF;

public class ProsjektDAO {
	/*
	 * find entity by custom attribute
	 */
		 
	public static <T> Prosjekt findOneByColumnEquals(String key, T value) {
		var em = StaticEMF.getNewEM(); 
	    try {
	    	Prosjekt item = em.createQuery(
	            "SELECT i FROM " + Prosjekt.class.getSimpleName() + " i WHERE  i." + key + " = :value", Prosjekt.class)
	            .setParameter("value", value)
	            .getSingleResult();
	        em.close();
	        return item;
	    } catch (NoResultException e) {
	    	em.close();
	        return null;
	    }
	}
	
	public static <T> List<?> findByColumnEquals(String key, T value, Class<?> clazz) {
		var em = StaticEMF.getNewEM(); 
	    try {
	    	var items = em.createQuery(
	            "SELECT o FROM " + clazz.getSimpleName() + " o WHERE o." + key + " = :value", clazz)
	            .setParameter("value", value)
	            .getResultList();
	        em.close();
	        return items;
	    } catch (NoResultException e) {
	    	em.close();
	        return null;
	    }
	}	
	
	
	public static Prosjekt findById(int id) {
		return findOneByColumnEquals("id", id);
	}
	
	@SuppressWarnings("unchecked")
	public static List<AnsattProsjektPivot> findParticipants(int prosjektId){
		return (List<AnsattProsjektPivot>) findByColumnEquals("prosjekt.id", prosjektId, AnsattProsjektPivot.class);
		/*
		var em = StaticEMF.getNewEM();
		var items = em
				.createQuery("SELECT i FROM AnsattProsjektPivot i WHERE i.prosjekt.id = :prosjekt", AnsattProsjektPivot.class)
				.setParameter("prosjekt", prosjektId)
				.getResultList();		
		em.close();
		return items;		
		*/		
	};	
}
