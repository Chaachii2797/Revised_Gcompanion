package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 7/18/2018.
 */

public class CreatePromo {
    private String promo_userID;
    private String promo_user_name;
    private String promo_price;
    private String promo_start;
    private String promo_ends;

    public CreatePromo(String promo_userID, String promo_user_name, String promo_price, String promo_start, String promo_ends) {
        this.promo_userID = promo_userID;
        this.promo_user_name = promo_user_name;
        this.promo_price = promo_price;
        this.promo_start = promo_start;
        this.promo_ends = promo_ends;
    }

    public CreatePromo(){}

    public String getPromo_userID() {
        return promo_userID;
    }

    public CreatePromo setPromo_userID(String promo_userID) {
        this.promo_userID = promo_userID;
        return this;
    }

    public String getUser_promo_name() {
        return promo_user_name;
    }

    public CreatePromo setUser_promo_name(String user_promo_name) {
        this.promo_user_name = user_promo_name;
        return this;
    }

    public String getPromo_price() {
        return promo_price;
    }

    public CreatePromo setPromo_price(String promo_price) {
        this.promo_price = promo_price;
        return this;
    }

    public String getPromo_start() {
        return promo_start;
    }

    public CreatePromo setPromo_start(String promo_start) {
        this.promo_start = promo_start;
        return this;
    }

    public String getPromo_ends() {
        return promo_ends;
    }

    public CreatePromo setPromo_ends(String promo_ends) {
        this.promo_ends = promo_ends;
        return this;
    }
}
