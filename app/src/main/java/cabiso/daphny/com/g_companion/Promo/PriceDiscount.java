package cabiso.daphny.com.g_companion.Promo;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cabiso.daphny.com.g_companion.DIYDetailViewActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.R;

public class PriceDiscount extends AppCompatActivity implements View.OnClickListener{

    private String diyNameID;
    private String productID;
    private String imgID;
    private String dbKey;

    private int percentedPrice;
    private double origPrice;

    private TextView mTvProductName;
    private TextView mExpiryDate;
    private TextView mFinish;
    private EditText mDetails;
    private EditText mPriceDiscount;
    private ImageButton mBtnBack;

    private PriceDiscountModel priceDiscountModel;
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    private FirebaseUser mFirebaseUser;
    private String userID;
    private DatabaseReference dbReferencePrice;
    private DatabaseReference dbPromoSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_discount);

        Intent intent = getIntent();
        diyNameID = intent.getExtras().getString("diy_Name");
        productID = intent.getExtras().getString("diy_ID");
        dbKey = intent.getExtras().getString("getDBKey");
        imgID = String.valueOf(intent.getExtras().get("diy_img"));

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        dbReferencePrice = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(dbKey);
        dbPromoSale = FirebaseDatabase.getInstance().getReference().child("promo_sale");

        mTvProductName = (TextView) findViewById(R.id.tvProductName);
        mExpiryDate = (TextView) findViewById(R.id.tv_set_expiry_date);
        mFinish = (TextView) findViewById(R.id.tvFinishClickable);
        mDetails = (EditText) findViewById(R.id.etPromoDetails);
        mPriceDiscount = (EditText) findViewById(R.id.etDiscountPercent);
        mBtnBack = (ImageButton) findViewById(R.id.ibBlack);

        mTvProductName.setText(diyNameID);

        mExpiryDate.setOnClickListener(this);

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(PriceDiscount.this, DIYDetailViewActivity.class);
                startActivity(back);
            }
        });

        dbReferencePrice.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                SellingDIY sellingDiy = dataSnapshot.getValue(SellingDIY.class);
//                int origPrice = Integer.parseInt(String.valueOf(sellingDiy.getSelling_price()));
//                int discount = Integer.parseInt(mPriceDiscount.getText().toString());
//                int percentDiscount = (discount/100);
//                percentedPrice = origPrice*percentDiscount;

                for (DataSnapshot postSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                    origPrice = postSnapshot.child("selling_price").getValue(double.class);
                    int discount = Integer.parseInt(mPriceDiscount.getText().toString());
                    int percentDiscount = (discount/100);
                    percentedPrice = (int) (origPrice*percentDiscount);
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

        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(priceDiscountModel!=null){
                   try {
                       priceDiscountModel.setPromo_id(productID);
                       priceDiscountModel.setPromo_diyName(diyNameID);
                       priceDiscountModel.setPromo_expiry(mExpiryDate.getText() + "");
                       priceDiscountModel.setPromo_details(mDetails.getText() + "");
                       priceDiscountModel.setPercent_discount(mPriceDiscount.getText() + "");
                       priceDiscountModel.setPromo_newPrice(String.valueOf(percentedPrice));
                       priceDiscountModel.setPromo_image(imgID);
                       priceDiscountModel.setPromo_createdDate(sdate);

                       dbPromoSale.push().setValue(priceDiscountModel);
                       Intent back = new Intent(PriceDiscount.this, MainActivity.class);
                       Log.e("PRICEEEEE", String.valueOf(origPrice));
                       startActivity(back);
                   }catch (DatabaseException de){
                       de.getMessage();
                   }
                }
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
    }
}
