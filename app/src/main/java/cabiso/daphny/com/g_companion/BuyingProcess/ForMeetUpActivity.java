package cabiso.daphny.com.g_companion.BuyingProcess;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Promo.PriceDiscountModel;
import cabiso.daphny.com.g_companion.Promo.PromoModel;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.UserProfileInfo;

/**
 * Created by Lenovo on 8/20/2018.
 */

public class ForMeetUpActivity extends AppCompatActivity {

    private ArrayList<DIYSell> meetUpList = new ArrayList<>();
    final ArrayList<String> meetupKey = new ArrayList<>();
    final ArrayList<String> meetupKeyyy = new ArrayList<>();

    private ListView lv;
    private Items_Adapter adapter;
    private ProgressDialog progressDialog;

    private String userID;
    private FirebaseUser mFirebaseUser;
    private UserProfileInfo userProfileInfo;
    private DatabaseReference userdataReference, meetUpRef, soldReference, diyReference, loggedInName, promoReference;
    int count;
    private ArrayList<String> prices = new ArrayList<>();
    private ArrayList<DIYSell> diyNames = new ArrayList<>();
    private ArrayList<String> snapKey = new ArrayList<>();
    private ArrayList<String> freeSnapKey = new ArrayList<>();//get key of promo free items

    private String loggedInUserName;
    private ArrayList<DIYSell> promoFreeList = new ArrayList<>();
    final ArrayList<String> hasFree = new ArrayList<>();
    final ArrayList<String> promokey = new ArrayList<>();
    final ArrayList<String> diyNameHasPromo = new ArrayList<>(); //get diy name, if ang diy naay promo
    final ArrayList<String> hasFreename = new ArrayList<>();
    final ArrayList<String> freeKeyxDIY = new ArrayList<>();

