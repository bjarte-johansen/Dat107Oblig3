package main;

import java.util.List;

import Entities.Ansatt;
import Entities.AnsattProsjektPivot;
import Entities.Prosjekt;
import jakarta.persistence.NoResultException;

public class ProsjektDAO {
	/*
	 * find entity by custom attribute
	 */
		 
	public static <T> Prosjekt findOneByColumnEquals(String key, T value) {
		var em = StaticEMF.getNewEM(); 
	    try {
	    	Prosjekt item = em.createQuery(
	            "SELECT i FROM Prosjekt i WHERE i." + key + " = :value", Prosjekt.class)
	            .setParameter("value", value)
	            .getSingleResult();
	        em.close();
	        return item;
	    } catch (NoResultException e) {
	    	em.close();
	        return null;
	    }
	}
	
	
	public static Prosjekt findById(int id) {
		return findOneByColumnEquals("id", id);
	}
	
	public static List<AnsattProsjektPivot> findParticipants(int prosjektId){
		var em = StaticEMF.getNewEM();
		var items = em
				.createQuery("SELECT i FROM AnsattProsjektPivot i WHERE i.prosjekt.id = :prosjekt", AnsattProsjektPivot.class)
				.setParameter("prosjekt", prosjektId)
				.getResultList();		
		em.close();
		
		return items;
	};	
}
