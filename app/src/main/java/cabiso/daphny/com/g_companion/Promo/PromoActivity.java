package cabiso.daphny.com.g_companion.Promo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.R;

public class PromoActivity extends AppCompatActivity {
    private DatabaseReference promoReference;

    private TextView mTvProductName;
    private TextView mTvProductDesc;
    private TextView mTvFinish;
    private ListView mListviewPromo;
    private Button mBtnExpiry;

    private PromoAdapterForList promo_adapter;
    private ArrayList<DIYnames> promoList;

    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);

        promoList = new ArrayList<>();

        mTvProductName = (TextView) findViewById(R.id.tvProductName);
        mTvProductDesc = (TextView) findViewById(R.id.tvProductDesc);
        mListviewPromo = (ListView) findViewById(R.id.lvPromoItemList);
        mTvFinish = (TextView) findViewById(R.id.tv_finish);
        mBtnExpiry = (Button) findViewById(R.id.btn_set_expiry_date);

        Intent intent = getIntent();
        itemId = intent.getExtras().getString("diy_Name");
        mTvProductName.setText(itemId);

        promoReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        promoReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                SellingDIY sellingDIY = dataSnapshot.getValue(SellingDIY.class);

                if(itemId.equals(diYnames.getDiyName())){
                    promoList.add(diYnames);
                    promo_adapter = new PromoAdapterForList(PromoActivity.this, R.layout.promo_item, promoList);
                    mListviewPromo.setAdapter(promo_adapter);
                    mTvProductDesc.setText(sellingDIY.getSelling_descr());
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
