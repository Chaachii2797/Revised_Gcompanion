package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.ViewRelatedDIYS;

/**
 * Created by Lenovo on 6/27/2018.
 */

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> {

    private Context mContext;
    private DatabaseReference relatedDIYReference, diy_owner;
    private ArrayList<String> ePrice = new ArrayList<>();
    private ArrayList<User_Profile> eOwner = new ArrayList<>();
    private ArrayList<DIYnames> ePics = new ArrayList<>();
    private ArrayList<DIYnames> itemsList;
    private FirebaseUser mFirebaseUser;


    public SectionListDataAdapter(Context context,ArrayList<DIYnames> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }


    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_diys, parent, false);
//        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return new SingleItemRowHolder(v);

    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        relatedDIYReference = FirebaseDatabase.getInstance().getReference().child("same_diy_product");
        diy_owner = FirebaseDatabase.getInstance().getReference().child("userdata");

        //get related DIY owner



        relatedDIYReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final DIYnames diyInfos = dataSnapshot.getValue(DIYnames.class);

                String sell_price = dataSnapshot.child("DIY Price").getValue().toString();
                String quantity = dataSnapshot.child("Item Quantity").getValue().toString();

                Log.e("diy_prices", sell_price);
                Log.e("quantity",quantity);
                final double price = Double.parseDouble(sell_price);
                final int qty = Integer.parseInt(quantity);

                Log.e("prices", String.valueOf(price));
                ePrice.add(dataSnapshot.child("DIY Price").getValue().toString());
                Log.e("ePrice", String.valueOf(ePrice));
//                holder.diyPrice.setText(sell_price);
//                holder.diyQty.setText(qty + " " + "piece/s");

                //Nakuha na siya boshet
                holder.diyPrice.setText("₱:" + " " + ePrice.get(position));

                ePics.add(dataSnapshot.getValue(DIYnames.class));
                Log.e("ePics", String.valueOf(ePics));

                Glide.with(mContext)
                .load(ePics.get(position).getDiyUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.itemImage);

                Log.e("diyItemURL", ePics.get(position).getDiyUrl());

                diy_owner.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        User_Profile owner_profile = dataSnapshot.getValue(User_Profile.class);

                        if(diyInfos.user_id.equals(owner_profile.getUserID())){
                            String user;
                            user = owner_profile.getF_name()+" "+owner_profile.getL_name();
//                            holder.diyOwner.setText(user);
                            Log.e("user",user);

                            eOwner.add(dataSnapshot.getValue(User_Profile.class));
                            Log.e("eOwner", String.valueOf(eOwner));
                            holder.diyOwner.setText("by:" + " " + eOwner.get(position).getF_name() + " " + eOwner.get(position).getL_name());
                            Log.e("fName", eOwner.get(position).getF_name() + " " + eOwner.get(position).getL_name());
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

//        holder.diyPrice.setText((int) sellItem.getSelling_price());
//        Log.e("price", String.valueOf(sellItem.getSelling_price()));

//        user_data = FirebaseDatabase.getInstance().getReference().child("userdata");
//        relatedDIYReference = FirebaseDatabase.getInstance().getReference().child("same_diy_product");
//
//        relatedDIYReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                final DIYnames related_diys = dataSnapshot.getValue(DIYnames.class);
//
//
//
//
//                Log.e("related_diys", String.valueOf(related_diys));
//                Log.e("postSnapshot", String.valueOf(dataSnapshot));
//
//                // pas pas mo fetch sa data pero mag double double
//                Log.e("itemsList", String.valueOf(itemsList.add(related_diys)));
//                Log.e("item", String.valueOf(itemsList));
////                holder.diyName.setText(itemsList.get(position).getDiyName());
//
////                    String diy_price = postSnapshot.child("DIY Price").getValue().toString();
//                String diy_price="";
//                List<String> message_Price = new ArrayList<String>();
//                for (DataSnapshot snapshot : dataSnapshot.child("DIY Price").getChildren()) {
//                    double price= snapshot.child("selling_price").getValue(double.class);
//                    diy_price += price;
//                    message_Price.add(diy_price);
//                }
//                Log.e("diy_price",diy_price);
//
//                holder.diyPrice.setText("₱: " + diy_price);
//
//                Glide.with(mContext)
//                        .load(related_diys.getDiyUrl())
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .centerCrop()
//                        .into(holder.itemImage);
//
//
//                user_data.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        User_Profile owner_profile = dataSnapshot.getValue(User_Profile.class);
//                        if(related_diys.user_id.equals(owner_profile.getUserID())){
//                            user_name = owner_profile.getF_name()+" "+owner_profile.getL_name();
//                            holder.diyOwner.setText(user_name);
//                            Log.e("OWNER","ID"+ related_diys.user_id+" "+"equals"+" "+owner_profile.getUserID());
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
//        return itemsList.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView diyOwner, diyPrice;
        protected ImageView itemImage;


        public SingleItemRowHolder(View view) {
            super(view);

            diyPrice = (TextView) view.findViewById(R.id.related_diy_price);
            diyOwner = (TextView) view.findViewById(R.id.related_diy_owner);
            itemImage = (ImageView) view.findViewById(R.id.retaled_diy_img);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DIYnames diys = new DIYnames();

                    //send data to ViewRelatedDIYS using intent
                    Intent intent = new Intent(v.getContext(), ViewRelatedDIYS.class);
                    intent.putExtra("Nname",diyOwner.getText());
                    intent.putExtra("Pprice", diyPrice.getText());
                    intent.putExtra("image",diys.getDiyUrl());

                    v.buildDrawingCache();
                    Bitmap image = v.getDrawingCache();
                    Bundle extras = new Bundle();
                    extras.putParcelable("imagebitmap", image);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("image", byteArray);
                    v.getContext().startActivity(intent);

                    Toast.makeText(v.getContext(), diyOwner.getText() + "\n" + diyPrice.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
