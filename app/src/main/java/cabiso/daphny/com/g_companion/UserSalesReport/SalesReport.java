package cabiso.daphny.com.g_companion.UserSalesReport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 7/31/2017.
 */


public class SalesReport extends AppCompatActivity {

    SalesReportAdapterforList salesReportAdapterforList;
    private ArrayList<DIYnames> sales_diylist = new ArrayList<>();
    private DatabaseReference myRef;
    private String userID;
    private TextView price_report;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ListView lv_sales_report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();

        myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        lv_sales_report = (ListView) findViewById(R.id.lv_sales_report);
        price_report = (TextView) findViewById(R.id.tv_report_price);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        salesReportAdapterforList = new SalesReportAdapterforList(SalesReport.this, R.layout.sales_report_item, sales_diylist);
        lv_sales_report.setAdapter(salesReportAdapterforList);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                if(userID.equals(diYnames.getUser_id())) {
                    Log.e("USERIDDDD", userID+" "+diYnames.getUser_id());
                    Log.e("DIYNAMESS", diYnames.getDiyName());

                    if(!diYnames.getIdentity().equalsIgnoreCase("community")){
                        sales_diylist.add(diYnames);
                        salesReportAdapterforList.notifyDataSetChanged();
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
    }
}
