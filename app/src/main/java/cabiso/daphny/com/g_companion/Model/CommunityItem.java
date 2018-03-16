package cabiso.daphny.com.g_companion.Model;

/**
 * Created by Lenovo on 10/19/2017.
 */

public class CommunityItem implements CharSequence {


    String val;

    public  CommunityItem(){}


    public CommunityItem(String val) {
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
