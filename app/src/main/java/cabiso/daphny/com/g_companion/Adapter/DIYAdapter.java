package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cabiso.daphny.com.g_companion.Maps.ViewSearchedDIYS;
import cabiso.daphny.com.g_companion.Model.DIYData;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 8/20/2017.
 */

public class DIYAdapter extends RecyclerView.Adapter<DIYAdapter.ViewHolder> {

    private Context mContext;
    private ViewHolder holder;
    private List<DIYData> mDIYs;
    private String user, email;

    public DIYAdapter(Context mContext, List<DIYData> mDIYs, String user, String email) {
        this.mContext = mContext;
        this.mDIYs = mDIYs;
        this.user = user;
        this.email = email;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();

        final View apartmentsLayout = LayoutInflater.from(mContext)
                .inflate(R.layout.searchfor_aparts_layout, null);

        holder = new ViewHolder(apartmentsLayout);


        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder Holder, final int position) {
        final DIYData diyData = mDIYs.get(position);

        if (diyData != null) {

            Holder.mTvTitle.setText(diyData.getTitle());
            Holder.mTvPrice.setText("Price: Php " + diyData.getApartmentPrice());
            Holder.mTvAddress.setText(diyData.getAddress());
        }


        Holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ViewSearchedDIYS.class);

                if (diyData != null) {

                    i.putExtra("Title", diyData.getTitle());
                    i.putExtra("Desc", diyData.getDescription());
                    i.putExtra("Cond", diyData.getCondition());
                    i.putExtra("Location", diyData.getLocation());
                    i.putExtra("Address", diyData.getAddress());
                    i.putExtra("Price", diyData.getApartmentPrice());
                    i.putExtra("NumBath", diyData.getNumofBaths());
                    i.putExtra("NumBeds", diyData.getNumofBeds());
                    i.putExtra("LandLordName", diyData.getLandlordName());
                    i.putExtra("Contact", diyData.getLandlordContact());
                    i.putExtra("Email", diyData.getLandlordEmail());
                    i.putExtra("ID", diyData.getApartmentID());
                    i.putExtra("TenantName", user);
                    i.putExtra("TenantEmail", email);

                }
                mContext.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mDIYs.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        ImageView mRecyclerImage;
        TextView mTvTitle, mTvPrice, mTvAddress;

        public ViewHolder(View view) {
            super(view);

            mRecyclerImage = (ImageView) view.findViewById(R.id.imgRecycler);
            mTvTitle = (TextView) view.findViewById(R.id.tvTitle);
            mTvPrice = (TextView) view.findViewById(R.id.tvRecyclerPrice);
            mTvAddress = (TextView) view.findViewById(R.id.tvRecyclerAddress);


        }
    }
}
