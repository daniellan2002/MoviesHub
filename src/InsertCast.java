import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InsertCast {
    public void runMe(Connection conn)
    {
        int[] iNoRows=null;

        String sqlInsertRecord;
        ArrayList<Actor> actorList = new ArrayList<>();
        ArrayList<MovieInfo> movieInfosList = new ArrayList<>();

        try {
            conn.setAutoCommit(false);
            FileWriter writer = new FileWriter("inconsistency_report.txt", true);

            String starIdQuery = "SELECT id, name FROM stars;";
            PreparedStatement preparedStatement = conn.prepareStatement(starIdQuery);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String starId = rs.getString("id");
                String name = rs.getString("name");
                Actor newActor = new Actor();
                newActor.setDBID(starId);
                newActor.setStagename(name);
                actorList.add(newActor);
                Actor.namesTable.put(starId, true);
            }

            String movieIdQuery = "SELECT id, title FROM movies;";
            preparedStatement = conn.prepareStatement(movieIdQuery);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String movieId = rs.getString("id");
                String title = rs.getString("title");
                MovieInfo newMovie = new MovieInfo();
                newMovie.setDBID(movieId);
                newMovie.setTitle(title);
                movieInfosList.add(newMovie);
                MovieInfo.movieHash.put(movieId, true);
            }

            //HASH original table
            String query = "SELECT * FROM stars_in_movies;";
            PreparedStatement psInsert = conn.prepareStatement(query);
            ResultSet rsInsert = psInsert.executeQuery();
            while (rsInsert.next()) {
                String starId = rsInsert.getString("starId");
                String moviesId = rsInsert.getString("movieId");
                String combined = starId + moviesId;
                Cast.hashTable.put(combined, true);
            }
            //parse Casts

            ParseCast parserCast = new ParseCast(actorList, movieInfosList);
            parserCast.runExample();
            List<Cast> cast = parserCast.cast;

            PreparedStatement psInsertRecord= null;
            sqlInsertRecord= "insert into stars_in_movies (starId, movieId) values(?,?)";

            psInsertRecord = conn.prepareStatement(sqlInsertRecord);
            for(int i=1;i < cast.size();i++)
            {
                String starID = cast.get(i).getActorDBID();
                String movieID = cast.get(i).getMovieDBID();
                //System.out.println(starID + ", " + movieID);

                if(Cast.hashTable.containsKey(starID+movieID))
                {
                    System.out.println("This listing is already exists in the table: " + starID + ", " + movieID);
                    writer.write("This listing is already exists in the table: " + starID + ", " + movieID + "\n");
                    System.out.println("not logged");
                    continue;
                }
                psInsertRecord.setString(1, starID);
                psInsertRecord.setString(2, movieID);
                psInsertRecord.addBatch();
                //System.out.println("-------");
                //System.out.println("add " + psInsertRecord);
            }

            if(psInsertRecord != null)
            {
                iNoRows = psInsertRecord.executeBatch();
                System.out.println("what's this cast");
            }

            conn.commit();
            if(psInsertRecord!=null) psInsertRecord.close();
            writer.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
