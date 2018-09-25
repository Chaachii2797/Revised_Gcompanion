package cabiso.daphny.com.g_companion;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cabiso.daphny.com.g_companion.Adapter.BiddersAdapter;
import cabiso.daphny.com.g_companion.Adapter.RecyclerViewDataAdapter;
import cabiso.daphny.com.g_companion.EditData.EditDIYDetailsActivity;
import cabiso.daphny.com.g_companion.InstantMessaging.ui.activities.ChatActivity;
import cabiso.daphny.com.g_companion.InstantMessaging.ui.activities.ChatSplashActivity;
import cabiso.daphny.com.g_companion.Model.Bidders;
import cabiso.daphny.com.g_companion.Model.CreatePromo;
import cabiso.daphny.com.g_companion.Model.DIYBidding;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SectionDataModel;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.Promo.PriceDiscountActivity;
import cabiso.daphny.com.g_companion.Promo.PromoActivity;
import cabiso.daphny.com.g_companion.notifications.PushNotification;

/**
 * Created by Lenovo on 7/5/2018.
 */

public class ViewRelatedDIYS extends AppCompatActivity {


    private ViewPager imgviewpager;
    private ViewImagesSearchPagerAdapter viewImagesSearchPagerAdapter;
    private TextView sview_diyName, sview_diyMaterials, sview_diyProcedures, sell_details, diyBy, diyOwner,
            diyQty, promoPrice, sellers_address, sellers_contact_no, textViewpromo, textViewPHP, tv_bid_xpire, tv_bid_price,
            tv_bid_comment, bidding_winner_amount, bidding_winner_name, tv_bidders;
    private CardView cardview2, cardview3, cardview4, cardView5, cardView6, cardView7, cardView8, cardView9;
    private String user_name;
    private String userID;
    private ImageView outside_view;
    private Button buyBtn, contactSellerBtn, bidMyItemBtn, buyerBidBtn, createMyPromoBtn, editDIYBtn, btn_bidders;
    private DatabaseReference relatedDiyReference, userData, pendingReference, loggedInName, relatedRelatedReference,
            biddersReference, biddingRef, pending_reference, user_reference;
    private FirebaseUser mFirebaseUser;
    final Context context = this;

    //related DIYs Arraylist
    ArrayList<SectionDataModel> allSampleData;
    private ArrayList<DIYnames> ePics = new ArrayList<>();
    private ArrayList<User_Profile> eOwner = new ArrayList<>();
    private ArrayList<String> ePrice = new ArrayList<>();
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private List<Bidders> diyBiddings = new ArrayList<Bidders>();;
    private BiddersAdapter biddersAdapter;
    private RecyclerView recyclerView;
    private RecyclerView lv_bidders;
    private String loggedInUserName;
    private User_Profile loggedInUser = null; //for notification

