package testing;

import java.util.List;

import DAO.AvdelingDAO;
import main.StaticEMF;
import main.*;
import Entities.*;
import jakarta.persistence.TypedQuery;

public class TestAnyToAny {
	/*
	 * teststuff fordi jeg faen ikke får til OneToMany
	 * uten å skrive JPQL query som henter List<Ansatt>
	 * i stedet for å bruke @OneToMany
	 */

	public static void testOneToManyForAvdelingAnsatte() {
		Avdeling avd1 = StaticEMF.getNewEM().find(Avdeling.class, 1);
		
		var em = StaticEMF.getNewEM();
		avd1 = em.merge(avd1);
		
		System.out.println("using List<>");
		System.out.println("antall ansatte: " + avd1.ansatte.size());			
		for(Ansatt ansatt : avd1.ansatte) {
			System.out.println(ansatt);
		}
		System.out.println();
		
		System.out.println("using getAnsatte()");
		System.out.println("antall ansatte: " + avd1.getAnsatte().size());			
		for(Ansatt ansatt : avd1.ansatte) {
			System.out.println(ansatt);
		}
		System.out.println();

		// duplicate, was using JPQL, not anymore, problem fixed
		System.out.println("using JPQL");
		System.out.println("antall ansatte: " + avd1.getAnsatte().size());						
		for(Ansatt ansatt : avd1.getAnsatte()) {
			System.out.println(ansatt);
		}
		System.out.println();
		
		List<Avdeling> avdelinger2 = StaticEMF.getNewEM().createQuery(
			    "SELECT d FROM Avdeling d LEFT JOIN FETCH d.ansatte", Avdeling.class)
			    .getResultList();	
		for (int i = 0; i < avdelinger2.size(); i++) {
			System.out.println(avdelinger2.get(i));
			System.out.println("antall ansatte: " + avdelinger2.get(i).ansatte.size());
		}
		System.out.println();		
{	
		Avdeling avd2 = AvdelingDAO.findById(1);
		System.out.println("using JPQL left join fetch");
		System.out.println("antall ansatte: " + avd2.getAnsatte().size());
		System.out.println();
}		

		System.out.println("using JPQL with LEFT JOIN FETCH");
		TypedQuery<Avdeling> q = em.createQuery(
		  "SELECT a FROM Avdeling a LEFT JOIN FETCH a.ansatte WHERE a.id = :id", Avdeling.class);
		q.setParameter("id", 1);
		Avdeling a = q.getSingleResult();

		System.out.println("Ansatte: " + a.ansatte.size()); // Should not be empty
		System.out.println();
					
		
		em.close();	
	}
}
