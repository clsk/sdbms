package query;

import fs.*;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map.Entry;

public class Select extends Query {
    public Select(HeapFile _hf)
    {
        hf = _hf;
    }

    private HeapFile hf;
    private String [] filters = null;
    private String [] conditions = null;

    @Override
    public void execute()
    {
    	System.out.println("\nSELECT statement on table => " + hf.getSchema().getSchemaName().toUpperCase() + ".");
        System.out.println("Virtual Table Generated.\n");
    	
    	System.out.print("RID");
        int len = 0;
        int i = 0;
        
    	for (int j = 0; j < filters.length; j++){
    		System.out.print(" | " + Utilities.padRight(filters[j], hf.getSchema().getFieldPair(filters[j]).size));
            len += hf.getSchema().getFieldPair(filters[j]).size;
            i++;
    	}    	
    	System.out.println(" |");
    	
    	//	Separador de Header
        char[] array = new char[(i*3) + len + 5];
        Arrays.fill(array, '-');
        System.out.println(new String(array));
        
        // Print Records
        ArrayList<Pair<RID, String>> records =  hf.getAllRecords();
        for (Pair<RID, String> record : records)
        {
            Record r = Record.valueOf(hf.getSchema(), record.getValue());
            System.out.print(record.getKey());
            String [] data = r.getData();
            for (int j = 0; j < filters.length; j++){
            	//	Printing record by schema field position
            	System.out.print(" | " + data[hf.getSchema().getFieldPos(filters[j])]);
            }
            data = null;
            System.out.println(" |");
        }
    }
    
    /*
     *	Metodo de Verificacion de Atributos y la Projeccion dentro
     *	del Heap File creado.
     */
    private boolean CheckFields (){
    	Set <String> attributes = hf.getSchema().getFields().keySet();
    	
    	for (int i = 0; i < filters.length; i++) {
    		if (!attributes.contains(filters[i])){
    			System.out.println("La Columna => " + filters[i] + ". No esta definida en la tabla => " + hf.getSchema().getSchemaName() + ".");
    			return false;
    		}
    	}
    	
    	return true;    	
    }
    
    private void SetFilters (String columns) {
    	filters = columns.split("\\s+");
    }
    
    private void SetWhereConditions(String [] _conditions){
    	conditions = _conditions;
    }
    
    private boolean CheckFields (boolean letsCheck) {
    	Set <String> attributes = hf.getSchema().getFields().keySet();
    	
    	for (int i = 0; i < conditions.length; i = i + 2) {
    		if (!attributes.contains(conditions[i])) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    static final Pattern SELECT_PATTERN = Pattern.compile("select\\s+\\*\\s+from\\s+(\\w+);?", Pattern.CASE_INSENSITIVE);
    static final Pattern SELECT_SPECIFIC_COLUMNS = Pattern.compile("select\\s+\\w+(,(\\s|)\\w+|)+?\\s+from\\s+(\\w+);?", 
    		Pattern.CASE_INSENSITIVE);
    static final Pattern WHERE_CLAUSE = Pattern.compile("where\\s([\\w]+\\s(<|>|<=|>=|&&|\\|\\|!=|==)\\s)+[\\w]+;$", 
    		Pattern.CASE_INSENSITIVE);

    static public Select parseSelect (String line, BufferedReader reader)
    {
        String [] fieldsNoperators = null;
        String lineAux;
        String filters = "";
        HeapFile hf= null;
    	
    	if (line.contains("where")) {
    		lineAux = line.replaceAll("\\s+where.*", "");
        	line = line.replaceAll("select.*\\s+where\\s+", "");
        	line = line.replaceAll(";", "");
        	line = line.toLowerCase();
        }
    	else {
    		lineAux = line;
    	}
        
        Matcher m = SELECT_PATTERN.matcher (lineAux);
        Matcher n = SELECT_SPECIFIC_COLUMNS.matcher(lineAux);
        Matcher compare = WHERE_CLAUSE.matcher(line);
        
        if (!m.matches() && !n.matches() && !compare.matches())
        {
        	System.out.println(line);
            System.out.println("Error matching SELECT statement");
            return null;           
        }

        if (m.matches()) {
        	hf = SystemCatalog.getInstance().getTable(m.group(1));
        	
        	if (hf == null) {      	
        		System.out.println("Select Error Table " + m.group(1).toUpperCase() + " does not exist!");
        		return null;
        	}
        	
        	for (Entry<String, FieldValue> column : hf.getSchema().getSortedFields()){
        		filters += column.getKey().toString() + " ";
        	}       	
        }
        
        if (n.matches()) {
        	hf = SystemCatalog.getInstance().getTable(n.group(3));
        	        	
        	if (hf == null){       	
        		System.out.println("Select Error: Table " + m.group(1).toUpperCase() + " does not exist!");
        		return null;
        	}
        	
        	/*
        	 *	Limpieza de atributos
        	 *	Las siguientes lineas eliminan del SELECT
        	 *	la parte inicial 'select' y la parte final 'from'
        	 */
        	filters = line.replaceAll("(^select\\s+)", "");
        	filters = filters.replaceAll("\\s+from.*", "");
        	filters = filters.replaceAll(",|\\s+", " ");        	
        }
        
        if (compare.matches()) {
        	fieldsNoperators = line.split("\\s+");      	
        }
        
        /*
    	 * Declaracion de Select (HeapFile)
    	 */
    	Select _nuevo = new Select(hf);
    	
    	//Lista de atributos llevada a minusculas.
    	_nuevo.SetFilters(filters.toLowerCase());
    	
    	//Checking los filtros de la sentencia WHERE
    	if (fieldsNoperators != null && compare.matches()){
    		//Establecimiento de las condiciones de busquedad de l WHERE
    		_nuevo.SetWhereConditions(fieldsNoperators);
    	}
    	else {
    		System.out.println("ERROR en la sentencia WHERE.");
    		return null;
    	}
    	
    	if (_nuevo.CheckFields() && _nuevo.CheckFields(true)){
    		return _nuevo;
    	}
    	else
    		return null;
    }
}