    private User_Profile relOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_related_diys);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        relatedDiyReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        userData = FirebaseDatabase.getInstance().getReference().child("userdata");
        pendingReference = FirebaseDatabase.getInstance().getReference("DIY Pending Items").child(userID);
        loggedInName = FirebaseDatabase.getInstance().getReference().child("userdata");
        relatedRelatedReference  = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        loggedInName = FirebaseDatabase.getInstance().getReference().child("userdata");
        pending_reference = FirebaseDatabase.getInstance().getReference("DIY Pending Items").child(userID);
        user_reference = FirebaseDatabase.getInstance().getReference().child("userdata");

        /* Toolbar Configurations */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //related DIYS
        allSampleData = new ArrayList<SectionDataModel>();
        ePics = new ArrayList<>();
        ePrice = new ArrayList<>();
        eOwner = new ArrayList<>();

   /* User Interface Initializations */
        recyclerView = (RecyclerView) findViewById(R.id.lv_bidders);
        recyclerView.setHasFixedSize(true);

         /* Bidding Lists */
        biddersAdapter = new BiddersAdapter(ViewRelatedDIYS.this,diyBiddings);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(biddersAdapter);

        //bidders
        tv_bidders = (TextView) findViewById(R.id.tv_bidders);
        lv_bidders = (RecyclerView) findViewById(R.id.lv_bidders);
        btn_bidders = (Button) findViewById(R.id.btn_bidders_bid);

        final RecyclerView relatedDIYrecyclerView = (RecyclerView) findViewById(R.id.relatedDIYrecyclerView);
        relatedDIYrecyclerView.setHasFixedSize(true);

        final RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this, allSampleData);

        relatedDIYrecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        relatedDIYrecyclerView.setAdapter(adapter);


        imgviewpager = (ViewPager) findViewById(R.id.related_view_pager);
        sview_diyName = (TextView) findViewById(R.id.diy_name);
        sview_diyMaterials = (TextView) findViewById(R.id.sview_diy_materials);
        sview_diyProcedures = (TextView) findViewById(R.id.sview_diy_procedures);
        sell_details = (TextView) findViewById(R.id.related_sell_details);
        textViewPHP = (TextView) findViewById(R.id.textView33);
        tv_bid_xpire = (TextView) findViewById(R.id.et_bidding_expiration);
        tv_bid_price = (TextView) findViewById(R.id.et_bidding_price);
        tv_bid_comment = (TextView) findViewById(R.id.et_bidding_commnt);
        bidding_winner_amount = (TextView) findViewById(R.id.winner_bid_amount);
        bidding_winner_name = (TextView) findViewById(R.id.winner_bid_owner);

        diyBy = (TextView) findViewById(R.id.txt_by);
        diyOwner = (TextView) findViewById(R.id.txt_user_owner_name);
        diyQty = (TextView) findViewById(R.id.textQTY);
        textViewpromo = (TextView) findViewById(R.id.textView34);
        promoPrice = (TextView) findViewById(R.id.related_promo_price);
        sellers_address = (TextView) findViewById(R.id.relatedDiy_owner_add);
        sellers_contact_no = (TextView) findViewById(R.id.relatedDiy_owner_cn);
        outside_view = (ImageView) findViewById(R.id.outside_imageview);
        buyBtn = (Button) findViewById(R.id.btn_buy_relatedDiy);
        contactSellerBtn = (Button) findViewById(R.id.btn_contact_relatedDiy_owner);
        bidMyItemBtn = (Button) findViewById(R.id.btn_bid_my_item);
        buyerBidBtn = (Button) findViewById(R.id.btn_buyers_bid);
        createMyPromoBtn = (Button) findViewById(R.id.btn_create_my_promo);
        editDIYBtn = (Button) findViewById(R.id.editDIYBtn); //edit DIY details

        cardview2 = (CardView) findViewById(R.id.cardview2);//materials
        cardview3 = (CardView) findViewById(R.id.cardview3); //procedures
        cardview4 = (CardView) findViewById(R.id.cardView4); //prices
        cardView5 = (CardView) findViewById(R.id.cardView5); //seller info
        cardView6 = (CardView) findViewById(R.id.cardView6); //seller bid Info
        cardView7 = (CardView) findViewById(R.id.cardView7); //buyers bid
        cardView8 = (CardView) findViewById(R.id.cardView8); //related diys
        cardView9 = (CardView) findViewById(R.id.cardView9); //bid winner

        final String get_name = getIntent().getStringExtra("Nname");
        sview_diyName.setText(get_name);

        relatedDiyReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                final DIYSell diySellinfo = dataSnapshot.getValue(DIYSell.class);


                if(get_name.equalsIgnoreCase(diYnames.getDiyName())){
                    if(diYnames.getIdentity().equalsIgnoreCase("selling")){
                        user_reference.child(diYnames.getUser_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                relOwner = dataSnapshot.getValue(User_Profile.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        textViewpromo.setVisibility(View.INVISIBLE);
                        promoPrice.setVisibility(View.INVISIBLE); //promo price
                        cardview2.setVisibility(View.INVISIBLE); //materials
                        cardview3.setVisibility(View.INVISIBLE); //procedures
                        cardView6.setVisibility(View.INVISIBLE); //sellers bid
                        cardView7.setVisibility(View.INVISIBLE); //buyers bid
                        buyerBidBtn.setVisibility(View.INVISIBLE);
                        cardView9.setVisibility(View.INVISIBLE);//display bidding winner


                        RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        nlp.topMargin = 580;
                        nlp.bottomMargin = 15;
                        nlp.leftMargin = 20;
                        nlp.rightMargin = 20;
                        cardview4.setLayoutParams(nlp);


                        RelativeLayout.LayoutParams rel = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        rel.topMargin = 1000;
                        rel.bottomMargin = 15;
                        rel.leftMargin = 20;
                        rel.rightMargin = 20;
                        cardView8.setLayoutParams(rel);

                        RelativeLayout.LayoutParams cs = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        cs.topMargin = 1600;
                        cs.bottomMargin = 15;
                        cs.leftMargin = 20;
                        cs.rightMargin = 20;
                        contactSellerBtn.setLayoutParams(cs);

                        RelativeLayout.LayoutParams buy = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        buy.topMargin = 1720;
                        buy.bottomMargin = 15;
                        buy.leftMargin = 20;
                        buy.rightMargin = 20;
                        buyBtn.setLayoutParams(buy);

                        RelativeLayout.LayoutParams cp = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        cp.topMargin = 1600;
                        cp.bottomMargin = 15;
                        cp.leftMargin = 20;
                        cp.rightMargin = 20;
                        createMyPromoBtn.setLayoutParams(cp);

                        RelativeLayout.LayoutParams bd = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        bd.topMargin = 1720;
                        bd.bottomMargin = 15;
                        bd.leftMargin = 20;
                        bd.rightMargin = 20;
                        bidMyItemBtn.setLayoutParams(bd);

                        final int categoryPosition = Integer.parseInt(dataSnapshot.child("category_postition").getValue().toString());
                        Log.e("categoryPosition", String.valueOf(categoryPosition));

                        final String category = dataSnapshot.child("category").getValue().toString();
                        Log.e("categoryInDBBB", category);

                        //Display related DIYS
                        final SectionDataModel dm = new SectionDataModel();
                        dm.setHeaderTitle("Related DIYS: ");
                        dm.setAllItemsInSection(ePrice);
                        dm.setAllProfileInSection(eOwner);
                        dm.setAllPicturesInSection(ePics);
                        allSampleData.add(dm);

                        relatedRelatedReference.orderByChild("category").equalTo(category).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                DIYnames diy = dataSnapshot.getValue(DIYnames.class);

                                Log.e("relOwner", "" + String.valueOf(relOwner));

                                if(diYnames.diyName != diy.diyName){

                                    Log.e("catStatusss", diy.diyName + " = " + String.valueOf(dataSnapshot.getChildrenCount()));

                                    Log.e("diysssss", diy.diyName);
                                    Log.e("relOwnersss", "" + dataSnapshot);

                                    String message_prices="";
                                    String message_qtys = "";
                                    List<String> message_Prices = new ArrayList<String>();
                                    List<String> message_Qtys = new ArrayList<String>();
                                    for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                                        double price = postSnapshot.child("selling_price").getValue(double.class);
                                        int qty = postSnapshot.child("selling_qty").getValue(int.class);
                                        message_qtys += qty;
                                        message_Qtys.add(message_qtys);
                                        message_prices += price;
                                        message_Prices.add(message_prices);

                                        Log.e("relPrice", message_prices);
                                        Log.e("relQty", message_qtys);
                                    }


                                    dm.addItemInSection(diy.diyName);
                                    dm.addProfileInSection(dataSnapshot.getValue(User_Profile.class));
                                    dm.addPictureInSection(dataSnapshot.getValue(DIYnames.class));

                                    Log.e("dm", String.valueOf(dm));
                                    Log.e("allSampleData", String.valueOf(allSampleData));
                                    Log.e("allPrice", String.valueOf(ePrice));
                                    Log.e("allOwner", String.valueOf(eOwner));
                                    Log.e("allPics", String.valueOf(ePics));

                                    adapter.notifyDataSetChanged();



                                }
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

                        loggedInName.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                                loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        userData.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                final User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                if(diYnames.user_id.equals(user_profile.getUserID())) {
                                    user_name = user_profile.getF_name() + " " + user_profile.getL_name();
                                    diyOwner.setText(user_name);

                                    diyOwner.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(ViewRelatedDIYS.this, "Owner: " + " " + user_name, Toast.LENGTH_SHORT).show();
                                            //get seller info nya i set sa profile activity, put.Extra?
                                            Intent profileIntent = new Intent(ViewRelatedDIYS.this, OtherUserProfileActivity.class);
                                            Log.e("sellerID", user_profile.getUserID());

                                            profileIntent.putExtra("profileUserId", user_profile.getUserID());
                                            profileIntent.putExtra("profileUserFName", user_profile.getF_name());
                                            profileIntent.putExtra("profileUserLName", user_profile.getL_name());
                                            profileIntent.putExtra("profileEmailAdd", user_profile.getEmail());
                                            profileIntent.putExtra("profileNumber", user_profile.getContact_no());
                                            profileIntent.putExtra("profileAddress", user_profile.getAddress());
                                            profileIntent.putExtra("profilePicture", user_profile.getUserProfileUrl());
                                            profileIntent.putExtra("profileRatings", user_profile.getUserRating().toString());
                                            profileIntent.putExtra("reportedBy", loggedInUserName);
                                            profileIntent.putExtra("customerID", userID);

                                            Log.e("reportedBy", loggedInUserName);
                                            Log.e("reportedByID", userID);

                                            startActivity(profileIntent);
                                        }
                                    });

                                    Log.e("user_owner_name", user_name);
                                    sellers_contact_no.setText(user_profile.getContact_no());
                                    sellers_address.setText(user_profile.getAddress());

                                    sellers_address.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(ViewRelatedDIYS.this, "Owner Address: " + user_profile.getAddress(),
                                                    Toast.LENGTH_SHORT).show();

                                            Intent addressIntent = new Intent(ViewRelatedDIYS.this, MapsActivity.class);
                                            addressIntent.putExtra("userLocation", sellers_address.getText().toString());
                                            Log.e("sellerAdd", sellers_address.getText().toString());
                                            startActivity(addressIntent);
                                        }
                                    });


                                }
                                if(diYnames.user_id.equals(userID)){
                                    buyBtn.setVisibility(View.INVISIBLE);
                                    contactSellerBtn.setVisibility(View.INVISIBLE);
                                    createMyPromoBtn.setVisibility(View.VISIBLE);
                                    bidMyItemBtn.setVisibility(View.VISIBLE);
                                    editDIYBtn.setVisibility(View.VISIBLE);

                                }else{
                                    buyBtn.setVisibility(View.VISIBLE);
                                    contactSellerBtn.setVisibility(View.VISIBLE);
                                    createMyPromoBtn.setVisibility(View.INVISIBLE);
                                    bidMyItemBtn.setVisibility(View.INVISIBLE);
                                    editDIYBtn.setVisibility(View.INVISIBLE);

                                }

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


                        String message_price="";
                        String message_qty = "";
                        String message_dsc = "";

                        List<String> message_Price = new ArrayList<String>();
                        List<String> message_Qty = new ArrayList<String>();
                        final List<String> message_Dsc = new ArrayList<String>();

                        for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                            double price = postSnapshot.child("selling_price").getValue(double.class);
                            int qty = postSnapshot.child("selling_qty").getValue(int.class);
                            String dsc = postSnapshot.child("selling_descr").getValue(String.class);

                            message_qty += qty;
                            message_Qty.add(message_qty);
                            message_price += price;
                            message_Price.add(message_price);
                            message_dsc +=dsc;
                            message_Dsc.add(message_dsc);

                        }

                        sell_details.setText(message_price);
                        diyQty.setText(message_qty + " " + "piece/s");


                        final String finalMessage_price1 = message_price;
                        final String finalMessage_qty1 = message_qty;
                        createMyPromoBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final Dialog myDialog = new Dialog(context);
                                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                myDialog.setContentView(R.layout.promo_chooser);
                                myDialog.setCancelable(false);

                                Button priceDiscount = (Button) myDialog.findViewById(R.id.id_price_discount);
                                Button buyTakeDiscount = (Button) myDialog.findViewById(R.id.id_buyTake_discount);
                                TextView cancel = (TextView) myDialog.findViewById(R.id.cancel);

                                priceDiscount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent toPriceDiscount = new Intent(ViewRelatedDIYS.this, PriceDiscountActivity.class);
                                        toPriceDiscount.putExtra("diy_Name",diYnames.getDiyName());
                                        toPriceDiscount.putExtra("diy_ID", diYnames.getProductID());
                                        toPriceDiscount.putExtra("diy_img", diYnames.getDiyUrl());
                                        toPriceDiscount.putExtra("getDBKey", dataSnapshot.getKey());
                                        toPriceDiscount.putExtra("sellingPrice", finalMessage_price1);
                                        toPriceDiscount.putExtra("sellingQuantity", finalMessage_qty1);
                                        toPriceDiscount.putExtra("sellerUserID", diYnames.getUser_id());
                                        toPriceDiscount.putExtra("sellerName", loggedInUserName);
                                        toPriceDiscount.putExtra("itemId", dataSnapshot.getKey());

                                        Log.e("promoDiscountItemID", dataSnapshot.getKey());
                                        Log.e("finalMessage_price1", finalMessage_price1);
                                        Log.e("sellerUserIDD", diYnames.getUser_id());
                                        Log.e("sellerNamee", loggedInUserName);

                                        startActivity(toPriceDiscount);
                                    }
                                });
                                buyTakeDiscount.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent to_promo = new Intent(ViewRelatedDIYS.this,PromoActivity.class);
                                        to_promo.putExtra("diy_Name",diYnames.getDiyName());
                                        to_promo.putExtra("diy_ID", diYnames.getProductID());
                                        to_promo.putExtra("diy_img", diYnames.getDiyUrl());
                                        to_promo.putExtra("sellingPrice", finalMessage_price1);
                                        to_promo.putExtra("sellingQuantity", finalMessage_qty1);
                                        to_promo.putExtra("sellerUserID", diYnames.getUser_id());
                                        to_promo.putExtra("sellerName", loggedInUserName);
                                        to_promo.putExtra("itemId", dataSnapshot.getKey());

                                        Log.e("promoBuyTakeItemID", dataSnapshot.getKey());
                                        Log.e("sellerUserID", diYnames.getUser_id());
                                        Log.e("sellerName", loggedInUserName);
                                        Log.e("NAMEEEE", diYnames.getDiyName());
                                        startActivity(to_promo);
                                    }
                                });
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myDialog.cancel();
                                    }
                                });

                                myDialog.show();
                            }
                        });



                        String messageMat = "";
                        List<String> messageMaterials = new ArrayList<String>();
                        //int count = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                            String dbMaterialName = postSnapshot.child("name").getValue(String.class).toLowerCase();
                            String dbMaterialUnit = postSnapshot.child("unit").getValue(String.class);
                            Long dbMaterialQuantity = postSnapshot.child("quantity").getValue(Long.class);
