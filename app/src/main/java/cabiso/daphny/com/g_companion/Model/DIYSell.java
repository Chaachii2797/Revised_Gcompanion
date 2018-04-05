package cabiso.daphny.com.g_companion.Model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Lenovo on 1/7/2018.
 */

public class DIYSell implements Comparable<DIYSell>, Serializable{

    public String diyName;
    public String diyUrl;
    public String user_id;
    public String productID;
    public String status;
    public Float bookmarks;
    public Float likes;
    public int materialMatches;

    public DIYSell(String diyName, String diyUrl, String user_id, String productID, String status,
                    Float bookmarks, Float likes){
        this.diyName = diyName;
        this.diyUrl = diyUrl;
        this.user_id = user_id;
        this.productID = productID;
        this.status = status;
        this.bookmarks = bookmarks;
        this.likes = likes;
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

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
