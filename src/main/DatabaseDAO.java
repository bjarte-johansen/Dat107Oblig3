package main;

import java.util.List;
import java.util.function.Function;

import Entities.Ansatt;
import Entities.AnsattProsjektPivot;
import Entities.Avdeling;
import Entities.Prosjekt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;


public class DatabaseDAO{

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
	 * @param clazz
	 * @return List<T>
	 */
	
	public static <T> List<T> findAll(Class<T> clazz){
		EntityManager em = StaticEMF.getNewEM();
		TypedQuery<T> query = em.createQuery("SELECT o FROM " + clazz.getSimpleName() + " o", clazz);
		List<T> items = query.getResultList();
		em.close();
		return items;
	}
	
	
	/**
	 * specific way to get list of Ansatt 
	 */
	
	public static List<Ansatt> getAnsattList(){	return findAll(Ansatt.class); }	
	public static List<Prosjekt> getProsjektList(){	return findAll(Prosjekt.class); }		
	public static List<Avdeling> getAvdelingList(){ return findAll(Avdeling.class); }	
	public static List<AnsattProsjektPivot> getAnsattProsjektPivotList(){ return findAll(AnsattProsjektPivot.class); }	
	
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
		
		List<?> items = DatabaseDAO.findAll(clazz);		
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
}
