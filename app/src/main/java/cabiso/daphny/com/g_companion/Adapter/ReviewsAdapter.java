package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.Review;

/**
 * Created by cicctuser on 4/16/2018.
 */

public class ReviewsAdapter extends ArrayAdapter<Review> {
    private Context mContext;
    private int mResource;

    public ReviewsAdapter(Context context, int resource, List<Review> reviews){
        super(context,resource,reviews);

    }
}
