package main;

/*
 * main class
 */


public class Main {	
	/*
	 * handle database demo data
	 */
	
	public static void handleDatabaseDemoData() {
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
		// handle data (reset + insert)
		handleDatabaseDemoData();
		
		// create menu items
		Menu.init();
		
		// print menu
		Menu.print();

		// close entity manager factory
		StaticEMF.close();
	}

}
