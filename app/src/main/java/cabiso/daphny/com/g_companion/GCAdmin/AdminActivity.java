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
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/13/2018.
 */

public class AdminActivity extends AppCompatActivity {

    private ArrayList<User_Profile> reportList = new ArrayList<>();
    private ListView lvReports;
    ReportUserListAdapter arrayAdapter;
    private ProgressDialog progressDialog;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference reportsReference;

    public AdminActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity);


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

        lvReports = (ListView) findViewById(R.id.lvReportedSeller);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        reportsReference = FirebaseDatabase.getInstance().getReference().child("reportedSeller");

        reportsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (final DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot", reportSnapshot.getKey());

                    User_Profile userInfo = reportSnapshot.child("user_info").getValue(User_Profile.class);
                    Log.e("childd", userInfo.getF_name());

                    reportList.add(userInfo);
                    arrayAdapter = new ReportUserListAdapter(AdminActivity.this, R.layout.reported_keys, reportList);
                    lvReports.setAdapter(arrayAdapter);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