//                                messageMat += "\n" + dbMaterialName + " = " + dbMaterialQuantity + " " + dbMaterialUnit;
                            messageMat += "\n" + dbMaterialQuantity + " " + dbMaterialUnit+ " " +dbMaterialName;
                            messageMaterials.add(messageMat);
                            //count++;
                        }

                        String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
                        Log.e("splits", "" + splits);

                        String messageProd = "";
                        List<String> messageProcedure = new ArrayList<String>();
                        for (int i = 0; i < splits.length; i++) {
                            Log.d("splitVal", splits[i].substring(5, splits[i].length() - 1));
                            String message = i + 1 + ". " + splits[i].substring(5, splits[i].length() - 1).replaceAll("\\}", "").replaceAll("=", "");
                            messageProd += "\n" + message;
                            messageProcedure.add(message);
                            Log.d("messageProd", messageProd);
                        }

                        sview_diyMaterials.setText(messageMat);
//                            tv_procedures.setText("NOT APPLICABLE! BUY THE ITEM FIRST OR ASK PERMISSION TO THE OWNER!");
                        sview_diyProcedures.setText(messageProd);
                        sview_diyProcedures.setTextColor(Color.BLACK);

                        final String finalMessage_price2 = message_price;
                        final String finalMessage_qty2 = message_qty;
                        final String finalMessage_dsc = message_dsc;
                        editDIYBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "Selling Edit Button Clicked", Toast.LENGTH_SHORT).show();

                                Intent item = new Intent(ViewRelatedDIYS.this,EditDIYDetailsActivity.class);
                                item.putExtra("diyKey", dataSnapshot.getKey());
                                item.putExtra("diyName", diYnames.diyName);
                                item.putExtra("diyOwner", user_name);
                                item.putExtra("diyCategory", categoryPosition);
                                item.putExtra("diyImage", diYnames.diyUrl);
                                item.putExtra("diyPrice", finalMessage_price2);
                                item.putExtra("diyQuantity", finalMessage_qty2);
                                item.putExtra("diyDescription", finalMessage_dsc);

                                Log.e("diyPrice", finalMessage_price2);
                                Log.e("diyQuantity", finalMessage_qty2);
                                Log.e("diyDescription",finalMessage_dsc);

                                startActivity(item);
                            }
                        });




                        //BUY btn
                        final String finalMessage_price = message_price;
                        final String finalMessage_qty = message_qty;
                        buyBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                relatedDiyReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        final Float float_this = Float.valueOf(0);
                                        //DIY price
                                        final float pendingPrice = Float.parseFloat((finalMessage_price));
                                        final int pendingQty = Integer.parseInt((finalMessage_qty));

                                        //Seller Info -> buyer side ni
                                        DIYSell productInfo =  dataSnapshot.getValue(DIYSell.class);
                                        final String diyName = diySellinfo.getDiyName();
                                        final String diyUrl = diySellinfo.getDiyUrl();
                                        final String user_id = diySellinfo.getUser_id();
                                        final String sellerName = diySellinfo.getLoggedInUser();
                                        final String productID = diySellinfo.getProductID();
                                        String status = diySellinfo.getIdentity();
                                        final String buyerid = userID;


                                        if (!userID.equals(diySellinfo.getUser_id())) {
                                            Log.e("pending_not_same", String.valueOf("" + userID != diySellinfo.getUser_id()));
                                            Log.e("userid", String.valueOf("" + userID));
                                            Log.e("info_userid", String.valueOf("" + diySellinfo.getUser_id()));

                                            pending_reference.orderByChild("productID").equalTo(diySellinfo.productID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        Log.e("same_prodID", "" + dataSnapshot.exists());
                                                        Log.e("same_data", "" + dataSnapshot);

                                                        final Dialog dialog = new Dialog(ViewRelatedDIYS.this);
                                                        dialog.setContentView(R.layout.exist_dialog);
                                                        TextView text = (TextView) dialog.findViewById(R.id.e_text);
                                                        text.setText("DIY already added to pending list!");
                                                        ImageView image = (ImageView) dialog.findViewById(R.id.exist_dialog_imageview);
                                                        image.setImageResource(R.drawable.exist);

                                                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOKI);
                                                        dialogButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(ViewRelatedDIYS.this, MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        });
                                                        dialog.show();
                                                    } else {

                                                        final Dialog buyDialog = new Dialog(ViewRelatedDIYS.this);
                                                        buyDialog.setContentView(R.layout.buying_quantity);
                                                        final TextView qtyInputted = (TextView) buyDialog.findViewById(R.id.quantityInputted);

                                                        Button dialogQtyButton = (Button) buyDialog.findViewById(R.id.btnQtyDialogDone);
                                                        dialogQtyButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Log.e("qtyInputted", qtyInputted.getText().toString());
                                                                buyDialog.dismiss();

                                                                String inputQty = qtyInputted.getText().toString();
                                                                int buyItemCount = Integer.parseInt(finalMessage_qty);

                                                                if(inputQty.equals(" ")){
                                                                    inputQty="0";
                                                                }

                                                                if (inputQty.isEmpty()) {
                                                                    Log.e("EmptyQty", "Please enter the quantity of item you want to buy.");
                                                                    Toast.makeText(context, "Please enter how many DIY " + diyName  + " you want to buy." + "", Toast.LENGTH_SHORT).show();
                                                                } else if(Integer.valueOf(inputQty) > buyItemCount){
                                                                    Log.e("NotEnoughQty", "NotEnoughQty");
                                                                    Toast.makeText(context, "Not enough total DIY quantity. We only have " + " " + buyItemCount
                                                                            + " DIY " + diyName + ".", Toast.LENGTH_LONG).show();
                                                                } else{

                                                                    //Notification
                                                                    user_reference.addChildEventListener(new ChildEventListener() {
                                                                        @Override
                                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                                                            if(loggedInUserName!=null){
                                                                                PushNotification pushNotification = new PushNotification(getApplicationContext());
                                                                                pushNotification.title("Buyer Notification")
                                                                                        .message(loggedInUserName +" buy your item!")
                                                                                        .accessToken(user_profile.getAccess_token())
                                                                                        .send();
                                                                            }
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

                                                                    int buyQty = Integer.parseInt(qtyInputted.getText().toString());

                                                                    DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, "Pending Item", float_this,
                                                                            float_this, buyerid, loggedInUserName, buyQty);
                                                                    final String upload_info = pending_reference.push().getKey();
                                                                    pending_reference.child(upload_info).setValue(info);
                                                                    pending_reference.child(upload_info).child("selling_price").setValue(pendingPrice);
                                                                    pending_reference.child(upload_info).child("userStatus").setValue("buyer");
                                                                    pending_reference.child(upload_info).child("dateAdded").setValue(sdate);

                                                                    //DBref for buyer
                                                                    final DatabaseReference pendingRefByOwner = FirebaseDatabase.getInstance().getReference("DIY Pending Items")
                                                                            .child(user_id); //userID sa seller na
                                                                    Log.e("pendingRefByOwner", String.valueOf(pendingRefByOwner));

                                                                    //BuyerInfo - userID sa buyer
                                                                    Log.e("userIDDD", userID);

                                                                    DIYSell buyer = new DIYSell(diyName, diyUrl, user_id, productID, "For Confirmation Item", float_this,
                                                                            float_this, buyerid, sellerName, buyQty);
                                                                    final String uploadBuyerInfo = pendingRefByOwner.child(upload_info).getKey();
                                                                    pendingRefByOwner.child(uploadBuyerInfo).setValue(buyer);
                                                                    pendingRefByOwner.child(uploadBuyerInfo).child("selling_price").setValue(pendingPrice);
                                                                    pendingRefByOwner.child(uploadBuyerInfo).child("selling_qty").setValue(pendingQty);
                                                                    pendingRefByOwner.child(uploadBuyerInfo).child("userStatus").setValue("seller");
                                                                    pendingRefByOwner.child(uploadBuyerInfo).child("dateAdded").setValue(sdate);


                                                                    final Dialog dialog = new Dialog(ViewRelatedDIYS.this);
                                                                    dialog.setContentView(R.layout.done_dialog);
                                                                    TextView text = (TextView) dialog.findViewById(R.id.text);
                                                                    text.setText("DIY added to pending list!");
                                                                    ImageView image = (ImageView) dialog.findViewById(R.id.dialog_imageview);
                                                                    image.setImageResource(R.drawable.done);

                                                                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                                    // if button is clicked, close the custom dialog
                                                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            Intent intent = new Intent(ViewRelatedDIYS.this, MainActivity.class);
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                                    dialog.show();



                                                                }

                                                            }
                                                        });
                                                        buyDialog.show();

