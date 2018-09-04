package cabiso.daphny.com.g_companion.Promo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import cabiso.daphny.com.g_companion.DIYDetailViewActivity;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.R;

public class PromoActivity extends AppCompatActivity implements View.OnClickListener, Serializable {
    private DatabaseReference promoReference;
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

    private String itemId;
    private String productID;
    private String imgID;
    private EditText mEtPromoQuantity;
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        promoList = new ArrayList<>();
        promoItem = new PromoModel();
        addPromoReference = FirebaseDatabase.getInstance().getReference();

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
        itemId = intent.getExtras().getString("diy_Name");
        productID = intent.getExtras().getString("diy_ID");
        imgID = String.valueOf(intent.getExtras().get("diy_img"));
        mTvProductName.setText(itemId);

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
                            promoItem.setPromo_diyName(itemId);
                            promoItem.setPromo_image(imgID);
                            promoItem.setPromo_details(mTvProductDesc.getText() + "");
                            promoItem.setPromo_expiry(mTvExpiry.getText() + "");
                            promoItem.setPromo_createdDate(sdate);
                            promoItem.setBuy_counts(mEtPromoQuantity.getText() + "");
                            promoItem.setStatus("wholesale");
//                        promoList.add(promoItem);

                            addPromoReference.child("promo_sale").push().setValue(promoItem);
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

        promoAddReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(itemId).getRef();
        promoReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        promoReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                SellingDIY sellingDIY = dataSnapshot.getValue(SellingDIY.class);

                if(userID.equals(diYnames.getUser_id())){
                    if(diYnames.getIdentity().equalsIgnoreCase("selling")){
                        if(!itemId.equals(diYnames.getDiyName())){
                            promoList.add(diYnames);
                            promo_adapter.notifyDataSetChanged();
                        }
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
