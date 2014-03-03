import fs.*;
import java.lang.System;
import java.util.*;

public class Main
{
    public static void main(String[] args) {
        bootstrapSchemas();
        SystemCatalog sc = buildCatalog(getCatalogSchema(), getCatalogFieldsSchema());

        System.out.println("1st record: " + sc.getTable("SYSTEMCATALOG").getRecord(new RID(0, 0)));
    }

    static Schema catalogSchema = null;

    private static Schema getCatalogSchema()
    {
        if (catalogSchema == null)
        {
            catalogSchema = new Schema("SYSTEMCATALOG", 0);
            catalogSchema.addField("name", 0, 128);
            catalogSchema.addField("lastPageNum", 1, 10);
        }

        return catalogSchema;
    }

    static Schema catalogFieldsSchema = null;

    private static Schema getCatalogFieldsSchema()
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

    static private Pair<Schema, Schema> bootstrapSchemas()
    {
        Page page = new Page(getCatalogSchema(), 0);

        String name = Utilities.padRight("SYSTEMCATALOG", 128);
        String lastPageNum = Utilities.padRight("0", 10);
        String record = name + lastPageNum;
        page.addRecord(record);

        name = Utilities.padRight("SYSTEMCATALOGFIELDS", 128);
        lastPageNum = Utilities.padRight("0", 10);

        record = name + lastPageNum;
        page.addRecord(record);

        Disk.writePage(page);
        Disk.writeHead(getCatalogSchema(), new Head(0, -1));

        // SYSTEMCATALOGFIELDS
        Record catalogRecord = new Record(getCatalogFieldsSchema());
        catalogRecord.setData("schema", "SYSTEMCATALOG");
        catalogRecord.setData("name", "name");
        catalogRecord.setData("pos", "0");
        catalogRecord.setData("size", "128");
        Page p2 = new Page(getCatalogFieldsSchema(), 0);
        System.out.println(p2.addRecord(catalogRecord.toString()));

        catalogRecord.setData("schema", "SYSTEMCATALOG");
        catalogRecord.setData("name", "lastPageNum");
        catalogRecord.setData("pos", "1");
        catalogRecord.setData("size", "10");
        System.out.println(p2.addRecord(catalogRecord.toString()));

        catalogRecord.setData("schema", "SYSTEMCATALOGFIELDS");
        catalogRecord.setData("name", "schema");
        catalogRecord.setData("pos", "0");
        catalogRecord.setData("size", "20");
        System.out.println(p2.addRecord(catalogRecord.toString()));

        catalogRecord.setData("schema", "SYSTEMCATALOGFIELDS");
        catalogRecord.setData("name", "name");
        catalogRecord.setData("pos", "1");
        catalogRecord.setData("size", "32");
        System.out.println(p2.addRecord(catalogRecord.toString()));


        catalogRecord.setData("schema", "SYSTEMCATALOGFIELDS");
        catalogRecord.setData("name", "pos");
        catalogRecord.setData("pos", "2");
        catalogRecord.setData("size", "3");
        System.out.println(p2.addRecord(catalogRecord.toString()));

        catalogRecord.setData("schema", "SYSTEMCATALOGFIELDS");
        catalogRecord.setData("name", "size");
        catalogRecord.setData("pos", "3");
        catalogRecord.setData("size", "5");
        System.out.println(p2.addRecord(catalogRecord.toString()));

        Disk.writePage(p2);
        Disk.writeHead(getCatalogFieldsSchema(), new Head(0, -1));

        return new Pair<Schema, Schema> (getCatalogSchema(), getCatalogFieldsSchema() );
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
}
