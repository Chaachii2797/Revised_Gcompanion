package cabiso.daphny.com.g_companion.Model;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class DIYnames implements Comparable<DIYnames>, Serializable{

    public String diyName;
    public String diyUrl;
    public String user_id;
    public String productID;
    public String identity;
    public Float bookmarks;
    public Float likes;
    public int materialMatches;

    public DIYnames(String diyName, String diyUrl, String user_id, String productID, String identity,
                    Float bookmarks, Float likes){
        this.diyName = diyName;
        this.diyUrl = diyUrl;
        this.user_id = user_id;
        this.productID = productID;
        this.identity = identity;
        this.bookmarks = bookmarks;
        this.likes = likes;
    }

    public DIYnames() {

    }


    public int getMaterialMatches() {
        return materialMatches;
    }

    public DIYnames setMaterialMatches(int materialMatches) {
        this.materialMatches = materialMatches;
        return this;
    }

    public String getDiyName() {
        return diyName;
    }

    public DIYnames setDiyName(String diyName) {
        this.diyName = diyName;
        return this;
    }

    public String getDiyUrl() {
        return diyUrl;
    }

    public DIYnames setDiyUrl(String diyUrl) {
        this.diyUrl = diyUrl;
        return this;
    }

    public String getUser_id() {
        return user_id;
    }

    public DIYnames setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getProductID() {
        return productID;
    }

    public DIYnames setProductID(String productID) {
        this.productID = productID;
        return this;
    }

    public String getIdentity() {
        return identity;
    }

    public DIYnames setIdentity(String identity) {
        this.identity = identity;
        return this;
    }

    public Float getBookmarks() {
        return bookmarks;
    }

    public DIYnames setBookmarks(Float bookmarks) {
        this.bookmarks = bookmarks;
        return this;
    }

    public Float getLikes() {
        return likes;
    }

    public DIYnames setLikes(Float likes) {
        this.likes = likes;
        return this;
    }

    @Override
    public int compareTo(@NonNull DIYnames o) {
        if(bookmarks.floatValue() > o.bookmarks.floatValue() && likes.floatValue() > o.likes.floatValue()) {
            return 1;
        }else if(bookmarks.floatValue() < o.bookmarks.floatValue() && likes.floatValue() < o.likes.floatValue()){
            return -1;
        }else{
            return 0;
        }
    }

    @Override
    public String toString() {
        return "product name : " + diyName;
    }
}
