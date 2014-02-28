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
    
    /*
     * Funcion para Agregar un atributo, indicando su nombre, posicion y tama�o.
     */    
    public void addField (String _name, Integer _pos, Integer _size) {
    	if (!fields.containsKey(_name)){
    		Integer var = _size.intValue();
        	Integer pos = _pos.intValue();
        	Pair <Integer, Integer> aux = new Pair <Integer, Integer> (pos, var);    	
        	fields.put(_name.toLowerCase(), aux);
        	addRecordLength(var);
    	}
    	else {
    		System.out.println("Este atributo ya existe en el Schema");
    	}
    }  
 
    /*
     * Funcion para Agregar un atributo, indicando su nombre y Pair <Integer, Integer>
     * el cual indica la posici�n y el tama�o respectivamente
     */  
    public void addField(String _name, Pair <Integer, Integer> size)
    {
    	if (!fields.containsKey(_name)){
    		Integer var = size.getValue().intValue();
        	Integer pos = size.getKey().intValue();
        	Pair <Integer, Integer> aux = new Pair <Integer, Integer> (pos, var);
        	fields.put(_name.toLowerCase(), aux);
        	addRecordLength(var);
    	}
    	else {
    		System.out.println("Este atributo ya existe en el Schema");
    	}
    }
    
    /*
     * Funcion de Retorno del Nombre del Schema.
     */
    public String getSchemaName() {
 		return name;
 	}
    
    /*
     * Funcion de Retorno del Nombre del Atributo.
     */
    public String getFieldName(Integer position) {
    	String auxName = null;    	
    	Integer auxPos;
    	
    	if (!fields.isEmpty()){
    		for (String n : fields.keySet()){
        		auxPos = fields.get(n).getKey();
        		if (position.intValue() == auxPos) {
        			auxName = n;
        			break;
        		} 
        	} 	    	    		
    	}
    	return auxName;	
    }
    
    /*
     * Funcion de Retorno de la Posici�n y Tama�o del Atributo.
     */
    public Pair <Integer, Integer> getFieldPair (String _name){
    	Pair <Integer, Integer> auxPos = null;
    	
    	if (!fields.isEmpty()){
    	   	 auxPos = fields.get(_name);
    	}
    	
    	return auxPos;
    }
    
    /*
     * Funcion de Retorno de la Posici�n del Atributo.
     */
    public Integer getFieldPos (String _name){
    	Integer auxPos = null;
    	
    	if (!fields.isEmpty()){
    	   	 auxPos = fields.get(_name).getKey();
    	}
    	
    	return auxPos;
    }
    
    /*
     * Funcion para Actualizar el Tama�o del Schema.
     */
    private void addRecordLength (int _value){
    	this.recordLength += _value;
    }
    
    /*
     * Funcion de Retorno de las Parejas de Posicion y Tama�o
     * de los Atributos.
     */
    public HashMap <String, Pair <Integer, Integer>> getFields (){
    	return fields;
    }
    
    /*
     * Funcion de Retorno del Tama�o del Schema.
     */
    public int getRecordLength (){
    	return recordLength;
    }
    
	private HashMap <String, Pair <Integer, Integer>> fields;
    private String name = null;
    private int recordLength = 0;
}