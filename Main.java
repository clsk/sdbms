import fs.*;
import query.Create;
import query.Query;

import java.lang.System;
import java.util.*;

public class Main
{
    public static void main(String[] args) {
//        bootstrapSchemas();
        if (args.length > 1)
        {
            if (args[1] == "--bootstrap")
                bootstrapSchemas();
        }

        System.out.println("1st record: " + SystemCatalog.getInstance().getTable("SYSTEMCATALOG").getRecord(new RID(0, 0)));

        Query.parse("sample.sql");
    }



    static private Pair<Schema, Schema> bootstrapSchemas()
    {
        Page page = new Page(SystemCatalog.getCatalogSchema(), 0);

        String name = Utilities.padRight("SYSTEMCATALOG", 128);
        String lastPageNum = Utilities.padRight("0", 10);
        String record = name + lastPageNum;
        page.addRecord(record);

        name = Utilities.padRight("SYSTEMCATALOGFIELDS", 128);
        lastPageNum = Utilities.padRight("0", 10);

        record = name + lastPageNum;
        page.addRecord(record);

        Disk.writePage(page);
        Disk.writeHead(SystemCatalog.getCatalogSchema(), new Head(0, -1));

        // SYSTEMCATALOGFIELDS
        Record catalogRecord = new Record(SystemCatalog.getCatalogFieldsSchema());
        catalogRecord.setData("schema", "SYSTEMCATALOG");
        catalogRecord.setData("name", "name");
        catalogRecord.setData("pos", "0");
        catalogRecord.setData("size", "128");
        Page p2 = new Page(SystemCatalog.getCatalogFieldsSchema(), 0);
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
        Disk.writeHead(SystemCatalog.getCatalogFieldsSchema(), new Head(0, -1));

        return new Pair<Schema, Schema> (SystemCatalog.getCatalogSchema(), SystemCatalog.getCatalogFieldsSchema() );
    }


}
