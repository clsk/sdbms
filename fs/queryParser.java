package fs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import miniDBMS.Pair;

public class queryParser {
	/**
	 * Vector que almacena los comandos del tipo Create Table.
	 * La estructura Pair <Integer, String>, indica que todos los
	 * elementos que contienen la llave Integer pertenecen al mismo comando.
	 * Donde Integer es el numero correspondiente a la secuencia en la que
	 * se encuentran desarrollados los Query, en el archivo dado.
	 */
	private Vector <Pair <Integer, String>> commandsCreate = null;
	
	/**
	 * Vector que almacena los comandos del tipo Insert Into.
	 * La estructura Pair <Integer, String>, indica que todos los
	 * elementos que contienen la llave Integer pertenecen al mismo comando.
	 * Donde Integer es el numero correspondiente a la secuencia en la que
	 * se encuentran desarrollados los Query, en el archivo dado.
	 */
	private Vector <Pair <Integer, String>> commandsInsertInto =  null;
	
	/**
	 * Vector que almacena los comandos del tipo Create Table.
	 * La estructura Pair <Integer, String>, indica que todos los
	 * elementos que contienen la llave Integer pertenecen al mismo comando.
	 * Donde Integer es el numero correspondiente a la secuencia en la que
	 * se encuentran desarrollados los Query, en el archivo dado.
	 */
	private Vector <Pair <Integer, String>> commandsSelect = null;
	
	public queryParser () {
				
	}
	
	/**
	 * Lectura de Archivo que contiene el Create Table
	 * y Insert Into para una tabla en especifico.
	 */
	public void readQueryFile (String pathQuery ) {	
		try {
			BufferedReader reader = new BufferedReader (new FileReader(pathQuery));
			/**
			 * Patron de Expresion Regular.
			 * Este realiza la verificación de si
			 * es un commando CREATE TABLE.
			 */
			Pattern patCreateTable = Pattern.compile("(?m)^(create table|CREATE TABLE)\\s[\\w]+\\s\\(");
			
			/**
			 * Patron de Expresion Regular.
			 * Este realiza la verificación de si
			 * es un commando NAME CHAR SIZE
			 */
			Pattern patAttribute = Pattern.compile("(?m)^[\\w]+\\s(char|CHAR)\\s[\\d]+(,|\\);)$");
			
			/**
			 * Patron de Expresion Regular.
			 * Este realiza la verificación de si
			 * es un commando INSERT INTO.
			 */
			Pattern patInsertInto = Pattern.compile("(?m)^(INSERT)\\s(INTO)\\s[\\w]+\\s\\([\\w]+(,\\s[\\w]+)+?\\)"
					+ "\\s(VALUES)\\s\\('[\\w\\s]+'(,\\s'[\\w\\s]+')+?\\s\\);$");
			
			/**
			 * Patron de Expresion Regular.
			 * Este realiza la verificación de si
			 * es un commando SELECT * FROM.
			 */
			Pattern patSelect = Pattern.compile("^(?m)^(SELECT)\\s\\*\\s(FROM)\\s[a-zA-Z]+;$");
			
			String line = null;
			int sequence = -1;
			while ((line = reader.readLine())  != null) {
				if ( !line.matches("")) {
					if ( patCreateTable.matcher(line).find() ) {
						sequence++;
						if (commandsCreate == null){
							commandsCreate = new Vector <Pair <Integer, String>> ();
						}
						Pair <Integer, String> auxPCT = new Pair <Integer, String> (sequence, line);
						commandsCreate.add(auxPCT);					
					}
					if (patAttribute.matcher(line).find()) {
						Pair <Integer, String> auxP = new Pair <Integer, String> (sequence, line);
						commandsCreate.add(auxP);
					}
					if (patInsertInto.matcher(line).find()) {
						sequence++;
						if (commandsInsertInto == null) {
							commandsInsertInto = new Vector <Pair <Integer, String>> ();
						}
						Pair <Integer, String> auxP = new Pair <Integer, String> (sequence, line);
						commandsInsertInto.add(auxPII);
					}
					if (patSelect.matcher(line).find()) {
						sequence++;
						if (commandsSelect == null) {
							commandsSelect = new Vector <Pair <Integer, String>> ();
						}
						Pair <Integer, String> auxP = new Pair <Integer, String> (sequence, line);
						commandsSelect.add(auxPII);
					}
					if (!patSelect.matcher(line).find() && !patInsertInto.matcher(line).find() 
							&& !patCreateTable.matcher(line).find() && !patAttribute.matcher(line).find()){
						System.err.println("La linea -> "+line+". NO CUMPLE CON LAS SENTENCIAS PERMITIDAS.\n\n");
						System.exit(1);
					}
				}				
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.print(e);
		}
	}
	
	public Vector<Pair<Integer, String>> getCommandsCreate() {
		return commandsCreate;
	}

	public void setCommandsCreate(Vector<Pair<Integer, String>> commandsCreate) {
		this.commandsCreate = commandsCreate;
	}

	public Vector<Pair<Integer, String>> getCommandsInsertInto() {
		return commandsInsertInto;
	}

	public void setCommandsInsertInto(Vector<Pair<Integer, String>> commandsInsertInto) {
		this.commandsInsertInto = commandsInsertInto;
	}

	public Vector<Pair<Integer, String>> getCommandsSelect() {
		return commandsSelect;
	}

	public void setCommandsSelect(Vector<Pair<Integer, String>> commandsSelect) {
		this.commandsSelect = commandsSelect;
	}
	
}