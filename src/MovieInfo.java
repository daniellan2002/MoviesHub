import java.util.ArrayList;
import java.util.Hashtable;

public class MovieInfo {
    private String xmlID;
    private String dbID;
    private String title;
    private int year;
    private String director;
    //private ArrayList<String> categories;
    private ArrayList<Categories> catcat;

    //private ArrayList<Actor> actors_inMovie = new ArrayList<>();
    static Hashtable<String, Boolean> movieHash = new Hashtable<String, Boolean>();

    public MovieInfo(){};
    public MovieInfo(String title, int year, String director) {
        this.title = title;
        this.year = year;
        this.director = director;

    }
    public MovieInfo(String fid, String title, int year, String director, ArrayList<Categories> categories) {
        this.xmlID = fid;
        this.title = title;
        this.year = year;
        this.director = director;
        this.catcat = categories;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(title).append("\n");
        sb.append("Year: ").append(year).append("\n");
        sb.append("Director: ").append(director).append("\n");
        sb.append("Categories: ");
        for (Categories category : catcat) {
            sb.append(category.getCategory()).append(", ");
        }
        sb.setLength(sb.length() - 2); // Remove the last ", "
        sb.append("\n");
        return sb.toString();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setXmlID(String id) {
        this.xmlID = id;
    }
    public String getfid() {
        return xmlID;
    }
    public String getDBID() {
        return dbID;
    }
    public void setDBID(String id) {
        this.dbID = id;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

//    public ArrayList<String> getCategories() {
//        return categories;
//    }
//
//    public void setCategories(ArrayList<String> categories) {
//        this.categories = categories;
//    }

    public ArrayList<Categories> getCatcat() {
        return catcat;
    }

    public void setCatcat(ArrayList<Categories> categories) {
        this.catcat = categories;
    }
}

