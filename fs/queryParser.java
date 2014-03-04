package fs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class queryParser {
	public queryParser () {
				
	}
	
	/**
	 * Lectura de Archivo que contiene el Create Table
	 * y Insert Into para una tabla en especifico.
	 */
	public void readQueryFile (String pathQuery ) {	
		try {
			BufferedReader reader = new BufferedReader (new FileReader(pathQuery));
			String line = null;
			lines = new Vector <String> ();
			while ((line = reader.readLine())  != null) {
				lines.add(line);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.print(e);
		}
	}
	
	public void splitQueryCreateTable() {
		Pattern myPattern = Pattern.compile("(?m)^(create table|CREATE TABLE)\\s[\\w]+\\s\\(");		
		
		if (!lines.isEmpty()){
			Matcher matching = myPattern.matcher(lines.elementAt(0));
			if (matching.find()){
				atributes = new Vector <Pair <String, Integer>> ();
				String line = lines.elementAt(0);
				String [] aux = line.split("\\s");
				setTable((aux[2]));
				aux = null;				
				for (int i = 1; i < lines.size(); i++){
					myPattern = Pattern.compile("(?m)^[\\w]+\\s(varchar|VARCHAR)\\s[\\d]+,$");
					line = lines.elementAt(i);
					matching = myPattern.matcher(line);
					if (matching.find()){
						if (atributes.isEmpty())
							atributes = new Vector <Pair <String, Integer>> ();
						Pair <String, Integer> auxp = new Pair <String, Integer> ();
						line = lines.elementAt(i);
						aux = line.split("\\s|\\,");
						myPattern = Pattern.compile("(?m)^(varchar|VARCHAR)$");
						line = aux[1];
						matching = myPattern.matcher(line);
						if (matching.find()) {
							//atriName VARCHAR 10
							auxp.setKey(aux[1 - 1]);
							auxp.setValue(Integer.parseInt(aux[1 + 1]));
							atributes.add(auxp);
						}
					}
					myPattern = Pattern.compile("(?m)\\);$");
					matching = myPattern.matcher(lines.elementAt(i));
					if (matching.find())
						break;
				}
			}
		}		
	}
	
	/**
	 * Funcion para obtener los registros que seran insertados a la tabla.
	 */
	public void splitInsertInto () {
		Pattern myPattern = Pattern.compile("(?m)^(INSERT)\\s(INTO)\\s[\\w]+\\s\\([\\w]+(,\\s[\\w]+)+?\\)\\s(VALUES)\\s\\('[\\w\\s]+'(,\\s'[\\w\\s]+')+?\\s\\);$");
		if (!lines.isEmpty()){
			String line;
			atriRecord = new Vector <Pair <String, String>> ();
			String [] aux = null;
			for (int i = 0; i < lines.size(); i++){
				line = lines.elementAt(i);
				Matcher matching = myPattern.matcher(line);				
				if (matching.find()){
					line = line.replaceAll("\\(|\\)|,|\\s{2,}"," ");
					aux = line.split("\\s");
					setTable(aux[2]);
					line = lines.elementAt(i);
					line = line.replaceAll("^(INSERT)\\s(INTO)\\s[\\w]+\\s", "");
					line = line.replaceAll("\\(|\\)|,|\\s{2,}"," ");
					aux = null;
					aux = line.split("\\s{2,}");
					int cant = (aux.length - 2);
					if (cant%2 == 0) {
						cant = cant / 2;
						for (int j = 0; j < cant; j++){
							Pair <String, String> auxp  = new Pair <String, String> ();
							auxp.setKey(aux[j]);
							auxp.setValue(aux[cant + j + 1]);
							atriRecord.add(auxp);
							auxp = null;
						}
					}
					else {
						System.out.print("La Cantidad de valores a Insertar no Coincide con la Cantidad de Atributos.");
					}
				}
			}			
		}		
	}
	
	/**
	 * Funcion para el analisis del Query "SELECT * FROM table_name"
	 */
	public void splitSelect(){
		Pattern myPattern = Pattern.compile("^(?m)^(SELECT)\\s\\*\\s(FROM)\\s[a-zA-Z]+;$");
		if (!lines.isEmpty() && lines.size() == 1){
			String line = lines.elementAt(0);
			Matcher matching = myPattern.matcher(line);
			if (matching.find()){
				line = line.replaceAll("(SELECT)\\s\\*\\s(FROM)\\s", "");
				line = line.replace(";", "");
				line = line.toLowerCase();
				setTable(line);
			}
		}
	}
	
	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}
	
	/**
	 * Funcion de Retorno de Los Records a ser insetados.
	 */
	public Vector <Pair <String, String>> GetRecords (){
		return atriRecord;
	}
	
	/**
	 * @return Los Atributos y las Posiciones que le corresponden en la tabla.
	 */
	public Vector <Pair <String, Integer>> GetAtributes () {
		return atributes;
	}
	
	private Vector <Pair <String, Integer>> atributes;
	private Vector <Pair <String, String>> atriRecord;
	private String table;
	private Vector <String> lines;
}