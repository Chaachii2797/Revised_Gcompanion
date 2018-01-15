package cabiso.daphny.com.g_companion.Recommend;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

import cabiso.daphny.com.g_companion.MarketPlaceFragment;
import cabiso.daphny.com.g_companion.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_BottleRecommend extends Fragment {

    private DatabaseReference mDatabaseReference;
    private DatabaseReference diyMethod;
    private RecyclerView recyclerView;
    private OnListFragmentInteractionListener mListener;
    File productImageTempFile = null;

    public Fragment_BottleRecommend() {
        // Required empty public constructor
    }

    public static Fragment_BottleRecommend newInstance() {
        Fragment_BottleRecommend fragment = new Fragment_BottleRecommend();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        diyMethod = mDatabaseReference.child("DIY_Method").child("category").child("bottle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_bar_main, container, false);
        // Inflate the layout for this fragment
        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<CategoryInfo, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<CategoryInfo, ItemViewHolder>
                        (CategoryInfo.class,R.layout.recycler_item,ItemViewHolder.class,diyMethod) {
            @Override
            protected void populateViewHolder(final ItemViewHolder viewHolder, CategoryInfo model, final int position) {
                Log.d("Firebase download", model.name);
                viewHolder.mNameView.setText(model.name);
                //viewHolder.mMaterialView.setText(model.material);
                //viewHolder.mProcedureView.setText(model.procedure);
                try {
                    String categoryPictureURL = model.categoryPictureURLs.get(0);
                    Log.d("ppURL", categoryPictureURL);
                    StorageReference pictureReference = FirebaseStorage.getInstance().getReferenceFromUrl(categoryPictureURL);
                    pictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("Product Picture URI is", uri.toString());
                            Picasso.with(getContext()).load(uri).resize(75, 75).into(viewHolder.mcategoryImageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

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

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final TextView mNameView;
        //public final TextView mMaterialView;
        //public final TextView mProcedureView;
        public final ImageView mcategoryImageView;

        public ItemViewHolder(View view){
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.item_name);
           // mMaterialView = (TextView) view.findViewById(R.id.item_description);
            mcategoryImageView = (ImageView) view.findViewById(R.id.diy_item_icons);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MarketPlaceFragment.OnListFragmentInteractionListener) {
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
