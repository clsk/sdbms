import fs.*;
import java.lang.System;
import java.util.*;

public class Main
{
    public static void main(String[] args) {
        Schema catalogSchema = new Schema("SYSTEMCATALOG", 0);
        catalogSchema.addField("name", 0, 128);
        catalogSchema.addField("lastPageNum", 1, 10);

        System.out.println("SYSTEMCATALOG Record Length: " + catalogSchema.getRecordLength());
        Page page = new Page(catalogSchema, 0);
        System.out.println("Page size: " + page.SIZE);
        System.out.println("Page capacity: " + page.getCapacity());
        System.out.println("Page Slot Count: " + page.getSlotCount());
        System.out.println("Page Empty Slots: " + page.getEmptySlotCount());

        String name = Utilities.padRight("SYSTEMCATALOG", 128);
        String lastPageNum = Utilities.padRight("0", 10);
        String record = name + lastPageNum;
        System.out.println("Record len: " + record.length());
        System.out.println("Adding record to slot: " +  page.addRecord(record));

        Disk.writePage(page);
        Disk.writeHead(catalogSchema, new Head(0, -1));

        for (Map.Entry<String, FieldValue> field : catalogSchema.getSortedFields())
        {
            System.out.println("" + field.getValue().pos + ": " + field.getKey() + "(" + field.getValue().size + ")");
        }


        // SYSTEMCATALOGFIELDS
        Schema catalogFieldSchema = new Schema("SYSTEMCATALOGFIELDS", 0);
        catalogFieldSchema.addField("schema", 0, 20);
        catalogFieldSchema.addField("name", 1, 32);
        catalogFieldSchema.addField("pos", 2, 3);
        catalogFieldSchema.addField("size", 3, 5);
        Record catalogRecord = new Record(catalogFieldSchema);
        catalogRecord.setData("schema", "SYSTEMCATALOG");
        catalogRecord.setData("name", "name");
        catalogRecord.setData("pos", "0");
        catalogRecord.setData("size", "128");
        Page p2 = new Page(catalogFieldSchema, 0);

        catalogRecord.setData("schema", "SYSTEMCATALOG");
        catalogRecord.setData("name", "free");
        catalogRecord.setData("pos", "1");
        catalogRecord.setData("size", "10");
        String rs = catalogRecord.toString();
        System.out.println(p2.addRecord(rs));

        catalogRecord.setData("schema", "SYSTEMCATALOG");
        catalogRecord.setData("name", "occupied");
        catalogRecord.setData("pos", "2");
        catalogRecord.setData("size", "10");
        System.out.println(p2.addRecord(catalogRecord.toString()));

        catalogRecord.setData("schema", "SYSTEMCATALOG");
        catalogRecord.setData("name", "lastPageNum");
        catalogRecord.setData("pos", "3");
        catalogRecord.setData("size", "10");
        System.out.println(p2.addRecord(catalogRecord.toString()));
        Disk.writePage(p2);
        Disk.writeHead(catalogFieldSchema, new Head(0, -1));

        Collection<Schema> schemas = buildCatalog(catalogSchema, catalogFieldSchema).values();
        for (Schema schema : schemas)
        {
            System.out.println("Schema: " + schema.getSchemaName());
        }
    }

    static private HashMap<String, Schema> buildCatalog(Schema catalogSchema, Schema catalogFieldsSchema)
    {
         // Read Catalog
        HeapFile hpfCatalog = new HeapFile(catalogSchema);
        ArrayList<Pair<RID, String>> records = hpfCatalog.getAllRecords();
        HashMap<String, Schema> schemas = new HashMap<String, Schema>();
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

        return schemas;
    }
}
