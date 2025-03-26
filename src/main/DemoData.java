package main;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import Entities.Ansatt;
import Entities.AnsattProsjektPivot;
import Entities.Avdeling;
import Entities.Prosjekt;

public class DemoData {
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
		var em = Main.emf.createEntityManager();
		em.getTransaction().begin();
		em.createNativeQuery("TRUNCATE TABLE AnsattProsjektPivot, Avdeling, Ansatt, Prosjekt CASCADE").executeUpdate();
		//em.createNativeQuery("DELETE FROM Avdeling CASCADE").executeUpdate();		
		//em.createNativeQuery("DELETE FROM Ansatt CASCADE").executeUpdate();
		//3em.createNativeQuery("DELETE FROM Prosjekt CASCADE").executeUpdate();		
		em.getTransaction().commit();
		em.close();
		
		Main.printVerbose("-- Old database data cleared");
	}
	
	
	/*
	 * method to clear old database data and insert demo data
	 */
	
	public static void createDemoData() {			
		String fornavn[] = {"Arne", "Kari", "Per", "Ola", "Knut"};
		String etternavn[] = {"Hansen", "Olsen", "Pettersen", "Nilsen", "Knutson"};
		String stilling[] = {"Sjef", "Vaskehjelp", "Kokk", "Servitør", "Bartender"};
		String rolle[] = {"Leder", "Assistent", "Sekretær", "Arbeider", "IT-ansvarlig"};
		
		var em = Main.emf.createEntityManager();
		
		// sett inn avdeling
		Avdeling avdeling1 = new Avdeling();
		avdeling1.setNavn("Odontologi");
		avdeling1.setLeder(null);
		Main.saveEntity(avdeling1, Avdeling::getId);
		
		Avdeling avdeling2 = new Avdeling();
		avdeling2.setNavn("Pediatri");
		avdeling2.setLeder(null);
		Main.saveEntity(avdeling2, Avdeling::getId);
		
		Avdeling avdeling3 = new Avdeling();
		avdeling3.setNavn("Radiologi");
		avdeling3.setLeder(null);
		Main.saveEntity(avdeling3, Avdeling::getId);
				
		
		
		// sett inn prosjekter
		Prosjekt prosjekt1 = new Prosjekt();
		prosjekt1.setNavn("Kårstø");
		prosjekt1.setBeskrivelse("Bygging av nytt anlegg");
		Main.createEntity(prosjekt1);
		
		Prosjekt prosjekt2 = new Prosjekt();
		prosjekt2.setNavn("Mongstad");
		prosjekt2.setBeskrivelse("Utvikling av eksisterende anlegg");
		Main.createEntity(prosjekt2);
		

		// sett inn ansatte
		for(int i=0; i<fornavn.length; i++) {
			Ansatt a1 = new Ansatt();
			a1.setFornavn(fornavn[i]);
			a1.setEtternavn(etternavn[i]);
			a1.setBrukernavn(fornavn[i].toLowerCase() + etternavn[i].substring(0,1).toLowerCase());
			a1.setAnsettelseDato(createRandomDate());
			a1.setStilling(stilling[i]);
			a1.setLoennPerMaaned((float) 550_000 + (int) (Math.random() * 300_000));
			a1.setAvdeling(avdeling1);
			Main.createEntity(a1);
		}
		
		avdeling1.setLeder(AnsattDAO.findOne());
		Main.saveEntity(avdeling1, Avdeling::getId);
		
		// sett inn ansatt-prosjekt koblinger
		List<Ansatt> ansattList = Main.getAnsattList();
		for (int i = 0; i < ansattList.size(); i++) {
			AnsattProsjektPivot app = new AnsattProsjektPivot();
			app.setAnsatt(ansattList.get(i));
			app.setProsjekt(Math.random() > 0.5 ? prosjekt1 : prosjekt2);
			app.setAntallTimer((int) (Math.random() * 100));
			app.setRolle(rolle[i]);
			Main.createEntity(app);
		}
		
		//em.flush();
		em.close();
		
		Main.printVerbose("-- New demo data inserted");
	}
}
