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






class DatabaseDAO{

	/*
	 * method to persist any object
	 */

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
			.createQuery("SELECT o FROM " + clazz.getSimpleName() + " o", clazz)
			.getResultList();
		em.close();
		return items;
	}		
	
	
	/**
	 * specific way to get list of Ansatt 
	 */
	
	public static List<Ansatt> getAnsattList(){	return getAnyEntityList(Ansatt.class); }	
	public static List<Prosjekt> getProsjektList(){	return getAnyEntityList(Prosjekt.class); }		
	public static List<Avdeling> getAvdelingList(){ return getAnyEntityList(Avdeling.class); }	
	public static List<AnsattProsjektPivot> getAnsattProsjektPivotList(){ return getAnyEntityList(AnsattProsjektPivot.class); }	
	

	/*
	public static <T> Integer getEntityId(T entity, Class<?> clazz) {
		// get id from entity-object if they are of certain classes
		if(clazz == Ansatt.class){
			return ((Ansatt) entity).getId();
		}else if (clazz == Avdeling.class) {
			return ((Avdeling) entity).getId();
		}else if (clazz == Prosjekt.class) {
			return ((Prosjekt) entity).getId();
		}else if (clazz == AnsattProsjektPivot.class) {
			return ((AnsattProsjektPivot) entity).getId();
		}

		throw new IllegalArgumentException("Unknown class");
	}
	
	public static <T1> void printEntityListItemsWithId(List<T1> entities, Class<?> clazz) {
		String prefix = "<";
		String postfix = ">";
		
		for (T1 entity : entities) {
			Integer entityId = getEntityId(entity, clazz);
			System.out.print((entityId != null) ? (prefix + entityId + postfix) : "unknown");
			System.out.print(": ");
			System.out.println(entity);
		}
	}
	*/

	
	/*
	 * generic methods to print entity lists
	 */
	
	public static <T1> void printEntityListItems(List<T1> entities) {		
		for (T1 entity : entities) {
			System.out.println(entity);
		}
	}	
	
	public static void printEntityList(Class<?> clazz) {				
		System.out.println("# Liste over " + clazz.getSimpleName().toLowerCase() + "(e/er/ere):");
		
		List<?> items = getAnyEntityList(clazz);		
		printEntityListItems(items);	
		
		System.out.println();
	}
	
	
	
	/*
	 * specific methods to print entity lists	
	 */
	
	public static void printAvdelingList() { printEntityList(Avdeling.class); }	
	public static void printAnsattList() { printEntityList(Ansatt.class); }		
	public static void printProsjektList() { printEntityList(Prosjekt.class); }
	public static void printAnsattProsjektPivotList() {	printEntityList(AnsattProsjektPivot.class); }	
	
}


/*
 * main class
 */


public class Main {	
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
	 * is department leader
	 */

	protected static boolean is_depertment_leader(int employee_id) {
		Avdeling department = AvdelingDAO.findByLeaderId(employee_id);
		return department != null;
	}	
	
	
	
	/*
	 * actions
	 */
	
	
	public static void action_employee_list() { 
		DatabaseDAO.printAnsattList(); 
	}
	
	public static void action_employee_find_by_id(){
		int needle = TextInput.readInt("Skriv inn ansatt-id:");					
		var item = AnsattDAO.findById(needle);
		printItemFoundMessage(item);	
	}
	
	public static void action_employee_find_by_username(){
		String needle = TextInput.readLine("Skriv inn ansatt-brukernavn:");					
		var emp = AnsattDAO.findByBrukernavn(needle);
		printItemFoundMessage(emp);
	}	

	public static void action_set_position_and_salary(){
		int needle = TextInput.readInt("Skriv inn ansatt-id:");				
		var emp = AnsattDAO.findById(needle);
		if(emp == null) {
            System.out.println("Ingen resultat funnet");
		}else {
			System.out.println("Resultat funnet: ");
			String newStilling = TextInput.readLine("Skriv inn ny stilling:");
			emp.setStilling(newStilling);
			
			float newLoenn = TextInput.readFloat("Skriv inn ny lønn:");
			emp.setLoennPerMaaned(newLoenn);
			System.out.println(emp);
			
			DatabaseDAO.saveEntity(emp, Ansatt::getId);
		}
	}
	
