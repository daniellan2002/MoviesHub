import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

public class BatchInsertMovie {

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
        String query = "SELECT DISTINCT title FROM movies_bp;";
        try {
            conn.setAutoCommit(false);

            //HASH original table
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                MovieInfo.movieHash.put(title, true);
            }

            //parse movies
            ParseMovie parserMovie = new ParseMovie();
            parserMovie.runParse();
            List<MovieInfo> movies = parserMovie.movies;

            int maxMovieId = 0;
            PreparedStatement findMaxStatement = conn.prepareStatement("SELECT MAX(RIGHT(id, 7)) as max_id FROM stars_bp");
            ResultSet maxRS = findMaxStatement.executeQuery();
            if (maxRS.next()) {
                maxMovieId =  Integer.parseInt(maxRS.getString("max_id"));
            }

            PreparedStatement psInsertRecord= null;
            sqlInsertRecord= "insert into movies_bp (id, title, year, director) values(?,?,?,?)";
            psInsertRecord = conn.prepareStatement(sqlInsertRecord);

            FileWriter writer = new FileWriter("inconsistency_report.txt", true);

            for(int i=1;i < movies.size();i++)
            {
                String title = movies.get(i).getTitle();
                int year = movies.get(i).getYear();
                String director = "";
                if (movies.get(i).getDirector() != null)
                {
                    director = movies.get(i).getDirector();
                }

                if(MovieInfo.movieHash.containsKey(title))
                {
                    System.out.println("Movie already exists in the table: " + title);
                    writer.write("Movie already exists in the table: " + title + "\n");
                    continue;
                }

                maxMovieId += 1;
                String padded_id_numeric = String.format("%07d", maxMovieId);
                String newMovieId = "tt" + padded_id_numeric;
                movies.get(i).setDBID(newMovieId);

                psInsertRecord.setString(1, newMovieId);
                psInsertRecord.setString(2,title);
                psInsertRecord.setInt(3, year);
                psInsertRecord.setString(4, director);

                psInsertRecord.addBatch();
            }

            if(psInsertRecord != null)
            {
                iNoRows = psInsertRecord.executeBatch();
                System.out.println("what's this");
            }
            conn.commit();

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

        System.out.println("Parse & Insert Movie Elapsed time: " + minutes + " minutes " + seconds + " seconds");
    }

}


