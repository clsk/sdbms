package fs;

import java.util.HashMap;
import java.lang.Integer;

public class Schema
{
    public Schema(String name)
    {
        fields = new HashMap <String, Pair <Integer, Integer>>();
        this.name = name;
    }

    public void addField(String name, Pair <Integer, Integer> size)
    {
    	Integer var = size.getValue().intValue() * 2;
    	Integer pos = size.getKey().intValue();
    	Pair <Integer, Integer> aux = new Pair <Integer, Integer> (pos, var);
    	fields.put(name, aux);

    	var = null;
    	pos = null;
    	aux = null;
    }

    public void addField (String _name, Integer _pos, Integer _size) {
    	Integer var = _size.intValue() * 2;
    	Integer pos = _pos.intValue();
    	Pair <Integer, Integer> aux = new Pair <Integer, Integer> (pos, var);    	
    	fields.put(name, aux);

    	var = null;
    	pos = null;
    	aux = null;
    }
    
    /*
     *	Funcion para evaluar la existencia del Field. 
     */
    private boolean checkField (){
    	return false;
    }
    
    public String getSchemaName() {
		return name;
	}

	private HashMap <String, Pair <Integer, Integer>> fields;
    private String name = null;
}