//
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
                                        }else if(userID.equals(diySellinfo.getUser_id())) {
                                            Log.e("pending_same", String.valueOf("" + userID.equals(diySellinfo.getUser_id())));
                                            Toast.makeText(ViewRelatedDIYS.this, "It's your own product!", Toast.LENGTH_SHORT).show();
                                        }



                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        bidMyItemBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent item = new Intent(ViewRelatedDIYS.this,ToBidProduct.class);
                                item.putExtra("itemId", dataSnapshot.getKey());
                                item.putExtra("sellerName", loggedInUserName);
                                Log.e("sellerNameBid", loggedInUserName);
                                startActivity(item);
                            }
                        });

                        contactSellerBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final Dialog myDialog = new Dialog(context);
                                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                myDialog.setContentView(R.layout.contact_seller);
                                myDialog.setCancelable(false);
                                Button chat = (Button) myDialog.findViewById(R.id.chat);
                                Button call = (Button) myDialog.findViewById(R.id.call);
                                Button sms = (Button) myDialog.findViewById(R.id.sms);
                                TextView cancel = (TextView) myDialog.findViewById(R.id.cancel);

                                chat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //get seller
//                                    if(diyInfo.getUser_id())
//                                    if(diyInfo.getUser_id().equals(loggedInName.getKey())){
//                                        Intent intent = new Intent(ViewRelatedDIYS.this, ChatSplashActivity.class);
//                                        startActivity(intent);
//                                        Log.e("DATAKEY",loggedInName.getKey());

                                        Log.e("CHAAAAATTTTTT",relOwner.getEmail());
                                        ChatActivity.startActivity(getApplicationContext(),relOwner.getEmail(),relOwner.getUserID(), relOwner.getAccess_token());
