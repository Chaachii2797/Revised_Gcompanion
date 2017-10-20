package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 7/30/2017.
 */

public class DIYMethods {
    private String diyName;
    private String diyMaterials;
    private String diyProcedures;
    private String diyTags;

    public DIYMethods(String diyName, String diyMaterials, String diyProcedures, String diyTags) {
        this.diyName = diyName;
        this.diyMaterials = diyMaterials;
        this.diyProcedures = diyProcedures;
        this.diyTags = diyTags;
    }

    public DIYMethods(){
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

    public String getDiyTags() {
        return diyTags;
    }

    public void setDiyTags(String diyTags) {
        this.diyTags = diyTags;
    }

}
