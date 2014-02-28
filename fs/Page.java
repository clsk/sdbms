package fs;

import java.lang.Error;
import java.util.Arrays;
import java.util.BitSet;

public class Page
{
    public static int SIZE = 16*1024; // 16kb

    public Page(Schema _schema, int _id)
    {
        id = _id;
        schema = _schema;
        capacity = SIZE / _schema.getRecordLength();
        int t = SIZE - capacity;
        if (t > ((capacity+7)/8) + 8) {
            capacity--;
        }

        slotMap = new BitSet(capacity);

        slots = new String[capacity];
        for (int i = 0; i < slots.length; i++)
        {
            char[] empty = new char[schema.getRecordLength()];
            Arrays.fill(empty, ' ');
            slots[i] = new String(empty);
        }
    }

    public Page(Schema _schema, String[] _records, BitSet _slotMap)
    {
        schema = _schema;
        slots = _records;
        slotMap = _slotMap;
        capacity = _records.length;
        slotCount = slotMap.cardinality();
    }

    public int addRecord(String record)
    {
        if (slotCount >= capacity) {
            throw new Error("Page is full");
        }

        int i = slotMap.nextSetBit(0);
        slots[i] = record;
        return ++slotCount;
    }

    public void removeRecord(int slot)
    {
        slotMap.clear(slot);
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

    // Atributo para Puntero/Llave de la Pagina Anterior.
    private int prevPage;
    // Atributo para Puntero/Llave de la Pagina Siguiente.
    private int nextPage;
}
