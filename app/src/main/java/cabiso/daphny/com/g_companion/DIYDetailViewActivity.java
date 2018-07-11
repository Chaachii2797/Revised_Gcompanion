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
import android.support.design.widget.FloatingActionButton;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cabiso.daphny.com.g_companion.Adapter.BiddersAdapter;
import cabiso.daphny.com.g_companion.Adapter.RecyclerViewDataAdapter;
import cabiso.daphny.com.g_companion.Adapter.RelatedDIYAdapter;
import cabiso.daphny.com.g_companion.Model.Bidders;
import cabiso.daphny.com.g_companion.Model.DIYBidding;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SectionDataModel;
import cabiso.daphny.com.g_companion.Model.User_Profile;

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
    private DatabaseReference pending_reference;
    private DatabaseReference user_data;
    private DatabaseReference itemReference;
    private DatabaseReference biddersReference;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference databaseReference, newDatabaseRef;

    private String userID;
    private SimpleDateFormat dateFormatter;
    private int start_year, start_month, start_day;
    private int end_year, end_month, end_day;
    static final int DATE_ID = 0;

    private FloatingActionButton fab_template;
    private RelatedDIYAdapter relatedDIYadapter;

    //private ArrayList<DIYnames> relatedDiyList;
    ArrayList<SectionDataModel> allSampleData;

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
        itemReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        newDatabaseRef = FirebaseDatabase.getInstance().getReference().child("same_diy_product");

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

        fab_template = (FloatingActionButton) findViewById(R.id.fab_add_product);
        fab_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(DIYDetailViewActivity.this, AddSameDIYTemplate.class);
                startActivity(in);
            }
        });

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

        createDummyData();
        final RecyclerView relatedDIYrecyclerView = (RecyclerView) findViewById(R.id.relatedDIYrecyclerView);

        relatedDIYrecyclerView.setHasFixedSize(true);

        RecyclerViewDataAdapter adapter = new RecyclerViewDataAdapter(this, allSampleData);
        relatedDIYrecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        relatedDIYrecyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final DIYnames diyInfo = dataSnapshot.getValue(DIYnames.class);
                final DIYSell info = dataSnapshot.getValue(DIYSell.class);
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

//                    newDatabaseRef.addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            Toast.makeText(DIYDetailViewActivity.this, "Loading Related DIYS...", Toast.LENGTH_SHORT).show();
//                            DIYnames diys =  dataSnapshot.getValue(DIYnames.class);
//
//                            DIYnames dataDIys = new DIYnames();
//
//                            String diy_name = diys.getDiyName();
//                            String diy_url = diys.getDiyUrl();
//
//                            dataDIys.setDiyName(diy_name);
//                            dataDIys.setDiyUrl(diy_url);
//
//                            SectionDataModel dm = new SectionDataModel();
//                            dm.setHeaderTitle("Related DIYs:" );
//
//                            ArrayList<DIYnames> singleItem = new ArrayList<>();
//
//                            for (int j = 0; j <= 1; j++) {
//                                dm.setAllItemsInSection(singleItem);
//                                singleItem.add(dataDIys);
//                                Log.e("j ", String.valueOf(j));
//
//                            }
//
//                            allSampleData.add(dm);
//
//                            Log.e("singleItem", String.valueOf(singleItem));
//                            Log.e("dataDIys", String.valueOf(dataDIys));
//                            Log.e("diys", String.valueOf(diys));
//
//                        }
//
//
//                        @Override
//                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });


                    String messageMat = "";
                    List<String> messageMaterials = new ArrayList<String>();
                    int count = 1;
                    for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                        String material_name = postSnapshot.child("name").getValue(String.class).toUpperCase();
                        Long material_qty = postSnapshot.child("quantity").getValue(Long.class);
                        String material_unit = postSnapshot.child("unit").getValue(String.class);
                        Log.e("message", "" + material_name);
                        messageMat += "\n" +  material_qty + " " + material_unit+ " " + material_name ;
                        messageMaterials.add(material_name);
                        count++;
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

                    Log.d("MessageProcedure", messageProcedure.toString());

                    diy_materials.setText(messageMat);
                    diy_procedures.setText(messageProd);

                    Log.d("SnapItem", "not null");
                    Log.d("SnapMaterial", "" + item);
                    Log.d("SnapDataSnap", "" + dataSnapshot.child("materials").getValue());

                    if (diyInfo.diyUrl != null) {
                        diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                        diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), diyInfo.diyUrl);
                        diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);
                    }
                }

                else if(diyInfo.getIdentity().equals("selling")){
                    diy_sell.setVisibility(View.VISIBLE);
                    user_owner_name.setVisibility(View.VISIBLE);
                    create_promo.setVisibility(View.VISIBLE);
                    php.setVisibility(View.VISIBLE);
                    sellers_address.setVisibility(View.VISIBLE);
                    sellers_contact_no.setVisibility(View.VISIBLE);
                    selling_price.setVisibility(View.VISIBLE);
                    seller_info.setVisibility(View.VISIBLE);
                    diy_name.setText(info.diyName);
//                    button_buy.setVisibility(View.VISIBLE);
                    contact_seller.setVisibility(View.VISIBLE);
                    bid_item.setVisibility(View.VISIBLE);
                    fab_template.setVisibility(View.INVISIBLE);
                    cardview07.setVisibility(View.INVISIBLE);
                    relatedDIYrecyclerView.setVisibility(View.INVISIBLE);
                    cardview05.setVisibility(View.VISIBLE);
                    cardview06.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);

