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
            if (SystemCatalog.getInstance().addColumn(table, column, columnSize))
                System.out.println("Successfully added column " + column + " with size " + columnSize);
            else
                System.out.println("Error while attempting to add column " + column);
		}
		
		if (operationType == 'D' ) {
			if (SystemCatalog.getInstance().removeColumn(table, column))
                System.out.println("Successfully delete column " + column + " from " + table);
            else
                System.out.println("Error while attempting to delete column " + column);			
		}
		if (operationType == 'R') {
			if (SystemCatalog.getInstance().resizeColumn(table, column, columnSize))
                System.out.println("Successfully resized column " + column + " from " + table + " to size " + columnSize);
            else
                System.out.println("Error while attempting to resize column " + column + " to size " + columnSize);
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
		
		if (!hf.getSchema().hasField(ColumnName) && (Op == 'D' || Op == 'R')){
			System.out.println("The attribute " + ColumnName.toUpperCase() + " does not exist.");
			return null;
		}
		
		if (hf.getSchema().hasField(ColumnName) && Op == 'A'){
			System.out.println("The attribute " + ColumnName.toUpperCase() + " already exist.");
			return null;
		}
		
		return new Alter (TableName, ColumnName, Op, ColumnSize);
	}
}
