package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYBidding;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 7/2/2018.
 */

public class BiddersAdapter extends RecyclerView.Adapter<BiddersAdapter.MyViewHolder>{

    private final Context mContext;
    private final List<DIYBidding> mBiddings;

    public BiddersAdapter(Context context, List<DIYBidding> biddings){
        mContext = context;
        mBiddings = biddings;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_bidders, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DIYBidding biddingItem = mBiddings.get(position);
        if(biddingItem != null){
            holder.mItemDateBidded.setText(biddingItem.getDate());
            holder.mItemPriceBidded.setText(biddingItem.getPrice_min()+" - "+biddingItem.getPrice_max());
            holder.mItemPropStmt.setText(biddingItem.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mBiddings.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView mItemDateBidded;
        private final TextView mItemPriceBidded;
        private final TextView mItemPropStmt;

        public MyViewHolder(View itemView) {
            super(itemView);
            mItemDateBidded = (TextView) itemView.findViewById(R.id.item_date_bidded);
            mItemPriceBidded = (TextView) itemView.findViewById(R.id.item_price_bidded);
            mItemPropStmt = (TextView) itemView.findViewById(R.id.item_bidders_cmnt);
        }
    }
}
