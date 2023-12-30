package edu.uci.ics.fabflixmobile.ui.singlemovie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListViewAdapter;
import edu.uci.ics.fabflixmobile.ui.search.SearchActivity;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {

    private EditText searchQuery;

    private TextView titleText;

    private TextView yearText;

    private TextView directorText;

    private TextView genresText;

    private TextView starsText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singlemovie);

        String title = getIntent().getStringExtra("title");
        short year = getIntent().getShortExtra("year", (short) 0);
        String director = getIntent().getStringExtra("director");
        String genres = getIntent().getStringExtra("genres");
        String stars = getIntent().getStringExtra("stars");

        titleText = findViewById(R.id.title);
        yearText = findViewById(R.id.year);
        directorText = findViewById(R.id.director);
        genresText = findViewById(R.id.genres);
        starsText = findViewById(R.id.stars);

        titleText.setText(title);
        yearText.setText(String.valueOf(year));
        directorText.setText(director);
        genresText.setText(genres);
        starsText.setText(stars);

        Button homeButton = findViewById(R.id.home);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event
                // Add your logic here
                finish();
            }
        });
    }
}
