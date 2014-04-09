/**
 * 
 */
package query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fs.*;
/**
 * @author Isis
 *
 */
public class Alter extends Query {
	char operationType = 'n';
	String table = "";
	String column = "";
	int columnSize = 0;
	
	public Alter (String _name, String _colName, char _op, int _colSize){
		table = _name;
		column = _colName;
		operationType = _op;
		columnSize = _colSize;
	}
	
	@Override
	public void execute (){
		if (operationType == 'A'){
			HeapFile hf = SystemCatalog.getInstance().getTable(table);
			
			//	Instancia del Schema Viejo de la tabla actual.
			Schema oldSM = new Schema(table);
			List<Entry<String, FieldValue>> fields = hf.getSchema().getSortedFields();			
			for (int i = 0; i < fields.size(); i++){
				String aux = fields.get(i).getKey();
				FieldValue _aux= fields.get(i).getValue();
				oldSM.addField( aux, _aux);
			}			
			
			//	Get all records of actual table.
			ArrayList<Pair<RID, String>> records =  hf.getAllRecords();
			
			//	Adding the new column to the actual table.
			hf.addNewField(column, columnSize);
			SystemCatalog.getInstance().addColumn(hf.getSchema(), column, columnSize);
								
			//	Eliminacion de la tabla actual.
			SystemCatalog.getInstance().dropTable(table);
			for (int j = 0; j < records.size(); j++){
				RID aux = records.get(j).getKey();
				hf.removeRecord(aux);
			}
						
			char [] _emptyValue = new char [columnSize];
			Arrays.fill(_emptyValue, '0');
			
			int _size = ( hf.getSchema().getFieldPos(column) );			
			String [] data = new String [_size];
			String _vAux = "";
			
			for ( Pair <RID, String> record : records){
				_vAux = record.getValue();
				Record rAux = Record.valueOf( oldSM, _vAux);
				
				for (int i = 0; i < data.length - 1; i++){
					data[i] = (rAux.getData())[i];
				}
				data [hf.getSchema().getFieldPos(column) - 1] = new String (_emptyValue);
				
				/*
				 *	Solo carga las dos primeras records.
				 *	Luego... Catch a exception.
				 */
				rAux.setData(data);
				RID rid = hf.addRecord(rAux);
				System.out.println("Inserted record at: " + rid);
			}
			
			if (SystemCatalog.getInstance().createTable(hf.getSchema())) {
				System.out.println("Column: " + column.toUpperCase() + " was added in Table: " + table.toUpperCase() + ".");
			}
		}
		
		if (operationType == 'D' ) {
						
		}
		if (operationType == 'R') {
			
		}
	}
	
	static final Pattern ALTER_DROP = Pattern.compile("alter\\s+table\\s+[\\w]+\\s+drop\\s+column\\s+[\\w]+;$", Pattern.CASE_INSENSITIVE);
	static final Pattern ALTER_ADD = Pattern.compile("alter\\s+table\\s+[\\w]+\\s+add\\s+[\\w]+\\s+char\\s+\\(\\d+\\);$", Pattern.CASE_INSENSITIVE);
	static final Pattern ALTER_RESIZE = Pattern.compile("alter\\s+table\\s+[\\w]+\\s+alter\\s+column\\s+[\\w]+\\s+char\\s+\\(\\d+\\);$", Pattern.CASE_INSENSITIVE);
	
	static public Alter parserAlter (String line){
		Matcher m = ALTER_DROP.matcher(line);
		Matcher n = ALTER_ADD.matcher(line);
		Matcher o = ALTER_RESIZE.matcher(line);		
		
		String TableName = "";
		String ColumnName = "";
		String aux = "";
		int ColumnSize = 0;
		char Op = 'n'; 
		HeapFile hf = null;
		
		if (!m.matches() && !n.matches() && !o.matches()) {
			System.out.println("Error matching ALTER statement.");
			return null;
		}
		
		if (m.matches()){
			/*
			 * Realizacion de Replacement para obtener:
			 * 		- Nombre de Tabla
			 * 		- Nombre de Columna
			 */
			TableName = line.replaceAll("alter\\s+table\\s+", "");
			TableName = TableName.replaceAll("\\s+drop\\s+column\\s+[\\w]+;", "");
			ColumnName = line.replaceAll("alter\\s+table\\s+[\\w]+\\s+drop\\s+column\\s+", "");
			ColumnName = ColumnName.replaceAll(";", "");
			Op = 'D'; 
		}
		
		if (n.matches()){
			/*
			 * Realizacion de Replacement para obtener:
			 * 		- Nombre de Tabla
			 * 		- Nombre de Columna
			 * 		- Longitud de Columna
			 */
			TableName = line.replaceAll("alter\\s+table\\s+", "");
			TableName = TableName.replaceAll("\\s+add\\s+[\\w]+\\s+char\\s+\\(\\d+\\);", "");
			ColumnName = line.replaceAll("alter\\s+table\\s+[\\w]+\\s+add\\s+", "");
			ColumnName = ColumnName.replaceAll("\\s+char\\s+\\(\\d+\\);", "");
			aux = line.replaceAll("alter\\s+table\\s+[\\w]+\\s+add\\s+[\\w]+\\s+char\\s+\\(", "");
			aux = aux.replaceAll("\\);", "");
			ColumnSize = Integer.parseInt(aux);			
			Op = 'A';
		}
		
		if (o.matches()){
			/*
			 * Realizacion de Replacement para obtener:
			 * 		- Nombre de Tabla
			 * 		- Nombre de Columna
			 * 		- Longitud de Columna
			 */
			TableName = line.replaceAll("alter\\s+table\\s+", "");
			TableName = TableName.replaceAll("\\s+alter\\s+column\\s+[\\w]+\\s+char\\s+\\(\\d+\\);", "");
			ColumnName = line.replaceAll("alter\\s+table\\s+[\\w]+\\s+alter\\s+column\\s+", "");			
			ColumnName = ColumnName.replaceAll("\\s+char\\s+\\(\\d+\\);", "");
			aux = line.replaceAll("alter\\s+table\\s+[\\w]+\\s+alter\\s+column\\s+[\\w]+\\s+char\\s+\\(", "");
			aux = aux.replaceAll("\\);", "");
			ColumnSize = Integer.parseInt(aux);
			Op = 'R';
		}
		
		hf = SystemCatalog.getInstance().getTable(TableName);
		
		if (hf == null){
			System.out.println("The table " + TableName.toUpperCase() + " does not exist.");
			return null;
		}
		
		if (hf.getSchema().hasField(ColumnName) && Op != 'R'){
			System.out.println("The attribute " + ColumnName.toUpperCase() + " does not exist.");
			return null;
		}
		
		return new Alter (TableName, ColumnName, Op, ColumnSize);
	}
}
