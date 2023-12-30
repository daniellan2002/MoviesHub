import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InsertGenre {
    public void runMe(Connection conn, ArrayList<MovieInfo> movies)
    {
        int[] iNoRows = null;

        String sqlInsertRecord;
        ArrayList<Actor> actorList = new ArrayList<>();
        ArrayList<MovieInfo> movieInfosList = new ArrayList<>();

        try {
            conn.setAutoCommit(false);
            FileWriter writer = new FileWriter("inconsistency_report.txt", true);

            String genreQuery = "SELECT * FROM genres;";
            PreparedStatement psGenre = conn.prepareStatement(genreQuery);
            ResultSet rs = psGenre.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Categories.allCategories.put(name, id);
            }

            int maxID = 0;
            String genreMaxQuery = "SELECT MAX(id) as maxID FROM genres;";
            PreparedStatement psGenreMax = conn.prepareStatement(genreMaxQuery);
            ResultSet rsGenreMax = psGenreMax.executeQuery();
            if (rsGenreMax.next()) {
               maxID = rsGenreMax.getInt("maxID");
            }


            String insertGenreQuery = "insert into genres (id, name) values(?,?)";
            PreparedStatement psInsertGenre = conn.prepareStatement(insertGenreQuery);
            String insertGenreMovieQuery = "insert into genres_in_movies (genreId, movieId) values(?,?)";
            PreparedStatement psInsertGenreMovie = null;


            for(int i=1;i < movies.size();i++)
            {
                //String movieID = movies.get(i).getDBID();
                ArrayList<Categories> catcat = movies.get(i).getCatcat();
                for(Categories cat: catcat)
                {
                    if(Categories.allCategories.containsKey(cat.getCategory())) {
                        int genreID = Categories.allCategories.get(cat.getCategory());
                        cat.setId(genreID);
                        //cat.setMovieID(movies.get(i).getDBID());
                    }
                    else {
                        maxID += 1;
                        cat.setId(maxID);
                        //cat.setMovieID(movies.get(i).getDBID());
                        Categories.allCategories.put(cat.getCategory(), maxID);
                        psInsertGenre.setInt(1, maxID);
                        psInsertGenre.setString(2, cat.getCategory());
                        psInsertGenre.addBatch();
                       // System.out.println(psInsertGenre);
                    }
                }
            }
            if(psInsertGenre != null)
            {
                iNoRows = psInsertGenre.executeBatch();
                System.out.println("what's this psInsertGenre");
                conn.commit();
            }

            psInsertGenreMovie = conn.prepareStatement(insertGenreMovieQuery);
            for(int i=1;i < movies.size();i++)
            {
                ArrayList<Categories> cats = movies.get(i).getCatcat();
                for(Categories cat: cats)
                {
                    if(movies.get(i).getDBID() != null)
                    {
                        psInsertGenreMovie.setInt(1, cat.getId());
                        psInsertGenreMovie.setString(2, movies.get(i).getDBID());
                        psInsertGenreMovie.addBatch();
                    }
                }
            }

            if(psInsertGenreMovie != null)
            {
                iNoRows = psInsertGenreMovie.executeBatch();
                System.out.println("what's this psInsertGenreMovie");
                conn.commit();
            }

            //conn.commit();
            if(psInsertGenre!=null) psInsertGenre.close();
            if(psInsertGenreMovie!=null) psInsertGenreMovie.close();

            writer.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
