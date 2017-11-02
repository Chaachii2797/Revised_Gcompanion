package cabiso.daphny.com.g_companion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 10/18/2017.
 */

public class CommunityFragment extends Fragment{


    private DatabaseReference databaseReference;
    private DatabaseReference communityReference;
    private FirebaseDatabase database;
    private StorageReference storageReference;
    private FirebaseUser mFirebaseUser;
    private String userID;
    private OnListFragmentInteractionListener mlistener;

    private ListView lv;
    private RecyclerView recyclerView;
    private RecommendDIYAdapter recommendDIYAdapter;
    private Activity context;
    private int resource;
    private ArrayList<DIYnames> listDIY;


    public static final String TITLE = " DIY Community";
    private FloatingActionButton fab2, fab3;
    private FloatingActionMenu fam;
    private Animation fab_open, fab_close;



    public CommunityFragment(){

    }

    public static CommunityFragment newInstance() {

        CommunityFragment fragment =  new CommunityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return new CommunityFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        communityReference = databaseReference.child("diy_by_tags").child(userID).child("people");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community_fragment, container, false);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.communityList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));



        fam = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                } else {
                }
            }
        });
        fab2 = (FloatingActionButton) view.findViewById(R.id.community_fab);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), CaptureDIY.class);
                startActivity(in);
            }
        });

        fab3 = (FloatingActionButton) view.findViewById(R.id.image_recog_fab);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), ImageRecognitionTags.class);
                Snackbar.make(view, "Please wait.......", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(in);
            }
        });


        return view;
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        recyclerView = (RecyclerView) view.findViewById(R.id.communityList);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//
//    }



    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onStart(){
        super.onStart();
        Toast.makeText(getActivity(), "yow!", Toast.LENGTH_SHORT).show();

        FirebaseRecyclerAdapter<DIYnames, ItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<DIYnames, ItemViewHolder>(DIYnames.class,
                        R.layout.recommend_ui,ItemViewHolder.class, communityReference ) {

                    @Override
                    protected void populateViewHolder(final ItemViewHolder viewHolder, DIYnames model, final int position) {
                        viewHolder.mNameView.setText(model.diyName);


                        try{
                            String productPictureURL = model.diyUrl;
                            Log.d("ppURL", productPictureURL);
                            StorageReference pictureReference = FirebaseStorage.getInstance().getReferenceFromUrl(productPictureURL);
                            pictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Got the download URL for 'users/me/profile.png'
                                    // Pass it to Picasso to download, show in ImageView and caching
                                    Log.d("Product Picture URI is", uri.toString());
                                    Glide.with(getContext()).load(uri).into(viewHolder.mProductImageView);


                                    //Picasso.with(getContext()).load(uri).resize(75, 75).into(viewHolder.mProductImageView);
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
                                if (null != mlistener) {
                                    // Notify the active callbacks interface (the activity, if the
                                    // fragment is attached to one) that an item has been selected.
                                    mlistener.onListFragmentInteractionListener(getRef(position));

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
        public final ImageView mProductImageView;

        public ItemViewHolder(View view){
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.get_diyName);
            mProductImageView = (ImageView) view.findViewById(R.id.diy_item_icon);

        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CommunityFragment.OnListFragmentInteractionListener) {
            mlistener = (CommunityFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mlistener = null;
    }


    public interface OnListFragmentInteractionListener {
        void onListFragmentInteractionListener(DatabaseReference ref);

    }
}
