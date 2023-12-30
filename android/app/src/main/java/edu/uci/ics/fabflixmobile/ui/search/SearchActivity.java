package edu.uci.ics.fabflixmobile.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;

public class SearchActivity extends AppCompatActivity {

    private EditText searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchpage);

        searchQuery = findViewById(R.id.query);
        Button searchButton = findViewById(R.id.search);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event
                // Add your logic here
                Intent movieListIntent = new Intent(SearchActivity.this, MovieListActivity.class);
                movieListIntent.putExtra("query", searchQuery.getText().toString());
                startActivity(movieListIntent);
            }
        });
    }
}
