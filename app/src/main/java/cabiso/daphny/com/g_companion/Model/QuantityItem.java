package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 2/8/2018.
 */

public class QuantityItem implements CharSequence{

    String val;

    public QuantityItem(){}

    public QuantityItem(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
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
