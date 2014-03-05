package query;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Query {
    public Query()
    {
    }

    public void execute()
    {
    }

    public static void parse(String filename)
    {
        try {
            BufferedReader reader = new BufferedReader (new FileReader(filename));
            Integer i = 0;
            for (String line = reader.readLine(); line != null; line = reader.readLine(), i++)
            {
                line = line.trim();
                Query query = null;
                if (line.isEmpty())
                    continue;
                if (line.toLowerCase().startsWith("create table"))
                    Create.parseCreate(line, reader).execute();
                else if (line.toLowerCase().startsWith("insert into"))
                    Insert.parseInsert(line, reader).execute();
                else if (line.toLowerCase().startsWith("select"))
                    Select.parseSelect(line, reader).execute();
            }
        } catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    protected String table;
}
