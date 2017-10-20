package cabiso.daphny.com.g_companion.Recommend;

/**
 * Created by Lenovo on 7/30/2017.
 */

public class DIYrecommend{

    public String diyName, diymaterial, diyprocedure, diyImageUrl, diyownerID, diyTags;
//    public int sold_items, transac_rating;
    public DIYrecommend(String diyName, String diymaterial, String diyprocedure, String diyImageUrl,
                        String diyownerID, String diyTags) {
        this.diyName = diyName;
        this.diymaterial = diymaterial;
        this.diyprocedure = diyprocedure;
        this.diyImageUrl = diyImageUrl;
        this.diyownerID  = diyownerID;
        this.diyTags = diyTags;
//        this.sold_items = sold_items;
//        this.transac_rating = transac_rating;
    }

    public DIYrecommend(){
    }

//    @Override
//    public int compareTo(@NonNull DIYrecommend o) {
//        return transac_rating - o.getTransac_rating();
//    }
//
//    class compareBysoldItems implements Comparator<DIYrecommend>{
//
//        @Override
//        public int compare(DIYrecommend o1, DIYrecommend o2) {
//            return o1.getTransac_rating() - o2.getTransac_rating();
//        }
//    }

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

    public String getDiyTags() {
        return diyTags;
    }

    public void setDiyTags(String diyTags) {
        this.diyTags = diyTags;
    }

    //    public int getSold_items() {
//        return sold_items;
//    }
//
//    public void setSold_items(int sold_items) {
//        this.sold_items = sold_items;
//    }
//
//    public int getTransac_rating() {
//        return transac_rating;
//    }
//
//    public void setTransac_rating(int transac_rating) {
//        this.transac_rating = transac_rating;
//    }
}

