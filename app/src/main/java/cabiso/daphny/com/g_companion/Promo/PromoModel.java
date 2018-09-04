package cabiso.daphny.com.g_companion.Promo;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cabiso.daphny.com.g_companion.Model.DIYnames;

/**
 * Created by cicctuser on 8/7/2018.
 */

public class PromoModel implements Serializable{
    public String promo_id;
    public String promo_details;
    public String promo_expiry;
    public String promo_createdDate;
    public String promo_diyName;
    public String promo_image;
//    public Map<String, Object> free_item;
    public ArrayList<DIYnames> freeItemList;
    public ArrayList<String> freeItemQuantity;
    public String buy_counts;
    public String take_counts;
    public String status;

    public PromoModel(){
//        this.free_item = new HashMap<>();
        this.freeItemList = new ArrayList<>();
        this.freeItemQuantity = new ArrayList<>();
    }

    public PromoModel(String promo_id, String promo_details, String promo_expiry, String promo_createdDate, String promo_diyName,
                      String promo_image, String buy_counts, String take_counts, String status) {
        this.promo_id = promo_id;
        this.promo_details = promo_details;
        this.promo_expiry = promo_expiry;
        this.promo_createdDate = promo_createdDate;
        this.promo_diyName = promo_diyName;
        this.promo_image = promo_image;
        this.buy_counts = buy_counts;
        this.take_counts = take_counts;
        this.status = status;
    }

    public PromoModel addPromoItem(int position,DIYnames item,String quantity){
        this.freeItemList.add(position,item);
        this.freeItemQuantity.add(position,quantity+"");
//        this.free_item.put(item.getProductID(),promoItem);
        return this;
    }

    public PromoModel removePromoItem(int position){
//        this.free_item.remove(position);
        this.freeItemList.remove(position);
        return this;
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

    public String getPromo_diyName() {
        return promo_diyName;
    }

    public PromoModel setPromo_diyName(String promo_diyName) {
        this.promo_diyName = promo_diyName;
        return this;
    }

    public String getPromo_image() {
        return promo_image;
    }

    public PromoModel setPromo_image(String promo_image) {
        this.promo_image = promo_image;
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

    public ArrayList<DIYnames> getFreeItemList() {
        return freeItemList;
    }

    public ArrayList<String> getFreeItemQuantity() {
        return freeItemQuantity;
    }

    public String getStatus() {
        return status;
    }

    public PromoModel setStatus(String status) {
        this.status = status;
        return this;
    }
}
