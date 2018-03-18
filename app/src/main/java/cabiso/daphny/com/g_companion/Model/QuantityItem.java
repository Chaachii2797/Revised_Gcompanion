package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 2/8/2018.
 */

public class QuantityItem implements CharSequence{

    String quantity;
    int qty_matches;

    public QuantityItem(){}

    public QuantityItem(String quantity) {
        this.quantity = quantity;
    }

    public int getQty_matches() {
        return qty_matches;
    }

    public void setQty_matches(int qty_matches) {
        this.qty_matches = qty_matches;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }
}
