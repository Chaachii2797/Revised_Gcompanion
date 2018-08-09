package cabiso.daphny.com.g_companion;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
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
import android.widget.DatePicker;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cabiso.daphny.com.g_companion.Adapter.BiddersAdapter;
import cabiso.daphny.com.g_companion.Adapter.RecyclerViewDataAdapter;
import cabiso.daphny.com.g_companion.Adapter.RelatedDIYAdapter;
import cabiso.daphny.com.g_companion.InstantMessaging.ui.activities.UserListingActivity;
import cabiso.daphny.com.g_companion.Model.Bidders;
import cabiso.daphny.com.g_companion.Model.CreatePromo;
import cabiso.daphny.com.g_companion.Model.DIYBidding;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SectionDataModel;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.Promo.PromoActivity;

/**
 * Created by Lenovo on 11/2/2017.
 */

public class DIYDetailViewActivity extends AppCompatActivity{

    private ProgressDialog progressDialog;

    private TextView diy_name, diy_materials, diy_procedures, diy_sell, php, user_owner_name,txtBy;
    private Button button_buy, contact_seller, create_promo, bid_item, want_to_bid;
    private String user_name;
    private CardView selling_price, seller_info, bid_info, cardview0, cardview00, cardview05, cardview06, cardview07;
    private RecyclerView recyclerView;
    private int currentNotificationID = 0;

    //owners statements
    private TextView ownr_txt, ownr_bid_price, tv_bid_price, ownr_bid_xpire, tv_bid_xpire, ownr_cmmnt, tv_ownr_cmmnt;

    //sellers info
    private TextView sellers_info, sellers_cnTV, sellers_addTV, sellers_contact_no, sellers_address;

    //promo
    private TextView promo_price, promo_textview;
    private ImageView outside_view;

    ////bidders
    private TextView tv_bidders;
    private RecyclerView lv_bidders;
    private Button btn_bidders;

    final Context context = this;
    List<String> item = new ArrayList<>();
    private List<Bidders> diyBiddings = new ArrayList<Bidders>();;
    private BiddersAdapter biddersAdapter;

    private DIYImagesViewPagerAdapter diyImagesViewPagerAdapter;
    private ViewPager diyImagesViewPager;
    private DatabaseReference user_data;
    private DatabaseReference loggedInName;
    private DatabaseReference identityReference;
    private DatabaseReference promoReference;
    private DatabaseReference pending_reference;
    private DatabaseReference winnersReference;
    private DatabaseReference biddersReference;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference databaseReference, itemReference;

    private String userID;
    private String loggedInUserName;

//    private FloatingActionButton fab_template;
    private RelatedDIYAdapter relatedDIYadapter;

    //related DIYs Arraylist
    ArrayList<SectionDataModel> allSampleData;
    private ArrayList<DIYnames> ePics = new ArrayList<>();
    private ArrayList<User_Profile> eOwner = new ArrayList<>();
    private ArrayList<String> ePrice = new ArrayList<>();

    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_data);
        String diyReferenceString = getIntent().getStringExtra("Community Ref");

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        /* Database References */
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(diyReferenceString);
        pending_reference = FirebaseDatabase.getInstance().getReference("DIY Pending Items").child(userID);
        user_data = FirebaseDatabase.getInstance().getReference().child("userdata");
        loggedInName = FirebaseDatabase.getInstance().getReference().child("userdata");
        itemReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        promoReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");


        allSampleData = new ArrayList<SectionDataModel>();
        ePics = new ArrayList<>();
        ePrice = new ArrayList<>();
        eOwner = new ArrayList<>();

        /* Toolbar Configurations */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarDetails);
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

        //FAB for adding related diys