//                    RelativeLayout.LayoutParams nlp = new RelativeLayout.LayoutParams(
//                            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                    nlp.topMargin = 1200;
//                    nlp.bottomMargin = 15;
//                    nlp.leftMargin = 20;
//                    nlp.rightMargin = 20;
//                    cardview07.setLayoutParams(nlp);


                    user_data.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                            if(diyInfo.user_id.equals(user_profile.getUserID())){
                                user_name = user_profile.getF_name()+" "+user_profile.getL_name();
                                user_owner_name.setText(user_name);
                                sellers_address.setText(user_profile.getContact_no());
                                sellers_contact_no.setText(user_profile.getAddress());
                            }
                            if(diyInfo.user_id.equals(userID)){
                                button_buy.setVisibility(View.INVISIBLE);
                                contact_seller.setVisibility(View.INVISIBLE);
                                bid_item.setVisibility(View.VISIBLE);
                                create_promo.setVisibility(View.VISIBLE);
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

                    for (DataSnapshot insideDataSnapshot: dataSnapshot.child("bidding").getChildren()) {
                        DIYBidding biddingItem = insideDataSnapshot.getValue(DIYBidding.class);
                        tv_bid_xpire.setText(biddingItem.getDate());
                        tv_bid_price.setText(biddingItem.getPrice_min()+" "+"to"+" "+biddingItem.getPrice_max());
                        tv_ownr_cmmnt.setText(biddingItem.getMessage());
                    }
//                    Log.e("letse",""+dataSnapshot.child("bidders").getValue());

                    for (DataSnapshot inside_DataSnapshot: dataSnapshot.child("bidders").getChildren()) {
                        Bidders biddersItem = inside_DataSnapshot.getValue(Bidders.class);
                        diyBiddings.add(biddersItem);
                        biddersAdapter.notifyDataSetChanged();
                    }

                    String message_price="";
                    List<String> message_Price = new ArrayList<String>();
                    for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                        double price= postSnapshot.child("selling_price").getValue(double.class);
                        message_price += price;
                        message_Price.add(message_price);

                    }
                    final Bidders bidders = new Bidders();

                    final User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                    want_to_bid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            biddersReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(dataSnapshot.getKey())
                                    .child("bidders");
                            final Dialog dialog = new Dialog(DIYDetailViewActivity.this);
                            dialog.setContentView(R.layout.bidders);

                            Button bidders_dialog = (Button) dialog.findViewById(R.id.btn_bidders_dialog);
                            bidders_dialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EditText bidders_value = (EditText) dialog.findViewById(R.id.et_bidders_price);
                                    Log.e("value_price",bidders_value.getText().toString());
                                    Log.e("value_datasnapshot",dataSnapshot.getKey());
                                    Log.e("database_value",bidders_value.getText().toString()+ " "+user_name);
                                    biddersReference.push().setValue(new Bidders(bidders_value.getText().toString(),userID));

                                    Intent intent = new Intent(DIYDetailViewActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });

                            dialog.show();
                        }
                    });


                    final String finalMessage_price = message_price;
                    button_buy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final Float float_this = Float.valueOf(0);

                                    DIYSell productInfo = dataSnapshot.getValue(DIYSell.class);
                                    final String diyName = productInfo.getDiyName();
                                    final String diyUrl = productInfo.getDiyUrl();
                                    final String user_id = productInfo.getUser_id();
                                    final String productID = productInfo.getProductID();
                                    final String status = productInfo.getIdentity();

                                    if (!userID.equals(info.getUser_id())) {
                                        Log.e("pending_not_same", String.valueOf("" + userID != info.getUser_id()));
                                        Log.e("userid", String.valueOf("" + userID));
                                        Log.e("info_userid", String.valueOf("" + info.getUser_id()));

                                        pending_reference.orderByChild("productID").equalTo(info.productID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    Log.e("same_prodID", "" + dataSnapshot.exists());
                                                    Log.e("same_data", "" + dataSnapshot);

                                                    final Dialog dialog = new Dialog(DIYDetailViewActivity.this);
                                                    dialog.setContentView(R.layout.exist_dialog);
                                                    TextView text = (TextView) dialog.findViewById(R.id.et_email);
                                                    text.setText("DIY already added to pending list!");
                                                    ImageView image = (ImageView) dialog.findViewById(R.id.exist_dialog_imageview);
                                                    image.setImageResource(R.drawable.exist);

                                                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOKI);
                                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(DIYDetailViewActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    dialog.show();
                                                } else {

                                                    DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, status, float_this, float_this);
                                                    String upload_info = pending_reference.push().getKey();
                                                    pending_reference.child(upload_info).setValue(info);
                                                    pending_reference.child(upload_info).child("DIY Price").setValue(finalMessage_price);

                                                    final Dialog dialog = new Dialog(DIYDetailViewActivity.this);
                                                    dialog.setContentView(R.layout.done_dialog);
                                                    TextView text = (TextView) dialog.findViewById(R.id.text);
                                                    text.setText("DIY added to pending list!");
                                                    ImageView image = (ImageView) dialog.findViewById(R.id.dialog_imageview);
                                                    image.setImageResource(R.drawable.done);

                                                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(DIYDetailViewActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    dialog.show();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    }else if(userID.equals(info.getUser_id())) {
                                        Log.e("pending_same", String.valueOf("" + userID.equals(info.getUser_id())));
                                        Toast.makeText(DIYDetailViewActivity.this, "It's your own product!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
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

                    create_promo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog myDialog = new Dialog(context);
                            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            myDialog.setContentView(R.layout.dialog_promo);
                            myDialog.show();

                            final EditText startDate = (EditText) myDialog.findViewById(R.id.et_start_date);
                            startDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Initialize a new date picker dialog fragment
                                    Bundle bundle = new Bundle();
                                    DatePickerFragment newFragment = new DatePickerFragment();
                                    newFragment.setArguments(bundle);
                                    newFragment.show(getSupportFragmentManager(), "datePicker");

                                }
                            });

                            EditText endDate = (EditText) myDialog.findViewById(R.id.et_end_date);
                            Button ok = (Button) myDialog.findViewById(R.id.btn_ok);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialog.cancel();
                                }
                            });
