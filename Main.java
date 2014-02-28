import fs.*;
import java.lang.System;

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
        Disk.writePage(page);
    }
}
