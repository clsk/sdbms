import fs.*;
import query.Create;
import query.Query;

import java.lang.System;
import java.util.*;

public class Main
{
    public static void main(String[] args) {
        if (args.length > 0)
        {
            if (args[0].equals("--bootstrap"))
                bootstrapSchemas();
        }

        String scriptName = null;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-f"))
            {
                if (args.length > i+1)
                {
                    scriptName = args[++i];
                    continue;
                }
                else
                {
                    System.out.println("Error parsing arguments: input file not specified");
                    return;
                }
            }
            if (args[i].equals("--bootstrap"))
            {
                System.out.print("Bootstraping System Catalog...");
                bootstrapSchemas();
                System.out.println("done");
            }
        }

        if (scriptName != null)
        {
            System.out.println("Parsing script file " + scriptName + "...");
            Query.parse(scriptName);
        }
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
        p2.addRecord(catalogRecord.toString());

        catalogRecord.setData("schema", "SYSTEMCATALOG");
        catalogRecord.setData("name", "lastPageNum");
        catalogRecord.setData("pos", "1");
        catalogRecord.setData("size", "10");
        p2.addRecord(catalogRecord.toString());

        catalogRecord.setData("schema", "SYSTEMCATALOGFIELDS");
        catalogRecord.setData("name", "schema");
        catalogRecord.setData("pos", "0");
        catalogRecord.setData("size", "20");
        p2.addRecord(catalogRecord.toString());

        catalogRecord.setData("schema", "SYSTEMCATALOGFIELDS");
        catalogRecord.setData("name", "name");
        catalogRecord.setData("pos", "1");
        catalogRecord.setData("size", "32");
        p2.addRecord(catalogRecord.toString());


        catalogRecord.setData("schema", "SYSTEMCATALOGFIELDS");
        catalogRecord.setData("name", "pos");
        catalogRecord.setData("pos", "2");
        catalogRecord.setData("size", "3");
        p2.addRecord(catalogRecord.toString());

        catalogRecord.setData("schema", "SYSTEMCATALOGFIELDS");
        catalogRecord.setData("name", "size");
        catalogRecord.setData("pos", "3");
        catalogRecord.setData("size", "5");
        p2.addRecord(catalogRecord.toString());

        Disk.writePage(p2);
        Disk.writeHead(SystemCatalog.getCatalogFieldsSchema(), new Head(0, -1));

        return new Pair<Schema, Schema> (SystemCatalog.getCatalogSchema(), SystemCatalog.getCatalogFieldsSchema() );
    }


}
