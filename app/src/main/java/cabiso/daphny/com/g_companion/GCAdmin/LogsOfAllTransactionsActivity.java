package cabiso.daphny.com.g_companion.GCAdmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/17/2018.
 */

public class LogsOfAllTransactionsActivity extends AppCompatActivity {

    private ArrayList<String> logList = new ArrayList<>();
    private ListView lvLogs;
    TransactionListAdapter arrayAdapter;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference logsReference;

    public LogsOfAllTransactionsActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_transaction_activity);

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


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lvLogs = (ListView) findViewById(R.id.lvLogs);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        logsReference = FirebaseDatabase.getInstance().getReference().child("Sold_Items");

        logsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (final DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot", reportSnapshot.getKey());

                    logList.add(reportSnapshot.getKey());
                    arrayAdapter = new TransactionListAdapter(LogsOfAllTransactionsActivity.this, R.layout.transaction_user_keys, logList);
                    lvLogs.setAdapter(arrayAdapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



}


