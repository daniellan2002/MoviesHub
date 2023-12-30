/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
import java.util.ArrayList;

public class User {
    private final String username;
    private int userID;
    private ArrayList<Movie> movies;

    public User(String username, int userID) {
        this.username = username;
        this.movies = new ArrayList<Movie>();
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }
    public int getUserID() {
        return userID;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }
    public Movie getMovie(String movieID) {
        for (Movie movie : movies) {

            if (movie.getMovieId().equals(movieID)) {
                return movie;
            }
        }
        System.out.println("No movieID exist in this user's cart.");
        return null;
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (Movie movie : movies) {
            totalPrice += movie.getPrice() * movie.getQuantity();
        }
        return totalPrice;
    }

    public void addMovie(String movieId, int quantity) {

        for (Movie movie : movies)
        {

            if (movie.getMovieId().equals(movieId))
            {
                movie.addQuantity(quantity);
                System.out.println("User add movieID: " + movieId);
                System.out.println("The amount is: " + movie.getQuantity());
                return;
            }
        }
        Movie newMovie = new Movie(movieId, 1);
        movies.add(newMovie);

        System.out.println("User add new movieID: " + movieId);

    }

    public void deleteMovie(String movieID) {
        for (Movie movie : movies) {

            if (movie.getMovieId().equals(movieID)) {
                movies.remove(movie);
                return;
            }
        }
        System.out.println("Delete failed. No movieID exist in this user's cart.");
    }

    public void emptyCart(){
        movies.clear();
        movies = new ArrayList<Movie>();
    }
}

