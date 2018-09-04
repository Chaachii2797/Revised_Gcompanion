package cabiso.daphny.com.g_companion.Model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Lenovo on 1/7/2018.
 */

public class DIYSell extends SellingDIY implements Comparable<DIYSell>, Serializable{

    public String diyName;
    public String diyUrl;
    public String user_id;
    public String productID;
    public String identity;
    public String buyerID;
    public Float bookmarks;
    public Float likes;
    public int materialMatches;
    public String loggedInUser;

    public DIYSell(String diyName, String diyUrl, String user_id, String productID, String identity,
                    Float bookmarks, Float likes, String buyerID, String loggedInUser){
        this.diyName = diyName;
        this.diyUrl = diyUrl;
        this.user_id = user_id;
        this.productID = productID;
        this.identity = identity;
        this.bookmarks = bookmarks;
        this.likes = likes;
        this.buyerID = buyerID;
        this.loggedInUser = loggedInUser;
    }

    public DIYSell(){
    }

    public String getDiyName() {
        return diyName;
    }

    public void setDiyName(String diyName) {
        this.diyName = diyName;
    }

    public String getDiyUrl() {
        return diyUrl;
    }

    public void setDiyUrl(String diyUrl) {
        this.diyUrl = diyUrl;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(String buyerID) {
        this.buyerID = buyerID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Float getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(Float bookmarks) {
        this.bookmarks = bookmarks;
    }

    public Float getLikes() {
        return likes;
    }

    public void setLikes(Float likes) {
        this.likes = likes;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public int getMaterialMatches() {
        return materialMatches;
    }

    public void setMaterialMatches(int materialMatches) {
        this.materialMatches = materialMatches;
    }

    @Override
    public int compareTo(@NonNull DIYSell o) {
        if(bookmarks.floatValue() > o.bookmarks.floatValue() && likes.floatValue() > o.likes.floatValue()) {
            return 1;
        }else if(bookmarks.floatValue() < o.bookmarks.floatValue() && likes.floatValue() < o.likes.floatValue()){
            return -1;
        }else{
            return 0;
        }
    }

//    @Override
//    public String toString() {
//        return "product name : " + diyName;
//    }

}
