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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cabiso.daphny.com.g_companion.DIYDetailViewActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.PushNotification;

public class PriceDiscountActivity extends AppCompatActivity implements View.OnClickListener{

    private String diyNameID, itemID;
    private String productID;
    private String imgID;
    private String dbKey;
    private String priceID, quantityID;
    private String sellerID, sellerName;

    private double origPrice;

    private TextView mTvProductName;
    private TextView mExpiryDate;
    private TextView mFinish;
    private EditText mDetails;
    private EditText mPriceDiscount;
    private ImageButton mBtnBack;

    private PriceDiscountModel priceDiscountModel = new PriceDiscountModel();
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    private FirebaseUser mFirebaseUser;
    private String userID;
    private DatabaseReference dbReferencePrice, identityReference, identityReferenceByUser;
    private DatabaseReference dbPromoSale;
    private DatabaseReference user_reference;
    private User_Profile loggedInUser = null; //for notification

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_discount);

        Intent intent = getIntent();
        diyNameID = intent.getExtras().getString("diy_Name");
        productID = intent.getExtras().getString("diy_ID");
        dbKey = intent.getExtras().getString("getDBKey");
        priceID = intent.getExtras().getString("sellingPrice");
        quantityID = intent.getExtras().getString("sellingQuantity");
        sellerID = intent.getExtras().getString("sellerUserID");
        sellerName = intent.getExtras().getString("sellerName");
        itemID = intent.getExtras().getString("itemId");
        Log.e("buyTakeID", itemID);

        imgID = String.valueOf(intent.getExtras().get("diy_img"));
        Log.e("PRICEUUUU", priceID+ "");

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        dbReferencePrice = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(dbKey);
        dbPromoSale = FirebaseDatabase.getInstance().getReference().child("promo_sale");
        identityReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(this.itemID);
        identityReferenceByUser = FirebaseDatabase.getInstance().getReference().child("diy_by_users").child(userID).child(this.itemID);
        user_reference = FirebaseDatabase.getInstance().getReference().child("userdata");

        mTvProductName = (TextView) findViewById(R.id.tvProductName);
        mExpiryDate = (TextView) findViewById(R.id.tv_set_expiry_date);
        mFinish = (TextView) findViewById(R.id.tvFinishClickable);
        mDetails = (EditText) findViewById(R.id.etPromoDetails);
        mPriceDiscount = (EditText) findViewById(R.id.etDiscountPercent);
        mBtnBack = (ImageButton) findViewById(R.id.ibBlack);

        Log.e("diy_ID", productID);

        mTvProductName.setText(diyNameID);

        mExpiryDate.setOnClickListener(this);
        mFinish.setOnClickListener(this);

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(PriceDiscountActivity.this, DIYDetailViewActivity.class);
                startActivity(back);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v==mExpiryDate){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int date = calendar.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    String set_expiry = year + "-" + String.valueOf(month + 1) + "-" + (dayOfMonth);
                    if (calendar.before(set_expiry)) {
                        Toast.makeText(getApplication(), "YOU CANNOT PICK PASSED WEEKS!", Toast.LENGTH_SHORT).show();
                    } else {
                        mExpiryDate.setText(set_expiry);
                    }
                }
            }, year, month, date);
            datePickerDialog.show();
        }

        if(v==mFinish){
            Log.e("diy_IDDDDDDDDD", productID);
            try {
                origPrice = Double.parseDouble(priceID);
                double discount = Integer.parseInt(mPriceDiscount.getText().toString());
                Log.e("discountOrigP", discount+ " "+origPrice);
                double percentDiscount = (discount/100);
                Log.e("percentDiscount", percentDiscount+"");
                double  percentedPrice = (origPrice*percentDiscount);
                double finalPrice = (origPrice-percentedPrice);
                Log.e("PRICEUUUUDISCOUNT", (origPrice*percentDiscount)+" "+ finalPrice+ "");
                priceDiscountModel.setPromo_id(productID +"");
                priceDiscountModel.setPromo_diyName(diyNameID);
                priceDiscountModel.setPromo_expiry(mExpiryDate.getText() + "");
                priceDiscountModel.setPromo_details(mDetails.getText() + "");
                priceDiscountModel.setPercent_discount(mPriceDiscount.getText() + ""+"%");
                priceDiscountModel.setPromo_newPrice(String.valueOf(finalPrice));
                priceDiscountModel.setPromo_image(imgID);
                priceDiscountModel.setPromo_createdDate(sdate);
                priceDiscountModel.setStatus("discount");
                Intent back = new Intent(PriceDiscountActivity.this, MainActivity.class);
                Log.e("PRICEEEEE", String.valueOf(origPrice));
                startActivity(back);

                int discountPromoQty = Integer.parseInt((quantityID));

                String upload_info = dbPromoSale.push().getKey();
                dbPromoSale.child(upload_info).setValue(priceDiscountModel);
                dbPromoSale.child(upload_info).child("sellerID").setValue(sellerID);
                dbPromoSale.child(upload_info).child("sellerName").setValue(sellerName);
                dbPromoSale.child(upload_info).child("sellDIYqty").setValue(discountPromoQty);

                HashMap<String, Object> result = new HashMap<>();
                result.put("identity", "Promo");
                identityReference.updateChildren(result);
                identityReferenceByUser.updateChildren(result);

                //Notification
                user_reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
//                        if(loggedInUser!=null){
                            PushNotification pushNotification = new PushNotification(getApplicationContext());
                            pushNotification.title("On Sale Notification")
                                    .message(diyNameID + " is now " + mPriceDiscount.getText() + ""+"%" + " discount!")
                                    .accessToken(user_profile.getAccess_token())
                                    .send();
//                        }
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

                Intent intent = new Intent(PriceDiscountActivity.this,MainActivity.class);
                startActivity(intent);




            }catch (NumberFormatException nFe){
                nFe.getMessage();
            }

        }
    }
}
