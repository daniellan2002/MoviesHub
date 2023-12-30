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


@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/addstar")
public class AddStarServlet extends HttpServlet {
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
        String star_name = request.getParameter("star");
        String year = request.getParameter("year");

        PrintWriter out = response.getWriter();


        System.out.println("star=" + star_name);
        System.out.println("year=" + year);


        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */

//        INSERT INTO stars (id, name, birthYear) VALUES('nm0547800','Ken Marino',1968);

        try (Connection conn = dataSource.getConnection()) {
            JsonObject responseJsonObject = new JsonObject();
            System.out.println("Point 1");

            String query = "SELECT max(id) FROM stars";

            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            // Checking metadata of result set
//            ResultSetMetaData metaData = rs.getMetaData();
//            int columnCount = metaData.getColumnCount();
//
//            System.out.println("Column names:");
//            for (int i = 1; i <= columnCount; i++) {
//                String columnName = metaData.getColumnName(i);
//                System.out.println(columnName);
//            }
            System.out.println("Point 2");

            if (rs.next())
            {
                System.out.println("Point 3");

                // Make new Id
                String max_id = rs.getString("max(id)");
                String prefix = max_id.substring(0, 2); // "nm"
                int suffix = Integer.parseInt(max_id.substring(2)); // 942309
                suffix++; // add 1 to the number
                String new_id = prefix + suffix; // "nm942310"

                // Prepare Query
                System.out.println("Point 4");
                // Check if year exists
                if (year != null && year != ""){
                    String insert_query = "INSERT INTO stars (id, name, birthYear) VALUES(?,?,?)";

                    System.out.println("Point 5");
                    PreparedStatement statement2 = conn.prepareStatement(insert_query);
                    statement2.setString(1, new_id);
                    statement2.setString(2, star_name);
                    statement2.setString(3, year);
                    int rowsAffected = statement2.executeUpdate();


                    if (rowsAffected != 0){
                        System.out.println("Point 6");
                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("star_id", new_id);
                    }
                    else{
                        System.out.println("Point 7");
                        responseJsonObject.addProperty("status", "fail");
                        request.getServletContext().log("Add star failed");
                        responseJsonObject.addProperty("message", "adding star failed");
                    }
                }
                else{
                    String insert_query = "INSERT INTO stars (id, name) VALUES(?,?)";

                    System.out.println("Point 5b");
                    PreparedStatement statement2 = conn.prepareStatement(insert_query);
                    statement2.setString(1, new_id);
                    statement2.setString(2, star_name);
                    int rowsAffected = statement2.executeUpdate();

                    if (rowsAffected != 0){
                        System.out.println("Point 6b");
                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("star_id", new_id);
                    }
                    else{
                        System.out.println("Point 7b");
                        responseJsonObject.addProperty("status", "fail");
                        request.getServletContext().log("Add star failed");
                        responseJsonObject.addProperty("message", "adding star failed");
                    }
                }


            } else {
                // Add star fail
                System.out.println("Point 4");
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Add star failed");
                responseJsonObject.addProperty("message", "getting max id failed");
            }
            System.out.println("jsonobject=" + responseJsonObject.toString());

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
