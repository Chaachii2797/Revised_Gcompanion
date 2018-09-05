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

import java.util.List;

import cabiso.daphny.com.g_companion.InstantMessaging.ui.activities.ChatSplashActivity;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;

public class ViewPromoActivity extends AppCompatActivity {

    private TextView viewPromoName, viewPromoPrice, buyCounts, freeItems, freeItemName;
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

        promoReference = FirebaseDatabase.getInstance().getReference().child("promo_sale");
        userData = FirebaseDatabase.getInstance().getReference().child("userdata");
        diyByTagsReference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        pending_reference = FirebaseDatabase.getInstance().getReference("DIY Pending Items").child(userID);
        loggedInName = FirebaseDatabase.getInstance().getReference().child("userdata");


        final String get_name = getIntent().getStringExtra("Pname");
        Log.e("getPromoName", get_name);
        viewPromoName.setText(get_name);


        promoReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PromoModel promoDiys = dataSnapshot.getValue(PromoModel.class);
                PriceDiscountModel discountDiys = dataSnapshot.getValue(PriceDiscountModel.class);

                Log.e("promoDiysName", promoDiys.getPromo_diyName());
                Log.e("discountDiysName", discountDiys.getPromo_diyName());

                if(get_name.equalsIgnoreCase(promoDiys.getPromo_diyName())){

                    if(promoDiys.getStatus().equalsIgnoreCase("wholesale")){
                        Log.e("wholesaleDetails", promoDiys.getPromo_details() + " = " + promoDiys.getPromo_id());

                        buyCounts.setText("Buy " + " " + promoDiys.buy_counts);
                        Glide.with(getApplicationContext()).load(promoDiys.getPromo_image()).into(buyThisImageView);

                        for (DataSnapshot freeSnapshot : dataSnapshot.child("freeItemQuantity").getChildren()) {
                            Log.e("freeSnapsht", String.valueOf(freeSnapshot.getValue()));
                            String free = freeSnapshot.getValue().toString();
                            freeItems.setText("Get " + " " + free);
                        }

                        for (DataSnapshot imageSnapshot : dataSnapshot.child("freeItemList").getChildren()) {
                            String freeImage = imageSnapshot.child("diyUrl").getValue().toString();
                            Log.e("freeImagee", freeImage);
                            Glide.with(getApplicationContext()).load(freeImage).into(freeImageView);

                            String message_price="";
                            double promoPrice = imageSnapshot.child("selling_price").getValue(double.class);
                            Log.e("promoPrice", String.valueOf(promoPrice));
                            message_price += promoPrice;
                            viewPromoPrice.setText("Promo Price: " + " " + message_price);

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

                }


                if(get_name.equalsIgnoreCase(discountDiys.getPromo_diyName())){
                    if(discountDiys.getStatus().equalsIgnoreCase("discount")){
                        Log.e("discountDetails", discountDiys.getPromo_details() + " = " + discountDiys.getPromo_id());
                        buyCounts.setVisibility(View.INVISIBLE);
                        freeItems.setVisibility(View.INVISIBLE);
                        freeItemName.setVisibility(View.INVISIBLE);
                        freeIcon.setVisibility(View.INVISIBLE);
                        freeLayout.setVisibility(View.INVISIBLE);
                        freeImageView.setVisibility(View.INVISIBLE);

                        viewPromoPrice.setText("New Price: " + " " + discountDiys.getPromo_newPrice());

                        Glide.with(getApplicationContext()).load(discountDiys.getPromo_image()).into(buyThisImageView);

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


        buyPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewPromoActivity.this, "Buy Promo Button", Toast.LENGTH_SHORT).show();

//                diyByTagsReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Toast.makeText(ViewPromoActivity.this, "Buy button clicked!", Toast.LENGTH_SHORT).show();
//                        final Float float_this = Float.valueOf(0);
//                        final DIYSell diySellinfo = dataSnapshot.getValue(DIYSell.class);
//
//                        String message_price="";
//                        String message_qty = "";
//                        List<String> message_Price = new ArrayList<String>();
//                        List<String> message_Qty = new ArrayList<String>();
//                        for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
//                            double price = postSnapshot.child("selling_price").getValue(double.class);
//                            int qty = postSnapshot.child("selling_qty").getValue(int.class);
//                            message_qty += qty;
//                            message_Qty.add(message_qty);
//                            message_price += price;
//                            message_Price.add(message_price);
//
//                        }
//
////                        //DIY price
////                        final float pendingPrice = Float.parseFloat((message_price));
////                        final int pendingQty = Integer.parseInt((message_qty));
//
//                        //Seller Info -> buyer side ni
//                        DIYSell productInfo =  dataSnapshot.getValue(DIYSell.class);
//                        final String diyName = diySellinfo.getDiyName();
//                        final String diyUrl = diySellinfo.getDiyUrl();
//                        final String user_id = diySellinfo.getUser_id();
//                        final String sellerName = diySellinfo.getLoggedInUser();
//                        final String productID = diySellinfo.getProductID();
//                        String status = diySellinfo.getIdentity();
//                        final String buyerid = userID;
//
//                        if (!userID.equals(diySellinfo.getUser_id())) {
//                            Log.e("pending_not_same", String.valueOf("" + userID != diySellinfo.getUser_id()));
//                            Log.e("userid", String.valueOf("" + userID));
//                            Log.e("info_userid", String.valueOf("" + diySellinfo.getUser_id()));
//
//                            final String finalMessage_price = message_price;
//                            pending_reference.orderByChild("productID").equalTo(diySellinfo.productID).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    if (dataSnapshot.exists()) {
//                                        Log.e("same_prodID", "" + dataSnapshot.exists());
//                                        Log.e("same_data", "" + dataSnapshot);
//
//                                        final Dialog dialog = new Dialog(ViewPromoActivity.this);
//                                        dialog.setContentView(R.layout.exist_dialog);
//                                        TextView text = (TextView) dialog.findViewById(R.id.e_text);
//                                        text.setText("DIY already added to pending list!");
//                                        ImageView image = (ImageView) dialog.findViewById(R.id.exist_dialog_imageview);
//                                        image.setImageResource(R.drawable.exist);
//
//                                        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOKI);
//                                        dialogButton.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                Intent intent = new Intent(ViewPromoActivity.this, MainActivity.class);
//                                                startActivity(intent);
//                                            }
//                                        });
//                                        dialog.show();
//                                    } else {
//
//                                        DIYSell info = new DIYSell(diyName, diyUrl, user_id, productID, "Pending", float_this,
//                                                float_this, buyerid, loggedInUserName);
//                                        final String upload_info = pending_reference.push().getKey();
//                                        pending_reference.child(upload_info).setValue(info);
//                                        pending_reference.child(upload_info).child("selling_price").setValue("");
//                                        pending_reference.child(upload_info).child("userStatus").setValue("buyer");
//
//
//                                        //DBref for buyer
//                                        final DatabaseReference pendingRefByOwner = FirebaseDatabase.getInstance().getReference("DIY Pending Items")
//                                                .child(user_id); //userID sa seller na
//                                        Log.e("pendingRefByOwner", String.valueOf(pendingRefByOwner));
//
//                                        //BuyerInfo - userID sa buyer
//                                        Log.e("userIDDD", userID);
//
//                                        DIYSell buyer = new DIYSell(diyName, diyUrl, user_id, productID, "For Confirmation", float_this,
//                                                float_this, buyerid, sellerName);
//                                        final String uploadBuyerInfo = pendingRefByOwner.child(upload_info).getKey();
//                                        pendingRefByOwner.child(uploadBuyerInfo).setValue(buyer);
//                                        pendingRefByOwner.child(uploadBuyerInfo).child("selling_price").setValue("");
//                                        pendingRefByOwner.child(uploadBuyerInfo).child("selling_qty").setValue("");
//                                        pendingRefByOwner.child(uploadBuyerInfo).child("userStatus").setValue("seller");
//
//
//                                        final Dialog dialog = new Dialog(ViewPromoActivity.this);
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
//                                                Intent intent = new Intent(ViewPromoActivity.this, MainActivity.class);
//                                                startActivity(intent);
//                                            }
//                                        });
//                                        dialog.show();
//                                    }
//                                }
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                }
//                            });
//                        }else if(userID.equals(diySellinfo.getUser_id())) {
//                            Log.e("pending_same", String.valueOf("" + userID.equals(diySellinfo.getUser_id())));
//                            Toast.makeText(ViewPromoActivity.this, "It's your own product!", Toast.LENGTH_SHORT).show();
//                        }
//
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
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
