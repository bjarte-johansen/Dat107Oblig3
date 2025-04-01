package GenericDAO;

import java.util.*;
import Entities.*;
import Entities.*;


public class GenericTesting {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ArrayList<GenericDAO.Condition> conds = new ArrayList<>();
		
		// add ocnditions
		conds.add(new GenericDAO.Condition("stilling", "=", "Sjef"));
		conds.add(new GenericDAO.Condition("loennPerMaaned", ">=", (Double) 0.0));
		
	    GenericDAO.Condition conditionArray[] = conds.toArray(new GenericDAO.Condition[0]);
		
		{
			// execute query
			List<Ansatt> ansatte = (List<Ansatt>) GenericDAO.findByConditions(conditionArray, GenericDAO.Quantity.MANY, Ansatt.class);
			
			// list results
			System.out.println("ansatte funnet: " + ansatte.size());
			for (Ansatt ansatt : ansatte) {
				System.out.println("ansatt funnet: " + ansatt);
			}
		}
		
		{
			//Ansatt ansatt = (Ansatt) GenericDAO.findByConditions(new GenericDAO.Condition[] {new GenericDAO.Condition("id", "=", 4)}, GenericDAO.Quantity.ONE, Ansatt.class);
			//System.out.println("ansatt funnet: " + ansatt);
		}
	}
}