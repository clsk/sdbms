package fs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Disk
{
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String PATH_SEPARATOR = OS.indexOf("win") >= 0 ? "\\" : "/";
	private static String ROOT = OS.indexOf("win") >= 0 ? "c:\\DB\\"  : "~/dev/sdbms/DB/";

    public static Page readPage(String file)
    {
        return null;
    }

    public static void writePage(Page page)
    {
        ByteBuffer buffer = ByteBuffer.allocate(page.SIZE);
        // Write records
        int index = 0;
        for (String record : page.getRecords())
        {
            try {
                buffer.put(record.getBytes("US-ACII"), index, record.length());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            index += page.getSchema().getRecordLength();
        }

        // Write Pointers and bitmap towards very end of file
        byte[] bitmap = page.getSlotMap().toByteArray();
        // - Calculate index
        index = page.SIZE - (bitmap.length + 8);
        // Write pointers and bitmap
        buffer.putInt(index, page.getPrevPage());
        buffer.putInt(index+4, page.getNextPage());
        buffer.put(bitmap, index+8, bitmap.length);


    	String dirPath = ROOT + PATH_SEPARATOR +  page.getSchema().getSchemaName();
    	File dir = new File(dirPath);
    	dir.mkdirs(); //Verifica si existe el directorio, sino lo crea
   
    	String filePath = dirPath + PATH_SEPARATOR + page.getID();
    	File file = new File(filePath);
    	try {
	    	if(!file.exists()) //Verifica si existe el archivo, sino lo crea
	    		file.createNewFile();

                FileChannel channel = new FileOutputStream(file, false).getChannel();
                channel.write(buffer);
                channel.close();
		} catch (Exception e) {
			System.out.println("Ha ocurrido un error en el metodo writePage.");
		}
    }
}
