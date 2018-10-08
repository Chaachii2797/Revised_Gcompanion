package cabiso.daphny.com.g_companion.BuyingProcess;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cabiso.daphny.com.g_companion.Adapter.Items_Adapter;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.PushNotification;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 10/7/2018.
 */

public class PurchasedActivity extends AppCompatActivity implements RatingDialogListener {


    private ArrayList<DIYSell> soldList = new ArrayList<>();
    private ListView lv;
    private Items_Adapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    //  RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference purchaedItemReference;
    private DatabaseReference userdataReference;
    private ArrayList<DBMaterial> dbMaterials;
    int count;
    float curRate;
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private DatabaseReference user_reference;
    private User_Profile loggedInUser = null; //for notification


    public PurchasedActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased_items);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        lv = (ListView) findViewById(R.id.lvView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait loading DIYs.....");
        progressDialog.show();

        purchaedItemReference = FirebaseDatabase.getInstance().getReference().child("PurchasedItems").child(userID);
        userdataReference = FirebaseDatabase.getInstance().getReference().child("userdata");
        user_reference = FirebaseDatabase.getInstance().getReference().child("userdata");

        purchaedItemReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("snapshot", snapshot.getKey());

                    final DIYSell img = snapshot.getValue(DIYSell.class);
                    int rate=0;
                    soldList.add(img);

                }

                //init adapter
                adapter = new Items_Adapter(PurchasedActivity.this, R.layout.pending_item, soldList);
                //set adapter for listview
                lv.setAdapter(adapter);
                final int count =lv.getAdapter().getCount();
                registerForContextMenu(lv);


                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final DIYSell itemRef = adapter.getItem(position);

                        if(itemRef.getUser_id().equals(userID)) {
                            Toast.makeText(PurchasedActivity.this, "You can't rate your own item.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //For rating seller (If na sold nani)
                            AlertDialog.Builder alert = new AlertDialog.Builder(
                                    PurchasedActivity.this);
                            alert.setTitle("HEY!");
                            alert.setMessage("Are you sure you already received the order?");
                            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //do your work here
                                    new AppRatingDialog.Builder()
                                            .setPositiveButtonText("Submit")
                                            .setNegativeButtonText("Cancel")
                                            .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                                            .setDefaultRating(2)
                                            .setTitle("Rate the seller!")
                                            .setDescription("Please select some stars and give your feedback to the seller.")
                                            .setTitleTextColor(R.color.gen_scheme4)
                                            .setDescriptionTextColor(R.color.contentTextColor)
                                            .setHint("Please write your comment here ...")
                                            .setHintTextColor(R.color.hintTextColor)
                                            .setCommentTextColor(R.color.commentTextColor)
                                            .setCommentBackgroundColor(R.color.gen_scheme1)
                                            .setWindowAnimation(R.style.MyDialogFadeAnimation)
                                            .create(PurchasedActivity.this)
                                            .show();

                                    dialog.dismiss();
                                }
                            });
                            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alert.show();

                        }


                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void onPositiveButtonClicked(int rate, @NotNull final String comment){
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        curRate = Float.valueOf(decimalFormat.format((curRate * count + rate)
                / ++count));
        Toast.makeText(PurchasedActivity.this,
                "New Rating: " + curRate, Toast.LENGTH_SHORT).show();

        Log.e("curRatee", String.valueOf(curRate));
        Log.e("countt", String.valueOf(count));

        purchaedItemReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final DIYSell diYs = dataSnapshot.getValue(DIYSell.class);

//                final Review review = new Review().setRatings(rate).setComment(comment).setReviewer(userID).setDate_submitted(sdate);

                if(diYs.getUser_id().equals(userID)){
                    Toast.makeText(PurchasedActivity.this, "USERIDIMG: "+diYs.user_id, Toast.LENGTH_SHORT).show();

                }else{

                    HashMap<String, Object> result = new HashMap<>();
                    result.put("userRating", curRate);

                    userdataReference.child(diYs.getUser_id()).updateChildren(result);
                    Log.e("ratee", String.valueOf(curRate));


                    //Notification
                    user_reference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User_Profile user_profile = dataSnapshot.getValue(User_Profile.class);
//                            if(loggedInUser!=null){
                            PushNotification pushNotification = new PushNotification(getApplicationContext());
                            pushNotification.title("Rate")
                                    .message(diYs.getLoggedInUser() + " rated you!")
                                    .accessToken(user_profile.getAccess_token())
                                    .send();
//                            }
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
    public void onNegativeButtonClicked() {

    }

}
