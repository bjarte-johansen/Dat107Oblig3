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
import java.util.function.Function;

import Entities.*;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;


/*
 * since its static, put it into its own class so we can access it from anywhere
 * if we put it in its own file
 */

class StaticEMF{	
	public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersistanceUnit");
	
	public static EntityManagerFactory getEMF() {
		return emf;
	}
	public static EntityManager getEM() {
		return emf.createEntityManager();
	}
	
	public static void close() {
		emf.close();
	}
}




/*
 * main class
 */


public class Main {
	// true if verbose output should be printed
	public static boolean VERBOSE_COMMANDS = true;

	// state machine state
	public static String CurrentMenuState = "main";
	
	// entity manager factory
	public static EntityManagerFactory emf = StaticEMF.getEMF();
	

	
	
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
	
	public static <T, ID> void saveEntity(T entity, Function<T, ID> idGetter) {
		EntityManager em = StaticEMF.getEM();	
		
	    em.getTransaction().begin();
	    if (idGetter.apply(entity) == null) {
	        em.persist(entity);
	    } else {
	        em.merge(entity);
	    }
	    em.getTransaction().commit();
	    
	    em.close();
	}	

	public static <T> void createEntity(T obj) {
		EntityManager em = StaticEMF.getEM();
		em.getTransaction().begin();
		em.persist(obj);
		em.getTransaction().commit();
		em.close();
	}
	/*
	public static <T> void updateObject(T obj) {
		EntityManager em = StaticEMF.getEM();
		em.getTransaction().begin();
		em.merge(obj);
		em.getTransaction().commit();
		em.close();
	}
	*/

	
	
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
			// get id from entity-object if they are of certain classes
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
				System.out.print("unknown");
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
	
	public static void printAvdelingList() { printEntityList(Avdeling.class, "Avdeling"); }	
	public static void printAnsattList() { printEntityList(Ansatt.class, "Ansatt"); }		
	public static void printProsjektList() { printEntityList(Prosjekt.class, "Prosjekt"); }
	public static void printAnsattProsjektPivotList() {	printEntityList(AnsattProsjektPivot.class, "AnsattProsjektPivot"); }	
	
	
	
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
		
		int choice = TextInput.readInt("Tast inn ditt valg:");
		String cmd = commands.get(choice);
		if(cmd == null) {
			System.out.println("ugyldig valg");
			System.out.println();
			rootMenu();
			return;
		}
		
		switch(cmd) {
			case "abort":
				rootMenu();
				break;
		
			case "finnAnsattById":
			{
				int needle = TextInput.readInt("Skriv inn ansatt-id:");					
				var item = AnsattDAO.findById(needle);
				printItemFoundMessage(item);	
				break;
			}
			
			case "finnAnsattByBrukernavn":
			{
				String needle = TextInput.readLine("Skriv inn ansatt-brukernavn:");					
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
				int needle = TextInput.readInt("Skriv inn ansatt-id:");				
				var item = AnsattDAO.findById(needle);
				if(item == null) {
                    System.out.println("Ingen resultat funnet");
				}else {
					System.out.println("Resultat funnet: ");
					String newStilling = TextInput.readLine("Skriv inn ny stilling:");
					item.setStilling(newStilling);
					float newLoenn = TextInput.readFloat("Skriv inn ny lønn:");
					item.setLoennPerMaaned(newLoenn);
					System.out.println(item);					
					saveEntity(item, Ansatt::getId);
				}
				break;
			}
			
			case "leggTilAnsatt": {
				// set up new Ansatt object
				Ansatt newAnsatt = new Ansatt();
				newAnsatt.setFornavn(TextInput.readLine("Skriv inn fornavn:"));
				newAnsatt.setEtternavn(TextInput.readLine("Skriv inn etternavn:"));
				newAnsatt.setBrukernavn(TextInput.readLine("Skriv inn brukernavn:"));
				newAnsatt.setAnsettelseDato(LocalDateTime.now());
				newAnsatt.setStilling(TextInput.readLine("Skriv inn stilling:"));
				newAnsatt.setLoennPerMaaned(TextInput.readFloat("Skriv inn lønn:"));
				newAnsatt.setAvdeling(getAvdelingList().get(0));

				// persist new Ansatt object
				saveEntity(newAnsatt, Ansatt::getId);
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
		
//		EntityManager em = emf.createEntityManager();	
		

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

//		em.close();
		emf.close();
		
		StaticEMF.close();
	}

}
