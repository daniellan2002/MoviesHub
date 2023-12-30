import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slave");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("POINT 1");

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        System.out.println("POINT 2");

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try {
            Connection conn = dataSource.getConnection();
            // Get a connection from dataSource
            System.out.println("POINT 3");

            // Construct a query with parameter represented by "?"
//            String query = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
//                    "where m.id = sim.movieId and sim.starId = s.id and m.id = ?";

            String query = "SELECT movies.id as id, movies.title as title,\n" +
                    "movies.year as year, movies.director as director,\n" +
                    "GROUP_CONCAT(DISTINCT genres.name ORDER BY genres.name ASC SEPARATOR ',') AS genres, \n" +
                    "GROUP_CONCAT(DISTINCT stars.name ORDER BY star_counts.num_movies DESC, stars.name ASC SEPARATOR ',') AS stars_name,\n" +
                    "GROUP_CONCAT(DISTINCT stars.id ORDER BY star_counts.num_movies DESC, stars.name ASC SEPARATOR ',') AS stars_id,\n" +
                    " ratings.rating as rating\n" +
                    "FROM movies\n" +
                    "JOIN genres_in_movies ON movies.id = genres_in_movies.movieId\n" +
                    "JOIN genres ON genres_in_movies.genreId = genres.id\n" +
                    "JOIN stars_in_movies ON movies.id = stars_in_movies.movieId\n" +
                    "JOIN stars ON stars_in_movies.starId = stars.id\n" +
                    "LEFT JOIN ratings ON movies.id = ratings.movieId\n" +
                    "JOIN (\n" +
                    "  SELECT stars.id, COUNT(*) AS num_movies\n" +
                    "  FROM stars_in_movies\n" +
                    "  JOIN stars ON stars_in_movies.starId = stars.id\n" +
                    "  GROUP BY stars.id\n" +
                    ") AS star_counts ON stars.id = star_counts.id\n" +
                    "WHERE movies.id = ?";

            System.out.println(query);
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            System.out.println("POINT 4");

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();
            System.out.println("POINT 5");

            // Iterate through each row of rs
            while (rs.next())  {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String movie_genres = rs.getString("genres");
                String stars_name = rs.getString("stars_name");
                String stars_id = rs.getString("stars_id");
                String movie_rating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_genres",movie_genres);
                jsonObject.addProperty("stars_name", stars_name);
                jsonObject.addProperty("stars_id", stars_id);
                jsonObject.addProperty("movie_rating", movie_rating);

                jsonArray.add(jsonObject);
            }

            System.out.println("POINT 6");

            rs.close();
            statement.close();
            out.write(jsonArray.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);
            conn.close();
            System.out.println("POINT 7");

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        JsonObject url = getURL(session);
        PrintWriter out = response.getWriter();
        JsonArray returnArr = new JsonArray();
        returnArr.add(url);
        out.write(returnArr.toString());
        out.close();
    }

    protected JsonObject getURL(HttpSession session){

        JsonObject url = new JsonObject();
        String sortBy = "";
        String sortOrder = "";
        String page = "";
        String title = "";
        String year = "";
        String director = "";
        String star = "";
        String genre = "";
        String moviePerPage = "";

        if(session.getAttribute("sortBy") != null)
        {
             sortBy = (String) session.getAttribute("sortBy");
        }
        if(session.getAttribute("sortOrder") != null)
        {
             sortOrder = (String) session.getAttribute("sortOrder");
        }
        if (session.getAttribute("title") != null)
        {
             title = (String) session.getAttribute("title");
        }
        if (session.getAttribute("page") != null)
        {
            page = String.valueOf(session.getAttribute("page"));
        }
        if (session.getAttribute("year") != null)
        {
            year = (String) session.getAttribute("year");
        }
        if (session.getAttribute("director") != null)
        {
            director = (String) session.getAttribute("director");
        }
        if (session.getAttribute("star") != null)
        {
            star = (String) session.getAttribute("star");
        }
        if (session.getAttribute("genre") != null)
        {
            genre = (String) session.getAttribute("genre");
        }
        if (session.getAttribute("moviePerPage") != null)
        {
            moviePerPage = (String) session.getAttribute("moviePerPage");
        }

        url.addProperty("sortBy", sortBy);
        url.addProperty("sortOrder", sortOrder);
        url.addProperty("page", page);
        url.addProperty("title", title);
        url.addProperty("year", year);
        url.addProperty("director", director);
        url.addProperty("star", star);
        url.addProperty("genre", genre);
        url.addProperty("moviePerPage", moviePerPage);

        return url;
    }

}


