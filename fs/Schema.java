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
     * Funcion para Agregar un atributo, indicando su nombre, posicion y tamaño.
     */    
    public void addField (String _name, Integer _pos, Integer _size) {
    	if (!fields.containsKey(_name)){
    		Integer var = _size.intValue() * 2;
        	Integer pos = _pos.intValue();
        	Pair <Integer, Integer> aux = new Pair <Integer, Integer> (pos, var);    	
        	fields.put(_name.toLowerCase(), aux);
        	setBlockSize(var);
    	}
    	else {
    		System.out.println("Este atributo ya existe en el Schema");
    	}
    }  
 
    /*
     * Funcion para Agregar un atributo, indicando su nombre y Pair <Integer, Integer>
     * el cual indica la posición y el tamaño respectivamente
     */  
    public void addField(String _name, Pair <Integer, Integer> size)
    {
    	if (!fields.containsKey(_name)){
    		Integer var = size.getValue().intValue() * 2;
        	Integer pos = size.getKey().intValue();
        	Pair <Integer, Integer> aux = new Pair <Integer, Integer> (pos, var);
        	fields.put(_name.toLowerCase(), aux);
        	setBlockSize(var);
    	}
    	else {
    		System.out.println("Este atributo ya existe en el Schema");
    	}
    }
    
    /*
     * Funcion de Retorno del Nombre del Schema.
     */
    public String getSchemaName() {
 		return this.name;
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
     * Funcion de Retorno de la Posición del Atributo.
     */
    public Integer getFieldPos (String _name){
    	Integer auxPos = null;
    	
    	if (!fields.isEmpty()){
    		for (String n : fields.keySet()){
    			if (n == _name.toLowerCase()){
    				auxPos = fields.get(n).getKey();
    				break;
    			}
    		}    			
       	}
    	return auxPos;
    }
    
    /*
     * Funcion para Actualizar el Tamaño del Schema.
     */
    private void setBlockSize (int _value){
    	blockSize += _value;
    }
    
    /*
     * Funcion de Retorno del Tamaño del Schema.
     */
    public int getBlockSize (){
    	return this.blockSize;
    }
    
    /*
     * Funcion de retorno de HashMap del Schema.
     */
    public HashMap <String, Pair <Integer, Integer>> getFields (){
    	return this.fields;
    }
    
	private HashMap <String, Pair <Integer, Integer>> fields;
    private String name = null;
    private Integer blockSize = 0;
}