package cabiso.daphny.com.g_companion.Recommend;

/**
 * Created by cicctuser on 8/24/2017.
 */

public class RecommendSaveTag {
    private String diyCategory;

    public RecommendSaveTag(){}
    public RecommendSaveTag(String diyCategory) {
        this.diyCategory = diyCategory;
    }

    public String getDiyCategory() {
        return diyCategory;
    }

    public void setDiyCategory(String diyCategory) {
        this.diyCategory = diyCategory;
    }
}