//                                    }
                                    }
                                });
                                call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String phone = sellers_contact_no.getText().toString();
                                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                                "tel", phone, null));
                                        startActivity(phoneIntent);
                                    }
                                });
                                sms.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String phone = sellers_contact_no.getText().toString();
                                        Intent smsMsgAppVar = new Intent(Intent.ACTION_VIEW);
                                        smsMsgAppVar.setData(Uri.parse("sms:" +  phone));
                                        smsMsgAppVar.putExtra("sms_body", "Hi, Good Day! ");
                                        startActivity(smsMsgAppVar);
                                    }
                                });
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myDialog.cancel();
                                    }
                                });

                                myDialog.show();
                                myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.cancel();
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                            }
                        });

                        for (DataSnapshot insideDataSnapshot: dataSnapshot.child("bidding").getChildren()) {
                            DIYBidding biddingItem = insideDataSnapshot.getValue(DIYBidding.class);
                            tv_bid_xpire.setText(biddingItem.getDate());
                            tv_bid_price.setText(biddingItem.getIntial_price()+"");
                            tv_bid_comment.setText(biddingItem.getMessage());
                        }

                        for (DataSnapshot inside_DataSnapshot: dataSnapshot.child("bidders").getChildren()) {
                            Bidders biddersItem = inside_DataSnapshot.getValue(Bidders.class);
                            diyBiddings.add(biddersItem);
                            biddersAdapter.notifyDataSetChanged();
                        }



                        if (diYnames.diyUrl != null) {
                            imgviewpager = (ViewPager) findViewById(R.id.related_view_pager);
                            viewImagesSearchPagerAdapter = new ViewImagesSearchPagerAdapter(getBaseContext(), diYnames.diyUrl);
                            imgviewpager.setAdapter(viewImagesSearchPagerAdapter);
                        }

                    }



                    else if(diYnames.getIdentity().equalsIgnoreCase("community")){
                        cardview4.setVisibility(View.INVISIBLE);
                        textViewpromo.setVisibility(View.INVISIBLE);
                        promoPrice.setVisibility(View.INVISIBLE);
                        sell_details.setVisibility(View.INVISIBLE);
                        textViewPHP.setVisibility(View.INVISIBLE);
                        cardView5.setVisibility(View.INVISIBLE);
                        buyBtn.setVisibility(View.INVISIBLE);
                        contactSellerBtn.setVisibility(View.INVISIBLE);
                        diyQty.setVisibility(View.INVISIBLE);
                        cardView5.setVisibility(View.INVISIBLE);
                        cardView6.setVisibility(View.INVISIBLE);
                        cardView7.setVisibility(View.INVISIBLE);
                        createMyPromoBtn.setVisibility(View.INVISIBLE);
                        bidMyItemBtn.setVisibility(View.INVISIBLE);
                        buyerBidBtn.setVisibility(View.INVISIBLE);
                        cardView9.setVisibility(View.INVISIBLE);//display bidding winner

                        RelativeLayout.LayoutParams related = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        related.topMargin = 1400;
                        related.bottomMargin = 15;
                        related.leftMargin = 20;
                        related.rightMargin = 20;
                        cardView8.setLayoutParams(related);

                        final int categoryPosition = Integer.parseInt(dataSnapshot.child("category_postition").getValue().toString());
                        Log.e("categoryPosition", String.valueOf(categoryPosition));

                        final String category = dataSnapshot.child("category").getValue().toString();
                        Log.e("categoryInDBBB", category);

                        //Display related DIYS
                        final SectionDataModel dm = new SectionDataModel();
                        dm.setHeaderTitle("Related DIYS: ");
                        dm.setAllItemsInSection(ePrice);
                        dm.setAllProfileInSection(eOwner);
                        dm.setAllPicturesInSection(ePics);
                        allSampleData.add(dm);

                        relatedRelatedReference.orderByChild("category").equalTo(category).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                DIYnames diy = dataSnapshot.getValue(DIYnames.class);
                                User_Profile relOwner = dataSnapshot.getValue(User_Profile.class);
                                Log.e("relOwner", "" + String.valueOf(relOwner));

                                if(diYnames.diyName != diy.diyName){
                                    Log.e("catStatusss", diy.diyName + " = " + String.valueOf(dataSnapshot.getChildrenCount()));
                                    Log.e("diysssss", diy.diyName);
                                    Log.e("relOwnersss", "" + dataSnapshot);

                                    String message_prices="";
                                    String message_qtys = "";
                                    List<String> message_Prices = new ArrayList<String>();
                                    List<String> message_Qtys = new ArrayList<String>();
                                    for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                                        double price = postSnapshot.child("selling_price").getValue(double.class);
                                        int qty = postSnapshot.child("selling_qty").getValue(int.class);
                                        message_qtys += qty;
                                        message_Qtys.add(message_qtys);
                                        message_prices += price;
                                        message_Prices.add(message_prices);

                                        Log.e("relPrice", message_prices);
                                        Log.e("relQty", message_qtys);
                                    }

                                    dm.addItemInSection(diy.diyName);
                                    dm.addProfileInSection(dataSnapshot.getValue(User_Profile.class));
                                    dm.addPictureInSection(dataSnapshot.getValue(DIYnames.class));

                                    Log.e("dm", String.valueOf(dm));
                                    Log.e("allSampleData", String.valueOf(allSampleData));
                                    Log.e("allPrice", String.valueOf(ePrice));
                                    Log.e("allOwner", String.valueOf(eOwner));
                                    Log.e("allPics", String.valueOf(ePics));

                                    adapter.notifyDataSetChanged();

                                }
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


                        userData.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                final User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                if(diYnames.user_id.equals(user_profile.getUserID())){
                                    user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                    diyOwner.setText(user_name);

                                    diyOwner.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(ViewRelatedDIYS.this, "Owner: " + " " + user_name, Toast.LENGTH_SHORT).show();
                                            //get seller info nya i set sa profile activity, put.Extra?
                                            Intent profileIntent = new Intent(ViewRelatedDIYS.this, OtherUserProfileActivity.class);

                                            profileIntent.putExtra("profileUserId", user_profile.getUserID());
                                            profileIntent.putExtra("profileUserFName", user_profile.getF_name());
                                            profileIntent.putExtra("profileUserLName", user_profile.getL_name());
                                            profileIntent.putExtra("profileEmailAdd", user_profile.getEmail());
                                            profileIntent.putExtra("profileNumber", user_profile.getContact_no());
                                            profileIntent.putExtra("profileAddress", user_profile.getAddress());
                                            profileIntent.putExtra("profilePicture", user_profile.getUserProfileUrl());
                                            profileIntent.putExtra("profileRatings", user_profile.getUserRating());
                                            profileIntent.putExtra("reportedBy", loggedInUserName);
                                            profileIntent.putExtra("customerID", userID);
                                            startActivity(profileIntent);
                                        }
                                    });

                                    Log.e("user_owner_name", user_name);

                                }if(diYnames.user_id.equals(userID)){
                                    editDIYBtn.setVisibility(View.VISIBLE);
                                } else{
                                    editDIYBtn.setVisibility(View.INVISIBLE);
                                }

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

                        String messageMat = "";
                        List<String> messageMaterials = new ArrayList<String>();
                        //int count = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                            String dbMaterialName = postSnapshot.child("name").getValue(String.class).toLowerCase();
                            String dbMaterialUnit = postSnapshot.child("unit").getValue(String.class);
                            Long dbMaterialQuantity = postSnapshot.child("quantity").getValue(Long.class);
