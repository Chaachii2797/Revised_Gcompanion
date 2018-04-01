package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 2/8/2018.
 */

public class QuantityItem implements CharSequence{

    String unit;
    int quantity;

    public QuantityItem(){}

    public QuantityItem(String unit) {
        this.unit = unit;
    }

    public QuantityItem(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
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
