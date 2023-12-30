import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/movielist"
@WebServlet(name = "StarsServlet", urlPatterns = "/api/movielist")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slave");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("POINT 1");

        long ts_startTime = System.nanoTime();
        response.setContentType("application/json");

        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();

        System.out.println("POINT 1b");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        String page_str = request.getParameter("page");
        String moviePerPage_str = "50";

        System.out.println("POINT 1c");
//        int page = Integer.parseInt(page_str);
//        int moviePerPage = Integer.parseInt(moviePerPage_str);
//        int offset = (page - 1) * moviePerPage;

        int page = 1;
        int moviePerPage = 50;
        int offset = (page - 1) * moviePerPage;

        System.out.println("POINT 1d");
        session.setAttribute("sortBy", sortBy);
        session.setAttribute("sortOrder", sortOrder);
        session.setAttribute("page", page_str);
        session.setAttribute("moviePerPage", moviePerPage_str);

        System.out.println("POINT 2");
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try {
            System.out.println("POINT 3");
            Connection conn = dataSource.getConnection();

            long tj_startTime = System.nanoTime();
            String title = request.getParameter("title");

            System.out.println("Title: " + title);
//            String year = request.getParameter("year");
//            String director = request.getParameter("director");
//
//            System.out.println("POINT 4");
//            String star = request.getParameter("star");
//            String genre = request.getParameter("genre");

            session.setAttribute("title", title);
//            session.setAttribute("year", year);
//            session.setAttribute("director", director);
//            session.setAttribute("star", star);
//            session.setAttribute("genre", genre);


            String sql = "SELECT movies.id as id, movies.title as title,\n" +
                    "movies.year as year, movies.director as director,\n" +
                    "GROUP_CONCAT(DISTINCT genres.name ORDER BY genres.name ASC SEPARATOR ',') AS genres, \n" +
                    "GROUP_CONCAT(DISTINCT stars.name ORDER BY star_counts.num_movies DESC, stars.name ASC SEPARATOR ',') AS stars_name,\n" +
                    "GROUP_CONCAT(DISTINCT stars.id ORDER BY star_counts.num_movies DESC, stars.name ASC SEPARATOR ',') AS stars_id,\n" +
                    "ratings.rating as rating\n" +
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
                    ") AS star_counts ON stars.id = star_counts.id";

            List<String> params = new ArrayList<>();

//            if (!((title == null || title == "") && (year == null || year == "") && (director == null || director == "") && (star == null || star == "") && (genre == null || genre == ""))){
//                sql += "\n WHERE ";
//            }

            if (title != null && title != "" && !title.equals("null")) {
                // sql += "\n WHERE title IN ";

                // SELECT title FROM movies WHERE MATCH (title) AGAINST ('+s* +lov*' IN BOOLEAN MODE)
                String[] words = title.split(" ");

                // Create a StringBuilder to store the modified words
                StringBuilder modifiedWords = new StringBuilder();

                // Iterate over the splitted words
                for (String word : words) {
                    // Add "+" in front and "*" behind each word
                    String modifiedWord = "+" + word + "*";
                    // Append the modified word to the StringBuilder
                    modifiedWords.append(modifiedWord).append(" ");
                }

                // Get the final modified string
                String modifiedTitle = modifiedWords.toString().trim();

                String fulltext_query = "\n WHERE MATCH (title) AGAINST (" + "'" + modifiedTitle + "'" + " IN BOOLEAN MODE)";

                sql += fulltext_query;

                // params.add("'" + title + "'");
            }

            sql += "\nGROUP BY movies.id\n";

            if(sortOrder!= null && sortBy != null)
            {
                if(sortOrder.equals("desc"))
                {
                    if(Objects.equals(sortBy, "rating"))
                    {
                        sql += "ORDER BY ratings.rating is Null, ratings.rating DESC, movies.title DESC\n";
                    }
                    if(Objects.equals(sortBy, "title"))
                    {
                        sql += "ORDER BY movies.title DESC, ratings.rating is Null, ratings.rating DESC\n";
                    }

                } else if (sortOrder.equals("asc")) {
                    if(Objects.equals(sortBy, "rating"))
                    {
                        sql += "ORDER BY ratings.rating IS NULL, ratings.rating ASC, movies.title ASC\n";
                    }
                    if(Objects.equals(sortBy, "title"))
                    {
                        sql += "ORDER BY movies.title ASC, ratings.rating IS NULL, ratings.rating ASC\n";
                    }
                }
            }
            else{
                sql += "ORDER BY movies.title ASC, ratings.rating IS NULL, ratings.rating ASC\n";
            }
            //params.add(moviePerPage_str);
            //params.add(String.valueOf(offset));

            sql += " LIMIT " + moviePerPage_str +" \n";
            sql += " OFFSET " + String.valueOf(offset) + " \n";

            System.out.println("POINT 5");

            PreparedStatement stmt = conn.prepareStatement(sql);

            // Setting LIKE statement
//            for (int i = 0; i < params.size(); i++) {
//                stmt.setString(i+1, params.get(i));
//            }

            System.out.println("POINT 6");
            request.getServletContext().log("query：" + sql);

            System.out.println(sql);

            ResultSet rs = stmt.executeQuery();


            JsonArray jsonArray = new JsonArray();
            System.out.println("POINT 7");
            // Iterate through each row of rs
            while (rs.next()) {
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
            System.out.println("POINT 8");
            rs.close();
            stmt.close();

            long tj_endTime = System.nanoTime();
            long tj_duration = tj_endTime - tj_startTime;

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());

            System.out.println(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

            long ts_endTime = System.nanoTime();
            long ts_duration = ts_endTime - ts_startTime;

            System.out.println("POINT 9");
            conn.close();

            // Output times to file
            String contextPath = getServletContext().getRealPath("/");
            String logFilePath = contextPath + "duration_results.txt";

            System.out.println(logFilePath);


            try (FileWriter writer = new FileWriter(logFilePath, true)) {
                System.out.println("TS: " + ts_duration);
                System.out.println("TJ: " + tj_duration);
                System.out.println("Current working directory: " + System.getProperty("user.dir"));

                // Write the durations to the file
                writer.write("ts_duration: " + ts_duration + " nanoseconds\n");
                writer.write("tj_duration: " + tj_duration + " nanoseconds\n");

                // Flush and close the writer
                writer.flush();
                writer.close();

                System.out.println("Duration results written to the file: " + logFilePath);
            } catch (IOException e) {
                // Handle any errors that occur during file writing
                System.out.println(e.getMessage());
                e.printStackTrace();
            }



        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            System.out.println(e.getMessage());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();

        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
