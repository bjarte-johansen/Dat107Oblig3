package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import Entities.*;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

class DemoData{
	
}

public class Main {
	
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersistanceUnit");;
	
	
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
	
	public static <T> void persistObject(T obj) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(obj);
		em.getTransaction().commit();
		em.close();
	}
	
	public static void insertAnsattProsjektPivotData() {
		var em = emf.createEntityManager();
		
		// remove old		
		em.getTransaction().begin();
		em.createQuery("DELETE FROM AnsattProsjektPivot").executeUpdate();
		em.getTransaction().commit();
		
		// never called, doesnt do anything yet
	}
	
	public static void clearOldDatabaseData() {
		var em = emf.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("DELETE FROM AnsattProsjektPivot").executeUpdate();		
		em.createQuery("DELETE FROM Ansatt").executeUpdate();
		em.createQuery("DELETE FROM Avdeling").executeUpdate();
		em.createQuery("DELETE FROM Prosjekt").executeUpdate();		
		em.getTransaction().commit();
		em.close();
		
		System.out.println("-- Old database data cleared");
	}
	
	public static void insertAnsattList() {
		// clear old database data
		clearOldDatabaseData();
		System.out.println();
		
		
		String fornavn[] = {"Arne", "Kari", "Per", "Ola", "Knut"};
		String etternavn[] = {"Hansen", "Olsen", "Pettersen", "Nilsen", "Knutson"};
		String stilling[] = {"Sjef", "Vaskehjelp", "Kokk", "Servitør", "Bartender"};
		
		var em = emf.createEntityManager();
		
		
		Avdeling avdeling1 = new Avdeling();
		avdeling1.setNavn("Kronstad 2");
		avdeling1.setLederId(null);
		persistObject(avdeling1);
		
		Prosjekt prosjekt1 = new Prosjekt();
		prosjekt1.setNavn("Kårstø");
		prosjekt1.setBeskrivelse("Bygging av nytt anlegg");
		persistObject(prosjekt1);
		
		Prosjekt prosjekt2 = new Prosjekt();
		prosjekt2.setNavn("Mongstad");
		prosjekt2.setBeskrivelse("Utvikling av eksisterende anlegg");
		persistObject(prosjekt2);
		
		
		int index = 0;
		for(int i=0; i<fornavn.length; i++) {
			Ansatt a1 = new Ansatt();
			a1.setBrukernavn("bruker" + i);
			a1.setFornavn(fornavn[i]);
			a1.setEtternavn(etternavn[i]);
			a1.setBrukernavn(fornavn[i].toLowerCase() + etternavn[i].substring(0,1).toLowerCase());
			a1.setAnsettelseDato(createRandomDate());
			a1.setStilling(stilling[i]);
			a1.setLoennPerMaaned((float) 550_000 + (int) (Math.random() * 300_000));
			a1.setAvdeling(avdeling1);
			persistObject(a1);
		}
		
		List<Ansatt> ansattList = getAnsattList();
		for (int i = 0; i < ansattList.size(); i++) {
			AnsattProsjektPivot app = new AnsattProsjektPivot();
			app.setAnsatt(ansattList.get(i));
			app.setProsjekt(Math.random() > 0.5 ? prosjekt1 : prosjekt2);
			app.setAntallTimer((int) (Math.random() * 100));
			persistObject(app);
		}
		
		em.close();
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
	

	
	public static <T1> void printEntityList(List<T1> entities, Class<?> clazz) {
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
		
		List<?> avdelinger = em
			.createQuery("SELECT a FROM " + classRefName + " a", classRef)
			.getResultList();
		
		System.out.println("# Liste over " + classRefName + "(e/er/ere):");		
		
		// print faktisk liste
		printEntityList(avdelinger, classRef);
		System.out.println();
		
		em.close();
	}
	
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

	public static void main(String[] args) {
		insertAnsattList();
		
		EntityManager em = emf.createEntityManager();	
		
		printAvdelingList();
		printAnsattList();
		printProsjektList();
		printAnsattProsjektPivotList();
		
		/*
		List<?> items = getAnyEntityList(new Ansatt(), Ansatt.class);
		for (var a : items) {
			System.out.println(a.getClass().getSimpleName());
			System.out.println(a);
		}
		*/

		em.close();
		emf.close();
	}

}
