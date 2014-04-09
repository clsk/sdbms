/**
 * 
 */
package query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fs.*;
/**
 * @author Isis
 *
 */
public class Alter extends Query {
	HeapFile hf = null;
	
	public Alter (String schema){
		hf = SystemCatalog.getInstance().getTable(schema);
	}
	
	@Override
	public void execute (){
		
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
		}
		
		return new Alter (TableName);
	}
}
