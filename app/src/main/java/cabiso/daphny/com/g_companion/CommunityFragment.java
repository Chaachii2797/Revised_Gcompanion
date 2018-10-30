package cabiso.daphny.com.g_companion;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 10/18/2017.
 */

public class CommunityFragment extends Fragment{

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

//        recyclerView = (RecyclerView) view.findViewById(R.id.communityList);
//
//        int numberOfColumns = 1;
//
//        recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
//        recyclerView.setNestedScrollingEnabled(false);


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
