import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InsertActor {
    public void runMe(Connection conn)
    {
        int[] iNoRows=null;

        String sqlInsertRecord;

        try {
            conn.setAutoCommit(false);
            FileWriter writer = new FileWriter("inconsistency_report.txt", true);
            //HASH original table
            String query = "SELECT DISTINCT name FROM stars;";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                Actor.namesTable.put(name, true);
            }

            //set maxID
            int maxStarId = 0;
            PreparedStatement findMaxStatement = conn.prepareStatement("SELECT MAX(RIGHT(id, 7)) as max_id FROM stars");
            ResultSet maxRS = findMaxStatement.executeQuery();
            if (maxRS.next()) {
                maxStarId =  Integer.parseInt(maxRS.getString("max_id"));
            }

            //parse actors
            ParseActor parserActor = new ParseActor();
            parserActor.runExample();
            List<Actor> actors = parserActor.actors;

            PreparedStatement psInsertRecord= null;
            sqlInsertRecord= "insert into stars (id, name, birthYear) values(?,?,?)";

            psInsertRecord = conn.prepareStatement(sqlInsertRecord);
            for(int i=1;i < actors.size();i++)
            {
                String starName = actors.get(i).getStagename();
                int birthYear = actors.get(i).getDob();

                if(Actor.namesTable.containsKey(starName))
                {
                    System.out.println("Star name already exists in the table: " + starName);
                    writer.write("Star name already exists in the table: " + starName + "\n");
                    continue;
                }

                maxStarId += 1;
                String padded_id_numeric = String.format("%07d", maxStarId);
                String newStarId = "nm" + padded_id_numeric;

                psInsertRecord.setString(1, newStarId);

                psInsertRecord.setString(2, starName);

                if(birthYear != 0)
                {
                    psInsertRecord.setInt(3, birthYear);
                }
                else {
                    psInsertRecord.setObject(3,null);
                }
                psInsertRecord.addBatch();
                //System.out.println("add " + psInsertRecord);
            }

            if(psInsertRecord != null)
            {
                iNoRows = psInsertRecord.executeBatch();
                System.out.println("what's this");
            }

            conn.commit();
            //conn2.commit();
            if(psInsertRecord!=null) psInsertRecord.close();
            writer.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
