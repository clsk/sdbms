package fs;

import java.lang.Number;

public class FieldValue implements Comparable<FieldValue> {
	public int pos;
	public int size;

    @Override
    public int compareTo(FieldValue f)
    {
       return pos - f.pos;
    }

	public FieldValue (int _pos, int _size){
        pos = _pos;
        size = _size;
	}

    public FieldValue(FieldValue f)
    {
        pos = f.pos;
        size = f.size;
    }
}
