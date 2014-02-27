package fs;

import java.util.HashMap;

public class Schema
{
    public Schema(String name)
    {
        fields = new HashMap <String, Pair <Integer, Integer>>();
        this.name = name;
    }

    public void addField(String name, Pair <Integer, Integer> PosLength)
    {
    	PosLength.pair = PosLength.pair * 2;
    	fields.put(name, PosLength);
    }

    public String getName() {
		return name;
	}

	private HashMap<String, Pair<Integer, Integer>> fields;
    private String name = null;
}
