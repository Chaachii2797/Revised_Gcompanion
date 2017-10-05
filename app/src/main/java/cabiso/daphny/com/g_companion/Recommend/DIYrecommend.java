package cabiso.daphny.com.g_companion.Recommend;

/**
 * Created by Lenovo on 7/30/2017.
 */

public class DIYrecommend {

    public String diyName, diymaterial, diyprocedure, diyImageUrl, diyownerID, category;
    public long sold_items;
    public DIYrecommend(String diyName, String diymaterial, String diyprocedure, String diyImageUrl,
                        String diyownerID, String category, long sold_items) {
        this.diyName = diyName;
        this.diymaterial = diymaterial;
        this.diyprocedure = diyprocedure;
        this.diyImageUrl = diyImageUrl;
        this.diyownerID  = diyownerID;
        this.category = category;
        this.sold_items = sold_items;
    }


    //    public DIYrecommend(String diyName, String diymaterial, String diyprocedure) {
//        this.diyName = diyName;
//        this.diymaterial = diymaterial;
//        this.diyprocedure = diyprocedure;
//    }
//
    public DIYrecommend(){
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

    public long getSold_items() {
        return sold_items;
    }

    public void setSold_items(long sold_items) {
        this.sold_items = sold_items;
    }
}

