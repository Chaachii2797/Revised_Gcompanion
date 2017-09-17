package cabiso.daphny.com.g_companion.Recommend;

import android.net.Uri;

import java.util.List;

/**
 * Created by Lenovo on 7/30/2017.
 */

public class DIYrecommend {

    public String diyName, diymaterial, diyprocedure;
    public List<String> diyImageUrl;
    public DIYrecommend(String diyName, String diymaterial, String diyprocedure, List<String> diyImageUrl) {
        this.diyName = diyName;
        this.diymaterial = diymaterial;
        this.diyprocedure = diyprocedure;
        this.diyImageUrl = diyImageUrl;
    }


    public DIYrecommend(String diyName, String diymaterial, String diyprocedure) {
        this.diyName = diyName;
        this.diymaterial = diymaterial;
        this.diyprocedure = diyprocedure;
    }
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

    public List<String> getDiyImageUrl() {
        return diyImageUrl;
    }

    public void setDiyImageUrl(List<String> diyImageUrl) {
        this.diyImageUrl = diyImageUrl;
    }
}

