package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String name;
    private final short year;

    private final String director;

    private final String genres;

    private final String stars;

    public Movie(String title, short year, String director, String genres, String stars) {
        this.name = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;

    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getGenres() {
        return genres;
    }

    public String getStars() {
        return stars;
    }
}