package cabiso.daphny.com.g_companion.GCAdmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.ReportsModel;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;
import cabiso.daphny.com.g_companion.notifications.PushNotification;

/**
 * Created by Lenovo on 9/13/2018.
 */

public class ViewComplaintsActivity extends AppCompatActivity {

    DatabaseReference reportsReference, userReportStatusRef;
    private ListView lvRreports;
    private ArrayList<ReportsModel> listComplains = new ArrayList<>();
    ViewReportsAdapter arrayAdapter;
    Button blockUnblock, warnUserBtn;
    private DatabaseReference user_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_complaints_activity);

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
        userReportStatusRef = FirebaseDatabase.getInstance().getReference().child("userdata");
        user_reference = FirebaseDatabase.getInstance().getReference().child("userdata");

        lvRreports = (ListView) findViewById(R.id.lvReportedSeller);
        blockUnblock = (Button) findViewById(R.id.blockUnblockBtn);
        warnUserBtn = (Button) findViewById(R.id.warnUserBtn);

        final String get_name = getIntent().getStringExtra("reportUserID");

        reportsReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("dataSnapshottt", dataSnapshot.getKey());

                if(get_name.equals(dataSnapshot.getKey())){
                    Log.e("byUserReport", get_name + " = " + dataSnapshot.getKey());

                    for(DataSnapshot reportSnap : dataSnapshot.child("reports").getChildren()){
                        Log.e("reportSnap", reportSnap.getKey());

                        ReportsModel viewReports = reportSnap.getValue(ReportsModel.class);
                        Log.e("viewReports", viewReports.complaint);

                        listComplains.add(viewReports);
                        arrayAdapter = new ViewReportsAdapter(ViewComplaintsActivity.this, R.layout.complains_item, listComplains);
                        lvRreports.setAdapter(arrayAdapter);

                    }

                    warnUserBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewComplaintsActivity.this);
                            builder.setTitle("Warn user: ");
                            builder.setMessage("Are you sure? ");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(ViewComplaintsActivity.this, "User Warn!", Toast.LENGTH_SHORT).show();

                                    //Notification
                                    user_reference.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
//                                            if(loggedInUser!=null){
                                                PushNotification pushNotification = new PushNotification(getApplicationContext());
                                                pushNotification.title("Warning!")
                                                        .message("You are warned by the admin for having 3 reports from your buyers!")
                                                        .accessToken(user_profile.getAccess_token())
                                                        .send();
//                                            }
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

                                    Intent intent = new Intent(ViewComplaintsActivity.this, MainActivity.class);
                                    startActivity(intent);

                                }
                            });

                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }
                    });

                    User_Profile infoo = dataSnapshot.child("user_info").getValue(User_Profile.class);
                    Log.e("infoo", infoo.getF_name());

                    if(infoo.getReport_status().equalsIgnoreCase("Block")){
                        blockUnblock.setText("Block");
                        blockUnblock.setBackgroundColor(Color.RED);

                        blockUnblock.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(ViewComplaintsActivity.this, "Long press to unblock user.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        blockUnblock.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(ViewComplaintsActivity.this);
                                builder.setTitle("Unblock user: ");
                                builder.setMessage("Are you sure? ");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ViewComplaintsActivity.this, "User Unblock!", Toast.LENGTH_SHORT).show();

                                        reportsReference.child(get_name).child("user_info").child("report_status").setValue("Unblock");
                                        userReportStatusRef.child(get_name).child("report_status").setValue("Unblock");

                                        Intent intent = new Intent(ViewComplaintsActivity.this, MainActivity.class);
                                        startActivity(intent);

                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.show();

                                return true;
                            }
                        });
                    }

                    if(blockUnblock.getText().equals("Block User ?")) {

                        blockUnblock.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(ViewComplaintsActivity.this);
                                builder.setTitle("Block user: ");
                                builder.setMessage("Are you sure? ");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(ViewComplaintsActivity.this, "User Block!", Toast.LENGTH_SHORT).show();

                                        reportsReference.child(get_name).child("user_info").child("report_status").setValue("Block");
                                        userReportStatusRef.child(get_name).child("report_status").setValue("Block");

                                        Intent intent = new Intent(ViewComplaintsActivity.this, MainActivity.class);
                                        startActivity(intent);

                                    }
                                });

                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.show();
                            }
                        });

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
