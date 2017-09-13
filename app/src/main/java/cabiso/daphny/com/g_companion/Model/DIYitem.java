package cabiso.daphny.com.g_companion.Model;

/**
 * Created by Lenovo on 7/30/2017.
 */

public class DIYitem {

    private String Image_URL, diyName, diyMaterials, diyProcedures;

    public DIYitem(String image_URL, String diyName, String diyMaterials, String diyProcedures) {
        Image_URL = image_URL;
        this.diyName = diyName;
        this.diyMaterials = diyMaterials;
        this.diyProcedures = diyProcedures;
    }

    public DIYitem(String dName, String dMaterials, String dProcedures){
    }

    public String getImage_URL() {
        return Image_URL;
    }

    public void setImage_URL(String image_URL) {
        Image_URL = image_URL;
    }

    public String getDiyName() {
        return diyName;
    }

    public void setDiyName(String diyName) {
        this.diyName = diyName;
    }

    public String getDiyMaterials() {
        return diyMaterials;
    }

    public void setDiyMaterials(String diyMaterials) {
        this.diyMaterials = diyMaterials;
    }

    public String getDiyProcedures() {
        return diyProcedures;
    }

    public void setDiyProcedures(String diyProcedures) {
        this.diyProcedures = diyProcedures;
    }

    //    public String getImage_URL() {
//        return Image_URL;
//    }
//
//    public void setImage_URL(String image_URL) {
//        Image_URL = image_URL;
//    }
//
//    public String getDiyName() {
//        return diyName;
//    }
//
//    public void setDiyName(String diyName) {
//        this.diyName = diyName;
//    }
//
//    public String getDiyPrice() {
//        return diyMaterials;
//    }
//
//    public void setDiyPrice(String diyPrice) {
//        this.diyMaterials = diyPrice;
//    }
//
//    public String getDiyCategory() {
//        return diyProcedures;
//    }
//
//    public void setDiyCategory(String diyCategory) {
//        this.diyProcedures = diyCategory;
//    }
}
