package cabiso.daphny.com.g_companion;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

import cabiso.daphny.com.g_companion.Adapter.BiddersAdapter;
import cabiso.daphny.com.g_companion.Model.DIYBidding;

public class ToBidProduct extends Activity implements View.OnClickListener {

    private EditText mEtPriceMin;
    private EditText mEtPriceMax;
    private EditText mEtPriceMessage;
    private FirebaseUser mFirebaseUser;
    private EditText mEtExpiryDate;
    private String userID;
    private String itemId;
    private Button mBtnAddBid;
    private DatabaseReference itemReference;
    private DatabaseReference identityReference;
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_bid_product);

        mEtPriceMin = (EditText) findViewById(R.id.etPriceMin);
        mEtPriceMax = (EditText) findViewById(R.id.etPriceMax);
        mEtPriceMessage = (EditText) findViewById(R.id.etMessage);
        mEtExpiryDate = (EditText) findViewById(R.id.et_xpiry_date);
        mBtnAddBid = (Button) findViewById(R.id.btnAddBid);

        Intent intent = getIntent();
        itemId = intent.getExtras().getString("itemId");

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        mEtExpiryDate.setOnClickListener(this);

        itemReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(this.itemId);
        identityReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(this.itemId);
        mBtnAddBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("addBid","CLICKED ADD BID");
                DIYBidding formBidding = getFormInput();
                itemReference.child("bidding").push().setValue(formBidding);
                Toast.makeText(ToBidProduct.this,"Successfully Added a new bid",Toast.LENGTH_SHORT);

                HashMap<String, Object> result = new HashMap<>();
                result.put("identity", "ON BID!");
                identityReference.updateChildren(result);
                Intent intent = new Intent(ToBidProduct.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private DIYBidding getFormInput(){
        return new DIYBidding()
                .setBidder(this.userID)
                .setMessage(this.mEtPriceMessage.getText()+"")
                .setPrice_min(Integer.parseInt(this.mEtPriceMin.getText()+""))
                .setPrice_max(Integer.parseInt(this.mEtPriceMax.getText()+""))
                .setDate(sdate)
                .setXpire_date(mEtExpiryDate.getText().toString());
    }

    @Override
    public void onClick(View v) {
        if (v == mEtExpiryDate) {
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
                        mEtExpiryDate.setText(set_expiry);
                    }
                }
            }, year, month, date);
            datePickerDialog.show();
//        }
        }
    }
}
