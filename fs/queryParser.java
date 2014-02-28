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
		Pattern myPattern = Pattern.compile("(?m)^(create table|CREATE TABLE)\\s[\\w]+\\s\\(");
		Matcher matching = myPattern.matcher("cadena");
		
		if (matching.find()) {
			for (int i = 0; i < lines.size(); i++) {
				myPattern = Pattern.compile("(?m)[\\w]+\\s(varchar|VARCHAR)\\s[\\d]+(,[\\n[\\w]+\\s(varchar|VARCHAR)\\s[\\d]+]+)\\)?;$");
				matching = myPattern.matcher(lines.elementAt(i + 1));
				String [] aux = lines.elementAt(i).split("\\s");
				if (matching.find()) {
					table = (aux[2]);
					aux = null;
					aux = lines.elementAt(i).split("\\s");
					atributes = new Vector <Pair <String, Integer>> ();
					Pair <String, Integer> auxp = new Pair <String, Integer> ();
					for (int J = 0; J < aux.length; J++){
						if (aux[J] == "varchar" || aux [J] == "VARCHAR") {
							//atriName, atriLength
							auxp.setKey(aux[J - 1]);
							auxp.setValue(Integer.parseInt(aux[J + 1]));
							atributes.add(auxp);
						}
					}
				}	
			}			
		}
		myPattern = Pattern.compile("(?m)^(INSERT)\\s[\\w]+(,\\s[\\w]+)?\\s(INTO)\\s[\\w]+\\s(VALUES)\\s[\\w]+(,\\s[\\w]+)?;$");
			
		myPattern = Pattern.compile("(?m)^(SELECT)\\s\\*\\s(FROM)\\s[\\w]+;$");
	}
	
	private Vector <Pair <String, Integer>> atributes;
	private String table;
}