package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import Entities.*;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;


class EMF{	
	public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersistanceUnit");
	
	public static EntityManagerFactory getEMF() {
		return emf;
	}
	public static EntityManager getEM() {
		return emf.createEntityManager();
	}
}

class AnsattDAO{
	/*
	 * find entity by custom attribute
	 */
		 
	public static <T> Ansatt findByColumnEquals(String key, T value) {
		var em = EMF.getEM(); 
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
		return findByColumnEquals("id", id);
	}

	public static Ansatt findByBrukernavn(String brukernavn) {
		return findByColumnEquals("brukernavn", brukernavn);
	}
}


class AvdelingDAO{
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


public class Main {
	// true if verbose output should be printed
	public static boolean VERBOSE_COMMANDS = true;

	// state machine state
	public static String CurrentMenuState = "main";
	
	// entity manager factory
	public static EntityManagerFactory emf = EMF.getEMF();
	
	// scanner
	private static final Scanner scanner = new Scanner(System.in);	
	
	
	/*
	 * print verbose output that can be muted by setting
	 * VERBOSE_COMMANDS to false
	 */
	
	public static void printVerbose(String message) {
        if(VERBOSE_COMMANDS) {
            System.out.println(message);
        }
    }


	
	/*
	 * method to persist any object
	 */
	
	public static <T> void persistObject(EntityManagerFactory emf, T obj) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(obj);
		em.getTransaction().commit();
		em.close();
	}
		
	
	public static <T> void persistObject(T obj) {
		persistObject(emf, obj);
	}

	
	
	/**
	 * parametric way to get list of any entity that has a model
	 * @param <T>
	 * @param entityInstance
	 * @param clazz
	 * @return List<T>
	 */
	
	public static <T> List<T> getAnyEntityList(T entityInstance, Class<T> clazz){		
		EntityManager em = emf.createEntityManager();
		List<T> items = em
			.createQuery("SELECT a FROM " + entityInstance.getClass().getSimpleName() + " a", clazz)
			.getResultList();
		em.close();
		return items;
	}		
	
	
	/**
	 * specific way to get list of Ansatt 
	 */
	
	public static List<Ansatt> getAnsattList(){
		return getAnyEntityList(new Ansatt(), Ansatt.class);
	}
	
	public static List<Prosjekt> getProsjektList(){
		return getAnyEntityList(new Prosjekt(), Prosjekt.class);
	}	
	
	public static List<Avdeling> getAvdelingList(){
		return getAnyEntityList(new Avdeling(), Avdeling.class);
	}
	
	public static List<AnsattProsjektPivot> getAnsattProsjektPivotList(){
		return getAnyEntityList(new AnsattProsjektPivot(), AnsattProsjektPivot.class);		
	}	
	

	
	/*
	 * generic methods to print entity lists
	 */
	
	public static <T1> void printEntityListItems(List<T1> entities, Class<?> clazz) {
		String prefix = "(id ";
		String postfix = ")";
		int index = 1;
		
		for (T1 entity : entities) {
			if(clazz == Ansatt.class){
				Ansatt casted = (Ansatt) entity;
				System.out.print(prefix + casted.getId() + postfix);
			}else if (clazz == Avdeling.class) {
				Avdeling casted = (Avdeling) entity;
				System.out.print(prefix + casted.getId() + postfix);
			}else if (clazz == Prosjekt.class) {
				Prosjekt casted = (Prosjekt) entity;
				System.out.print(prefix + casted.getId() + postfix);
			}else if (clazz == AnsattProsjektPivot.class) {
				AnsattProsjektPivot casted = (AnsattProsjektPivot) entity;
				System.out.print(prefix + casted.getId() + postfix);
			}else {
				System.out.print(index++);
			}

			System.out.print(" : ");
			System.out.println(entity);
		}
	}

	public static void printEntityList(Class<?> classRef, String classRefName) {
		EntityManager em = emf.createEntityManager();
		
		List<?> items = em
			.createQuery("SELECT a FROM " + classRefName + " a", classRef)
			.getResultList();
		
		System.out.println("# Liste over " + classRefName + "(e/er/ere):");		
		
		// print faktisk liste
		printEntityListItems(items, classRef);
		System.out.println();
		
		em.close();
	}
	
	
	
	/*
	 * specific methods to print entity lists	
	 */
	
	public static void printAvdelingList() {		
		printEntityList(Avdeling.class, "Avdeling");
	}
	
	public static void printAnsattList() {		
		printEntityList(Ansatt.class, "Ansatt");
	}	
	
	public static void printProsjektList() {
		printEntityList(Prosjekt.class, "Prosjekt");
	}
	
	public static void printAnsattProsjektPivotList() {
		printEntityList(AnsattProsjektPivot.class, "AnsattProsjektPivot");
	}	
	
	

	/*
	 * find entity by custom attribute
	 */
	 
	public static <T> Ansatt findAnsattByColumn(String key, T value) {
		return AnsattDAO.findByColumnEquals(key, value);
	}

	
	
	/*
	 * read menu choice
	 */
	
	public static Integer readMenuChoiceInt(String message) {
		if(message != null) {
			System.out.println(message);
			System.out.flush();
		}
		
		while (!scanner.hasNextInt()) {
		    System.out.println("Invalid input. Try again:");
		    scanner.next(); // discard invalid token
		}	
		int choice = scanner.nextInt();
		System.out.println();
		return choice;
	}
	
