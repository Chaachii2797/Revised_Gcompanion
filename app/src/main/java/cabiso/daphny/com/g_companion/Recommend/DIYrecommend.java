package cabiso.daphny.com.g_companion.Recommend;

/**
 * Created by Lenovo on 7/30/2017.
 */

public class DIYrecommend {

    private String Image_URL, diyName, diymaterial, diyprocedure;

    public DIYrecommend(String image_URL, String diyName, String diymaterial, String diyprocedure) {
        Image_URL = image_URL;
        this.diyName = diyName;
        this.diymaterial = diymaterial;
        this.diyprocedure = diyprocedure;
    }

    public DIYrecommend(){
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


}

