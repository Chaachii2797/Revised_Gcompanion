package cabiso.daphny.com.g_companion.Promo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;
/**
 * Created by cicct on 3/26/2017.
 */

public class PromoCardListAdapter extends RecyclerView.Adapter<PromoCardListAdapter.ViewHolder> {
    private List<PromoModel> mPromoModels;
    private List<DIYnames> diYnamesList;
    private Context mContext;
    String itemDetails;

    public PromoCardListAdapter(Context context, List<PromoModel> promoModels, List<DIYnames> diYnamesList){
        this.mContext = context;
        this.mPromoModels = promoModels;
        this.diYnamesList = diYnamesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promo_sale_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PromoModel item = mPromoModels.get(position);
        itemDetails = "Free: ";
            if(item != null){
                holder.tvPromoProductName.setText(item.getPromo_diyName());
                ArrayList<DIYnames> freeItems = item.getFreeItemList();
                ArrayList<String> freeItemsQuantity = item.getFreeItemQuantity();
                for(int x = 0; x < freeItems.size(); x++){
                    itemDetails+= "("+freeItemsQuantity.get(x)+") "+freeItems.get(x).getDiyName()+", ";
                }
                if(item.getPromo_image()!=null){
                    Glide.with(mContext).load(item.getPromo_image()).into(holder.tvPromoMainImage);
                }
                if(item.getStatus().equalsIgnoreCase("wholesale")){
                    holder.tvPromoDetail.setText(itemDetails);
                }else{
                    holder.tvPromoDetail.setText("Discount Promo");
                }

            }

    }

    @Override
    public int getItemCount() {
        return mPromoModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView tvPromoMainImage;
        TextView tvPromoProductName;
        TextView tvPromoDetail;

        ViewHolder(View itemView) {
            super(itemView);
            tvPromoProductName = (TextView) itemView.findViewById(R.id.promo_item_name);
            tvPromoDetail = (TextView) itemView.findViewById(R.id.promo_item_name_owner);
            tvPromoMainImage = (ImageView) itemView.findViewById(R.id.promo_main_item_image);
        }

        public void onClick(View view) {
            // deal with itemView click
        }
    }
}
