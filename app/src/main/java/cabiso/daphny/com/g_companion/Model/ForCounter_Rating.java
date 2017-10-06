package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 10/1/2017.
 */

public class ForCounter_Rating {
    int sold;
    String ownerID;

    public ForCounter_Rating(){

    }
    public ForCounter_Rating(int sold, String ownerID) {
        this.sold = sold;
        this.ownerID = ownerID;
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
}
