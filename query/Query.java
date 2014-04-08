package query;

import java.io.BufferedReader;
import java.io.FileReader;

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
                if (line.isEmpty())
                    continue;
                if (line.toLowerCase().startsWith("create table"))
                    Create.parseCreate(line, reader).execute();
                else if (line.toLowerCase().startsWith("insert into"))
                    Insert.parseInsert(line, reader).execute();
                else if (line.toLowerCase().startsWith("select"))
                    Select.parseSelect(line, reader).execute();
                else if (line.toLowerCase().startsWith("drop table"))
                     Drop.parserDrop(line, reader).execute();
            }
        } catch (Exception ex)
        {
            System.out.println( "Received Value: "+ ex.getMessage());
        }
    }

    protected String table;
}
