import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import miniDBMS.Pair;

public class queryParser {
	public queryParser () {
		atributes = new Vector <Pair <String, Integer>> ();		
	}
	
	public void readQueryFile (string path) {	
		try {
			BufferedReader reader = new BufferedReader (new FileReader(path));
			String line = null;
			lines = new Vector <String> ();
			while ((line = reader.readLine())  != null) {
				lines.add(line);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void splitQueryCreateTable() {
		/*
		 * Create Table Super Query
		 * Responde a :
		 * Create Table NameTable (
		 * atributoName varchar length,
		 * atributoName varchar length,
		 * );
		 * 
		 * Testeado :P
		 */
		Pattern myPattern = Pattern.compile("(?m)^(create table|CREATE TABLE)\\s[\\w]+\\s\\(");		
		
		if (!lines.isEmpty()){
			Matcher matching = myPattern.matcher(lines.elementAt(0));
			if (matching.find()){
				String line = lines.elementAt(0);
				String [] aux = line.split("\\s");
				setTable((aux[2]));
				aux = null;				
				for (int i = 1; i < lines.size(); i++){
					myPattern = Pattern.compile("(?m)[\\w]+\\s(varchar|VARCHAR)\\s[\\d]+,$");
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
			
		myPattern = Pattern.compile("(?m)^(SELECT)\\s\\*\\s(FROM)\\s[\\w]+;$");
	}
	
	/*
	 * Try parser Insert Into.
	 */
	public void splitInsertInto () {
		Pattern myPattern = Pattern.compile("(?m)^(INSERT)\\s(INTO)\\s[\\w]+\\s\\([\\w]+(,\\s[\\w]+)+?\\)\\s(VALUES)\\s\\('[\\w\\s]+'(,\\s'[\\w\\s]+')+?\\s\\);$");
		if (!lines.isEmpty()){
			String line;
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
						atriRecord = new Vector <Pair <String, String>>();
						Pair <String, String> auxp  = new Pair <String, String> ();
						for (int j = 0; j < cant; j++){
							auxp.setKey(aux[j]);
							auxp.setValue(aux[cant + j + 1]);
							atriRecord.add(auxp);
						}
					}
					else {
						System.out.print("La Cantidad de valores a Insertar no Coincide con la Cantidad de Atributos.");
					}
				}
			}			
		}		
	}
	
	private Vector <Pair <String, Integer>> atributes;
	private String table;
	private Vector <String> lines;
}