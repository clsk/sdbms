package fs;


import java.util.List;
import java.util.Map.Entry;

public class Record
{
    public Record(Schema _schema)
    {
        data = new String[_schema.getFieldCount()];
        schema = _schema;
    }

    public Record(Schema _schema, String [] _data)
    {
        data = _data;
        schema = _schema;
    }

    public void setData(Pair<String, String> [] fields)
    {
        for (Pair<String, String> field : fields)
        {
            FieldValue fv = schema.getFieldPair(field.getKey());
            data[fv.pos] = Utilities.padRight(field.getValue(), fv.size);
        }
    }

    public void setData (String field, String value)
    {
        FieldValue fv = schema.getFieldPair(field);
        data[fv.pos] = Utilities.padRight(value, fv.size);
    }
    
    public String getValueForField(String field)
    {
        Integer pos = schema.getFieldPos(field);
        return pos != null ? data[pos] : null;
    }

    public String[] getData()
    {
        return data;
    }

    @Override
    public String toString()
    {
        String record = "";
        for (String field : data)
        {
            record += field;
        }

        return record;
    }

    public static Record valueOf(Schema schema, String record)
    {
        List<Entry<String, FieldValue>> fields = schema.getSortedFields();
        String[] data = new String[schema.getFieldCount()];
        int i = 0, index = 0;
        for (Entry<String, FieldValue> field : fields)
        {
            data[i] = record.substring(index, index+field.getValue().size);
            index += field.getValue().size;
            i++;
        }

        return new Record(schema, data);
    }
        
    private String[] data;
    private Schema schema;
    
	public void setData (String[] _newData) {
		data = _newData;
	}

}
