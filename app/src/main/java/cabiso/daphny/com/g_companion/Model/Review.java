package cabiso.daphny.com.g_companion.Model;

/**
 * Created by cicctuser on 4/15/2018.
 */

public class Review {

    int ratings;
    String comment;
    String reviewer;
    String date_submitted;

    public int getRatings() {
        return ratings;
    }

    public Review setRatings(int ratings) {
        this.ratings = ratings;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Review setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getReviewer() {
        return reviewer;
    }

    public Review setReviewer(String reviewer) {
        this.reviewer = reviewer;
        return this;
    }

    public String getDate_submitted() {
        return date_submitted;
    }

    public Review setDate_submitted(String date_submitted) {
        this.date_submitted = date_submitted;
        return this;
    }
}
