package cabiso.daphny.com.g_companion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 10/18/2017.
 */

public class    CommunityFragment extends Fragment{

    private DatabaseReference databaseReference;
    private DatabaseReference userdata_reference;
    private DatabaseReference communityReference;
    private DatabaseReference categoryRef;

    private FirebaseUser mFirebaseUser;
    private String userID;
    private OnListFragmentInteractionListener mListener;

    private ListView lv;
    private RecyclerView recyclerView;
    private RecommendDIYAdapter recommendDIYAdapter;
    private Activity context;
    private int resource;
    private ArrayList<DIYnames> diyList = new ArrayList<>();
    private String username;
    private RecommendDIYAdapter adapter;


    public static final String PAGE_TITLE = "DIY Items";
    private FloatingActionButton fab2, fab3;
    private FloatingActionMenu fam;
    private Animation fab_open, fab_close;
    private ProgressDialog progressDialog;

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
        communityReference = databaseReference.child("diy_by_tags");
        userdata_reference = databaseReference.child("userdata");
        categoryRef = databaseReference.child("diy_by_tags");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community_fragment, container, false);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        recyclerView = (RecyclerView) view.findViewById(R.id.communityList);

        int numberOfColumns = 1;

        recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
        recyclerView.setNestedScrollingEnabled(false);


//        mPromoRecyclerView = (RecyclerView) view.findViewById(R.id.promoRecyclerView);
//        mPromoRecyclerView.setLayoutManager(new LinearLayoutManager(context));
//        mPromoRecyclerView.setItemAnimator(new DefaultItemAnimator());


        fam = (FloatingActionMenu) view.findViewById(R.id.fab_menu);
        fam.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    WindowManager.LayoutParams windowManager = getActivity().getWindow().getAttributes();
                    windowManager.dimAmount = 0.75f;
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
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
                Intent in = new Intent(getActivity(), ImageRecognitionForMaterials.class);
                Snackbar.make(view, "Please wait.......", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                startActivity(in);
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart(){
        super.onStart();
        Toast.makeText(getActivity(), "Hi! Welcome to G-Companion", Toast.LENGTH_SHORT).show();

        final FirebaseRecyclerAdapter<DIYnames, ItemViewHolder> cAdapter =
                new FirebaseRecyclerAdapter<DIYnames, ItemViewHolder>(DIYnames.class,
                        R.layout.recycler_item,ItemViewHolder.class, communityReference) {
                    public int heartCount=0; //pila ka heart * 0.4
                    public int starCount=0; //pila ka bookmark * 0.6

                    public double totalBmLike = ((starCount * 0.6) + (heartCount * 0.4));// i plus ang total percent sa bookmark ug like
                    public int totalDIYs = 0; //pila kabuok diys ang under ana nga tag nya i divide daton nas totaBmLike times 100

                    @Override
                    protected void populateViewHolder(final ItemViewHolder viewHolder, final DIYnames model, final int position) {
                        viewHolder.mNameView.setText(model.diyName);
                        userdata_reference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);

                                if(model.getUser_id().equals(user_profile.getUserID())){
                                    username = user_profile.getF_name()+" "+user_profile.getL_name();
//                                    viewHolder.mOwnerName.setText(username);
                                    Log.e("userID", model.user_id);
                                    Log.e("username", username);
                                }else{
//                                    viewHolder.mOwnerName.setText("NAN");
                                    viewHolder.mOwnerName.setText("NAN");
                                }
                                viewHolder.mOwnerName.setText(username);
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

                        if(model.identity!=null){
                            if(model.identity.equalsIgnoreCase("selling")){
                                viewHolder.mIdentity.setText("Selling");
                                viewHolder.mIdentity.setBackgroundColor(Color.MAGENTA);
                            }else if(model.identity.equalsIgnoreCase("community")){
                                viewHolder.mIdentity.setText("Community");
                                viewHolder.mIdentity.setBackgroundColor(Color.YELLOW);
                            }else if(model.identity.equalsIgnoreCase("on bid!")){
                                viewHolder.mIdentity.setText("On BID!");
                                viewHolder.mIdentity.setBackgroundColor(Color.GREEN);
                            }else if(model.identity.equalsIgnoreCase("ON SALE!")){
                                viewHolder.mIdentity.setText("On SALE!");
                                viewHolder.mIdentity.setBackgroundColor(Color.RED);
                                viewHolder.mIdentityImageView.setImageResource(R.drawable.sale_badge);
                                viewHolder.mIdentityImageView.setRotation((float) 15.0);
                            }else if(model.identity.equalsIgnoreCase("sold")) {
                                viewHolder.mIdentity.setText("SOLD");
                                viewHolder.mIdentity.setBackgroundColor(Color.CYAN);
                            }
                    }

                        //dec.29,2017
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
                        final String key = this.getRef(position).getKey();

                        viewHolder.mStar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(), "Bookmark DIY!" + " " + starCount, Toast.LENGTH_SHORT).show();
                                starCount++;
                                totalBmLike++;

                                final DatabaseReference db = FirebaseDatabase.getInstance().getReference("bookmarks");
                                db.child(userID).push().setValue(model);

                                viewHolder.mStar.setColorFilter(ContextCompat.getColor(getContext(), R.color.star_yello));

                                if (viewHolder.starResult.put("bookmarks", starCount * 0.6) != null) {
                                    reference.child(key).updateChildren(viewHolder.starResult);

                                }
                            }
                        });


                        viewHolder.mHeart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(), "Liked DIY!" + " " + heartCount, Toast.LENGTH_SHORT).show();
                                heartCount++;
                                totalBmLike++;

                                viewHolder.mHeart.setColorFilter(Color.RED);

                                if (viewHolder.likeResult.put("likes", heartCount * 0.4) != null) {
                                    reference.child(key).updateChildren(viewHolder.likeResult);
                                }
                            }
                        });

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
                                    Glide.with(getContext()).load(uri)
                                            .fitCenter().centerCrop()
                                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                            .into(viewHolder.mProductImageView);
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
                                    mListener.onListFragmentInteractionListener(getRef(position));

                                    Log.e("DIY position" + " " + position, String.valueOf(getRef(position)));

                                    Toast.makeText(getActivity(), "You clicked on position!" + " " + position, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };

        recyclerView.setAdapter(cAdapter);
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final TextView mNameView;
        public final TextView mIdentity;
        public final TextView mOwnerName;
        public final ImageView mProductImageView;
        public final ImageView mIdentityImageView;

        public ImageButton mStar;
        public ImageButton mHeart;

        HashMap<String, Object> starResult = new HashMap<>();
        HashMap<String, Object> likeResult = new HashMap<>();

        public ItemViewHolder(View view){
            super(view);
            mView = view;

            mNameView = (TextView) view.findViewById(R.id.item_name);
            mProductImageView = (ImageView) view.findViewById(R.id.diy_item_icon);
            mIdentityImageView = (ImageView) view.findViewById(R.id.outside_imageview);
            mStar = (ImageButton) view.findViewById(R.id.staru);
            mHeart = (ImageButton) view.findViewById(R.id.heartu);
            mIdentity = (TextView) view.findViewById(R.id.item_identity);
            mOwnerName = (TextView) view.findViewById(R.id.item_name_owner);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CommunityFragment.OnListFragmentInteractionListener) {
            mListener = (CommunityFragment.OnListFragmentInteractionListener) context;
        }
        else {
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
        void onListFragmentInteractionListener(DatabaseReference ref);
    }

}