//                            showDatePickerDialog(v);
                            Toast.makeText(context, "CREATE PROMO BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                        }
                    });

                    bid_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent item = new Intent(DIYDetailViewActivity.this,ToBidProduct.class);
                            item.putExtra("itemId", dataSnapshot.getKey());
                            startActivity(item);
                        }
                    });


                    if (item != null) {
                        String[] splitsMat = dataSnapshot.child("materials").getValue().toString().split(",");
                        Log.e("messageProd", "" + splitsMat);

                        String messageMat = "";
                        List<String> messageMaterials = new ArrayList<String>();
                        int count = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                            String material_name = postSnapshot.child("name").getValue(String.class).toUpperCase();
                            Long material_qty = postSnapshot.child("quantity").getValue(Long.class);
                            String material_unit = postSnapshot.child("unit").getValue(String.class);
                            Log.e("message", "" + material_name);
                            messageMat += "\n" + material_qty + " " + material_unit + " " + material_name;
                            messageMaterials.add(material_name);
                            count++;
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
//
//                        Log.d("MessageProcedure", messageProcedure.toString());
//
                        diy_materials.setText(messageMat);
                        diy_procedures.setText(messageProd);
//                        //diy_procedures.setText("ASK PERMISSION TO THE OWNER OR BUY THE ITEM!");
                         diy_sell.setText(message_price);
//
//                        Log.d("SnapItem", "not null");
//                        Log.d("SnapMaterial", "" + item);
//                        Log.d("SnapDataSnap", "" + dataSnapshot.child("materials").getValue());
//                    } else {
//                        Log.d("SnapItem", "null");
                    }

                    if (diyInfo.diyUrl != null) {
                        diyImagesViewPager = (ViewPager) findViewById(R.id.diyImagesViewPagers_sell);
                        diyImagesViewPagerAdapter = new DIYImagesViewPagerAdapter(getBaseContext(), diyInfo.diyUrl);
                        diyImagesViewPager.setAdapter(diyImagesViewPagerAdapter);

//                        Toast.makeText(DIYDetailViewActivity.this, diyInfo.diyUrl, Toast.LENGTH_SHORT).show();
                    }

                }else if(diyInfo.getIdentity().equalsIgnoreCase("on bid")){
                    diy_sell.setVisibility(View.VISIBLE);
                    user_owner_name.setVisibility(View.VISIBLE);
                    create_promo.setVisibility(View.VISIBLE);
                    php.setVisibility(View.VISIBLE);
                    sellers_address.setVisibility(View.VISIBLE);
                    sellers_contact_no.setVisibility(View.VISIBLE);
                    selling_price.setVisibility(View.VISIBLE);
                    seller_info.setVisibility(View.VISIBLE);
                    diy_name.setText(info.diyName);
                    contact_seller.setVisibility(View.VISIBLE);
                    bid_item.setVisibility(View.VISIBLE);

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
                                bid_item.setVisibility(View.VISIBLE);
                                create_promo.setVisibility(View.VISIBLE);
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
                    for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                        double price= postSnapshot.child("selling_price").getValue(double.class);
                        message_price += price;
                        message_Price.add(message_price);

                    }

                    for (DataSnapshot insideDataSnapshot: dataSnapshot.child("bidding").getChildren()) {
                        DIYBidding biddingItem = insideDataSnapshot.getValue(DIYBidding.class);
                        tv_bid_xpire.setText(biddingItem.getDate());
                        tv_bid_price.setText(biddingItem.getPrice_min()+" "+"to"+" "+biddingItem.getPrice_max());
                        tv_ownr_cmmnt.setText(biddingItem.getMessage());
                    }

                    for (DataSnapshot inside_DataSnapshot: dataSnapshot.child("bidders").getChildren()) {
                        Bidders biddersItem = inside_DataSnapshot.getValue(Bidders.class);
                        diyBiddings.add(biddersItem);
                        biddersAdapter.notifyDataSetChanged();
                    }

                    want_to_bid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(diyInfo.user_id.equals(userID)){
                                Toast.makeText(DIYDetailViewActivity.this,"THIS IS YOUR ITEM!!!", Toast.LENGTH_SHORT).show();
                            }else{
                                biddersReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(dataSnapshot.getKey())
                                        .child("bidders");
                                final Dialog dialog = new Dialog(DIYDetailViewActivity.this);
                                dialog.setContentView(R.layout.bidders);

                                Button bidders_dialog = (Button) dialog.findViewById(R.id.btn_bidders_dialog);
                                bidders_dialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        EditText bidders_value = (EditText) dialog.findViewById(R.id.et_bidders_price);
                                        Log.e("value_price",bidders_value.getText().toString());
                                        Log.e("value_datasnapshot",dataSnapshot.getKey());
                                        Log.e("database_value",bidders_value.getText().toString()+ " "+user_name);
                                        biddersReference.push().setValue(new Bidders(bidders_value.getText().toString(),user_name));

                                        Intent intent = new Intent(DIYDetailViewActivity.this, DIYDetailViewActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                dialog.show();
                            }
                        }
                    });


                    final String finalMessage_price = message_price;
                    button_buy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final Float float_this = Float.valueOf(0);

                                    DIYSell productInfo = dataSnapshot.getValue(DIYSell.class);
                                    final String diyName = productInfo.getDiyName();
                                    final String diyUrl = productInfo.getDiyUrl();
                                    final String user_id = productInfo.getUser_id();
                                    final String productID = productInfo.getProductID();
                                    final String status = productInfo.getIdentity();

                                    if (!userID.equals(info.getUser_id())) {
                                        Log.e("pending_not_same", String.valueOf("" + userID != info.getUser_id()));
                                        Log.e("userid", String.valueOf("" + userID));
                                        Log.e("info_userid", String.valueOf("" + info.getUser_id()));

                                        pending_reference.orderByChild("productID").equalTo(info.productID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    Log.e("same_prodID", "" + dataSnapshot.exists());
                                                    Log.e("same_data", "" + dataSnapshot);

                                                    final Dialog dialog = new Dialog(DIYDetailViewActivity.this);
                                                    dialog.setContentView(R.layout.exist_dialog);
                                                    TextView text = (TextView) dialog.findViewById(R.id.et_email);
                                                    text.setText("DIY already added to pending list!");
                                                    ImageView image = (ImageView) dialog.findViewById(R.id.exist_dialog_imageview);
                                                    image.setImageResource(R.drawable.exist);

                                                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOKI);
                                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(DIYDetailViewActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    dialog.show();
                                                } else {

                                                    DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, status, float_this, float_this);
                                                    String upload_info = pending_reference.push().getKey();
                                                    pending_reference.child(upload_info).setValue(info);
                                                    pending_reference.child(upload_info).child("DIY Price").setValue(finalMessage_price);

                                                    final Dialog dialog = new Dialog(DIYDetailViewActivity.this);
                                                    dialog.setContentView(R.layout.done_dialog);
                                                    TextView text = (TextView) dialog.findViewById(R.id.text);
                                                    text.setText("DIY added to pending list!");
                                                    ImageView image = (ImageView) dialog.findViewById(R.id.dialog_imageview);
                                                    image.setImageResource(R.drawable.done);

                                                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(DIYDetailViewActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    dialog.show();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    }else if(userID.equals(info.getUser_id())) {
                                        Log.e("pending_same", String.valueOf("" + userID.equals(info.getUser_id())));
                                        Toast.makeText(DIYDetailViewActivity.this, "It's your own product!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
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

                    create_promo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog myDialog = new Dialog(context);
                            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            myDialog.setContentView(R.layout.dialog_promo);
                            myDialog.show();

                            final EditText startDate = (EditText) myDialog.findViewById(R.id.et_start_date);
                            startDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Initialize a new date picker dialog fragment
                                    Bundle bundle = new Bundle();
                                    DatePickerFragment newFragment = new DatePickerFragment();
                                    newFragment.setArguments(bundle);
                                    newFragment.show(getSupportFragmentManager(), "datePicker");

                                }
                            });

                            EditText endDate = (EditText) myDialog.findViewById(R.id.et_end_date);
                            Button ok = (Button) myDialog.findViewById(R.id.btn_ok);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myDialog.cancel();
                                }
                            });
