package cabiso.daphny.com.g_companion.Model;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class DIYDetails{

    private String diyName;
    private String diyMaterials;
    private String diyProcedures;
    private String Image_URL;


//    public DIYDetails(String diyName, String diyMaterials, String diyProcedures, String image_URL) {
//        this.diyName = diyName;
//        this.diyMaterials = diyMaterials;
//        this.diyProcedures = diyProcedures;
//        this.Image_URL = image_URL;
//
//    }

    public DIYDetails(String dName, String dMaterials, String dProcedures) {
        this.diyName = dName;
        this.diyMaterials = dMaterials;
        this.diyProcedures = dProcedures;
    }

    public DIYDetails(){

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

    public String getImage_URL() {
        return Image_URL;
    }

    public void setImage_URL(String image_URL) {
        Image_URL = image_URL;
    }
}

