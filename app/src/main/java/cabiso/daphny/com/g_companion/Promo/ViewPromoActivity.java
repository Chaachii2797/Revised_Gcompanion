package cabiso.daphny.com.g_companion.Promo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cabiso.daphny.com.g_companion.Bidding.ToBidProduct;
import cabiso.daphny.com.g_companion.InstantMessaging.ui.activities.ChatActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.PushNotification;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.ViewRelatedDIYS;

public class ViewPromoActivity extends AppCompatActivity {

    private TextView viewPromoName, viewOrigPromoPrice, viewNewPromoPrice, buyCounts, freeItems, freeItemName, tvSellerName, tvExpiration, buyText, gettText, promo_qty;
    private ImageView buyThisImageView, freeImageView, freeIcon, soldOutItem;
    private DatabaseReference promoReference, userData, diyByTagsReference, pending_reference, loggedInName, diyByTagsUpdate;
    private DatabaseReference expirationReference, identityReferenceWholesale;
    private List<PromoModel> mPromoModels;
    private List<PriceDiscountModel> mDiscountModels;
    private LinearLayout freeLayout;
    private Button buyPromoBtn, contactPromoSellerBtn;
    private String user_name;
    private String sellerContactNo;
    private String userID;
    private FirebaseUser mFirebaseUser;
    private String loggedInUserName;
    private ArrayList<DIYnames> promoList;
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private ArrayList<PriceDiscountModel> discountNames = new ArrayList<>();
    private ArrayList<String> statusKey = new ArrayList<>();
    private DatabaseReference user_reference;
    private User_Profile relOwner;
    private Button bid_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_promo);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        viewPromoName = (TextView) findViewById(R.id.view_promo_item_diyName);
        viewOrigPromoPrice = (TextView) findViewById(R.id.promo_selling_orig_price);
        viewNewPromoPrice = (TextView) findViewById(R.id.promo_selling_new_price);

        buyCounts = (TextView) findViewById(R.id.buyCountsQty);
        freeItems = (TextView) findViewById(R.id.freeItemQty);
        buyThisImageView = (ImageView) findViewById(R.id.img_buy_view_promo);
        freeImageView = (ImageView) findViewById(R.id.img_free_view_promo);
        freeIcon = (ImageView) findViewById(R.id.freeImg);
        freeLayout = (LinearLayout) findViewById(R.id.freeLayout);
        freeItemName = (TextView) findViewById(R.id.view_promo_free_item);
        buyPromoBtn = (Button) findViewById(R.id.btnBuyPromo);
        contactPromoSellerBtn = (Button) findViewById(R.id.btnContactPromoSeller);
        tvSellerName = (TextView) findViewById(R.id.sellerNameTv);
        tvExpiration = (TextView) findViewById(R.id.tvPromoExpiration);
        buyText = (TextView) findViewById(R.id.buyText);
        gettText = (TextView) findViewById(R.id.gettText);
        promo_qty = (TextView) findViewById(R.id.promoQty);
        soldOutItem = (ImageView) findViewById(R.id.sold_out_sign);
        bid_btn = (Button) findViewById(R.id.btn_bid_item);

        promoReference = FirebaseDatabase.getInstance().getReference().child("promo_sale");
        expirationReference = FirebaseDatabase.getInstance().getReference().child("promo_sale");
        userData = FirebaseDatabase.getInstance().getReference().child("userdata");
        diyByTagsReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        pending_reference = FirebaseDatabase.getInstance().getReference("DIY Pending Items").child(userID);
        loggedInName = FirebaseDatabase.getInstance().getReference().child("userdata");
        diyByTagsUpdate = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        user_reference = FirebaseDatabase.getInstance().getReference().child("userdata");

        promoList = new ArrayList<>();

        final String get_name = getIntent().getStringExtra("Pname");
        Log.e("getPromoName", get_name);
        viewPromoName.setText(get_name);


        promoReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                final PromoModel promoDiys = dataSnapshot.getValue(PromoModel.class);
                final PriceDiscountModel discountDiys = dataSnapshot.getValue(PriceDiscountModel.class);
