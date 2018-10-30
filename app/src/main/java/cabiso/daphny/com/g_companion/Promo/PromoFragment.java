package cabiso.daphny.com.g_companion.Promo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.MainDIYS.ViewMorePromoDIYS;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

public class PromoFragment extends Fragment {
    private FirebaseUser mFirebaseUser;
    private String userID;
    private String expireDate;
    private String promo_name;
    private DatabaseReference promoReference;
    private Activity context;
    private OnListFragmentInteractionListener promo_Listener;
    private ArrayList<PromoModel> promoList = new ArrayList<>();
    private ArrayList<DIYnames> diYnamesList = new ArrayList<>(); // for all free items
    private RecyclerView promo_recycler;
    private PromoCardListAdapter mPromoRecyclerViewAdapter;
    private Button moreDiysPromoBtn;
    PromoModel promoModel;

    public PromoFragment() {
        // Required empty public constructor
    }

    public static PromoFragment newInstance() {
        PromoFragment fragment = new PromoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return new PromoFragment();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();
        promoReference = FirebaseDatabase.getInstance().getReference().child("promo_sale");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.promo_sale_fragment, container, false);

        moreDiysPromoBtn = (Button) view.findViewById(R.id.viewMorePromoDiysBtn);

        moreDiysPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Clicked View More Promo DIYS", Toast.LENGTH_SHORT).show();
                Intent promoIntent = new Intent(getActivity(), ViewMorePromoDIYS.class);
                startActivity(promoIntent);
            }
        });

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();
        promo_recycler = (RecyclerView) view.findViewById(R.id.promoList);
        promo_recycler.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.HORIZONTAL,false));
        promo_recycler.setNestedScrollingEnabled(true);

        mPromoRecyclerViewAdapter = new PromoCardListAdapter(view.getContext(),promoList,diYnamesList);
        promo_recycler.setAdapter(mPromoRecyclerViewAdapter);
        promoReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                promoModel = dataSnapshot.getValue(PromoModel.class);


//                promoModel.setPromo_diyName(dataSnapshot.child("promo_diyName").getValue(String.class));
//                promoModel.setPromo_image(dataSnapshot.child("promo_image").getValue(String.class));
//                promoModel.setPromo_id(dataSnapshot.getKey());
//                promoModel.setPromo_details(dataSnapshot.child("promo_promo_details").getValue(String.class));

                promoList.add(promoModel);
                mPromoRecyclerViewAdapter.notifyDataSetChanged();
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(getActivity(), "Hi! Welcome to G-Companion-Promos", Toast.LENGTH_SHORT).show();

    }

    public static class PromoItemHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final TextView mPromoNameView;
        public final TextView mExpiryView;
        public final ImageView mPromoBuyImageView;

        public PromoItemHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mPromoNameView = (TextView) itemView.findViewById(R.id.promo_item_name);
            mExpiryView = (TextView) itemView.findViewById(R.id.promo_item_name_owner);
            mPromoBuyImageView = (ImageView) itemView.findViewById(R.id.promo_main_item_image);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof PromoFragment.OnListFragmentInteractionListener){
            promo_Listener = (PromoFragment.OnListFragmentInteractionListener) context;
        }else{
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        promo_Listener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteractionListener(DatabaseReference ref);
    }
}
