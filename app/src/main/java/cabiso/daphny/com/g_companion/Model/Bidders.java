package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 7/4/2018.
 */

public class Bidders {
    private String bid_price;
    private String user_id;

    public Bidders() {
    }

    public Bidders(String s, String userID){
        this.bid_price = s;
        this.user_id = userID;
    }

    public String getBid_price() {
        return bid_price;
    }

    public Bidders setBid_price(String bid_price) {
        this.bid_price = bid_price;
        return this;
    }

    public String getUser_id() {
        return user_id;
    }

    public Bidders setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }
}
