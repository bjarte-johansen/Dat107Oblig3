package DAO;

import java.util.Arrays;
import java.util.stream.Collectors;

import Entities.Ansatt;
import Entities.AnsattProsjektPivot;
import jakarta.persistence.NoResultException;
import main.StaticEMF;





public class AnsattProsjektPivotDAO {
	/*
	 * find entity by custom attribute
	 */
		 
	public static <T> AnsattProsjektPivot findOneByAnsattIdAndProjectId(Integer ansattId, Integer prosjektId) {		
		var em = StaticEMF.getNewEM(); 
	    try {
	        var item = em.createQuery(
	            "SELECT o FROM AnsattProsjektPivot o WHERE ((o.ansatt.id = :ansattId) AND (o.prosjekt.id = :prosjektId))", AnsattProsjektPivot.class)
	            .setParameter("ansattId", ansattId)
	            .setParameter("prosjektId", prosjektId)
	            .setMaxResults(1)
	            .getSingleResult();
	        em.close();
	        return item;
	    } catch (NoResultException e) {
	    	em.close();
	        return null;
	    }
	}
}
