package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
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

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 6/27/2018.
 */

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> {

    private ArrayList<DIYnames> itemsList;
    private Context mContext;
    private DatabaseReference relatedDIYReference;

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
    public void onBindViewHolder(final SingleItemRowHolder holder, int i) {
        relatedDIYReference = FirebaseDatabase.getInstance().getReference().child("same_diy_product");

        relatedDIYReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String diy_price = dataSnapshot.child("DIY Price").getValue().toString();
                Log.e("diy_price", diy_price);

                holder.diyPrice.setText(diy_price);

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


        DIYnames singleItem = itemsList.get(i);

//        float price = selling_price.getSelling_price();

        holder.diyName.setText(singleItem.getDiyName());
        holder.diyOwner.setText(singleItem.getUser_id());

//        holder.diyPrice.setText((int) price);

        Glide.with(mContext)
                .load(singleItem.getDiyUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.itemImage);
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


                    Toast.makeText(v.getContext(), diyName.getText() + "\n" + diyPrice.getText().toString(),
                            Toast.LENGTH_SHORT).show();

                }
            });


        }

    }

}
