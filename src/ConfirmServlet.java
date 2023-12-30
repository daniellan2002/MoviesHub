import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.sql.PreparedStatement;
import java.time.LocalDate;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "ConfirmServlet", urlPatterns = "/api/confirm")
public class ConfirmServlet extends HttpServlet {
    private static final long serialVersionUID = 20L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        int userId = user.getUserID();

        System.out.println("Who's the user" + user);
        JsonArray userCart = new JsonArray();
        StringBuilder saleID = new StringBuilder();

        for(Movie movie: user.getMovies())
        {
            int quantity = movie.getQuantity();
            double price = movie.getPrice();
            String title = movie.getTitle();
            String id = movie.getMovieId();
            saleID.append(id);
            System.out.println(id + " " + title + " x" + quantity + " &" + price);
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("movie_id", id);
            jsonObject.addProperty("movie_title", title);
            jsonObject.addProperty("quantity", quantity);
            jsonObject.addProperty("price", quantity * price);
            userCart.add(jsonObject);
        }
        JsonObject saleIdObject = new JsonObject();
        saleIdObject.addProperty("sale_id", saleID.toString());
        saleIdObject.addProperty("total_price", user.getTotalPrice());
        userCart.add(saleIdObject);
        response.getWriter().write(userCart.toString());

        //insertSaleid
        try (Connection conn = dataSource.getConnection()) {
            String saleID_str = saleID.toString();
            LocalDate currentDate = LocalDate.now();
//            String query = "INSERT INTO moviedb.sales (customerId, movieId, saleDate) VALUES ("+
//                    userId + ",'" + saleID.toString() + "', '" + currentDate + "')";
//
//            System.out.println(query);
//            PreparedStatement statement = conn.prepareStatement(query);
//            //Statement statement = conn.createStatement();
//            System.out.println("point 1");
//            statement.executeUpdate(query);
//            System.out.println("point 2");
//            statement.close();

            String query = "INSERT INTO moviedb.sales (customerId, movieId, saleDate) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, saleID_str);
            statement.setDate(3, java.sql.Date.valueOf(currentDate));
            statement.executeUpdate();
            statement.close();
            user.emptyCart();


        } catch (Exception e) {
            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            System.out.println("Error: " + e.getMessage());
            response.setStatus(500);
        }

    }
}