//                identityReferenceDiscount = FirebaseDatabase.getInstance().getReference().child(dataSnapshot.getKey());

                Log.e("promoDiysName", promoDiys.getPromo_diyName());
                Log.e("discountDiysName", discountDiys.getPromo_diyName());

                boolean isExpired = false;

                if (get_name.equalsIgnoreCase(promoDiys.getPromo_diyName())) {
                    if (promoDiys.getStatus().equalsIgnoreCase("wholesale")) {

                        Log.e("wholesaleDetails", promoDiys.getPromo_details() + " = " + promoDiys.getPromo_id());
                        soldOutItem.setVisibility(View.INVISIBLE);

                        discountNames.add(discountDiys);
                        Log.e("discountKayee", String.valueOf(discountNames));

                        buyCounts.setText(promoDiys.buy_counts);
                        Glide.with(getApplicationContext()).load(promoDiys.getPromo_image()).into(buyThisImageView);

                        final String sellerName = dataSnapshot.child("sellerName").getValue().toString();
                        final String promoSellerID = dataSnapshot.child("sellerID").getValue().toString();
                        Log.e("sellerID", promoSellerID);
                        Log.e("sellerNameeee", sellerName);
                        tvSellerName.setText("By:" + " " + sellerName);

                        user_reference.child(promoSellerID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                relOwner = dataSnapshot.getValue(User_Profile.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        final double promoPrice = dataSnapshot.child("promoPrice").getValue(double.class);
                        final int promoQty = dataSnapshot.child("sellDIYqty").getValue(int.class);
                        String promoExpiry = dataSnapshot.child("promo_expiry").getValue().toString();

                        Double priceXBuy = promoPrice * Double.parseDouble(promoDiys.buy_counts);

                        viewOrigPromoPrice.setText("Price: " + " " + promoPrice + 0 + " each");
                        viewNewPromoPrice.setText("New Price: " + " " + priceXBuy + 0);
                        viewNewPromoPrice.setTextColor(Color.RED);
                        tvExpiration.setText("Promo expires on: " + " " + promoExpiry + " !");
                        promo_qty.setText(promoQty + " " + "pieces left");

                        if (promoQty == 0) {
                            soldOutItem.setVisibility(View.VISIBLE);
                        }

                        try {
                            // check expiry
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                            Date strDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(promoExpiry);
                            if (new Date().after(strDate)) {
                                isExpired = true;
                            }
                        } catch (ParseException e) {
                            Log.e("expiryException", e.getMessage());
                        }
                        Log.e("CHECKEXPIRATION", isExpired + "" + " = " + viewPromoName.getText());


                        if (isExpired) {
                            expirationReference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.e("keysss", dataSnapshot.getKey());

                                    final PriceDiscountModel diyPrice = dataSnapshot.getValue(PriceDiscountModel.class);
                                    Log.e("diyPricee", diyPrice.getPromo_diyName());

                                    if (get_name.equals(diyPrice.getPromo_diyName())) {
                                        Log.e("equalss", get_name + " = " + diyPrice.getPromo_diyName());

                                        statusKey.add(dataSnapshot.getKey());
                                        Log.e("statusKeyy", dataSnapshot.getKey());

                                        HashMap<String, Object> promoresult = new HashMap<>();
                                        promoresult.put("status", "Selling");
                                        expirationReference.child(dataSnapshot.getKey()).updateChildren(promoresult);

                                        expirationReference.child(dataSnapshot.getKey()).removeValue();

                                        //Notification
                                        user_reference.addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
//                                    if(loggedInUserName!=null){
                                                PushNotification pushNotification = new PushNotification(getApplicationContext());
                                                pushNotification.title("Expire On Sale Item Notification")
                                                        .message("On sale " + diyPrice.getPromo_diyName() + " is now expire!")
                                                        .accessToken(user_profile.getAccess_token())
                                                        .send();
//                                    }
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

                            diyByTagsUpdate.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.e("keyssOfLife", dataSnapshot.getKey());

                                    DIYSell sellUpdate = dataSnapshot.getValue(DIYSell.class);
                                    Log.e("sellUpdate", sellUpdate.getDiyName());

                                    if (get_name.equals(sellUpdate.getDiyName())) {
                                        Log.e("equalsBa", get_name + " = " + sellUpdate.getDiyName());
                                        statusKey.add(dataSnapshot.getKey());
                                        Log.e("statusKeyy", dataSnapshot.getKey());

                                        HashMap<String, Object> result = new HashMap<>();
                                        result.put("identity", "Selling");
                                        diyByTagsReference.child(dataSnapshot.getKey()).updateChildren(result);

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


                            tvExpiration.setVisibility(View.INVISIBLE);
                        }

                        for (DataSnapshot freeSnapshot : dataSnapshot.child("freeItemQuantity").getChildren()) {
                            Log.e("freeSnapsht", String.valueOf(freeSnapshot.getValue()));
                            String free = freeSnapshot.getValue().toString();
                            freeItems.setText(free);
                        }


                        //for getting freeItems promo
                        for (DataSnapshot imageSnapshot : dataSnapshot.child("freeItemList").getChildren()) {
                            final DIYnames diYnames = imageSnapshot.getValue(DIYnames.class);
                            Log.e("diYnamesss", diYnames.diyName);
                            promoList.add(diYnames);


                            String freeImage = imageSnapshot.child("diyUrl").getValue().toString();
                            Log.e("freeImagee", freeImage);
                            Glide.with(getApplicationContext()).load(freeImage).into(freeImageView);

                            String freeDiyName = imageSnapshot.child("diyName").getValue().toString();
                            freeItemName.setText(freeDiyName);

                            final String sellerID = imageSnapshot.child("user_id").getValue().toString();

                            userData.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                    if (sellerID.equals(user_profile.getUserID())) {
                                        user_name = user_profile.getF_name() + " " + user_profile.getL_name();
                                        Log.e("user_owner_name", user_name);
                                        sellerContactNo = user_profile.getContact_no();
                                        Log.e("ownerIDD", sellerID);

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
                                    loggedInUserName += " " + dataSnapshot.child("l_name").getValue(String.class);
                                    Log.e("loggedInUser", loggedInUserName);
                                    if (sellerName.equals(loggedInUserName)) {
                                        buyPromoBtn.setVisibility(View.INVISIBLE);
                                        contactPromoSellerBtn.setVisibility(View.INVISIBLE);
                                    } else {
                                        buyPromoBtn.setVisibility(View.VISIBLE);
                                        contactPromoSellerBtn.setVisibility(View.VISIBLE);
                                    }
                                }


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            bid_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(ViewPromoActivity.this,"clicked!",Toast.LENGTH_SHORT);
                                    Intent item = new Intent(ViewPromoActivity.this,ToBidProduct.class);
                                    item.putExtra("itemId", dataSnapshot.getKey());
                                    item.putExtra("sellerName", loggedInUserName);
                                    Log.e("sellerNameBid", loggedInUserName);
                                    startActivity(item);
                                }
                            });

                            buyPromoBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(ViewPromoActivity.this, "Buy Promo Button", Toast.LENGTH_SHORT).show();

                                    diyByTagsReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Toast.makeText(ViewPromoActivity.this, "Buy button clicked!", Toast.LENGTH_SHORT).show();
                                            final Float float_this = Float.valueOf(0);

                                            for (DataSnapshot forPromoSnapshot : dataSnapshot.getChildren()) {
                                                DIYSell productInfo = forPromoSnapshot.getValue(DIYSell.class);
                                                Log.e("diySellInfoName", productInfo.getDiyName());

                                                if (productInfo.diyName.equalsIgnoreCase(get_name)) {

                                                    final String diyName = productInfo.getDiyName();
                                                    final String diyUrl = productInfo.getDiyUrl();
                                                    final String user_id = productInfo.getUser_id();
                                                    final String sellerName = productInfo.getLoggedInUser();
                                                    final String productID = productInfo.getProductID();
                                                    String status = productInfo.getIdentity();
                                                    final String buyerid = userID;


                                                    if (productID.equals(promoDiys.getPromo_id())) {
                                                        Log.e("diyProdID", productID);
                                                        Log.e("promoProdID", promoDiys.getPromo_id());


                                                        if (!userID.equals(productInfo.getUser_id())) {
                                                            Log.e("pending_not_same", String.valueOf("" + userID != productInfo.getUser_id()));
                                                            Log.e("userid", String.valueOf("" + userID));
                                                            Log.e("info_userid", String.valueOf("" + productInfo.getUser_id()));

                                                            pending_reference.orderByChild("productID").equalTo(productInfo.productID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    if (dataSnapshot.exists()) {
                                                                        Log.e("same_prodID", "" + dataSnapshot.exists());
                                                                        Log.e("same_data", "" + dataSnapshot);

                                                                        final Dialog dialog = new Dialog(ViewPromoActivity.this);
                                                                        dialog.setContentView(R.layout.exist_dialog);
                                                                        TextView text = (TextView) dialog.findViewById(R.id.e_text);
                                                                        text.setText("DIY already added to pending list!");
                                                                        ImageView image =
                                                                                (ImageView) dialog.findViewById(R.id.exist_dialog_imageview);
                                                                        image.setImageResource(R.drawable.exist);

                                                                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOKI);
                                                                        dialogButton.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                Intent intent = new Intent(ViewPromoActivity.this, MainActivity.class);
                                                                                startActivity(intent);
                                                                            }
                                                                        });
                                                                        dialog.show();
                                                                    } else {

                                                                        int freeItemCount = Integer.parseInt(freeItems.getText().toString());
                                                                        int buyItemCount = Integer.parseInt(promoDiys.buy_counts);

                                                                        DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, "Pending Buy and Take Promo Item", float_this,
                                                                                float_this, buyerid, loggedInUserName, buyItemCount, 0, 0);
                                                                        final String upload_info = pending_reference.push().getKey();
                                                                        pending_reference.child(upload_info).setValue(info);
                                                                        pending_reference.child(upload_info).child("selling_price").setValue(promoPrice);
                                                                        pending_reference.child(upload_info).child("userStatus").setValue("buyer");
                                                                        pending_reference.child(upload_info).child("freeItemList").setValue(promoList);
                                                                        pending_reference.child(upload_info).child("freeItemQuantity").setValue(freeItemCount);


                                                                        //DBref for buyer
                                                                        final DatabaseReference pendingRefByOwner = FirebaseDatabase.getInstance().getReference("DIY Pending Items")
                                                                                .child(user_id); //userID sa seller na
                                                                        Log.e("pendingRefByOwner", String.valueOf(pendingRefByOwner));

                                                                        //BuyerInfo - userID sa buyer
                                                                        Log.e("userIDDD", userID);

                                                                        DIYSell buyer = new DIYSell(diyName, diyUrl, user_id, productID, "For Confirmation Buy and Take Promo Item", float_this,
                                                                                float_this, buyerid, sellerName, buyItemCount, 0, 0);
                                                                        final String uploadBuyerInfo = pendingRefByOwner.child(upload_info).getKey();
                                                                        pendingRefByOwner.child(uploadBuyerInfo).setValue(buyer);
                                                                        pendingRefByOwner.child(uploadBuyerInfo).child("selling_price").setValue(promoPrice);
                                                                        pendingRefByOwner.child(uploadBuyerInfo).child("selling_qty").setValue(promoQty);
                                                                        pendingRefByOwner.child(uploadBuyerInfo).child("userStatus").setValue("seller");
                                                                        pendingRefByOwner.child(uploadBuyerInfo).child("freeItemList").setValue(promoList);
                                                                        pendingRefByOwner.child(uploadBuyerInfo).child("freeItemQuantity").setValue(freeItemCount);

                                                                        final Dialog dialog = new Dialog(ViewPromoActivity.this);
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
                                                                                Intent intent = new Intent(ViewPromoActivity.this, MainActivity.class);
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
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            });
                        }
                    }
                }

                if (get_name.equalsIgnoreCase(discountDiys.getPromo_diyName())) {
                    if (discountDiys.getStatus().equalsIgnoreCase("discount")) {
                        Log.e("discountDetails", discountDiys.getPromo_details() + " = " + discountDiys.getPromo_id());
                        buyCounts.setVisibility(View.INVISIBLE);
                        freeItems.setVisibility(View.INVISIBLE);
                        freeIcon.setVisibility(View.INVISIBLE);
                        freeLayout.setVisibility(View.INVISIBLE);
                        freeImageView.setVisibility(View.INVISIBLE);
                        buyText.setVisibility(View.INVISIBLE);
                        gettText.setVisibility(View.INVISIBLE);
                        soldOutItem.setVisibility(View.INVISIBLE);

                        final String discountSellerID = dataSnapshot.child("sellerID").getValue().toString();
                        final String totalDIYQty = dataSnapshot.child("sellDIYqty").getValue().toString();
                        final String sellerName = dataSnapshot.child("sellerName").getValue().toString();
                        Log.e("sellerNameeee", sellerName);
                        tvSellerName.setText("By: " + " " + sellerName);
                        String promoExpiry = dataSnapshot.child("promo_expiry").getValue().toString();

                        Log.e("discountSellerID", discountSellerID);
                        user_reference.child(discountSellerID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                relOwner = dataSnapshot.getValue(User_Profile.class);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        String input_new_price = discountDiys.getPromo_newPrice();
                        double new_price = Double.parseDouble(input_new_price);
                        String originalPrice = discountDiys.getOriginalPrice();
                        Log.e("otrigPricee", originalPrice);

                        viewOrigPromoPrice.setText("P̶r̶i̶c̶e̶:̶ ̶" + " " + originalPrice + 0);
                        viewNewPromoPrice.setText("New Price: " + " " + new_price + 0);
                        viewNewPromoPrice.setTextColor(Color.RED);

                        tvExpiration.setText("Promo expires on: " + " " + promoExpiry + " !");
                        promo_qty.setText(totalDIYQty + " " + "pieces left");

                        int qtyLeft = Integer.parseInt(totalDIYQty);

                        if (qtyLeft == 0) {
                            soldOutItem.setVisibility(View.VISIBLE);
                            buyPromoBtn.setVisibility(View.INVISIBLE);
                            bid_btn.setVisibility(View.INVISIBLE);
                            contactPromoSellerBtn.setVisibility(View.INVISIBLE);
                        }

                        try {
                            // check expiry
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                            Date strDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(promoExpiry);
                            if (new Date().after(strDate)) {
                                isExpired = true;
                            }
                        } catch (ParseException e) {
                            Log.e("expiryException", e.getMessage());
                        }
                        Log.e("CHECKEXPIRATION", isExpired + "" + " = " + viewPromoName.getText());


                        if (isExpired) {

                            expirationReference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.e("keysss", dataSnapshot.getKey());

                                    PriceDiscountModel diyPrice = dataSnapshot.getValue(PriceDiscountModel.class);
                                    Log.e("diyPricee", diyPrice.getPromo_diyName());

                                    if (get_name.equals(diyPrice.getPromo_diyName())) {
                                        Log.e("equalss", get_name + " = " + diyPrice.getPromo_diyName());

                                        statusKey.add(dataSnapshot.getKey());
                                        Log.e("statusKeyy", dataSnapshot.getKey());

                                        HashMap<String, Object> promoresult = new HashMap<>();
                                        promoresult.put("status", "Selling");
                                        expirationReference.child(dataSnapshot.getKey()).updateChildren(promoresult);

                                        expirationReference.child(dataSnapshot.getKey()).removeValue();

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

                            diyByTagsUpdate.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.e("keyssOfLife", dataSnapshot.getKey());

                                    DIYSell sellUpdate = dataSnapshot.getValue(DIYSell.class);
                                    Log.e("sellUpdate", sellUpdate.getDiyName());

                                    if (get_name.equals(sellUpdate.getDiyName())) {
                                        Log.e("equalsBa", get_name + " = " + sellUpdate.getDiyName());
                                        statusKey.add(dataSnapshot.getKey());
                                        Log.e("statusKeyy", dataSnapshot.getKey());

                                        HashMap<String, Object> result = new HashMap<>();
                                        result.put("identity", "Selling");
                                        diyByTagsReference.child(dataSnapshot.getKey()).updateChildren(result);

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


                            tvExpiration.setVisibility(View.INVISIBLE);
                        }

                        freeItemName.setText(discountDiys.getPercent_discount() + " " + "off");
                        Glide.with(getApplicationContext()).load(discountDiys.getPromo_image()).into(buyThisImageView);

                        userData.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                                loggedInUserName += " " + dataSnapshot.child("l_name").getValue(String.class);

                                if (sellerName.equals(loggedInUserName)) {
                                    buyPromoBtn.setVisibility(View.INVISIBLE);
                                    contactPromoSellerBtn.setVisibility(View.INVISIBLE);
                                } else {
                                    buyPromoBtn.setVisibility(View.VISIBLE);
                                    contactPromoSellerBtn.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        userData.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
                                if (discountSellerID.equals(user_profile.getUserID())) {
                                    user_name = user_profile.getF_name() + " " + user_profile.getL_name();
                                    Log.e("user_owner_name", user_name);
                                    sellerContactNo = user_profile.getContact_no();
                                    Log.e("ownerIDD", String.valueOf(discountSellerID));

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

                        bid_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(ViewPromoActivity.this,"clicked!",Toast.LENGTH_SHORT);
                                Intent item = new Intent(ViewPromoActivity.this,ToBidProduct.class);
                                item.putExtra("itemId", dataSnapshot.child("promo_id").getValue(String.class));
                                item.putExtra("sellerName", loggedInUserName);
                                Log.e("sellerNameBid", loggedInUserName);
                                startActivity(item);
                            }
                        });

                        buyPromoBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(ViewPromoActivity.this, "Buy Discount Promo Button", Toast.LENGTH_SHORT).show();

                                diyByTagsReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final Float float_this = Float.valueOf(0);

                                        for (DataSnapshot forPromoSnapshot : dataSnapshot.getChildren()) {
                                            DIYSell productInfo = forPromoSnapshot.getValue(DIYSell.class);
                                            Log.e("diySellInfoName", productInfo.getDiyName());

                                            if (productInfo.diyName.equalsIgnoreCase(get_name)) {

                                                final String diyName = productInfo.getDiyName();
                                                final String diyUrl = productInfo.getDiyUrl();
                                                final String user_id = productInfo.getUser_id();
                                                final String sellerName = productInfo.getLoggedInUser();
                                                final String productID = productInfo.getProductID();
                                                String status = productInfo.getIdentity();
                                                final String buyerid = userID;


                                                if (productID.equals(discountDiys.getPromo_id())) {
                                                    Log.e("diyProdID", productID);
                                                    Log.e("promoProdID", discountDiys.getPromo_id());


                                                    if (!userID.equals(productInfo.getUser_id())) {
                                                        Log.e("pending_not_same", String.valueOf("" + userID != productInfo.getUser_id()));
                                                        Log.e("userid", String.valueOf("" + userID));
                                                        Log.e("info_userid", String.valueOf("" + productInfo.getUser_id()));

                                                        pending_reference.orderByChild("productID").equalTo(productInfo.productID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    Log.e("same_prodID", "" + dataSnapshot.exists());
                                                                    Log.e("same_data", "" + dataSnapshot);

                                                                    final Dialog dialog = new Dialog(ViewPromoActivity.this);
                                                                    dialog.setContentView(R.layout.exist_dialog);
                                                                    TextView text = (TextView) dialog.findViewById(R.id.e_text);
                                                                    text.setText("DIY already added to pending list!");
                                                                    ImageView image = (ImageView) dialog.findViewById(R.id.exist_dialog_imageview);
                                                                    image.setImageResource(R.drawable.exist);

                                                                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOKI);
                                                                    dialogButton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            Intent intent = new Intent(ViewPromoActivity.this, MainActivity.class);
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                                    dialog.show();
                                                                } else {

                                                                    final Dialog buyDialog = new Dialog(ViewPromoActivity.this);
                                                                    buyDialog.setContentView(R.layout.buying_quantity);
                                                                    final TextView qtyInputted = (TextView) buyDialog.findViewById(R.id.quantityInputted);

                                                                    Button dialogQtyButton = (Button) buyDialog.findViewById(R.id.btnQtyDialogDone);
                                                                    dialogQtyButton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {


                                                                            final int promoTotalDIYQty = Integer.parseInt(totalDIYQty);

                                                                            Log.e("qtyInputted", qtyInputted.getText().toString());
                                                                            buyDialog.dismiss();

                                                                            String inputQty = qtyInputted.getText().toString();

                                                                            int buyItemCount = Integer.parseInt(totalDIYQty);

                                                                            if (inputQty.equals(" ")) {
                                                                                inputQty = "0";
                                                                            }

                                                                            if (inputQty.isEmpty()) {
                                                                                Log.e("EmptyQty", "Please enter the quantity of item you want to buy.");
                                                                                Toast.makeText(ViewPromoActivity.this, "Please enter how many DIY " + diyName + " you want to buy." + "", Toast.LENGTH_SHORT).show();
                                                                            } else if (Integer.valueOf(inputQty) > buyItemCount) {
                                                                                Log.e("NotEnoughQty", "NotEnoughQty");
                                                                                Toast.makeText(ViewPromoActivity.this, "Not enough total DIY quantity. We only have " + " " + buyItemCount
                                                                                        + " DIY " + diyName + ".", Toast.LENGTH_LONG).show();
                                                                            } else {

                                                                                String promoPrice = discountDiys.getPromo_newPrice();
                                                                                String discount = discountDiys.getPercent_discount();
                                                                                int buyQty = Integer.parseInt(qtyInputted.getText().toString());


                                                                                DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, "Pending Discount Promo Item", float_this,
                                                                                        float_this, buyerid, loggedInUserName, buyQty, 0, 0);

                                                                                final String upload_info = pending_reference.push().getKey();
                                                                                pending_reference.child(upload_info).setValue(info);
                                                                                pending_reference.child(upload_info).child("selling_price").setValue(Double.parseDouble(promoPrice));
                                                                                pending_reference.child(upload_info).child("userStatus").setValue("buyer");
                                                                                pending_reference.child(upload_info).child("percent_discount").setValue(discount);


                                                                                //DBref for buyer
                                                                                final DatabaseReference pendingRefByOwner = FirebaseDatabase.getInstance().getReference("DIY Pending Items")
                                                                                        .child(user_id); //userID sa seller na
                                                                                Log.e("pendingRefByOwner", String.valueOf(pendingRefByOwner));

                                                                                //BuyerInfo - userID sa buyer
                                                                                Log.e("userIDDD", userID);

                                                                                DIYSell buyer = new DIYSell(diyName, diyUrl, user_id, productID, "For Confirmation Discount Promo Item", float_this,
                                                                                        float_this, buyerid, sellerName, buyQty, 0, 0);
                                                                                final String uploadBuyerInfo = pendingRefByOwner.child(upload_info).getKey();
                                                                                pendingRefByOwner.child(uploadBuyerInfo).setValue(buyer);
                                                                                pendingRefByOwner.child(uploadBuyerInfo).child("selling_price").setValue(Double.parseDouble(promoPrice));
                                                                                pendingRefByOwner.child(uploadBuyerInfo).child("selling_qty").setValue(promoTotalDIYQty);
                                                                                pendingRefByOwner.child(uploadBuyerInfo).child("userStatus").setValue("seller");
                                                                                pendingRefByOwner.child(uploadBuyerInfo).child("percent_discount").setValue(discount);


                                                                                final Dialog dialog = new Dialog(ViewPromoActivity.this);
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
                                                                                        Intent intent = new Intent(ViewPromoActivity.this, MainActivity.class);
                                                                                        startActivity(intent);
                                                                                    }
                                                                                });
                                                                                dialog.show();


                                                                            }


                                                                        }
                                                                    });
                                                                    buyDialog.show();

                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                            }
                                                        });
                                                    }

                                                }


                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });


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


        contactPromoSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewPromoActivity.this, "Contact Promo Seller", Toast.LENGTH_SHORT).show();

                final Dialog myDialog = new Dialog(ViewPromoActivity.this);
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
                        Toast.makeText(ViewPromoActivity.this, "Chat button clicked!", Toast.LENGTH_SHORT).show();

                        Log.e("CHAAAAATTTTTT", relOwner.getEmail());
                        ChatActivity.startActivity(getApplicationContext(), relOwner.getEmail(), relOwner.getUserID(), relOwner.getAccess_token());

                    }
                });
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ViewPromoActivity.this, "Call button clicked!", Toast.LENGTH_SHORT).show();

                        String phone = sellerContactNo;
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                "tel", phone, null));
                        startActivity(phoneIntent);
                    }
                });
                sms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ViewPromoActivity.this, "SMS button clicked!", Toast.LENGTH_SHORT).show();

                        String phone = sellerContactNo;
                        Intent smsMsgAppVar = new Intent(Intent.ACTION_VIEW);
                        smsMsgAppVar.setData(Uri.parse("sms:" + phone));
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


    }
}
