package cabiso.daphny.com.g_companion.MainDIYS;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Promo.PromoModel;
import cabiso.daphny.com.g_companion.Promo.ViewPromoActivity;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/3/2018.
 */

public class ViewMorePromoDIYSAdapter extends RecyclerView.Adapter<ViewMorePromoDIYSAdapter.ViewHolder> {

    private List<PromoModel> diyPromoList;
    private Context mContext;
    private String username;
    private DatabaseReference userdata_reference, diyPromoReference;
    private FirebaseUser mFirebaseUser;
    private String userID;
    int GRID = 0;
    String itemDetails;
    private DatabaseReference promoReference;


    public ViewMorePromoDIYSAdapter(Context context, List<PromoModel> diyPromoList, int GRID){
        this.mContext = context;
        this.diyPromoList = diyPromoList;
        this.GRID = GRID;
    }


    @Override
    public ViewMorePromoDIYSAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.promo_sale_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewMorePromoDIYSAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewMorePromoDIYSAdapter.ViewHolder holder, final int position) {
        int itemRef = position;

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        userdata_reference = FirebaseDatabase.getInstance().getReference().child("userdata");
        diyPromoReference = FirebaseDatabase.getInstance().getReference().child("promo_sale");

        promoReference = FirebaseDatabase.getInstance().getReference().child("promo_sale");
        promoReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("promo_id").getValue(String.class).equals(diyPromoList.get(position).getPromo_id())){
                    int sold_qty = dataSnapshot.child("sellDIYqty").getValue(int.class);
                    Log.e("soldQuatity",sold_qty+"");
                    if(sold_qty == 0 ){
                        holder.tvSoldOutImage.setVisibility(View.VISIBLE);
                    }else{
                        holder.tvSoldOutImage.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        PromoModel item = diyPromoList.get(position);
        itemDetails = "Free: ";

        if(item != null){
            holder.tvPromoProductName.setText("Buy (" + item.getBuy_counts() + ") " + item.getPromo_diyName());
            ArrayList<DIYnames> freeItems = item.getFreeItemList();
            ArrayList<String> freeItemsQuantity = item.getFreeItemQuantity();
            for(int x = 0; x < freeItems.size(); x++){
                itemDetails+= "("+freeItemsQuantity.get(x)+") "+freeItems.get(x).getDiyName();
            }
            if(item.getPromo_image()!=null){
                Glide.with(mContext).load(item.getPromo_image()).into(holder.tvPromoMainImage);
            }
            if(item.getStatus().equalsIgnoreCase("wholesale")){
                holder.tvPromoDetail.setText(itemDetails);
            }else{
                holder.tvPromoProductName.setText(item.getPromo_diyName());
                holder.tvPromoDetail.setText("Discount Promo");
            }
        }

    }

    @Override
    public int getItemCount() {
        return diyPromoList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView tvSoldOutImage;
        ImageView tvPromoMainImage;
        TextView tvPromoProductName;
        TextView tvPromoDetail;

        ViewHolder(final View itemView) {
            super(itemView);
            tvPromoProductName = (TextView) itemView.findViewById(R.id.promo_item_name);
            tvPromoDetail = (TextView) itemView.findViewById(R.id.promo_item_name_owner);
            tvPromoMainImage = (ImageView) itemView.findViewById(R.id.promo_main_item_image);
            tvSoldOutImage = (ImageView) itemView.findViewById(R.id.img_sold_out);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PromoModel promoDiys = (PromoModel) diyPromoList.get(getAdapterPosition());

                    //send data to ViewRelatedDIYS using intent
                    Intent intent = new Intent(itemView.getContext(), ViewPromoActivity.class);
                    intent.putExtra("Pname", promoDiys.promo_diyName);
                    Log.e("Pname", promoDiys.promo_diyName);

                    itemView.getContext().startActivity(intent);

                    Toast.makeText(itemView.getContext(), tvPromoProductName.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

}
