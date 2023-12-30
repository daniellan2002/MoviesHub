import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/addmovie")
public class AddMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");
        String director = request.getParameter("director");
        String year = request.getParameter("year");
        String star_name = request.getParameter("star");
        String genre = request.getParameter("genre");


        PrintWriter out = response.getWriter();


        System.out.println("title=" + title);
        System.out.println("director=" + director);
        System.out.println("year=" + year);
        System.out.println("star=" + star_name);
        System.out.println("genre=" + genre);


        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */

//        INSERT INTO stars (id, name, birthYear) VALUES('nm0547800','Ken Marino',1968);

        try (Connection conn = dataSource.getConnection()) {
            JsonObject responseJsonObject = new JsonObject();
            System.out.println("Point 1");

            //Check if movie exists


            // Prepare the SQL query
            System.out.println("Checking if movie exists");
            String sql = "SELECT COUNT(*) as count FROM movies WHERE title = ? AND year = ? AND director = ?";
            PreparedStatement statement = conn.prepareStatement(sql);

            // Set the parameters
            statement.setString(1, title);
            statement.setString(2, year);
            statement.setString(3, director);

            System.out.println("executing find movie");
            // Execute the query and retrieve the result set
            ResultSet rs = statement.executeQuery();
            rs.next();

            // Check if a duplicate record exists
            int count = rs.getInt("count");
            if (count > 0) {
                System.out.println("Duplicate movie exists");

                // A matching record already exists in the database
                // Do something to handle the duplicate
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Add Movie failed");
                responseJsonObject.addProperty("message", "Duplicate Movie Already Exists");

            }
            else{
                // Insert movie
                System.out.println("Adding new movie");

                String insert_movie = "{CALL insert_movie(?, ?, ?)}";
                PreparedStatement statementAdd = conn.prepareCall(insert_movie);

                statementAdd.setString(1, title);
                statementAdd.setString(2, year);
                statementAdd.setString(3, director);
                statementAdd.execute();

                System.out.println("running stored procedure");

                int rowsAffected = statementAdd.getUpdateCount();

                if (rowsAffected != 0){
                    System.out.println("New Movie Added!");
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "New Movie Added!");
                }
                else{
                    System.out.println("Movie Add Failed!");
                    responseJsonObject.addProperty("status", "fail");
                    request.getServletContext().log("Add star failed");
                    responseJsonObject.addProperty("message", "adding movie failed");
                }
                // Check if star exists
                System.out.println("Checking if star exists");

                String new_id = "";
                String check_query = "SELECT COUNT(*),id FROM stars WHERE name = ? GROUP BY id";
                PreparedStatement statementCheck1 = conn.prepareStatement(check_query);
                statementCheck1.setString(1, star_name);
                ResultSet result = statementCheck1.executeQuery();

                if (result.next()){
                    new_id = result.getString("id");
                    System.out.println("point a");
                    System.out.println("New Id " + new_id);

                }
                else {
                    // Insert new star record
                    System.out.println("Adding new star records");

                    // find max id
                    String findMax = "SELECT max(id) FROM stars";

                    PreparedStatement statementFM = conn.prepareStatement(findMax);
                    ResultSet getMax = statementFM.executeQuery();

                    getMax.next();
                    System.out.println("point b");


                    String max_id = getMax.getString("max(id)");
                    System.out.println("Current Max Id: " + max_id);
//                    char lastChar = max_id.charAt(max_id.length() - 1); // get the last character
//                    int lastCharAscii = (int) lastChar; // convert it to an ASCII number
//                    lastCharAscii += 1; // add 1 to the ASCII number
//                    new_id = max_id.substring(0, max_id.length() - 1) + (char) lastCharAscii; // replace the last character with the new ASCII number

                    String prefix = max_id.substring(0, 2); // "nm"
                    int suffix = Integer.parseInt(max_id.substring(2)); // 942309
                    suffix++; // add 1 to the number
                    new_id = prefix + suffix; // "nm942310"

                    System.out.println("point c");
                    System.out.println("New ID    " + new_id);


                    // insert
                    String insert_query = "INSERT INTO stars (id, name) VALUES(?,?)";


                    PreparedStatement statementInsertStar = conn.prepareStatement(insert_query);
                    statementInsertStar.setString(1, new_id);
                    statementInsertStar.setString(2, star_name);
                    int rowsAffected2 = statementInsertStar.executeUpdate();
                    System.out.println("point d");
                    // check if successful
                    if (rowsAffected2 != 0){
                        System.out.println("successfully added new star");
//                        responseJsonObject.addProperty("status", "success");
//                        responseJsonObject.addProperty("star_id", new_id);
                    }
                    else{
                        System.out.println("failed to add new star");
//                        responseJsonObject.addProperty("status", "fail");
//                        request.getServletContext().log("Add star failed");
//                        responseJsonObject.addProperty("message", "adding star failed");
                    }
                }
                // Check if genre exists
                System.out.println("Checking if genre exists");

                String newGenreId = "";
                String check_genre = "SELECT COUNT(*),id FROM genres WHERE name = ? GROUP BY id";
                PreparedStatement statementCheck2 = conn.prepareStatement(check_genre);
                statementCheck2.setString(1, genre);
                ResultSet result2 = statementCheck2.executeQuery();
                System.out.println("Point a");

                int countGenre = 0;
                if (result2.next()) {
                    System.out.println("Point b");
                    countGenre = result2.getInt(1);
                    // rest of your code
                }
                else {
                    // handle the case where the query failed
                    System.out.println("Point c");
                    System.out.println("Check genre failed");
                }
                if (countGenre > 0) {
                    newGenreId = result2.getString("id");
                    System.out.println("Duplicate genre found");
                }
                else{
                    // Genre does not exist, generate a new id
                    System.out.println("Point d");
                    String maxIdQuery = "SELECT MAX(id) as max_id FROM genres";
                    PreparedStatement maxIdStatement = conn.prepareStatement(maxIdQuery);
                    ResultSet maxIdResult = maxIdStatement.executeQuery();
                    maxIdResult.next();
                    int genreId = maxIdResult.getInt("max_id") + 1;

                    newGenreId = String.valueOf(genreId);

                    // Insert the new genre into the database
                    System.out.println("inserting new genre");

                    String insertQuery = "INSERT INTO genres (id, name) VALUES (?, ?)";
                    PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                    insertStatement.setString(1, newGenreId);
                    insertStatement.setString(2, genre);

                    int rowsAffected3 = insertStatement.executeUpdate();

                    // check if successful
                    if (rowsAffected3 != 0){
                        System.out.println("successfully added new genre");
//                        responseJsonObject.addProperty("status", "success");
//                        responseJsonObject.addProperty("genre_id", newGenreId);
                    }
                    else{
                        System.out.println("failed to add new genre");
//                        responseJsonObject.addProperty("status", "fail");
//                        request.getServletContext().log("Add star failed");
//                        responseJsonObject.addProperty("message", "adding star failed");
                    }
                }

                // Link star and genre to movie
                System.out.println("Linking star and genre to movie");


                System.out.println("getting movie id");

                String getMovieId = "SELECT id FROM movies WHERE title = ? AND year = ? AND director = ?";
                PreparedStatement statementGetId = conn.prepareStatement(getMovieId);
                statementGetId.setString(1, title);
                statementGetId.setString(2, year);
                statementGetId.setString(3, director);
                System.out.println("running get movie id query");

                ResultSet resultGetId = statementGetId.executeQuery();
                if (resultGetId.next()) {
                    String movieId = resultGetId.getString("id");
                    // Do something with the movieId
                    String insertGenreMovie = "INSERT INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";

                    PreparedStatement pstmt = conn.prepareStatement(insertGenreMovie);
                    pstmt.setString(1, newGenreId);
                    pstmt.setString(2, movieId);
                    int rowCount = pstmt.executeUpdate();

                    if (rowCount > 0) {
                        System.out.println("Inserted " + rowsAffected + " row(s) into genres_in_movies table");
                    } else {
                        System.out.println("Failed to insert row into genres_in_movies table");
                    }

                    String insertStarMovie = "INSERT INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
                    PreparedStatement statementStarMovie = conn.prepareStatement(insertStarMovie);
                    statementStarMovie.setString(1, new_id);
                    statementStarMovie.setString(2, movieId);

                    // execute the statement
                    int rowsChanged = statementStarMovie.executeUpdate();

                    if (rowsChanged > 0) {
                        System.out.println("Successfully inserted star-movie relation.");

                        // Add MovieId, StarId, GenreId
                        responseJsonObject.addProperty("movieid", movieId);
                        responseJsonObject.addProperty("starid", new_id);
                        responseJsonObject.addProperty("genreid", newGenreId);
                    } else {
                        System.out.println("Failed to insert star-movie relation.");
                    }


                } else {
                    // No movie found with the given criteria
                    System.out.println("Can't find movie id");
                }
            }
            out.write(responseJsonObject.toString());



        } catch (Exception e) {
            // Write error message JSON object to output
            System.out.println("Point 8");
            JsonObject jsonObject = new JsonObject();
            System.out.println(e.getMessage());
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);

        } finally {
            out.close();
        }

    }
}
