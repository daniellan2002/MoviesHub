import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.CallableStatement;
import java.util.List;
import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;
/*

The SQL command to create the table ft.

DROP TABLE IF EXISTS ft;
CREATE TABLE ft (
    entryID INT AUTO_INCREMENT,
    entry text,
    PRIMARY KEY (entryID),
    FULLTEXT (entry)) ENGINE=MyISAM;

*/

/*

Note: Please change the username, password and the name of the datbase.

*/

public class BatchInsertActor {

    //private int maxStarId = 0;

    public static void main (String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        long startTime = System.currentTimeMillis();
        Connection conn = null;

        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedb";

        try {
            conn = DriverManager.getConnection(jdbcURL,"mytestuser", "My6$Password");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int[] iNoRows=null;

        String sqlInsertRecord;

        try {
            conn.setAutoCommit(false);
            FileWriter writer = new FileWriter("inconsistency_report.txt", true);
            //HASH original table
            String query = "SELECT DISTINCT name FROM stars_bp;";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                Actor.namesTable.put(name, true);
            }

            //set maxID
            int maxStarId = 0;
            PreparedStatement findMaxStatement = conn.prepareStatement("SELECT MAX(RIGHT(id, 7)) as max_id FROM stars_bp");
            ResultSet maxRS = findMaxStatement.executeQuery();
            if (maxRS.next()) {
                maxStarId =  Integer.parseInt(maxRS.getString("max_id"));
            }

            //parse actors
            ParseActor parserActor = new ParseActor();
            parserActor.runExample();
            List<Actor> actors = parserActor.actors;

            PreparedStatement psInsertRecord= null;
            sqlInsertRecord= "insert into stars_bp (id, name, birthYear) values(?,?,?)";

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
                System.out.println("newId " + newStarId);
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

        try {
            //if(psInsertRecord!=null) psInsertRecord.close();
            if(conn!=null) conn.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        long elapsedSeconds = elapsedTime / 1000;
        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;

        System.out.println("Elapsed time: " + minutes + " minutes " + seconds + " seconds");
    }

}


