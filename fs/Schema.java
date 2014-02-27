package fs;

import java.util.HashMap;
import java.util.Map;


public class Schema
{
    public Schema(String name)
    {
        fields = new HashMap<String, Integer>();
        recordLength = 0;
        this.name = name;
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

    public String getName() {
		return name;
	}

	private HashMap<String, Integer> fields;
    private int recordLength;
    private String name = null;
}
