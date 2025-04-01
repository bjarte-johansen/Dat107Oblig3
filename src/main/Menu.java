package main;

import java.util.LinkedHashMap;
import java.util.Map;


/*
 * menu item class
 */

@FunctionalInterface
interface MenuAction{
	void execute();
};



/*
 * menu item base class
 */

class MenuItemBase{
	private int key;
	private String text;
    private MenuAction action;
    
    public MenuItemBase(int key, String text, MenuAction action) {
    	this.key = key;
        this.text = text;
        this.action = action;
    }
    
    public void execute() {
    	try {
    		if(action != null) {
    			action.execute();
    		}
			System.out.println();    		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    }
    
	public String getText() {
		return text;
	}
	public int getKey() {
		return key;
	}
	public MenuAction getAction() {
		return action;
	}
    
    public String toString() {
        return String.format("%-4s ", key + ".") + text;
    }
};



/*
 * menu class
 */

public class Menu {
	public static class MenuItem extends MenuItemBase{
		public MenuItem(int key, String text, MenuAction action) {
            super(key, text, action);
        }
    };
    
	// menu items
	private static Map<Integer, MenuItem> menuItems = new LinkedHashMap<>();
	private static int menuItemInvalidIndex = -1;
	
    
	/*
	 * menu methods
	 */
		
	public static void print() {
		// print menu header
		System.out.println("-".repeat(20));
		System.out.println("     MENY");
		System.out.println("-".repeat(20));
		System.out.println();

		// print menu items
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
		
		// read input
		int choice = TextInput.readInt("Tast inn ditt valg:");
		
		// find menu item
		MenuItem selectedMenuItem = menuItems.get(choice);
		
		// execute menu item action
		if(selectedMenuItem == null) {
			System.out.println("Ugyldig valg");
			System.out.println();
		}else if(selectedMenuItem.getAction() == null){
			System.out.println("Program avsluttet");			
		}else {
			try {
				selectedMenuItem.execute();
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
			
			// wait untill user presses enter
			TextInput.waitUntillInput();
	
			// print menu again
			print();	
		}
		

	}
	
	public static void addMenuItem(int index, String text, MenuAction action) {				
		if(index < 0) {
			menuItems.put(menuItemInvalidIndex--, new MenuItem(index >= 0 ? index : -1, text, action));
		}else {		
			menuItems.put(index, new MenuItem(index, text, action));
		}
	}
	
	public static void init() {
		// addMenuItem(choice, "title", actionMethodReference);
		addMenuItem(1, "Ansatt, list", MenuActionImpl::action_employee_list);	
		addMenuItem(2, "Ansatt, finn etter id", MenuActionImpl::action_employee_find_by_id);	
		addMenuItem(3, "Ansatt, finn etter brukernavn", MenuActionImpl::action_employee_find_by_username);
		addMenuItem(4, "Ansatt, endre stilling og lønn", MenuActionImpl::action_set_position_and_salary);
		addMenuItem(5, "Ansatt, legg til", MenuActionImpl::action_employee_add);
		addMenuItem(6, "Ansatt, finn etter avdeling", MenuActionImpl::action_employee_find_by_department);		
		addMenuItem(7, "Ansatt, endre avdeling", MenuActionImpl::action_employee_update_department);			
		addMenuItem(-1, null, null);
		addMenuItem(8, "Avdeling, list", MenuActionImpl::action_department_list);
		addMenuItem(9, "Avdeling, finn etter id", MenuActionImpl::action_department_find_by_id);		
		addMenuItem(10, "Avdeling, legg til", MenuActionImpl::action_department_add);		
		addMenuItem(-1, null, null);		
		addMenuItem(11, "Prosjekt, list", MenuActionImpl::action_project_list);
		addMenuItem(12, "Prosjekt, finn etter id", MenuActionImpl::action_project_find_by_id);
		addMenuItem(13, "Prosjekt, legg til", MenuActionImpl::action_project_add);
		addMenuItem(14, "Prosjekt, legg til ansatt", MenuActionImpl::action_project_add_participant_by_project_id);
		addMenuItem(15, "Prosjekt, legg til timer", MenuActionImpl::action_project_add_participant_hours_by_project_id);
		addMenuItem(16, "Prosjekt, vis detaljer etter id", MenuActionImpl::action_project_list_details);
		addMenuItem(-1, null, null);
		addMenuItem(0, "Avslutt", null);
	}
	    
}
