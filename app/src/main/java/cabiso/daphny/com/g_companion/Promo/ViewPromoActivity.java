package cabiso.daphny.com.g_companion.Promo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.InstantMessaging.ui.activities.ChatSplashActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;

public class ViewPromoActivity extends AppCompatActivity {

    private TextView viewPromoName, viewPromoPrice, buyCounts, freeItems, freeItemName, tvSellerName, tvExpiration, buyText, gettText, promo_qty;
    private ImageView buyThisImageView, freeImageView, freeIcon;
    private DatabaseReference promoReference, userData, diyByTagsReference, pending_reference, loggedInName;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_promo);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        viewPromoName = (TextView) findViewById(R.id.view_promo_item_diyName);
        viewPromoPrice = (TextView) findViewById(R.id.promo_selling_price);
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

        promoReference = FirebaseDatabase.getInstance().getReference().child("promo_sale");
        userData = FirebaseDatabase.getInstance().getReference().child("userdata");
        diyByTagsReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        pending_reference = FirebaseDatabase.getInstance().getReference("DIY Pending Items").child(userID);
        loggedInName = FirebaseDatabase.getInstance().getReference().child("userdata");

        promoList = new ArrayList<>();

        final String get_name = getIntent().getStringExtra("Pname");
        Log.e("getPromoName", get_name);
        viewPromoName.setText(get_name);


        promoReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final PromoModel promoDiys = dataSnapshot.getValue(PromoModel.class);
                final PriceDiscountModel discountDiys = dataSnapshot.getValue(PriceDiscountModel.class);

                Log.e("promoDiysName", promoDiys.getPromo_diyName());
                Log.e("discountDiysName", discountDiys.getPromo_diyName());

                if(get_name.equalsIgnoreCase(promoDiys.getPromo_diyName())){

                    if(promoDiys.getStatus().equalsIgnoreCase("wholesale")){
                        Log.e("wholesaleDetails", promoDiys.getPromo_details() + " = " + promoDiys.getPromo_id());

                        buyCounts.setText(promoDiys.buy_counts);
                        Glide.with(getApplicationContext()).load(promoDiys.getPromo_image()).into(buyThisImageView);

                        final String sellerName = dataSnapshot.child("sellerName").getValue().toString();
                        final String promoSellerID = dataSnapshot.child("sellerID").getValue().toString();
                        Log.e("sellerNameeee", sellerName);
                        tvSellerName.setText("By:" + " " + sellerName);

                        final double promoPrice = dataSnapshot.child("promoPrice").getValue(double.class);
                        final int promoQty = dataSnapshot.child("promoQuantity").getValue(int.class);
                        String promoExpiry = dataSnapshot.child("promo_expiry").getValue().toString();

                        viewPromoPrice.setText("Promo Price: " + " " + promoPrice);
                        tvExpiration.setText("Promo expires on: " + " " + promoExpiry  + " !");
                        promo_qty.setText(promoQty + " " + "pieces left");

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
                                    if(sellerID.equals(user_profile.getUserID())) {
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
                                    loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);
                                    Log.e("loggedInUser", loggedInUserName);
                                    if(sellerName.equals(loggedInUserName)){
                                        buyPromoBtn.setVisibility(View.INVISIBLE);
                                        contactPromoSellerBtn.setVisibility(View.INVISIBLE);
                                    }
                                    else{
                                        buyPromoBtn.setVisibility(View.VISIBLE);
                                        contactPromoSellerBtn.setVisibility(View.VISIBLE);
                                    }
                                }



                                @Override
                                public void onCancelled(DatabaseError databaseError) {

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

                                            for (DataSnapshot forPromoSnapshot : dataSnapshot.getChildren()){
                                                DIYSell productInfo =  forPromoSnapshot.getValue(DIYSell.class);
                                                Log.e("diySellInfoName", productInfo.getDiyName());

                                                if(productInfo.diyName.equalsIgnoreCase(get_name)){

                                                    final String diyName = productInfo.getDiyName();
                                                    final String diyUrl = productInfo.getDiyUrl();
                                                    final String user_id = productInfo.getUser_id();
                                                    final String sellerName = productInfo.getLoggedInUser();
                                                    final String productID = productInfo.getProductID();
                                                    String status = productInfo.getIdentity();
                                                    final String buyerid = userID;


                                                    if(productID.equals(promoDiys.getPromo_id())){
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
                                                                    }

                                                                    else {

                                                                        int freeItemCount = Integer.parseInt(freeItems.getText().toString());
                                                                        int buyItemCount = Integer.parseInt(promoDiys.buy_counts);

                                                                        DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, "Pending Buy and Take Promo Item", float_this,
                                                                                float_this, buyerid, loggedInUserName, buyItemCount);
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
                                                                                float_this, buyerid, sellerName, buyItemCount);
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


                if(get_name.equalsIgnoreCase(discountDiys.getPromo_diyName())){
                    if(discountDiys.getStatus().equalsIgnoreCase("discount")){
                        Log.e("discountDetails", discountDiys.getPromo_details() + " = " + discountDiys.getPromo_id());
                        buyCounts.setVisibility(View.INVISIBLE);
                        freeItems.setVisibility(View.INVISIBLE);
                        freeIcon.setVisibility(View.INVISIBLE);
                        freeLayout.setVisibility(View.INVISIBLE);
                        freeImageView.setVisibility(View.INVISIBLE);
                        buyText.setVisibility(View.INVISIBLE);
                        gettText.setVisibility(View.INVISIBLE);

                        final String discountSellerID = dataSnapshot.child("sellerID").getValue().toString();
                        final String totalDIYQty = dataSnapshot.child("sellDIYqty").getValue().toString();
                        final String sellerName = dataSnapshot.child("sellerName").getValue().toString();
                        Log.e("sellerNameeee", sellerName);
                        tvSellerName.setText("By: " + " " + sellerName);
                        String promoExpiry = dataSnapshot.child("promo_expiry").getValue().toString();

                        viewPromoPrice.setText("New Price: " + " " + discountDiys.getPromo_newPrice());
                        tvExpiration.setText("Promo expires on: " + " " + promoExpiry + " !");
                        promo_qty.setText(totalDIYQty + " " + "pieces left");

                        freeItemName.setText(discountDiys.getPercent_discount() + " " + "off");
                        Glide.with(getApplicationContext()).load(discountDiys.getPromo_image()).into(buyThisImageView);

                        userData.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                                loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);

                                if(sellerName.equals(loggedInUserName)){
                                    buyPromoBtn.setVisibility(View.INVISIBLE);
                                    contactPromoSellerBtn.setVisibility(View.INVISIBLE);
                                }
                                else{
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
                                if(discountSellerID.equals(user_profile.getUserID())) {
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

                        buyPromoBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(ViewPromoActivity.this, "Buy Discount Promo Button", Toast.LENGTH_SHORT).show();

                                diyByTagsReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final Float float_this = Float.valueOf(0);

                                        for (DataSnapshot forPromoSnapshot : dataSnapshot.getChildren()){
                                            DIYSell productInfo =  forPromoSnapshot.getValue(DIYSell.class);
                                            Log.e("diySellInfoName", productInfo.getDiyName());

                                            if(productInfo.diyName.equalsIgnoreCase(get_name)){

                                                final String diyName = productInfo.getDiyName();
                                                final String diyUrl = productInfo.getDiyUrl();
                                                final String user_id = productInfo.getUser_id();
                                                final String sellerName = productInfo.getLoggedInUser();
                                                final String productID = productInfo.getProductID();
                                                String status = productInfo.getIdentity();
                                                final String buyerid = userID;


                                                if(productID.equals(discountDiys.getPromo_id())){
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
                                                                }

                                                                else {

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

                                                                            if(inputQty.equals(" ")){
                                                                                inputQty="0";
                                                                            }

                                                                            if (inputQty.isEmpty()) {
                                                                                Log.e("EmptyQty", "Please enter the quantity of item you want to buy.");
                                                                                Toast.makeText(ViewPromoActivity.this, "Please enter how many DIY " + diyName  + " you want to buy." + "", Toast.LENGTH_SHORT).show();
                                                                            } else if(Integer.valueOf(inputQty) > buyItemCount){
                                                                                Log.e("NotEnoughQty", "NotEnoughQty");
                                                                                Toast.makeText(ViewPromoActivity.this, "Not enough total DIY quantity. We only have " + " " + buyItemCount
                                                                                        + " DIY " + diyName + ".", Toast.LENGTH_LONG).show();
                                                                            } else{

                                                                                String promoPrice = discountDiys.getPromo_newPrice();
                                                                                String discount = discountDiys.getPercent_discount();
                                                                                int buyQty = Integer.parseInt(qtyInputted.getText().toString());


                                                                                DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, "Pending Discount Promo Item", float_this,
                                                                                        float_this, buyerid, loggedInUserName, buyQty);

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
                                                                                        float_this, buyerid, sellerName, buyQty);
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

                        Intent intent = new Intent(ViewPromoActivity.this, ChatSplashActivity.class);
                        startActivity(intent);
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




    }
}