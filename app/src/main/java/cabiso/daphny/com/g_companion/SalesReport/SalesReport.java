package cabiso.daphny.com.g_companion.SalesReport;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.cunoraz.tagview.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

public class SalesReport extends Activity {
    private ArrayList<DIYnames> sales_diylist = new ArrayList<>();
    private ArrayList<Integer> incomeCount = new ArrayList<>();
    private DatabaseReference salesRef;

    SalesReportAdapters salesReportAdapters;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String userID;
    private ListView lv_sales_report;

    private int totalIncomeCount = 0;
    private int totalExpenseCount = 0;
    private int totalIncomeOverall = 0;
    private int totalExpenseOverall = 0;
    private int totalPrice = 0;
    private TextView incomeTotal, expenseTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();

        salesRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");
        lv_sales_report = (ListView) findViewById(R.id.lv_sales_report);
        incomeTotal = (TextView) findViewById(R.id.tv_Income);
        expenseTotal = (TextView) findViewById(R.id.tv_Expense);

        salesReportAdapters = new SalesReportAdapters(SalesReport.this, R.layout.item_sales_report, sales_diylist);
        lv_sales_report.setAdapter(salesReportAdapters);

        salesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DIYnames diy = dataSnapshot.getValue(DIYnames.class);
                if (userID.equals(diy.getUser_id())) {
                    if (diy.getIncomeCount() != 0) {

                        totalIncomeCount = dataSnapshot.child("incomeCount").getValue(Integer.class);
                        totalExpenseCount = dataSnapshot.child("expenseCount").getValue(Integer.class);

                        String name = dataSnapshot.child("diyName").getValue(String.class);
                        for (DataSnapshot sellingSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                            totalPrice = sellingSnapshot.child("selling_price").getValue(int.class);
                        }

                        totalIncomeOverall += totalIncomeCount * totalPrice;
                        totalExpenseOverall += totalExpenseCount * totalPrice;

                        Log.e("incomesheeee", name + " -- " + totalIncomeCount + "x" + totalPrice + " = " + (totalIncomeCount * totalPrice) + "");
                        Log.e("incomesheeee", totalIncomeOverall+"");
                        incomeTotal.setText("Total Income: " + totalIncomeOverall+ "");
                        expenseTotal.setText("Total Expense: " + totalExpenseOverall+ "");
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

        salesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
//                Log.e("incomeCount",dataSnapshot.child("incomeCount").getValue(String.class));
                if (userID.equals(diYnames.getUser_id())) {
                    if (!diYnames.getIdentity().equalsIgnoreCase("community")) {
                        if (!diYnames.getIdentity().equalsIgnoreCase("on bid!")) {
                            sales_diylist.add(diYnames);
                            salesReportAdapters.notifyDataSetChanged();
                        }
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
