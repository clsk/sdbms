package fs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class SystemCatalog {

    static public SystemCatalog getInstance()
    {
        if (instance == null)
        {
            instance = buildCatalog(getCatalogSchema(), getCatalogFieldsSchema());
        }

        return instance;
    }

    static private SystemCatalog instance;
    private SystemCatalog(HashMap<String, HeapFile> _heapFiles)
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
            Head h = new Head(0, Page.NULL_ID);
            Disk.writeHead(schema, h);
            Page p = new Page(schema, 0);
            Disk.writePage(p);
            HeapFile hf = new HeapFile(schema, h, p, null);
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

    public boolean dropTable(String schemaName)
    {
        if (heapFiles.containsKey(schemaName))
        {
            try {
                // Remove schema from SYSTEMCATALOG table
                ArrayList<Pair<RID, String>> records =  catalogHeap.getAllRecords();
                for (Pair<RID, String> record : records)
                {
                    Record r = Record.valueOf(catalogHeap.getSchema(), record.getValue());
                    if (r.getValueForField("name").trim().equals(schemaName))
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
                    if (r.getValueForField("schema").trim().equals(schemaName))
                    {
                        catalogFieldsHeap.removeRecord(record.getKey());
                    }
                }

                // Delete table directory from Disk
                Disk.deleteTable(schemaName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        else
        {
            return false;
        }

    }

    private static Schema catalogSchema = null;

    public static Schema getCatalogSchema()
    {
        if (catalogSchema == null)
        {
            catalogSchema = new Schema("SYSTEMCATALOG", 0);
            catalogSchema.addField("name", 0, 128);
            catalogSchema.addField("lastPageNum", 1, 10);
        }

        return catalogSchema;
    }

    private static Schema catalogFieldsSchema = null;

    public static Schema getCatalogFieldsSchema()
    {
        if (catalogFieldsSchema == null)
        {
            catalogFieldsSchema = new Schema("SYSTEMCATALOGFIELDS", 0);
            catalogFieldsSchema.addField("schema", 0, 20);
            catalogFieldsSchema.addField("name", 1, 32);
            catalogFieldsSchema.addField("pos", 2, 3);
            catalogFieldsSchema.addField("size", 3, 5);
        }

        return catalogFieldsSchema;
    }

    static private SystemCatalog buildCatalog(Schema catalogSchema, Schema catalogFieldsSchema)
    {
         // Read Catalog
        HeapFile hpfCatalog = new HeapFile(catalogSchema);
        ArrayList<Pair<RID, String>> records = hpfCatalog.getAllRecords();
        HashMap<String, Schema> schemas = new HashMap<String, Schema>(records.size());
        for (Pair<RID, String> record : records)
        {
            Record r = Record.valueOf(catalogSchema, record.getValue());
            schemas.put(r.getValueForField("name").trim(), new Schema(r.getValueForField("name").trim(), Integer.parseInt(r.getValueForField("lastPageNum").trim())));
        }

        HeapFile hpfCatalogFields = new HeapFile(catalogFieldsSchema);
        records = hpfCatalogFields.getAllRecords();
        for (Pair<RID, String> record : records)
        {
            Record r = Record.valueOf(catalogFieldsSchema, record.getValue());
            Schema schema = schemas.get(r.getValueForField("schema").trim());
            schema.addField(r.getValueForField("name").trim(), Integer.parseInt(r.getValueForField("pos").trim()), Integer.parseInt(r.getValueForField("size").trim()));
        }

        HashMap<String, HeapFile> heapFiles = new HashMap<String, HeapFile>(schemas.size());
        for (Schema schema : schemas.values())
        {
            heapFiles.put(schema.getSchemaName(), new HeapFile(schema));
        }

        return new SystemCatalog(heapFiles);
    }

    HashMap<String, HeapFile> heapFiles;
    HeapFile catalogHeap;
    HeapFile catalogFieldsHeap;
}
