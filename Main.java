import fs.*;
import java.lang.System;
import java.util.Map;

public class Main
{
    public static void main(String[] args) {
        Schema catalogSchema = new Schema("SYSTEMCATALOG");
        catalogSchema.addField("name", 0, 128);
        catalogSchema.addField("free", 1, 10);
        catalogSchema.addField("occupied", 2, 10);

        Schema catalogFieldSchema = new Schema("CATALOGFIELD");
        catalogFieldSchema.addField("catalogID", 0, 20);
        catalogFieldSchema.addField("name", 1, 32);
        catalogFieldSchema.addField("pos", 2, 3);
        catalogFieldSchema.addField("length", 3, 5);

        System.out.println("SYSTEMCATALOG Record Length: " + catalogSchema.getRecordLength());
        Page page = new Page(catalogSchema, 0);
        System.out.println("Page size: " + page.SIZE);
        System.out.println("Page capacity: " + page.getCapacity());
        System.out.println("Page Slot Count: " + page.getSlotCount());
        System.out.println("Page Empty Slots: " + page.getEmptySlotCount());

        String name = Utilities.padRight("SYSTEMCATALOG", 128);
        String free = Utilities.padRight("0", 10);
        String occupied = Utilities.padRight("-1", 10);
        String record = name + free + occupied;
        System.out.println("Record len: " + record.length());
        System.out.println("Adding record to slot: " +  page.addRecord(record));

        Disk.writePage(page);

        Page rPage = Disk.readPage(catalogSchema, 0);
        System.out.println("Occupied slots: " + rPage.getSlotMap());

        for (Map.Entry<String, FieldValue> field : catalogFieldSchema.getSortedFields())
        {
            System.out.println("" + field.getValue().pos + ": " + field.getKey() + "(" + field.getValue().size + ")");
        }
    }
}
