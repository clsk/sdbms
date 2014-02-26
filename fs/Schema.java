package fs;
import java.util.HashMap;
import java.util.Map;


public class Schema
{
    public Schema()
    {
        fields = new HashMap<String, Integer>();
        recordLength = 0;
    }

    public void addField(String name, int size)
    {
        fields.put(name, size);
        recordLength += 2 * size;
    }

    public int getRecordLength()
    {
        return recordLength;
    }

    private HashMap<String, Integer> fields;
    private int recordLength;
}