//                                messageMat += "\n" + dbMaterialName + " = " + dbMaterialQuantity + " " + dbMaterialUnit;
                            messageMat += "\n" + dbMaterialQuantity + " " + dbMaterialUnit+ " " +dbMaterialName;
                            messageMaterials.add(messageMat);
                            //count++;
                        }

                        String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
                        Log.e("splits", "" + splits);

                        String messageProd = "";
                        List<String> messageProcedure = new ArrayList<String>();
                        for (int i = 0; i < splits.length; i++) {
                            Log.d("splitVal", splits[i].substring(5, splits[i].length() - 1));
                            String message = i + 1 + ". " + splits[i].substring(5, splits[i].length() - 1).replaceAll("\\}", "").replaceAll("=", "");
                            messageProd += "\n" + message;
                            messageProcedure.add(message);
                            Log.d("messageProd", messageProd);
                        }

                        sview_diyMaterials.setText(messageMat);
                        sview_diyProcedures.setText(messageProd);
                        sview_diyProcedures.setTextColor(Color.BLACK);

                        //Edit DIY (Community)
                        final String finalMessageMat = messageMat;
                        final String finalMessageProd = messageProd;
                        editDIYBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "Community Edit Button Clicked", Toast.LENGTH_SHORT).show();

                                Intent item = new Intent(ViewRelatedDIYS.this,EditDIYDetailsActivity.class);
                                item.putExtra("diyKey", dataSnapshot.getKey());
                                item.putExtra("diyName", diYnames.diyName);
                                item.putExtra("diyOwner", user_name);
                                item.putExtra("diyCategory", categoryPosition);
                                item.putExtra("diyMaterials", finalMessageMat);
                                item.putExtra("diyProcedures", finalMessageProd);
                                item.putExtra("diyImage", diYnames.diyUrl);

                                startActivity(item);
                            }
                        });



                        if (diYnames.diyUrl != null) {
                            imgviewpager = (ViewPager) findViewById(R.id.related_view_pager);
                            viewImagesSearchPagerAdapter = new ViewImagesSearchPagerAdapter(getBaseContext(), diYnames.diyUrl);
                            imgviewpager.setAdapter(viewImagesSearchPagerAdapter);
                        }

                    }



                    else if (diYnames.getIdentity().equalsIgnoreCase("on bid!")){
                        textViewpromo.setVisibility(View.INVISIBLE);
                        promoPrice.setVisibility(View.INVISIBLE); //promo price
                        cardview2.setVisibility(View.INVISIBLE); //materials
                        cardview3.setVisibility(View.INVISIBLE); //procedures
                        cardview4.setVisibility(View.INVISIBLE); //price
                        contactSellerBtn.setVisibility(View.INVISIBLE);
                        buyBtn.setVisibility(View.INVISIBLE);
                        bidMyItemBtn.setVisibility(View.INVISIBLE);
                        createMyPromoBtn.setVisibility(View.INVISIBLE);
                        diyQty.setVisibility(View.INVISIBLE);
                        cardView9.setVisibility(View.INVISIBLE);

                        RelativeLayout.LayoutParams sD = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        sD.topMargin = 580;
                        sD.bottomMargin = 15;
                        sD.leftMargin = 20;
                        sD.rightMargin = 20;
                        cardView5.setLayoutParams(sD);

                        final String category = dataSnapshot.child("category").getValue().toString();
                        Log.e("categoryInDBBB", category);

                        //Display related DIYS
                        final SectionDataModel dm = new SectionDataModel();
                        dm.setHeaderTitle("Related DIYS: ");
                        dm.setAllItemsInSection(ePrice);
                        dm.setAllProfileInSection(eOwner);
                        dm.setAllPicturesInSection(ePics);
                        allSampleData.add(dm);

                        relatedRelatedReference.orderByChild("category").equalTo(category).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                DIYnames diy = dataSnapshot.getValue(DIYnames.class);
                                User_Profile relOwner = dataSnapshot.getValue(User_Profile.class);
                                Log.e("relOwner", "" + String.valueOf(relOwner));

                                if(diYnames.diyName != diy.diyName){
                                    Log.e("catStatusss", diy.diyName + " = " + String.valueOf(dataSnapshot.getChildrenCount()));
                                    Log.e("diysssss", diy.diyName);
                                    Log.e("relOwnersss", "" + dataSnapshot);

                                    String message_prices="";
                                    String message_qtys = "";
                                    List<String> message_Prices = new ArrayList<String>();
                                    List<String> message_Qtys = new ArrayList<String>();
                                    for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                                        double price = postSnapshot.child("selling_price").getValue(double.class);
                                        int qty = postSnapshot.child("selling_qty").getValue(int.class);
                                        message_qtys += qty;
                                        message_Qtys.add(message_qtys);
                                        message_prices += price;
                                        message_Prices.add(message_prices);

                                        Log.e("relPrice", message_prices);
                                        Log.e("relQty", message_qtys);
                                    }

                                    dm.addItemInSection(diy.diyName);
                                    dm.addProfileInSection(dataSnapshot.getValue(User_Profile.class));
                                    dm.addPictureInSection(dataSnapshot.getValue(DIYnames.class));

                                    Log.e("dm", String.valueOf(dm));
                                    Log.e("allSampleData", String.valueOf(allSampleData));
                                    Log.e("allPrice", String.valueOf(ePrice));
                                    Log.e("allOwner", String.valueOf(eOwner));
                                    Log.e("allPics", String.valueOf(ePics));

                                    adapter.notifyDataSetChanged();

                                }
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

                        userData.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                                loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        userData.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                final User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                if(diYnames.user_id.equals(user_profile.getUserID())){
                                    user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                    diyOwner.setText(user_name);

                                    diyOwner.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(ViewRelatedDIYS.this, "Owner: " + " " + user_name, Toast.LENGTH_SHORT).show();
                                            //get seller info nya i set sa profile activity, put.Extra?
                                            Intent profileIntent = new Intent(ViewRelatedDIYS.this, OtherUserProfileActivity.class);

                                            profileIntent.putExtra("profileUserId", user_profile.getUserID());
                                            profileIntent.putExtra("profileUserFName", user_profile.getF_name());
                                            profileIntent.putExtra("profileUserLName", user_profile.getL_name());                                            profileIntent.putExtra("profileEmailAdd", user_profile.getEmail());
                                            profileIntent.putExtra("profileNumber", user_profile.getContact_no());
                                            profileIntent.putExtra("profileAddress", user_profile.getAddress());
                                            profileIntent.putExtra("profilePicture", user_profile.getUserProfileUrl());
                                            profileIntent.putExtra("profileRatings", user_profile.getUserRating());
                                            profileIntent.putExtra("reportedBy", loggedInUserName);
                                            profileIntent.putExtra("customerID", userID);
                                            startActivity(profileIntent);
                                        }
                                    });

                                    Log.e("user_owner_name", user_name);
                                }if(diYnames.user_id.equals(userID)){
                                    editDIYBtn.setVisibility(View.VISIBLE);
                                } else{
                                    editDIYBtn.setVisibility(View.INVISIBLE);
                                    bidMyItemBtn.setVisibility(View.INVISIBLE);
                                    createMyPromoBtn.setVisibility(View.INVISIBLE);
                                }

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

                        boolean isExpired = false;

                        final int categoryPosition = Integer.parseInt(dataSnapshot.child("category_postition").getValue().toString());
                        Log.e("categoryPosition", String.valueOf(categoryPosition));

                        //bidding expiration
                        for (final DataSnapshot insideDataSnapshot: dataSnapshot.child("bidding").getChildren()) {

                            final DIYBidding biddingItem = insideDataSnapshot.getValue(DIYBidding.class);
                            tv_bid_xpire.setText(biddingItem.getXpire_date());
                            tv_bid_price.setText("₱: " + biddingItem.getIntial_price());
                            Log.e("initialBid", biddingItem.getIntial_price()+"");

                            tv_bid_comment.setText(biddingItem.getMessage());
                            Log.e("expiryDate",biddingItem.getXpire_date());
                            try{
                                // check expiry
                                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                                Date strDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(biddingItem.getXpire_date());
                                if (new Date().after(strDate)) {
                                    isExpired = true;
                                }
                            } catch(ParseException e){
                                Log.e("expiryException",e.getMessage());
                            }
                            Log.e("CHECKEXPIRATION",isExpired+"");


                            //EDIT DIY (ON BID)
                            editDIYBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(context, "On bid Edit Button Clicked", Toast.LENGTH_SHORT).show();

                                    Intent item = new Intent(ViewRelatedDIYS.this,EditDIYDetailsActivity.class);
                                    item.putExtra("diyKey", dataSnapshot.getKey());
                                    item.putExtra("diyKeyInside", insideDataSnapshot.getKey());
                                    Log.e("diyKeyInside", insideDataSnapshot.getKey());

                                    item.putExtra("diyName", diYnames.diyName);
                                    item.putExtra("diyOwner", user_name);
                                    item.putExtra("diyCategory", categoryPosition);
                                    item.putExtra("diyImage", diYnames.diyUrl);
                                    //bidding info

                                    item.putExtra("diyBidInitialPrice", biddingItem.getIntial_price()+"");
                                    item.putExtra("diyBidExpiry", biddingItem.getXpire_date());
                                    item.putExtra("diyBidComment", biddingItem.getMessage());

                                    Log.e("diyBidInitialPrice", "" + biddingItem.getIntial_price()+"");
                                    Log.e("diyBidExpiry", "" + biddingItem.getXpire_date());
                                    Log.e("diyBidComment", "" + biddingItem.getMessage());


                                    startActivity(item);
                                }
                            });

                            buyerBidBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(diYnames.user_id.equals(userID)){
                                        Toast.makeText(ViewRelatedDIYS.this,"THIS IS YOUR ITEM!!!", Toast.LENGTH_SHORT).show();
                                    }else {
                                        biddersReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(dataSnapshot.getKey())
                                                .child("bidders");

                                        final Dialog dialog = new Dialog(context);
                                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                        dialog.setContentView(R.layout.bidders);

                                        Button bidders_dialog_btn = (Button) dialog.findViewById(R.id.btn_bidders_dialog);

                                        bidders_dialog_btn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final EditText bidders_value = (EditText) dialog.findViewById(R.id.et_bidders_price);
                                                String inputBid = bidders_value.getText().toString();
                                                int initialBidAmount = biddingItem.getIntial_price();

                                                if(inputBid.equals(" ")){
                                                    inputBid="0";
                                                }

                                                if (inputBid.isEmpty()) {
                                                    Log.e("EmptyAmount", "Please enter bid amount.");
                                                    Toast.makeText(context, "Please enter bid amount." + "", Toast.LENGTH_SHORT).show();
                                                } else if(Integer.valueOf(inputBid) <= initialBidAmount){
                                                    Log.e("NotEnoughAmount", "NotEnoughAmount");
                                                    Toast.makeText(context, "Bid amount must be equal or greater than " +
                                                            "the set minimum bid amount by the seller." + "", Toast.LENGTH_LONG).show();
                                                } else{
                                                    biddersReference.push().setValue(new Bidders(bidders_value.getText().toString()
                                                            , userID, loggedInUserName, sdate));

                                                    //Notification
                                                    user_reference.addChildEventListener(new ChildEventListener() {
                                                        @Override
                                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                                            if(loggedInUserName!=null){
                                                                PushNotification pushNotification = new PushNotification(getApplicationContext());
                                                                pushNotification.title("Bidders Notification")
                                                                        .message(loggedInUserName +" bid " + bidders_value.getText().toString() + " on your item.")
                                                                        .accessToken(user_profile.getAccess_token())
                                                                        .send();
                                                            }
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

                                                    Intent intent = new Intent(ViewRelatedDIYS.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                        dialog.show();
                                        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                            @Override
                                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                    dialog.cancel();
                                                    return true;
                                                }
                                                return false;
                                            }
                                        });
                                    }
                                }
                            });

                        }

                        //for getting the max bid
                        biddingRef = FirebaseDatabase.getInstance().getReference().child("diy_by_tags")
                                .child(dataSnapshot.getKey()).child("bidders");
                        Log.e("biddingRef", String.valueOf(biddingRef));

                        biddingRef.orderByChild("bid_price").limitToLast(1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                    Log.e("childSnapshot", String.valueOf(childSnapshot));

                                    final Bidders biddersss = childSnapshot.getValue(Bidders.class);
                                    Log.e("bidPrices", biddersss.getBid_price());
                                    String Key = childSnapshot.getKey();
                                    Log.e("Keyssss", Key);
                                    Toast.makeText(context, "Max bid" + " = " + biddersss.getBid_price(), Toast.LENGTH_SHORT).show();

                                    bidding_winner_amount.setText("₱ :" + " " +biddersss.getBid_price());
                                    bidding_winner_name.setText(biddersss.getUser_name());

                                    //Notification
                                    user_reference.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                            if(loggedInUserName!=null){
                                                PushNotification pushNotification = new PushNotification(getApplicationContext());
                                                pushNotification.title("Bidding Notification")
                                                        .message(bidding_winner_name + " is the bidding winner!" )
                                                        .accessToken(user_profile.getAccess_token())
                                                        .send();
                                            }
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

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //invisible views when bidding iss expired
                        if(isExpired){
                            buyerBidBtn.setVisibility(View.INVISIBLE);
                            cardView6.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                            cardView7.setVisibility(View.INVISIBLE);
                            lv_bidders.setVisibility(View.INVISIBLE);
                            cardView9.setVisibility(View.VISIBLE);

                            RelativeLayout.LayoutParams winBid = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                            winBid.topMargin = 830;
                            winBid.bottomMargin = 15;
                            winBid.leftMargin = 20;
                            winBid.rightMargin = 20;
                            cardView9.setLayoutParams(winBid);

                            RelativeLayout.LayoutParams related = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                            related.topMargin = 1020;
                            related.bottomMargin = 15;
                            related.leftMargin = 20;
                            related.rightMargin = 20;
                            cardView8.setLayoutParams(related);

                        }

                        for (DataSnapshot inside_DataSnapshot: dataSnapshot.child("bidders").getChildren()) {
                            Bidders biddersItem = inside_DataSnapshot.getValue(Bidders.class);
                            Log.e("biddersItem", String.valueOf(biddersItem));

                            String bidds = inside_DataSnapshot.child("bid_price").getValue(String.class);
                            Log.e("bidds", String.valueOf(bidds));
                            Log.e(" biddsssss",biddersItem.getBid_price());

                            diyBiddings.add(biddersItem);
                            biddersAdapter.notifyDataSetChanged();
                        }



                        bidMyItemBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent item = new Intent(ViewRelatedDIYS.this,ToBidProduct.class);
                                item.putExtra("itemId", dataSnapshot.getKey());
                                item.putExtra("sellerName", loggedInUserName);
                                Log.e("sellerNameBid", loggedInUserName);
                                startActivity(item);
                            }
                        });



                        String messageMat = "";
                        List<String> messageMaterials = new ArrayList<String>();
                        //int count = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                            String dbMaterialName = postSnapshot.child("name").getValue(String.class).toLowerCase();
                            String dbMaterialUnit = postSnapshot.child("unit").getValue(String.class);
                            Long dbMaterialQuantity = postSnapshot.child("quantity").getValue(Long.class);