	public static void action_employee_add() {
		// print notice
		System.out.println("// ps: vi har satt ansettelsedato til now() i java:");
		
		// cache variables
		LocalDateTime nowDateTime = LocalDateTime.now();

		List<Avdeling> departmentList = DatabaseDAO.getAvdelingList();
		if (departmentList.size() == 0) {
			throw new RuntimeException("Ingen avdelinger funnet. Kan ikke legge til ansatt.");
		}
		
		// set up new Ansatt object
		Ansatt newEmp = new Ansatt();
		newEmp.setFornavn(TextInput.readLine("Skriv inn fornavn:"));
		newEmp.setEtternavn(TextInput.readLine("Skriv inn etternavn:"));
		newEmp.setBrukernavn(TextInput.readLine("Skriv inn brukernavn:"));
		newEmp.setAnsettelseDato(nowDateTime);
		newEmp.setStilling(TextInput.readLine("Skriv inn stilling:"));
		newEmp.setLoennPerMaaned(TextInput.readFloat("Skriv inn lønn:"));
		
		// select avdeling untill suitable is found
		while(true) {
			DatabaseDAO.printAvdelingList();
			int avdelingId = TextInput.readInt("Velg en avdeling (id):");
			Avdeling avdeling = AvdelingDAO.findById(avdelingId);
			if (avdeling != null) {
				newEmp.setAvdeling(avdeling);						
				break;
			}
			
			System.out.println("Ingen avdeling funnet med id " + avdelingId);
		}
		
		// persist new Ansatt object
		DatabaseDAO.saveEntity(newEmp, Ansatt::getId);
	}

	public static void action_employee_find_by_department()	{			
		int needle = TextInput.readInt("Skriv inn avdeling-id:");
		var dept = AvdelingDAO.findById(needle);
		
		if (dept == null) {
			System.out.println("Ingen resultat funnet");
		} else {
			System.out.println("Resultat funnet: ");
			System.out.println(dept);
			
			List<Ansatt> emps = dept.getAnsatte();
			System.out.println("Ansatte: (" + emps.size() + ")");
			for (Ansatt emp : emps) {
				if(emp.equals(dept.getLeder())) {
					System.out.print("[Leder ");
					System.out.print(emp);
					System.out.println("]");
				}else {
					System.out.println(emp);
				}
			}
		}
	}
	
	public static void action_employee_update_department(){
		// find ansatt
		int ansattId = TextInput.readInt("Skriv inn ansatt-id:");
		Ansatt ansatt = AnsattDAO.findById(ansattId);
		if (ansatt == null) {
			throw new RuntimeException("Ingen ansatt funnet med id " + ansattId);
		}				
		
		// find avdeling
		DatabaseDAO.printAvdelingList();
		int avdelingId = TextInput.readInt("Skriv inn avdeling-id:");				
		Avdeling avdeling = AvdelingDAO.findById(avdelingId);
		if (avdeling == null) {
			throw new RuntimeException("Ingen avdeling funnet med id " + avdelingId);
		}
		
		if(is_depertment_leader(ansatt.getId())) {
            throw new RuntimeException("Kan ikke endre avdeling for en leder");
		}
		
		ansatt.setAvdeling(avdeling);
		DatabaseDAO.saveEntity(ansatt, Ansatt::getId);
		
		avdeling.setLeder(ansatt);
		DatabaseDAO.saveEntity(avdeling, Avdeling::getId);
	}
	
	
	
	// department actions
	
	public static void action_department_add() {
		// opprett object
		Avdeling avdeling = new Avdeling();
		
		// less inn navn
		avdeling.setNavn(TextInput.readLine("Skriv inn avdelingens navn:"));
		
		// print ansatte
		DatabaseDAO.printAnsattList();
		
		// read input and find ansatt
		int leaderId = TextInput.readInt("Skriv inn ansatt-id for avdelingsleder:");
		Ansatt newLeader = AnsattDAO.findById(leaderId);
		if(newLeader == null) {
			throw new RuntimeException("Ingen leder med id funnet");
		}
		
		if(is_depertment_leader(newLeader.getId())) {
			throw new RuntimeException("Kan ikke endre avdeling for en leder");
		}		
	
		// set ansatt's avdeling
		avdeling.setLeder(newLeader);
		DatabaseDAO.saveEntity(avdeling, Avdeling::getId);
		
		// update ansatt avdeling
		newLeader.setAvdeling(avdeling);
		DatabaseDAO.saveEntity(newLeader, Ansatt::getId);
	}

	public static void action_department_find_by_id(){
		int needle = TextInput.readInt("Skriv inn avdeling-id:");					
		var item = AvdelingDAO.findById(needle);
		printItemFoundMessage(item);	
	}	
	
