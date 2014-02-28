package fs;

public class RID {
    public int pageId;
    public int offset;


    public String toString()
    {
        return String.valueOf(pageId) + String.valueOf(offset);
    }

    public int toInteger()
    {
        return (pageId*10)+offset;
    }
}
