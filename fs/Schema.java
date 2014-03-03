package fs;

import java.util.*;
import java.lang.Integer;

import static java.util.Map.Entry;

public class Schema
{
    public Schema(String name, int _lastPageNum)
    {
        fields = new HashMap <String, FieldValue>();
        this.name = name;
        lastPageNum = _lastPageNum;
    }

    public int getLastPageNum()
    {
        return lastPageNum;
    }

    public int getNewPageNum()
    {
        return ++lastPageNum;
    }
    
    /*
     * Funcion para Agregar un atributo, indicando su nombre, posicion y tama�o.
     */    
    public void addField (String _name, Integer _pos, Integer _size) {
    	if (!fields.containsKey(_name.toLowerCase())){
        	fields.put(_name.toLowerCase(), new FieldValue(_pos, _size));
        	increaseRecordLength(_size);
    	}
    	else {
    		System.out.println("Este atributo ya existe en el Schema");
    	}
    }  
 
    /*
     * Funcion para Agregar un atributo, indicando su nombre y Pair <Integer, Integer>
     * el cual indica la posici�n y el tama�o respectivamente
     */  
    public void addField(String _name, FieldValue fieldValue)
    {
    	if (!fields.containsKey(_name.toLowerCase())){
        	fields.put(_name.toLowerCase(), new FieldValue(fieldValue));
        	increaseRecordLength(fieldValue.size);
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
        for (Entry<String, FieldValue> field : fields.entrySet())
            if (field.getValue().pos == position)
                return field.getKey();

    	return null;
    }
    
    /*
     * Funcion de Retorno de la Posici�n y Tama�o del Atributo.
     */
    public FieldValue getFieldPair (String _name){
    	return fields.get(_name.toLowerCase());
    }
    
    /*
     * Funcion de Retorno de la Posici�n del Atributo.
     */
    public Integer getFieldPos (String _name){
        FieldValue field = fields.get(_name.toLowerCase());
        return field != null ? field.pos : null;

    }
    
    /*
     * Funcion para Actualizar el Tama�o del Schema.
     */
    private void increaseRecordLength (int _value){
    	this.recordLength += _value;
    }
    
    /*
     * Funcion de Retorno de las Parejas de Posicion y Tama�o
     * de los Atributos.
     */
    public HashMap<String, FieldValue> getFields (){
        return fields;
    }

    public List<Entry<String, FieldValue>> getSortedFields()
    {
        List<Entry<String, FieldValue>> f = new ArrayList<Entry<String, FieldValue>>(fields.entrySet());

        Collections.sort(f, new Comparator<Entry<String, FieldValue>>() {

            public int compare(Entry<String, FieldValue> o1, Entry<String, FieldValue> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        return f;
    }
    
    /*
     * Funcion de Retorno del Tama�o del Schema.
     */
    public int getRecordLength (){
    	return recordLength;
    }

    public int getFieldCount()
    {
        return fields.size();
    }
    
	private HashMap <String, FieldValue> fields;
    private String name = null;
    private int recordLength = 0;
    private int lastPageNum = 0;
}