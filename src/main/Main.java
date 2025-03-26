package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
 * menu item class
 */

@FunctionalInterface
interface MenuAction{
	void execute();
};


class MenuItem{
	private int key;
	private String text;
    private MenuAction action;
    
    public MenuItem(int key, String text, MenuAction action) {
    	this.key = key;
        this.text = text;
        this.action = action;
    }
    
    public void execute() {
        action.execute();
    }
    
	public String getText() {
		return text;
	}
	public int getKey() {
		return key;
	}
    
    public String toString() {
        return String.format("%d. ", key) + text;
    }
};




/*
 * main class
 */


public class Main {
	/*
	 * interface for menu actions
	 */
	



	// menu items
	private static Map<Integer, MenuItem> menuItems = new LinkedHashMap<>();
	private static int menuItemInvalidIndex = -1;
	
	
	// true if verbose output should be printed
	public static boolean VERBOSE_COMMANDS = true;
	
	// entity manager factory
	//private static EntityManagerFactory emf = StaticEMF.getNewEMF();
	

	
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
		em.flush();		
		em.getTransaction().commit();
		em.close();
	}

	
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
	
	public static void printEntityList(Class<?> classRef) {				
		System.out.println("# Liste over " + classRef.getSimpleName() + "(e/er/ere):");
		
		List<?> items = getAnyEntityList(classRef);		
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
	 * actions
	 */
	
	
	public static void action_employee_list() { 
		printAnsattList(); 
	}
	
	public static void action_employee_find_by_id(){
		int needle = TextInput.readInt("Skriv inn ansatt-id:");					
		var item = AnsattDAO.findById(needle);
		printItemFoundMessage(item);	
	}
	
	public static void action_employee_find_by_username(){
		String needle = TextInput.readLine("Skriv inn ansatt-brukernavn:");					
		var item = AnsattDAO.findByBrukernavn(needle);
		printItemFoundMessage(item);
	}	

	public static void action_set_position_and_salary(){
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
	}
	
	public static void action_employee_add() {
		// print notice
		System.out.println("// ps: vi har satt ansettelsedato til now() i java:");
		
		// cache variables
		LocalDateTime nowDateTime = LocalDateTime.now();
		
		List<Avdeling> avdelingList = getAvdelingList();
		if (avdelingList.size() == 0) {
			System.out.println("Ingen avdelinger funnet. Kan ikke legge til ansatt.");
			return;
		}
		
		// set up new Ansatt object
		Ansatt newAnsatt = new Ansatt();
		newAnsatt.setFornavn(TextInput.readLine("Skriv inn fornavn:"));
		newAnsatt.setEtternavn(TextInput.readLine("Skriv inn etternavn:"));
		newAnsatt.setBrukernavn(TextInput.readLine("Skriv inn brukernavn:"));
		newAnsatt.setAnsettelseDato(nowDateTime);
		newAnsatt.setStilling(TextInput.readLine("Skriv inn stilling:"));
		newAnsatt.setLoennPerMaaned(TextInput.readFloat("Skriv inn lønn:"));
		
		// select avdeling untill suitable is found
		while(true) {
			printAvdelingList();
			int avdelingId = TextInput.readInt("Velg en avdeling (id):");
			Avdeling avdeling = AvdelingDAO.findById(avdelingId);
			if (avdeling != null) {
				newAnsatt.setAvdeling(avdeling);						
				break;
			}
			
			System.out.println("Ingen avdeling funnet med id " + avdelingId);
		}
		
		
		// persist new Ansatt object
		saveEntity(newAnsatt, Ansatt::getId);
	}

	public static void action_employee_find_by_department()	{			
		int needle = TextInput.readInt("Skriv inn avdeling-id:");
		var item = AvdelingDAO.findById(needle);
		
		if (item == null) {
			System.out.println("Ingen resultat funnet");
		} else {
			System.out.println("Resultat funnet: ");
			System.out.println(item);
			
			List<Ansatt> ansatte = item.getAnsatte();
			System.out.println("Ansatte: (" + ansatte.size() + ")");
			for (Ansatt ansatt : ansatte) {
				if(ansatt.equals(item.getLeder())) {
					System.out.print("[Leder ");
					System.out.print(ansatt);
					System.out.println("]");
				}else {
					System.out.println(ansatt);
				}
			}
			
			System.out.println("Leder (sjef):");
			System.out.println(item.getLeder());
		}
	}
	
	public static void action_employee_update_department(){
		// find ansatt
		int ansattId = TextInput.readInt("Skriv inn ansatt-id:");
		Ansatt ansatt = AnsattDAO.findById(ansattId);
		if (ansatt == null) {
			System.out.println("Ingen ansatt funnet med id " + ansattId);
			return;
		}				
		
		// find avdeling
		printAvdelingList();
		int avdelingId = TextInput.readInt("Skriv inn avdeling-id:");				
		Avdeling avdeling = AvdelingDAO.findById(avdelingId);
		if (avdeling == null) {
			System.out.println("Ingen avdeling funnet med id " + avdelingId);
			return;
		}
		
		ansatt.setAvdeling(avdeling);
		saveEntity(ansatt, Ansatt::getId);
	}
	
	// department actions

	public static void action_department_add() {
		// opprett object
		Avdeling avdeling = new Avdeling();
		
		// less inn navn
		avdeling.setNavn(TextInput.readLine("Skriv inn avdelingens navn:"));
		
		// print ansatte
		printAnsattList();
		
		// read input and find ansatt
		int leaderId = TextInput.readInt("Skriv inn leder-ansatt-id:");
		Ansatt newLeader = AnsattDAO.findById(leaderId);
		if(newLeader == null) {
			System.out.println("Ingen leder med id funnet");
			return;
		}
	
		// set ansatt's avdeling
		avdeling.setLeder(newLeader);
		saveEntity(avdeling, Avdeling::getId);
		
		// update ansatt avdeling
		newLeader.setAvdeling(avdeling);
		saveEntity(newLeader, Ansatt::getId);
	}

	public static void action_department_find_by_id(){
		int needle = TextInput.readInt("Skriv inn avdeling-id:");					
		var item = AvdelingDAO.findById(needle);
		printItemFoundMessage(item);	
	}	
	
	public static void action_department_list() {
		printAvdelingList();
	}	
	
	
	// project actions
	
	public static void action_project_list() {
		printProsjektList();
	}
	
	public static void action_project_find_by_id() {
		int needle = TextInput.readInt("Skriv inn prosjekt-id:");
		var item = ProsjektDAO.findById(needle);
		printItemFoundMessage(item);
	}	
	
	
	
	/*
	 * menu methods
	 */
		
	public static void printMenu() {
		// menu header
		System.out.println("-".repeat(20));
		System.out.println("   MENY");
		System.out.println("-".repeat(20));
		System.out.println();

		for (var entry : menuItems.entrySet()) {
			if (entry.getKey() < 0) {
				System.out.println("-");
			} else {
				System.out.println(entry.getValue().toString());
			}
		}
				
		// print empty line and flush
		System.out.println();
		System.out.flush();
		
		int choice = TextInput.readInt("Tast inn ditt valg:");
		MenuItem selectedMenuItem = menuItems.get(choice);
		if(selectedMenuItem == null) {
			System.out.println("Ugyldig valg");
			System.out.println();
		}else {
			try {
				selectedMenuItem.execute();
			}catch(Exception e) {
			}
			System.out.println();			
		}
		
		// wait untill user presses enter
		TextInput.waitUntillInput();
		
		printMenu();
	}
	
	public static void addMenuItem(int index, String text, MenuAction action) {				
		if(index < 0) {
			menuItems.put(menuItemInvalidIndex--, new MenuItem(index >= 0 ? index : -1, text, action));
		}else {		
			menuItems.put(index, new MenuItem(index, text, action));
		}
	}
	
	public static void createMenu() {
		// items
		addMenuItem(1, "Ansatt, list", Main::action_employee_list);	
		addMenuItem(2, "Ansatt, finn etter id", Main::action_employee_find_by_id);	
		addMenuItem(3, "Ansatt, finn etter brukernavn", Main::action_employee_find_by_username);
		addMenuItem(4, "Ansatt, endre stilling og lønn", Main::action_set_position_and_salary);
		addMenuItem(5, "Ansatt, legg til", Main::action_employee_add);
		addMenuItem(6, "Ansatt, finn etter avdeling", Main::action_employee_find_by_department);		
		addMenuItem(7, "Ansatt, endre avdeling", Main::action_employee_update_department);			
		addMenuItem(-1, null, null);
		addMenuItem(8, "Avdeling, list", Main::action_department_list);
		addMenuItem(9, "Avdeling, finn etter id", Main::action_department_find_by_id);		
		addMenuItem(10, "Avdeling, legg til", Main::action_department_add);		
		addMenuItem(-1, null, null);
		addMenuItem(11, "Prosjekt, list", Main::action_project_list);
		addMenuItem(12, "Prosjekt, finn etter id", Main::action_project_find_by_id);
	}
	
	/*
	 * main method
	 */	

	public static void main(String[] args) {
		// handle database reseting		
		boolean allwaysResetDatabase = true;
		boolean showResetDatabaseOption = true;
		int resetDatabaseFlag = 0;
		
		if (!allwaysResetDatabase && showResetDatabaseOption) {
			resetDatabaseFlag = TextInput.readInt("Vil du resete databasen? (1=ja, 0=nei):");
		}
		
		if(allwaysResetDatabase || resetDatabaseFlag == 1) {
			DemoData.deleteDemoData();
            DemoData.createDemoData();
            System.out.println();
		}
		
		// create menu
		createMenu();
		
		// print menu
		printMenu();

		// close entity manager factory
		StaticEMF.close();
	}

}