    private ArrayList<PromoModel> promoList = new ArrayList<>();
    final ArrayList<String> promoSalekey = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_meetup);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        userdataReference = FirebaseDatabase.getInstance().getReference().child("userdata");
        meetUpRef = FirebaseDatabase.getInstance().getReference().child("Items_ForMeetUp").child(userID);
        diyReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags"); //para sa item sa lv
        promoReference = FirebaseDatabase.getInstance().getReference().child("promo_sale");

        Log.e("diyReference", String.valueOf(diyReference));
        loggedInName = FirebaseDatabase.getInstance().getReference().child("userdata");

        meetUpRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                progressDialog.dismiss();
                userProfileInfo = dataSnapshot.getValue(UserProfileInfo.class);
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot", snapshot.getKey());

                    meetupKey.add(snapshot.getKey());
                    meetupKeyyy.add(snapshot.getKey());

                    Log.e("meetupKey", String.valueOf(meetupKey));
                    Log.e("meetupKeyyy", String.valueOf(meetupKeyyy));

                    final DIYSell img = snapshot.getValue(DIYSell.class);
                    meetUpList.add(img);

                    promokey.add(snapshot.getKey()); //key sa mga DIY sa meeet-up
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
                        Log.e("naayDiscount", "Yes" + " " + snapshot.getKey());
                    }else{
                        Log.e("naayDiscount", "No" + " " + snapshot.getKey());
                    }

                    final Object itemDiscount =  snapshot.child("percent_discount").getValue();
                    Log.e("itemDiscountt", String.valueOf(itemDiscount));

                    //init adapter
                    adapter = new Items_Adapter(ForMeetUpActivity.this, R.layout.pending_item, meetUpList);
                    //set adapter for listview
                    lv.setAdapter(adapter);
                    final int count = lv.getAdapter().getCount();
                    registerForContextMenu(lv);


                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            final DIYSell itemRef = adapter.getItem(position);
                            Log.e("iteReff", itemRef.getProductID());

                            diyNames.add(itemRef);
                            Log.e("diyNamessssss", String.valueOf(diyNames));

                            final DIYSell item = diyNames.get(position); //get product ID sa mga item na naa sa LV
                            Log.e("itemID", item.getProductID());

                            for (DataSnapshot postSnapshot : snapshot.child("freeItemList").getChildren()) {
                                DIYSell promoFreeDIY = postSnapshot.getValue(DIYSell.class);
                                Log.e("promoFreeDIYname", promoFreeDIY.getDiyName());  //i get ang prdouctID ani nya i sud sa list
                                Log.e("promoFreeDIYProdID", promoFreeDIY.getProductID());
                                promoFreeList.add(promoFreeDIY);
                                Log.e("promoFreeListInside", String.valueOf(promoFreeList));
                            }



                            //get promo free quantity
                            final Object freeItemQty =  snapshot.child("freeItemQuantity").getValue();
                            Log.e("freeItemQty", String.valueOf(freeItemQty));

                            //Selling Item
                            if(itemRef.getIdentity().equalsIgnoreCase("For Buyer Meet-up")) {
                                Toast.makeText(ForMeetUpActivity.this, "DIY name: " + itemRef.getDiyName() + " = " + position, Toast.LENGTH_SHORT).show();

                                diyReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for(final DataSnapshot snaps : dataSnapshot.getChildren()){
                                            final DIYSell diySellinfo = snaps.getValue(DIYSell.class);
                                            Log.e("diySellinfoID", diySellinfo.getProductID()); //tanan prodID sa DIY sa DB

                                            if(item != null){
                                                if(item.getProductID().equals(diySellinfo.getProductID())){
                                                    Log.e("itemProdID", item.getProductID()); //kaning item kay items na naa sa lv
                                                    Log.e("diySellPRodeID", diySellinfo.getProductID()); //prodID sa DB na ni equal sa LV

                                                    snapKey.add(snaps.getKey()); //get key sa diy  na naa sa diy_by_tags na ni equal sa forMeetUp items
                                                    Log.e("keyyDIYName", snaps.getKey() );

                                                    AlertDialog.Builder ab = new AlertDialog.Builder(ForMeetUpActivity.this, R.style.MyAlertDialogStyle);
                                                    ab.setTitle("Approval Meet-up");
                                                    ab.setMessage("Are you done meeting with the buyer?");

                                                    ab.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            final Float float_this = Float.valueOf(0);
                                                            //ika done, maadto siya sa sold item, nya ari mabutang ang counter para sa qty
                                                            soldReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items").child(userID);

                                                            final String sold_diyName = meetUpList.get(position).getDiyName();
                                                            final String sold_diyUrl = meetUpList.get(position).getDiyUrl();
                                                            final String sold_user_id = meetUpList.get(position).getUser_id();
                                                            final String sold_productID = meetUpList.get(position).getProductID();
                                                            String sold_status = meetUpList.get(position).getIdentity();
                                                            final String sold_buyer = meetUpList.get(position).getBuyerID();
                                                            final double sold_price = meetUpList.get(position).getSelling_price();
                                                            String sellerName = meetUpList.get(position).getLoggedInUser();
                                                            int sold_qty = meetUpList.get(position).getSelling_qty();
                                                            final int buyQty = meetUpList.get(position).getBuyingQuantity();

                                                            DIYSell product = new DIYSell(sold_diyName, sold_diyUrl, sold_user_id,
                                                                    sold_productID, "DELIVERED", float_this, float_this,
                                                                    sold_buyer, sellerName, buyQty);

                                                            final String upload = soldReference.push().getKey();
                                                            soldReference.child(upload).setValue(product);
                                                            soldReference.child(upload).child("userStatus").setValue("seller");
                                                            soldReference.child(upload).child("selling_price").setValue(sold_price);
                                                            soldReference.child(upload).child("selling_qty").setValue(sold_qty);

                                                            loggedInName.child(sold_buyer).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                                                                    loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);
                                                                    //DBref for buyer
                                                                    DatabaseReference soldReference = FirebaseDatabase.getInstance().getReference()
                                                                            .child("Sold_Items").child(sold_buyer);

                                                                    DIYSell buyProduct = new DIYSell(sold_diyName, sold_diyUrl, sold_user_id,
                                                                            sold_productID, "PURCHASED", float_this, float_this,
                                                                            sold_buyer, loggedInUserName, buyQty);
                                                                    String buyUpload = soldReference.child(upload).getKey();
                                                                    soldReference.child(buyUpload).setValue(buyProduct);
                                                                    soldReference.child(buyUpload).child("userStatus").setValue("buyer");
                                                                    soldReference.child(buyUpload).child("selling_price").setValue(sold_price);

                                                                }
                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                }
                                                            });

                                                            DatabaseReference meetReference = FirebaseDatabase.getInstance().getReference()
                                                                    .child("Items_ForMeetUp").child(sold_buyer);
                                                            //remove my item and send to SOLD_Items
                                                            String key = meetupKey.get(position);
                                                            String keys = meetupKeyyy.get(position);

                                                            meetUpRef.child(key).removeValue();
                                                            meetReference.child(keys).removeValue();

                                                            //get DIY total quantity sa DB
                                                            int meetUpQty = meetUpList.get(position).getSelling_qty();
                                                            Log.e("meetUpQty", String.valueOf(meetUpQty));

                                                            String message_price="";
                                                            String message_qty = "";
                                                            String message_dsc = "";
                                                            List<String> message_Price = new ArrayList<String>();
                                                            List<String> message_Qty = new ArrayList<String>();
                                                            final List<String> message_Dsc = new ArrayList<String>();
