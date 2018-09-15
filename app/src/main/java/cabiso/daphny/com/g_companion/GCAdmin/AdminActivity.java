package cabiso.daphny.com.g_companion.GCAdmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/13/2018.
 */

public class AdminActivity extends AppCompatActivity {

    private ArrayList<String> reportList = new ArrayList<>();
    private ListView lvReports;
    ReportUserListAdapter arrayAdapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference reportsReference;
    private DatabaseReference userdataReference;
    private ArrayList<DBMaterial> dbMaterials;
    int count;
    float curRate;
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


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
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        reportsReference = FirebaseDatabase.getInstance().getReference().child("reportedSeller");
        userdataReference = FirebaseDatabase.getInstance().getReference().child("userdata");

        reportsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (final DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot", reportSnapshot.getKey());

                    reportList.add(reportSnapshot.getKey());
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
