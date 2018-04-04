package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 4/4/2018.
 */

public class ImgRecogSetQty {
    private String name;
    private int qty;
    private String unit;

    public String getName() {
        return name;
    }

    public ImgRecogSetQty setName(String name) {
        this.name = name;
        return this;
    }

    public int getQty() {
        return qty;
    }

    public ImgRecogSetQty setQty(int qty) {
        this.qty = qty;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public ImgRecogSetQty setUnit(String unit) {
        this.unit = unit;
        return this;

    }
}
