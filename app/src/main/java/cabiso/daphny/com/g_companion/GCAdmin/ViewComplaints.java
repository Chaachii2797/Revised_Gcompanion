package cabiso.daphny.com.g_companion.GCAdmin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lenovo on 9/13/2018.
 */

public class ViewComplaints extends AppCompatActivity {

    DatabaseReference reportsReference;
    TextView tv_userID, tv_sellerName;
    CircleImageView seller_picture;
    ToggleButton block_unblok;
    private ListView lvRreports;
    private ArrayList<String> listComplains = new ArrayList<>();
    ViewReportsAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_complaints);

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

        reportsReference = FirebaseDatabase.getInstance().getReference().child("reportedSeller");

        tv_userID = (TextView) findViewById(R.id.tv_userID);
        tv_sellerName = (TextView) findViewById(R.id.tv_sellerName);
        seller_picture = (CircleImageView) findViewById(R.id.seller_picture);
        lvRreports = (ListView) findViewById(R.id.lvReportedSeller);
        block_unblok = (ToggleButton) findViewById(R.id.toggleBtnBlock);

        final String get_name = getIntent().getStringExtra("reportUserID");
        tv_userID.setText(get_name);


        reportsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("dataSnapshottt", dataSnapshot.getKey());

                if(get_name.equals(dataSnapshot.getKey())){
                    Log.e("exxx", get_name + " = " + dataSnapshot.getKey());

                    for (final DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                        Log.e("reportSnapshot", reportSnapshot.getKey());

                        Object sellerFName = reportSnapshot.child("f_name").getValue();
                        Object sellerLName = reportSnapshot.child("l_name").getValue();
                        Object sellerPic = reportSnapshot.child("userProfileUrl").getValue();
                        Object dbComplains = reportSnapshot.child("complaint").getValue();
                        Log.e("complainss", String.valueOf(dbComplains));
                        String complainss = String.valueOf(dbComplains);
                        tv_sellerName.setText(sellerFName + " " + sellerLName);

                        Glide.with(ViewComplaints.this).load(sellerPic).diskCacheStrategy(DiskCacheStrategy.ALL)
                                .fitCenter().crossFade()
                                .into(seller_picture);

                        listComplains.add(complainss);
                        arrayAdapter = new ViewReportsAdapter(ViewComplaints.this, R.layout.complains_item, listComplains);
                        lvRreports.setAdapter(arrayAdapter);

                        block_unblok.setTextOn("Block"); // displayed text of the Switch whenever it is in checked or on state
                        block_unblok.setTextOff("Unblock");


                        block_unblok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ViewComplaints.this).edit();
                                editor.putBoolean("service_status", block_unblok.isChecked());
                                editor.commit();

                                Toast.makeText(ViewComplaints.this, block_unblok.getText() + " " + tv_sellerName.getText(), Toast.LENGTH_SHORT).show();

                            }
                        });

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ViewComplaints.this);
                        boolean switchState = prefs.getBoolean("service_status", false);

                        if(switchState){
                            //Do your work for service is selected on
                            block_unblok.setChecked(true);
                        } else {
                            //Code for service off
                            block_unblok.setChecked(false);
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
