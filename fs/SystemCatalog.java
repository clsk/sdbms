package fs;

import java.io.IOException;
import java.util.ArrayList;
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

            // Remove schema from SYSTEMCATALOG table
            ArrayList<Pair<RID, String>> records =  catalogHeap.getAllRecords();
            for (Pair<RID, String> record : records)
            {
                Record r = Record.valueOf(catalogHeap.getSchema(), record.getValue());
                if (r.getValueForField("name").trim() == schemaName)
                {
                    catalogHeap.removeRecord(record.getKey());
                    break;
                }
            }

            // Remove schema fields from SYSTEMCATALOGFIELDS
            records =  catalogFieldsHeap.getAllRecords();
            for (Pair<RID, String> record : records)
            {
                Record r = Record.valueOf(catalogFieldsHeap.getSchema(), record.getValue());
                if (r.getValueForField("schema").trim() == schemaName)
                {
                    catalogFieldsHeap.removeRecord(record.getKey());
                }
            }

            // Delete table directory from Disk
            Disk.deleteTable(schemaName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    HashMap<String, HeapFile> heapFiles;
    HeapFile catalogHeap;
    HeapFile catalogFieldsHeap;
}
