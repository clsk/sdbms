package fs;

import java.util.ArrayList;
import java.util.BitSet;

public class HeapFile
{
    public HeapFile(Schema _schema)
    {
        schema = _schema;
        head = Disk.readHead(schema);
        free = Disk.readPage(schema, head.getFree());
        occupied = Disk.readPage(schema, head.getOccupied());
    }

    public HeapFile(Schema _schema, Head _head, Page _free, Page _occupied)
    {
        schema = _schema;
        head = _head;
        setFree(_free);
        setOccupied(_occupied);
    }

    public RID addRecord(String record)
    {
        int id = free.addRecord(record);
        RID rid = new RID(free.getID(), id);
        if (free.getEmptySlotCount() == 0)
        {
            if (occupied != null)
            {
                occupied.setPrevPage(free.getID());
                free.setNextPage(occupied.getID());
                Disk.writePage(occupied);
            }

            Page newFree;
            if (free.getNextPage() == Page.NULL_ID) {
                newFree = new Page(schema, schema.getNewPageNum());
                newFree.setNextPage(Page.NULL_ID);
            } else {
                newFree = Disk.readPage(schema, free.getNextPage());
            }
            newFree.setPrevPage(Page.NULL_ID);
            Disk.writePage(free);
            setOccupied(free);
            setFree(newFree);
        }

        Disk.writePage(free);
        return rid;
    }

    public RID addRecord(Record record)
    {
        return addRecord(record.toString());
    }

    public void removeRecord(RID rid)
    {
        Page page = getPage(rid.pageId);
        if (page != null)
        {
            Boolean needToMove = page.getEmptySlotCount() == 0;
            page.removeRecord(rid.offset);

            if (needToMove)
            {
                // This page was full, Move to empty list
                Page prevPage = null, nextPage = null;
                if (page.getPrevPage() != Page.NULL_ID)
                    prevPage = Disk.readPage(schema, page.getPrevPage());
                if (page.getNextPage() != Page.NULL_ID)
                    nextPage = Disk.readPage(schema, page.getNextPage());

                if (prevPage == null)
                {
                    setOccupied(nextPage);
                    if (nextPage != null)
                        nextPage.setPrevPage(Page.NULL_ID);
                }
                else if (nextPage != null)
                {
                    nextPage.setPrevPage(prevPage.getID());
                    prevPage.setNextPage(nextPage.getID());
                } else
                {
                    prevPage.setNextPage(Page.NULL_ID);
                }
                Disk.writePage(nextPage);
                Disk.writePage(prevPage);

                page.setNextPage(free.getID());
                free.setPrevPage(page.getID());
                Disk.writePage(free);
                setFree(page);
            }
        }

        Disk.writePage(page);
    }

    public void setRecord(RID rid, String record)
    {
        Page page = getPage(rid.pageId);
        page.setRecordValue(rid.offset, record);
        Disk.writePage(page);
    }

    public String getRecord(RID rid)
    {
        Page page = getPage(rid.pageId);
        return page.getRecord(rid.offset);
    }

    // Attempt to find a page in free and occupied list
    public Page getPage(int pageId)
    {
        Page page = null;
        if (occupied != null)
            page = getPage(occupied, pageId);

        if (page == null)
        {
            page = getPage(free, pageId);
        }

        return page;
    }

    // Attempt to find a page recursively
    public Page getPage(Page page, int pageId)
    {
        if (page.getID() == pageId)
            return page;
        else if (page.getNextPage() == Page.NULL_ID)
            return null;
        else
        {
            int nextPage = page.getNextPage();
            page = null; // set to null so stack doesn't get too big
            return Disk.readPage(schema, nextPage);
        }
    }

    // Get all records from free and occupied lists
    public ArrayList<Pair<RID, String>> getAllRecords()
    {
        ArrayList<Pair<RID, String>> records = new ArrayList<Pair<RID, String>>();
        if (occupied != null)
            records.addAll(getAllRecords(occupied));

        records.addAll(getAllRecords(free));
        return records;
    }

    // Get all records from a list recursively
    public ArrayList<Pair<RID, String>> getAllRecords(Page page)
    {
        ArrayList<Pair<RID, String>> records = new ArrayList<Pair<RID, String>>(page.getSlotCount());
        BitSet bs = page.getSlotMap();
        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1))
        {
            records.add(new Pair<RID, String>(new RID(page.getID(), i), page.getRecord(i)));
        }

        if (page.getNextPage() != Page.NULL_ID)
        {
            int nextPageId = page.getNextPage();
            page = null; // Set page to null so stack doesn't get too big
            Page nextPage = Disk.readPage(schema, nextPageId);
            records.addAll(getAllRecords(nextPage));
        }

        return records;
    }

    private void setFree(Page page)
    {
        free = page;
        head.setFree(page.getID());
    }

    private void setOccupied(Page page)
    {
        occupied = page;
        head.setOccupied(page == null ? Page.NULL_ID : page.getID());
    }

    public Schema getSchema()
    {
        return schema;
    }
    
    public void addNewField (String _colName, int _colSize){
    	schema.addField(_colName, schema.getFieldCount() + 1, _colSize);
    }

    private Schema schema;
    private Head head;
    private Page free;
    private Page occupied;
}
