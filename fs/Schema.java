package fs;

import java.util.HashMap;
import java.lang.Integer;
import java.util.Map;

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
        	Pair <Integer, Integer> aux = new Pair <Integer, Integer> (_pos, _size);
        	fields.put(_name.toLowerCase(), aux);
        	addRecordLength(_size);
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
        	Pair <Integer, Integer> aux = new Pair <Integer, Integer> (size);
        	fields.put(_name.toLowerCase(), new Pair <Integer, Integer> (size));
        	addRecordLength(size.getValue());
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
        for (Map.Entry<String, Pair<Integer, Integer>> field : fields.entrySet())
            if (field.getValue().getKey() == position)
                return field.getKey();

    	return null;
    }
    
    /*
     * Funcion de Retorno de la Posici�n y Tama�o del Atributo.
     */
    public Pair <Integer, Integer> getFieldPair (String _name){
    	return fields.get(_name);
    }
    
    /*
     * Funcion de Retorno de la Posici�n del Atributo.
     */
    public Integer getFieldPos (String _name){
        Pair<Integer, Integer> field = fields.get(_name);
        return field != null ? field.getKey() : null;

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