package query;

import fs.*;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map.Entry;

class Operation
{
	public String op;
	public String lhs;
	public String rhs;
}

public class Select extends Query {
    public Select(HeapFile _hf)
    {
        hf = _hf;
    }

    private HeapFile hf;
    private String [] filters = null;
    private String [] conditions = null;
    
    public boolean compare(Operation ope, Record record)
    {
    	boolean val = record.getValueForField(ope.lhs).trim().equals(ope.rhs);
    	
    	if (ope.op.equals("==")) {
    		return val;
    	}
    	else {    		
    		return !val;
    	}
    }
    
    public boolean filter(Record r)
    {
    	boolean matches = true;
    	
    	if (conditions == null){
    		return true;
    	}
    	
    	if (conditions.length > 0)
    	{
    		Operation operation = new Operation();
    		
    		if (Pattern.matches("'.*'", conditions[0])){
    			conditions[0] = conditions[0].replaceAll("['']+", "");    			
    		}
    		if (Pattern.matches("'.*'", conditions[1])) {
    			conditions[1] = conditions[1].replaceAll("['']+", "");
    		}
    		if (Pattern.matches("'.*'", conditions[2])) {
    			conditions[2] = conditions[2].replaceAll("['']+", "");
    		}
    		operation.lhs = conditions[0];
    		operation.op = conditions[1];
    		operation.rhs = conditions[2];
    		matches = compare(operation, r);
    		for (int i = 3; i < conditions.length; i++)
    		{
    			String logicalOp = conditions[i];
    			operation.lhs = conditions[++i];
    			operation.op = conditions[++i];
    			operation.rhs = conditions[++i];
    			
    			if (logicalOp.equals("and"))
    			{	
    				boolean temp = compare (operation, r);
    				if (matches && temp) {
    					matches = temp;    					
    				}
    			}
    			else if (logicalOp.equals("or"))
    			{
    				if (matches)
    					continue;
    				else
    					matches = compare(operation, r);
    			}
    			else
    			{
    				System.out.println("Unexpected logical operator");
    				matches = false;
    			}
    		}
    	}
    	
  	
    	return matches;
    }

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
        
        // Print Record
        ArrayList<Pair<RID, String>> records =  hf.getAllRecords();
        for (Iterator<Pair<RID, String>> Iter = records.iterator(); Iter.hasNext() ;)
        {
        	Pair<RID, String> record = Iter.next();
        	Record r = Record.valueOf(hf.getSchema(), record.getValue());
        	if (!filter(r))
        		continue;
        	
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
    	
    	if (conditions == null) {
    		return true;
    	}
    	
    	Set <String> attributes = hf.getSchema().getFields().keySet();
    	
    	for (int i = 0; i < conditions.length; i ++) {
    		if (!Pattern.matches("'.*'", conditions[i]) && !Pattern.matches("<|>|<=|>=|and|or|!=|==", conditions[i])) {
    			if (!attributes.contains(conditions[i])) {
    				return false;
    			}
    		}
    	}
    	
    	return true;
    }
    
    static final Pattern SELECT_PATTERN = Pattern.compile("select\\s+\\*\\s+from\\s+(\\w+);?", Pattern.CASE_INSENSITIVE);
    static final Pattern SELECT_SPECIFIC_COLUMNS = Pattern.compile("select\\s+\\w+(,(\\s|)\\w+|)+?\\s+from\\s+(\\w+);?", 
    		Pattern.CASE_INSENSITIVE);
    static final Pattern WHERE_CLAUSE = Pattern.compile("([\\w]+\\s(<|>|<=|>=|AND|OR|!=|==)\\s)+[\\w]+", 
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
        String temp = line.replaceAll("['']+", "");
        Matcher compare = WHERE_CLAUSE.matcher(temp);
        
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
        	filters = lineAux.replaceAll("(^select\\s+)", "");
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
    	
    	if (!_nuevo.CheckFields(true)){
    		return null;
    	}
    	
    	if (_nuevo.CheckFields()){
    		return _nuevo;
    	}
    	else
    		return null;
    }
}
