package fs;

public class HeapFile
{
    public HeapFile(Schema _schema, Page _free, Page _occupied)
    {
        schema = _schema;
        free = _free;
        occupied = _occupied;
    }

    public RID addRecord(String record)
    {
        int id = free.addRecord(record);
        RID rid = new RID(free.getID(), id);
        if (free.getEmptySlotCount() == 0)
        {
            occupied.setPrevPage(free.getID());
            free.setNextPage(occupied.getID());
            Disk.writePage(occupied);
            Disk.writePage(free);
            Page newFree;
            if (free.getNextPage() == -1) {
                newFree = new Page(schema, schema.getNewPageNum());
                newFree.setNextPage(-1);
            } else {
                newFree = Disk.readPage(schema, free.getNextPage());
            }
            newFree.setPrevPage(-1);
            occupied = free;
            free = newFree;
        }

        Disk.writePage(free);
        return rid;
    }

    public void removeRecord(RID rid)
    {
        Page page = getPage(rid.pageId);
        if (page != null)
            page.removeRecord(rid.offset);

        Disk.writePage(page);
    }

    public void setRecord(RID rid, String record)
    {
        Page page = getPage(rid.pageId);
        page.setRecordValue(rid.offset, record);
        Disk.writePage(page);
    }

    public Page getPage(int pageId)
    {
        Page page = getPage(occupied, pageId);
        if (page == null)
        {
            page = getPage(free, pageId);
        }

        return page;
    }

    public Page getPage(Page page, int pageId)
    {
        if (page.getID() == pageId)
            return page;
        else if (page.getNextPage() == -1)
            return null;
        else
            return Disk.readPage(schema, page.getNextPage());
    }

    Schema schema;
    Page free;
    Page occupied;
}
