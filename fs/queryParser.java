import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import miniDBMS.Pair;

public class queryParser {
	public queryParser () {
		
	}
	
	public void readQueryFile () {	
		String line = null;
		
		try {
			BufferedReader reader;
			reader = new BufferedReader (new FileReader("C:/Users/Isis/programming/javaworkspace/miniDBMS/archivo.txt"));
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void splitQuery() {
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
		myPattern = Pattern.compile("(?m)^(INSERT)\\s[\\w]+(,\\s[\\w]+)?\\s(INTO)\\s[\\w]+\\s(VALUES)\\s[\\w]+(,\\s[\\w]+)?;$");
			
		myPattern = Pattern.compile("(?m)^(SELECT)\\s\\*\\s(FROM)\\s[\\w]+;$");
	}
	
	private Vector <Pair <String, Integer>> atributes;
	private String table;
}