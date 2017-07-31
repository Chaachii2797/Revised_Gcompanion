package cabiso.daphny.com.g_companion.Model;

/**
 * Created by Lenovo on 7/31/2017.
 */

public class DIYDetails{

    private String diyName;
    private String diyPrice;
    private String diyCategory;


    public DIYDetails(String diyName, String diyPrice, String diyCategory) {
        this.diyName = diyName;
        this.diyPrice = diyPrice;
        this.diyCategory = diyCategory;
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

