package fs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

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
            // create table on disk
            Page p = new Page(schema, 0);
            Disk.writePage(p);
            Head h = new Head(0, Page.NULL_ID);
            Disk.writeHead(schema, h);
            HeapFile hf = new HeapFile(schema, p, null);
            heapFiles.put(schema.getSchemaName(), hf);

            // Add to system catalog table
            Record r = new Record(catalogHeap.getSchema());
            r.setData("name", schema.getSchemaName());
            r.setData("lastPageNum", "0");

            catalogHeap.addRecord(r);
            for (Entry<String, FieldValue> entry : schema.getFields().entrySet())
            {
                r = new Record(catalogFieldsHeap.getSchema());
                r.setData("schema", schema.getSchemaName());
                r.setData("name", entry.getKey());
                r.setData("pos", Integer.toString(entry.getValue().pos));
                r.setData("size", Integer.toString(entry.getValue().size));
                catalogFieldsHeap.addRecord(r);
            }
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
            // TODO: Remove table from system catalog tables
            Disk.deleteTable(schemaName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    HashMap<String, HeapFile> heapFiles;
    HeapFile catalogHeap;
    HeapFile catalogFieldsHeap;
}
