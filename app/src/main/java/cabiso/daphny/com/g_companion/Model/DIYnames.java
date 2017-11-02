package cabiso.daphny.com.g_companion.Model;

/**
 * Created by Lenovo on 10/23/2017.
 */

public class DIYnames {

    public String diyName;
    public String diyUrl;
    private String tags;

    public DIYnames(){}




    public DIYnames(String diyName, String diyUrl, String tags){
        this.diyName = diyName;
        this.diyUrl = diyUrl;
        this.tags = tags;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
