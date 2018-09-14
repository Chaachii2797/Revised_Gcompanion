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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cabiso.daphny.com.g_companion.Model.DIYBidding;

public class ToBidProduct extends Activity implements View.OnClickListener {

    private EditText mEtPriceMin;
    private EditText mEtPriceMessage;
    private FirebaseUser mFirebaseUser;
    private EditText mEtExpiryDate;
    private TextView mTvDateToday;
    private String userID;
    private String itemId;
    private Button mBtnAddBid;
    private DatabaseReference itemReference;
    private DatabaseReference identityReference, itemReferenceByUser;
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_bid_product);

        mEtPriceMin = (EditText) findViewById(R.id.etInitialPrice);
        mEtPriceMessage = (EditText) findViewById(R.id.etMessage);
        mEtExpiryDate = (EditText) findViewById(R.id.et_xpiry_date);
        mTvDateToday = (TextView) findViewById(R.id.tv_on_date);
        mBtnAddBid = (Button) findViewById(R.id.btnAddBid);

        mTvDateToday.setText(sdate);

        Intent intent = getIntent();
        itemId = intent.getExtras().getString("itemId");
        Log.e("itemId", itemId);


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        mEtExpiryDate.setOnClickListener(this);

        itemReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(this.itemId);
        identityReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(this.itemId);
        itemReferenceByUser = FirebaseDatabase.getInstance().getReference().child("diy_by_users").child(userID).child(this.itemId);
        Log.e("itemReferenecByUser", String.valueOf(itemReferenceByUser));

        mBtnAddBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DIYBidding formBidding = getFormInput();
                itemReference.child("bidding").push().setValue(formBidding);
                itemReferenceByUser.child("bidding").push().setValue(formBidding);

                HashMap<String, Object> result = new HashMap<>();
                result.put("identity", "ON BID!");
                identityReference.updateChildren(result);
                itemReferenceByUser.updateChildren(result);
                Intent intent = new Intent(ToBidProduct.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private DIYBidding getFormInput(){
        return new DIYBidding()
                .setBidder(this.userID)
                .setMessage(this.mEtPriceMessage.getText()+"")
                .setInitialPrice(Integer.parseInt(this.mEtPriceMin.getText()+""))
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
