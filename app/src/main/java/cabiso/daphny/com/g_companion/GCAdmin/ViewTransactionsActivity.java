package cabiso.daphny.com.g_companion.GCAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/18/2018.
 */

public class ViewTransactionsActivity extends AppCompatActivity {

    DatabaseReference diyLogsReference, userRef;
    private ListView lvTransac;
    private ArrayList<DIYSell> listTransacs = new ArrayList<>();
    ViewTransactionsAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_transactions_activity);

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

        diyLogsReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items");
        userRef = FirebaseDatabase.getInstance().getReference().child("userdata");

        lvTransac = (ListView) findViewById(R.id.lvViewLogs);

        final String get_name = getIntent().getStringExtra("transacUserID");
        Log.e("get_namee", get_name);

        diyLogsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("logSnapshottt", dataSnapshot.getKey());

                if(get_name.equals(dataSnapshot.getKey())){
                    for(DataSnapshot diyLogSnap : dataSnapshot.getChildren()){
                        Log.e("diyLogSnappp", String.valueOf(diyLogSnap));

                        DIYSell diyLogs = diyLogSnap.getValue(DIYSell.class);
                        Log.e("diyLogs", diyLogs.getDiyName());

                        listTransacs.add(diyLogs);
                        arrayAdapter = new ViewTransactionsAdapter(ViewTransactionsActivity.this, R.layout.logs_diy_item, listTransacs);
                        lvTransac.setAdapter(arrayAdapter);

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
