package fs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.BitSet;

public class Disk
{
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String PATH_SEPARATOR = OS.contains("win") ? "\\" : "/";
	private static String ROOT = OS.contains("win") ? "c:\\DB\\"  : "DB/";

    public static Page readPage(Schema schema, int pageId)
    {
        if (pageId == Page.NULL_ID)
            return null;

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

    public static Boolean writePage(Page page)
    {
        // Don't do anything if page is null
        if (page == null)
            return false;

        ByteBuffer buffer = ByteBuffer.allocate(Page.SIZE);
        // Write records
        final BitSet slotMap = page.getSlotMap();
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
                buffer.rewind();
                channel.write(buffer, 0);
                channel.close();

		} catch (Exception e) {
			System.out.println("Ha ocurrido un error en el metodo writePage: " + e.getMessage());
            e.printStackTrace();
		}

        return true;
    }

    static public Boolean writeHead(Schema schema, Head head)
    {
        if (schema == null | head == null)
            return false;

        String dirPath = ROOT + PATH_SEPARATOR +  schema.getSchemaName();
    	File dir = new File(dirPath);
    	dir.mkdirs(); //Verifica si existe el directorio, sino lo crea

    	String filePath = dirPath + PATH_SEPARATOR + "head";
    	File file = new File(filePath);
    	try {
	    	if(!file.exists()) //Verifica si existe el archivo, sino lo crea
	    		file.createNewFile();

            Files.write(file.toPath(), head.getData());

        } catch (Exception e) {
			System.out.println("Ha ocurrido un error en el metodo writeHead: " + e.getMessage());
            e.printStackTrace();
		}

        return true;
    }

    static public Head readHead(Schema schema)
    {

        String dirPath = ROOT + PATH_SEPARATOR +  schema.getSchemaName();
    	File dir = new File(dirPath);
    	dir.mkdirs(); //Verifica si existe el directorio, sino lo crea

    	String filePath = dirPath + PATH_SEPARATOR + "head";
    	File file = new File(filePath);
    	try {
	    	if(!file.exists()) //Verifica si existe el archivo, sino lo crea
                throw new Error("Error Opening File: File " + filePath + " does not exist");


            final byte[] buffer = Files.readAllBytes(file.toPath());
            return new Head(buffer);
        } catch (Exception e) {
			System.out.println("Ha ocurrido un error en el metodo readHead: " + e.getMessage());
            e.printStackTrace();
		}

        return null;
    }

    static public void deleteTable(String schemaName) throws IOException {
        String dirPath = ROOT + PATH_SEPARATOR +  schemaName;
        File dir = new File(dirPath);
        if (dir.exists())
        {
            String[] fileList = dir.list();
            for (String file : fileList)
            {
                File f = new File(dir, file);
                f.delete();
            }
            dir.delete();
        }
    }
    
    static public boolean ExistsTable (String schemaName){
    	String dirPath = ROOT + PATH_SEPARATOR +  schemaName;
        File dir = new File(dirPath);
        if (dir.exists()){
        	return true;
        }
        return false;
    }

    private static byte[] toByteArray(BitSet bits, int len) {
        byte[] bytes = new byte[len/8+1];
        for (int i=0; i<bits.size(); i++) {
            if (bits.get(i)) {
                bytes[i/8] |= 1<<(i%8);
            }
        }
        return bytes;
    }
}
