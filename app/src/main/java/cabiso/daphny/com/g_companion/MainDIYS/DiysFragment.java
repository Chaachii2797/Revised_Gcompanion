package cabiso.daphny.com.g_companion.MainDIYS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 8/31/2018.
 */

public class DiysFragment extends Fragment {

    private FirebaseUser mFirebaseUser;
    private String userID;
    private DatabaseReference diysReference, categoryReference;

    private Activity context;
    private OnListFragmentInteractionListener diys_Listener;
    private String username;

    private ArrayList<DIYnames> diysSellingList = new ArrayList<>();
    private ArrayList<DIYnames> diysOnBidList = new ArrayList<>();
    private ArrayList<DIYnames> diysComunityList = new ArrayList<>();
    private ArrayList<DIYnames> diysNewAddedList = new ArrayList<>();

    private RecyclerView diys_recycler_selling, diy_recycler_bidCategory, diy_recycler_community, diy_recycler_new_added;

    private DIYSCardListAdapter mDIYSRecyclerViewAdapter;
    private DIYSCardListAdapter mDIYSOnBidAdapter;
    private DIYSCardListAdapter mDIYSCommunityAdapter;
    private DIYSCardListAdapter mDIYSNewAddedAdapter;
    private ArrayAdapter<String> setListAdapter;
    private Button moreDiysBtn, moreBtnBid, moreBtnCommunity, moreBtnNewAdded;

    public DiysFragment() {
        // Required empty public constructor
    }

    public static DiysFragment newInstance() {

        DiysFragment fragment = new DiysFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return new DiysFragment();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        diysReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        categoryReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diys_fragment, container, false);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        //view more button
        moreDiysBtn = (Button) view.findViewById(R.id.viewMoreDiysBtn);
        moreBtnBid = (Button) view.findViewById(R.id.viewMoreDiysBtnBid);
        moreBtnCommunity = (Button) view.findViewById(R.id.viewMoreDiysBtnCommunity);
//        moreBtnNewAdded = (Button) view.findViewById(R.id.viewMoreDiysBtnNew);

        diys_recycler_selling = (RecyclerView) view.findViewById(R.id.diyListItems);
        diy_recycler_bidCategory = (RecyclerView) view.findViewById(R.id.diyListItemsBid);
        diy_recycler_community = (RecyclerView) view.findViewById(R.id.diyListItemsCommunity);
//        diy_recycler_new_added = (RecyclerView) view.findViewById(R.id.diyListItemsNewAdd);

        //selling recycler view
        diys_recycler_selling.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        diys_recycler_selling.setNestedScrollingEnabled(true);

        //on bid recycler view
        diy_recycler_bidCategory.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        diy_recycler_bidCategory.setNestedScrollingEnabled(true);

        //community recycler view
        diy_recycler_community.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        diy_recycler_community.setNestedScrollingEnabled(true);

        //tire catgory recycler view
//        diy_recycler_new_added.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
//        diy_recycler_new_added.setNestedScrollingEnabled(true);


        SimpleDateFormat curFormater = new SimpleDateFormat("EEEE, MMMM dd, yyyy");
        final GregorianCalendar date = new GregorianCalendar();
        final String[] dateStringArray = new String[7];

        for (int day = 0; day < 7; day++) {
            dateStringArray[day] = curFormater.format(date.getTime());
            date.roll(Calendar.DAY_OF_YEAR, true);
            Log.e("dateRoll", String.valueOf(date));
        }

        //DIYSCardListAdapter
        mDIYSRecyclerViewAdapter = new DIYSCardListAdapter(view.getContext(), diysSellingList);
        mDIYSOnBidAdapter = new DIYSCardListAdapter(view.getContext(), diysOnBidList);
        mDIYSCommunityAdapter = new DIYSCardListAdapter(view.getContext(), diysComunityList);
//        mDIYSNewAddedAdapter = new DIYSCardListAdapter(view.getContext(), diysNewAddedList);


        moreDiysBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Clicked View More Selling", Toast.LENGTH_SHORT).show();
                Intent sellIntent = new Intent(getActivity(), ViewMoreDiysSellingActivity.class);
                startActivity(sellIntent);
            }
        });

        moreBtnBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Clicked View More Bidding", Toast.LENGTH_SHORT).show();
                Intent bidIntent = new Intent(getActivity(), ViewMoreDiysBidActivity.class);
                startActivity(bidIntent);
            }
        });

        moreBtnCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Clicked View More Community", Toast.LENGTH_SHORT).show();
                Intent commIntent = new Intent(getActivity(), ViewMoreDiysCommunityActivity.class);
                startActivity(commIntent);
            }
        });

//        moreBtnNewAdded.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Clicked View More New Added", Toast.LENGTH_SHORT).show();
//                Intent newIntent = new Intent(getActivity(), ViewMoreNewAddedDIYSActivity.class);
//                startActivity(newIntent);
//            }
//        });

        Toast.makeText(getActivity(), "Hi! Welcome to G-Companion version 2.0", Toast.LENGTH_SHORT).show();

        diys_recycler_selling.setAdapter(mDIYSRecyclerViewAdapter);
        diy_recycler_bidCategory.setAdapter(mDIYSOnBidAdapter);
        diy_recycler_community.setAdapter(mDIYSCommunityAdapter);
//        diy_recycler_new_added.setAdapter(mDIYSNewAddedAdapter);


        diysReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final DIYnames diysModel = dataSnapshot.getValue(DIYnames.class);

                if (diysModel.getIdentity().equalsIgnoreCase("selling")) {
                    diysSellingList.add(diysModel);
                    Log.e("sell_Diys", String.valueOf(diysModel.getDiyName()));

                    mDIYSRecyclerViewAdapter.notifyDataSetChanged();
                } else if (diysModel.getIdentity().equalsIgnoreCase("on bid!")) {
                    diysOnBidList.add(diysModel);
                    Log.e("bid_Diyss", String.valueOf(diysModel.getDiyName()));

                    mDIYSOnBidAdapter.notifyDataSetChanged();
                } else if (diysModel.getIdentity().equalsIgnoreCase("community")) {
                    diysComunityList.add(diysModel);
                    Log.e("com_Diysss", String.valueOf(diysModel.getDiyName()));

                    mDIYSCommunityAdapter.notifyDataSetChanged();
                }


//                Object diyDate = dataSnapshot.child("dateAdded").getValue();
//                Log.e("diyDate", diyDate + " = " + diysModel.getDiyName());
//
//                if (diyDate.equals("2018-09-02")) {
//                    diysNewAddedList.add(diysModel);
//                    Log.e("date_diys", String.valueOf(diysModel.getDiyName()));
//                    mDIYSNewAddedAdapter.notifyDataSetChanged();
//
//
//                }


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


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getActivity(), "Hi! Welcome to G-Companion version 2.0", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onResume() {
        super.onResume();
    }


//    public static class DIYSItemHolder extends RecyclerView.ViewHolder{
//
//        public final View mView;
//        public final TextView mDIYNameView;
//        public final TextView mOwnerView;
//        public final TextView mPriceView;
//        public final ImageView mDIYImageView;
//        public ImageButton mStar;
//        public ImageButton mHeart;
//        HashMap<String, Object> starResult = new HashMap<>();
//        HashMap<String, Object> likeResult = new HashMap<>();
//
//        public DIYSItemHolder(View itemView) {
//            super(itemView);
//
//            mView = itemView;
//            mDIYNameView = (TextView) itemView.findViewById(R.id.diy_item_main_name);
//            mOwnerView = (TextView) itemView.findViewById(R.id.diy_item_main_owner);
//            mPriceView = (TextView) itemView.findViewById(R.id.diy_item_main_price);
//            mDIYImageView = (ImageView) itemView.findViewById(R.id.diy_main_item_image);
//            mStar = (ImageButton) itemView.findViewById(R.id.staru);
//            mHeart = (ImageButton) itemView.findViewById(R.id.heartu);
//
//        }
//
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DiysFragment.OnListFragmentInteractionListener) {
            diys_Listener = (DiysFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        diys_Listener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteractionListener(DatabaseReference ref);
    }
}
