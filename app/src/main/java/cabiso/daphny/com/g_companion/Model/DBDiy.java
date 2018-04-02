package cabiso.daphny.com.g_companion.Model;

import java.util.ArrayList;

/**
 * Created by cicctuser on 4/2/2018.
 */

public class DBDiy {
    private String productID;
    private int bookmarks;
    private String name;
    private String url;
    private int likes;
    private ArrayList<DBMaterial> dbMaterials;
    private ArrayList<String> procedures;
    private String user_id;

    public DBDiy(){

    }

    public String getProductID() {
        return productID;
    }

    public DBDiy setProductID(String productID) {
        this.productID = productID;
        return this;
    }

    public int getBookmarks() {
        return bookmarks;
    }

    public DBDiy setBookmarks(int bookmarks) {
        this.bookmarks = bookmarks;
        return this;
    }

    public String getName() {
        return name;
    }

    public DBDiy setName(String name) {
        this.name = name;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DBDiy setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getLikes() {
        return likes;
    }

    public DBDiy setLikes(int likes) {
        this.likes = likes;
        return this;
    }

    public ArrayList<DBMaterial> getDbMaterials() {
        return dbMaterials;
    }

    public DBDiy setDbMaterials(ArrayList<DBMaterial> dbMaterials) {
        this.dbMaterials = dbMaterials;
        return this;
    }

    public ArrayList<String> getProcedures() {
        return procedures;
    }

    public DBDiy setProcedures(ArrayList<String> procedures) {
        this.procedures = procedures;
        return this;
    }

    public String getUser_id() {
        return user_id;
    }

    public DBDiy setUser_id(String user_id) {
        this.user_id = user_id;
        return this;
    }
}