	public static void action_department_list() {
		DatabaseDAO.printAvdelingList();
	}	
	
	
	// project actions
	
	public static void action_project_list() {
		DatabaseDAO.printProsjektList();
	}
	
	public static void action_project_list_details() {
		int needle = TextInput.readInt("Skriv inn prosjekt-id:");
		var item = ProsjektDAO.findById(needle);
		if (item == null) {
			throw new RuntimeException("Ingen prosjekt funnet med id " + needle);
		}
		
		System.out.println("# Detaljer for prosjekt:");		
		System.out.println(item);
		System.out.println();		
		
		int totalHours = 0;
		var deltagere = item.getDeltagere();			
		System.out.println("Deltakere:");
		
		if(deltagere.size() == 0) {
			System.out.println("0 funnet");
		}
		
		for (AnsattProsjektPivot ppi : deltagere) {
			System.out.println(ppi);
			totalHours += ppi.getAntallTimer();
		}
		System.out.println();
		
		System.out.println("Total antall timer:");
		System.out.println(totalHours);
		System.out.println();
	}	
	
	public static void action_project_find_by_id() {
		int needle = TextInput.readInt("Skriv inn prosjekt-id:");
		var item = ProsjektDAO.findById(needle);
		printItemFoundMessage(item);
	}	
	
	public static void action_project_add() {
		Prosjekt prosjekt = new Prosjekt();
		prosjekt.setNavn(TextInput.readLine("Skriv inn prosjektets navn:"));
		prosjekt.setBeskrivelse(TextInput.readLine("Skriv inn prosjektets beskrivelse:"));
		DatabaseDAO.saveEntity(prosjekt, Prosjekt::getId);
	}
	
	public static void action_project_add_participant_by_project_id() {
		// find project
		int needle = TextInput.readInt("Skriv inn prosjekt-id:");
		var project = ProsjektDAO.findById(needle);
		if(project == null) {
			throw new RuntimeException("Ingen prosjekt funnet med id " + needle);
		}
		
		// find ansatt
		DatabaseDAO.printAnsattList();
		int ansattId = TextInput.readInt("Skriv inn ansatt-id:");
		var ansatt = AnsattDAO.findById(ansattId);
		if (ansatt == null) {
			throw new RuntimeException("Ingen ansatt funnet med id " + ansattId);
		}
		
		System.out.println("Legger inn hardkodede verdier for timer og rolle");
		AnsattProsjektPivot ppi = new AnsattProsjektPivot();
		ppi.setAnsatt(ansatt);
		ppi.setProsjekt(project);
		ppi.setAntallTimer(10);
		ppi.setRolle("Software Developer");
		DatabaseDAO.saveEntity(ppi, AnsattProsjektPivot::getId);
		
		System.out.println("Ansatt lagt til i prosjekt");
		System.out.println(ppi);
	}
	
	public static void action_project_add_participant_hours_by_project_id() {
		// find project
		int needle = TextInput.readInt("Skriv inn prosjekt-id:");
		var project = ProsjektDAO.findById(needle);
		if (project == null) {
			throw new RuntimeException("Ingen prosjekt funnet med id " + needle);
		}

		// find ansatt
		DatabaseDAO.printAnsattList();
		int ansattId = TextInput.readInt("Skriv inn ansatt-id:");
		var ansatt = AnsattDAO.findById(ansattId);
		if (ansatt == null) {
			throw new RuntimeException("Ingen ansatt funnet med id " + ansattId);
		}

		// find pivot
		AnsattProsjektPivot ppi = AnsattProsjektPivotDAO.findOneByAnsattIdAndProjectId(ansattId, needle);
		if (ppi == null) {
			throw new RuntimeException("Ingen pivot funnet for ansatt " + ansattId + " og prosjekt " + needle);
		}

		// set hours
		int hours = TextInput.readInt("Legg til antall timer:");
		if(ppi.getAntallTimer() == null) {
            ppi.setAntallTimer(0);
		}
		ppi.setAntallTimer(ppi.getAntallTimer() + hours);
		DatabaseDAO.saveEntity(ppi, AnsattProsjektPivot::getId);

		System.out.println("Timer lagt til for ansatt i prosjekt");
		System.out.println(ppi);
	}
	

	
	/*
	 * main method
	 */	
	
	public static void handleDatabaseDemoData() {
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
	}

	public static void main(String[] args) {
		// handle demo data
		handleDatabaseDemoData();
		
		// create menu items
		Menu.init();
		
		// print menu
		Menu.print();

		// close entity manager factory
		StaticEMF.close();
	}

}
