import java.util.Hashtable;

public class Cast {
    private String actorDBID;
    private String actorName;
    private String movieTitle;
    private String movieDBID;

    static Hashtable<String, Boolean> hashTable = new Hashtable<String, Boolean>();

    public Cast(String actorDBID, String movieDBID) {
        this.actorDBID = actorDBID;
        this.movieDBID = movieDBID;
    }

    public String getActorDBID() {
        return actorDBID;
    }

    public void setActor(String actorDBID) {
        this.actorDBID = actorDBID;
    }

    public String getMovieDBID() {
        return movieDBID;
    }

    public void setMovie(String movie) {
        this.movieDBID = movie;
    }

    @Override
    public String toString() {
        return actorDBID + " in " + movieDBID;
    }
}
