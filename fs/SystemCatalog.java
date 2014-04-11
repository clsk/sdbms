package fs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

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
        if ( !heapFiles.containsKey(schema.getSchemaName()) )
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
                heapFiles.remove(schemaName);
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

    public boolean addColumn(String table, String name, Integer length)
    {
        // Check if column doesn't exist
        HeapFile hf = getTable(table);
        Schema oldSchema = hf.getSchema();

        if (oldSchema.hasField(name))
        {
            System.out.println(1);
            return false;
        }
        
        // Get all records
        ArrayList<Pair<RID, String>> records = hf.getAllRecords();
        
        //Destroying HeapFile on Memory
        hf = null;
        
        // Drop table
        dropTable(table);
        
        // Create new schema
        Schema newSchema = new Schema(oldSchema);
        newSchema.addField(name, length);
        
        // Recreate table
        if (createTable(newSchema) == false)
        {
            System.out.println(2);
            return false;
        }

        hf = getTable(table);
        
        //For fill the values for the new column
        char [] _emptyValue = new char [length];
        Arrays.fill( _emptyValue, '#');
        String _newValue = new String (_emptyValue);
        
        // Reinsert records
        for (Pair<RID, String> record : records)
        {
            Record oldRecord = Record.valueOf(oldSchema, record.getValue());
            Record newRecord = new Record(newSchema);
            for (Entry<String, FieldValue> entry : oldSchema.getFields().entrySet())
            {
            	newRecord.setData(entry.getValue(), oldRecord.getData()[entry.getValue().pos]);
            }
            newRecord.setData(name, _newValue);

            hf.addRecord(newRecord);
        }
        
        records.clear();
  
        return true;
    }

    public boolean resizeColumn(String table, String name, Integer length)
    {
          // Check if column doesn't exist
        HeapFile hf = getTable(table);
        Schema oldSchema = hf.getSchema();

        if (oldSchema.hasField(name))
            return false;
        // Get all records
        ArrayList<Pair<RID, String>> records = hf.getAllRecords();
        // Drop table
        dropTable(table);
        // Create new schema
        Schema newSchema = new Schema(oldSchema);
        newSchema.addField(name, length);
        
        //Limpiado el HeapFile
        hf = null;
        
        // Recreate table
        createTable(newSchema);
        
        //Cargando la tabla recreada.
        hf = getTable(table);
        		
        // Reinsert records
        for (Pair<RID, String> record : records)
        {
            Record oldRecord = Record.valueOf(oldSchema, record.getValue());
            Record newRecord = new Record(newSchema);
            for (Entry<String, FieldValue> entry : oldSchema.getFields().entrySet())
            {
                if (entry.getKey().equals(name.toLowerCase()))
                    newRecord.setData(entry.getValue(), oldRecord.getData()[entry.getValue().pos].substring(0, length < entry.getValue().size ? length : entry.getValue().size));
                else
                    newRecord.setData(entry.getValue(), oldRecord.getData()[entry.getValue().pos]);
            }
            //Reescribiendo records.
            hf.addRecord(newRecord);
        }

        return true;
    }

     public boolean removeColumn(String table, String name)
    {
          // Check if column doesn't exist
        HeapFile hf = getTable(table);
        Schema oldSchema = hf.getSchema();

        if (!oldSchema.hasField(name))
            return false;
        // Get all records
        ArrayList<Pair<RID, String>> records = hf.getAllRecords();
        
        // Drop table
        dropTable(table);
        
        //Cleaning HF
        hf = null;
        
        // Create new schema
        Schema newSchema = new Schema(oldSchema);
        newSchema.removeField(name);
        
        // Recreate table
        createTable(newSchema);
        
        // Getting the table
        hf = getTable(table);
        
        // Reinsert records
        for (Pair<RID, String> record : records)
        {
            Record oldRecord = Record.valueOf(oldSchema, record.getValue());
            Record newRecord = new Record(newSchema);
            for (Entry<String, FieldValue> entry : oldSchema.getFields().entrySet())
            {
                if (entry.getKey().equals(name.toLowerCase()))
                    continue;
                else
                    newRecord.setData(entry.getValue(), oldRecord.getData()[entry.getValue().pos]);
            }
            //Reescribiendo Records.
            hf.addRecord(newRecord);
        }
        
        records.clear();
        
        //This step is for test.
        /*
         * Se obtendran los records que han sido insertados en el HeapFile.
         * El error se genera cuando se intenta realizar un getAllRecords en otro command.
         * Por Ejemplo:
         * 	- Se realiza un ALTER DROP, primero.
         * 	- Se verifica en la carpeta de la Base de Datos la modificacion del esquema de la tabla.
         * 	- Luego se realiza un SELECT commmand y no se cargan los records contenidos en el page.
         */
        records = hf.getAllRecords();
        
        return true;
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
