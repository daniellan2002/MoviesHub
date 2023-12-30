import com.google.gson.JsonObject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movie_id = request.getParameter("item_id");
        String newQuantity = request.getParameter("quantity");


        HttpSession session = request.getSession();
        System.out.println("User who? " + session.getAttribute("user"));
        User user = (User) session.getAttribute("user");
        System.out.println("User before change: " + movie_id + " quantity#:" + user.getMovie(movie_id).getQuantity());
        user.getMovie(movie_id).setQuantity(Integer.valueOf(newQuantity));
        System.out.println("User after change: " + movie_id + " quantity#:"+ user.getMovie(movie_id).getQuantity());
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String item_id = request.getParameter("id");
        System.out.println("Entering delete " + item_id);

        HttpSession session = request.getSession();
        System.out.println("Delete User who? " + session.getAttribute("user"));
        User user = (User) session.getAttribute("user");
        user.deleteMovie(item_id);
    }
}