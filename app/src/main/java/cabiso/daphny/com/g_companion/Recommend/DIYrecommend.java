package cabiso.daphny.com.g_companion.Recommend;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by Lenovo on 7/30/2017.
 */

public class DIYrecommend implements Comparable<DIYrecommend>{

    public String diyName, diymaterial, diyprocedure, diyImageUrl, diyownerID, category;
    public int sold_items, user_ratings, transac_rating;
    public DIYrecommend(String diyName, String diymaterial, String diyprocedure, String diyImageUrl,
                        String diyownerID, String category, int sold_items, int user_ratings, int transac_rating) {
        this.diyName = diyName;
        this.diymaterial = diymaterial;
        this.diyprocedure = diyprocedure;
        this.diyImageUrl = diyImageUrl;
        this.diyownerID  = diyownerID;
        this.category = category;
        this.sold_items = sold_items;
        this.user_ratings = user_ratings;
        this.transac_rating = transac_rating;
    }


    //    public DIYrecommend(String diyName, String diymaterial, String diyprocedure) {
//        this.diyName = diyName;
//        this.diymaterial = diymaterial;
//        this.diyprocedure = diyprocedure;
//    }
//
    public DIYrecommend(){
    }

    @Override
    public int compareTo(@NonNull DIYrecommend o) {
        return transac_rating - o.getTransac_rating();
    }

    class compareBysoldItems implements Comparator<DIYrecommend>{


        @Override
        public int compare(DIYrecommend o1, DIYrecommend o2) {
            return o1.getTransac_rating() - o2.getTransac_rating();
        }
    }

    public String getDiyName() {
        return diyName;
    }

    public void setDiyName(String diyName) {
        this.diyName = diyName;
    }

    public String getDiymaterial() {
        return diymaterial;
    }

    public void setDiymaterial(String diymaterial) {
        this.diymaterial = diymaterial;
    }

    public String getDiyprocedure() {
        return diyprocedure;
    }

    public void setDiyprocedure(String diyprocedure) {
        this.diyprocedure = diyprocedure;
    }

    public String getDiyImageUrl() {
        return diyImageUrl;
    }

    public void setDiyImageUrl(String diyImageUrl) {
        this.diyImageUrl = diyImageUrl;
    }

    public String getDiyownerID() {
        return diyownerID;
    }

    public void setDiyownerID(String diyownerID) {
        this.diyownerID = diyownerID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSold_items() {
        return sold_items;
    }

    public void setSold_items(int sold_items) {
        this.sold_items = sold_items;
    }

    public int getUser_ratings() {
        return user_ratings;
    }

    public void setUser_ratings(int user_ratings) {
        this.user_ratings = user_ratings;
    }

    public int getTransac_rating() {
        return transac_rating;
    }

    public void setTransac_rating(int transac_rating) {
        this.transac_rating = transac_rating;
    }
}