//
                                                            //get price key
                                                            for(DataSnapshot priceSnap : snaps.child("DIY Price").getChildren()){
                                                                Log.e("priceSnap", String.valueOf(priceSnap));
                                                                Log.e("priceSnapKey", priceSnap.getKey());
                                                                double price = priceSnap.child("selling_price").getValue(double.class);
                                                                int qty = priceSnap.child("selling_qty").getValue(int.class);
                                                                String dsc = priceSnap.child("selling_descr").getValue(String.class);

                                                                message_qty += qty;
                                                                message_Qty.add(message_qty);

                                                                message_price += price;
                                                                message_Price.add(message_price);

                                                                message_dsc +=dsc;
                                                                message_Dsc.add(message_dsc);

                                                                Log.e("message_qty","" +  message_qty);
                                                                Log.e("message_price", "" + message_price);

                                                                prices.add(message_qty);
                                                                Log.e("pricess", String.valueOf(prices));

                                                                //decrease quantity
                                                                int quantityCountMeetUp = meetUpQty - buyQty; //if dli promo
                                                                Log.e("quantityCountMeetUp", String.valueOf(quantityCountMeetUp));

                                                                //update quantity count
                                                                String penkey = snapKey.get(position);
                                                                Log.e("penKey", penkey);

                                                                HashMap<String, Object> results = new HashMap<>();
                                                                results.put("selling_qty", quantityCountMeetUp);
                                                                diyReference.child(penkey).child("DIY Price")
                                                                        .child(priceSnap.getKey()).updateChildren(results);

                                                                soldReference.child(upload).updateChildren(results);

                                                                Intent intent = new Intent(ForMeetUpActivity.this, MainActivity.class);
                                                                startActivity(intent);

                                                            }
                                                        }
                                                    });
                                                    ab.setNegativeButton("NOT YET", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    ab.create().show();
                                                }

                                            }

                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }

                                });

                            }
                            //Discount Promo
                            else if (itemRef.getIdentity().equalsIgnoreCase("For Buyer Meet-up Discount Item")){
                                Toast.makeText(ForMeetUpActivity.this, "DIY name: " + itemRef.getDiyName() + " = " + position, Toast.LENGTH_SHORT).show();

                                promoReference.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        for(final DataSnapshot promoDiscountSnap : dataSnapshot.getChildren()){
                                            PriceDiscountModel promoDiscount = promoDiscountSnap.getValue(PriceDiscountModel.class);
                                            Log.e("promoPrice", promoDiscount.getPromo_diyName());

                                            Log.e("discountKey", promoDiscountSnap.getKey());

                                            //dapat if unsay i click na item sa lv, ma equal sa name sa promo_sale node, nya adto
                                            // isulod ang keyList
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

                                diyReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for(final DataSnapshot snaps : dataSnapshot.getChildren()){
                                            final DIYSell diySellinfo = snaps.getValue(DIYSell.class);
                                            Log.e("diySellinfoID", diySellinfo.getProductID()); //tanan prodID sa DIY sa DB

                                            if(item != null){
                                                if(item.getProductID().equals(diySellinfo.getProductID())){
                                                    Log.e("itemProdID", item.getProductID()); //kaning item kay items na naa sa lv
                                                    Log.e("diySellPRodeID", diySellinfo.getProductID()); //prodID sa DB na ni equal sa LV

                                                    snapKey.add(snaps.getKey()); //get key sa diy  na naa sa diy_by_tags na ni equal sa forMeetUp items
                                                    Log.e("keyyDIYName", snaps.getKey() );

                                                    AlertDialog.Builder ab = new AlertDialog.Builder(ForMeetUpActivity.this, R.style.MyAlertDialogStyle);
                                                    ab.setTitle("Approval Meet-up");
                                                    ab.setMessage("Are you done meeting with the buyer?");

                                                    ab.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            final Float float_this = Float.valueOf(0);
                                                            //ika done, maadto siya sa sold item, nya ari mabutang ang counter para sa qty
                                                            soldReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items").child(userID);

                                                            final String sold_diyName = meetUpList.get(position).getDiyName();
                                                            final String sold_diyUrl = meetUpList.get(position).getDiyUrl();
                                                            final String sold_user_id = meetUpList.get(position).getUser_id();
                                                            final String sold_productID = meetUpList.get(position).getProductID();
                                                            String sold_status = meetUpList.get(position).getIdentity();
                                                            final String sold_buyer = meetUpList.get(position).getBuyerID();
                                                            final double sold_price = meetUpList.get(position).getSelling_price();
                                                            String sellerName = meetUpList.get(position).getLoggedInUser();
                                                            int sold_qty = meetUpList.get(position).getSelling_qty();
                                                            final int buyQty = meetUpList.get(position).getBuyingQuantity();

                                                            DIYSell product = new DIYSell(sold_diyName, sold_diyUrl, sold_user_id,
                                                                    sold_productID, "Delivered Discount Item", float_this, float_this,
                                                                    sold_buyer, sellerName, buyQty);

                                                            final String upload = soldReference.push().getKey();
                                                            soldReference.child(upload).setValue(product);
                                                            soldReference.child(upload).child("userStatus").setValue("seller");
                                                            soldReference.child(upload).child("selling_price").setValue(sold_price);
                                                            soldReference.child(upload).child("selling_qty").setValue(sold_qty);
                                                            soldReference.child(upload).child("percent_discount").setValue(itemDiscount);

                                                            loggedInName.child(sold_buyer).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                                                                    loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);

                                                                    //DBref for buyer
                                                                    DatabaseReference soldReference = FirebaseDatabase.getInstance().getReference()
                                                                            .child("Sold_Items").child(sold_buyer);

                                                                    DIYSell buyProduct = new DIYSell(sold_diyName, sold_diyUrl, sold_user_id,
                                                                            sold_productID, "Purchased Discount Item", float_this, float_this,
                                                                            sold_buyer, loggedInUserName, buyQty);

                                                                    String buyUpload = soldReference.child(upload).getKey();
                                                                    soldReference.child(buyUpload).setValue(buyProduct);
                                                                    soldReference.child(buyUpload).child("userStatus").setValue("buyer");
                                                                    soldReference.child(buyUpload).child("selling_price").setValue(sold_price);
                                                                    soldReference.child(buyUpload).child("percent_discount").setValue(itemDiscount);

                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                }
                                                            });

                                                            DatabaseReference meetReference = FirebaseDatabase.getInstance().getReference()
                                                                    .child("Items_ForMeetUp").child(sold_buyer);
                                                            //remove my item and send to SOLD_Items
                                                            String key = meetupKey.get(position);
                                                            String keys = meetupKeyyy.get(position);

                                                            meetUpRef.child(key).removeValue();
                                                            meetReference.child(keys).removeValue();

                                                            //get DIY total quantity sa DB
                                                            int meetUpQty = meetUpList.get(position).getSelling_qty();
                                                            Log.e("meetUpQty", String.valueOf(meetUpQty));

                                                            String message_price="";
                                                            String message_qty = "";
                                                            String message_dsc = "";
                                                            List<String> message_Price = new ArrayList<String>();
                                                            List<String> message_Qty = new ArrayList<String>();
                                                            final List<String> message_Dsc = new ArrayList<String>();
//
                                                            //get price key
                                                            for(DataSnapshot priceSnap : snaps.child("DIY Price").getChildren()){
                                                                Log.e("priceSnap", String.valueOf(priceSnap));
                                                                Log.e("priceSnapKey", priceSnap.getKey());
                                                                double price = priceSnap.child("selling_price").getValue(double.class);
                                                                int qty = priceSnap.child("selling_qty").getValue(int.class);
                                                                String dsc = priceSnap.child("selling_descr").getValue(String.class);

                                                                message_qty += qty;
                                                                message_Qty.add(message_qty);

                                                                message_price += price;
                                                                message_Price.add(message_price);

                                                                message_dsc +=dsc;
                                                                message_Dsc.add(message_dsc);

                                                                Log.e("message_qty","" +  message_qty);
                                                                Log.e("message_price", "" + message_price);

                                                                prices.add(message_qty);
                                                                Log.e("pricess", String.valueOf(prices));

                                                                //decrease quantity
                                                                int quantityCountMeetUp = meetUpQty - buyQty; //if dli promo
                                                                Log.e("quantityCountMeetUp", String.valueOf(quantityCountMeetUp));

                                                                //update quantity count
                                                                String penkey = snapKey.get(position);
                                                                Log.e("penKey", penkey);

                                                                HashMap<String, Object> results = new HashMap<>();
                                                                results.put("selling_qty", quantityCountMeetUp);
                                                                diyReference.child(penkey).child("DIY Price")
                                                                        .child(priceSnap.getKey()).updateChildren(results);

                                                                soldReference.child(upload).updateChildren(results);

                                                                Intent intent = new Intent(ForMeetUpActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                                    ab.setNegativeButton("NOT YET", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    ab.create().show();
                                                }

                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }

                                });

                            }

                            //Buy and Take Promo
                            else if(itemRef.getIdentity().equalsIgnoreCase("For Buyer Meet-up Buy and Take Item")){

                                Toast.makeText(ForMeetUpActivity.this, "DIY name: " + itemRef.getDiyName() + " = " + position, Toast.LENGTH_SHORT).show();

                                final DIYSell promoFreeItem = promoFreeList.get(position);
                                Log.e("freeItemP", promoFreeItem.getDiyName());

                                promoReference.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        for(final DataSnapshot promoBuyTakeSnap : dataSnapshot.getChildren()){
                                            PromoModel buytakePromo = promoBuyTakeSnap.getValue(PromoModel.class);
                                            Log.e("buytakePromo", buytakePromo.getPromo_diyName());

                                            Log.e("buyTakeKey", promoBuyTakeSnap.getKey());
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


                                diyReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for(final DataSnapshot snaps : dataSnapshot.getChildren()){
                                            final DIYSell diySellinfo = snaps.getValue(DIYSell.class);
                                            Log.e("diySellinfoID", diySellinfo.getProductID()); //tanan prodID sa DIY sa DB

                                            if(item != null){

                                                if (promoFreeItem.getDiyName().equals(diySellinfo.getDiyName())){
                                                    Log.e("Equalss?", promoFreeItem.getDiyName() + " = " + diySellinfo.getDiyName());
                                                    freeKeyxDIY.add(snaps.getKey()); //gi sud ang key sa free promo item
                                                    Log.e("freeKeyxDIY", snaps.getKey());

                                                    Log.e("proomoFreeQty", promoFreeItem.getSelling_price() + " = " + promoFreeItem.getSelling_qty());
                                                }

                                                if(item.getProductID().equals(diySellinfo.getProductID())){
                                                    Log.e("itemProdID", item.getProductID()); //kaning item kay items na naa sa lv
                                                    Log.e("diySellPRodeID", diySellinfo.getProductID()); //prodID sa DB na ni equal sa LV

                                                    snapKey.add(snaps.getKey()); //get key sa diy  na naa sa diy_by_tags na ni equal sa forMeetUp items
                                                    Log.e("keyyDIYName", snaps.getKey() );


                                                    AlertDialog.Builder ab = new AlertDialog.Builder(ForMeetUpActivity.this, R.style.MyAlertDialogStyle);
                                                    ab.setTitle("Approval Meet-up");
                                                    ab.setMessage("Are you done meeting with the buyer?");

                                                    ab.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            final Float float_this = Float.valueOf(0);

                                                            //ika done, maadto siya sa sold item, nya ari mabutang ang counter para sa qty
                                                            soldReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items").child(userID);

                                                            final String sold_diyName = meetUpList.get(position).getDiyName();
                                                            final String sold_diyUrl = meetUpList.get(position).getDiyUrl();
                                                            final String sold_user_id = meetUpList.get(position).getUser_id();
                                                            final String sold_productID = meetUpList.get(position).getProductID();
                                                            String sold_status = meetUpList.get(position).getIdentity();
                                                            final String sold_buyer = meetUpList.get(position).getBuyerID();
                                                            final double sold_price = meetUpList.get(position).getSelling_price();
                                                            String sellerName = meetUpList.get(position).getLoggedInUser();
                                                            int sold_qty = meetUpList.get(position).getSelling_qty();
                                                            final int buyQty = meetUpList.get(position).getBuyingQuantity();

                                                            DIYSell product = new DIYSell(sold_diyName, sold_diyUrl, sold_user_id,
                                                                    sold_productID, "Delivered Buy and Take Item", float_this, float_this,
                                                                    sold_buyer, sellerName, buyQty);

                                                            final String upload = soldReference.push().getKey();
                                                            soldReference.child(upload).setValue(product);
                                                            soldReference.child(upload).child("userStatus").setValue("seller");
                                                            soldReference.child(upload).child("selling_price").setValue(sold_price);
                                                            soldReference.child(upload).child("selling_qty").setValue(sold_qty);

                                                            //confirm promo DIY, push to meet up
                                                            for (int pos=0; pos < hasFree.size(); pos++) {
                                                                if(promokey.get(position).equals(hasFree.get(pos))){
                                                                    Log.e("equalsSilaa", "YESS" + " " + promokey.get(position) + " = " + hasFree.get(pos));
                                                                    soldReference.child(upload).setValue(product);
                                                                    soldReference.child(upload).child("userStatus").setValue("seller");
                                                                    soldReference.child(upload).child("selling_price").setValue(sold_price);
                                                                    soldReference.child(upload).child("selling_qty").setValue(sold_qty);
                                                                    soldReference.child(upload).child("freeItemList").setValue(promoFreeList);
                                                                    soldReference.child(upload).child("freeItemQuantity").setValue(freeItemQty);

                                                                    freeSnapKey.add(snaps.getKey()); //get key sa diy_by_tags na ni equal sa key sa freeItem
                                                                    Log.e("freeSnapKey", snaps.getKey());//key ni siya sa item sa promo jud, dli free, DAPAT SULOD PAS FREEITEMLIST

                                                                    //get DIY TOTAL quantity sa free item promo
                                                                    int freeMeetUpQty = meetUpList.get(position).getSelling_qty();
                                                                    Log.e("freeMeetUpQty", String.valueOf(freeMeetUpQty));

                                                                    //get ang quantity nga free sa promo
                                                                    int freeDIYItemQty = Integer.parseInt(String.valueOf(freeItemQty));
                                                                    Log.e("freeDIYItemQty", String.valueOf(freeDIYItemQty));

                                                                    //minus freeItemList selling_qty ug freeItemQuantity
                                                                    int freeQtyCounter = freeMeetUpQty - freeDIYItemQty;
                                                                    Log.e("freeQtyCounter", String.valueOf(freeQtyCounter));

                                                                }else{
                                                                    soldReference.child(upload).setValue(product);
                                                                    soldReference.child(upload).child("userStatus").setValue("seller");
                                                                    soldReference.child(upload).child("selling_price").setValue(sold_price);
                                                                    soldReference.child(upload).child("selling_qty").setValue(sold_qty);
                                                                }

                                                            }


                                                            loggedInName.child(sold_buyer).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                                                                    loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);

                                                                    //DBref for buyer
                                                                    DatabaseReference soldReference = FirebaseDatabase.getInstance().getReference()
                                                                            .child("Sold_Items").child(sold_buyer);

                                                                    DIYSell buyProduct = new DIYSell(sold_diyName, sold_diyUrl, sold_user_id,
                                                                            sold_productID, "Purchased Buy and Take Item", float_this, float_this,
                                                                            sold_buyer, loggedInUserName, buyQty);

                                                                    String buyUpload = soldReference.child(upload).getKey();
                                                                    soldReference.child(buyUpload).setValue(buyProduct);
                                                                    soldReference.child(buyUpload).child("userStatus").setValue("buyer");
                                                                    soldReference.child(buyUpload).child("selling_price").setValue(sold_price);

                                                                    //confirm pending promo DIY, push to meet up
                                                                    for (int post =0; post < hasFree.size(); post++) {
                                                                        if(promokey.get(position).equals(hasFree.get(post))){
                                                                            Log.e("equalsSilaaa", "YESS" + " " + promokey.get(position) + " = " + hasFree.get(post));

                                                                            soldReference.child(buyUpload).setValue(buyProduct);
                                                                            soldReference.child(buyUpload).child("userStatus").setValue("buyer");
                                                                            soldReference.child(buyUpload).child("selling_price").setValue(sold_price);
                                                                            soldReference.child(buyUpload).child("freeItemList").setValue(promoFreeList);
                                                                            soldReference.child(buyUpload).child("freeItemQuantity").setValue(freeItemQty);
                                                                        } else{
                                                                            soldReference.child(buyUpload).setValue(buyProduct);
                                                                            soldReference.child(buyUpload).child("userStatus").setValue("buyer");
                                                                            soldReference.child(buyUpload).child("selling_price").setValue(sold_price);
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                            });


                                                            DatabaseReference meetReference = FirebaseDatabase.getInstance().getReference()
                                                                    .child("Items_ForMeetUp").child(sold_buyer);

                                                            //remove my item and send to SOLD_Items
                                                            String key = meetupKey.get(position);
                                                            String keys = meetupKeyyy.get(position);

                                                            meetUpRef.child(key).removeValue();
                                                            meetReference.child(keys).removeValue();

                                                            //get DIY total quantity sa DB sa promo Item
                                                            int meetUpQty = meetUpList.get(position).getSelling_qty();
                                                            Log.e("meetUpQty", String.valueOf(meetUpQty));

                                                            //free quantity sa promo item
                                                            int freeQty = Integer.parseInt(String.valueOf(freeItemQty));
                                                            Log.e("freeQty", String.valueOf(freeQty));

                                                            String message_price="";
                                                            String message_qty = "";
                                                            String message_dsc = "";
                                                            List<String> message_Price = new ArrayList<String>();
                                                            List<String> message_Qty = new ArrayList<String>();
                                                            final List<String> message_Dsc = new ArrayList<String>();
//
                                                            //get price key
                                                            for(DataSnapshot priceSnap : snaps.child("DIY Price").getChildren()){
                                                                Log.e("priceSnap", String.valueOf(priceSnap));
                                                                Log.e("priceSnapKey", priceSnap.getKey());
                                                                double price = priceSnap.child("selling_price").getValue(double.class);
                                                                int qty = priceSnap.child("selling_qty").getValue(int.class);
                                                                String dsc = priceSnap.child("selling_descr").getValue(String.class);

                                                                message_qty += qty;
                                                                message_Qty.add(message_qty);

                                                                message_price += price;
                                                                message_Price.add(message_price);

                                                                message_dsc +=dsc;
                                                                message_Dsc.add(message_dsc);

                                                                Log.e("message_qty","" +  message_qty);
                                                                Log.e("message_price", "" + message_price);

                                                                prices.add(message_qty);
                                                                Log.e("pricess", String.valueOf(prices));

                                                                //decrease quantity sa promo Item
                                                                int quantityCountMeetUp = meetUpQty - buyQty; //if dli promo
                                                                Log.e("quantityCountMeetUp", String.valueOf(quantityCountMeetUp));

                                                                //decrease quantity sa free item
                                                                int promoQtyCountMeetUp = promoFreeItem.getSelling_qty() - freeQty;
                                                                Log.e("promoQtyCountMeetUp", String.valueOf(promoQtyCountMeetUp));

                                                                //update quantity count
                                                                String penkey = snapKey.get(position);
                                                                Log.e("penKey", penkey);

                                                                String promKey = freeKeyxDIY.get(position);
                                                                Log.e("promKey", promKey);

                                                                HashMap<String, Object> results = new HashMap<>();
                                                                results.put("selling_qty", quantityCountMeetUp);
                                                                diyReference.child(penkey).child("DIY Price")
                                                                        .child(priceSnap.getKey()).updateChildren(results);

                                                                HashMap<String, Object> freeResults = new HashMap<>();
                                                                freeResults.put("selling_qty", promoQtyCountMeetUp);
                                                                diyReference.child(promKey).child("DIY Price")
                                                                        .child(priceSnap.getKey()).updateChildren(freeResults);


                                                                soldReference.child(upload).updateChildren(results);

                                                                Intent intent = new Intent(ForMeetUpActivity.this, MainActivity.class);
                                                                startActivity(intent);

                                                            }

                                                        }
                                                    });

                                                    ab.setNegativeButton("NOT YET", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });

                                                    ab.create().show();
                                                }



                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }

                                });

                            }

                            else{
                                Toast.makeText(ForMeetUpActivity.this, "For seller meet-up.", Toast.LENGTH_SHORT).show();
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
