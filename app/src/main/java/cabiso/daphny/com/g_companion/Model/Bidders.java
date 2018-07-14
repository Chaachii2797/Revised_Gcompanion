package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 7/4/2018.
 */

public class Bidders {
    private String bid_price;
    private String user_id;
    private String user_name;
    private String bid_date;

    public Bidders() {
    }

    public Bidders(String s, String userID, String user_name, String bid_date){
        this.bid_price = s;
        this.user_id = userID;
        this.user_name = user_name;
        this.bid_date = bid_date;
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

    public String getUser_name() {
        return user_name;
    }

    public Bidders setUser_name(String user_name) {
        this.user_name = user_name;
        return this;
    }

    public String getBid_date() {
        return bid_date;
    }

    public Bidders setBid_date(String bid_date) {
        this.bid_date = bid_date;
        return this;
    }
}
