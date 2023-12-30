public class Movie {
    private String movie_id;
    private int quantity;
    private double price;

    private String title;

    public Movie(String movie_id, int quantity) {
        this.movie_id = movie_id;
        this.quantity = quantity;
        this.price = 10;
    }

    public String getMovieId() {
        return movie_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() { return price;}
    public String getTitle() {
        return title;
    }

    public void addQuantity(int amount) {
        quantity += amount;
    }

    public void setQuantity(int newQuantity) {
        quantity = newQuantity;
    }
    public void setTitle(String movie_title) {
        title = movie_title;
    }


}
