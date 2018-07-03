package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 6/26/2018.
 */

public class DIYBidding {
    private int price_max;
    private int price_min;
    private String date;
    private String bidder;
    private String message;

    public DIYBidding(){

    }

    public int getPrice_max() {
        return price_max;
    }

    public DIYBidding setPrice_max(int price_max) {
        this.price_max = price_max;
        return this;
    }

    public int getPrice_min() {
        return price_min;
    }

    public DIYBidding setPrice_min(int price_min) {
        this.price_min = price_min;
        return this;
    }

    public String getDate() {
        return date;
    }

    public DIYBidding setDate(String date) {
        this.date = date;
        return this;
    }

    public String getBidder() {
        return bidder;
    }

    public DIYBidding setBidder(String bidder) {
        this.bidder = bidder;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public DIYBidding setMessage(String message) {
        this.message = message;
        return this;
    }
}
