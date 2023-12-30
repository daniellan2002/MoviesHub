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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 14L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slave");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        double cartTotal = user.getTotalPrice();
        String sum =String.valueOf(cartTotal);
        response.getWriter().write(sum);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        String firstname = request.getParameter("first-name");
        String lastname = request.getParameter("last-name");
        String credit_card = request.getParameter("credit-card");
        String expiration_date = request.getParameter("expiration-date");


        if (firstname == "" || lastname == "" || credit_card == "" || expiration_date == "") {
         out.write("Please fill in all required fields.");
         return;
     }
        if(!isValidDate(expiration_date)){
            out.write("Wrong date format type");
            return;
        }

        try (Connection conn = dataSource.getConnection()) {

//            String query = "SELECT * FROM moviedb.creditcards WHERE id = \'"
//                    + credit_card + "\' and expiration = \'" + expiration_date +
//                    "\' and firstName = \'" + firstname + "\' and lastName = \'" + lastname + "\'";
//
//            Statement statement = conn.createStatement();
//            ResultSet rs = statement.executeQuery(query);

            String query = "SELECT * FROM moviedb.creditcards WHERE id = ? and expiration = ? and firstName = ? and lastName = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, credit_card);
            statement.setString(2, expiration_date);
            statement.setString(3, firstname);
            statement.setString(4, lastname);
            ResultSet rs = statement.executeQuery();

            if(rs.next())
            {
                out.write("True");
                rs.close();;
            }
            else
            {
                out.write("False");
            }
            out.close();
            statement.close();

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

    }
    public static boolean isValidDate(String dateString) {
        // Regular expression to match the date format "YYYY-MM-DD"
        String dateFormat = "^\\d{4}-\\d{2}-\\d{2}$";

        // Test if the string matches the date format
        return dateString.matches(dateFormat);
    }

}
