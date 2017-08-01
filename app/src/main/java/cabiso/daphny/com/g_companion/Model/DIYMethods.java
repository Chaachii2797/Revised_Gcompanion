package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 7/30/2017.
 */

public class DIYMethods {
    private String diyName, diyPrice, diyCategory;

    public DIYMethods(String diyName, String diyPrice, String diyCategory) {
        this.diyName = diyName;
        this.diyPrice = diyPrice;
        this.diyCategory = diyCategory;
    }

    public DIYMethods(){
    }

    public String getDiyName() {
        return diyName;
    }

    public void setDiyName(String diyName) {
        this.diyName = diyName;
    }

    public String getDiyPrice() {
        return diyPrice;
    }

    public void setDiyPrice(String diyPrice) {
        this.diyPrice = diyPrice;
    }

    public String getDiyCategory() {
        return diyCategory;
    }

    public void setDiyCategory(String diyCategory) {
        this.diyCategory = diyCategory;
    }
}