//                            showDatePickerDialog(v);
                            Toast.makeText(context, "CREATE PROMO BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                        }
                    });

                    bid_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent item = new Intent(DIYDetailViewActivity.this,ToBidProduct.class);
                            item.putExtra("itemId", dataSnapshot.getKey());
                            startActivity(item);
                        }
                    });


                    if (item != null) {
                        String[] splitsMat = dataSnapshot.child("materials").getValue().toString().split(",");
                        Log.e("messageProd", "" + splitsMat);

                        String messageMat = "";
                        List<String> messageMaterials = new ArrayList<String>();
                        int count = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                            String material_name = postSnapshot.child("name").getValue(String.class).toUpperCase();
                            Long material_qty = postSnapshot.child("quantity").getValue(Long.class);
                            String material_unit = postSnapshot.child("unit").getValue(String.class);
                            Log.e("message", "" + material_name);
                            messageMat += "\n" + material_qty + " " + material_unit + " " + material_name;
                            messageMaterials.add(material_name);
                            count++;
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

                        diy_materials.setText(messageMat);
                        diy_procedures.setText(messageProd);
//                        //diy_procedures.setText("ASK PERMISSION TO THE OWNER OR BUY THE ITEM!");
                        diy_sell.setText(message_price);
                    }

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


//        public void createDummyData() {
//            SectionDataModel dm = new SectionDataModel();
//
//            dm.setHeaderTitle("Here: " );
//
//            ArrayList<DIYnames> singleItem = new ArrayList<DIYnames>();
//            for (int j = 0; j <= 15; j++) {
//                singleItem.add(new DIYnames("Item " + j, "URL " + j));
//            }
//            dm.setAllItemsInSection(singleItem);
//            allSampleData.add(dm);
//
//    }

    public void createDummyData() {
        Toast.makeText(DIYDetailViewActivity.this, "Loading Related DIYS...", Toast.LENGTH_SHORT).show();

        SectionDataModel dm = new SectionDataModel();
        DIYnames dataDIys = new DIYnames();
        Log.e("dataDIysss", String.valueOf(dataDIys));
        dm.setHeaderTitle("Related DIYS: " );

        ArrayList<DIYnames> singleItem = new ArrayList<DIYnames>();

//            for (int j = 0; j <= 3; j++) {
//                singleItem.add(new DIYnames("Item ", "URL "));
        singleItem.add(dataDIys);
//            }
        dm.setAllItemsInSection(singleItem);
        allSampleData.add(dm);

        Log.e("singleItem", String.valueOf(singleItem));


    }

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