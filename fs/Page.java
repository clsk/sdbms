package fs;

import java.lang.Error;
import java.util.Arrays;
import java.util.BitSet;

public class Page
{
    public static int SIZE = 16*1024; // 16kb
    public static int NULL_ID = -1;

    public Page(Schema _schema, int _id)
    {
        id = _id;
        schema = _schema;
        capacity = calcCapacity(_schema.getRecordLength());

        slotMap = new BitSet(capacity);

        slots = new String[capacity];
        for (int i = 0; i < slots.length; i++)
        {
            char[] empty = new char[schema.getRecordLength()];
            Arrays.fill(empty, ' ');
            slots[i] = new String(empty);
        }

        nextPage = NULL_ID;
        prevPage = NULL_ID;
    }

    public static int calcCapacity(int recordLength)
    {
        int capacity = SIZE / recordLength;
        int remainingSpace = SIZE - (capacity*recordLength);

        while (remainingSpace < (capacity/8+1) + 8) {
            capacity--;
            remainingSpace += SIZE-(capacity*recordLength);
        }

        return capacity;
    }

    public Page(Schema _schema, String[] _records, int _prevPage, int _nextPage, BitSet _slotMap)
    {
        schema = _schema;
        slots = _records;
        slotMap = _slotMap;
        capacity = _records.length;
        slotCount = slotMap.cardinality();
        prevPage = _prevPage;
        nextPage = _nextPage;
    }

    // Returns slot record was inserted into
    public int addRecord(String record)
    {
        if (slotCount >= capacity) {
            throw new Error("Page is full");
        }

        int i = slotMap.nextClearBit(0);
        slots[i] = record;
        slotMap.set(i);
        slotCount++;
        return i;
    }

    public void setRecordValue(int slot, String record)
    {
        slotMap.set(slot);
        slots[slot] = record;
    }

    public Boolean removeRecord(int slot)
    {
        if (slotMap.get(slot))
        {
            slotMap.clear(slot);
            return true;
        } else {
            return false;
        }
    }



    public String getRecord(int slot)
    {
        return slotMap.get(slot) ? slots[slot] : null;
    }

    public String[] getRecords()
    {
        return slots;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public int getSlotCount()
    {
        return slotCount;
    }

    public int getEmptySlotCount()
    {
        return capacity - slotCount;
    }

    public void setSlotMap(BitSet map)
    {
        slotMap = map;
    }

    public BitSet getSlotMap()
    {
        return slotMap;
    }

    public Schema getSchema(){
    	return this.schema;
    }
    
    public int getPrevPage()
    {
        return prevPage;
    }

    public void setPrevPage(int pageId)
    {
        prevPage = pageId;
    }

    public int getNextPage()
    {
        return nextPage;
    }

    public void setNextPage(int pageId)
    {
        nextPage = pageId;
    }
    
    public int getID()
    {
        return this.id;
    }

    public void setID(int id)
    {
        this.id = id;
    }

    // Private Members
    private int id;
    private Schema schema;
    private String[] slots;
    private int capacity;
    private int slotCount;
    private BitSet slotMap;
    private int prevPage;
    private int nextPage;
}
