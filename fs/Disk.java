package fs;

import java.io.File;
import java.io.FileWriter;

public class Disk
{
	private static String root = "c:\\DB"; //root por defecto
	
    public static Page readPage(String file)
    {
        return null;
    }

    public static void writePage(Page page)
    {
    	String dirPath = root + "\\" + page.getSchema().getSchemaName();
    	File dir = new File(dirPath);
    	dir.mkdirs(); //Verifica si existe el directorio, sino lo crea
   
    	String filePath = dirPath + "\\" + page.getID() + ".txt";
    	File file = new File(filePath);
    	try {
	    	if(!file.exists()) //Verifica si existe el archivo, sino lo crea
	    		file.createNewFile();
	    	
	    	FileWriter fw = new FileWriter(filePath, true);
	    	Pair pair = null;
	    	int size;
	    	int position;
	    	String str;
	    	for (String cadena : page.getRecords()) {
	    		if(cadena != null){
	    			for (String field : page.getSchema().getFields().keySet()) {
	    				pair = page.getSchema().getFields().get(field);
	    				position = (int)pair.getKey(); //position
	    				size = (int)pair.getValue(); //size
	    				
	    				//En esta parte se colocaran las sub-cadenas para formar el record
	    				//esto dependera del schema
					}
	    			
	    			//fw.write(cadena); 
	    		}
			}
	    	fw.write(page.getPrevPage());
	    	fw.write(page.getNextPage());
	    	//fw.write(page.getSlotMap());
	    	fw.flush();
	    	fw.close();
		} catch (Exception e) {
			System.out.println("Ha ocurrido un error en el metodo writePage.");
		}
    }
}
