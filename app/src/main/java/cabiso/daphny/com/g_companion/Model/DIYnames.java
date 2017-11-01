package cabiso.daphny.com.g_companion.Model;

/**
 * Created by Lenovo on 10/23/2017.
 */

public class DIYnames extends CommunityItem {

    public String diyName;
    public String diyUrl;

    public  DIYnames(){}


    public DIYnames(String diyName, String diyUrl){
        this.diyName = diyName;
        this.diyUrl = diyUrl;
    }

    public String getDiyName() {
        return diyName;
    }

    public void setDiyName(String diyName) {
        this.diyName = diyName;
    }

    public String getDiyUrl() {
        return diyUrl;
    }

    public void setDiyUrl(String diyUrl) {
        this.diyUrl = diyUrl;
    }
}
