package cabiso.daphny.com.g_companion.Recommend;

import java.util.List;

/**
 * Created by cicctuser on 9/12/2017.
 */

public class CategoryInfo {
    public String name;
    public String material;
    public String procedure;
    public List<String> categoryPictureURLs;

    CategoryInfo(){

    }

    CategoryInfo(String name, String material, String procedure, List<String> categoryPictureURLs){
        this.name = name;
        this.material = material;
        this. procedure = procedure;
        this.categoryPictureURLs = categoryPictureURLs;
    }
}
