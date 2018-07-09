package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 4/5/2018.
 */

public class SellingDIY {
    double selling_price;
    int selling_qty;
    String selling_descr;

    public double getSelling_price() {
        return selling_price;
    }

    public SellingDIY setSelling_price(double selling_price) {
        this.selling_price = selling_price;
        return this;
    }

    public int getSelling_qty() {
        return selling_qty;
    }

    public SellingDIY setSelling_qty(int selling_qty) {
        this.selling_qty = selling_qty;
        return this;
    }

    public String getSelling_descr() {
        return selling_descr;
    }

    public SellingDIY setSelling_descr(String selling_descr) {
        this.selling_descr = selling_descr;
        return this;
    }
}
