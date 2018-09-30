package cabiso.daphny.com.g_companion.Bidding;

/**
 * Created by cicctuser on 6/26/2018.
 */

public class DIYBidding {
    private int intial_price;
    private int quantity;
    private String date;
    private String xpire_date;
    private String bidder;
    private String message;

    public DIYBidding(){

    }

    public int getIntial_price() {
        return intial_price;
    }

    public DIYBidding setInitialPrice(int intial_price) {
        this.intial_price = intial_price;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public DIYBidding setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getDate() {
        return date;
    }

    public DIYBidding setDate(String date) {
        this.date = date;
        return this;
    }

    public String getXpire_date() {
        return xpire_date;
    }

    public DIYBidding setXpire_date(String xpire_date) {
        this.xpire_date = xpire_date;
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
