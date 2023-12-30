import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/index")
public class IndexServlet extends HttpServlet {
    private static final long serialVersionUID = 15L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<String>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();
        previousItems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);
        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String item = request.getParameter("item");
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("user");
        //user.getMovies().clear();


        // get the previous items in a ArrayList
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<String>();
            if(item != null)
            {
                previousItems.add(item);
                user.addMovie(item, 1);
            }
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
                if(item != null) {
                    previousItems.add(item);
                    user.addMovie(item, 1);
                }

            }
        }
        response.getWriter().write(getTable(previousItems, user).toString());

    }
    protected JsonArray getTable(ArrayList<String> previousItems, User user) throws IOException {

        try (Connection conn = dataSource.getConnection()) {
//            String query = "SELECT * FROM movies WHERE";
//            int k = 0;
//            for(Movie movie: user.getMovies()){
//                k++;
//                query += " id = \"" + movie.getMovieId() + "\"";
//                if(k == user.getMovies().size())
//                {
//                    query += ";";
//                    break;
//                }
//                query += " or";
//            }
//            System.out.println(query);
//
//            Statement statement = conn.createStatement();
//            ResultSet rs = statement.executeQuery(query);

            String query = "SELECT * FROM movies WHERE id IN (";
            for (int i = 0; i < user.getMovies().size(); i++) {
                query += "?, ";
            }
            query = query.substring(0, query.length() - 2) + ")";
            PreparedStatement statement = conn.prepareStatement(query);
            for (int i = 0; i < user.getMovies().size(); i++) {
                statement.setString(i + 1, user.getMovies().get(i).getMovieId());
            }
            ResultSet rs = statement.executeQuery();


            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next())  {
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                int quantity = user.getMovie(movie_id).getQuantity();
                double price = user.getMovie(movie_id).getPrice();
                user.getMovie(movie_id).setTitle(movie_title);

                System.out.println("id + " + movie_id);
                System.out.println("title + " + movie_title);
                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("quantity", quantity);
                jsonObject.addProperty("unit_price", price);
                jsonObject.addProperty("price", quantity * price);
                jsonArray.add(jsonObject);
            }
            //JsonObject jsonObject = new JsonObject();
            System.out.println("Total price from User: $" + user.getTotalPrice());
            //jsonObject.addProperty("total_price", user.getTotalPrice());
            //jsonArray.add(jsonObject);

            rs.close();
            statement.close();
            conn.close();
            return jsonArray;

            // Write JSON string to output
//            out.write(jsonArray.toString());
//            // Set response status to 200 (OK)
//            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
//            out.write(jsonObject.toString());
//
//            // Log error to localhost log
//            request.getServletContext().log("Error:", e);
//            // Set response status to 500 (Internal Server Error)
//            response.setStatus(500);
        } finally {
//            out.close();
        }
        return null;

    }
}

