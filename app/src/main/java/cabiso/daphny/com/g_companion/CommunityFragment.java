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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
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
import java.util.HashMap;

import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 10/18/2017.
 */

public class CommunityFragment extends Fragment{

    private DatabaseReference databaseReference;

    private DatabaseReference communityReference;
    private DatabaseReference marketplaceReference;

    private FirebaseDatabase database;
    private StorageReference storageReference;
    private FirebaseUser mFirebaseUser;
    private String userID;
    private OnListFragmentInteractionListener mListener;
    private OnListFragmentInteraction mlistener;



    private ListView lv;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;
    private RecommendDIYAdapter recommendDIYAdapter;
    private Activity context;
    private int resource;
    private ArrayList<DIYnames> diyList = new ArrayList<>();

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
        marketplaceReference = databaseReference.child("Sell DIY");

//        Collections.sort(diyList);
//        Collections.reverse(diyList);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.community_fragment, container, false);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();



        final Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.communityList);

        recyclerView1 = (RecyclerView) view.findViewById(R.id.marketList);

        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
        recyclerView1.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView1.setNestedScrollingEnabled(false);

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
        Toast.makeText(getActivity(), "Hi! Welcome!", Toast.LENGTH_SHORT).show();
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
                     //   viewHolder.mCategory.setText(model.tag);

                        //dec.29,2017
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
//                        reference.addValueEventListener(new ValueEventListener() {
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                String sellItem = dataSnapshot.child("DIY Price").getValue().toString();
//                                viewHolder.mPriceView.setText(sellItem);
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });


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
                                    mListener.onListFragmentInteractionListener(getRef(position));

                                    Toast.makeText(getActivity(), "You clicked on position!" + " " + position, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };

        cAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.smoothScrollToPosition(cAdapter.getItemCount());
            }
        });

        recyclerView.setAdapter(cAdapter);

        //selling DIY
        Toast.makeText(getActivity(), "Welcome!", Toast.LENGTH_SHORT).show();
        final FirebaseRecyclerAdapter<DIYSell, ItemHolder> mAdapter =
                new FirebaseRecyclerAdapter<DIYSell, ItemHolder>(DIYSell.class,
                        R.layout.diy_item,ItemHolder.class, marketplaceReference) {
                    public int heartCount=0; //pila ka heart * 0.4
                    public int starCount=0; //pila ka bookmark * 0.6

                    public double totalBmLike = ((starCount * 0.6) + (heartCount * 0.4));// i plus ang total percent sa bookmark ug like
                    public int totalDIYs = 0; //pila kabuok diys ang under ana nga tag nya i divide daton nas totaBmLike times 100
                    @Override
                    protected void populateViewHolder(final ItemHolder viewHolder, final DIYSell model, final int position) {
                        viewHolder.mNameViews.setText(model.diyName);
                        //   viewHolder.mCategory.setText(model.tag);

                        //dec.29,2017
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Sell DIY");
//                        reference.addValueEventListener(new ValueEventListener() {
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                String sellItem = dataSnapshot.child("DIY Price").getValue().toString();
//                                viewHolder.mPriceView.setText(sellItem);
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });


                        final String key = this.getRef(position).getKey();

                        viewHolder.mStars.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(), "Bookmark DIY!" + " " + starCount, Toast.LENGTH_SHORT).show();
                                starCount++;
                                totalBmLike++;

                                final DatabaseReference db = FirebaseDatabase.getInstance().getReference("bookmarks");
                                db.child(userID).push().setValue(model);

                                viewHolder.mStars.setColorFilter(ContextCompat.getColor(getContext(), R.color.star_yello));

                                if (viewHolder.starResult.put("bookmarks", starCount * 0.6) != null) {
                                    reference.child(key).updateChildren(viewHolder.starResult);

                                }
                            }
                        });


                        viewHolder.mHearts.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getContext(), "Liked DIY!" + " " + heartCount, Toast.LENGTH_SHORT).show();
                                heartCount++;
                                totalBmLike++;

                                viewHolder.mHearts.setColorFilter(Color.RED);

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
                                    Glide.with(getContext()).load(uri).into(viewHolder.mProductImageViews);
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
                        viewHolder.mViews.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != mlistener) {
                                    // Notify the active callbacks interface (the activity, if the
                                    // fragment is attached to one) that an item has been selected.
                                    mlistener.onListFragmentInteraction(getRef(position));

                                    Toast.makeText(getActivity(), "You clicked on position!" + " " + position, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                };

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView1.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });


        recyclerView1.setAdapter(mAdapter);

        //adapter in  main page
//        if(mListener != null){
//            Toast.makeText(getActivity(), "Yowwwwaaaaaaa!", Toast.LENGTH_SHORT).show();
//            recyclerView.setAdapter(cAdapter);
//        }
//        if(mlistener != null){
//            Toast.makeText(getActivity(), "Yowwwwiiiiiiiii!", Toast.LENGTH_SHORT).show();
//            recyclerView1.setAdapter(mAdapter);
//        }
//        else{
//            Toast.makeText(getActivity(), "SHIT!", Toast.LENGTH_SHORT).show();
//        }
    }

    public interface OnListFragmentInteraction {
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final TextView mNameView;
        public final ImageView mProductImageView;

        public ImageButton mStar;
        public ImageButton mHeart;

        HashMap<String, Object> starResult = new HashMap<>();
        HashMap<String, Object> likeResult = new HashMap<>();

        public ItemViewHolder(View view){
            super(view);
            mView = view;

            mNameView = (TextView) view.findViewById(R.id.item_name);
            mProductImageView = (ImageView) view.findViewById(R.id.diy_item_icon);
            mStar = (ImageButton) view.findViewById(R.id.staru);
            mHeart = (ImageButton) view.findViewById(R.id.heartu);
        }
    }

    public static class ItemHolder extends RecyclerView.ViewHolder{

        public final View mViews;
        public final TextView mNameViews;
        public final ImageView mProductImageViews;

        public ImageButton mStars;
        public ImageButton mHearts;

        HashMap<String, Object> starResult = new HashMap<>();
        HashMap<String, Object> likeResult = new HashMap<>();

        public ItemHolder(View view){
            super(view);
            mViews = view;

            mNameViews = (TextView) view.findViewById(R.id.item_names);
            mProductImageViews = (ImageView) view.findViewById(R.id.diy_item_icons);
            mStars = (ImageButton) view.findViewById(R.id.star);
            mHearts = (ImageButton) view.findViewById(R.id.heart);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CommunityFragment.OnListFragmentInteractionListener) {
            mListener = (CommunityFragment.OnListFragmentInteractionListener) context;
        }
        if(context instanceof CommunityFragment.OnListFragmentInteraction) {
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
        mlistener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteractionListener(DatabaseReference ref);

        void onListFragmentInteraction(DatabaseReference ref);
    }

    public interface OnListFragmentInteraction {
        void onListFragmentInteraction(DatabaseReference ref);

    }

}
