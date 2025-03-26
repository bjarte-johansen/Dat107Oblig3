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




/*
 * main class
 */


public class Main {
	// true if verbose output should be printed
	public static boolean VERBOSE_COMMANDS = true;

	// state machine state
	public static String CurrentMenuState = "main";
	
	// entity manager factory
	public static EntityManagerFactory emf = StaticEMF.getNewEMF();
	

	
	
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
	
	public static <T, ID> void saveEntity(T entity, String op) {
		if(op == null || op.length() == 0) {
			throw new IllegalArgumentException("Invalid operation (must be persist|merge)");
		}
		
		EntityManager em = StaticEMF.getNewEM();	
		
	    em.getTransaction().begin();
	    if (op.equals("persist")) {
	        em.persist(entity);
	    } else if (op.equals("merge")){
	        em.merge(entity);
	    }else {
	    	throw  new IllegalArgumentException("Invalid operation c(reate) or u(pdate)");
	    }
	    
	    em.getTransaction().commit();
	    
	    em.close();
	}		
	
	public static <T, ID> void saveEntity(T entity, Function<T, ID> idGetter) {
		EntityManager em = StaticEMF.getNewEM();	
		
	    em.getTransaction().begin();
	    if ((idGetter == null) || (idGetter.apply(entity) == null)) {
	        em.persist(entity);
	    } else {
	        em.merge(entity);
	    }
	    em.getTransaction().commit();
	    
	    em.close();
	}	

	public static <T> void createEntity(T obj) {
		EntityManager em = StaticEMF.getNewEM();
		em.getTransaction().begin();
		em.persist(obj);
		em.getTransaction().commit();
		em.close();
	}
	/*
	public static <T> void updateObject(T obj) {
		EntityManager em = StaticEMF.getNewEM();
		em.getTransaction().begin();
		em.merge(obj);
		em.getTransaction().commit();
		em.close();
	}
	*/

	
	
	/**
	 * parametric way to get list of any entity that has a model
	 * @param <T>s
	 * @param entityInstance
	 * @param clazz
	 * @return List<T>
	 */
	
	public static <T> List<T> getAnyEntityList(Class<T> clazz){
		EntityManager em = StaticEMF.getNewEM();
		List<T> items = em
			.createQuery("SELECT a FROM " + clazz.getSimpleName() + " a", clazz)
			.getResultList();
		em.close();
		return items;
	}		
	
	
	/**
	 * specific way to get list of Ansatt 
	 */
	
	public static List<Ansatt> getAnsattList(){	return getAnyEntityList(Ansatt.class);	}	
	public static List<Prosjekt> getProsjektList(){	return getAnyEntityList(Prosjekt.class); }		
	public static List<Avdeling> getAvdelingList(){ return getAnyEntityList(Avdeling.class); }	
	public static List<AnsattProsjektPivot> getAnsattProsjektPivotList(){ return getAnyEntityList(AnsattProsjektPivot.class); }	
	

	
	/*
	 * generic methods to print entity lists
	 */
	
	public static <T> Integer getEntityId(T entity, Class<?> clazz) {
		// get id from entity-object if they are of certain classes
		if(clazz == Ansatt.class){
			var e = ((Ansatt) entity);
			return e.getId();
		}else if (clazz == Avdeling.class) {
			var e = (Avdeling) entity;
			return e.getId();
		}else if (clazz == Prosjekt.class) {
			var e = (Prosjekt) entity;
			return e.getId();
		}else if (clazz == AnsattProsjektPivot.class) {
			var e = (AnsattProsjektPivot) entity;
			return e.getId();
		}

		throw new IllegalArgumentException("Unknown class");
	}
	
	public static <T1> void printEntityListItems(List<T1> entities, Class<?> clazz) {
		String prefix = "(id ";
		String postfix = ")";
		
		for (T1 entity : entities) {
			Integer entityId = getEntityId(entity, clazz);
			System.out.print((entityId != null) ? (prefix + entityId + postfix) : "unknown");
			System.out.print(" : ");
			System.out.println(entity);
		}
	}
	
	public static <T> void printEntity(T entity) {
		System.out.println(entity);
	}
	
	public static void printEntityList(Class<?> classRef) {		
		List<?> items = getAnyEntityList(classRef);
		
		System.out.println("# Liste over " + classRef.getSimpleName() + "(e/er/ere):");		
		printEntityListItems(items, classRef);	
		
		System.out.println();
	}
	
	
	
	/*
	 * specific methods to print entity lists	
	 */
	
	public static void printAvdelingList() { printEntityList(Avdeling.class); }	
	public static void printAnsattList() { printEntityList(Ansatt.class); }		
	public static void printProsjektList() { printEntityList(Prosjekt.class); }
	public static void printAnsattProsjektPivotList() {	printEntityList(AnsattProsjektPivot.class); }	
	
	
	
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
		commands.put(6, "resetDatabase");		
		
		int menuItemIndex = 1;
		System.out.println("# MENY");
		System.out.println("\t" + (menuItemIndex++) + ". Finn ansatt (id)");
		System.out.println("\t" + (menuItemIndex++) + ". Finn ansatt (brukernavn)");
		System.out.println("\t" + (menuItemIndex++) + ". List ansatte");
		System.out.println("\t" + (menuItemIndex++) + ". Oppdatering stilling og lønn");
		System.out.println("\t" + (menuItemIndex++) + ". Legg til ansatt");
		System.out.println("\t" + (menuItemIndex++) + ". Reset database");				
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
		
		System.out.println("Du valgte " + cmd);
		System.out.println();
		
		switch(cmd) {
			case "abort":
				rootMenu();
				break;
				
			case "resetDatabase":
				DemoData.deleteDemoData();
				DemoData.createDemoData();
				System.out.println();
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
				var item = AnsattDAO.findByBrukernavn(needle);
				printItemFoundMessage(item);
				break;
			}
			
			case "listAnsatt": 
			{
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
			
			case "leggTilAnsatt": 
			{
				// print notice
				System.out.println("// ps: vi har satt ansettelsedato til now() i java:");
				
				// cache variables
				LocalDateTime dtNow = LocalDateTime.now();
				
				List<Avdeling> avdelingList = getAvdelingList();
				if (avdelingList.size() == 0) {
					System.out.println("Ingen avdelinger funnet. Kan ikke legge til ansatt.");
					break;
				}
				
				// set up new Ansatt object
				Ansatt newAnsatt = new Ansatt();
				newAnsatt.setFornavn(TextInput.readLine("Skriv inn fornavn:"));
				newAnsatt.setEtternavn(TextInput.readLine("Skriv inn etternavn:"));
				newAnsatt.setBrukernavn(TextInput.readLine("Skriv inn brukernavn:"));
				newAnsatt.setAnsettelseDato(dtNow);
				newAnsatt.setStilling(TextInput.readLine("Skriv inn stilling:"));
				newAnsatt.setLoennPerMaaned(TextInput.readFloat("Skriv inn lønn:"));
				newAnsatt.setAvdeling(avdelingList.getFirst());

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
