package cabiso.daphny.com.g_companion.Promo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cabiso.daphny.com.g_companion.DIYDetailViewActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.PushNotification;

public class PromoActivity extends AppCompatActivity implements View.OnClickListener, Serializable {
    private DatabaseReference promoReference, identityReference, identityReferenceByUser;
    private DatabaseReference promoAddReference;

    private TextView mTvProductName;
    private TextView mTvProductDesc;
    private TextView mTvFinish;
    private ListView mListviewPromo;
    private TextView mTvExpiry;
    private ImageButton mIbBack;
    private PromoModel promoItem;

    private PromoAdapterForList promo_adapter;
    private ArrayList<DIYnames> promoList;
    private DatabaseReference addPromoReference;

    private FirebaseUser mFirebaseUser;
    private String userID;

    private String itemName, itemID;
    private String productID;
    private String imgID;
    private String sellingPrice, sellingQuantity;
    private String sellerUserID, sellerName;

    private EditText mEtPromoQuantity;
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private DatabaseReference user_reference;
    private User_Profile loggedInUser = null; //for Notification

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        promoList = new ArrayList<>();
        promoItem = new PromoModel();

        addPromoReference = FirebaseDatabase.getInstance().getReference().child("promo_sale");

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        mTvProductName = (TextView) findViewById(R.id.tvProductName);
        mListviewPromo = (ListView) findViewById(R.id.lvPromoItemList);
        mTvFinish = (TextView) findViewById(R.id.tv_finish);
        mTvExpiry = (TextView) findViewById(R.id.tv_set_expiry_date);
        mIbBack = (ImageButton) findViewById(R.id.ibBlack);
        mTvProductDesc = (EditText) findViewById(R.id.etPromoDetails);
        mEtPromoQuantity = (EditText) findViewById(R.id.etPromoQuantity);
//        mBtntForCheck = (ImageView) findViewById(R.id.img_for_checked);

        Intent intent = getIntent();
        itemName = intent.getExtras().getString("diy_Name");
        productID = intent.getExtras().getString("diy_ID");
        sellerUserID = intent.getExtras().getString("sellerUserID");
        sellerName = intent.getExtras().getString("sellerName");
        sellingPrice = intent.getExtras().getString("sellingPrice");
        sellingQuantity = intent.getExtras().getString("sellingQuantity");
        itemID = intent.getExtras().getString("itemId");
        Log.e("buyTakeID", itemID);

        identityReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(this.itemID);
        identityReferenceByUser = FirebaseDatabase.getInstance().getReference().child("diy_by_users").child(userID).child(this.itemID);
        user_reference = FirebaseDatabase.getInstance().getReference().child("userdata");

        imgID = String.valueOf(intent.getExtras().get("diy_img"));

        mTvProductName.setText(itemName);

        mTvExpiry.setOnClickListener(this);
        mIbBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(PromoActivity.this, DIYDetailViewActivity.class);
                startActivity(back);
            }
        });

        mTvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(promoItem.getFreeItemList().size() > 0) {
                    if (promoItem != null) {
                        try {
                            promoItem.setPromo_id(productID);
                            promoItem.setPromo_diyName(itemName);
                            promoItem.setPromo_image(imgID);
                            promoItem.setPromo_details(mTvProductDesc.getText() + "");
                            promoItem.setPromo_expiry(mTvExpiry.getText() + "");
                            promoItem.setPromo_createdDate(sdate);
                            promoItem.setBuy_counts(mEtPromoQuantity.getText() + "");
                            promoItem.setStatus("wholesale");
                            promoItem.setPromo_createdDate(sdate);

                            //DIY price
                            float promoPrice = Float.parseFloat((sellingPrice));
                            int promoQty = Integer.parseInt((sellingQuantity));

                            String upload_info = addPromoReference.push().getKey();
                            addPromoReference.child(upload_info).setValue(promoItem);
                            addPromoReference.child(upload_info).child("sellerID").setValue(sellerUserID);
                            addPromoReference.child(upload_info).child("sellerName").setValue(sellerName);
                            addPromoReference.child(upload_info).child("promoPrice").setValue(promoPrice);
                            addPromoReference.child(upload_info).child("promoQuantity").setValue(promoQty);

                            HashMap<String, Object> result = new HashMap<>();
                            result.put("identity", "Promo");
                            identityReference.updateChildren(result);
                            identityReferenceByUser.updateChildren(result);

                            //Notification
                            user_reference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
//                                    if(loggedInUser!=null){
                                        PushNotification pushNotification = new PushNotification(getApplicationContext());
                                        pushNotification.title("On Sale Notification")
                                                .message(itemName + " is on sale!")
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


                            Intent back = new Intent(PromoActivity.this, MainActivity.class);
                            startActivity(back);
                            Toast.makeText(getApplicationContext(),"Succesfully created a new promo",Toast.LENGTH_SHORT).show();



                        } catch (DatabaseException de) {
                            de.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Please select atleast 1 freebie.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        promo_adapter = new PromoAdapterForList(PromoActivity.this, R.layout.promo_item, promoList,promoItem);
        mListviewPromo.setAdapter(promo_adapter);

        promoAddReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(itemName).getRef();
        promoReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        promoReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                SellingDIY sellingDIY = dataSnapshot.getValue(SellingDIY.class);

                if(userID.equals(diYnames.getUser_id())){
                    if(diYnames.getIdentity().equalsIgnoreCase("selling")){
//                        if(!itemName.equals(diYnames.getDiyName())){

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

                            //DIY price
                            float freePromoPrice = Float.parseFloat((message_price));
                            int freePromoQty = Integer.parseInt((message_qty));

                            //for free promo item
//                            promoList.add(diYnames);
                            promoList.add((DIYnames) diYnames.setSelling_price(freePromoPrice).setSelling_qty(freePromoQty));
//                            promoList.add((DIYnames) diYnames.setSelling_qty(freePromoQty));

                            promo_adapter.notifyDataSetChanged();
//                        }
                    }
                }

//                if(itemId.equals(diYnames.getDiyName())){
//                    promoList.add(diYnames);
//                    promo_adapter.notifyDataSetChanged();
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
    }



    @Override
    public void onClick(View v) {
        if(v==mTvExpiry){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String set_expiry = year + "-" + String.valueOf(month + 1) + "-" + (dayOfMonth);
//                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
//                    String set_expiry = String.valueOf(month + 1) + "/" + (dayOfMonth) + "/" + year;
                    if (calendar.before(set_expiry)) {
                        Toast.makeText(getApplication(), "YOU CANNOT PICK PASSED WEEKS!", Toast.LENGTH_SHORT).show();
                    } else {
                        mTvExpiry.setText(set_expiry);
                    }
                }
            }, year, month, date);
            datePickerDialog.show();
        }
    }
}
