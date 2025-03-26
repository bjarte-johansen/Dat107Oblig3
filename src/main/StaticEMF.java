package main;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/*
 * Use a static EntityManagerFactory to avoid creating multiple factories, and to facility
 * simple access to it to multiple units without circular dependencies.
 */

public class StaticEMF{	
	public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersistanceUnit");
	
	public static EntityManagerFactory getNewEMF() {
		return emf;
	}
	public static EntityManager getNewEM() {
		return emf.createEntityManager();
	}
	
	public static void close() {
		emf.close();
	}
}