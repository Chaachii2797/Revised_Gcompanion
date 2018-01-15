package cabiso.daphny.com.g_companion;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionMenu;
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
    private DatabaseReference communityReference;
    private DatabaseReference marketplaceReference;
    private GridView gridview;
    private RecyclerView recyclerView;
    private OnListFragmentInteractionListener mListener;
    File productImageTempFile = null;
    //private FloatingActionButton fab1;
    private com.github.clans.fab.FloatingActionButton fab1, fab2, fab3;
    private FloatingActionMenu fam;
    private Animation fab_open, fab_close;


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
//        gridview = (GridView) view.findViewById(R.id.gridview);
//        gridview.setAdapter(new DIYHolder(this));
        int numberOfColumns = 2;

        recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

        fam = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                } else {
                }
            }
        });
        fab2 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.community_fab);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), CaptureDIY.class);
                startActivity(in);
            }
        });

        fab3 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.image_recog_fab);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), ImageRecognitionTags.class);
                Snackbar.make(view, "Please wait.......", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                startActivity(in);
            }
        });


        fab1 = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.market_fab);
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
        Toast.makeText(getActivity(), "Hi! Welcome!", Toast.LENGTH_SHORT).show();

        FirebaseRecyclerAdapter<ProductInfo, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<ProductInfo, ItemViewHolder>(ProductInfo.class,
                R.layout.recycler_item,ItemViewHolder.class, marketplaceReference) {

            @Override
            protected void populateViewHolder(final ItemViewHolder viewHolder, ProductInfo model, final int position) {
                viewHolder.mNameView.setText(model.title);
                //viewHolder.mDescriptionView.setText(model.desc);
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
       // public final TextView mDescriptionView;
        public final ImageView mProductImageView;

        public ItemViewHolder(View view){
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.item_name);
           // mDescriptionView = (TextView) view.findViewById(R.id.item_description);
            mProductImageView = (ImageView) view.findViewById(R.id.diy_item_icons);

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
