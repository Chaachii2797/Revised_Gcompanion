package cabiso.daphny.com.g_companion.Promo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cabiso.daphny.com.g_companion.DIYDetailViewActivity;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.R;

public class PromoActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference promoReference;
    private DatabaseReference promoAddReference;

    private TextView mTvProductName;
    private TextView mTvProductDesc;
    private TextView mTvFinish;
    private ListView mListviewPromo;
    private TextView mTvExpiry;
    private ImageButton mIbBack;
    private ArrayList<DIYnames> mListSelectedItems;

    private PromoAdapterForList promo_adapter;
    private ArrayList<DIYnames> promoList;

    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        promoList = new ArrayList<>();
        mListSelectedItems = new ArrayList<>();

        mTvProductName = (TextView) findViewById(R.id.tvProductName);
        mListviewPromo = (ListView) findViewById(R.id.lvPromoItemList);
        mTvFinish = (TextView) findViewById(R.id.tv_finish);
        mTvExpiry = (TextView) findViewById(R.id.tv_set_expiry_date);
        mIbBack = (ImageButton) findViewById(R.id.ibBlack);
//        mBtntForCheck = (ImageView) findViewById(R.id.img_for_checked);

        Intent intent = getIntent();
        itemId = intent.getExtras().getString("diy_Name");
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
                Toast.makeText(PromoActivity.this,"CLICKED!",Toast.LENGTH_SHORT);
                Log.e("LETSE",mListSelectedItems.size()+"");
            }
        });

        promo_adapter = new PromoAdapterForList(PromoActivity.this, R.layout.promo_item, promoList,mListSelectedItems);
        mListviewPromo.setAdapter(promo_adapter);

        promoAddReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(itemId).getRef();
        promoReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        promoReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                SellingDIY sellingDIY = dataSnapshot.getValue(SellingDIY.class);

                if(itemId.equals(diYnames.getDiyName())){
                    promoList.add(diYnames);
                    promo_adapter.notifyDataSetChanged();
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
