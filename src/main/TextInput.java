package main;

import java.util.Scanner;

public class TextInput {
	// scanner
	private static final Scanner scanner = new Scanner(System.in);	
	
	
	/*
	 * read menu choice
	 */
	
	public static Integer readInt(String message) {
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
	
	public static String readLine(String message) {
		if(message != null) {
			System.out.println(message);
			System.out.flush();
		}

	    String line = scanner.nextLine();
	    if(line.trim().isEmpty()) {
	    	line = scanner.nextLine();
	    }
	    
	    while (line.trim().isEmpty()) {
	        System.out.println("Invalid input. Try again:");
	        line = scanner.nextLine();
	    }

	    System.out.println();
	    return line;
	}	
	
	public static Float readFloat(String message) {
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
}
