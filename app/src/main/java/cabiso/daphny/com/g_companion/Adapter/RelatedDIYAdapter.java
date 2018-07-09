package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 6/26/2018.
 */

public class RelatedDIYAdapter extends RecyclerView.Adapter<RelatedDIYAdapter.ItemRowHolder> {

    private Context context;
    private int resource;
    private ArrayList<DIYnames> relatedListDIY;
    private int count=0;
    private ListView lv;
    private FirebaseUser mFirebaseUser;
    private String userID;
    private DatabaseReference relatedDIYReference;

//    public RelatedDIYAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<DIYnames> objects) {
//        super(context, resource, objects);
//        this.context = context;
//        this.resource = resource;
//        relatedListDIY = objects;
//    }

    public RelatedDIYAdapter(Context context, ArrayList<DIYnames> dataList) {
        this.relatedListDIY = dataList;
        this.context = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.related_diys, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder itemRowHolder, int i) {
        relatedDIYReference = FirebaseDatabase.getInstance().getReference().child("same_diy_product");

        relatedDIYReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String diy_price = dataSnapshot.child("DIY Price").getValue().toString();
                Log.e("diy_price", diy_price);

                itemRowHolder.relatedDIYprice.setText(diy_price);

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


        DIYnames singleItem = relatedListDIY.get(i);

        itemRowHolder.relatedDIYname.setText(singleItem.getDiyName());
        itemRowHolder.relatedDIYowner.setText(singleItem.getUser_id());
        //itemRowHolder.relatedDIYprice.setText(price);

         Glide.with(context)
                .load(singleItem.getDiyUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(itemRowHolder.relatedDIYimg);

    }

    @Override
    public int getItemCount() {
        return (null != relatedListDIY ? relatedListDIY.size() : 0);
    }


    public class ItemRowHolder extends RecyclerView.ViewHolder {

        TextView relatedDIYname,relatedDIYowner, relatedDIYprice;
        ImageView relatedDIYimg;
        protected RecyclerView recycler_view_list;


        public ItemRowHolder(View view) {
            super(view);

            this.relatedDIYimg = (ImageView) view.findViewById(R.id.retaled_diy_img);
            this.relatedDIYname = (TextView) view.findViewById(R.id.related_diy_name);
            this.relatedDIYowner = (TextView) view.findViewById(R.id.related_diy_owner);
            this.relatedDIYprice = (TextView) view.findViewById(R.id.related_diy_price);


        }

    }

//    @NonNull
//
//    @Override
//    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
//        final LayoutInflater inflater = context.getLayoutInflater();
//
//        View v = inflater.inflate(resource, null);
//        ImageView relatedDIYimg = (ImageView) v.findViewById(R.id.retaled_diy_img);
//        TextView relatedDIYname = (TextView) v.findViewById(R.id.related_diy_name);
//        TextView relatedDIYowner = (TextView) v.findViewById(R.id.related_diy_owner);
//        TextView relatedDIYprice = (TextView) v.findViewById(R.id.related_diy_price);
//
//        relatedDIYname.setText(relatedListDIY.get(position).getDiyName());
//        relatedDIYowner.setText(relatedListDIY.get(position).getUser_id());
//
//        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        userID = mFirebaseUser.getUid();
//
//        Glide.with(context).load(relatedListDIY.get(position).getDiyUrl()).into(relatedDIYimg);
//
//        return v;
//    }

}