//                                messageMat += "\n" + dbMaterialName + " = " + dbMaterialQuantity + " " + dbMaterialUnit;
                            messageMat += "\n" + dbMaterialQuantity + " " + dbMaterialUnit+ " " +dbMaterialName;
                            messageMaterials.add(messageMat);
                            //count++;
                        }

                        String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
                        Log.e("splits", "" + splits);

                        String messageProd = "";
                        List<String> messageProcedure = new ArrayList<String>();
                        for (int i = 0; i < splits.length; i++) {
                            Log.d("splitVal", splits[i].substring(5, splits[i].length() - 1));
                            String message = i + 1 + ". " + splits[i].substring(5, splits[i].length() - 1).replaceAll("\\}", "").replaceAll("=", "");
                            messageProd += "\n" + message;
                            messageProcedure.add(message);
                            Log.d("messageProd", messageProd);
                        }

                        sview_diyMaterials.setText(messageMat);
//                            tv_procedures.setText("NOT APPLICABLE! BUY THE ITEM FIRST OR ASK PERMISSION TO THE OWNER!");
                        sview_diyProcedures.setText(messageProd);
                        sview_diyProcedures.setTextColor(Color.BLACK);


                        userData.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                final User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                if(diYnames.user_id.equals(user_profile.getUserID())) {
                                    user_name = user_profile.getF_name() + " " + user_profile.getL_name();
                                    diyOwner.setText(user_name);

                                    diyOwner.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(ViewRelatedDIYS.this, "Owner: " + " " + user_name, Toast.LENGTH_SHORT).show();
                                            //get seller info nya i set sa profile activity, put.Extra?
                                            Intent profileIntent = new Intent(ViewRelatedDIYS.this, OtherUserProfileActivity.class);

                                            profileIntent.putExtra("profileUserId", user_profile.getUserID());
                                            profileIntent.putExtra("profileUserFName", user_profile.getF_name());
                                            profileIntent.putExtra("profileUserLName", user_profile.getL_name());                                            profileIntent.putExtra("profileEmailAdd", user_profile.getEmail());
                                            profileIntent.putExtra("profileNumber", user_profile.getContact_no());
                                            profileIntent.putExtra("profileAddress", user_profile.getAddress());
                                            profileIntent.putExtra("profilePicture", user_profile.getUserProfileUrl());
                                            profileIntent.putExtra("profileRatings", user_profile.getUserRating());
                                            profileIntent.putExtra("reportedBy", loggedInUserName);
                                            profileIntent.putExtra("customerID", userID);
                                            startActivity(profileIntent);
                                        }
                                    });
                                    Log.e("user_owner_name", user_name);
                                    sellers_address.setText(user_profile.getAddress());
                                    sellers_contact_no.setText(user_profile.getContact_no());

                                    sellers_address.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(ViewRelatedDIYS.this, "Owner Address: " + user_profile.getAddress(),
                                                    Toast.LENGTH_SHORT).show();

                                            Intent addressIntent = new Intent(ViewRelatedDIYS.this, MapsActivity.class);
                                            addressIntent.putExtra("userLocation", sellers_address.getText().toString());
                                            Log.e("sellerAdd", sellers_address.getText().toString());
                                            startActivity(addressIntent);
                                        }
                                    });

                                }
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


                        contactSellerBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final Dialog myDialog = new Dialog(context);
                                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                myDialog.setContentView(R.layout.contact_seller);
                                myDialog.setCancelable(false);
                                Button chat = (Button) myDialog.findViewById(R.id.chat);
                                Button call = (Button) myDialog.findViewById(R.id.call);
                                Button sms = (Button) myDialog.findViewById(R.id.sms);
                                TextView cancel = (TextView) myDialog.findViewById(R.id.cancel);

                                chat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //get seller
//                                    if(diyInfo.getUser_id())
//                                    if(diyInfo.getUser_id().equals(loggedInName.getKey())){
                                        Intent intent = new Intent(ViewRelatedDIYS.this, ChatSplashActivity.class);
                                        startActivity(intent);
                                        Log.e("DATAKEY",loggedInName.getKey());
//                                    }
                                    }
                                });
                                call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String phone = sellers_contact_no.getText().toString();
                                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                                "tel", phone, null));
                                        startActivity(phoneIntent);
                                    }
                                });
                                sms.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String phone = sellers_contact_no.getText().toString();
                                        Intent smsMsgAppVar = new Intent(Intent.ACTION_VIEW);
                                        smsMsgAppVar.setData(Uri.parse("sms:" +  phone));
                                        smsMsgAppVar.putExtra("sms_body", "Hi, Good Day! ");
                                        startActivity(smsMsgAppVar);
                                    }
                                });
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myDialog.cancel();
                                    }
                                });

                                myDialog.show();
                                myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.cancel();
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                            }
                        });


                        if (diYnames.diyUrl != null) {
                            imgviewpager = (ViewPager) findViewById(R.id.related_view_pager);
                            viewImagesSearchPagerAdapter = new ViewImagesSearchPagerAdapter(getBaseContext(), diYnames.diyUrl);
                            imgviewpager.setAdapter(viewImagesSearchPagerAdapter);
                        }

                    }




                    else if (diYnames.getIdentity().equalsIgnoreCase("on sale!")){
                        String messageMat = "";
                        List<String> messageMaterials = new ArrayList<String>();
                        //int count = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                            String dbMaterialName = postSnapshot.child("name").getValue(String.class).toLowerCase();
                            String dbMaterialUnit = postSnapshot.child("unit").getValue(String.class);
                            Long dbMaterialQuantity = postSnapshot.child("quantity").getValue(Long.class);
//                                messageMat += "\n" + dbMaterialName + " = " + dbMaterialQuantity + " " + dbMaterialUnit;
                            messageMat += "\n" + dbMaterialQuantity + " " + dbMaterialUnit+ " " +dbMaterialName;
                            messageMaterials.add(messageMat);
                            //count++;
                        }

                        String[] splits = dataSnapshot.child("procedures").getValue().toString().split(",");
                        Log.e("splits", "" + splits);

                        String messageProd = "";
                        List<String> messageProcedure = new ArrayList<String>();
                        for (int i = 0; i < splits.length; i++) {
                            Log.d("splitVal", splits[i].substring(5, splits[i].length() - 1));
                            String message = i + 1 + ". " + splits[i].substring(5, splits[i].length() - 1).replaceAll("\\}", "").replaceAll("=", "");
                            messageProd += "\n" + message;
                            messageProcedure.add(message);
                            Log.d("messageProd", messageProd);
                        }

                        sview_diyMaterials.setText(messageMat);
