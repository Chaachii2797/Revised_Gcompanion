package cabiso.daphny.com.g_companion.MainDIYS;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.List;

import cabiso.daphny.com.g_companion.Bidding.DIYBidding;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.ViewRelatedDIYS;

/**
 * Created by Lenovo on 8/31/2018.
 */

public class DIYSCardListAdapter extends RecyclerView.Adapter<DIYSCardListAdapter.ViewHolder> {


    private List<DIYnames> diYnamesList;
    private Context mContext;
    private String username;
    private DatabaseReference userdata_reference, diyPriceReference;
    private FirebaseUser mFirebaseUser;
    private String userID;
    final ArrayList<String> kkey = new ArrayList<>();

    public int heartCount=0; //pila ka heart * 0.4
    public int starCount=0; //pila ka bookmark * 0.6

    public double totalBmLike = ((starCount * 0.6) + (heartCount * 0.4));// i plus ang total percent sa bookmark ug like
    public int totalDIYs = 0; //pila kabuok diys ang under ana nga tag nya i divide daton nas totaBmLike times 100

    public DIYSCardListAdapter(Context context, List<DIYnames> diYnamesList){
        this.mContext = context;
        this.diYnamesList = diYnamesList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.diys_main_items, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int itemRef = position;

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        userdata_reference = FirebaseDatabase.getInstance().getReference().child("userdata");
        diyPriceReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");


        final DIYnames item = diYnamesList.get(position);
        if(item != null){
            holder.tvDIyMaintName.setText(item.getDiyName());

            holder.tvDIYMainPrice.setText(String.valueOf(item.getSelling_price()));

            userdata_reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);

                    if(item.getUser_id().equals(user_profile.getUserID())){
                        username = user_profile.getF_name()+" "+user_profile.getL_name();
                        Log.e("userID", item.user_id);
                        Log.e("username", username);
                    }else{
                        holder.tvDIYMainOwner.setText("NAN");
                    }
                    holder.tvDIYMainOwner.setText("by: " + username);

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

            diyPriceReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    DIYSell diysell = dataSnapshot.getValue(DIYSell.class);


                    if(item.getIdentity().equalsIgnoreCase("community")) {
                        holder.tvDIYMainPrice.setText("Free");
                    }
                        if(item.getProductID().equals(diysell.getProductID())){
                            kkey.add(dataSnapshot.getKey());
                            Log.e("keyy", dataSnapshot.getKey() );

                            String message_price = "";
                            String message_qty = "";
                            String message_dsc = "";

                            List<String> message_Price = new ArrayList<String>();
                            List<String> message_Qty = new ArrayList<String>();
                            final List<String> message_Dsc = new ArrayList<String>();

                            for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                                double price = postSnapshot.child("selling_price").getValue(double.class);
                                int qty = postSnapshot.child("selling_qty").getValue(int.class);
                                String dsc = postSnapshot.child("selling_descr").getValue(String.class);

                                message_qty += qty;
                                message_Qty.add(message_qty);

                                message_price += price;
                                message_Price.add(message_price);

                                message_dsc += dsc;
                                message_Dsc.add(message_dsc);

                            }
                            holder.tvDIYMainPrice.setText("₱: " + message_price);
                            Log.e("descccc", " " + item.getSelling_descr());

                            Log.e("pricessss", message_price + " = " + message_qty + " = " + message_dsc);
                            Log.e("messPrice", String.valueOf(message_Price));


                            holder.mStar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String keyss = kkey.get(position);
                                    Toast.makeText(mContext, "Bookmark DIY!" + " " + starCount, Toast.LENGTH_SHORT).show();
                                    starCount++;
                                    totalBmLike++;

                                    final DatabaseReference db = FirebaseDatabase.getInstance().getReference("bookmarks");
                                    db.child(userID).push().setValue(item);

                                    holder.mStar.setColorFilter(ContextCompat.getColor(mContext, R.color.star_yello));

                                    if (holder.starResult.put("bookmarks", starCount * 0.6) != null) {
                                        diyPriceReference.child(keyss).updateChildren(holder.starResult);

                                    }
                                }
                            });


                            holder.mHeart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String keysss = kkey.get(position);

                                    Toast.makeText(mContext, "Liked DIY!" + " " + heartCount, Toast.LENGTH_SHORT).show();
                                    heartCount++;
                                    totalBmLike++;

                                    holder.mHeart.setColorFilter(Color.RED);

                                    if (holder.likeResult.put("likes", heartCount * 0.4) != null) {
                                        diyPriceReference.child(keysss).updateChildren(holder.likeResult);
                                    }
                                }
                            });


                            if(item.getIdentity().equalsIgnoreCase("on bid!")){
                                for (final DataSnapshot insideDataSnapshot: dataSnapshot.child("bidding").getChildren()) {
                                    final DIYBidding biddingItem = insideDataSnapshot.getValue(DIYBidding.class);
                                    Log.e("biddingItems", String.valueOf(biddingItem.getIntial_price()));
                                    holder.tvDIYMainPrice.setText("Initial Bid:  ₱ " + biddingItem.getIntial_price());

                                }
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


            if(item.getDiyUrl()!=null){
                Glide.with(mContext).load(item.getDiyUrl()).into(holder.tvDiyMainImage);

            }

        }

    }

    @Override
    public int getItemCount() {
        return diYnamesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView tvDiyMainImage;
        TextView tvDIyMaintName;
        TextView tvDIYMainOwner;
        TextView tvDIYMainPrice;
        ImageButton mStar;
        ImageButton mHeart;
        HashMap<String, Object> starResult = new HashMap<>();
        HashMap<String, Object> likeResult = new HashMap<>();


        ViewHolder(final View itemView) {
            super(itemView);
            tvDIyMaintName = (TextView) itemView.findViewById(R.id.diy_item_main_name);
            tvDIYMainOwner = (TextView) itemView.findViewById(R.id.diy_item_main_owner);
            tvDIYMainPrice = (TextView) itemView.findViewById(R.id.diy_item_main_price);
            tvDiyMainImage = (ImageView) itemView.findViewById(R.id.diy_main_item_image);
            mStar = (ImageButton) itemView.findViewById(R.id.staru);
            mHeart = (ImageButton) itemView.findViewById(R.id.heartu);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // deal with itemView click
                    DIYnames relDiys = (DIYnames) diYnamesList.get(getAdapterPosition());


                    //send data to ViewRelatedDIYS using intent
                    Intent intent = new Intent(itemView.getContext(), ViewRelatedDIYS.class);
                    intent.putExtra("Nname", relDiys.diyName);
                    Log.e("Nname", relDiys.diyName);

                    itemView.getContext().startActivity(intent);

                    Toast.makeText(itemView.getContext(), tvDIyMaintName.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

}
