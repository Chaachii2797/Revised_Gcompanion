package cabiso.daphny.com.g_companion.BuyingProcess;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;
import cabiso.daphny.com.g_companion.InstantMessaging.ui.activities.ChatSplashActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.UserProfileInfo;
import cabiso.daphny.com.g_companion.PushNotification;

/**
 * Created by cicctuser on 10/03/2017.
 */

public class Pending_Activity extends AppCompatActivity{

    private ArrayList<DIYnames> pendingList = new ArrayList<>();
    private ArrayList<DIYSell> sellList = new ArrayList<>();

    private ListView lv;
    private Items_Adapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;
    private UserProfileInfo userProfileInfo;
    private DatabaseReference pendingReference, forMeetupReference, loggedInName;
    private DatabaseReference userdataReference;
//    String snapId;
    private ArrayList<DBMaterial> dbMaterials;
    int count;

    private DatabaseReference soldReference;
    final ArrayList<String> keyList = new ArrayList<>();
    final ArrayList<String> keyListss = new ArrayList<>();
    final ArrayList<Integer> buyQtyList = new ArrayList<>();
    private ArrayList<DIYnames> promoFreeList;
    final ArrayList<String> hasFree = new ArrayList<>();
    final ArrayList<String> promokey = new ArrayList<>();
    final ArrayList<String> hasDiscount = new ArrayList<>();
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private String loggedInUserName;
    private DatabaseReference user_reference;

    public Pending_Activity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        database = FirebaseDatabase.getInstance();
        pendingReference = FirebaseDatabase.getInstance().getReference().child("DIY Pending Items").child(userID);
        loggedInName = FirebaseDatabase.getInstance().getReference().child("userdata");
        user_reference = FirebaseDatabase.getInstance().getReference().child("userdata"); // for notification

        promoFreeList = new ArrayList<>();

        pendingReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot", snapshot.getKey());

                    final DIYSell img = snapshot.getValue(DIYSell.class);
                    sellList.add(img);

                    //get DIY key
                    keyList.add(snapshot.getKey());
                    keyListss.add(snapshot.getKey());

                    Log.e("keyList", String.valueOf(keyList));
                    Log.e("keyListss", String.valueOf(keyListss));

                    promokey.add(snapshot.getKey());
                    Log.e("promokey", String.valueOf(promokey));
                    Log.e("promoKeySize", String.valueOf(promokey.size()));

                    //store key promo that has freeItems in an array
                    if(snapshot.hasChild("freeItemList") && snapshot.hasChild("freeItemQuantity")){
                        hasFree.add(snapshot.getKey());
                        Log.e("Hass", "Yes" + " " + snapshot.getKey());
                        Log.e("freeSize", String.valueOf(hasFree.size()));
                    } else{
                        Log.e("Hass", "No" + " " + snapshot.getKey());

                    }

                    if(snapshot.hasChild("percent_discount")){
                        Log.e("discountHas", "Yes" + " " + snapshot.getKey());

                    }else{
                        Log.e("discountNO", "No" + " " + snapshot.getKey());
                    }