	public static String readMenuChoiceString(String message) {
		if(message != null) {
			System.out.println(message);
			System.out.flush();
		}
		
		while (!scanner.hasNextLine()) {
		    System.out.println("Invalid input. Try again:");
		    scanner.next(); // discard invalid token
		}	
		String choice = scanner.nextLine();
		System.out.println();
		return choice;
	}	
	
	public static Float readMenuChoiceFloat(String message) {
		if(message != null) {
			System.out.println(message);
			System.out.flush();
		}
		
		while (!scanner.hasNextFloat()) {
		    System.out.println("Invalid input. Try again:");
		    scanner.next(); // discard invalid token
		}				
		float choice = scanner.nextFloat();
		System.out.println();
		return choice;
	}	
	
	
	/*
	 * print item found message
	 */
	
	public static <T> void printItemFoundMessage(T item) {
		if(item == null) {
			System.out.println("Ingen resultat funnet");
		}else {
			System.out.println("Resultat funnet: ");
			System.out.println(item);
		}
	}
	
	
	/*
	 * command methods
	 */
	
	public static void print_state_menu() {
		Map<Integer, String> commands = new HashMap<Integer, String>();
		commands.put(0, "abort");
		commands.put(1, "finnAnsattById");
		commands.put(2, "finnAnsattByBrukernavn");
		commands.put(3, "listAnsatt");
		commands.put(4, "oppdateringStillingOgLonn");
		commands.put(5, "leggTilAnsatt");
		
		
		System.out.println("# MENY");
		System.out.println("\t1. Finn ansatt (id)");
		System.out.println("\t2. Finn ansatt (brukernavn)");
		System.out.println("\t3. List ansatte");
		System.out.println("\t4. Oppdatering stilling og lønn");
		System.out.println("\t5. Legg til ansatt");
		System.out.println();
		System.out.flush();
		
		int choice = readMenuChoiceInt("Tast inn ditt valg:");
		switch(commands.get(choice)) {
			case "abort":
				rootMenu();
				break;
		
			case "finnAnsattById":
			{
				int needle = readMenuChoiceInt("Skriv inn ansatt-id:");					
				var item = AnsattDAO.findById(needle);
				printItemFoundMessage(item);	
				break;
			}
			
			case "finnAnsattByBrukernavn":
			{
				String needle = readMenuChoiceString("Skriv inn ansatt-brukernavn:");					
				Ansatt item = AnsattDAO.findByBrukernavn(needle);
				printItemFoundMessage(item);
				break;
			}
			
			case "listAnsatt": {
				printAnsattList();
				break;
			}
			
			case "oppdateringStillingOgLonn":
			{
				int needle = readMenuChoiceInt("Skriv inn ansatt-id:");				
				var item = findAnsattByColumn("id", needle);
				if(item == null) {
                    System.out.println("Ingen resultat funnet");
				}else {
					System.out.println("Resultat funnet: ");
					String newStilling = readMenuChoiceString("Skriv inn ny stilling:");
					item.setStilling(newStilling);
					float newLoenn = readMenuChoiceFloat("Skriv inn ny lønn:");
					item.setLoennPerMaaned(newLoenn);
					System.out.println(item);					
					persistObject(item);
				}
				break;
			}
			
			case "leggTilAnsatt": {
				// set up new Ansatt object
				Ansatt newAnsatt = new Ansatt();
				newAnsatt.setFornavn(readMenuChoiceString("Skriv inn fornavn:"));
				newAnsatt.setEtternavn(readMenuChoiceString("Skriv inn etternavn:"));
				newAnsatt.setBrukernavn(readMenuChoiceString("Skriv inn brukernavn:"));
				newAnsatt.setAnsettelseDato(LocalDateTime.now());
				newAnsatt.setStilling(readMenuChoiceString("Skriv inn stilling:"));
				newAnsatt.setLoennPerMaaned(readMenuChoiceFloat("Skriv inn lønn:"));
				newAnsatt.setAvdeling(getAvdelingList().get(0));

				// persist new Ansatt object
				persistObject(newAnsatt);
				break;
			}
			
			default:
                System.out.println("ugyldig valg");
		}
		System.out.println();
		
		rootMenu();
	}
	
	public static void rootMenu() {
		CurrentMenuState = "main";
		printMenu();
	}
	
	public static void printMenu() {
		switch(CurrentMenuState) {
			case "main":
				print_state_menu();
				System.out.flush();
				break;
		}
	}
	
	/*
	 * main method
	 */	

	public static void main(String[] args) {
		// delete old data and insert new data
		// clear old database data
		DemoData.deleteDemoData();
		DemoData.createDemoData();
		System.out.println();
		
		EntityManager em = emf.createEntityManager();	
		

		/*
		printAvdelingList();
		printAnsattList();
		printProsjektList();
		printAnsattProsjektPivotList();
		*/
		
		/*
		List<?> items = getAnyEntityList(new Ansatt(), Ansatt.class);
		for (var a : items) {
			System.out.println(a.getClass().getSimpleName());
			System.out.println(a);
		}
		*/
		
		printMenu();

		em.close();
		emf.close();
	}

}
