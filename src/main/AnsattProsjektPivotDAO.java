package main;

import Entities.Ansatt;
import Entities.AnsattProsjektPivot;
import jakarta.persistence.NoResultException;

/*
class DAOFunctions{
	public class Condition{
		String key;
		Object value;
		String operator;
		
		public Condition(String key, String operator, Object value) {
			this.key = key;
			this.operator = operator;			
			this.value = value;
		}

		public String buildJPQLString(String objectName) {
			objectName = (objectName == null ? "" : objectName + ".");
			
			return "(" + objectName + key + " " + operator + " :" + key + ")";
		}		
		public String toString() {
			return "(" + key + " " + operator + " " + (value == "null" ? "null" : String.valueOf(value)) + ")";
		}		
    }
         
    public static <T> Object findOneByColumnEquals(Condition conditions[], Class<?> clazz) {
        var em = StaticEMF.getNewEM(); 
        try {
        	// create query string
        	String queryStr = "SELECT o FROM " + clazz.getSimpleName() + " o WHERE ";
        	for(int i = 0; i < conditions.length; i++) {
				if (i > 0) {
					queryStr += " AND ";
				}
				
				queryStr += conditions[i].buildJPQLString("o");
        	}

        	// create query and execute it
        	var query = em.createQuery(queryStr, clazz);
			for (int i = 0; i < conditions.length; i++) {
				query.setParameter(conditions[i].key, conditions[i].value);
			}
        	query.setMaxResults(1);
        	var item = query.getSingleResult();
        	
            em.close();
            return item;
        } catch (NoResultException e) {
        	em.close();
            return null;
        }
    }
}
*/

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
	            .getSingleResult();
	        em.close();
	        return item;
	    } catch (NoResultException e) {
	    	em.close();
	        return null;
	    }
	}
}
