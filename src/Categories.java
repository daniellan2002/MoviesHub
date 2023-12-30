import java.util.Hashtable;

public class Categories {
    private String category;
    private int id;
    //private String movieID;
    static Hashtable<String, Integer> allCategories = new Hashtable<>();
    public Categories(String category, int id) {
        this.category = category;
        this.id = id;
    }

    public Categories(){}
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    //public String getMovieID(){return movieID;};
    //public void setMovieID(String id){this.movieID = id;};

}

