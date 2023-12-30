import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 5L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Building page head with title
        out.println("<html><head><title>MovieDBExample: Found Records</title></head>");

        // Building page body
        out.println("<body><h1>MovieDBExample: Found Records</h1>");



//        response.setContentType("application/json"); // Response mime type

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try  {

            Connection conn = dataSource.getConnection();
            String title = request.getParameter("title");


            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");

            String sql = "SELECT * FROM movies WHERE ";

            List<String> params = new ArrayList<>();

            if (title != null) {
                sql += "title LIKE ? ";
                params.add("%" + title + "%");
            }
            if (year != null) {
                if (params.size() > 0) {
                    sql += "AND ";
                }
                sql += "year = ? ";
                params.add(year);
            }
            if (director != null) {
                if (params.size() > 0) {
                    sql += "AND ";
                }
                sql += "director LIKE ? ";
                params.add("%" + director + "%");
            }
            if (star != null) {
                if (params.size() > 0) {
                    sql += "AND ";
                }
                sql += "id IN (SELECT movie_id FROM stars_in_movies WHERE star_id IN (SELECT id FROM stars WHERE name LIKE ?)) ";
                params.add("%" + star + "%");
            }

            PreparedStatement stmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.size(); i++) {
                stmt.setString(i+1, params.get(i));
            }

            request.getServletContext().log("query：" + sql);

            ResultSet rs = stmt.executeQuery();

            // Create a html <table>
            out.println("<table border>");

            // Iterate through each row of rs and create a table row <tr>
            out.println("<tr><td>Title</td><td>Director</td></tr>");

            JsonArray jsonArray = new JsonArray();

            //String result = "<table><tr><th>Title</th><th>Year</th><th>Director</th><th>Stars</th></tr>";
//            while (rs.next()) {
//
//                String movieTitle = rs.getString("title");
//                String movieYear = rs.getString("year");
//                String movieDirector = rs.getString("director");
//                String movieStars = getStarsForMovie(rs.getInt("id"));
//                result += "<tr><td>" + movieTitle + "</td><td>" + movieYear + "</td><td>" + movieDirector + "</td><td>" + movieStars + "</td></tr>";
//
//                JsonObject jsonObject = new JsonObject();
//                //jsonObject.addProperty("movie_id", movie_id);
//                jsonObject.addProperty("movie_title", movieTitle);
//                jsonObject.addProperty("movie_year", movieYear);
//                jsonObject.addProperty("movie_director", movieDirector);
//                //jsonObject.addProperty("movie_genres",movie_genres);
//                //jsonObject.addProperty("stars_name", stars_name);
//                jsonObject.addProperty("stars_id", movieStars);
//                //jsonObject.addProperty("movie_rating", movie_rating);
//                jsonArray.add(jsonObject);
//            }

            while (rs.next()) {
                String movieTitle = rs.getString("title");

                int movieYear = rs.getInt("year");

                String movieDirector = rs.getString("director");

//                String movieStars = getStarsForMovie(rs.getInt("id"));
//                result += "<tr><td>" + movieTitle + "</td><td>" + movieYear + "</td><td>" + movieDirector + "</td><td>" + movieStars + "</td></tr>";


                out.println(String.format("<tr><td>%s</td><td>%s</td></tr>", movieTitle, movieDirector));

            }
            out.println("</table></body></html>");

            rs.close();
            stmt.close();
            conn.close();

//            result += "</table>";

//            out.println(result);
        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }
    }

    private String getStarsForMovie(int movieId) {
        String query = "SELECT s.name FROM stars s JOIN stars_in_movies sim ON s.id = sim.starId WHERE sim.movieId = ?";
        StringBuilder stars = new StringBuilder();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, movieId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String starName = resultSet.getString("name");
                if (stars.length() > 0) {
                    stars.append(", ");
                }
                stars.append(starName);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return stars.toString();
    }


}
