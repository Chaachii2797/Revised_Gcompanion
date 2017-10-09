package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 10/1/2017.
 */

public class ForCounter_Rating {
    int sold;
    String ownerID;
    int rating;
    int transac_rating;

    public ForCounter_Rating(){

    }
    public ForCounter_Rating(int sold, String ownerID,int rating, int transac_rating) {
        this.sold = sold;
        this.ownerID = ownerID;
        this.rating = rating;
        this.transac_rating = transac_rating;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getTransac_rating() {
        return transac_rating;
    }

    public void setTransac_rating(int transac_rating) {
        this.transac_rating = (int) ((rating * 0.4) + (sold * 0.6));
    }
}
