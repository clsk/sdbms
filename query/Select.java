package query;

import fs.*;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map.Entry;

public class Select extends Query {
    public Select(HeapFile _hf)
    {
        hf = _hf;
    }

    private HeapFile hf;

    @Override
    public void execute()
    {
        // Print header
        System.out.print("RID");
        int len = 0;
        int i = 0;
        for (Entry<String, FieldValue> column : hf.getSchema().getSortedFields())
        {
            System.out.print(" | " + Utilities.padRight(column.getKey(), column.getValue().size));
            len += column.getValue().size;
            i++;
        }
        System.out.println(" |");
        char[] array = new char[(i*3) + len + 5];
        Arrays.fill(array, '-');
        System.out.println(new String(array));

        // Print Records
        ArrayList<Pair<RID, String>> records =  hf.getAllRecords();
        for (Pair<RID, String> record : records)
        {
            Record r = Record.valueOf(hf.getSchema(), record.getValue());
            System.out.print(record.getKey());
            for (String column : r.getData())
            {
                System.out.print(" | " + column);
            }
            System.out.println(" |");
        }
    }

    static final Pattern SELECT_PATTERN = Pattern.compile("select\\s+\\*\\s+from\\s+(\\w+);?", Pattern.CASE_INSENSITIVE);

    static public Select parseSelect(String line, BufferedReader reader)
    {

        Matcher m = SELECT_PATTERN.matcher(line);
        if (!m.matches())
        {
            System.out.println(line);
            System.out.println("Error matching INSERT statement");
            return null;
        }

        HeapFile hf = SystemCatalog.getInstance().getTable(m.group(1));
        if (hf == null)
        {
            System.out.println("Insert Error: Table " + m.group(1) + " does not exist!");
        }

        return new Select(hf);

    }
}
