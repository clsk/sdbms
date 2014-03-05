package query;

import fs.*;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.Map.Entry;

public class Insert extends Query {

    Insert(HeapFile _hf, Record _record)
    {
        hf = _hf;
        record = _record;
    }

    @Override
    public void execute()
    {
        RID rid = hf.addRecord(record);
        System.out.println("Inserted record at: " + rid);
    }

    private HeapFile hf;
    private Record record;

    static final Pattern T_PATTERN = Pattern.compile("insert\\s+into\\s+(\\w+)\\s*(\\((\\s*\\w+\\s*,?\\s*)+\\))?\\s+values\\s*(\\((\\s*\\'[^\\']+\\'\\s*,?\\s*)+\\));?", Pattern.CASE_INSENSITIVE);

    static public Insert parseInsert(String line, BufferedReader reader)
    {
        Matcher m = T_PATTERN.matcher(line);
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

        String[] columns = null;
        if (m.group(2) != null)
        {
            // Parse parameters
            // Get rid of outside parenthesis, then split in commas
            columns = m.group(2).substring(1, m.group(2).length()-2).split(",");
            // Check if schema has this field
            for (String column : columns)
            {
                if (!hf.getSchema().hasField(column))
                {
                     System.out.println("Insert Error: Schema " + hf.getSchema().getSchemaName() + " does not have a column named " + column + ".");
                    return null;
                }
            }
        }

        // Parse values
        // Get rid of parenthesis
        String valStr = m.group(4).substring(1, m.group(4).length()-1);
        // read from quote to quote, ignoring escaped quotes
        ArrayList<String> values = new ArrayList<String>();
        int iStartQuote = -1; // index of last start quote
        for (int i = 0; i != -1; i = valStr.indexOf('\'', i+1))
        {
            if (iStartQuote == -1)
            {
                iStartQuote = i;
            }
            else
            {
                values.add(valStr.substring(iStartQuote+1, i));
                iStartQuote = -1;
            }
        }

        Record r = new Record(hf.getSchema());
        if (columns != null)
        {
            if (columns.length != values.size())
            {
                System.out.println("Insert Error: Amount of columns and values does not match");
                return null;
            }

            for (int i = 0; i < columns.length; i++)
            {
                r.setData(columns[i], values.get(i));
            }
        }
        else
        {
            if (values.size() != hf.getSchema().getFieldCount())
            {
                System.out.println("Insert Error: Schema " + hf.getSchema().getSchemaName() + " has " + hf.getSchema().getFieldCount() + " columns and you have only provided " + values.size() + " values");
                return null;
            }


            List<Entry<String, FieldValue>> c = hf.getSchema().getSortedFields();
            int i = 0;
            for (Entry<String, FieldValue> column : c)
            {
                r.setData(column.getKey(), values.get(i));
                i++;
            }
        }


        return new Insert(hf, r);
    }
}
