package cabiso.daphny.com.g_companion;

import java.util.List;

/**
 * Created by Lenovo on 8/22/2017.
 */

public class ProductInfo {
    public String title;
    public String desc;
    public String price;
    public String negotiable;
    public List<String> productPictureURLs;
    public String ownerUserID="";

    ProductInfo(){

    }

    ProductInfo(String title, String desc, String price, String negotiable, List<String> productPictureURLs, String ownerUserID){
        this.title = title;
        this.desc = desc;
        this.price = price;
        this.negotiable = negotiable;
        this.productPictureURLs = productPictureURLs;
        this.ownerUserID = ownerUserID;
    }

    public String getOwnerUserID() {
        return ownerUserID;
    }

    public void setOwnerUserID(String ownerUserID) {
        this.ownerUserID = ownerUserID;
    }

    public List<String> getProductPictureURLs() {
        return productPictureURLs;
    }

    public void setProductPictureURLs(List<String> productPictureURLs) {
        this.productPictureURLs = productPictureURLs;
    }
}
