package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cabiso.daphny.com.g_companion.Model.Bidders;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 7/2/2018.
 */

public class BiddersAdapter extends RecyclerView.Adapter<BiddersAdapter.MyViewHolder>{

    private final Context mContext;
    private final List<Bidders> mBiddings;

    public BiddersAdapter(Context context, List<Bidders> biddings){
        mContext = context;
        mBiddings = biddings;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bidders_bid, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Bidders biddingItem = mBiddings.get(position);
        holder.mItemPriceBidded.setText(biddingItem.getBid_price());
        holder.mItemBiddersName.setText(biddingItem.getUser_id());
//        if(biddingItem != null){
//        }
    }

    @Override
    public int getItemCount() {
        return mBiddings.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView mItemPriceBidded;
        private final TextView mItemBiddersName;

        public MyViewHolder(View itemView) {
            super(itemView);
            mItemPriceBidded = (TextView) itemView.findViewById(R.id.tv_bidders_price);
            mItemBiddersName = (TextView) itemView.findViewById(R.id.bidders_name);
        }
    }
}
