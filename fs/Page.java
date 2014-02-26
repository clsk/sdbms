package fs;

import java.lang.Error;

public class Page
{
    public static short SIZE = 16*1024; // 16kb

    public Page(Schema _schema)
    {
        schema = _schema;
        capacity = SIZE / _schema.getRecordLength();
        records = new String[capacity];
    }

    Schema schema;

    public int addRecord(String record)
    {
        if (currentSize < capacity) {
            records[currentSize] = record;
            currentSize++;
        } else {
            throw new java.lang.Error("Page is full");
        }

        return currentSize;
    }

    public String[] getRecords()
    {
        return records;
    }

    public int getCapacity()
    {
        return capacity;
    }

    private String[] records;
    private int capacity;
    private int currentSize;
}
