package cabiso.daphny.com.g_companion.Model;

import android.support.annotation.NonNull;

/**
 * Created by Lenovo on 10/23/2017.
 */

public class DIYnames implements Comparable<DIYnames>{

    public String diyName;
    public String diyUrl;
    public String user_id;
    public String tag;
    public String productID;
    public Float bookmarks;
    public Float likes;

    public DIYnames(){}

    public DIYnames(String diyName, String diyUrl, String user_id, String tag, String productID, Float bookmarks, Float likes){
        this.diyName = diyName;
        this.diyUrl = diyUrl;
        this.user_id = user_id;
        this.tag = tag;
        this.productID = productID;
        this.bookmarks = bookmarks;
        this.likes = likes;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
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
}