//        fab_template = (FloatingActionButton) findViewById(R.id.fab_add_product);

        /* User Interface Initializations */
        recyclerView = (RecyclerView) findViewById(R.id.lv_bidders);
        recyclerView.setHasFixedSize(true);

        progressDialog = new ProgressDialog(this);
        diy_name = (TextView) findViewById(R.id.diy_name);
        diy_materials = (TextView) findViewById(R.id.diy_material);
        diy_procedures = (TextView) findViewById(R.id.diy_procedure);
        diy_sell = (TextView) findViewById(R.id.sell_details);
        user_owner_name = (TextView) findViewById(R.id.txt_user_owner_name);
        txtBy = (TextView) findViewById(R.id.txt_by);

        button_buy = (Button) findViewById(R.id.btn_sell_diy);
        want_to_bid = (Button) findViewById(R.id.btn_bidders_bid);
        button_buy = (Button) findViewById(R.id.btn_sell_diy);
        php = (TextView) findViewById(R.id.textView33);
        contact_seller = (Button) findViewById(R.id.btn_contact_diy_owner);
        create_promo = (Button) findViewById(R.id.btn_create_promo);
        bid_item = (Button) findViewById(R.id.btn_bid_item);

        //sellers info
        sellers_info = (TextView) findViewById(R.id.textView313);
        sellers_address = (TextView) findViewById(R.id.diy_owner_add);
        sellers_contact_no = (TextView) findViewById(R.id.diy_owner_cn);
        sellers_cnTV = (TextView) findViewById(R.id.cnTV);
        sellers_addTV = (TextView) findViewById(R.id.addTV);

        //owners statement
        ownr_txt = (TextView) findViewById(R.id.owners_statement);
        ownr_bid_xpire = (TextView) findViewById(R.id.bidding_expiration);
        ownr_bid_price = (TextView) findViewById(R.id.bidding_price);
        ownr_cmmnt = (TextView) findViewById(R.id.bidding_cmmnt);
        tv_bid_xpire = (TextView) findViewById(R.id.et_bidding_expiration);
        tv_bid_price = (TextView) findViewById(R.id.et_bidding_price);
        tv_ownr_cmmnt = (TextView) findViewById(R.id.et_bidding_commnt);

        //Promo Price
        promo_price = (TextView) findViewById(R.id.tv_new_price_Promo);
        promo_textview = (TextView) findViewById(R.id.textView34);
        outside_view = (ImageView) findViewById(R.id.outside_imageview);

        cardview0 = (CardView) findViewById(R.id.cardView);
        cardview00 = (CardView) findViewById(R.id.cardView2);
        selling_price = (CardView) findViewById(R.id.cardView3);
        seller_info = (CardView) findViewById(R.id.cardView4);
        cardview05 = (CardView) findViewById(R.id.cardView5); //bidders
        cardview06 = (CardView) findViewById(R.id.cardView6); //seller bid info
        cardview07 = (CardView) findViewById(R.id.cardView7); //related DIYS

        /* Bidding Lists */
        biddersAdapter = new BiddersAdapter(DIYDetailViewActivity.this,diyBiddings);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(biddersAdapter);

        //for related DIYS
        allSampleData = new ArrayList<SectionDataModel>();

        //bidders
        tv_bidders = (TextView) findViewById(R.id.tv_bidders);
        lv_bidders = (RecyclerView) findViewById(R.id.lv_bidders);
        btn_bidders = (Button) findViewById(R.id.btn_bidders_bid);

