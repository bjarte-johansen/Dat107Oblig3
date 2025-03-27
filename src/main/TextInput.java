package main;

import java.util.Scanner;

public class TextInput {
	// scanner
	private static final Scanner scanner = new Scanner(System.in);	
	

	public static void waitUntillInput() {
		scanner.nextLine();		
	
        System.out.print("Press Enter to continue...");		
		
        scanner.nextLine();		
        
        System.out.print("\n\n\n");
    }
	
	
	/*
	 * read menu choice
	 */
	
	public static Integer readInt(String message) {
		if(message != null) {
			System.out.println(message);
			System.out.flush();
		}

		// skip invalid tokens, with error message
		while (!scanner.hasNextInt()) {
		    System.out.println("Invalid input. Try again:");
		    scanner.next(); 
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

		// skip first invalid token, without error message		
	    String line = scanner.nextLine();
	    if(line.trim().isEmpty()) {
	    	line = scanner.nextLine();
	    }

		// skip invalid tokens, with error message	    
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

		// skip invalid tokens, with error message		
		while (!scanner.hasNextFloat()) {
		    System.out.println("Invalid input. Try again:");
		    scanner.next(); 
		}
		
		float choice = scanner.nextFloat();
		System.out.println();
		return choice;
	}		
}
