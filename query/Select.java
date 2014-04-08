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

    @Override
    public void execute()
    {
        System.out.print("RID");
        int len = 0;
        int i = 0;
        
        if (filters != null) {
        	
        	for (int j = 0; j < filters.length; j++){
        		System.out.print(" | " + Utilities.padRight(filters[j], hf.getSchema().getFieldPair(filters[j]).size));
                len += hf.getSchema().getFieldPair(filters[j]).size;
                i++;
        	}
        	
        	System.out.println(" |");
            char[] array = new char[(i*3) + len + 5];
            Arrays.fill(array, '-');
            System.out.println(new String(array));
            
            // Print Records
            ArrayList<Pair<RID, String>> records =  hf.getAllRecords();
            for (Pair<RID, String> record : records)
            {
                Record r = Record.valueOf(hf.getSchema(), record.getValue());
                System.out.print(record.getKey());
                /*
                 * TODO: Presentar los records dependiendo de la columna.
                 */
                for (String column : r.getData())
                {
                    System.out.print(" | " + column);
                }
                System.out.println(" |");
            }
        }
        else {
        	// Print header
            for (Entry<String, FieldValue> column : hf.getSchema().getSortedFields())
            {
                System.out.print(" | " + Utilities.padRight(column.getKey(), column.getValue().size));
                len += column.getValue().size;
                i++;
            }
            
            System.out.println(" |");
            char[] array = new char[(i*3) + len + 5];
            Arrays.fill(array, '-');
            System.out.println(new String(array));

            // Print Records
            ArrayList<Pair<RID, String>> records =  hf.getAllRecords();
            for (Pair<RID, String> record : records)
            {
                Record r = Record.valueOf(hf.getSchema(), record.getValue());
                System.out.print(record.getKey());
                for (String column : r.getData())
                {
                    System.out.print(" | " + column);
                }
                System.out.println(" |");
            }        	
        }
    }
    
    /*
     *	Metodo de Verificacion de Atributos y la Projeccion dentro
     *	del Heap File creado.
     */
    boolean CheckFields (){
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
    
    static final Pattern SELECT_PATTERN = Pattern.compile("select\\s+\\*\\s+from\\s+(\\w+);?", Pattern.CASE_INSENSITIVE);
    static final Pattern SELECT_SPECIFIC_COLUMNS = Pattern.compile("select\\s+\\w+(,(\\s|)\\w+|)+?\\s+from\\s+(\\w+);?", 
    		Pattern.CASE_INSENSITIVE);

    static public Select parseSelect (String line, BufferedReader reader)
    {

        Matcher m = SELECT_PATTERN.matcher (line);
        Matcher n = SELECT_SPECIFIC_COLUMNS.matcher(line);
        
        if (!m.matches() && !n.matches())
        {
        	System.out.println(line);
            System.out.println("Error matching SELECT statement");
            return null;           
        }

        if (m.matches()) {
        	HeapFile hf = SystemCatalog.getInstance().getTable(m.group(1));
        	if (hf == null)       	
        		System.out.println("Select Error: Table " + m.group(1) + " does not exist!");
        	return new Select(hf);
        }
        
        if (n.matches()) {
        	HeapFile hf = SystemCatalog.getInstance().getTable(n.group(3));
        	        	
        	if (hf == null)       	
        		System.out.println("Select Error: Table " + m.group(1) + " does not exist!");
        	
        	/*
        	 *	Limpieza de atributos
        	 *	Las siguientes lineas eliminan del SELECT
        	 *	la parte inicial 'select' y la parte final 'from'
        	 */
        	String filters = line.replaceAll("(^select\\s+)", "");
        	filters = filters.replaceAll("\\s+from.*", "");
        	filters = filters.replaceAll(",|\\s+", " ");
        	
        	/*
        	 * Declaracion de HeapFile
        	 */
        	Select _nuevo = new Select(hf);
        	
        	//	Lista de atributos llevada a minusculas.
        	_nuevo.SetFilters(filters.toLowerCase());
        	
        	if (_nuevo.CheckFields()){
        		return _nuevo;
        	}
        	else
        		return null;
        }
        
        return new Select(null);
    }
}
