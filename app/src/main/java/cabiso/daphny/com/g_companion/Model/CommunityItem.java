package cabiso.daphny.com.g_companion.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lenovo on 10/19/2017.
 */

public class CommunityItem implements Parcelable {


        String val;

    public  CommunityItem(){}


        public CommunityItem(String val) {
            this.val = val;
        }

    protected CommunityItem(Parcel in) {
        val = in.readString();
    }

    public static final Parcelable.Creator<CommunityItem> CREATOR = new Parcelable.Creator<CommunityItem>() {
        @Override
        public CommunityItem createFromParcel(Parcel in) {
            return new CommunityItem(in);
        }

        @Override
        public CommunityItem[] newArray(int size) {
            return new CommunityItem[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(val);
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

}
