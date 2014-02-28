package fs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.BitSet;

public class Disk
{
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String PATH_SEPARATOR = OS.indexOf("win") >= 0 ? "\\" : "/";
	private static String ROOT = OS.indexOf("win") >= 0 ? "c:\\DB\\"  : "DB/";

    public static Page readPage(String file)
    {
        return null;
    }

    public static void writePage(Page page)
    {
        ByteBuffer buffer = ByteBuffer.allocate(page.SIZE);
        // Write records
        BitSet slotMap = page.getSlotMap();
        Schema schema = page.getSchema();
        String[] records = page.getRecords();
        for (String record : records)
        {
            try {
                buffer.put(record.getBytes("US-ASCII"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        // Write Pointers and bitmap towards very end of file
        byte[] bitmap = toByteArray(slotMap);

        // - Calculate index
        int index = page.SIZE - (bitmap.length + 8) - 1;
        // Write pointers and bitmap
        buffer.putInt(page.getPrevPage());
        buffer.putInt(page.getNextPage());
        buffer.put(bitmap);

    	String dirPath = ROOT + PATH_SEPARATOR +  page.getSchema().getSchemaName();
    	File dir = new File(dirPath);
    	dir.mkdirs(); //Verifica si existe el directorio, sino lo crea
   
    	String filePath = dirPath + PATH_SEPARATOR + page.getID();
    	File file = new File(filePath);
    	try {
	    	if(!file.exists()) //Verifica si existe el archivo, sino lo crea
	    		file.createNewFile();

                FileChannel channel = new FileOutputStream(file, false).getChannel();

                System.out.println(buffer.toString());
                buffer.rewind();
                System.out.println("Writing " + channel.write(buffer, 0) + " bytes");
                channel.close();

		} catch (Exception e) {
			System.out.println("Ha ocurrido un error en el metodo writePage.");
		}
    }
    private static byte[] toByteArray(BitSet bits) {
        byte[] bytes = new byte[bits.size()/8+1];
        for (int i=0; i<bits.size(); i++) {
            if (bits.get(i)) {
                bytes[bytes.length-i/8-1] |= 1<<(i%8);
            }
        }
        return bytes;
    }
}
