import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;


public class BatchInsert {
    public static void main (String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        long startTime = System.currentTimeMillis();
        Connection conn = null;

        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        String jdbcURL = "jdbc:mysql://localhost:3306/moviedb";

        try {
            conn = DriverManager.getConnection(jdbcURL, "mytestuser", "My6$Password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        InsertActor insertActor = new InsertActor();
        insertActor.runMe(conn);
        InsertMovie insertMovie = new InsertMovie();
        insertMovie.runMe(conn);
        InsertCast insertCast = new InsertCast();
        insertCast.runMe(conn);

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
