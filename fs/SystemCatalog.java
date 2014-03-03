package fs;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class SystemCatalog {

    public SystemCatalog(HashMap<String, HeapFile> _heapFiles)
    {
        heapFiles = _heapFiles;
        catalogHeap = heapFiles.get("SYSTEMCATALOG");
        catalogFieldsHeap = heapFiles.get("SYSTEMCATALOGFIELDS");
    }

    public boolean createTable(Schema schema)
    {
        if (!heapFiles.containsKey(schema.getSchemaName()))
        {
            Page p = new Page(schema, 0);
            Disk.writePage(p);
            Head h = new Head(0, Page.NULL_ID);
            Disk.writeHead(schema, h);
            HeapFile hf = new HeapFile(schema, p, null);
            heapFiles.put(schema.getSchemaName(), hf);
            return true;
        }

        return false;
    }

    public HeapFile getTable(String schemaName)
    {
        return heapFiles.get(schemaName);
    }

    public void dropTable(String schemaName)
    {
        try {
            Disk.deleteTable(schemaName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    HashMap<String, HeapFile> heapFiles;
    HeapFile catalogHeap;
    HeapFile catalogFieldsHeap;
}
