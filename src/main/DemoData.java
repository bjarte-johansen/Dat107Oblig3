package main;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import DAO.AnsattDAO;
import Entities.Ansatt;
import Entities.AnsattProsjektPivot;
import Entities.Avdeling;
import Entities.Prosjekt;

public class DemoData {
	
	// true if verbose output should be printed
	public static boolean VERBOSE_COMMANDS = true;
	
	
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
	 * method to create a random date between 2000 and 2025
     */

	public static LocalDateTime createRandomDate() {
		LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59);

		// Convert to epoch seconds
		long startSec = start.toEpochSecond(ZoneOffset.UTC);
		long endSec = end.toEpochSecond(ZoneOffset.UTC);

		// Generate random epoch second
		long randomSec = ThreadLocalRandom.current().nextLong(startSec, endSec);

		// Convert back to LocalDateTime
		LocalDateTime randomDateTime = LocalDateTime.ofEpochSecond(randomSec, 0, ZoneOffset.UTC);
		
		return randomDateTime;
	}
	
	

	/*
	 * method to clear old database data
	 */
	
	public static void deleteDemoData() {
		var em = StaticEMF.getNewEM();
		em.getTransaction().begin();
		
		// truncate tables, with CASCADE
		em.createNativeQuery("TRUNCATE TABLE AnsattProsjektPivot, Avdeling, Ansatt, Prosjekt CASCADE").executeUpdate();


		// reset sequences
		em.createNativeQuery("ALTER SEQUENCE AnsattProsjektPivot_id_seq RESTART WITH 1;").executeUpdate();
		em.createNativeQuery("ALTER SEQUENCE Avdeling_id_seq RESTART WITH 1;").executeUpdate();
		em.createNativeQuery("ALTER SEQUENCE Ansatt_id_seq RESTART WITH 1;").executeUpdate();
		em.createNativeQuery("ALTER SEQUENCE Prosjekt_id_seq RESTART WITH 1;").executeUpdate();
		
		em.getTransaction().commit();
		em.close();
		
		printVerbose("-- Old database data cleared");
	}
	
	
	/*
	 * method to clear old database data and insert demo data
	 */
	
    public static void addEmployeeProjectParticipation(Prosjekt project, Ansatt ansatt, String rolle, int antallTimer) {
		AnsattProsjektPivot pivot = new AnsattProsjektPivot();
		pivot.setAnsatt(ansatt);
		pivot.setProsjekt(project);
		pivot.setAntallTimer(antallTimer);
		pivot.setRolle(rolle);
		DatabaseDAO.saveEntity(pivot, null);

		// update entity
		ansatt.getProsjekter().add(pivot);
		DatabaseDAO.saveEntity(ansatt, Ansatt::getId);
		
		// update entity
		project.getDeltagere().add(pivot);
		DatabaseDAO.saveEntity(project, Prosjekt::getId);
    }
	
	public static void createDemoData() {			
		int antallAnsatte = 10;
		
		//String fornavn[] = {"Arne", "Bjarne", "Cato", "Dolly", "Endre", "Frode", "Gunnar", "Hans", "Ivar", "Jens"};
		//String etternavn[] = {"Arntzen", "Berge", "Chad", "Dimple", "Endrsen", "Foss", "Gundersen", "Hansen", "Iversen", "Jensen"};
		String stilling[] = {"Sjef", "Vaskehjelp", "Kokk", "Servitør", "Bartender", "Sjef", "Vaskehjelp", "Kokk", "Servitør", "Bartender"};
		String rolle[] = {"Leder", "Assistent", "Sekretær", "Arbeider", "IT-ansvarlig", "Vaskehjelp", "Kokk", "Servitør", "Bartender", "Sjef"};
				
		var em = StaticEMF.getNewEM();
		
		// sett inn avdeling
		Avdeling avdeling1 = new Avdeling();
		avdeling1.setNavn("Odontologi");
		avdeling1.setLeder(null);
		DatabaseDAO.saveEntity(avdeling1, Avdeling::getId);
		
		Avdeling avdeling2 = new Avdeling();
		avdeling2.setNavn("Pediatri");
		avdeling2.setLeder(null);
		DatabaseDAO.saveEntity(avdeling2, Avdeling::getId);
		
		Avdeling avdeling3 = new Avdeling();
		avdeling3.setNavn("Radiologi");
		avdeling3.setLeder(null);
		DatabaseDAO.saveEntity(avdeling3, Avdeling::getId);
		
		// setup avdelinger array to use for setting leder for each avdeling
		Avdeling avdelinger[] = new Avdeling[] {
			avdeling1, avdeling2, avdeling3
			};

 
		// sett inn prosjekter
		Prosjekt prosjekt1 = new Prosjekt();
		prosjekt1.setNavn("Kårstø");
		prosjekt1.setBeskrivelse("Bygging av nytt anlegg");
		DatabaseDAO.saveEntity(prosjekt1, null);
		
		Prosjekt prosjekt2 = new Prosjekt();
		prosjekt2.setNavn("Mongstad");
		prosjekt2.setBeskrivelse("Utvikling av eksisterende anlegg");
		DatabaseDAO.saveEntity(prosjekt2, null);
		
		Prosjekt prosjekt3 = new Prosjekt();
		prosjekt3.setNavn("Sotra");
		prosjekt3.setBeskrivelse("Vedlikeholdsarbeid");
		DatabaseDAO.saveEntity(prosjekt3, null);		
		
		Prosjekt prosjekter[] = new Prosjekt[] {
			prosjekt1, prosjekt2, prosjekt3
		};
		

		// sett inn ansatte
		for(int i=0; i<antallAnsatte; i++) {
			Avdeling avd = avdelinger[i % avdelinger.length]; 
			
			Ansatt a1 = new Ansatt();
			a1.setFornavn(String.valueOf((char)((int) 'a' + i)));
			a1.setEtternavn(String.valueOf((char)((int) 'a' + i)));
			a1.setBrukernavn(a1.getFornavn().toLowerCase().charAt(0) + a1.getEtternavn().substring(0,1).toLowerCase());
			a1.setAnsettelseDato(createRandomDate());
			a1.setStilling(stilling[i]);
			a1.setLoennPerMaaned((float) 1001 + i);
			avd.addAnsatt(a1);
			DatabaseDAO.saveEntity(a1, null);
			
			//avdelinger[i % avdelinger.length].ansatte.add(a1);
			//DatabaseDAO.saveEntity(avdelinger[i % avdelinger.length], Avdeling::getId);
		}
		
		// sett leder for hver avdeling
		for (Avdeling avd : avdelinger) {
			avd.setLeder(AnsattDAO.findByAvdelingId(avd.getId()).get(0));
			DatabaseDAO.saveEntity(avd, Avdeling::getId);
		}
		
		
		// sett inn ansatt-prosjekt koblinger
		List<Ansatt> ansattList = DatabaseDAO.getAnsattList();
		
		for (int i = 0; i < ansattList.size(); i++) {
			Ansatt relAnsatt = ansattList.get(i);
			Prosjekt relProsjekt = prosjekter[i % prosjekter.length];
			
			addEmployeeProjectParticipation(relProsjekt, relAnsatt, rolle[i], 10 + (int) (Math.random() * 10));		
			
			// randomly add to another project
			if(Math.random() >= 0.5) {				
				relProsjekt = prosjekter[(i + 1) % prosjekter.length];
				addEmployeeProjectParticipation(relProsjekt, relAnsatt, rolle[i], (int) (Math.random() * 10));
			}
		}
		
		// clear persistence context
		//em.clear();	
		
		// 
		em.close();
		
		printVerbose("-- New demo data inserted");
	}
}
