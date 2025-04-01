package testing;

public class EntityFormatter {
	static boolean PRETTY_PRINT = true;
	
	public static String formatEntity(String name, Object... objects) {
		var sb = new StringBuilder(1024);
		
		if (PRETTY_PRINT) {
            sb.append(name);
            sb.append(" [");
            sb.append(formatPropertyArray(objects));
        	sb.append("\n");            
            sb.append("]");
        }else {
            sb.append(name);
            sb.append(" [");
            sb.append(formatPropertyArray(objects));
            sb.append("]");        	
        }
		return sb.toString();
	}
	
	public static String formatPropertyArray(Object... objects) {
		var sb = new StringBuilder(1024);
		
		if (PRETTY_PRINT) {
            for(int i=0; i<objects.length; i+=2) {
            	if(i > 0) {
                	sb.append(",");
            	}
            	
            	sb.append("\n");            	
            	sb.append("\t");
            	sb.append(objects[i + 0]);
            	sb.append(": ");
            	sb.append(objects[i + 1]);
            }
        }else {
            for(int i=0; i<objects.length; i+=2) {
            	if(i > 0) {
                	sb.append(",");
            	}
            	
            	sb.append(objects[i + 0]);
            	sb.append("=");
            	sb.append(objects[i + 1]);
            }       	
        }
		return sb.toString();
	}
	
}
