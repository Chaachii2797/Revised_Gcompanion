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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private ArrayList<DIYnames> itemsList;
    private Context mContext;
    private DatabaseReference relatedDIYReference, user_data;

    public SectionListDataAdapter(Context context, ArrayList<DIYnames> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.related_diys, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;

    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int i) {


        user_data = FirebaseDatabase.getInstance().getReference().child("userdata");

        relatedDIYReference = FirebaseDatabase.getInstance().getReference().child("same_diy_product");

        relatedDIYReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    final DIYnames related_diys = postSnapshot.getValue(DIYnames.class);
//                    DIYnames dataDIys = new DIYnames();

                    String diy_name = related_diys.getDiyName();
                    String diy_price = postSnapshot.child("DIY Price").getValue().toString();

                    Log.e("postSnapshot", String.valueOf(postSnapshot));
                    holder.diyName.setText(diy_name);
                    Log.e("diy_name",diy_name);
                    holder.diyPrice.setText("₱: " + diy_price);

                    Glide.with(mContext)
                        .load(related_diys.getDiyUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(holder.itemImage);
//                dataDIys.setDiyName(diy_name);

                    user_data.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                        if (related_diys.user_id.equals(user_profile.getUserID())) {
                            String user_name;
                            user_name = user_profile.getF_name() + " " + user_profile.getL_name();
                            holder.diyOwner.setText("by: " + user_name);

                            Log.e("diyOwner", user_name);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        relatedDIYReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                for (DataSnapshot postSnapshot : dataSnapshot.child("diyName").getChildren()) {
//                final DIYnames related_diys = postSnapshot.getValue(DIYnames.class);
//
//                DIYnames dataDIys = new DIYnames();
//
//                String diy_name = related_diys.getDiyName();
//                String diy_price = postSnapshot.child("DIY Price").getValue().toString();
//                Log.e("diy_price", diy_price);
//                Log.e("diy_name", diy_name);
//                    Log.e("postSnapshot", String.valueOf(postSnapshot));
//
//                holder.diyPrice.setText("₱: " + diy_price);
//                holder.diyName.setText(diy_name);
//
//                Glide.with(mContext)
//                        .load(related_diys.getDiyUrl())
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .centerCrop()
//                        .into(holder.itemImage);
//                dataDIys.setDiyName(diy_name);
//
//
//                user_data.addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                        User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
//                        if (related_diys.user_id.equals(user_profile.getUserID())) {
//                            String user_name;
//                            user_name = user_profile.getF_name() + " " + user_profile.getL_name();
//                            holder.diyOwner.setText("by: " + user_name);
//
//                            Log.e("diyOwner", user_name);
//                        }
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
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


//        DIYnames singleItem = itemsList.get(i);

//        float price = selling_price.getSelling_price();
//
//        holder.diyName.setText(singleItem.getDiyName());
//        holder.diyOwner.setText(singleItem.getUser_id());
//
//        holder.diyPrice.setText((int) price);

//        Glide.with(mContext)
//                .load(singleItem.getDiyUrl())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .centerCrop()
//                .into(holder.itemImage);


    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView diyName, diyOwner, diyPrice;
        protected ImageView itemImage;


        public SingleItemRowHolder(View view) {
            super(view);

            this.diyName = (TextView) view.findViewById(R.id.related_diy_name);
            this.diyOwner = (TextView) view.findViewById(R.id.related_diy_owner);
            this.itemImage = (ImageView) view.findViewById(R.id.retaled_diy_img);
            this.diyPrice = (TextView) view.findViewById(R.id.related_diy_price);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DIYnames diys = new DIYnames();
//                    String name = diys.diyName;

                    Intent intent = new Intent(v.getContext(), ViewRelatedDIYS.class);
                    intent.putExtra("name",diys.getDiyName());
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

//                    Bundle extra = new Bundle();
//                    extra.putSerializable("dbmaterials", dbMaterials);
//                    intent.putExtra("dbmaterials", extra);
//                    startActivity(intent);

                    Toast.makeText(v.getContext(), diyName.getText() + "\n" + diyPrice.getText().toString(),
                            Toast.LENGTH_SHORT).show();

                }
            });


        }

    }

}
