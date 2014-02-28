package fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.BitSet;

public class Disk
{
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String PATH_SEPARATOR = OS.indexOf("win") >= 0 ? "\\" : "/";
	private static String ROOT = OS.indexOf("win") >= 0 ? "c:\\DB\\"  : "DB/";

    public static Page readPage(Schema schema, int pageId)
    {
        String dirPath = ROOT + PATH_SEPARATOR +  schema.getSchemaName();
    	File dir = new File(dirPath);
    	dir.mkdirs(); //Verifica si existe el directorio, sino lo crea

    	String filePath = dirPath + PATH_SEPARATOR + pageId;
    	File file = new File(filePath);
    	try {
	    	if(!file.exists()) //Verifica si existe el archivo, sino lo crea
                throw new Error("Error Opening File: File " + filePath + " does not exist");


            final byte[] buffer = Files.readAllBytes(file.toPath());
            final int capacity = Page.calcCapacity(schema.getRecordLength());
            final String[] records = new String[capacity];
            int index = 0;
            for (int i = 0; i < capacity; i++, index += schema.getRecordLength())
            {
                records[i] = new String(Arrays.copyOfRange(buffer, index, index+schema.getRecordLength()), "US-ASCII");
            }

            System.out.println("Index of prevPage: " + index);
            final int prevPage = ByteBuffer.wrap(Arrays.copyOfRange(buffer, index, index+4)).getInt();
            index += 4;
            final int nextPage = ByteBuffer.wrap(Arrays.copyOfRange(buffer, index, index+4)).getInt();
            index += 4;
            final BitSet slotMap = BitSet.valueOf(Arrays.copyOfRange(buffer, index, index + (capacity / 8 + 1)));

            return new Page(schema, records, prevPage, nextPage, slotMap);

        } catch (Exception e) {
			System.out.println("Ha ocurrido un error en el metodo readPage: " + e.getMessage());
            e.printStackTrace();
		}

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
        byte[] bitmap = toByteArray(slotMap, page.getCapacity());

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
			System.out.println("Ha ocurrido un error en el metodo writePage: " + e.getMessage());
            e.printStackTrace();
		}
    }
    private static byte[] toByteArray(BitSet bits, int len) {
        int n = len/8+1;
        System.out.println("n: " + n);
        System.out.println("bits.size(): " + len);
        byte[] bytes = new byte[len/8+1];
        for (int i=0; i<bits.size(); i++) {
            if (bits.get(i)) {
//                bytes[bytes.length-i/8-1] |= 1<<(i%8);
                bytes[i/bytes.length] |= 1<<(i%8);
            }
        }
        return bytes;
    }
}
