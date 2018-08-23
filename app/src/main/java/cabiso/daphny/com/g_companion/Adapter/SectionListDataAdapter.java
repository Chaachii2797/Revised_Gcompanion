package cabiso.daphny.com.g_companion.Adapter;

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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.ViewRelatedDIYS;

/**
 * Created by Lenovo on 6/27/2018.
 */

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder> {

    private Context mContext;
    private DatabaseReference diy_owner, diyByTags, dbRef;
    private ArrayList<SellingDIY> ePrice = new ArrayList<>();
    private ArrayList<User_Profile> eOwner = new ArrayList<>();
    private ArrayList<DIYnames> ePics = new ArrayList<>();
    private ArrayList<DIYnames> itemsList;
    private FirebaseUser mFirebaseUser;


    public SectionListDataAdapter(Context context, ArrayList<DIYnames> itemsList, ArrayList<User_Profile> eOwner) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.eOwner = eOwner;
        this.eOwner = eOwner;
    }


    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_diys, parent, false);
        return new SingleItemRowHolder(v);

    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, final int position) {
        diy_owner = FirebaseDatabase.getInstance().getReference().child("userdata");
        diyByTags = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        dbRef = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");

        holder.diyName.setText(this.itemsList.get(position).getDiyName());

        Glide.with(mContext)
                        .load(itemsList.get(position).getDiyUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(holder.itemImage);



    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }



    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView diyOwner, diyName;
        protected ImageView itemImage;


        public SingleItemRowHolder(final View view) {
            super(view);

            diyName = (TextView) view.findViewById(R.id.related_diy_name);
            itemImage = (ImageView) view.findViewById(R.id.retaled_diy_img);

//            view.setOnClickListener(this);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DIYnames diys = new DIYnames();
                    DIYnames relDiys = (DIYnames) itemsList.get(getAdapterPosition());


                    //send data to ViewRelatedDIYS using intent
                    Intent intent = new Intent(v.getContext(), ViewRelatedDIYS.class);
                    intent.putExtra("Nname", relDiys.diyName);
                    Log.e("Nname", relDiys.diyName);

//                    intent.putExtra("Pprice", sellPrice.getSelling_price());
//                    Log.e("Pprice", String.valueOf(sellPrice.getSelling_price()));

//                    v.buildDrawingCache();
//                    Bitmap image = v.getDrawingCache();
//                    Bundle extras = new Bundle();
//                    extras.putParcelable("imagebitmap", image);
//
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
//                    intent.putExtra("image", byteArray);
                    v.getContext().startActivity(intent);

                    Toast.makeText(v.getContext(), diyName.getText().toString()
                            + "\n" + itemImage.getDrawable(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
//        @Override
//        public void onClick(View v) {
//            Log.e("POS_CLICKED: ",""+getAdapterPosition());
//
//            int position = getAdapterPosition();
//            Toast.makeText(v.getContext(),Integer.toString(position), Toast.LENGTH_SHORT).show();
//
//
//            DIYnames rel_diy = itemsList.get(position);
//            String name = (String) rel_diy.diyName;
//
//            Log.e("name: ",rel_diy.diyName);
//            Intent intent = new Intent(v.getContext(), ViewRelatedDIYS.class);
//            intent.putExtra("Nname",name);
//        }
    }

}