                    //init adapter
                    adapter = new Items_Adapter(Pending_Activity.this, R.layout.pending_item, sellList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
                    final int count =lv.getAdapter().getCount();
                    registerForContextMenu(lv);


                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            final DIYSell itemRef = adapter.getItem(position);

                            Toast.makeText(Pending_Activity.this, "Item identity: " + " = " + itemRef.getIdentity(), Toast.LENGTH_SHORT).show();
                            //get promo free quantity
                            final Object freeItemQty =  snapshot.child("freeItemQuantity").getValue();
                            Log.e("freeItemQty", String.valueOf(freeItemQty));

                            final Object itemDiscount =  snapshot.child("percent_discount").getValue();
                            Log.e("itemDiscount", String.valueOf(itemDiscount));

                            //get promo free item
                            for (DataSnapshot postSnapshot : snapshot.child("freeItemList").getChildren()) {
                                DIYnames promoFreeDIY = postSnapshot.getValue(DIYnames.class);
                                Log.e("promoFreeDIY", String.valueOf(promoFreeDIY.getDiyName()));
                                promoFreeList.add(promoFreeDIY);

                            }

                            if(itemRef.getIdentity().equalsIgnoreCase("For Confirmation Item")){
                                Toast.makeText(Pending_Activity.this, "Chat buyer!", Toast.LENGTH_SHORT).show();
                                Log.e("itemref", itemRef.getBuyerID());

                                final Dialog myDialog = new Dialog(Pending_Activity.this);
                                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                myDialog.setContentView(R.layout.dialog_pending_approval);
                                final Button chat = (Button) myDialog.findViewById(R.id.dialogBtbChatSeller);
                                final Button forMeetup = (Button) myDialog.findViewById(R.id.dialogBtnForMeetUp);
                                final Button decline = (Button) myDialog.findViewById(R.id.dialogBtnDecline);
                                TextView textTitle = (TextView) myDialog.findViewById(R.id.textTitle);
                                final TextView textDone = (TextView) myDialog.findViewById(R.id.textDone);

                                //chat buyer for some details
                                chat.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Pending_Activity.this, ChatSplashActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                //confirm for meet up (only for seller)
                                forMeetup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Float float_this = Float.valueOf(0);
                                        //push data to ForMeetUp after confirming the pending item
                                        forMeetupReference = FirebaseDatabase.getInstance().getReference()
                                                .child("Items_ForMeetUp").child(userID);

                                        final String meetup_diyName = sellList.get(position).getDiyName();
                                        final String meetup_diyUrl = sellList.get(position).getDiyUrl();
                                        final String meetup_user_id = sellList.get(position).getUser_id();
                                        final String meetup_productID = sellList.get(position).getProductID();
                                        String meetup_status = sellList.get(position).getIdentity();
                                        final String meetup_buyer = sellList.get(position).getBuyerID();
                                        final double meetup_price = sellList.get(position).getSelling_price();
                                        int meetup_qty = sellList.get(position).getSelling_qty();
                                        final String sellerName = sellList.get(position).getLoggedInUser();
                                        final int buyQty = sellList.get(position).getBuyingQuantity();
                                        Log.e("buyQtyyy", String.valueOf(buyQty));

                                        //push to seller side
                                        DIYSell product = new DIYSell(meetup_diyName, meetup_diyUrl, meetup_user_id,
                                                meetup_productID, "For Buyer Meet-up" , float_this, float_this, meetup_buyer,
                                                sellerName, buyQty,0 ,0);

                                        //confirm pending DIY, push to meet up (seller side)
                                        final String upload = forMeetupReference.push().getKey();
                                        forMeetupReference.child(upload).setValue(product);
                                        forMeetupReference.child(upload).child("userStatus").setValue("seller");
                                        forMeetupReference.child(upload).child("selling_price").setValue(meetup_price);
                                        forMeetupReference.child(upload).child("selling_qty").setValue(meetup_qty);
                                        forMeetupReference.child(upload).child("dateAdded").setValue(sdate);

                                        //for buyer name
                                        loggedInName.child(meetup_buyer).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                                                loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);
                                                Log.e("LoggedUserName", loggedInUserName);

                                                DatabaseReference meetReference = FirebaseDatabase.getInstance().getReference()
                                                        .child("Items_ForMeetUp").child(meetup_buyer);

                                                DIYSell buyProduct = new DIYSell(meetup_diyName, meetup_diyUrl, meetup_user_id,
                                                        meetup_productID, "For Seller Meet-up" , float_this, float_this,
                                                        meetup_buyer, loggedInUserName, buyQty,0 ,0);

                                                //push to buyer pending DIY, push to meet up (buyer side)
                                                String buyUpload = meetReference.child(upload).getKey();
                                                meetReference.child(buyUpload).setValue(buyProduct);
                                                meetReference.child(buyUpload).child("userStatus").setValue("buyer");
                                                meetReference.child(buyUpload).child("selling_price").setValue(meetup_price);
                                                meetReference.child(buyUpload).child("dateAdded").setValue(sdate);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });


                                        //remove confirm DIY in listview
                                        sellList.remove(position);
                                        adapter.notifyDataSetChanged();

                                        //remove my item and send to For_MeetUp
                                        DatabaseReference pendReference = FirebaseDatabase.getInstance().getReference()
                                                .child("DIY Pending Items").child(meetup_buyer);

                                        String penkey = keyList.get(position);
                                        String penkeys = keyListss.get(position);
                                        //remove confirm DIY in DB
                                        pendingReference.child(penkey).removeValue();
                                        pendReference.child(penkeys).removeValue();


                                        //Notification
                                        user_reference.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                                if(loggedInUserName!=null){
                                                    PushNotification pushNotification = new PushNotification(getApplicationContext());
                                                    pushNotification.title("Pending Item Notification")
                                                            .message(sellerName + " place your pending item " +  meetup_diyName + "  for meet-up!")
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
                                });
                                //decline pending item (seller side)
                                decline.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        String pending_buyer = sellList.get(position).getBuyerID();
                                        final String sellerName = sellList.get(position).getLoggedInUser();
                                        final String meetup_diyName = sellList.get(position).getDiyName();

                                        //remove decline DIY in listview
                                        sellList.remove(position);
                                        adapter.notifyDataSetChanged();

                                        DatabaseReference pendReference = FirebaseDatabase.getInstance().getReference()
                                                .child("DIY Pending Items").child(pending_buyer);

                                        String penkey = keyList.get(position);
                                        String penkeys = keyListss.get(position);
                                        //remove decline DIY in DB
                                        pendingReference.child(penkey).removeValue();
                                        pendReference.child(penkeys).removeValue();

                                        //Notification
                                        user_reference.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                                if(loggedInUserName!=null){
                                                    PushNotification pushNotification = new PushNotification(getApplicationContext());
                                                    pushNotification.title("Pending Item Notification")
                                                            .message(sellerName + " decline your pending item " +  meetup_diyName + "!")
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
                                });
                                textDone.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Pending_Activity.this, MainActivity.class);
                                        startActivity(intent);
                                        myDialog.dismiss();
                                    }
                                });
                                myDialog.show();

                            }

                            else if (itemRef.getIdentity().equalsIgnoreCase("For Confirmation Discount Promo Item")){
                                Toast.makeText(Pending_Activity.this, "Chat buyer!", Toast.LENGTH_SHORT).show();
                                Log.e("itemref", itemRef.getBuyerID());


                                final Dialog myDialog = new Dialog(Pending_Activity.this);
                                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                myDialog.setContentView(R.layout.dialog_pending_approval);
                                final Button chat = (Button) myDialog.findViewById(R.id.dialogBtbChatSeller);
                                final Button forMeetup = (Button) myDialog.findViewById(R.id.dialogBtnForMeetUp);
                                final Button decline = (Button) myDialog.findViewById(R.id.dialogBtnDecline);
                                TextView textTitle = (TextView) myDialog.findViewById(R.id.textTitle);
                                final TextView textDone = (TextView) myDialog.findViewById(R.id.textDone);

                                //chat buyer for some details
                                chat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Pending_Activity.this, ChatSplashActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                //confirm for meet up (only for seller)
                                forMeetup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Float float_this = Float.valueOf(0);
                                        //push data to ForMeetUp after confirming the pending item
                                        forMeetupReference = FirebaseDatabase.getInstance().getReference()
                                                .child("Items_ForMeetUp").child(userID);

                                        final String meetup_diyName = sellList.get(position).getDiyName();
                                        final String meetup_diyUrl = sellList.get(position).getDiyUrl();
                                        final String meetup_user_id = sellList.get(position).getUser_id();
                                        final String meetup_productID = sellList.get(position).getProductID();
                                        String meetup_status = sellList.get(position).getIdentity();
                                        final String meetup_buyer = sellList.get(position).getBuyerID();
                                        final double meetup_price = sellList.get(position).getSelling_price();
                                        int meetup_qty = sellList.get(position).getSelling_qty();
                                        String sellerName = sellList.get(position).getLoggedInUser();
                                        final int buyQty = sellList.get(position).getBuyingQuantity();
                                        Log.e("buyQtyyy", String.valueOf(buyQty));

                                        //push to seller side
                                        DIYSell product = new DIYSell(meetup_diyName, meetup_diyUrl, meetup_user_id,
                                                meetup_productID, "For Buyer Meet-up Discount Item" , float_this, float_this, meetup_buyer,
                                                sellerName, buyQty,0 ,0);

                                        //confirm pending DIY, push to meet up (seller side)
                                        final String upload = forMeetupReference.push().getKey();
                                        forMeetupReference.child(upload).setValue(product);
                                        forMeetupReference.child(upload).child("userStatus").setValue("seller");
                                        forMeetupReference.child(upload).child("selling_price").setValue(meetup_price);
                                        forMeetupReference.child(upload).child("selling_qty").setValue(meetup_qty);
                                        forMeetupReference.child(upload).child("percent_discount").setValue(itemDiscount);
                                        forMeetupReference.child(upload).child("dateAdded").setValue(sdate);

                                        //for buyer name
                                        loggedInName.child(meetup_buyer).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                                                loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);
                                                Log.e("LoggedUserName", loggedInUserName);

                                                DatabaseReference meetReference = FirebaseDatabase.getInstance().getReference()
                                                        .child("Items_ForMeetUp").child(meetup_buyer);

                                                DIYSell buyProduct = new DIYSell(meetup_diyName, meetup_diyUrl, meetup_user_id,
                                                        meetup_productID, "For Seller Meet-up Discount Item" , float_this, float_this,
                                                        meetup_buyer, loggedInUserName, buyQty,0 ,0);

                                                //push to buyer pending DIY, push to meet up (buyer side)
                                                String buyUpload = meetReference.child(upload).getKey();
                                                meetReference.child(buyUpload).setValue(buyProduct);
                                                meetReference.child(buyUpload).child("userStatus").setValue("buyer");
                                                meetReference.child(buyUpload).child("selling_price").setValue(meetup_price);
                                                meetReference.child(buyUpload).child("percent_discount").setValue(itemDiscount);
                                                meetReference.child(buyUpload).child("dateAdded").setValue(sdate);

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });


                                        //remove confirm DIY in listview
                                        sellList.remove(position);
                                        adapter.notifyDataSetChanged();

                                        //remove my item and send to For_MeetUp
                                        DatabaseReference pendReference = FirebaseDatabase.getInstance().getReference()
                                                .child("DIY Pending Items").child(meetup_buyer);

                                        String penkey = keyList.get(position);
                                        String penkeys = keyListss.get(position);
                                        //remove confirm DIY in DB
                                        pendingReference.child(penkey).removeValue();
                                        pendReference.child(penkeys).removeValue();

                                    }
                                });
                                //decline pending item (seller side)
                                decline.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        String pending_buyer = sellList.get(position).getBuyerID();

                                        //remove decline DIY in listview
                                        sellList.remove(position);
                                        adapter.notifyDataSetChanged();

                                        DatabaseReference pendReference = FirebaseDatabase.getInstance().getReference()
                                                .child("DIY Pending Items").child(pending_buyer);

                                        String penkey = keyList.get(position);
                                        String penkeys = keyListss.get(position);
                                        //remove decline DIY in DB
                                        pendingReference.child(penkey).removeValue();
                                        pendReference.child(penkeys).removeValue();


                                    }
                                });

                                textDone.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Pending_Activity.this, MainActivity.class);
                                        startActivity(intent);
                                        myDialog.dismiss();
                                    }
                                });

                                myDialog.show();
                            }


                            else if(itemRef.getIdentity().equalsIgnoreCase("For Confirmation Buy and Take Promo Item")){
                                Toast.makeText(Pending_Activity.this, "Chat buyer!", Toast.LENGTH_SHORT).show();
                                Log.e("itemref", itemRef.getBuyerID());

                                final Dialog myDialog = new Dialog(Pending_Activity.this);
                                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                myDialog.setContentView(R.layout.dialog_pending_approval);
                                final Button chat = (Button) myDialog.findViewById(R.id.dialogBtbChatSeller);
                                final Button forMeetup = (Button) myDialog.findViewById(R.id.dialogBtnForMeetUp);
                                final Button decline = (Button) myDialog.findViewById(R.id.dialogBtnDecline);
                                TextView textTitle = (TextView) myDialog.findViewById(R.id.textTitle);
                                final TextView textDone = (TextView) myDialog.findViewById(R.id.textDone);

                                //chat buyer for some details
                                chat.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Pending_Activity.this, ChatSplashActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                //confirm for meet up (only for seller)
                                forMeetup.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final Float float_this = Float.valueOf(0);
                                        //push data to ForMeetUp after confirming the pending item
                                        forMeetupReference = FirebaseDatabase.getInstance().getReference()
                                                .child("Items_ForMeetUp").child(userID);

                                        final String meetup_diyName = sellList.get(position).getDiyName();
                                        final String meetup_diyUrl = sellList.get(position).getDiyUrl();
                                        final String meetup_user_id = sellList.get(position).getUser_id();
                                        final String meetup_productID = sellList.get(position).getProductID();
                                        String meetup_status = sellList.get(position).getIdentity();
                                        final String meetup_buyer = sellList.get(position).getBuyerID();
                                        final double meetup_price = sellList.get(position).getSelling_price();
                                        int meetup_qty = sellList.get(position).getSelling_qty();
                                        String sellerName = sellList.get(position).getLoggedInUser();
                                        final int buyQty = sellList.get(position).getBuyingQuantity();
                                        Log.e("buyQtyyy", String.valueOf(buyQty));

                                        //push to seller side
                                        DIYSell product = new DIYSell(meetup_diyName, meetup_diyUrl, meetup_user_id,
                                                meetup_productID, "For Buyer Meet-up Buy and Take Item" , float_this, float_this, meetup_buyer,
                                                sellerName, buyQty,0 ,0);

                                        //confirm pending DIY, push to meet up (seller side)
                                        final String upload = forMeetupReference.push().getKey();

                                        //confirm promo DIY, push to meet up
                                        for (int pos=0; pos < hasFree.size(); pos++) {
                                            if(promokey.get(position).equals(hasFree.get(pos))){
                                                Log.e("equalsSilaa", "YESS" + " " + promokey.get(position) + " = " + hasFree.get(pos));
                                                forMeetupReference.child(upload).setValue(product);
                                                forMeetupReference.child(upload).child("userStatus").setValue("seller");
                                                forMeetupReference.child(upload).child("selling_price").setValue(meetup_price);
                                                forMeetupReference.child(upload).child("selling_qty").setValue(meetup_qty);
                                                forMeetupReference.child(upload).child("freeItemList").setValue(promoFreeList);
                                                forMeetupReference.child(upload).child("freeItemQuantity").setValue(freeItemQty);
                                                forMeetupReference.child(upload).child("dateAdded").setValue(sdate);

                                            }else{
                                                forMeetupReference.child(upload).setValue(product);
                                                forMeetupReference.child(upload).child("userStatus").setValue("seller");
                                                forMeetupReference.child(upload).child("selling_price").setValue(meetup_price);
                                                forMeetupReference.child(upload).child("selling_qty").setValue(meetup_qty);
                                                forMeetupReference.child(upload).child("dateAdded").setValue(sdate);

                                            }

                                        }

                                        //for buyer name
                                        loggedInName.child(meetup_buyer).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                                                loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);
                                                Log.e("LoggedUserName", loggedInUserName);

                                                DatabaseReference meetReference = FirebaseDatabase.getInstance().getReference()
                                                        .child("Items_ForMeetUp").child(meetup_buyer);

                                                DIYSell buyProduct = new DIYSell(meetup_diyName, meetup_diyUrl, meetup_user_id,
                                                        meetup_productID, "For Seller Meet-up Buy and Take Item" , float_this, float_this,
                                                        meetup_buyer, loggedInUserName, buyQty,0 ,0);

                                                //push to buyer pending DIY, push to meet up (buyer side)
                                                String buyUpload = meetReference.child(upload).getKey();

                                                //confirm pending promo DIY, push to meet up
                                                for (int post =0; post < hasFree.size(); post++) {
                                                    if(promokey.get(position).equals(hasFree.get(post))){
                                                        Log.e("equalsSilaaa", "YESS" + " " + promokey.get(position) + " = " + hasFree.get(post));
                                                        meetReference.child(buyUpload).setValue(buyProduct);
                                                        meetReference.child(buyUpload).child("userStatus").setValue("buyer");
                                                        meetReference.child(buyUpload).child("selling_price").setValue(meetup_price);
                                                        meetReference.child(buyUpload).child("freeItemList").setValue(promoFreeList);
                                                        meetReference.child(buyUpload).child("freeItemQuantity").setValue(freeItemQty);
                                                        meetReference.child(buyUpload).child("dateAdded").setValue(sdate);

                                                    } else{
                                                        meetReference.child(buyUpload).setValue(buyProduct);
                                                        meetReference.child(buyUpload).child("userStatus").setValue("buyer");
                                                        meetReference.child(buyUpload).child("selling_price").setValue(meetup_price);
                                                        meetReference.child(buyUpload).child("dateAdded").setValue(sdate);

                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });


                                        //remove confirm DIY in listview
                                        sellList.remove(position);
                                        adapter.notifyDataSetChanged();

                                        //remove my item and send to For_MeetUp
                                        DatabaseReference pendReference = FirebaseDatabase.getInstance().getReference()
                                                .child("DIY Pending Items").child(meetup_buyer);

                                        String penkey = keyList.get(position);
                                        String penkeys = keyListss.get(position);
                                        //remove confirm DIY in DB
                                        pendingReference.child(penkey).removeValue();
                                        pendReference.child(penkeys).removeValue();

                                    }
                                });
                                //decline pending item (seller side)
                                decline.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        String pending_buyer = sellList.get(position).getBuyerID();

                                        //remove decline DIY in listview
                                        sellList.remove(position);
                                        adapter.notifyDataSetChanged();

                                        DatabaseReference pendReference = FirebaseDatabase.getInstance().getReference()
                                                .child("DIY Pending Items").child(pending_buyer);

                                        String penkey = keyList.get(position);
                                        String penkeys = keyListss.get(position);
                                        //remove decline DIY in DB
                                        pendingReference.child(penkey).removeValue();
                                        pendReference.child(penkeys).removeValue();


                                    }
                                });

                                textDone.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Pending_Activity.this, MainActivity.class);
                                        startActivity(intent);
                                        myDialog.dismiss();
                                    }
                                });

                                myDialog.show();
                            }

                            else {

                                Log.e("itemrefff", itemRef.getBuyerID());
                                // Alert Dialog for pending item state
                                AlertDialog.Builder ab = new AlertDialog.Builder(Pending_Activity.this, R.style.MyAlertDialogStyle);
                                ab.setTitle("Approval Pending");
                                ab.setMessage("Your DIY item with product ID " + "[" + itemRef.getProductID() + "]" + " is in Pending state");
                                ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                ab.create().show();

                            }
                        }
                    });

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}