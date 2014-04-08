/**
 * 
 */
package query;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fs.*;

/**
 * @class	Drop
 * @brief	Clase para el realizacion de la eliminacion
 * 			de una tabla de la base de datos.
 *
 */
public class Drop extends Query {
	
	public Drop (String _schema){
		TableName = _schema;
	}
	
	@Override
	public void execute (){
		if (SystemCatalog.getInstance().dropTable(TableName)){
			System.out.println("\nDeleting Table: " + TableName.toUpperCase() + "...");
			System.out.println(TableName.toLowerCase() + " Deleted " + ".");
		}
		else {
			System.out.println("Table: " + TableName.toUpperCase() + " Couldn't be deleted.");
		}
	}
	
	static final Pattern D_PATTERN = Pattern.compile("drop\\s+table\\s+[\\w]+;$", Pattern.CASE_INSENSITIVE);
	
	static public Drop parserDrop(String line, BufferedReader reader){
		
		Matcher m = D_PATTERN.matcher(line);
		HeapFile hf = null;
		String tableName = "";
				
		if (!m.matches()) {
			System.out.println("Error matching DROP TABLE statement");
			return null;
		}
		
		tableName = line.replaceAll("drop\\s+table\\s+", "");
		tableName = tableName.replaceAll(";", "");
		
		hf = SystemCatalog.getInstance().getTable(tableName.toLowerCase());
		
		if (hf == null){
			System.out.println("Drop Error: Table " + tableName.toUpperCase() + " has not been defined.");
			return null;
		}
		
		return new Drop(tableName);
	}
	
	//	Nombre de Tabla que sera eliminada.
	String TableName = null;
}
