package fs;
import java.io.File;

public class Disk
{
	private static String path = "c:/DB";
	
    public static Page readPage(String file)
    {
        return null;
    }

    public static void writePage(Page page)
    {
    	File dir = new File(path + "/" + page.getSchema().getName());
    	boolean existDir = dir.mkdir();
   
    	System.out.println(existDir);
    	
    	for (String str : page.getRecords()) {
    		System.out.println(str);
		}
    }
}
