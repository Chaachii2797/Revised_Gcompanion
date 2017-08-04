package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 7/30/2017.
 */

public class DIYMethods {
    private String diyName, diyMaterials, diyProcedures, diyQty;

    public DIYMethods(String diyName, String diyMaterials, String diyProcedures, String diyQty) {
        this.diyName = diyName;
        this.diyMaterials = diyMaterials;
        this.diyProcedures = diyProcedures;
        this.diyQty = diyQty;
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


    public String getDiyQty() {
        return diyQty;
    }
    public void setDiyQty(String diyQty) {
        this.diyQty = diyQty;
    }
}