//        createDummyData();
        final RecyclerView relatedDIYrecyclerView = (RecyclerView) findViewById(R.id.relatedDIYrecyclerView);
        relatedDIYrecyclerView.setHasFixedSize(true);

        final RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this, allSampleData);

        relatedDIYrecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        relatedDIYrecyclerView.setAdapter(adapter);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot itemSnapshot) {
                final DIYnames diyInfo = itemSnapshot.getValue(DIYnames.class);
                final DIYSell info = itemSnapshot.getValue(DIYSell.class);
                identityReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(itemSnapshot.getKey());

                if(diyInfo.getIdentity().equals("community")){
                    diy_sell.setVisibility(View.INVISIBLE);
                    button_buy.setVisibility(View.INVISIBLE);
                    contact_seller.setVisibility(View.INVISIBLE);
                    create_promo.setVisibility(View.INVISIBLE);
                    php.setVisibility(View.INVISIBLE);
                    sellers_address.setVisibility(View.INVISIBLE);
                    sellers_contact_no.setVisibility(View.INVISIBLE);
                    selling_price.setVisibility(View.INVISIBLE);
                    seller_info.setVisibility(View.INVISIBLE);
                    user_owner_name.setVisibility(View.VISIBLE);
                    txtBy.setVisibility(View.VISIBLE);
                    bid_item.setVisibility(View.INVISIBLE);
                    diy_name.setText(diyInfo.diyName);
                    cardview07.setVisibility(View.VISIBLE);
                    relatedDIYrecyclerView.setVisibility(View.VISIBLE);
                    cardview05.setVisibility(View.INVISIBLE);
                    cardview06.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);

                    //promo
                    promo_price.setVisibility(View.INVISIBLE);
                    promo_textview.setVisibility(View.INVISIBLE);

                    //bidders
                    tv_bidders.setVisibility(View.INVISIBLE);
                    lv_bidders.setVisibility(View.INVISIBLE);
                    btn_bidders.setVisibility(View.INVISIBLE);

                    RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    nlp.topMargin = 1200;
                    nlp.bottomMargin = 15;
                    nlp.leftMargin = 20;
                    nlp.rightMargin = 20;
                    cardview07.setLayoutParams(nlp);

                    //Display related DIYS
                    SectionDataModel dm = new SectionDataModel();
                    dm.setHeaderTitle("Related DIYS: ");
                    dm.setAllItemsInSection(ePrice);
                    dm.setAllProfileInSection(eOwner);
                    dm.setAllPicturesInSection(ePics);

                    for (DataSnapshot postDataSnapshot: itemSnapshot.child("related_diys").getChildren()) {
                        DIYnames diynem = postDataSnapshot.getValue(DIYnames.class);
                        Log.e("diynem", String.valueOf(diynem));
                        if(diyInfo.diyName.equals(diynem.getDiyName())){
                            Toast.makeText(DIYDetailViewActivity.this, "Loading Related DIYS...", Toast.LENGTH_SHORT).show();//

                            dm.addItemInSection(postDataSnapshot.child("DIY Price").getValue().toString());
                            dm.addProfileInSection(postDataSnapshot.getValue(User_Profile.class));
                            dm.addPictureInSection(postDataSnapshot.getValue(DIYnames.class));

                            Log.e("dm", String.valueOf(dm));
                            Log.e("allSampleData", String.valueOf(allSampleData));
                            Log.e("allPrice", String.valueOf(ePrice));
                            Log.e("allOwner", String.valueOf(eOwner));
                            Log.e("allPics", String.valueOf(ePics));

                        }
                    }
                    allSampleData.add(dm);
                    adapter.notifyDataSetChanged();


                    user_data.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                            if(diyInfo.user_id.equals(user_profile.getUserID())){
                                user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                user_owner_name.setText(user_name);
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

//                    fab_template.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if(diyInfo.user_id.equals(userID)){
//                                Toast.makeText(DIYDetailViewActivity.this,"This is your item!", Toast.LENGTH_SHORT).show();
//                            }
//                            else {
//                                Toast.makeText(DIYDetailViewActivity.this, "Please complete this.", Toast.LENGTH_SHORT).show();
//                                Intent in = new Intent(DIYDetailViewActivity.this, AddSameDIYTemplate.class);
//                                in.putExtra("sameItemId", itemSnapshot.getKey());
//                                Log.e("sameItemId",itemSnapshot.getKey());
//
//                                in.putExtra("itemProdName", diyInfo.diyName);
//                                Log.e("itemProdName", diyInfo.diyName);
//                                startActivity(in);
//                            }
//                        }
//                    });


                    String messageMat = "";
                    List<String> messageMaterials = new ArrayList<String>();
                    int count = 1;
                    for (DataSnapshot postSnapshot : itemSnapshot.child("materials").getChildren()) {
                        String material_name = postSnapshot.child("name").getValue(String.class).toUpperCase();
                        Long material_qty = postSnapshot.child("quantity").getValue(Long.class);
                        String material_unit = postSnapshot.child("unit").getValue(String.class);
                        Log.e("message", "" + material_name);
                        messageMat += "\n" +  material_qty + " " + material_unit+ " " + material_name ;
                        messageMaterials.add(material_name);
                        count++;
                    }

                    String[] splits = itemSnapshot.child("procedures").getValue().toString().split(",");
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

                    Log.d("MessageProcedure", messageProcedure.toString());

                    diy_materials.setText(messageMat);
                    diy_procedures.setText(messageProd);

                    diy_materials.setFocusable(true);
                    diy_materials.setEnabled(true);
                    diy_materials.setClickable(true);
                    diy_materials.setFocusableInTouchMode(true);

                    Log.d("SnapItem", "not null");
                    Log.d("SnapMaterial", "" + item);
                    Log.d("SnapDataSnap", "" + itemSnapshot.child("materials").getValue());

                    if (diyInfo.diyUrl != null) {
                        diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                        diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), diyInfo.diyUrl);
                        diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);
                    }
                }

                else if(diyInfo.getIdentity().equalsIgnoreCase("selling")){
                    diy_sell.setVisibility(View.VISIBLE);
                    user_owner_name.setVisibility(View.VISIBLE);
                    php.setVisibility(View.VISIBLE);
                    sellers_address.setVisibility(View.VISIBLE);
                    sellers_contact_no.setVisibility(View.VISIBLE);
                    selling_price.setVisibility(View.VISIBLE);
                    seller_info.setVisibility(View.VISIBLE);
                    diy_name.setText(info.diyName);
//                    button_buy.setVisibility(View.VISIBLE);
//                    fab_template.setVisibility(View.INVISIBLE);
                    cardview07.setVisibility(View.INVISIBLE);
                    relatedDIYrecyclerView.setVisibility(View.INVISIBLE);
                    cardview05.setVisibility(View.INVISIBLE);
                    cardview06.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);

                    bid_item.setVisibility(View.INVISIBLE);
                    create_promo.setVisibility(View.INVISIBLE);
                    //promo
                    promo_price.setVisibility(View.INVISIBLE);
                    promo_textview.setVisibility(View.INVISIBLE);
                    cardview0.setVisibility(View.INVISIBLE);//materials
                    cardview00.setVisibility(View.INVISIBLE);//procedures


                    RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    nlp.topMargin = 700;
                    nlp.bottomMargin = 15;
                    nlp.leftMargin = 20;
                    nlp.rightMargin = 20;
                    selling_price.setLayoutParams(nlp);

                    RelativeLayout.LayoutParams adjs = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    adjs.topMargin = 1100;
                    adjs.bottomMargin = 15;
                    adjs.leftMargin = 20;
                    adjs.rightMargin = 20;
                    contact_seller.setLayoutParams(adjs);

                    RelativeLayout.LayoutParams adjust = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    adjust.topMargin = 1210;
                    adjust.bottomMargin = 20;
                    adjust.leftMargin = 20;
                    adjust.rightMargin = 20;
                    button_buy.setLayoutParams(adjust);

                    Log.e("userDataID",userID);
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

                    user_data.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                            if(diyInfo.user_id.equals(user_profile.getUserID())){
                                user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                user_owner_name.setText(user_name);
                                sellers_address.setText(user_profile.getAddress());
                                sellers_contact_no.setText(user_profile.getContact_no());
                            }
                            if(diyInfo.user_id.equals(userID)){
                                button_buy.setVisibility(View.INVISIBLE);
                                contact_seller.setVisibility(View.INVISIBLE);
                                create_promo.setVisibility(View.VISIBLE);
                                bid_item.setVisibility(View.VISIBLE);
                            }else{
                                button_buy.setVisibility(View.VISIBLE);
                                contact_seller.setVisibility(View.VISIBLE);
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

                    contact_seller.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(DIYDetailViewActivity.this, "Contact Seller button clicked!", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(DIYDetailViewActivity.this, "Chat button clicked!", Toast.LENGTH_SHORT).show();

//                                    if(diyInfo.getUser_id())
                                    if(diyInfo.getUser_id().equals(loggedInName.getKey())){
                                        Intent intent = new Intent(DIYDetailViewActivity.this, UserListingActivity.class);
                                        startActivity(intent);
                                        sendNotification();
                                        Log.e("DATAKEY",loggedInName.getKey());
                                    }
                                }
                            });
                            call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(DIYDetailViewActivity.this, "Call button clicked!", Toast.LENGTH_SHORT).show();

                                    String phone = sellers_contact_no.getText().toString();
                                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                            "tel", phone, null));
                                    startActivity(phoneIntent);
                                }
                            });
                            sms.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(DIYDetailViewActivity.this, "SMS button clicked!", Toast.LENGTH_SHORT).show();

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

                    for (DataSnapshot insideDataSnapshot: itemSnapshot.child("bidding").getChildren()) {
                        DIYBidding biddingItem = insideDataSnapshot.getValue(DIYBidding.class);
                        tv_bid_xpire.setText(biddingItem.getDate());
                        tv_bid_price.setText(biddingItem.getIntial_price());
                        tv_ownr_cmmnt.setText(biddingItem.getMessage());
                    }

                    for (DataSnapshot inside_DataSnapshot: itemSnapshot.child("bidders").getChildren()) {
                        Bidders biddersItem = inside_DataSnapshot.getValue(Bidders.class);
                        diyBiddings.add(biddersItem);
                        biddersAdapter.notifyDataSetChanged();
                    }

                    String message_price="";
                    List<String> message_Price = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : itemSnapshot.child("DIY Price").getChildren()) {
                        double price = postSnapshot.child("selling_price").getValue(double.class);
                        message_price += price;
                        message_Price.add(message_price);

                    }

                    diy_sell.setText(message_price);

                    if (diyInfo.diyUrl != null) {
                        diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                        diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), diyInfo.diyUrl);
                        diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);
                    }

                    create_promo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent to_promo = new Intent(DIYDetailViewActivity.this,PromoActivity.class);
                            to_promo.putExtra("diy_Name",diyInfo.getDiyName());
                            Log.e("NAMEEEE", diyInfo.getDiyName());
                            startActivity(to_promo);
                        }
                    });

