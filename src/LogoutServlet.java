import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "LogoutServlet", urlPatterns = "/api/logout")
public class LogoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the user's session
        HttpSession session = request.getSession(false);

        // Check if the session exists and has the "user" attribute set
        if (session != null && session.getAttribute("user") != null) {
            // Invalidate the session to remove all session attributes
            session.invalidate();

            // Send a response to indicate that the logout was successful
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Logout successful");
        } else {
            // Send a response to indicate that the user was not logged in
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("User not logged in");
        }
    }

}
