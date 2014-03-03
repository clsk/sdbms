import fs.*;
import java.lang.System;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;

public class Main
{
    public static void main(String[] args) {
        Schema catalogSchema = new Schema("SYSTEMCATALOG", 0);
        catalogSchema.addField("name", 0, 128);
        catalogSchema.addField("lastPageNum", 3, 10);

        System.out.println("SYSTEMCATALOG Record Length: " + catalogSchema.getRecordLength());
        Page page = new Page(catalogSchema, 0);
        System.out.println("Page size: " + page.SIZE);
        System.out.println("Page capacity: " + page.getCapacity());
        System.out.println("Page Slot Count: " + page.getSlotCount());
        System.out.println("Page Empty Slots: " + page.getEmptySlotCount());

        String name = Utilities.padRight("SYSTEMCATALOG", 128);
        String free = Utilities.padRight("0", 10);
        String occupied = Utilities.padRight("-1", 10);
        String lastPageNum = Utilities.padRight("0", 10);
        String record = name + free + occupied + lastPageNum;
        System.out.println("Record len: " + record.length());
        System.out.println("Adding record to slot: " +  page.addRecord(record));

        Disk.writePage(page);

        for (Map.Entry<String, FieldValue> field : catalogSchema.getSortedFields())
        {
            System.out.println("" + field.getValue().pos + ": " + field.getKey() + "(" + field.getValue().size + ")");
        }


        // SYSTEMCATALOGFIELDS
        Schema catalogFieldSchema = new Schema("SYSTEMCATALOGFIELDS", 0);
        catalogFieldSchema.addField("catalogID", 0, 20);
        catalogFieldSchema.addField("name", 1, 32);
        catalogFieldSchema.addField("pos", 2, 3);
        catalogFieldSchema.addField("length", 3, 5);
        Record catalogRecord = new Record(catalogFieldSchema);
        catalogRecord.setData("catalogID", "0|0");
        catalogRecord.setData("name", "name");
        catalogRecord.setData("pos", "0");
        catalogRecord.setData("length", "128");
        Page p2 = new Page(catalogFieldSchema, 0);

        catalogRecord.setData("catalogID", "0|0");
        catalogRecord.setData("name", "free");
        catalogRecord.setData("pos", "1");
        catalogRecord.setData("length", "10");
        String rs = catalogRecord.toString();
        System.out.println(p2.addRecord(rs));

        catalogRecord.setData("catalogID", "0|0");
        catalogRecord.setData("name", "occupied");
        catalogRecord.setData("pos", "2");
        catalogRecord.setData("length", "10");
        System.out.println(p2.addRecord(catalogRecord.toString()));

        catalogRecord.setData("catalogID", "0|0");
        catalogRecord.setData("name", "lastPageNum");
        catalogRecord.setData("pos", "3");
        catalogRecord.setData("length", "10");
        System.out.println(p2.addRecord(catalogRecord.toString()));
        Disk.writePage(p2);

        Schema[] schemas = readCatalog(catalogSchema, catalogFieldSchema);
        for (Schema schema : schemas)
        {
            System.out.println("Schema: " + schema.getSchemaName());
        }
    }

    static private Schema[] readCatalog(Schema catalogSchema, Schema catalogFieldsSchema)
    {
         // Read Catalog
        HeapFile hpf = new HeapFile(catalogSchema);
        ArrayList<Pair<RID, String>> records = hpf.getAllRecords();
        Schema[] ret = new Schema[records.size()];
        int i = 0;
        for (Pair<RID, String> record : records)
        {
            Record r = Record.valueOf(catalogSchema, record.getValue());
            ret[i] = new Schema(r.getValueForField("name").trim(), Integer.parseInt(r.getValueForField("lastPageNum").trim()));
            i++;
        }

        return ret;
    }
}
