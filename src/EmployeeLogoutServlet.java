import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "EmpLogoutServlet", urlPatterns = "/_dashboard/api/logout")
public class EmployeeLogoutServlet extends HttpServlet {

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
