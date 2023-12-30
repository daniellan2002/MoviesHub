import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertMovie {
    public void runMe(Connection conn){
        int[] iNoRows=null;

        String sqlInsertRecord;
        String query = "SELECT DISTINCT title FROM movies;";
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
            ArrayList<MovieInfo> movies = parserMovie.movies;

            int maxMovieId = 0;
            PreparedStatement findMaxStatement = conn.prepareStatement("SELECT MAX(RIGHT(id, 7)) as max_id FROM stars");
            ResultSet maxRS = findMaxStatement.executeQuery();
            if (maxRS.next()) {
                maxMovieId =  Integer.parseInt(maxRS.getString("max_id"));
            }

            PreparedStatement psInsertRecord= null;
            sqlInsertRecord= "insert into movies (id, title, year, director) values(?,?,?,?)";
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
                System.out.println("what's this INSERTmOVIE");
            }
            conn.commit();

            if(psInsertRecord!=null) psInsertRecord.close();
            writer.close();

            PreparedStatement psInsertRating= null;
            sqlInsertRecord = "insert into ratings (movieId, rating, numVotes) values(?,?,?)";
            psInsertRating = conn.prepareStatement(sqlInsertRecord);

            for(int i=1;i < movies.size();i++) {
                String movieId = movies.get(i).getDBID();
                if (movieId != null) {

                    psInsertRating.setString(1, movieId);
                    psInsertRating.setInt(2,0);
                    psInsertRating.setInt(3, 0);
                    psInsertRating.addBatch();
                    System.out.println(psInsertRating);
                }
            }
            if(psInsertRating != null)
            {
                iNoRows = psInsertRating.executeBatch();
                System.out.println("what's this insert rating");
            }
            conn.commit();
            if(psInsertRating != null) psInsertRecord.close();

            InsertGenre insertGenre = new InsertGenre();
            insertGenre.runMe(conn, movies);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
