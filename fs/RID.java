package fs;

public class RID {
    public int pageId;
    public int offset;

    public RID (int _pageId, int _offset)
    {
        pageId = _pageId;
        offset = _offset;
    }

    public String toString()
    {
        return String.valueOf(pageId) + "|" + String.valueOf(offset);
    }

    public static RID valueOf(String str)
    {
        String[] parts = str.split("|");
        return parts.length == 2 ? new RID(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])) : null;
    }
}
