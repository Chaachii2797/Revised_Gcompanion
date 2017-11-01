package cabiso.daphny.com.g_companion;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by Lenovo on 8/22/2017.
 */

public class MarketPlaceFragment extends Fragment{

    private DatabaseReference mDatabaseReference;
    private DatabaseReference marketplaceReference;
    private RecyclerView recyclerView;
    private OnListFragmentInteractionListener mListener;
    File productImageTempFile = null;
    private FloatingActionButton fab1;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    public static final String TITLE = "DIY Market";

    public MarketPlaceFragment() {
    }


    public static MarketPlaceFragment newInstance() {
        MarketPlaceFragment fragment = new MarketPlaceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        marketplaceReference = mDatabaseReference.child("marketplace");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_page, container, false);

        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.marketList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        fab1 = (FloatingActionButton) view.findViewById(R.id.market_fab);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), AddProductActivity.class);
                startActivity(in);
            }
        });

        return view;


    }

    @Override
    public void onStart(){
        super.onStart();
        Toast.makeText(getActivity(), "yow! yow!", Toast.LENGTH_SHORT).show();

        FirebaseRecyclerAdapter<ProductInfo, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductInfo, ItemViewHolder>(ProductInfo.class,
                R.layout.recycler_item,ItemViewHolder.class, marketplaceReference ) {

            @Override
            protected void populateViewHolder(final ItemViewHolder viewHolder, ProductInfo model, final int position) {
                viewHolder.mNameView.setText(model.title);
                viewHolder.mDescriptionView.setText(model.desc);
                viewHolder.mPriceView.setText(model.price);
                try{
                    String productPictureURL = model.productPictureURLs.get(0);
                    Log.d("ppURL", productPictureURL);
                    StorageReference pictureReference = FirebaseStorage.getInstance().getReferenceFromUrl(productPictureURL);
                    pictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            // Pass it to Picasso to download, show in ImageView and caching
                            Log.d("Product Picture URI is", uri.toString());
                            Glide.with(getContext()).load(uri).into(viewHolder.mProductImageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });


                }
                catch(Exception e){
                    Log.d("Exception", "Failed to fetch product Picture");
                }


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.
                            mListener.onListFragmentInteraction(getRef(position));
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final TextView mNameView;
        public final TextView mDescriptionView;
        public final TextView mPriceView;
        public final ImageView mProductImageView;

        public ItemViewHolder(View view){
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.item_name);
            mDescriptionView = (TextView) view.findViewById(R.id.item_description);
            mPriceView = (TextView) view.findViewById(R.id.item_price);
            mProductImageView = (ImageView) view.findViewById(R.id.diy_item_icon);

        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(DatabaseReference ref);
    }
}
