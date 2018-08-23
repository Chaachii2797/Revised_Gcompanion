package cabiso.daphny.com.g_companion.Promo;

/**
 * Created by cicctuser on 8/23/2018.
 */

public class PriceDiscountModel {
    private String promo_id;
    private String promo_diyName;
    private String promo_expiry;
    private String promo_details;
    private String percent_discount;
    private String promo_newPrice;
    private String promo_image;
    private String promo_createdDate;

    public PriceDiscountModel(){

    }

    public PriceDiscountModel(String promo_id, String promo_diyName, String promo_expiry, String promo_details,
                              String percent_discount, String promo_newPrice, String promo_image, String promo_createdDate) {
        this.promo_id = promo_id;
        this.promo_diyName = promo_diyName;
        this.promo_expiry = promo_expiry;
        this.promo_details = promo_details;
        this.percent_discount = percent_discount;
        this.promo_newPrice = promo_newPrice;
        this.promo_image = promo_image;
        this.promo_createdDate = promo_createdDate;
    }

    public String getPromo_id() {
        return promo_id;
    }

    public PriceDiscountModel setPromo_id(String promo_id) {
        this.promo_id = promo_id;
        return this;
    }

    public String getPromo_diyName() {
        return promo_diyName;
    }

    public PriceDiscountModel setPromo_diyName(String promo_diyName) {
        this.promo_diyName = promo_diyName;
        return this;
    }

    public String getPromo_expiry() {
        return promo_expiry;
    }

    public PriceDiscountModel setPromo_expiry(String promo_expiry) {
        this.promo_expiry = promo_expiry;
        return this;
    }

    public String getPromo_details() {
        return promo_details;
    }

    public PriceDiscountModel setPromo_details(String promo_details) {
        this.promo_details = promo_details;
        return this;
    }

    public String getPercent_discount() {
        return percent_discount;
    }

    public PriceDiscountModel setPercent_discount(String percent_discount) {
        this.percent_discount = percent_discount;
        return this;
    }

    public String getPromo_newPrice() {
        return promo_newPrice;
    }

    public PriceDiscountModel setPromo_newPrice(String promo_newPrice) {
        this.promo_newPrice = promo_newPrice;
        return this;
    }

    public String getPromo_image() {
        return promo_image;
    }

    public PriceDiscountModel setPromo_image(String promo_image) {
        this.promo_image = promo_image;
        return this;
    }

    public String getPromo_createdDate() {
        return promo_createdDate;
    }

    public PriceDiscountModel setPromo_createdDate(String promo_createdDate) {
        this.promo_createdDate = promo_createdDate;
        return this;
    }
}