//                    create_promo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            final Dialog myDialog = new Dialog(context);
//                            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                            myDialog.setContentView(R.layout.dialog_promo);
//                            myDialog.show();
//
//                            final EditText promo_end_date = (EditText) myDialog.findViewById(R.id.et_promo_end_date);
//                            final EditText promo_price = (EditText) myDialog.findViewById(R.id.et_promo_prce);
//                            Button ok = (Button) myDialog.findViewById(R.id.btn_ok);
//                            final Calendar calendar = Calendar.getInstance();
//                            final int year = calendar.get(Calendar.YEAR);
//                            final int month = calendar.get(Calendar.MONTH);
//                            final int date = calendar.get(Calendar.DAY_OF_MONTH);
//
//                            final String price = String.valueOf(promo_price.getText().toString());
//                            promo_end_date.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    final DatePickerDialog datePickerDialog = new DatePickerDialog(DIYDetailViewActivity.this,
//                                            new DatePickerDialog.OnDateSetListener() {
//                                        @Override
//                                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                                            String set_end_date = year + "-" + String.valueOf(month + 1) + "-" + (dayOfMonth);
//                                            promo_end_date.setText(set_end_date);
//                                        }
//                                    }, year, month, date);
//                                    datePickerDialog.show();
//                                }
//                            });
//                            ok.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    CreatePromo createPromo = new CreatePromo();
//                                    promoReference.child(itemSnapshot.getKey()).child("itemPromo").push()
//                                            .setValue((createPromo.setPromo_userID(userID).setUser_promo_name(loggedInUserName)
//                                                    .setPromo_price(promo_price.getText().toString()).setPromo_start(sdate)
//                                                    .setPromo_ends(promo_end_date.getText().toString())));
//
//                                    HashMap<String, Object> result = new HashMap<>();
//                                    result.put("identity", "ON SALE!");
//                                    identityReference.updateChildren(result);
//                                    myDialog.cancel();
//                                }
//                            });
//                            Toast.makeText(context, "CREATE PROMO BUTTON CLICKED", Toast.LENGTH_SHORT).show();
//                        }
//                    });

                    bid_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent item = new Intent(DIYDetailViewActivity.this,ToBidProduct.class);
                            item.putExtra("itemId", itemSnapshot.getKey());
                            startActivity(item);
                        }
                    });

                }else if(diyInfo.getIdentity().equalsIgnoreCase("on sale!")){
                    diy_sell.setVisibility(View.VISIBLE);
                    user_owner_name.setVisibility(View.VISIBLE);
                    php.setVisibility(View.VISIBLE);
                    sellers_address.setVisibility(View.VISIBLE);
                    sellers_contact_no.setVisibility(View.VISIBLE);
                    selling_price.setVisibility(View.VISIBLE);
                    seller_info.setVisibility(View.VISIBLE);
                    diy_name.setText(info.diyName);

//                    fab_template.setVisibility(View.INVISIBLE);
                    cardview07.setVisibility(View.INVISIBLE);
                    relatedDIYrecyclerView.setVisibility(View.INVISIBLE);
                    cardview05.setVisibility(View.INVISIBLE);
                    cardview06.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    cardview0.setVisibility(View.INVISIBLE);//materials
                    cardview00.setVisibility(View.INVISIBLE);//procedures

                    RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    nlp.topMargin = 700;
                    nlp.bottomMargin = 15;
                    nlp.leftMargin = 25;
                    nlp.rightMargin = 25;
                    selling_price.setLayoutParams(nlp);

                    RelativeLayout.LayoutParams ad = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    ad.topMargin = 1100;
                    ad.bottomMargin = 15;
                    ad.leftMargin = 20;
                    ad.rightMargin = 20;
                    contact_seller.setLayoutParams(ad);

                    RelativeLayout.LayoutParams adj = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    adj.topMargin = 1210;
                    adj.bottomMargin = 20;
                    adj.leftMargin = 20;
                    adj.rightMargin = 20;
                    button_buy.setLayoutParams(adj);

                    RelativeLayout.LayoutParams adju = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    adju.topMargin = 1320;
                    adju.bottomMargin = 20;
                    adju.leftMargin = 20;
                    adju.rightMargin = 20;
                    create_promo.setLayoutParams(adju);

                    RelativeLayout.LayoutParams adjus = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    adjus.topMargin = 1430;
                    adjus.bottomMargin = 20;
                    adjus.leftMargin = 20;
                    adjus.rightMargin = 20;
                    bid_item.setLayoutParams(adjus);

                    user_data.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                            if(diyInfo.user_id.equals(user_profile.getUserID())){
                                user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                user_owner_name.setText(user_name);
                                sellers_address.setText(user_profile.getAddress());
                                sellers_contact_no.setText(user_profile.getContact_no());

                            }
                            if(diyInfo.user_id.equals(userID)){
                                button_buy.setVisibility(View.VISIBLE);
                                contact_seller.setVisibility(View.VISIBLE);
                                bid_item.setVisibility(View.INVISIBLE);
                                create_promo.setVisibility(View.INVISIBLE);
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

                    for(DataSnapshot saleSnapshot:itemSnapshot.child("itemPromo").getChildren()){
                        CreatePromo createPromo = saleSnapshot.getValue(CreatePromo.class);

                        try{
                            // check expiry
                            Date strDate = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).parse(createPromo.getPromo_ends());
                            if (new Date().after(strDate)) {
                                isExpired = true;
                            }
                        } catch(ParseException e){
                            Log.e("expiryException",e.getMessage());
                        }

                    }

                    if(isExpired){
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("identity", "Selling");
                        identityReference.updateChildren(result);

                        outside_view.setImageResource(0);

                    }

                    contact_seller.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(DIYDetailViewActivity.this, "Contact Seller button clicked!", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(DIYDetailViewActivity.this, "Chat button clicked!", Toast.LENGTH_SHORT).show();
                                    sendNotification();
                                }
                            });
                            call.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(DIYDetailViewActivity.this, "Call button clicked!", Toast.LENGTH_SHORT).show();

                                    String phone = sellers_contact_no.getText().toString();
                                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                            "tel", phone, null));
                                    startActivity(phoneIntent);
                                }
                            });
                            sms.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(DIYDetailViewActivity.this, "SMS button clicked!", Toast.LENGTH_SHORT).show();

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

                    for(DataSnapshot insideDataSnapshot: itemSnapshot.child("itemPromo").getChildren()){
                        CreatePromo createPromo = insideDataSnapshot.getValue(CreatePromo.class);
                        promo_price.setText(createPromo.getPromo_price());
                        outside_view.setImageResource(R.drawable.horizontal_line);
                    }

                    String message_price="";
                    List<String> message_Price = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : itemSnapshot.child("DIY Price").getChildren()) {
                        double price = postSnapshot.child("selling_price").getValue(double.class);
                        message_price += price;
                        message_Price.add(message_price);
                    }

                    diy_sell.setText(message_price);

                    if (diyInfo.diyUrl != null) {
                        diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                        diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), diyInfo.diyUrl);
                        diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);
                    }
                }

                //BIDDDDDDDDDINGGGGGGGGGGGGGG
                else if(diyInfo.getIdentity().equalsIgnoreCase("on bid!")){
                    diy_sell.setVisibility(View.VISIBLE);
                    user_owner_name.setVisibility(View.VISIBLE);
                    create_promo.setVisibility(View.INVISIBLE);
                    php.setVisibility(View.VISIBLE);
                    sellers_address.setVisibility(View.VISIBLE);
                    sellers_contact_no.setVisibility(View.VISIBLE);
                    selling_price.setVisibility(View.VISIBLE);
                    seller_info.setVisibility(View.VISIBLE);
                    diy_name.setText(info.diyName);
                    contact_seller.setVisibility(View.VISIBLE);
                    bid_item.setVisibility(View.INVISIBLE);
//                    fab_template.setVisibility(View.INVISIBLE);
                    cardview07.setVisibility(View.INVISIBLE);
                    relatedDIYrecyclerView.setVisibility(View.INVISIBLE);
                    cardview0.setVisibility(View.INVISIBLE);//materials
                    cardview00.setVisibility(View.INVISIBLE);//procedures

                    //owners statement
                    ownr_txt.setVisibility(View.VISIBLE);
                    ownr_bid_xpire.setVisibility(View.VISIBLE);
                    ownr_bid_price.setVisibility(View.VISIBLE);
                    ownr_cmmnt.setVisibility(View.VISIBLE);
                    tv_bid_xpire.setVisibility(View.VISIBLE);
                    tv_bid_price.setVisibility(View.VISIBLE);
                    tv_ownr_cmmnt.setVisibility(View.VISIBLE);

                    //bidders
                    tv_bidders.setVisibility(View.VISIBLE);
                    lv_bidders.setVisibility(View.VISIBLE);
                    btn_bidders.setVisibility(View.VISIBLE);

                    //promo
                    promo_price.setVisibility(View.INVISIBLE);
                    promo_textview.setVisibility(View.INVISIBLE);

//                    cardview0.setVisibility(View.INVISIBLE);
//                    cardview00.setVisibility(View.INVISIBLE);
//                    selling_price.setVisibility(View.INVISIBLE);
//                    seller_info.setVisibility(View.INVISIBLE);

                    RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    nlp.topMargin = 700;
                    nlp.bottomMargin = 15;
                    nlp.leftMargin = 20;
                    nlp.rightMargin = 20;
                    selling_price.setLayoutParams(nlp);

                    boolean isExpired = false;

                    Log.e("userDataID",userID);
                    user_data.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                            loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    user_data.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                            if(diyInfo.user_id.equals(user_profile.getUserID())){
                                user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                user_owner_name.setText(user_name);
                                Log.e("","IIDIDD"+diyInfo.user_id+" "+"equals"+" "+user_profile.getUserID());
                                sellers_contact_no.setText(user_profile.getContact_no());
                                sellers_address.setText(user_profile.getAddress());
                            }
                            if(diyInfo.user_id.equals(userID)){
                                button_buy.setVisibility(View.INVISIBLE);
                                contact_seller.setVisibility(View.INVISIBLE);
                            }
                            else{
                                button_buy.setVisibility(View.VISIBLE);
                                contact_seller.setVisibility(View.VISIBLE);
                                bid_item.setVisibility(View.INVISIBLE);
                                create_promo.setVisibility(View.INVISIBLE);
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
                    List<String> message_Price = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : itemSnapshot.child("DIY Price").getChildren()) {
                        double price= postSnapshot.child("selling_price").getValue(double.class);
                        message_price += price;
                        message_Price.add(message_price);

                    }

                    for (DataSnapshot insideDataSnapshot: itemSnapshot.child("bidding").getChildren()) {
                        DIYBidding biddingItem = insideDataSnapshot.getValue(DIYBidding.class);
                        tv_bid_xpire.setText(biddingItem.getXpire_date());
                        tv_bid_price.setText(biddingItem.getIntial_price()+"");
                        tv_ownr_cmmnt.setText(biddingItem.getMessage());
                        Log.e("expiryDate",biddingItem.getXpire_date());
                        try{
                            // check expiry
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                            Date strDate = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).parse(biddingItem.getXpire_date());
                            if (new Date().after(strDate)) {
                                isExpired = true;
                            }
                        } catch(ParseException e){
                            Log.e("expiryException",e.getMessage());
                        }
                        Log.e("CHECKEXPIRATION",isExpired+"");
                    }

                    for (DataSnapshot inside_DataSnapshot: itemSnapshot.child("bidders").getChildren()) {
                        Bidders biddersItem = inside_DataSnapshot.getValue(Bidders.class);
                        diyBiddings.add(biddersItem);
                        biddersAdapter.notifyDataSetChanged();
                    }

                    winnersReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags")
                            .child(itemSnapshot.getKey());
                    Query highestBid = winnersReference.child("bidders").orderByChild("bid_price").limitToLast(1);
                    if(isExpired){
                        want_to_bid.setVisibility(View.INVISIBLE);
                        highestBid.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot insideSnapshot:dataSnapshot.getChildren()){
                                    Toast.makeText(DIYDetailViewActivity.this,insideSnapshot.child("bid_price").getValue().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    want_to_bid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(diyInfo.user_id.equals(userID)){
                                Toast.makeText(DIYDetailViewActivity.this,"THIS IS YOUR ITEM!!!", Toast.LENGTH_SHORT).show();
                            }else{
                                biddersReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(itemSnapshot.getKey())
                                        .child("bidders");
                                final Dialog dialog = new Dialog(DIYDetailViewActivity.this);
                                dialog.setContentView(R.layout.bidders);

                                Button bidders_dialog = (Button) dialog.findViewById(R.id.btn_bidders_dialog);
                                bidders_dialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        EditText bidders_value = (EditText) dialog.findViewById(R.id.et_bidders_price);
                                        Log.e("value_price",bidders_value.getText().toString());
                                        Log.e("value_datasnapshot",itemSnapshot.getKey());
                                        Log.e("database_value",bidders_value.getText().toString()+ " "+user_name);
                                        biddersReference.push().setValue(new Bidders(bidders_value.getText().toString()
                                                ,userID, loggedInUserName, sdate));

                                        Intent intent = new Intent(DIYDetailViewActivity.this, DIYDetailViewActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                dialog.show();
                            }
                        }
                    });

                    bid_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent item = new Intent(DIYDetailViewActivity.this,ToBidProduct.class);
                            item.putExtra("itemId", itemSnapshot.getKey());
                            startActivity(item);
                        }
                    });

                    diy_sell.setText(message_price);

                    if (diyInfo.diyUrl != null) {
                        diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                        diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), diyInfo.diyUrl);
                        diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    public void createDummyData() {
//        Toast.makeText(DIYDetailViewActivity.this, "Loading Related DIYS...", Toast.LENGTH_SHORT).show();
//
//        itemReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                DIYnames diynem = dataSnapshot.getValue(DIYnames.class);
//                User_Profile owner =dataSnapshot.getValue(User_Profile.class);
//
//                SectionDataModel dm = new SectionDataModel();
//                dm.setHeaderTitle("Related DIYS: ");
//
//
//                ePrice.add(dataSnapshot.child("DIY Price").getValue().toString());
//                eOwner.add(dataSnapshot.getValue(User_Profile.class));
//                ePics.add(dataSnapshot.getValue(DIYnames.class));
//
//                dm.setAllItemsInSection(ePrice);
//                dm.setAllProfileInSection(eOwner);
//                dm.setAllPicturesInSection(ePics);
//
//                allSampleData.add(dm);
//                Log.e("dm", String.valueOf(dm));
//                Log.e("allSampleData", String.valueOf(allSampleData));
//
//                Log.e("allPrice", String.valueOf(ePrice));
//                Log.e("allOwner", String.valueOf(eOwner));
//                Log.e("allPics", String.valueOf(ePics));
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    public void sendNotification() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.new_logo_green2)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentTitle("Notification")
                        .setContentText("Chat Seller!");

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        currentNotificationID++;
        int notificationId = currentNotificationID;
        if (notificationId == Integer.MAX_VALUE - 1)
            notificationId = 0;

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //ma  display sa notification bar
        mNotificationManager.notify(notificationId, notification);

        currentNotificationID = 500;

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private class DIYImagesViewPagerAdapter extends PagerAdapter {

        private Context context;
        private String diyUrl;

        public DIYImagesViewPagerAdapter(Context context, String diyUrl) {
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
                        //Picasso.with(context).load(uri.getLastPathSegment()).resize(350,350).into(productImageView);
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

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        private EditText text;

        public DatePickerFragment(){

        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int start_year = calendar.get(Calendar.YEAR);
            int start_month = calendar.get(Calendar.MONTH);
            int start_day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(),this,start_year,start_month,start_day);
            return  dpd;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//            EditText startDate = (EditText) view.findViewById(R.id.et_start_date);
            SimpleDateFormat dateFormatter;
            dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, dayOfMonth);
            EditText text = (EditText) this.getArguments().getSerializable("editText");
            text.setText(dateFormatter.format(newDate.getTime()));
//            startDate.setText(dateFormatter.format(newDate.getTime()));

        }

    }

}