//                            tv_procedures.setText("NOT APPLICABLE! BUY THE ITEM FIRST OR ASK PERMISSION TO THE OWNER!");
                        sview_diyProcedures.setText(messageProd);
                        sview_diyProcedures.setTextColor(Color.BLACK);


                        userData.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                final User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                if(diYnames.user_id.equals(user_profile.getUserID())) {
                                    user_name = user_profile.getF_name() + " " + user_profile.getL_name();
                                    diyOwner.setText(user_name);

                                    diyOwner.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Toast.makeText(ViewRelatedDIYS.this, "Owner: " + " " + user_name, Toast.LENGTH_SHORT).show();
                                            //get seller info nya i set sa profile activity, put.Extra?
                                            Intent profileIntent = new Intent(ViewRelatedDIYS.this, OtherUserProfileActivity.class);

                                            profileIntent.putExtra("profileUserId", user_profile.getUserID());
                                            profileIntent.putExtra("profileUserFName", user_profile.getF_name());
                                            profileIntent.putExtra("profileUserLName", user_profile.getL_name());                                            profileIntent.putExtra("profileEmailAdd", user_profile.getEmail());
                                            profileIntent.putExtra("profileNumber", user_profile.getContact_no());
                                            profileIntent.putExtra("profileAddress", user_profile.getAddress());
                                            profileIntent.putExtra("profilePicture", user_profile.getUserProfileUrl());
                                            profileIntent.putExtra("profileRatings", user_profile.getUserRating());
                                            profileIntent.putExtra("reportedBy", loggedInUserName);
                                            profileIntent.putExtra("customerID", userID);
                                            startActivity(profileIntent);
                                        }
                                    });

                                    Log.e("user_owner_name", user_name);
                                    sellers_address.setText(user_profile.getAddress());
                                    sellers_contact_no.setText(user_profile.getContact_no());
                                }
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


                        for(DataSnapshot insideDataSnapshot: dataSnapshot.child("itemPromo").getChildren()){
                            CreatePromo createPromo = insideDataSnapshot.getValue(CreatePromo.class);
                            promoPrice.setText(createPromo.getPromo_price());
                            outside_view.setImageResource(R.drawable.horizontal_line);
                        }

                        String message_price="";
                        String message_qty = "";
                        List<String> message_Price = new ArrayList<String>();
                        List<String> message_Qty = new ArrayList<String>();
                        for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                            double price = postSnapshot.child("selling_price").getValue(double.class);
                            int qty = postSnapshot.child("selling_qty").getValue(int.class);
                            message_qty += qty;
                            message_Qty.add(message_qty);
                            message_price += price;
                            message_Price.add(message_price);

                        }

                        sell_details.setText(message_price);
                        diyQty.setText(message_qty + " " + "piece/s");


//                        //BUY btn
//                        final String finalMessage_price = message_price;
//                        buyBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                relatedDiyReference.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        Toast.makeText(ViewRelatedDIYS.this, "Buy button clicked!", Toast.LENGTH_SHORT).show();
//                                        Float float_this = Float.valueOf(0);
//
//                                        DIYSell productInfo =  dataSnapshot.getValue(DIYSell.class);
//
//                                        String diyName = productInfo.getDiyName();
//                                        String diyUrl = productInfo.getDiyUrl();
//                                        String user_id = productInfo.getUser_id();
//                                        String productID = productInfo.getProductID();
//                                        String status = productInfo.getIdentity();
//
//                                        DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, status, float_this, float_this, "seller");
//                                        String upload_info = pendingReference.push().getKey();
//                                        pendingReference.child(upload_info).setValue(info);
//                                        pendingReference.child(upload_info).child("DIY Price").setValue(finalMessage_price);
//
//                                        final Dialog dialog = new Dialog(ViewRelatedDIYS.this);
//                                        dialog.setContentView(R.layout.done_dialog);
//                                        TextView text = (TextView) dialog.findViewById(R.id.text);
//                                        text.setText("DIY added to pending list!");
//                                        ImageView image = (ImageView) dialog.findViewById(R.id.dialog_imageview);
//                                        image.setImageResource(R.drawable.done);
//
//                                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
//                                        // if button is clicked, close the custom dialog
//                                        dialogButton.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = new Intent(ViewRelatedDIYS.this, MainActivity.class);
//                                                startActivity(intent);
//                                            }
//                                        });
//                                        dialog.show();
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//
//                            }
//                        });


                        contactSellerBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final Dialog myDialog = new Dialog(context);
                                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                myDialog.setContentView(R.layout.contact_seller);
                                myDialog.setCancelable(false);
                                Button chat = (Button) myDialog.findViewById(R.id.chat);
                                Button call = (Button) myDialog.findViewById(R.id.call);
                                Button sms = (Button) myDialog.findViewById(R.id.sms);
                                TextView cancel = (TextView) myDialog.findViewById(R.id.cancel);

                                chat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        //get seller
//                                    if(diyInfo.getUser_id())
//                                    if(diyInfo.getUser_id().equals(loggedInName.getKey())){
                                        Intent intent = new Intent(ViewRelatedDIYS.this, ChatSplashActivity.class);
                                        startActivity(intent);
                                        Log.e("DATAKEY",loggedInName.getKey());
//                                    }
                                    }
                                });
                                call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String phone = sellers_contact_no.getText().toString();
                                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                                "tel", phone, null));
                                        startActivity(phoneIntent);
                                    }
                                });
                                sms.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String phone = sellers_contact_no.getText().toString();
                                        Intent smsMsgAppVar = new Intent(Intent.ACTION_VIEW);
                                        smsMsgAppVar.setData(Uri.parse("sms:" +  phone));
                                        smsMsgAppVar.putExtra("sms_body", "Hi, Good Day! ");
                                        startActivity(smsMsgAppVar);
                                    }
                                });
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myDialog.cancel();
                                    }
                                });

                                myDialog.show();
                                myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                                            dialog.cancel();
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                            }
                        });



                        if (diYnames.diyUrl != null) {
                            imgviewpager = (ViewPager) findViewById(R.id.related_view_pager);
                            viewImagesSearchPagerAdapter = new ViewImagesSearchPagerAdapter(getBaseContext(), diYnames.diyUrl);
                            imgviewpager.setAdapter(viewImagesSearchPagerAdapter);
                        }
                    }
                }





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

    }



    private class ViewImagesSearchPagerAdapter extends PagerAdapter {

        private Context context;
        private String diyUrl;

        public ViewImagesSearchPagerAdapter(Context context, String diyUrl) {
            this.context = context;
            this.diyUrl = diyUrl;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            View currentView = LayoutInflater.from(context).inflate(R.layout.viewpager_product_images_commu, container, false);
            final ImageView diyImageView = (ImageView) currentView.findViewById(R.id.viewpager_productImage_community);
            try {
                final StorageReference diyImageStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(diyUrl);
                diyImageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("Image Download URI", uri.toString());
                        Glide.with(context).load(uri.toString())
                                .fitCenter().centerCrop().crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(diyImageView);

                    }
                });
            } catch (Exception e) {
                Log.d("Exception", "Getting diy image");
            }
            container.addView(currentView);
            return currentView;
        }


        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return diyUrl.length();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }




}
