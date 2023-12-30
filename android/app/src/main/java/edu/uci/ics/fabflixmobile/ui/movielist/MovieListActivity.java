package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;
import edu.uci.ics.fabflixmobile.ui.singlemovie.SingleMovieActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieListActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 10; // Number of movies per page
    private int currentPage = 1; // Current page number

    private final String host = "ec2-54-241-34-106.us-west-1.compute.amazonaws.com";
    private final String port = "8443";
    private final String domain = "s23-122b-kickin";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    private ProgressBar progressBar;

    private String title_query = "";

    private EditText searchQuery;

    private RequestQueue queue;
    private String lastRequestUrl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        // Initialize the progress bar
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        Button searchButton = findViewById(R.id.search_page);
        Button prevButton = findViewById(R.id.prev);
        Button nextButton = findViewById(R.id.next);

        searchQuery = findViewById(R.id.query);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event
                // Add your logic here
                String newQuery = searchQuery.getText().toString();
                if (!newQuery.equals(title_query)) {
                    title_query = newQuery;
                    fetchMovies(1);
                    Log.d("onClick.info--->", "searchButton: Calling fetchMovies");
                    Log.d("onClick.info--->", "searchButton: " + title_query);
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMovies(currentPage - 1);
                Log.d("nextButton.info--->", "nextButton: " + "Calling fetchMovies");
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchMovies(currentPage + 1);
                Log.d("nextButton.info--->", "nextButton: " + "Calling fetchMovies");
            }
        });

        queue = NetworkManager.sharedManager(this).queue;

        if (title_query.equals("")) {
            fetchMovies(currentPage);
            Log.d("onCreate.info--->", "search title: " + title_query);
        }
//        if (!moviesFetched){
//            fetchMovies(currentPage);
//            moviesFetched = true;
//        }


    }


    private void fetchMovies(int page) {

        queue.cancelAll(this);

        Button searchButton = findViewById(R.id.search_page);
        searchButton.setEnabled(false);

        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Hide the movie list view
        ListView listView = findViewById(R.id.list);
        listView.setVisibility(View.GONE);

        Log.d("fetchMovies.info--->", "search title: " + title_query);

        this.currentPage = page;


        Log.d("fetchMovies.info--->", "current page: " + currentPage);
        Log.d("fetchMovies.info--->", "current page: " + "Fetch Movies Is Ran");

        final String url = baseURL + "/api/androidmovielist?title=" + title_query + "&page=" + currentPage + "&moviePerPage=" + PAGE_SIZE;

        // Check if the request URL is the same as the last request
        if (url.equals(lastRequestUrl)) {
            // A request with the same URL is already pending, do not send another request
            return;
        }

        lastRequestUrl = url;

        // Request movies using GET method
        final StringRequest movieListRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        // Parse the JSON response and retrieve the movie data

                        Log.d("fetchMovies.info--->", "Movie Array: " + response);

                        JSONArray movieArray = new JSONArray(response);

                        Log.d("fetchMovies.info--->", "Movie Array: " + movieArray);

                        JSONObject lastObject = movieArray.getJSONObject(movieArray.length() - 1);

                        // Extract the values of total_count and current_page
                        int totalCount = lastObject.getInt("total_count");
                        int currentPage = lastObject.getInt("current_page");

                        ArrayList<Movie> movies = new ArrayList<>();

                        // Extract movie information from the JSON array
                        for (int i = 0; i < movieArray.length() - 1; i++) {
                            JSONObject movieObject = movieArray.getJSONObject(i);
                            String title = movieObject.getString("movie_title");
                            int year = movieObject.getInt("movie_year");
                            String director = movieObject.getString("movie_director");

                            String genres = movieObject.getString("movie_genres").replace(",", ", ");
                            String stars = movieObject.getString("stars_name").replace(",", ", ");

                            movies.add(new Movie(title, (short) year, director, genres, stars));
                        }

                        // Update the current page number
                        this.currentPage = currentPage;

                        // Update the ListView with the fetched movies
                        updateMovieList(movies);

                        // Enable/disable pagination buttons based on current page
                        updatePaginationButtons(currentPage, totalCount);

                        // Hide the progress bar
                        progressBar.setVisibility(View.GONE);

                        // Show the movie list view
                        listView.setVisibility(View.VISIBLE);

                        searchButton.setEnabled(true);


                    } catch (JSONException e) {
                        Log.d("fetchMovies.error", "Error parsing JSON response: " + e.getMessage());
                    }
                },
                error -> {
                    Log.d("fetchMovies.error", error.toString());
                    // Hide the progress bar
                    progressBar.setVisibility(View.GONE);

                    searchButton.setEnabled(true);

                }
        );



        // Set a custom timeout duration for the request
        int timeoutMilliseconds = 85000; // Example: Set timeout to 10 seconds
        movieListRequest.setRetryPolicy(new DefaultRetryPolicy(timeoutMilliseconds,
                0, 1.0f
        ));
        // Add the request to the queue
        queue.add(movieListRequest);


    }
    ;
        // important: queue.add is where the login request is actually sent

        // Mock implementation to populate dummy movies for testing
        // ArrayList<Movie> movies = createDummyMovies(page);

        // Update the ListView with the fetched movies
        // updateMovieList(movies);
    private void updatePaginationButtons(int currentPage, int totalPages) {
        Button prevButton = findViewById(R.id.prev);
        Button nextButton = findViewById(R.id.next);

        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }


    private void updateMovieList(ArrayList<Movie> movies) {
        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Movie movie = movies.get(position);
            String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
            SingleMoviePage.putExtra("title", movie.getName());
            SingleMoviePage.putExtra("year", movie.getYear());
            SingleMoviePage.putExtra("director", movie.getDirector());
            SingleMoviePage.putExtra("genres", movie.getGenres());
            SingleMoviePage.putExtra("stars", movie.getStars());
            startActivity(SingleMoviePage);
        });
    }
}