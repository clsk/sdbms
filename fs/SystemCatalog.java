package fs;

import java.util.HashMap;

public class SystemCatalog {

    public SystemCatalog(HashMap<String, HeapFile> _heapFiles)
    {
        heapFiles = _heapFiles;
    }

    public void createTable(Schema schema)
    {
        // Insert schema into system catalog table
        // create first page

    }

    HashMap<String, HeapFile> heapFiles;
    HeapFile catalogHeap;
    HeapFile catalogFieldsHeap;
}
