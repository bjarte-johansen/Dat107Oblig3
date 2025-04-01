package GenericDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import main.StaticEMF;


public class GenericDAO {	
	public static enum Quantity {
		ONE, MANY
	};
	
	public static enum Connective {
		AND, OR
	};
	
	public static class Conditions{
		private Stack<ConditionList> conditionStack;
		private ConditionList currentConditions;
		private Stack<Connective> connectiveStack;
		private Connective currentConnective;

		public Conditions() {
			connectiveStack = new Stack<>();
			conditionStack = new Stack<>();
			
			currentConditions = new ConditionList(Connective.AND);
			currentConnective = Connective.AND;
		}
		
		public void begin(Connective newConnective) {
			conditionStack.push(currentConditions);
			connectiveStack.push(currentConnective);
			
            currentConditions = new ConditionList(newConnective);
            currentConnective = newConnective;
        }
		public void end() {
			var lastConditions = conditionStack.pop();
			var lastConnective = connectiveStack.pop();
			
			currentConditions = lastConditions;
			currentConnective = lastConnective;
		}
		
		public void add(Condition condition) {
			currentConditions.add(condition);
		}
		

		public String buildJPQLString(String objectName) {
			return "error";
		}

		public String toString() {
			return "error";
		}
	}
	
	public static class Condition{
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
		
		public String escape(String value, boolean quote) {
			if (quote) {
				return "'" + value.replace("'", "''") + "'";
			}
			return value.replace("'", "''");				
		}
		
		public String toString() {
			System.out.println("GenericDAO value: " + String.valueOf(value) + " is naively quoted, using testclass");
			return "(" + key + " " + operator + " " + (value == null ? "null" : escape(String.valueOf(value), true)) + ")";
		}
    }
	
	public static class ConditionList extends Condition{
		protected Connective connective;
		
	    protected ArrayList<Condition> conditions;
	    
	    public ConditionList(Connective connective) {
	        super(null, null, null);
	        this.conditions = new ArrayList<>();
	        this.connective = connective;
	    }
	    
	    public ConditionList(Connective connective, Condition... conditions) {
	        super(null, null, null);
	        this.conditions = new ArrayList<>(Arrays.asList(conditions));
	        this.connective = connective;
	    }
	    
	    public ConditionList(Connective connective, ArrayList<Condition> conditions) {
	        super(null, null, null);
	        this.conditions = conditions;
	        this.connective = connective;
	    }	    
	    
	    public Condition[] getConditionsAsArray() {
	    	return conditions.toArray(new Condition[0]);
	    }
	    
		public void add(Condition condition) {
			conditions.add(condition);
		}

	    @Override
	    public String buildJPQLString(String objectName) {
	        return Arrays.stream(getConditionsAsArray())
	                .map(c -> c.buildJPQLString(objectName))
	                .collect(Collectors.joining(" "+ operator.toString() + " ", "(", ")"));
	    }

	    @Override
	    public String toString() {
	        return Arrays.stream(getConditionsAsArray())
	                .map(Condition::toString)
	                .collect(Collectors.joining(" " + operator.toString() + " ", "(", ")"));
	    }
	}
         
	
	public static <T> Object getQueryResult(TypedQuery<T> query, Quantity quantity) {
    	if(quantity == Quantity.ONE) {
    		try {
    			return query.getSingleResult();
    		} catch (NoResultException e) {
    			return null;
    		}
		} 
    	else if (quantity == Quantity.MANY) {
			return (List<?>) query.getResultList();
		}
    	    	
		throw new IllegalArgumentException("ONE_OR_MANY must be ONE or MANY");
	}
	
    public static <T> Object findByConditions(Condition conditions[], Quantity quantity, Class<?> clazz) {
        var em = StaticEMF.getNewEM(); 
        try {
        	// create query string
        	String queryStr = "SELECT o FROM " + clazz.getSimpleName() + " o";
        	
			if (conditions.length > 0) {
				queryStr += " WHERE ";

	        	for(int i = 0; i < conditions.length; i++) {
					if (i > 0) {
						queryStr += " AND ";
					}
					
					queryStr += conditions[i].buildJPQLString("o");					
	        	}
			}        	
        	
        	System.out.println("queryString: " + queryStr);

        	// create query and execute it
        	var query = em.createQuery(queryStr, clazz);
			for (int i = 0; i < conditions.length; i++) {
				query.setParameter(conditions[i].key, conditions[i].value);
			}
        	
        	return getQueryResult(query, quantity);
        } catch (NoResultException e) {
            return null;
        }finally {
        	em.close();
        }
    }
    
    public static <T> Object findOneByConditions(Condition conditions[], Quantity quantity, Class<?> clazz) {
    	return findByConditions(conditions, Quantity.ONE, clazz);
    }

}
