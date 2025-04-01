package main;

import java.time.LocalDateTime;
import java.util.List;

import DAO.AnsattDAO;
import DAO.AnsattProsjektPivotDAO;
import DAO.AvdelingDAO;
import DAO.ProsjektDAO;
import Entities.Ansatt;
import Entities.AnsattProsjektPivot;
import Entities.Avdeling;
import Entities.Prosjekt;

/*
 * menu action implementations
 */

public class MenuActionImpl {		
	public static void action_employee_list() { 
		DatabaseDAO.printAnsattList(); 
	}
	
	public static void action_employee_find_by_id(){
		int needle = TextInput.readInt("Skriv inn ansatt-id:");					
		var employee = AnsattDAO.findById(needle);
		DatabaseDAO.printItemFoundMessage(employee);
		
		int n = employee.getProsjekter().size();
		System.out.println("Prosjekter (" + n + "):");
		for (int i = 0; i < n; i++) {
			System.out.println("prosjekt: " + employee.getProsjekter().get(i));
		}
		System.out.println();
	}
	
	public static void action_employee_find_by_username(){
		String needle = TextInput.readLine("Skriv inn ansatt-brukernavn:");					
		var emp = AnsattDAO.findByBrukernavn(needle);
		DatabaseDAO.printItemFoundMessage(emp);
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
			
			// read input and find avdeling untill suitable is found
			int avdelingId = TextInput.readInt("Velg en avdeling (id):");
			Avdeling avdeling = AvdelingDAO.findById(avdelingId);
			if (avdeling != null) {
				newEmp.setAvdeling(avdeling);						
				break;
			}
			
			System.out.println("Ingen avdeling funnet med id " + avdelingId + ", prøv igjen");
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
				if(emp.erAvdelingsLeder()) {
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
		
		// dont allow to change department for leader
		if(ansatt.erAvdelingsLeder()) {
            throw new RuntimeException("Kan ikke endre avdeling for en leder");
		}
		
		// update ansatt
		ansatt.setAvdeling(avdeling);
		DatabaseDAO.saveEntity(ansatt, Ansatt::getId);
		
		// update avdeling
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
		
		// dont allow to change department for lead
		if(newLeader.erAvdelingsLeder()) {
			throw new RuntimeException("Kan ikke endre avdeling for en leder");
		}		
	
		// update avdeling
		avdeling.setLeder(newLeader);
		DatabaseDAO.saveEntity(avdeling, Avdeling::getId);
		
		// update ansatt
		newLeader.setAvdeling(avdeling);
		DatabaseDAO.saveEntity(newLeader, Ansatt::getId);
	}

	public static void action_department_find_by_id(){
		int needle = TextInput.readInt("Skriv inn avdeling-id:");					
		var item = AvdelingDAO.findById(needle);
		DatabaseDAO.printItemFoundMessage(item);	
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
		var projectRef = ProsjektDAO.findById(needle);
		if (projectRef == null) {
			throw new RuntimeException("Ingen prosjekt funnet med id " + needle);
		}
		
		System.out.println("# Detaljer for prosjekt:");		
		System.out.println(projectRef);
		System.out.println();		
		
		int totalHours = 0;
		var deltagere = projectRef.getDeltagere();			
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
		DatabaseDAO.printItemFoundMessage(item);
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
		
		ansatt.getProsjekter().add(ppi);
		DatabaseDAO.saveEntity(ansatt, Ansatt::getId);
		
		project.getDeltagere().add(ppi);		
		DatabaseDAO.saveEntity(project, Prosjekt::getId);
		
		System.out.println("Ansatt lagt til i prosjekt");
		System.out.println(ppi);
	}
	
	public static void action_project_add_participant_hours_by_project_id() {
		// find project
		int projectId = TextInput.readInt("Skriv inn prosjekt-id:");
		var project = ProsjektDAO.findById(projectId);
		if (project == null) {
			throw new RuntimeException("Ingen prosjekt funnet med id " + projectId);
		}

		// find ansatt
		DatabaseDAO.printAnsattList();
		int ansattId = TextInput.readInt("Skriv inn ansatt-id:");
		var ansatt = AnsattDAO.findById(ansattId);
		if (ansatt == null) {
			throw new RuntimeException("Ingen ansatt funnet med id " + ansattId);
		}

		// find pivot
		AnsattProsjektPivot ppi = AnsattProsjektPivotDAO.findOneByAnsattIdAndProjectId(ansattId, projectId);
		if (ppi == null) {
			throw new RuntimeException("Ingen pivot funnet for ansatt " + ansattId + " og prosjekt " + projectId);
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
}
