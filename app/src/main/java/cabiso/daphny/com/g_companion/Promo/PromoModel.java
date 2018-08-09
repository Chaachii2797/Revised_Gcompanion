package cabiso.daphny.com.g_companion.Promo;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DIYnames;

/**
 * Created by cicctuser on 8/7/2018.
 */

public class PromoModel {
    String promo_id;
    String promo_details;
    String promo_expiry;
    String promo_createdDate;
    ArrayList<DIYnames> promo_diy_names;
    String buy_counts;
    String take_counts;

    public PromoModel(){}

    public PromoModel(String promo_id, String promo_details, String promo_expiry, String promo_createdDate,
                      ArrayList<DIYnames> promo_diy_names, String buy_counts, String take_counts) {
        this.promo_id = promo_id;
        this.promo_details = promo_details;
        this.promo_expiry = promo_expiry;
        this.promo_createdDate = promo_createdDate;
        this.promo_diy_names = promo_diy_names;
        this.buy_counts = buy_counts;
        this.take_counts = take_counts;
    }

    public String getPromo_id() {
        return promo_id;
    }

    public PromoModel setPromo_id(String promo_id) {
        this.promo_id = promo_id;
        return this;
    }

    public String getPromo_details() {
        return promo_details;
    }

    public PromoModel setPromo_details(String promo_details) {
        this.promo_details = promo_details;
        return this;
    }

    public String getPromo_expiry() {
        return promo_expiry;
    }

    public PromoModel setPromo_expiry(String promo_expiry) {
        this.promo_expiry = promo_expiry;
        return this;
    }

    public String getPromo_createdDate() {
        return promo_createdDate;
    }

    public PromoModel setPromo_createdDate(String promo_createdDate) {
        this.promo_createdDate = promo_createdDate;
        return this;
    }

    public ArrayList<DIYnames> getPromo_diy_names() {
        return promo_diy_names;
    }

    public PromoModel setPromo_diy_names(ArrayList<DIYnames> promo_diy_names) {
        this.promo_diy_names = promo_diy_names;
        return this;
    }

    public String getBuy_counts() {
        return buy_counts;
    }

    public PromoModel setBuy_counts(String buy_counts) {
        this.buy_counts = buy_counts;
        return this;
    }

    public String getTake_counts() {
        return take_counts;
    }

    public PromoModel setTake_counts(String take_counts) {
        this.take_counts = take_counts;
        return this;
    }
}
