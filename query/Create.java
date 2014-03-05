package query;

import fs.Schema;
import fs.SystemCatalog;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Create extends Query {

    public Create(Schema _schema)
    {
        schema = _schema;
    }

    public Schema getSchema()
    {
        return schema;
    }

    @Override
    public void execute()
    {
        SystemCatalog sc = SystemCatalog.getInstance();
        if (sc.createTable(schema))
        {
            System.out.println("Successfully created table " + schema.getSchemaName());
        }
        else
        {
            System.out.println("Table " + schema.getSchemaName() + " already exists. You will have to drop it first if you want to recreate it.");
        }
    }

    private Schema schema;

    static final Pattern CREATE_PATTERN = Pattern.compile("create\\s+table\\s+(\\w+)\\s+\\(", Pattern.CASE_INSENSITIVE);
    static final Pattern FIELD_PATTERN = Pattern.compile("^(\\w+)\\s+CHAR\\s*\\((\\d+)\\)\\s*(,|\\);?)$", Pattern.CASE_INSENSITIVE);
    public static Create parseCreate(String line, BufferedReader reader)
    {
        Matcher m = CREATE_PATTERN.matcher(line);
        if (!m.matches())
        {
            System.out.println("Error matching CREATE(1)");
            return null;
        }

        Schema schema = new Schema(m.group(1), 0);

        try
        {
            line = reader.readLine();
            for (int i = 0; line != null; line = reader.readLine(), i++)
            {
                line = line.trim();
                m = FIELD_PATTERN.matcher(line);
                if (!m.matches())
                {
                    System.out.println("Error matching CREATE(2)("+i+")");
                    return null;
                }

                schema.addField(m.group(1), i, Integer.parseInt(m.group(2)));
                // ")" or ");" was matched at end of line. This is the end of the definition
                if (!m.group(3).equals(","))
                    break;
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }

        return schema.getFieldCount() > 0 ? new Create(schema) : null;
    }
}
