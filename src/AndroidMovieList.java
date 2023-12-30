import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "AndroidServlet", urlPatterns = "/api/androidmovielist")
public class AndroidMovieList extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        System.out.println("POINT 1");

        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        String page_str = request.getParameter("page");
        String moviePerPage_str = request.getParameter("moviePerPage");

        System.out.println("POINT 2");

        int page = Integer.parseInt(page_str);
        int moviePerPage = Integer.parseInt(moviePerPage_str);
        int offset = Math.max((page - 1) * moviePerPage, 0);

        session.setAttribute("sortBy", sortBy);
        session.setAttribute("sortOrder", sortOrder);
        session.setAttribute("page", page_str);
        session.setAttribute("moviePerPage", moviePerPage_str);

        System.out.println("POINT 3");

        try (Connection conn = dataSource.getConnection()) {
            String title = request.getParameter("title");
            session.setAttribute("title", title);
            System.out.println("title " + title);

            System.out.println("POINT 4");

            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT SQL_CALC_FOUND_ROWS movies.id as id, movies.title as title, movies.year as year, movies.director as director, ")
                    .append("GROUP_CONCAT(DISTINCT genres.name ORDER BY genres.name ASC SEPARATOR ',') AS genres, ")
                    .append("GROUP_CONCAT(DISTINCT stars.name ORDER BY star_counts.num_movies DESC, stars.name ASC SEPARATOR ',') AS stars_name, ")
                    .append("GROUP_CONCAT(DISTINCT stars.id ORDER BY star_counts.num_movies DESC, stars.name ASC SEPARATOR ',') AS stars_id, ")
                    .append("ratings.rating as rating ")
                    .append("FROM movies ")
                    .append("JOIN genres_in_movies ON movies.id = genres_in_movies.movieId ")
                    .append("JOIN genres ON genres_in_movies.genreId = genres.id ")
                    .append("JOIN stars_in_movies ON movies.id = stars_in_movies.movieId ")
                    .append("JOIN stars ON stars_in_movies.starId = stars.id ")
                    .append("LEFT JOIN ratings ON movies.id = ratings.movieId ")
                    .append("JOIN ( ")
                    .append("  SELECT stars.id, COUNT(*) AS num_movies ")
                    .append("  FROM stars_in_movies ")
                    .append("  JOIN stars ON stars_in_movies.starId = stars.id ")
                    .append("  GROUP BY stars.id ")
                    .append(") AS star_counts ON stars.id = star_counts.id ");

            List<Object> params = new ArrayList<>();

            if (title != null && !title.isEmpty() && !title.equals("null")) {
                String[] words = title.split(" ");
                StringBuilder modifiedWords = new StringBuilder();

                for (String word : words) {
                    String modifiedWord = "+" + word + "*";
                    modifiedWords.append(modifiedWord).append(" ");
                }

                String modifiedTitle = modifiedWords.toString().trim();
                sqlBuilder.append("WHERE MATCH (title) AGAINST (? IN BOOLEAN MODE) ");
                params.add(modifiedTitle);
            }

            System.out.println("POINT 5");
            sqlBuilder.append("GROUP BY movies.id ");

            if (sortOrder != null && sortBy != null) {
                String orderField = null;
                if (Objects.equals(sortBy, "rating")) {
                    orderField = "ratings.rating";
                } else if (Objects.equals(sortBy, "title")) {
                    orderField = "movies.title";
                }

                if (orderField != null) {
                    sqlBuilder.append("ORDER BY ");
                    if (Objects.equals(sortOrder, "desc")) {
                        sqlBuilder.append(orderField).append(" IS NULL, ")
                                .append(orderField).append(" DESC, ")
                                .append("movies.title DESC ");
                    } else if (Objects.equals(sortOrder, "asc")) {
                        sqlBuilder.append(orderField).append(" IS NULL, ")
                                .append(orderField).append(" ASC, ")
                                .append("movies.title ASC ");
                    }
                }
            } else {
                sqlBuilder.append("ORDER BY movies.title ASC, ratings.rating IS NULL, ratings.rating ASC ");
            }

            System.out.println("POINT 6");

            String query = sqlBuilder.toString();
            query += "LIMIT ? OFFSET ?";

            params.add(Integer.parseInt(moviePerPage_str));
            params.add(offset);

            System.out.println("query " + query);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                int index = 1;
                for (Object param : params) {
                    stmt.setObject(index++, param);
                }

                System.out.println("POINT 7");

                ResultSet rs = stmt.executeQuery();

                JsonArray jsonArray = new JsonArray();

                while (rs.next()) {
                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    String movie_year = rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_genres = rs.getString("genres");
                    String stars_name = rs.getString("stars_name");
                    String stars_id = rs.getString("stars_id");
                    String movie_rating = rs.getString("rating");

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    jsonObject.addProperty("movie_genres", movie_genres);
                    jsonObject.addProperty("stars_name", stars_name);
                    jsonObject.addProperty("stars_id", stars_id);
                    jsonObject.addProperty("movie_rating", movie_rating);

                    jsonArray.add(jsonObject);
                }

                System.out.println("POINT 8");

                int totalCount = 0;
                try (PreparedStatement countStmt = conn.prepareStatement("SELECT FOUND_ROWS()")) {
                    ResultSet countRs = countStmt.executeQuery();
                    if (countRs.next()) {
                        totalCount = countRs.getInt(1);
                    }
                    countRs.close();
                }

                System.out.println("total count " + totalCount);
                System.out.println("page " + page);



                JsonObject metadata = new JsonObject();
                metadata.addProperty("total_count", totalCount);
                metadata.addProperty("current_page", page);
                jsonArray.add(metadata);

                rs.close();

                out.write(jsonArray.toString());

                System.out.println("POINT 9");
                response.setStatus(200);

            } catch (SQLException e) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("errorMessage", e.getMessage());
                out.write(jsonObject.toString());
                response.setStatus(500);
                System.out.println("POINT 7" + e.getMessage());
            }
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
            System.out.println("POINT 8" + e.getMessage());
        } finally {
            out.close();
        }
    }
}
