package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 2/8/2018.
 */

public class QuantityItem implements CharSequence{

    String quantity;

    public QuantityItem(){}

    public QuantityItem(String quantity) {
        this.quantity = quantity;
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
